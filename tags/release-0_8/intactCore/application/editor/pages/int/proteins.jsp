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

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<c:set var="view" value="${user.view}"/>

<%-- Menus to edit a Protein --%>
<c:set var="rolelist" value="${view.editProteinRoleMenu}"/>
<%-- Menu to add a new Protein --%>
<c:set var="rolelist_" value="${view.addProteinRoleMenu}"/>

<%-- BioSource menu lists --%>
<c:set var="biosrclist_" value="${view.addBioSourceMenu}"/>

<h3>Proteins</h3>

<c:if test="${not empty intForm.map.proteins}">

    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr class="tableRowHeader">
            <th class="tableCellHeader" width="2%" rowspan="2"></th>
            <th class="tableCellHeader" width="10%" rowspan="2">
                <bean:message key="label.action"/>
            </th>
            <th class="tableCellHeader" width="10%">
                <bean:message key="label.shortlabel"/>
            </th>
            <th class="tableCellHeader" width="10%">SP AC</th>
            <th class="tableCellHeader" width="10%">
                <bean:message key="label.ac"/>
            </th>
            <th class="tableCellHeader" width="10%" rowspan="2">Organism</th>
            <th class="tableCellHeader" width="50%" rowspan="2">
                <bean:message key="label.fullname"/>
            </th>
        </tr>
        <tr class="tableRowHeader">
            <th class="tableCellHeader">Role*</th>
            <th class="tableCellHeader">Stoichiometry</th>
            <th class="tableCellHeader">ExpressedIn</th>
        </tr>
        <%-- To calculate row or even row --%>
        <c:set var="row"/>
        <c:forEach var="proteins" items="${intForm.map.proteins}">
            <%-- Different styles for even or odd rows --%>
            <c:choose>
                <c:when test="${row % 2 == 0}">
                    <tr class="tableRowEven">
                </c:when>
                <c:otherwise>
                    <tr class="tableRowOdd">
                </c:otherwise>
            </c:choose>

            <c:if test="${proteins.editState == 'editing'}" var="edit"/>
            <c:if test="${proteins.editState != 'editing'}" var="notEdit"/>
            <c:if test="${proteins.editState == 'saving'}" var="save"/>
            <c:if test="${proteins.editState == 'saveNew'}" var="saveNew"/>
            <c:if test="${proteins.editState == 'error'}" var="error"/>

            <%-- Fill with appropriate color; simply sets the color for the
                 cell; no information is displayed yet.
            --%>
            <c:if test="${edit}">
                <td class="tableCell" rowspan="2"/>
            </c:if>

            <c:if test="${save}">
                <td class="editCell" rowspan="2"/>
            </c:if>

            <c:if test="${saveNew}">
                <td class="editCell" rowspan="2"/>
            </c:if>

            <c:if test="${error}">
                <td class="errorCell" rowspan="2"/>
            </c:if>

               <%-- Delete button: common to all --%>
                <td class="tableCell">
                    <html:submit indexed="true" property="protCmd"
                        titleKey="int.proteins.button.delete.titleKey">
                        <bean:message key="int.proteins.button.delete"/>
                    </html:submit>
                </td>

                <td class="tableCell">
                    <bean:write name="proteins" property="shortLabelLink" filter="false"/>
                </td>
                <td class="tableCell">
                    <bean:write name="proteins" property="spAc"/>
                </td>
                <td class="tableCell">
                    <bean:write name="proteins" property="ac"/>
                </td>
                <td class="tableCell" rowspan="2">
                    <bean:write name="proteins" property="organism"/>
                </td>
                <td class="tableCell" rowspan="2">
                    <bean:write name="proteins" property="fullName"/>
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

            <!-- Increment row by 1 -->
            <c:set var="row" value="${row + 1}"/>

                <%-- Buttons --%>
                <td class="tableCell">
                    <c:if test="${edit}">
                        <html:submit indexed="true" property="protCmd"
                            titleKey="int.proteins.button.edit.titleKey">
                            <bean:message key="int.proteins.button.edit"/>
                        </html:submit>
                    </c:if>

                    <c:if test="${notEdit}">
                        <html:submit indexed="true" property="protCmd"
                            titleKey="int.proteins.button.save.titleKey">
                            <bean:message key="int.proteins.button.save"/>
                        </html:submit>
                    </c:if>
                </td>

                <%-- Data --%>
                <c:if test="${edit}">
                    <td class="tableCell">
                        <bean:write name="proteins" property="role"/>
                    </td>
                    <td class="tableCell">
                        <bean:write name="proteins" property="stoichiometry"/>
                    </td>
                    <td class="tableCell">
                        <bean:write name="proteins" property="expressedIn"/>
                    </td>
                </c:if>

                <c:if test="${save}">
                    <td class="tableCell">
                        <html:select name="proteins" property="role" indexed="true"
                            styleClass="inputRequired">
                            <html:options name="rolelist" />
                        </html:select>
                    </td>
                    <td class="tableCell">
                        <html:text name="proteins" size="5" property="stoichiometry" indexed="true"/>
                    </td>
                    <td class="tableCell">
                        <html:select name="proteins" property="expressedIn" indexed="true">
                            <html:options name="biosrclist_" />
                        </html:select>
                    </td>
                </c:if>

                <c:if test="${saveNew}">
                    <td class="tableCell">
                        <html:select name="proteins" property="role" indexed="true"
                            styleClass="inputRequired">
                            <html:options name="rolelist_" />
                        </html:select>
                    </td>
                    <td class="tableCell">
                        <html:text name="proteins" size="5" property="stoichiometry" indexed="true"/>
                    </td>
                    <td class="tableCell">
                        <html:select name="proteins" property="expressedIn" indexed="true">
                            <html:options name="biosrclist_" />
                        </html:select>
                    </td>
                </c:if>

                <c:if test="${error}">
                    <td class="tableCell">
                        <html:select name="proteins" property="role" indexed="true"
                            styleClass="inputRequired">
                            <html:options name="rolelist_" />
                        </html:select>
                    </td>
                    <td class="tableCell">
                        <html:text name="proteins" size="5" property="stoichiometry" indexed="true"/>
                    </td>
                    <td class="tableCell">
                        <html:select name="proteins" property="expressedIn" indexed="true">
                            <html:options name="biosrclist_" />
                        </html:select>
                    </td>
                </c:if>
            </tr>
        </c:forEach>
    </table>
</c:if>