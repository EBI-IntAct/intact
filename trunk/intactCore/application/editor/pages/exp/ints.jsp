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
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%-- Need these for beans for to get argumnets to display a message when
     the number of interactions exceed the allowed limit
--%>
<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<jsp:useBean id="service" scope="application"
    class="uk.ac.ebi.intact.application.editor.business.EditorService"/>

<h3>Interactions</h3>

<%-- Check the limit --%>
<c:if test="${user.view.numberOfInteractions gt service.interactionLimit}">
    <div class="warning">
        <bean:message key="message.ints.limit"
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
<c:if test="${not empty expForm.map.ints and empty noDisplayInts}">
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr class="tableRowHeader">
            <th class="tableCellHeader" width="29%">
                <bean:message key="label.action"/>
            </th>
            <th class="tableCellHeader" width="10%">
                <bean:message key="label.shortlabel"/>
            </th>
            <th class="tableCellHeader" width="10%">
                <bean:message key="label.ac"/>
            </th>
            <th class="tableCellHeader" width="51%">
                <bean:message key="label.fullname"/>
            </th>
        </tr>
    </table>
<div style="overflow: auto; width: 100%; height: 150px; border-left: 1px gray solid; border-bottom: 1px gray solid;">
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <%-- To calculate row or even row --%>
        <c:set var="row"/>
        <c:forEach var="interaction" items="${expForm.map.ints}">
            <%-- Different styles for even or odd rows --%>
            <c:choose>
                <c:when test="${row % 2 == 0}">
                    <tr class="tableRowEven">
                </c:when>
                <c:otherwise>
                    <tr class="tableRowOdd">
                </c:otherwise>
            </c:choose>

                <td width="10%" class="tableCell">
                    <html:submit indexed="true" property="intCmd"
                        titleKey="exp.int.button.edit.titleKey">
                        <bean:message key="exp.int.button.edit"/>
                    </html:submit>
                </td>

                <td width="10%" class="tableCell">
                    <html:submit indexed="true" property="intCmd"
                        titleKey="exp.int.button.del.titleKey">
                        <bean:message key="exp.int.button.del"/>
                    </html:submit>
                </td>

                <td width="11%" class="tableCell">
                    <bean:write name="interaction" property="shortLabelLink" filter="false"/>
                </td>
                <td width="12%" class="tableCell">
                    <bean:write name="interaction" property="ac"/>
                </td>
                <td width="57%" class="tableCell">
                    <bean:write name="interaction" property="fullName"/>
                </td>
            </tr>
        </c:forEach>
    </table>
    </div>
</c:if>