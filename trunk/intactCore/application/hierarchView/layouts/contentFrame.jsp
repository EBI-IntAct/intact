<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>

<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - Content of the content frame, it rely on the Tiles configuration.

   - @author: Samuel Kerrien (skerrien@ebi.ac.uk)
   - @version: $Id$
-->

<html:html>

<head>
    <html:base target="_top"/>
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="expires" content="-1">
    <link rel="stylesheet" type="text/css" href="styles/intact.css"/>
</head>

<frameset cols="63%,*" border=0>

   <frame src="graphFrame.jsp" name="FOO">

   <frameset ROWS="31%,*">
      <frame src="sourceListFrame.jsp"     name="sourceListFrame">
      <frame src="selectedSourceFrame.jsp" name="selectedSourcetFrame">
   </frameset>

   <noframes>
   Your browser doesn't support frames.
   </noframes>

</frameset>

<body bgcolor="#FFFFFF" topmargin="0" leftmargin="0">

</body>
</html:html>
