<%@ page import="uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants,
                 org.apache.commons.beanutils.DynaBean"
%>
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

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

    <%
        // Fill with form data for this page to display.
        String formName = EditorConstants.FORM_INFO;
        DynaBean dynaBean = user.createForm(formName, request);
        user.getView().populate(dynaBean);
        pageContext.setAttribute(formName, dynaBean);
    %>

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
                <bean:write property="ac" name="<%=EditorConstants.FORM_INFO%>"/>
            </td>
            <td class="tableCell">
                <html:text property="shortLabel"  size="10" maxlength="16"
                    name="<%=EditorConstants.FORM_INFO%>"/>
<%--                <html:errors property="shortLabel"/>--%>
            </td>
            <td class="tableCell">
                <html:text property="fullName"  size="80" maxlength="250"
                    name="<%=EditorConstants.FORM_INFO%>"/>
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
