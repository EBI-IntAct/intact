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
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"%>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<%-- Set the drop down lists --%>
<c:set var="menus" value="${user.view.editorMenus}"/>

<%-- Individual menu lists --%>
<c:set var="organismlist" value="${menus['Organisms']}"/>
<c:set var="interactiontypelist" value="${menus['InteractionTypes']}"/>

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

        <%-- Only display validation error messages relevant to this page. --%>
        <logic:messagesPresent property="int.validation">
            <tr class="tableRowOdd">
                <td class="tableErrorCell" colspan="4">
                    <html:errors/>
                </td>
            </tr>
        </logic:messagesPresent>

        <tr class="tableRowHeader">
            <th class="tableCellHeader">Action</th>
            <th class="tableCellHeader">kD</th>
            <th class="tableCellHeader">
                <a href="javascript:showInteraction('CvInteractionType', 2)">
                    Interaction Type
                </a>
            </th>
            <th class="tableCellHeader">
                <a href="javascript:showInteraction('BioSource', 3)">
                    Organism
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
                    <html:options name="interactiontypelist" />
                </html:select>
                <html:errors property="int.interaction"/>
            </td>

            <td class="tableCell" align="left" valign="top">
                <html:select property="organism" name="<%=formName%>">
                    <html:options name="organismlist" />
                </html:select>
                <html:errors property="int.organism"/>
            </td>
        </tr>
    </table>
</html:form>
