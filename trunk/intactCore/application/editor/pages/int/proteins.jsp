<%@ page import="org.apache.struts.util.PropertyMessageResources,
                 org.apache.struts.Globals"%>
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
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tld/struts-nested.tld" prefix="nested"%>

<c:set var="view" value="${user.view}"/>

<%-- Menus to edit a Protein --%>
<c:set var="rolelist" value="${view.editProteinRoleMenu}"/>
<%-- Menu to add a new Protein --%>
<c:set var="rolelist_" value="${view.addProteinRoleMenu}"/>

<%-- BioSource menu lists --%>
<c:set var="biosrclist_" value="${view.addBioSourceMenu}"/>

<%
    PropertyMessageResources msgres = (PropertyMessageResources)
            getServletConfig().getServletContext().getAttribute(Globals.MESSAGES_KEY);
%>

<%-- Need this to set the dispatch feature flag --%>
<html:hidden property="dispatchFeature" />

<script language="JavaScript" type="text/javascript">
    // Set the hidden feature dispatch field when the user clicks on Edit/Delete Feature.
    function setFeatureDispatch(label) {
        if (label == 'edit') {
            //window.alert('I am here');
            document.forms[0].dispatchFeature.value='<%=msgres.getMessage(
                    "int.proteins.button.feature.edit")%>';
        }
        else {
            document.forms[0].dispatchFeature.value='<%=msgres.getMessage(
                    "int.proteins.button.feature.delete")%>';
        }
        //window.alert(document.forms[0].dispatchFeature.value);
    }
</script>

<h3>Proteins</h3>

<c:if test="${not empty intForm.components}">

    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr class="tableRowHeader">
            <th class="tableCellHeader" width="2%" rowspan="2"></th>
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

<%--        <nested:nest property="proteins">--%>
        <%-- To calculate row or even row --%>
        <c:set var="row"/>
