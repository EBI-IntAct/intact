<%@ page import="org.apache.commons.beanutils.DynaBean"%>
<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - This page accepts inputs for the bio source editor.
  --%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<%-- Set the drop down lists --%>
<c:set var="menus" value="${user.view.editorMenus}"/>

<%-- Individual menu lists --%>
<c:set var="organismlist" value="${menus['Organisms']}"/>
<c:set var="interactiontypelist" value="${menus['InteractionTypes']}"/>
<c:set var="experimentlist" value="${menus['Experiments']}"/>

<%-- Class wide declarations. --%>
<%!
    String formName = "interactionForm";
%>

<%-- Fill the form before the display --%>
<%
    DynaBean form = user.getDynaBean(formName, request);
    user.getView().fillEditorSpecificInfo(form);
    request.setAttribute(formName, form);
%>

<html:form action="/interaction/info">
    <table width="50%" border="0" cellspacing="1" cellpadding="2">
        <tr class="tableRowEven">
            <th class="tableCellHeader">kD</th>
            <td class="tableCell">
                <html:text property="kD" name="<%=formName%>" size="5"
                    maxlength="16"/>
            </td>
        </tr>
        <tr class="tableRowOdd">
            <th class="tableCellHeader">Interaction Type</th>
            <td class="tableCell" align="left" valign="top">
                <html:select property="interactionType" name="<%=formName%>">
                    <html:options name="interactiontypelist" />
                </html:select>
                <html:errors property="int.interaction"/>
            </td>
        <tr class="tableRowEven">
            <th class="tableCellHeader">Organisms Type</th>
            <td class="tableCell" align="left" valign="top">
                <html:select property="organism" name="<%=formName%>">
                    <html:options name="organismlist" />
                </html:select>
                <html:errors property="int.organism"/>
            </td>
            </tr>
        <tr class="tableRowOdd">
            <th class="tableCellHeader">Experiment</th>
            <td class="tableCell" align="left" valign="top">
                <html:select property="experiment" name="<%=formName%>">
                    <html:options name="experimentlist" />
                </html:select>
                <html:errors property="int.experimentlist"/>
                 or <html:text property="searchInput" name="<%=formName%>" size="10"
                    maxlength="16"/>&nbsp;
                <html:submit property="submit">
                    <bean:message key="button.search"/>
                </html:submit>
            </td>
        </tr>
    </table>
</html:form>
