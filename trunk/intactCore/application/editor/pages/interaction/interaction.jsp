<%@ page import="uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory"%>
<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - This page accepts inputs for an interaction.
  --%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<%-- The current view --%>
<c:set var="view" value="${user.view}"/>

<%-- Individual menu lists --%>
<c:set var="organismmenu" value="${view.organismMenu}"/>
<c:set var="intertypemenu" value="${view.interactionTypeMenu}"/>

<%-- Class wide declarations. --%>
<%!
    String formName = "interactionForm";
%>

<script language="JavaScript" type="text/javascript">
    function showInteraction(type, n) {
        var v = document.interactionForm.elements[n].value;
        if (v == "<%= EditorMenuFactory.SELECT_LIST_ITEM%>") {
            alert("Please select an item from the list first!");
            return;
        }
        show(type, v);
    }
</script>

<html:form action="/interaction/info">
    <table width="50%" border="0" cellspacing="1" cellpadding="2">

        <tr class="tableRowHeader">
            <th class="tableCellHeader">Action</th>
            <th class="tableCellHeader">kD</th>
            <th class="tableCellHeader">
                <a href="javascript:showInteraction('CvInteractionType', 2)">
                    <bean:message key="int.label.cvtype"/>
                </a>
            </th>
            <th class="tableCellHeader">
                <a href="javascript:showInteraction('BioSource', 3)">
                    <bean:message key="int.label.biosrc"/>
                </a>
            </th>
        </tr>

        <tr class="tableRowEven">
            <td class="tableCell">
                <html:submit titleKey="button.save.titleKey">
                    <bean:message key="button.save"/>
                </html:submit>
            </td>

            <td class="tableCell">
                <html:text property="kD" name="<%=formName%>" size="5"
                    maxlength="16"/>
            </td>

            <td class="tableCell" align="left" valign="top">
                <html:select property="interactionType" name="<%=formName%>">
                    <html:options name="intertypemenu" />
                </html:select>
                <html:errors property="int.interaction"/>
            </td>

            <td class="tableCell" align="left" valign="top">
                <html:select property="organism" name="<%=formName%>">
                    <html:options name="organismmenu" />
                </html:select>
                <html:errors property="int.organism"/>
            </td>
        </tr>

    </table>
</html:form>
