<%@ page import="org.apache.commons.beanutils.DynaBean"%>
<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - This page accepts inputs for the bio source editor.
  --%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%-- Class wide declarations. --%>
<%!
    String formName = "bioSourceForm";
%>

<html:form action="/biosource/info">
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
                <html:submit titleKey="biosource.button.taxid.titleKey">
                    <bean:message key="biosource.button.taxid"/>
                </html:submit>
            </td>
            <td class="tableCell">
                <html:text property="taxId" name="<%=formName%>"
                    size="10" maxlength="16"/>
            </td>
        </tr>
    </table>
</html:form>
