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

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<%-- The current view --%>
<c:set var="view" value="${user.view}"/>

<%-- Individual menu lists --%>
<c:set var="organismmenu" value="${view.organismMenu}"/>
<c:set var="intermenu" value="${view.interMenu}"/>
<c:set var="identmenu" value="${view.identMenu}"/>

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
                    <html:options name="organismmenu"/>
                </html:select>
            </td>
            <td class="tableCell" align="left" valign="top">
                <html:select property="inter" name="<%=formName%>">
                    <html:options name="intermenu"/>
                </html:select>
            </td>
            <td class="tableCell" align="left" valign="top">
                <html:select property="ident" name="<%=formName%>">
                    <html:options name="identmenu"/>
                </html:select>
            </td>
        </tr>
    </table>
</html:form>
