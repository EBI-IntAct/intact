<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld"  prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tld/intact.tld"       prefix="intact"%>

<%--
    Intact default look & feel layout with frames.
    It consists of a sidebar and a display area.
    The display area conatns the header, contents and the footer as shown below:
    +-----------------+
    | side | header   +
    | bar  | contents +
    |      | footer   +
    |-----------------+

    Author: Samuel Kerrien (skerrien@ebi.ac.uk)
    Version: $Id$
--%>

<html:html>

<head>
    <title><tiles:getAsString name="title"/></title>
    <html:base target="_top"/>
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="expires" content="-1">
    <link rel="stylesheet" type="text/css" href="styles/intact.css"/>
</head>

    <frameset cols="12%,*" border=0>

       <frame src="<tiles:getAsString name="sidebar"/>" name="sidebarFrame">

       <frameset rows="8%,*, 9%">
          <frame src="<tiles:getAsString name="header"/>"  name="headerFrame">
          <frame src="<tiles:getAsString name="content"/>" name="contentFrame">
          <frame src="footerFrame.jsp"  name="footerFrame">
       </frameset>

       <noframes>
       Your browser doesn't support frames.
       </noframes>

    </frameset>

<body bgcolor="#FFFFFF" topmargin="0" leftmargin="0">

<intact:saveErrors/>

</body>
</html:html>
