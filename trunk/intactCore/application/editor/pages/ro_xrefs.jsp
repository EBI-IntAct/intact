<%@ page import="uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractROViewBean"%>
<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - This page displays a read only version of a xreference.
  --%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<%-- Class wide declarations. --%>
<%!
    String xrefs = "xrefs";
%>

<%-- Fill the form before the display --%>
<%
    AbstractROViewBean view =
            (AbstractROViewBean) request.getAttribute("view");
    pageContext.setAttribute(xrefs, view.getXrefs());
%>

<c:if test="${not empty view.xrefs}">

    <h3>Xreferences</h3>

    <display:table width="100%"select name="<%=xrefs%>" pagesize="5">
        <display:column property="database" title="Database" width="11%"/>
        <display:column property="primaryId" title="Primary ID" width="20%"/>
        <display:column property="secondaryId" title="Secondary ID" width="20%"/>
        <display:column property="releaseNumber" title="Release Number" width="20%"/>
        <display:column property="qualifier" title="Reference Qualifier" width="20%"/>
    </display:table>
</c:if>
