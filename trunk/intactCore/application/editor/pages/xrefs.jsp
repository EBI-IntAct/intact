<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Presents crossreference information for an Annotated object.
  --%>

<%@ page language="java"%>
<%@ page import="uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants,
                 uk.ac.ebi.intact.application.editor.struts.view.EditBean"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/struts-nested.tld" prefix="nested"%>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<%--<c:set var="viewbean" value="${user.view}"/>--%>
<c:set var="view" value="${user.view}"/>
<c:set var="dblist" value="${view.editDatabaseMenu}"/>
<c:set var="menus" value="${view.editXrefMenus}"/>
<%--<c:set var="dblist" value="${menus['DatabaseNames']}"/>--%>
<c:set var="qlist" value="${menus['QualifierNames']}"/>

<%-- Class wide declarations. --%>
<%!
    String formName = EditorConstants.FORM_XREF_EDIT;
    String viewState = EditBean.VIEW;
    String saveState = EditBean.SAVE;
%>

<h3>Crossreferences</h3>

<c:if test="${not empty view.xrefs}">

    <html:form action="/xref/edit">
        <table width="80%">
            <tr class="tableRowHeader">
                <th class="tableCellHeader" colspan="2">
                    <bean:message key="label.action"/>
                </th>
                <th class="tableCellHeader">
                    <bean:message key="xref.label.database"/>
                </th>
                <th class="tableCellHeader">
                    <bean:message key="xref.label.primary"/>
                </th>
                <th class="tableCellHeader">
                    <bean:message key="xref.label.secondary"/>
                </th>
                <th class="tableCellHeader">
                    <bean:message key="xref.label.release"/>
                </th>
                <th class="tableCellHeader">
                    <bean:message key="xref.label.reference"/>
                </th>
            </tr>
            <%-- To calculate row or even row --%>
            <c:set var="row"/>
            <nested:iterate name="<%=formName%>" property="items">
                <!-- Different styles for even or odd rows -->
                <c:choose>
                    <c:when test="${row % 2 == 0}">
                        <tr class="tableRowEven">
                    </c:when>
                    <c:otherwise>
                        <tr class="tableRowOdd">
                    </c:otherwise>
                </c:choose>
                <c:set var="row" value="${row + 1}"/>
                <%-- The following loop is under <tr> tag --%>

                    <%-- Buttons; Edit or Save depending on the bean state;
                         Delete is visible regardless of the state.
                     --%>
                    <td class="tableCell">
                        <nested:equal property="editState" value="<%=viewState%>">
                            <html:submit indexed="true" property="cmd"
                                titleKey="xrefs.button.edit.titleKey">
                                <bean:message key="button.edit"/>
                            </html:submit>
                        </nested:equal>

                        <nested:equal property="editState" value="<%=saveState%>">
                            <html:submit indexed="true" property="cmd"
                                titleKey="xrefs.button.save.titleKey">
                                <bean:message key="button.save"/>
                            </html:submit>
                        </nested:equal>
                    </td>

                    <td class="tableCell">
                        <html:submit indexed="true" property="cmd"
                                titleKey="xrefs.button.delete.titleKey">
                            <bean:message key="button.delete"/>
                        </html:submit>
                    </td>

                    <%-- In view mode --%>
                    <nested:equal property="editState" value="<%=viewState%>">
                        <td class="tableCell">
                            <nested:write property="databaseLink" filter="false"/>
                        </td>
                        <td class="tableCell">
                            <nested:write property="primaryId"/>
                        </td>
                        <td class="tableCell">
                            <nested:write property="secondaryId"/>
                        </td>
                        <td class="tableCell">
                            <nested:write property="releaseNumber"/>
                        </td>
                        <td class="tableCell">
                            <nested:write property="qualifierLink" filter="false"/>
                        </td>
                    </nested:equal>

                    <%-- In save mode --%>
                    <nested:equal property="editState" value="<%=saveState%>">
                        <td class="tableCell">
                            <nested:select property="database">
                                <nested:options name="dblist" />
                            </nested:select>
                        </td>
                        <td class="tableCell">
                            <nested:text size="15" property="primaryId"/>
                        </td>
                        <td class="tableCell">
                            <nested:text size="15" property="secondaryId"/>
                        </td>
                        <td class="tableCell">
                            <nested:text size="15" property="releaseNumber"/>
                        </td>
                        <td class="tableCell">
                            <nested:select property="qualifier">
                                <nested:options name="qlist" />
                            </nested:select>
                        </td>
                    </nested:equal>
                </tr>
            </nested:iterate>
        </table>
    </html:form>
</c:if>
