<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Presents Proteins information for an Interaction.
  --%>

<%@ page language="java"%>
<%@ page import="uk.ac.ebi.intact.application.editor.struts.view.EditBean,
                 uk.ac.ebi.intact.application.editor.struts.view.EditForm,
                 uk.ac.ebi.intact.application.editor.struts.viewx.interaction.InteractionViewBean,
                 uk.ac.ebi.intact.application.editor.struts.viewx.interaction.ProteinBean"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/struts-nested.tld" prefix="nested"%>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<c:set var="viewbean" value="${user.view}"/>

<%-- The menus needed to edit/add Proteins --%>
<c:set var="rolelist" value="${viewbean.editProteinRoleMenu}"/>
<c:set var="rolelist_" value="${viewbean.addProteinRoleMenu}"/>

<%-- Class wide declarations. --%>
<%!
    String formName = "proteinEditForm";
    String viewState = EditBean.VIEW;
    String saveState = EditBean.SAVE;
    String saveNewState = ProteinBean.SAVE_NEW;
%>

<%
    // Fill with form data for this page to display.
    EditForm form = (EditForm) session.getAttribute(formName);
    ((InteractionViewBean) user.getView()).fillProteins(form);
%>

<h3>Proteins</h3>

<c:if test="${not empty viewbean.proteins}">

    <html:form action="/interaction/protein/edit">
        <table width="100%" border="0" cellspacing="1" cellpadding="2">
            <tr class="tableRowHeader">
                <th class="tableCellHeader" width="10%" rowspan="2">Action</th>
                <th class="tableCellHeader" width="10%">Short Label</th>
                <th class="tableCellHeader" width="10%">SP AC</th>
                <th class="tableCellHeader" width="10%">IntAct AC</th>
                <th class="tableCellHeader" width="60%" rowspan="2">Full Name</th>
            </tr>
            <tr class="tableRowHeader">
                <th class="tableCellHeader">Role*</th>
                <th class="tableCellHeader">Stoichiometry</th>
                <th class="tableCellHeader">Organism</th>
            </tr>
            <%-- To calculate row or even row --%>
            <c:set var="row"/>
            <nested:iterate name="<%=formName%>" property="items">
                <%-- Different styles for even or odd rows --%>
                <c:choose>
                    <c:when test="${row % 2 == 0}">
                        <tr class="tableRowEven">
                    </c:when>
                    <c:otherwise>
                        <tr class="tableRowOdd">
                    </c:otherwise>
                </c:choose>

                    <td class="tableCell">
                        <html:submit indexed="true" property="cmd"
                            titleKey="annotations.button.delete.titleKey">
                            <bean:message key="button.delete"/>
                        </html:submit>
                    </td>

                    <td class="tableCell">
                        <nested:write property="shortLabel"/>
                    </td>
                    <td class="tableCell">
                        <nested:write property="spAc"/>
                    </td>
                    <td class="tableCell">
                        <nested:write property="ac"/>
                    </td>
                    <td class="tableCell" rowspan="2">
                        <nested:write property="fullName"/>
                    </td>
                </tr>
                    <%-- Buttons; Edit or Save depending on the bean state;
                         Delete is visible regardless of the state.
                     --%>
                <%-- Start of the second row --%>
                <c:choose>
                    <c:when test="${row % 2 == 0}">
                        <tr class="tableRowEven">
                    </c:when>
                    <c:otherwise>
                        <tr class="tableRowOdd">
                    </c:otherwise>
                </c:choose>
                <c:set var="row" value="${row + 1}"/>

                    <%-- Buttons --%>
                    <td class="tableCell">
                        <nested:equal property="editState" value="<%=viewState%>">
                            <html:submit indexed="true" property="cmd"
                                titleKey="annotations.button.edit.titleKey">
                                <bean:message key="button.edit"/>
                            </html:submit>
                        </nested:equal>

                        <nested:equal property="editState" value="<%=saveState%>">
                            <html:submit indexed="true" property="cmd"
                                titleKey="annotations.button.save.titleKey">
                                <bean:message key="button.save"/>
                            </html:submit>
                        </nested:equal>

                        <nested:equal property="editState" value="<%=saveNewState%>">
                            <html:submit indexed="true" property="cmd"
                                titleKey="annotations.button.save.titleKey">
                                <bean:message key="button.save"/>
                            </html:submit>
                        </nested:equal>
                    </td>

                    <%-- Data --%>
                    <nested:equal property="editState" value="<%=viewState%>">
                        <td class="tableCell">
                            <nested:write property="role"/>
                        </td>
                        <td class="tableCell">
                            <nested:write property="stoichiometry"/>
                        </td>
                    </nested:equal>

                    <nested:equal property="editState" value="<%=saveState%>">
                        <td class="tableCell">
                            <nested:select property="role">
                                <nested:options name="rolelist" />
                            </nested:select>
                        </td>
                        <td class="tableCell">
                            <nested:text size="5" property="stoichiometry"/>
                        </td>
                    </nested:equal>

                    <nested:equal property="editState" value="<%=saveNewState%>">
                        <td class="tableCell">
                            <nested:select property="role">
                                <nested:options name="rolelist_" />
                            </nested:select>
                            <html:errors property="protein.role"/>
                        </td>
                        <td class="tableCell">
                            <nested:text size="5" property="stoichiometry"/>
                        </td>
                    </nested:equal>

                    <td class="tableCell">
                        <nested:write property="organism"/>
                    </td>
                </tr>
            </nested:iterate>
        </table>
    </html:form>
</c:if>