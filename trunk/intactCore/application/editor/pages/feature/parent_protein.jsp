<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
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

<h3>Parent protein</h3>

<table width="100%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableRowHeader">
        <th class="tableCellHeader">
            <bean:message key="feature.parent.label.ac"/>
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
            <bean:write property="parentAc" name="featureForm" filter="false"/>
        </td>
        <td class="tableCell">
            <bean:write property="parentShortLabel"name="featureForm"/>
        </td>

        <td class="tableCell">
            <bean:write property="parentFullName" name="featureForm"/>
        </td>
    </tr>
</table>
