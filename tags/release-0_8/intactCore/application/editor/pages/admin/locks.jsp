<%@ page import="uk.ac.ebi.intact.application.editor.util.LockManager"%>

<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Displays locks held with the locks manager.
  --%>

<%@ page language="java"%>

<%@ taglib uri="http://jakarta.apache.org/taglibs/display" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>

<%
    pageContext.setAttribute("locks", LockManager.getInstance().getLocks());
%>

<jsp:useBean id="now" class="java.util.Date" />
Last Refresh: <i><fmt:formatDate value="${now}" pattern="EE, dd MMM yyyy HH:mm:ss Z"/></i>

<display:table width="100%" name="locks">
    <display:column property="id" title="AC" sort="true"/>
    <display:column property="owner" title="Owner"/>
    <display:column property="lockDate" title="Date" />
</display:table>
