 <%--
    Search results page.

    @author Chris Lewington and Sugath Mudali
    @version $Id$
--%>

<%@ page language="java"%>

<%@ page import="uk.ac.ebi.intact.application.search.struts.framework.util.SearchConstants,
                 uk.ac.ebi.intact.application.search.business.IntactServiceIF,
                 uk.ac.ebi.intact.application.search.struts.view.BasicObjectViewBean,
                 uk.ac.ebi.intact.application.search.struts.view.AbstractViewBean,
                 uk.ac.ebi.intact.model.Protein"%>

<%@ page import="javax.xml.transform.stream.StreamResult"%>

<!-- Import util classes -->
<%@ page import="java.util.*"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>

<%
    // To allow access hierarchView properties.
    IntactServiceIF service = (IntactServiceIF) application.getAttribute(
            SearchConstants.INTACT_SERVICE);
%>

<script language="JavaScript" type="text/javascript">

    // Returns true if a checkbox has been checked.
    function checkAC(form, msg) {
        for (var i = 0; i < form.elements.length; i++) {
            // Only interested in 'checkbox' fields.
            if (form.elements[i].type == "checkbox") {
                // Only porcess if they are checked.
                if (form.elements[i].checked) {
                    return true;
                }
            }
        }
        window.alert(msg);
        return false;
    }

    // This is a global variable to setup a window.
    var newWindow;

    // Create a new window if it hasnt' created before and bring it to the
    // front if it is focusable.
    function makeNewWindow(link) {
        if (!newWindow || newWindow.closed) {
            newWindow = window.open(link, "hvWindow");
        }
        else if (newWindow.focus) {
            newWindow.location.href = link;
            newWindow.focus();
        }
    }

    // Will be invoked when user selects graph button. An AC must be selected.
    // This in trun will create a new widow and invoke hierarchView application
    // in the new window.
    function writeToWindow(form, msg) {
        // An AC must have been selected.
        if (!checkAC(form, msg)) {
            return;
        }
        var ac;
        for (var i = 0; i < form.elements.length; i++) {
            // Only interested in 'checkbox' fields.
            if (form.elements[i].type == "checkbox") {
                // Only process if they are checked.
                //TODO: This should handle multiple checkboxes....
                if (form.elements[i].checked) {
                    var name = form.elements[i].name;
                    // Remove the table identifer from name to get ac.
                    if(ac == null) {
                        ac = name.split("_")[2];
                    }
                    else {
                        ac = ac + "," + (name.split("_")[2]);
                    }
                }
            }
        }
        var link = "<%=service.getHierarchViewProp("hv.url")%>"
            + "?AC=" + ac + "&depth=" + <%=service.getHierarchViewProp("hv.depth")%>
            + "&method=" + "<%=service.getHierarchViewProp("hv.method")%>";
        //window.alert(link);
        makeNewWindow(link);
    }

    // Checks all the check boxes.
    function checkAll() {
        var form = document.viewForm;
        for (var i = 0; i < form.elements.length; i++) {
            // Only interested in 'checkbox' fields.
            if (form.elements[i].type == "checkbox") {
                // Only check it if it isn't already checked.
                if (!form.elements[i].checked) {
                    form.elements[i].checked = true;
                }
            }
        }
    }

    // Clears all the check boxes.
    function clearAll(form) {
        var form = document.viewForm;
        for (var i = 0; i < form.elements.length; i++) {
            // Only interested in 'checkbox' fields.
            if (form.elements[i].type == "checkbox") {
                // Only clear it if it is checked.
                if (form.elements[i].checked) {
                    form.elements[i].checked = false;
                }
            }
        }
    }
</script>

<h1>Search Results for
    <%=session.getAttribute(SearchConstants.SEARCH_CRITERIA) %></h1>
    <h4>(short labels of search criteria matches highlighted in <b><i>bold italic</i></b>)</h4>
<!-- a line to separate the header -->
<hr size=2>

