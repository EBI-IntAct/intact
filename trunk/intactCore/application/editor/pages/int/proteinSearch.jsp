<%@ page import="org.apache.commons.beanutils.DynaBean"%>
<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - The page to searh for Proteins.
  --%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/intact.tld" prefix="intact"%>

<table width="70%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableRowHeader">
        <th class="tableCellHeader" width="10%">Action</th>
        <th class="tableCellHeader" width="15%">Short Label</th>
        <th class="tableCellHeader" width="15%">SP AC</th>
        <th class="tableCellHeader" width="15%">IntAct AC</th>
        <th class="tableCellHeader" width="2%">
            <intact:documentation section="editor.int.proteins"/>
        </th>
    </tr>
    <tr class="tableRowEven">
        <td class="tableCell">
            <html:submit titleKey="int.proteins.button.search.titleKey"
                property="dispatch">
                <bean:message key="int.proteins.button.search"/>
            </html:submit>
        </td>
        <td class="tableCell">
            <html:text property="protSearchLabel" size="20" maxlength="20"/>
        </td>
        <td class="tableCell">
            <html:text property="protSearchSpAC" size="20" maxlength="20"/>
        </td>
        <td class="tableCell">
            <html:text property="protSearchAC" size="20" maxlength="20"/>
        </td>
    </tr>
</table>
