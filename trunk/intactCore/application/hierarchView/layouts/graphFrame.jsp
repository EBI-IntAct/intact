<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles"%>

<%--
    Content of the graph frame, it rely on the Tiles configuration.

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
                 <td width="60%" valign="top">
                      <!-- Top Left cell: displays the interaction network title -->
<%--                      <tiles:insert attribute="graphTitle" ignore="true"/>--%>
                      <tiles:insert definition="hierarchView.graphTitle.layout" ignore="true"/>
                 </td>
          </tr>

          <tr>
                 <td width="40%" valign="top">
                       <!-- Bottom Left cell: displays the interaction network -->
<%--                       <tiles:insert attribute="graph" ignore="true"/>--%>
                       <tiles:insert definition="hierarchView.graph.layout" ignore="true"/>
                 </td>
          </tr>

    </table>

</body>
</html:html>
