<%@ page language="java" %>

<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - This layout displays graph management components for hierarchView.
   - According to the current state of the displayed graph : its depth,
   - we display button in order to get expanded or contracted.
   - We show only available options (e.g. if the depth can be desacrease
   - we don't show the contract button).
   -
   - @author Samuel Kerrien (skerrien@ebi.ac.uk)
   - @version $Id$
-->

<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.IntactUserI,
                 uk.ac.ebi.intact.application.hierarchView.business.Constants,
                 java.util.Collection,
                 java.util.Iterator,
                 uk.ac.ebi.intact.model.Interactor,
                 java.util.ArrayList,
                 uk.ac.ebi.intact.application.hierarchView.struts.view.utils.DropDownItemBean"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/intact.tld"      prefix="intact"%>

<%
    /**
     * Retreive user's data from the session
     */
    IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);

    if (user == null) {
        // no user in the session, don't display anything
        return;
    }
%>

<%
   if (user.InteractionNetworkReadyToBeDisplayed()) {
%>

<br>
<hr>

<!-- PSI Download section -->

<script language="JavaScript" type="text/javascript">

    // Returns true an interactor has been selected.
    function checkIfDataSelected(form, msg) {
        if (form.ac.value == "") {
            window.alert( msg );
            return false;
        }
        return true;
    }

    // This is a global variable to setup a window.
    var newWindow;

    // Create a new window if it hasnt' created before and bring it to the
    // front if it is focusable.
    function makeNewWindow(link) {
        if (!newWindow || newWindow.closed) {
            newWindow = window.open(link, "PSI-MI Download");
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
        if (!checkIfDataSelected(form, msg)) {
            return;
        }
        var ac = form.ac.value;
        <%
            String graph2mif = user.GRAPH2MIF_PROPERTIES.getProperty( "graph2mif.url" );
        %>

        var link = "<%= graph2mif %>"
            + "?ac=" + ac + "&depth=" + form.depth.value
            + "&strict=false";
        //window.alert(link);
        makeNewWindow(link);
    }

</script>


 <form name="psiForm">

    <table width="100%">
        <tr>
          <th colspan="2">
             <div align="left">
                <strong><bean:message key="sidebar.psi.section.title"/></strong>
                <intact:documentation section="hierarchView.PPIN.download" />
             </div>
          </th>
        </tr>

        <tr>
            <td>

                <select name="ac">
                    <option value="">Interactor</option>

                <%
                    // Build up the drop-down list content.
                    Collection interactors = user.getInteractionNetwork().getCentralInteractors();
                    for ( Iterator iterator = interactors.iterator (); iterator.hasNext (); ) {
                        Interactor interactor = (Interactor) iterator.next ();
                    %>
                          <option value="<%= interactor.getAc() %>"> <%= interactor.getShortLabel() %> </option>
                    <%
                    }
                %>

                </select>

                <input type="hidden" name="depth" value="<%= user.getCurrentDepth() %>">
                <input type="hidden" name="strict" value="false">

            </td>
        <tr>
        </tr>
            <td>

                <input type="button"
                       name="action"
                       value="Download"
                       onclick="writeToWindow( this.form, 'Please select a interactor' )">

            </td>
        </tr>
    </table>

 </form>

<%
   } // if InteractionNetworkReady
%>