<%--        <c:forEach var="proteins" items="${intForm.proteins}">--%>
        <nested:iterate name="intForm" property="components">
            <%-- Different styles for even or odd rows --%>
            <c:choose>
                <c:when test="${row % 2 == 0}">
                    <tr class="tableRowEven">
                </c:when>
                <c:otherwise>
                    <tr class="tableRowOdd">
                </c:otherwise>
            </c:choose>

            <c:if test="${components.editState == 'editing'}" var="edit"/>
            <c:if test="${components.editState != 'editing'}" var="notEdit"/>
            <c:if test="${components.editState == 'saving'}" var="save"/>
            <c:if test="${components.editState == 'saveNew'}" var="saveNew"/>
            <c:if test="${components.editState == 'error'}" var="error"/>

            <%-- Fill with appropriate color; simply sets the color for the
                 cell; no information is displayed yet.
            --%>
            <c:if test="${edit}">
                <td class="tableCell" rowspan="3"/>
            </c:if>

            <c:if test="${save}">
                <td class="editCell" rowspan="3"/>
            </c:if>

            <c:if test="${saveNew}">
                <td class="editCell" rowspan="3"/>
            </c:if>

            <c:if test="${error}">
                <td class="errorCell" rowspan="3"/>
            </c:if>

            <%-- The empty cell for check box --%>
            <td class="tableCell" rowspan="3"/>

               <%-- Delete button: common to all --%>
                <td class="tableCell">
                    <html:submit indexed="true" property="protCmd"
                        titleKey="int.proteins.button.delete.titleKey">
                        <bean:message key="int.proteins.button.delete"/>
                    </html:submit>
                </td>

                <td class="tableCell">
                    <bean:write name="components" property="shortLabelLink" filter="false"/>
                </td>
                <td class="tableCell">
                    <bean:write name="components" property="spAc"/>
                </td>
                <td class="tableCell">
                    <bean:write name="components" property="ac"/>
                </td>
                <td class="tableCell" rowspan="2">
                    <bean:write name="components" property="organism"/>
                </td>
                <td class="tableCell" rowspan="2">
                    <bean:write name="components" property="fullName"/>
                </td>
            </tr>

            <%-- ==============================================================
                 Buttons; Edit or Save depending on the bean state;
                 Delete is visible regardless of the state.
                 Start of the second row
                 ==============================================================
            --%>
            <c:choose>
                <c:when test="${row % 2 == 0}">
                    <tr class="tableRowEven">
                </c:when>
                <c:otherwise>
                    <tr class="tableRowOdd">
                </c:otherwise>
            </c:choose>

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
                        <bean:write name="components" property="role"/>
                    </td>
                    <td class="tableCell">
                        <bean:write name="components" property="stoichiometry"/>
                    </td>
                    <td class="tableCell">
                        <bean:write name="components" property="expressedIn"/>
                    </td>
                </c:if>

                <c:if test="${save}">
                    <td class="tableCell">
                        <html:select name="components" property="role" indexed="true"
                            styleClass="inputRequired">
                            <html:options name="rolelist" />
                        </html:select>
                    </td>
                    <td class="tableCell">
                        <html:text name="components" size="5" property="stoichiometry" indexed="true"/>
                    </td>
                    <td class="tableCell">
                        <html:select name="components" property="expressedIn" indexed="true">
                            <html:options name="biosrclist_" />
                        </html:select>
                    </td>
                </c:if>

                <c:if test="${saveNew}">
                    <td class="tableCell">
                        <html:select name="components" property="role" indexed="true"
                            styleClass="inputRequired">
                            <html:options name="rolelist_" />
                        </html:select>
                    </td>
                    <td class="tableCell">
                        <html:text name="components" size="5" property="stoichiometry" indexed="true"/>
                    </td>
                    <td class="tableCell">
                        <html:select name="components" property="expressedIn" indexed="true">
                            <html:options name="biosrclist_" />
                        </html:select>
                    </td>
                </c:if>

                <c:if test="${error}">
                    <td class="tableCell">
                        <html:select name="components" property="role" indexed="true"
                            styleClass="inputRequired">
                            <html:options name="rolelist_" />
                        </html:select>
                    </td>

                    <%-- Stoichiometry --%>
                    <td class="tableCell">
                        <html:text name="components" size="5" property="stoichiometry" indexed="true"/>
                    </td>

                    <%-- Expressed In --%>
                    <td class="tableCell">
                        <html:select name="components" property="expressedIn" indexed="true">
                            <html:options name="biosrclist_" />
                        </html:select>
                    </td>
                </c:if>
            </tr>

            <%-- ==============================================================
                 Start of the third row
                 ==============================================================
            --%>
            <c:choose>
                <c:when test="${row % 2 == 0}">
                    <tr class="tableRowEven">
                </c:when>
                <c:otherwise>
                    <tr class="tableRowOdd">
                </c:otherwise>
            </c:choose>
               <%-- Add feature button: common to all --%>
                <td class="tableCell">
                    <html:submit indexed="true" property="protCmd"
                        titleKey="int.proteins.button.feature.add.titleKey">
                        <bean:message key="int.proteins.button.feature.add"/>
                    </html:submit>
                </td>

                <%-- Empty cell spanning many cells --%>
                <td class="tableCell"  colspan="5"/>
                </tr>

            <%-- Loop though ranges for each component --%>
            <nested:iterate property="features">
                <!-- Increment row by 1 -->
                <c:set var="row" value="${row + 1}"/>

                <%-- ==============================================================
                     Start of the fourth row. Prints Ranges for each component.
                     ==============================================================
                --%>
                <c:choose>
                    <c:when test="${row % 2 == 0}">
                        <tr class="tableRowEven">
                    </c:when>
                    <c:otherwise>
                        <tr class="tableRowOdd">
                    </c:otherwise>
                </c:choose>

                <%-- Empty cells for error/link boxes --%>
                <td class="tableCell" rowspan="2"/>
                <td class="tableCell" rowspan="2">
                    <nested:checkbox property="linked"/>
                </td>

                   <%-- Edit feature button --%>
                    <td class="tableCell" rowspan="2">
                        <%-- Feature edit button --%>
                        <nested:submit property="featureCmd" onclick="setFeatureDispatch('edit');"
                            titleKey="int.proteins.button.feature.edit.titleKey">
                            <bean:message key="int.proteins.button.feature.edit"/>
                        </nested:submit>

                        <%-- Feature delete button --%>
                        <nested:submit property="featureCmd"  onclick="setFeatureDispatch('delete');"
                            titleKey="int.proteins.button.feature.delete.titleKey">
                            <bean:message key="int.proteins.button.feature.delete"/>
                        </nested:submit>
                    </td>
                    <td class="tableCell">
                        <c:out value="${features.shortLabel}"/>
                    </td>
                    <td class="tableCell">
                        <c:out value="${features.ac}"/>
                    </td>
                    <td class="tableCell" rowspan="2">
                        <c:out value="${features.ranges}"/>
                    </td>
                    <td class="tableCell" rowspan="2">
                        <c:out value="${features.boundDomain}"/>
                    </td>
                    <td class="tableCell" rowspan="2">
                        <c:out value="${features.fullName}"/>
                    </td>
                    </tr>

                    <%-- ==============================================================
                         Start of the fifth row. This row prints feature type and detection
                         ==============================================================
                    --%>
                    <c:choose>
                        <c:when test="${row % 2 == 0}">
                            <tr class="tableRowEven">
                        </c:when>
                        <c:otherwise>
                            <tr class="tableRowOdd">
                        </c:otherwise>
                    </c:choose>

                            <td class="tableCell">
                                <c:out value="${features.type}"/>
                            </td>
                            <td class="tableCell">
                                <c:out value="${features.detection}"/>
                            </td>
                        </tr>
                </nested:iterate>
            <!-- Increment row by 1 -->
            <c:set var="row" value="${row + 1}"/>
            </nested:iterate>
    </table>
</c:if>
<html:errors property="int.prot.role"/>
<html:errors property="int.unsaved.prot"/>

