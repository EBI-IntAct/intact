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
<%@ taglib uri="/WEB-INF/tld/intact.tld" prefix="intact"%>

<%-- The current view --%>
<c:set var="view" value="${user.view}"/>

<%-- Individual menu lists --%>
<c:set var="organismmenu" value="${view.organismMenu}"/>
<c:set var="intermenu" value="${view.interMenu}"/>
<c:set var="identmenu" value="${view.identMenu}"/>

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
            <bean:write property="ac" name="expForm" filter="false"/>
        </td>
        <td class="tableCell">
            <html:text property="shortLabel" size="20" maxlength="20" name="expForm"
                styleClass="inputRequired"/>
        </td>

        <td class="tableCell">
            <html:text property="fullName" size="100" maxlength="250" name="expForm"/>
        </td>
    </tr>
</table>

<p></p>

<table width="100%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableRowHeader">
        <th class="tableCellHeader" width="20%">Host Organism</th>
        <th class="tableCellHeader">Interaction Detection</th>
        <th class="tableCellHeader">Participant Detection</th>
    </tr>
    <tr class="tableRowEven">
        <td class="tableCell" align="left" valign="top">
            <html:select property="organism" name="expForm" styleClass="inputRequired">
                <html:options name="organismmenu"/>
            </html:select>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:select property="inter" name="expForm" styleClass="inputRequired">
                <html:options name="intermenu"/>
            </html:select>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:select property="ident" name="expForm" styleClass="inputRequired">
                <html:options name="identmenu"/>
            </html:select>
        </td>
    </tr>
</table>
