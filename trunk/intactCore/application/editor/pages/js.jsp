<%@ page import="uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants,
                 uk.ac.ebi.intact.application.editor.business.EditorService"%>
<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - This page contains java script code common to all the pages.
  --%>

<%@ page language="java"%>

<%
    // To Allow access to Editor Service.
    EditorService service = (EditorService)
            application.getAttribute(EditorConstants.EDITOR_SERVICE);
%>

<script language="JavaScript" type="text/javascript">
    // This is a global variable to setup a window.
    var newWindow;

    // Create a new window if it hasnt' created before and bring it to the
    // front if it is focusable.
    function makeNewWindow(link) {
        if (!newWindow || newWindow.closed) {
            newWindow = window.open(link, "display", "scrollbars=yes,height=500,width=600");
            newWindow.focus();
        }
        else if (newWindow.focus) {
            newWindow.focus();
            newWindow.location.href = link;
        }
    }

    // Will be invoked when the user selects on a link.
    function show(topic, label) {
        var link = "<%=service.getSearchURL(request)%>"
            + "?searchString=" + label + "&searchClass=" + topic;
        //    window.alert(link);
        makeNewWindow(link);
    }

    // Displays the link from Xref's primary id in the same window as the
    // search window.
    function showXrefPId(link) {
        makeNewWindow(link);
    }
</script>
