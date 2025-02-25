 <!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - The top panel consists of information for a sequence.
  --%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/intact.tld" prefix="intact"%>

<%-- The menus --%>
<c:set var="menus" value="${user.view.menus}"/>

<%-- Individual menu lists --%>
<c:set var="polymermenu" value="${menus['Polymer']}"/>
<c:set var="organismmenu" value="${menus['Organism']}"/>

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
            <bean:write property="ac" name="seqForm" filter="false"/>
        </td>
        <td class="tableCell">
            <html:text property="shortLabel" size="20" maxlength="20" name="seqForm"
                styleClass="inputRequired"/>
        </td>

        <td class="tableCell">
            <html:text property="fullName" size="100" maxlength="250" name="seqForm"/>
        </td>
    </tr>
</table>
<html:errors property="shortLabel"/>

<table class="table" width="100%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableRowHeader">
        <th class="tableCellHeader">Interactor Type</th>
        <th class="tableCellHeader">Source</th>
        <th class="tableCellHeader">Sequence</th>
        <th>
            <intact:documentation section="editor.sequence"/>
        </th>
    </tr>
    <tr class="tableRowOdd">
        <td class="tableCell" align="left" valign="top">
            <html:select property="interactorType" name="seqForm" styleClass="inputRequired">
                <html:options name="polymermenu" />
            </html:select>
            <html:errors property="polymer"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:select property="organism" name="seqForm" styleClass="inputRequired">
                <html:options name="organismmenu" />
            </html:select>
            <html:errors property="organism"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:textarea property="sequence" name="seqForm" rows="3" cols="90"/>
            <html:errors property="sequence"/>
        </td>
    </tr>
</table>
