<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Interaction editor.
  --%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/intact.tld" prefix="intact"%>

<%-- The menus --%>
<c:set var="menus" value="${user.view.menus}"/>

<%-- Individual menu lists --%>
<c:set var="organismmenu" value="${menus['Organism']}"/>
<c:set var="intertypemenu" value="${menus['InteractionType']}"/>

<%-- The anchor name for this page --%>
<a name="info"/>

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
            <bean:write property="ac" name="intForm" filter="false"/>
        </td>

        <td class="tableCell">
            <html:text property="shortLabel" size="20" maxlength="20"
                name="intForm" styleClass="inputRequired"/>
        </td>

        <td class="tableCell">
            <html:text property="fullName" size="100" maxlength="250" name="intForm"/>
        </td>
    </tr>
</table>
<html:errors property="shortLabel"/>

<p></p>

<table width="50%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableLinkRowHeader">
        <th>Kd</th>
        <th>
            <a href="javascript:showColumnLink('CvInteractionType',
                document.forms['intForm'].elements['interactionType'].value)"/>
                <bean:message key="int.label.cvtype"/>
            </a>
        </th>
        <th>
            <a href="javascript:showColumnLink('BioSource',
                document.forms['intForm'].elements['organism'].value)">
                <bean:message key="int.label.biosrc"/>
            </a>
        </th>
    </tr>

    <tr class="tableRowHeader">
        <td class="tableCell">
            <html:text property="kd" name="intForm" size="5" maxlength="16"/>
        </td>

        <td class="tableCell" align="left" valign="top">
            <html:select property="interactionType" name="intForm" styleClass="inputRequired">
                <html:options name="intertypemenu" />
            </html:select>
            <html:errors property="interactionType"/>
        </td>

        <td class="tableCell" align="left" valign="top">
            <html:select property="organism" name="intForm">
                <html:options name="organismmenu" />
            </html:select>
            <html:errors property="organism"/>
        </td>
    </tr>
</table>
