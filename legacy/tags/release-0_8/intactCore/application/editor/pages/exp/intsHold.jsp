<%@ page import="uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentViewBean"%>

<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Displays Experiments not yest added for an Interaction.
  --%>

<%@ page language="java"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/display" prefix="display" %>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<%-- Only display the table if the interactions less (<) the allowed limit --%>
<c:if test="${user.view.numberOfInteractions lt service.interactionLimit}">

    <h3>Interactions not yet added to the Experiment</h3>

    <c:if test="${user.view.holdInteractionCount gt 0 and empty noDisplayInts}">

        <%
            ExperimentViewBean view = (ExperimentViewBean) user.getView();
            pageContext.setAttribute("ints", view.getHoldInteractions());
        %>

        <display:table width="100%" name="ints"
            decorator="uk.ac.ebi.intact.application.editor.struts.view.experiment.IntHoldDisplayWrapper">
            <display:column property="action" title="Action" />
            <display:column property="shortLabel" title="Short Label" sort="true"/>
            <display:column property="ac" title="Ac" />
            <display:column property="fullName" title="Full Name" />
        </display:table>
    </c:if>
</c:if>