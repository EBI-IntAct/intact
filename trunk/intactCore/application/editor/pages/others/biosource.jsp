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
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"%>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<%-- Class wide declarations. --%>
<%!
    String formName = "bioSourceForm";
%>

<%-- Fill the form before the display --%>
<%
//    DynaBean form = user.getDynaBean(formName, request);
//    user.getView().fillEditorSpecificInfo(form);
//    request.setAttribute(formName, form);
%>

<html:form action="/biosource/info">
    <table width="50%" border="0" cellspacing="1" cellpadding="2">
        <tr class="tableRowHeader">
            <th class="tableCellHeader" width="30%">Action</th>
            <th class="tableCellHeader">NCBI Tax Id</th>
        </tr>
        <tr class="tableRowEven">
            <td class="tableCell">
                <html:submit titleKey="biosource.taxid.button.titleKey">
                    <bean:message key="biosource.taxid.button"/>
                </html:submit>
            </td>
            <td class="tableCell">
                <html:text property="taxId" name="<%=formName%>"
                    size="10" maxlength="16"/>
            </td>
        </tr>

        <%-- Prints all the error messages relevant to this page only. --%>
        <logic:messagesPresent>
            <tr class="tableRowOdd">
                <td class="tableErrorCell" colspan="2">
                    <%-- Filter out other error messages. --%>
                    <html:errors property="biosource"/>
                </td>
            </tr>
        </logic:messagesPresent>

    </table>
</html:form>
