<%@ page import="org.apache.commons.beanutils.DynaBean,
                 uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants"%>
<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - This page accepts inputs for tax id.
  --%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

    <%
        // Fill with form data for this page to display.
        String formName = EditorConstants.FORM_BIOSOURCE;
        DynaBean form = user.createForm(formName, request);
        user.getView().populateEditorSpecificInfo(form);
        pageContext.setAttribute(formName, form);
    %>

<html:form action="/biosource/taxid">
    <table width="40%" border="0" cellspacing="1" cellpadding="2">
        <tr class="tableRowHeader">
            <th class="tableCellHeader">Action</th>
            <th class="tableCellHeader">NCBI Tax Id</th>
        </tr>
        <tr class="tableRowEven">
            <td class="tableCell">
                <html:submit titleKey="biosource.taxid.button.titleKey">
                    <bean:message key="biosource.taxid.button"/>
                </html:submit>
            </td>
            <td class="tableCell">
                <html:text property="taxId"  size="10" maxlength="16"
                    name="<%=EditorConstants.FORM_BIOSOURCE%>"/>
            </td>
        </tr>
    </table>
</html:form>
