<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tld/hierarchView.tld" prefix="hierarchView" %>

<html:html locale="true">
<head>
<title><bean:message key="hierarchView.index.title"/></title>
<html:base/>

  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <META HTTP-EQUIV="Expires" CONTENT="-1">

  <!-- Initialize User's session -->
  <hierarchView:init/>

  <!-- Save needed request parameter -->
  <%
      String AC    = request.getParameter("AC");
      String depth = request.getParameter("depth");
      if (null != AC)
          pageContext.setAttribute("AC", AC, PageContext.PAGE_SCOPE);

      if (null != depth)
          pageContext.setAttribute("depth", depth, PageContext.PAGE_SCOPE);
  %>

 <!-- Create frames and forward any query to the left frame (in one is passed) -->
 <hierarchView:createFrames/>

</head>
<body bgcolor="white">


</body>
</html:html>
