<%@ page import="uk.ac.ebi.intact.application.predict.struts.framework.PredictConstants"%>

<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Displays the results data from the Pay-As-You-Go Prediction Algorithm.
  --%>

<%@ page language="java" %>

<%@ taglib uri="http://jakarta.apache.org/taglibs/display" prefix="display" %>

<script language="JavaScript" type="text/javascript">
    // This is a global variable to setup a window.
    var newWindow;

    // Create a new window if it hasnt' created before and bring it to the
    // front if it is focusable.
    function makeNewWindow(link) {
        if (!newWindow || newWindow.closed) {
            newWindow = window.open(link, "display", "scrollbars=yes,height=500,width=600");
            newWindow.focus();
        }
        else if (newWindow.focus) {
            newWindow.focus();
            newWindow.location.href = link;
        }
    }

    // Displays the link from the protein id.
    function showProtein(link) {
        makeNewWindow(link);
    }
</script>

<%--<html:errors/>--%>

<%--       <head>--%>
<%--        <title> Pay-As-You-Go Prediction Algorithm - Top 50 Protein Targets </title>--%>
<%--    	</head>--%>
<h1>Top 50 proteins predicted as the best candidates for the next round of pull-down experiments via the Pay-As-You-Go Strategy</h1><hr>

<%
    String uri = request.getContextPath() + "/predict.do";
%>

<display:table width="100%" name="<%=PredictConstants.PREDICTION%>"
    scope="request" pagesize="20" requestURI="<%=uri%>">
    <display:column property="rank" title="Rank" />
    <display:column property="shortLabelLink" title="Short Label" />
    <display:column property="fullName" title="Full Name" />
</display:table>

<%--	--%>
<%--	   <logic:iterate name="PREDICTION" id="protein" type="uk.ac.ebi.intact.application.predict.struts.view.ResultBean" scope="request">--%>
<%--			  <jsp:getProperty name="protein" property="rank"/>--%>
<%--              <jsp:getProperty name="protein" property="shortLabelLink"/>--%>
<%--              <jsp:getProperty name="protein" property="fullName"/>--%>
<%--        		  <br>--%>
<%--		   --%>
<%----%>
<%--    	   </logic:iterate>--%>
	
	
