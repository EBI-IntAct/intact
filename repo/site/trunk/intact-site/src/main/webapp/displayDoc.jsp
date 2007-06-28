<%@ page language="java"%>

<%--
    Create necessary frame to display the intact documentation
    show directly the requested section if any.
    WARNING:
             The name of that file is used by a JSP tag :
             uk.ac.ebi.intact.application.commons.struts.taglibs.DocumentationTag
             changing that name would affect the behaviour of the tag.

    Author: Samuel Kerrien (skerrien@ebi.ac.uk)
    Version: $Id$
--%>

 <%
   String section = request.getParameter("section");
   if (section == null) {
       section = ""; // no section specified, display
   } else {
       section = "#" + section;
   }

%>

<html>
<head>
    <title></title>
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="expires" content="-1">
</head>

    <!-- Frames definition -->
    <frameset cols="40%,*" border="1">

       <%-- The names of the frames are not supposed to be changes,
            they are used in the HTML document. Specified in the XSLs
            document.
         --%>
       <frame src="doc/html/tableOfContent.html<%= section %>" name="contents">
       <frame src="doc/html/documentation.html<%= section %>"  name="text">

       <noframes>
       Your browser doesn't support frames.
       </noframes>

    </frameset>

<body bgcolor="#FFFFFF" topmargin="0" leftmargin="0">
</body>
</html>

