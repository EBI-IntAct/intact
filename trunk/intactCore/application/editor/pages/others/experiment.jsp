<%@ page import="org.apache.commons.beanutils.DynaBean"%>
<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Experiment specific editor.
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"%>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<%-- Set the drop down lists --%>
<c:set var="menus" value="${user.view.editorMenus}"/>

<%-- Individual menu lists --%>
<c:set var="organismlist" value="${menus['Organisms']}"/>
<c:set var="interactionlist" value="${menus['Interactions']}"/>
<c:set var="identificationlist" value="${menus['Identifications']}"/>

<%-- Class wide declarations. --%>
<%!
    String formName = "experimentForm";
%>

<html:form action="/experiment/info">
    <table width="80%" border="0" cellspacing="1" cellpadding="2">
        <tr class="tableRowHeader">
            <th class="tableCellHeader">Action</th>
            <th class="tableCellHeader">Host Organism</th>
            <th class="tableCellHeader">Interaction Detection</th>
            <th class="tableCellHeader">Participant Detection</th>
        </tr>
        <tr class="tableRowEven">
            <td class="tableCell">
                <html:submit titleKey="button.save.titleKey">
                    <bean:message key="button.save"/>
                </html:submit>
            </td>
            <td class="tableCell" align="left" valign="top">
                <html:select property="organism" name="<%=formName%>">
                    <html:options name="organismlist" />
                </html:select>
                <html:errors property="exp.organism"/>
            </td>
            <td class="tableCell" align="left" valign="top">
                <html:select property="interaction" name="<%=formName%>">
                    <html:options name="interactionlist" />
                </html:select>
                <html:errors property="exp.interaction"/>
            </td>
            <td class="tableCell" align="left" valign="top">
                <html:select property="identification" name="<%=formName%>">
                    <html:options name="identificationlist" />
                </html:select>
                <html:errors property="exp.identification"/>
            </td>
        </tr>

        <%-- Prints all the error messages relevant to this page only. --%>
        <logic:messagesPresent>
            <tr class="tableRowOdd">
                <td class="tableErrorCell" colspan="4">
                    <%-- Filter out other error messages. --%>
                    <html:errors property="exp.validation"/>
                </td>
            </tr>
        </logic:messagesPresent>
    </table>

</html:form>
