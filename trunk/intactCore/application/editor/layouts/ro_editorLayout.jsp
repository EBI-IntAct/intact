<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles"%>

<%--
    Intact default look & feel layout. It consists of a header, contents and
    the footer as shown below:
    +---------------------+
    | Organization header +
    |---------------------+
    | header              +
    | contents            +
    | footer              +
    |---------------------+

    Author: Sugath Mudali (smudali@ebi.ac.uk)
    Version: $Id$
--%>

<html:html>
<head>
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="expires" content="-1">
    <title><tiles:getAsString name="title"/></title>
    <html:base/>
    <link rel="stylesheet" type="text/css" href="styles/intact.css"/>
</head>

<body bgcolor="#FFFFFF" topmargin="0" leftmargin="0">
<table border="0" height="100%" width="100%" cellspacing="5">

    <tr>
        <td bgcolor="#ffeeaa" height="8%">
            <tiles:insert attribute="header"/>
        </td>
    </tr>

    <%-- Content section --%>
    <tr>
        <td valign="top" height="*">
            <tiles:insert attribute="content"/>
        </td>
    </tr>

    <%-- The footer --%>
    <tr>
        <td valign="bottom" height="15%">
            <tiles:insert attribute="footer"/>
        </td>
    </tr>
</table>
</body>
</html:html>
