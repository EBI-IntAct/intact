<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>

<html:html locale="true">
<head>
<title><bean:message key="hierarchView.index.title"/></title>
<html:base/>

  <META HTTP-EQUIV="Pragma" CONTENT="no-cache"> 
  <META HTTP-EQUIV="Expires" CONTENT="-1">



  <frameset cols="50%, 50%">
    <frame name="frameView"      src="view.jsp">
    <frame name="frameHierarchy" src="hierarchy.jsp">
  </frameset>

  <noframes>
    <p>This frameset document contains:      
    <ul>
      <li><a href="view.jsp"> Visualization page </a>
      <li><a href="hierarchy.jsp"> Hierarchy page </a>
    </ul>
  </noframes>

</head>
<body bgcolor="white">
 
</body>
</html:html>
