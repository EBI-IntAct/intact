<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles"%>

<%--
    Content of the sources list frame, it rely on the Tiles configuration.

    Author: Samuel Kerrien (skerrien@ebi.ac.uk)
    Version: $Id$
--%>

<html:html>

<head>
    <html:base target="_top"/>
    <link rel="stylesheet" type="text/css" href="styles/intact.css"/>
</head>

<body bgcolor="#FFFFFF" topmargin="0" leftmargin="0">

    <table border="1" cellpadding="3" cellspacing="0" width="100%" heigth="100%">

          <tr>
                 <td width="40%" valign="top">
                       <!-- Top Right cell: Display the highligh tool title -->
<%--                       <tiles:insert attribute="highlightTitle"/>--%>
                       <tiles:insert definition="highlightTitle"/>

                 </td>
          </tr>

          <tr>
                 <td width="40%" valign="top">
                     <!-- Bottom Right cell: Display the highligh tool -->
<%--                     <tiles:insert attribute="highlight"/>--%>
                     <tiles:insert definition="highlight"/>
                 </td>
          </tr>

    </table>

</body>
</html:html>