<html:form action="/view">
    <html:link href='javascript:checkAll()'>
        Check All
    </html:link>
    <html:link href='javascript:clearAll()'>
        Clear All
    </html:link>
    <%
        //build the help link - uses the current host path information
        //plus the filename as specified in the servlet context
        String relativeHelpLink = this.getServletConfig().getServletContext().getInitParameter("helpLink");
        //build the help link out of the context path - strip off the 'search' bit...
        String ctxtPath = (request.getContextPath());
        String relativePath = ctxtPath.substring(0, ctxtPath.lastIndexOf("search"));
        String helpLink = relativePath.concat(relativeHelpLink) + "TOP_DOC";
    %>
    <!-- 'http://web7-node1.ebi.ac.uk:8160/intact/displayDoc.jsp?section=ALL' -->
   <html:link href="<%=helpLink %>" target="new">
        Help
    </html:link>
    <hr size=2>

<!-- Get the view Bean and dump its data to the web page-->
<%
    //write to the JSP output stream
    StreamResult result = new StreamResult(out);

    if(session.getAttribute(SearchConstants.SINGLE_OBJ_VIEW_BEAN) != null) {
        //only have a single object to show..
        BasicObjectViewBean bean = (BasicObjectViewBean)session.getAttribute(SearchConstants.SINGLE_OBJ_VIEW_BEAN);
        if(bean.getWrappedObject() instanceof Protein) {
            //this means we are displaying a Protein with no Interactions - print
            //a message first...
            %>
            <h2>This Protein is currently not attached to any Interactions:</h2>
            <%
        }
        bean.transform("0", result);
    }
    else {

        //Need to display either:
        // 1) A Protein Partner view;
        // 2) An Experiment view (different map);
        // 3) A 'partner view Experiment link' (a filter on the Experiment beans)
        Map partnerBeanMap = (Map)session.getAttribute(SearchConstants.PARTNER_BEAN_MAP);
        Map idToView = (Map) session.getAttribute(SearchConstants.FORWARD_MATCHES);
        Set beanFilter = (Set)session.getAttribute(SearchConstants.BEAN_FILTER);

        //simplest first..
        if((beanFilter != null) & (!beanFilter.isEmpty()) & (idToView != null)){
            //do 2) above....
            for(Iterator it = beanFilter.iterator(); it.hasNext();) {
                String id = (String)it.next();
                AbstractViewBean bean = (AbstractViewBean)idToView.get(id);
                bean.transform(id, result);
            }
        }
        else {
            //check the more complex views...
            Map displayItems = null;
            if((partnerBeanMap != null) & (!partnerBeanMap.isEmpty())&
                    (beanFilter != null) & (beanFilter.isEmpty())) {
                //do 1) above...
                displayItems = partnerBeanMap;
            }
            else {
                //do 3) above...
                displayItems = idToView;
            }

            // Process every bean in the Map.
            for (Iterator it = displayItems.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                AbstractViewBean bean = (AbstractViewBean) entry.getValue();
                String id = (String) entry.getKey();
                bean.transform(id, result);
            }
            %>
<hr size=2>
        <%
        }
    }
%>
    <!-- The footer table. -->
    <table cellpadding="1" cellspacing="0" border="1" width="100%">
        <tr>

        <%
            if(session.getAttribute(SearchConstants.SINGLE_OBJ_VIEW_BEAN) == null) {
                //display buttons - none to display for single object views..
        %>
            <td align="center">
                <%
                    if (request.getAttribute(SearchConstants.PROTEIN_VIEW_BUTTON) != null) {
                %>
                        <html:submit property="action"
                            value="<%=(String) request.getAttribute(SearchConstants.PROTEIN_VIEW_BUTTON)%>"/>
                <%
                    }
                %>
                <html:button property="action"
                    onclick="writeToWindow(
                        this.form, 'Please select an AC to display the graph')">
                    <bean:message key="results.button.graph"/>
                </html:button>
                <html:reset/>
            </td>

            <%
            }
            %>
        </tr>
    </table>
</html:form>
</html>