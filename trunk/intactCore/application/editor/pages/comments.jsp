<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Presents comments (annotations) information for an Annotated object.
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

<%-- The list of topics --%>
<c:set var="viewbean" value="${user.view}"/>
<c:set var="topiclist" value="${viewbean.editTopicMenu}"/>

<%-- Class wide declarations. --%>
<%!
    String formName = EditorConstants.FORM_COMMENT_EDIT;
    String viewState = EditBean.VIEW;
    String saveState = EditBean.SAVE;
%>

<h3>Annotations</h3>

<c:if test="${not empty viewbean.annotations}">

    <html:form action="/comment/edit">
        <table width="80%" border="0" cellspacing="1" cellpadding="2">
            <tr class="tableRowHeader">
                <th class="tableCellHeader" colspan="2">
                    <bean:message key="label.action"/>
                </th>
                <th class="tableCellHeader">
                    <bean:message key="annotation.label.topic"/>
                </th>
                <th class="tableCellHeader">
                    <bean:message key="annotation.label.desc"/>
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

                    <%-- Buttons; Edit or Save depending on the bean state;
                         Delete is visible regardless of the state.
                     --%>
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
                    </td>

                    <td class="tableCell">
                        <html:submit indexed="true" property="cmd"
                            titleKey="annotations.button.delete.titleKey">
                            <bean:message key="button.delete"/>
                        </html:submit>
                    </td>

                    <nested:equal property="editState" value="<%=viewState%>">
                        <td class="tableCell">
                            <nested:write property="topicLink" filter="false"/>
                        </td>
                        <td class="tableCell">
                            <nested:write property="description"/>
                        </td>
                    </nested:equal>

                    <nested:equal property="editState" value="<%=saveState%>">
                        <td class="tableCell">
                            <nested:select property="topic">
                                <nested:options name="topiclist" />
                            </nested:select>
                        </td>
                        <td class="tableCell">
                            <nested:textarea cols="70" rows="3" property="description"/>
                        </td>
                    </nested:equal>
                </tr>
            </nested:iterate>
        </table>
    </html:form>
</c:if>