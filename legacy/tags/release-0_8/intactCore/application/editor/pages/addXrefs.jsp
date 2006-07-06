<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Presents the page to add a xreference to an Annotated object.
  --%>

<%@ page language="java"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/intact.tld" prefix="intact"%>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<c:set var="menus" value="${user.view.addXrefMenus}"/>

<%-- Individual menu lists --%>
<c:set var="view" value="${user.view}"/>
<c:set var="dblist" value="${view.addDatabaseMenu}"/>
<c:set var="qlist" value="${menus['QualifierNames']}"/>

<%-- Adds a new xreferece. This will invoke addXref action. --%>
<table class="table" width="100%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableRowHeader">
        <th class="tableCellHeader">
            <bean:message key="label.action"/>
        </th>
        <th class="tableCellHeader">
            <bean:message key="xref.label.database"/>
        </th>
        <th class="tableCellHeader">
            <bean:message key="xref.label.primary"/>
        </th>
        <th class="tableCellHeader">
            <bean:message key="xref.label.secondary"/>
        </th>
        <th class="tableCellHeader">
            <bean:message key="xref.label.release"/>
        </th>
        <th class="tableCellHeader">
            <bean:message key="xref.label.reference"/>
        </th>
        <th>
            <intact:documentation section="editor.xrefs"/>
        </th>
    </tr>
    <tr class="tableRowOdd">
        <td class="tableCell" align="right" valign="top">
            <html:submit property="dispatch" titleKey="xrefs.button.add.titleKey">
                <bean:message key="xrefs.button.add"/>
            </html:submit>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:select property="xref.database">
                <html:options name="dblist" />
            </html:select>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:text property="xref.primaryId" size="15"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:text property="xref.secondaryId" size="15"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:text property="xref.releaseNumber" size="15"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:select property="xref.qualifier"
                value="<%=user.getView().getDefaultXrefQualifier()%>">
                <html:options name="qlist" />
            </html:select>
        </td>
    </tr>
</table>
