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
                 uk.ac.ebi.intact.application.editor.struts.view.EditBean,
                 uk.ac.ebi.intact.application.editor.struts.view.EditForm"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/struts-nested.tld" prefix="nested"%>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<c:set var="viewbean" value="${user.view}"/>
<c:set var="dblist" value="${user.databaseList}"/>
<c:set var="qlist" value="${user.qualifierList}"/>

    <%
        // Fill with form data for this page to display.
        String formName = EditorConstants.FORM_XREF_EDIT;
        EditForm form = (EditForm) session.getAttribute(formName);
        user.getView().populateXrefs(form);
        pageContext.setAttribute(formName, form);
    %>

<h3>Crossreferences</h3>

<c:if test="${not empty viewbean.xrefs}">

    <html:form action="/xref/edit">
        <table width="80%">
            <tr class="tableRowHeader">
                <th class="tableCellHeader" colspan="2">Action</th>
                <th class="tableCellHeader">Database</th>
                <th class="tableCellHeader">Primary ID</th>
                <th class="tableCellHeader">Secondary ID</th>
                <th class="tableCellHeader">Release Number</th>
                <th class="tableCellHeader">Reference Qualifier</th>
            </tr>
            <%-- To calculate row or even row --%>
            <c:set var="row"/>
            <nested:iterate name="<%=EditorConstants.FORM_XREF_EDIT%>" property="items">
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
                        <nested:equal name="<%=EditorConstants.FORM_XREF_EDIT%>"
                            property="editState" value="<%=EditBean.VIEW%>">
                            <html:submit indexed="true" property="cmd"
                                titleKey="xrefs.button.edit.titleKey">
                                <bean:message key="button.edit"/>
                            </html:submit>
                        </nested:equal>

                        <nested:equal name="<%=EditorConstants.FORM_XREF_EDIT%>"
                            property="editState" value="<%=EditBean.SAVE%>">
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
                    <nested:equal name="<%=EditorConstants.FORM_XREF_EDIT%>"
                        property="editState" value="<%=EditBean.VIEW%>">
                        <td class="tableCell">
                            <nested:write property="database"/>
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
                            <nested:write property="qualifier"/>
                        </td>
                    </nested:equal>

                    <%-- In save mode --%>
                    <nested:equal name="<%=EditorConstants.FORM_XREF_EDIT%>"
                        property="editState" value="<%=EditBean.SAVE%>">
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
