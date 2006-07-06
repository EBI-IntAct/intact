<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles"%>

<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - Content of the sources list frame, it rely on the Tiles configuration.
   -
   - @author: Samuel Kerrien (skerrien@ebi.ac.uk)
   - @version: $Id$
-->

<html:html>

<head>
    <base target="_top">

    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="expires" content="-1">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/layouts/styles/intact.css"/>
</head>

<body bgcolor="#FFFFFF" topmargin="0" leftmargin="0">

    <table border="1" cellpadding="3" cellspacing="0" width="100%" heigth="100%">

          <tr>
                 <td width="40%" valign="top">
                       <!-- Top Right cell: Display the highligh tool title -->
                       <tiles:insert definition="highlightTitle"/>

                 </td>
          </tr>

          <tr>
                 <td width="40%" valign="top">
                     <!-- Bottom Right cell: Display the highligh tool -->
                     <tiles:insert definition="highlight"/>
                 </td>
          </tr>

    </table>

</body>
</html:html>
