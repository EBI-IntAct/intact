<%@ page import="uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractROViewBean"
%>
<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - This page displays  a read only version of a comment.
  --%>

<%@ page language="java"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/display" prefix="display" %>

<%-- Class wide declarations. --%>
<%!
    String annotations = "annotations";
%>

<%-- Fill the form before the display --%>
<%
    AbstractROViewBean view =
            (AbstractROViewBean) request.getAttribute("view");
    pageContext.setAttribute(annotations, view.getAnnotations());
%>

<c:if test="${not empty view.annotations}">

    <h3>Comments</h3>

    <display:table width="100%" name="<%=annotations%>" pagesize="5">
        <display:column property="topic" title="Topic"/>
        <display:column property="description" title="Description"/>
    </display:table>
</c:if>
