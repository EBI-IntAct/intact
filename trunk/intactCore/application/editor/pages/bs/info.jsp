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

<style type="text/css">
    <%@ include file="/layouts/styles/editor.css" %>
</style>

<table width="100%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableRowHeader">
        <th class="tableCellHeader">AC</th>
        <th class="tableCellHeader">
            <bean:message key="cvinfo.label.shortlabel"/>
        </th>
        <th class="tableCellHeader">Full Name</th>
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
    </tr>
    <tr class="tableRowEven">
        <td class="tableCell">
            <html:submit property="dispatch" titleKey="biosource.button.taxid.titleKey">
                <bean:message key="biosource.button.taxid"/>
            </html:submit>
        </td>
        <td class="tableCell">
            <html:text property="taxId" name="bsForm" size="10" maxlength="16"/>
        </td>
    </tr>
</table>
