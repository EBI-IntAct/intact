<%@ page import="uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentROViewBean"%>
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
    ExperimentROViewBean view =
            (ExperimentROViewBean) request.getAttribute("view");
%>

Host Organism: <c:out value="${view.organism}"/>
&nbsp;&nbsp;Interaction Detection: <b><c:out value="${view.interaction}"/></b>
&nbsp;&nbsp;Participant Detection: <b><c:out value="${view.detection}"/></b>

<!-- a line to separate the header -->
<hr size=2>
