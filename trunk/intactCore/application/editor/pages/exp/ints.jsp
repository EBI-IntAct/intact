<%@ page import="uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentViewBean"%>

<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Presents Interaction information for an experiment. The form name is hard
  - coded (expForm).
  --%>

<%@ page language="java"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/display" prefix="display" %>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<jsp:useBean id="service" scope="application"
    class="uk.ac.ebi.intact.application.editor.business.EditorService"/>

<h3>Interactions</h3>

<%-- Check the limit --%>
<c:if test="${user.view.numberOfInteractions gt service.interactionLimit}">
    <div class="warning">
        <bean:message key="message.ints.limit.exceed"
            arg0='<%=Integer.toString(
                    ((ExperimentViewBean) user.getView()).getNumberOfInteractions())%>'
            arg1='<%=service.getResource("exp.interaction.limit")%>'/>
    </div>
    <%-- Set a flag to not to display any interactions --%>
    <c:set var="noDisplayInts" value="yes"/>
</c:if>

<%-- Don't display an empty table if there are no interactions to display or too
     many interactions to display.
--%>
<c:if test="${user.view.interactionCount gt 0 and empty noDisplayInts}">

    <%
        ExperimentViewBean view = (ExperimentViewBean) user.getView();
        pageContext.setAttribute("ints", view.getInteractions());
        String uri = request.getContextPath() + "/do/result?ac="
                + view.getAc() + "&searchClass=Experiment";
    %>

    <display:table width="100%" name="ints"
        pagesize="<%=service.getResource(\"exp.interaction.page.limit\")%>"
        requestURI="<%=uri%>"
        decorator="uk.ac.ebi.intact.application.editor.struts.view.experiment.IntDisplayWrapper">
        <display:column property="action" title="Action" />
        <display:column property="shortLabel" title="Short Label"/>
        <display:column property="ac" title="Ac" />
        <display:column property="fullName" title="Full Name" />
    </display:table>
</c:if>