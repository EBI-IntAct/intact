<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tld/hierarchView.tld" prefix="hierarchView" %>

<html:html locale="true">
<head>

   <META HTTP-EQUIV="Pragma" CONTENT="no-cache"> 
   <META HTTP-EQUIV="Expires" CONTENT="-1">

   <title>
      <bean:message key="hierarchView.hierarchy.title"/>
   </title>

   <link rel="stylesheet" href="hierarchview.css" type="text/css">

   <html:base/>

   <script language="JavaScript">
    <!--
    /**
     * Allows to forward to a specified URL inside a frame
     */
    function forward ( absoluteUrl ) {
       parent.frameHierarchy.location.href = absoluteUrl;
    }
    //-->
   </script>

</head>

<body>
        <!-- Displays available source for the selected protein -->
        <hierarchView:displaySource/>

</body>
</html:html>
