<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Presents Experiments information for an Interaction.
  --%>

<%@ page language="java"%>
<%@ page import="uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/struts-nested.tld" prefix="nested"%>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"%>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<c:set var="viewbean" value="${user.view}"/>

<%-- Class wide declarations. --%>
<%!
    String formName = EditorConstants.FORM_INTERACTION_EXP;
%>

<h3>Experiments</h3>

<c:if test="${not empty viewbean.experiments}">

    <html:form action="/interaction/experiment">
        <table width="100%" border="0" cellspacing="1" cellpadding="2">
            <tr class="tableRowHeader">
                <th class="tableCellHeader" width="10%">Action</th>
                <th class="tableCellHeader" width="10%">Short Label</th>
                <th class="tableCellHeader" width="10%">Pubmed Id</th>
                <th class="tableCellHeader" width="10%">IntAct AC</th>
                <th class="tableCellHeader" width="60%">Full Name</th>
            </tr>
            <%-- To calculate row or even row --%>
            <c:set var="row"/>
            <nested:iterate name="<%=formName%>" property="items">
                <%-- Different styles for even or odd rows --%>
                <c:choose>
                    <c:when test="${row % 2 == 0}">
                        <tr class="tableRowEven">
                    </c:when>
                    <c:otherwise>
                        <tr class="tableRowOdd">
                    </c:otherwise>
                </c:choose>

                    <td class="tableCell">
                        <html:submit indexed="true" property="cmd"
                            titleKey="int.exp.button.del.titleKey">
                            <bean:message key="button.delete"/>
                        </html:submit>
                    </td>

                    <td class="tableCell">
                        <nested:write property="shortLabelLink" filter="false"/>
                    </td>
                    <td class="tableCell">
                    </td>
                    <td class="tableCell">
                        <nested:write property="ac"/>
                    </td>
                    <td class="tableCell">
                        <nested:write property="fullName"/>
                    </td>
                </tr>
            </nested:iterate>

            <%-- Prints all the error messages relevant to this page only. --%>
<%--            <logic:messagesPresent property="int.exp.add">--%>
<%--                <tr class="tableRowOdd">--%>
<%--                    <td class="tableErrorCell" colspan="5">--%>
<%--                        <html:errors/>--%>
<%--                    </td>--%>
<%--                </tr>--%>
<%--            </logic:messagesPresent>--%>
<%----%>
        </table>
    </html:form>
</c:if>