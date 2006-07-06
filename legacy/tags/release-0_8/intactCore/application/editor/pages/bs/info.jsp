<%@ page import="uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory"%>
<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - This page accepts changes to an Annotated object's short label and full name.
  --%>

<%@ page language="java"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/intact.tld" prefix="intact"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<%-- The current view --%>
<c:set var="view" value="${user.view}"/>

<%-- Individual menu lists --%>
<c:set var="tissuemenu" value="${view.tissueMenu}"/>
<c:set var="cellmenu" value="${view.cellTypeMenu}"/>

<table width="100%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableRowHeader">
        <th class="tableCellHeader">
            <bean:message key="label.ac"/>
        </th>
        <th class="tableCellHeader">
            <bean:message key="label.shortlabel"/>
        </th>
        <th class="tableCellHeader">
            <bean:message key="label.fullname"/>
        </th>
        <th>
            <intact:documentation section="editor.short.labels"/>
        </th>
    </tr>
    <tr class="tableRowEven">
        <td class="tableCell">
            <bean:write property="ac" name="bsForm" filter="false"/>
        </td>
        <td class="tableCell">
            <html:text property="shortLabel" size="20" maxlength="20"
                name="bsForm" styleClass="inputRequired"/>
        </td>

        <td class="tableCell">
            <html:text property="fullName" size="100" maxlength="250" name="bsForm"/>
        </td>
    </tr>
</table>

<p></p>

<table width="50%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableRowHeader">
        <th class="tableCellHeader" width="30%">
            <bean:message key="label.action"/>
        </th>
        <th class="tableCellHeader">
            <bean:message key="biosource.label.tax"/>
        </th>
        <th class="tableCellHeader">
            <a href="javascript:showColumnLink('CvTissue', 'bsForm', 4)">
                <bean:message key="biosource.label.tissue"/>
            </a>
        </th>
        <th class="tableCellHeader">
            <a href="javascript:showColumnLink('CvCellType', 'bsForm', 5)">
                <bean:message key="biosource.label.cell"/>
            </a>
        </th>
    </tr>
    <tr class="tableRowEven">
        <td class="tableCell">
            <html:submit property="dispatch" titleKey="biosource.button.taxid.titleKey">
                <bean:message key="biosource.button.taxid"/>
            </html:submit>
        </td>

        <td class="tableCell">
            <html:text property="taxId" name="bsForm" size="10" maxlength="16"
                styleClass="inputRequired"/>
        </td>

        <td class="tableCell" align="left" valign="top">
            <html:select property="tissue" name="bsForm">
                <html:options name="tissuemenu"/>
            </html:select>
        </td>

        <td class="tableCell" align="left" valign="top">
            <html:select property="cellType" name="bsForm">
                <html:options name="cellmenu"/>
            </html:select>
        </td>
    </tr>
</table>
