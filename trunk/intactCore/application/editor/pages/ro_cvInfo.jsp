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
  - This page displays (read only) an Annotated object's short label and full
  - name.
  --%>

<%@ page language="java"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<%
    AbstractROViewBean view =
            (AbstractROViewBean) request.getAttribute("view");
%>

AC: <b><c:out value="${view.ac}"/></b>
&nbsp;&nbsp;Short Label: <b><c:out value="${view.shortLabel}"/></b>
&nbsp;&nbsp;Full Name: <b><c:out value="${view.fullName}"/></b>

<!-- a line to separate the header -->
<hr size=2>
