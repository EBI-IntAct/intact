 <%--
    Search results page.

    @author Chris Lewington and Sugath Mudali
    @version $Id$
--%>

<%@ page language="java"  %>
<%@ page buffer="none"    %>
<%@ page autoFlush="true" %>

<%@ page import="uk.ac.ebi.intact.application.search2.struts.framework.util.SearchConstants,
                 uk.ac.ebi.intact.application.search2.business.IntactServiceIF,
                 uk.ac.ebi.intact.application.search2.struts.view.AbstractViewBean,
                 uk.ac.ebi.intact.model.Protein"%>

<!-- Import util classes -->
<%@ page import="java.util.*"%>
 <%@ page import="uk.ac.ebi.intact.application.commons.util.UrlUtil"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>


<%
    // To allow access hierarchView properties.
    IntactServiceIF service = (IntactServiceIF) application.getAttribute(
            SearchConstants.INTACT_SERVICE);

    //build the absolute path out of the context path for 'search'
    String relativePath = UrlUtil.absolutePathWithoutContext(request);

    //build the URL for hierarchView from the absolute path and the relative details..
    String hvPath = relativePath.concat(service.getHierarchViewProp("hv.url"));
    String minePath = relativePath.concat("mine/display.jsp");
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

    function writeToWindow(form, msg, id) {
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
                        ac = name;
                    }
                    else {
                        ac = ac + "," + name;
                    }
                }
            }
        }
        if(id == 0) {
	     	var link = "<%=hvPath%>"
    	        + "?AC=" + ac + "&depth=" + <%=service.getHierarchViewProp("hv.depth")%>
        	    + "&method=" + "<%=service.getHierarchViewProp("hv.method")%>";
        	//window.alert(link);
        	makeNewWindow(link);
       	}
       	else {
       		var link = "<%=minePath%>?AC=" + ac ;
        	//window.alert(link);
        	makeNewWindow(link);
       	} 
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

<form name="viewForm">

<!-- Get the view Bean and dump its data to the web page-->
<%
    AbstractViewBean bean = (AbstractViewBean) session.getAttribute(SearchConstants.VIEW_BEAN);

    if (bean == null) {
        out.write("nothing found in the session.");
    }  else {

         if(bean.showGraphButtons()) {
         //display links - none to display for single object views.
%>
                <html:link href='javascript:checkAll()'>
                    Check All
                </html:link>
                <html:link href='javascript:clearAll()'>
                    Clear All
                </html:link>
<%
         }

        //build the help link - uses the current host path information
        //plus the filename as specified in the servlet context
        String relativeHelpLink = this.getServletConfig().getServletContext().getInitParameter("helpLink");
        //build the help link out of the context path - strip off the 'search' bit...
        String helpLink = relativePath.concat(relativeHelpLink) + bean.getHelpSection();

%>
        <html:link href="<%=helpLink %>" target="new">
            Help
        </html:link>
        <hr size=2>

<%
        // Displays the content of the view.
        bean.getHTML( out );
%>

    <hr size=2>

        <!-- The footer table. -->
        <table cellpadding="1" cellspacing="0" border="1" width="100%">
            <tr>

            <%
                if(bean.showGraphButtons()) {
                    //display buttons - none to display for single object views..
            %>
                <td align="center">
                    <input type="button" name="action" value="Graph"
                        onclick="writeToWindow( this.form, 'Please select an AC to display the graph', 0)">
                    <input type="button" name="action" value="Path"
                        onclick="writeToWindow( this.form, 'Please select an AC to display the path', 1)">
                    <input type="reset" value="Reset">
                </td>
           <%
              }
           %>
            </tr>
        </table>
<%
     } // end if bean != null
%>
</form>
