 <%--
    Search results page.

    @author Chris Lewington and Sugath Mudali
    @version $Id$
--%>

<%@ page language="java"%>

<%@ page import="uk.ac.ebi.intact.application.search.struts.framework.util.SearchConstants,
                 uk.ac.ebi.intact.application.search.business.IntactServiceIF,
                 uk.ac.ebi.intact.application.search.struts.view.IntactViewBean"%>

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
                // Only porcess if they are checked.
                if (form.elements[i].checked) {
                    var name = form.elements[i].name;
                    // Remove the table identifer from name to get ac.
                    ac = name.split("_")[2];
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
    <hr size=2>

<!-- Get the view Bean and dump its data to the web page-->
<%
    //write to the JSP output stream
    StreamResult result = new StreamResult(out);

    if(session.getAttribute(SearchConstants.SINGLE_OBJ_VIEW_BEAN) != null) {
        //only have a single object to show..
        IntactViewBean bean = (IntactViewBean)session.getAttribute(SearchConstants.SINGLE_OBJ_VIEW_BEAN);
        bean.transform("0", result);
    }
    else {

        // The collection of beans to process.
        Map idToView = (Map) session.getAttribute(SearchConstants.FORWARD_MATCHES);

        // Process each bean.
        for (Iterator it = idToView.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            IntactViewBean bean = (IntactViewBean) entry.getValue();
            String id = (String) entry.getKey();
            bean.transform(id, result);
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