<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Displays the results data from the search query.
  --%>

<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"%>

<%--<jsp:useBean id="user" scope="session"--%>
<%--    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>--%>

<style type="text/css">
    <%@ include file="/layouts/styles/editor.css" %>
</style>

<%-- Javascript code to link to the search page. --%>
<jsp:include page="js.jsp" />

<logic:messagesPresent>
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <html:messages id="error">
            <tr class="tableRowEven">
                <td class="tableErrorCell"><bean:write name="error"/></td>
            </tr>
        </html:messages>
    </table>
</logic:messagesPresent>

<html:form action="/result">
    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr class="tableRowHeader">
            <th class="tableCellHeader">AC</th>
            <th class="tableCellHeader">Short Label</th>
            <th class="tableCellHeader" width="70%">Full Name</th>
            <th class="tableCellHeader">Lock</th>
        </tr>

        <%-- To calculate row or even row --%>
        <c:set var="row"/>

        <c:forEach var="i" items="${user.searchResult}">
            <!-- Different styles for even or odd rows -->
            <c:choose>
                <c:when test="${row % 2 == 0}">
                    <tr class="tableRowEven">
                </c:when>
                <c:otherwise>
                    <tr class="tableRowOdd">
                </c:otherwise>
            </c:choose>
            <c:set var="row" value="${row + 1}"/>

            <td class="tableCell">
                <bean:write name="i" property="searchLink" filter="false"/>
            </td>

            <!-- The link for the short label is not displayed if there is a
                 lock for the object and displayed in different colour -->
            <c:choose>
                <c:when test="${i.locked == 'true'}">
                    <c:choose>
                        <c:when test="${i.lockOwnerSimple == user.userName}">
                           <!-- Lock owner is the current user -->
                            <td class="editCell">
                                <bean:write name="i" property="editorLink" filter="false"/>
                            </td>
                        </c:when>
                        <c:otherwise>
                           <!-- Locked by other than the current user -->
                            <td class="errorCell">
                                <bean:write name="i" property="shortLabel"/>
                            </td>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <td class="tableCell">
                        <bean:write name="i" property="editorLink" filter="false"/>
                    </td>
                   </c:otherwise>
            </c:choose>

            <td class="tableCell">
                <bean:write name="i" property="fullName"/>
            </td>
            <td class="tableCell">
                <bean:write name="i" property="lockOwner" filter="false"/>
            </td>
        </tr>
        </c:forEach>
    </table>
</html:form>
