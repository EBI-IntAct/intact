<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>

<%--
    Content of the content frame, it rely on the Tiles configuration.

    Author: Samuel Kerrien (skerrien@ebi.ac.uk)
    Version: $Id$
--%>

<html:html>

<head>
    <html:base target="_top"/>
    <link rel="stylesheet" type="text/css" href="styles/intact.css"/>
</head>

<frameset cols="60%,*" border=0>

   <frame src="graphFrame.jsp" name="graphFrame">

   <frameset ROWS="31%,*">
      <frame src="sourceListFrame.jsp"      name="sourceListFrame">
      <frame src="selectedSourceFrame.jsp" name="selectedSourcetFrame">
   </frameset>

   <noframes>
   Your browser doesn't support frames.
   </noframes>

</frameset>

<body bgcolor="#FFFFFF" topmargin="0" leftmargin="0">

</body>
</html:html>
