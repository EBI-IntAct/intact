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
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"%>

<html:form action="/info/edit">
    <table width="80%" border="0" cellspacing="1" cellpadding="2">
        <tr class="tableRowHeader">
            <th class="tableCellHeader">Action</th>
            <th class="tableCellHeader">AC</th>
            <th class="tableCellHeader">Short Label</th>
            <th class="tableCellHeader">Full Name</th>
        </tr>
        <tr class="tableRowEven">
            <td class="tableCell">
                <html:submit titleKey="cvinfo.buton.save.titleKey">
                    <bean:message key="button.save"/>
                </html:submit>
            </td>
            <td class="tableCell">
                <bean:write property="ac" name="cvinfoForm"/>
            </td>
            <td class="tableCell">
                <html:text property="shortLabel" name="cvinfoForm" size="10" maxlength="16"/>
<%--                <html:errors property="shortLabel"/>--%>
            </td>
            <td class="tableCell">
                <html:text property="fullName" name="cvinfoForm" size="80" maxlength="250"/>
<%--                <html:errors property="fullName"/>--%>
            </td>
        </tr>

        <%-- Prints all the error messages relevant to this page only. --%>
        <logic:messagesPresent>
            <tr class="tableRowOdd">
                <td class="tableErrorCell" colspan="4">
                    <%-- Filter out other error messages. --%>
                    <html:errors property="cvinfo"/>
                </td>
            </tr>
        </logic:messagesPresent>

    </table>
</html:form>
