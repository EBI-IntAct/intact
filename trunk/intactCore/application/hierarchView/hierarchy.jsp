<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tld/hierarchView.tld" prefix="hierarchView" %>

<%@ page import="uk.ac.ebi.intact.application.hierarchView.highlightment.source.*,
                 uk.ac.ebi.intact.application.hierarchView.struts.view.LabelValueBean" %>
<%@ page import="uk.ac.ebi.intact.application.hierarchView.struts.StrutsConstants" %>
<%@ page import="uk.ac.ebi.intact.application.hierarchView.struts.*"%>
<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader" %>

<%@page import="java.util.Collection" %>
<%@page import="java.util.Iterator" %>
<%@page import="java.util.Properties"%>

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
