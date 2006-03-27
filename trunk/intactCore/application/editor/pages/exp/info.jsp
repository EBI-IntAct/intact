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

<%-- The menus --%>
<c:set var="menus" value="${user.view.menus}"/>

<%-- Individual menu lists --%>
<c:set var="organismmenu" value="${menus['Organism']}"/>
<c:set var="intermenu" value="${menus['Interaction']}"/>
<c:set var="identmenu" value="${menus['Identification']}"/>

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
    <tr class="tableRowEven">
        <td class="tableCell" colspan="2">
            <bean:message key="label.creation"/> <bean:write property="created" name="expForm" /> by <bean:write property="creator" name="expForm" />.
        </td>
        <td class="tableCell" colspan="2">
            <bean:message key="label.update"/> <bean:write property="updated" name="expForm" /> by <bean:write property="updator" name="expForm" />.
        </td>
    </tr>
</table>
<html:errors property="shortLabel"/>
<%----%>
<%--<html:messages id="message" message="true">--%>
<%--	<span class="warning">--%>
<%--		<bean:write name="message" filter="false"/>--%>
<%--	</span>--%>
<%--</html:messages>--%>

<p></p>

<table width="100%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableLinkRowHeader">
        <th width="20%">
            <a href="javascript:showColumnLink('BioSource',
                document.forms['expForm'].elements['organism'].value)">
                Host Organism
            </a>
        </th>

        <th>
            <a href="javascript:showColumnLink('CvInteraction',
                document.forms['expForm'].elements['inter'].value)">
                Interaction Detection
            </a>
        </th>

        <th>
            <a href="javascript:showColumnLink('CvIdentification',
                document.forms['expForm'].elements['ident'].value)">
                Participant Detection
            </a>
        </th>

    </tr>

    <tr class="tableRowHeader">
        <td class="tableCell" align="left" valign="top">
            <html:select property="organism" name="expForm" styleClass="inputRequired">
                <html:options name="organismmenu"/>
            </html:select>
            <html:errors property="organism"/>
        </td>

        <td class="tableCell" align="left" valign="top">
            <html:select property="inter" name="expForm" styleClass="inputRequired">
                <html:options name="intermenu"/>
            </html:select>
            <html:errors property="inter"/>
        </td>

        <td class="tableCell" align="left" valign="top">
            <html:select property="ident" name="expForm" styleClass="inputRequired">
                <html:options name="identmenu"/>
            </html:select>
            <html:errors property="ident"/>
        </td>
    </tr>
</table>
