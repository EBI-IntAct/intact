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

<%-- The menus --%>
<c:set var="menus" value="${user.view.menus}"/>

<%-- Menus to edit a Protein --%>
<c:set var="rolelist" value="${menus['Role']}"/>
<%-- Menu to add a new Protein --%>
<c:set var="rolelist_" value="${menus['Role_']}"/>
<%-- BioSource menu --%>
<c:set var="biosrclist_" value="${menus['Organism']}"/>

<%
    PropertyMessageResources msgres = (PropertyMessageResources)
            getServletConfig().getServletContext().getAttribute(Globals.MESSAGES_KEY);
%>

<%-- Initialize the dispatch protein flag --%>
<html:hidden property="dispatchProtein" value="error" />

<%-- Initialize the dispatch feature flag --%>
<html:hidden property="dispatchFeature" value="error" />

<script language="JavaScript" type="text/javascript">
    // Set the hidden protein dispatch field when the user clicks on
    // Edit/Save/Delete Protein.
    function setProteinDispatch(label) {
        if (label == 'edit') {
            document.forms[0].dispatchProtein.value='<%=msgres.getMessage(
                    "int.interactors.button.edit")%>';
        }
        else if (label == 'save') {
            document.forms[0].dispatchProtein.value='<%=msgres.getMessage(
                    "int.interactors.button.save")%>';
        }
        else {
            document.forms[0].dispatchProtein.value='<%=msgres.getMessage(
                    "int.interactors.button.delete")%>';
        }
        //window.alert(document.forms[0].dispatchProtein.value);
    }

    // ------------------------------------------------------------------------

    // Set the hidden feature dispatch field when the user clicks on
    // Add/Edit/Delete Feature.
    function setFeatureDispatch(label) {
        //window.alert(label)
        if (label == 'edit') {
            document.forms[0].dispatchFeature.value='<%=msgres.getMessage(
                    "int.proteins.button.feature.edit")%>';
        }
        else if (label == 'add') {
            document.forms[0].dispatchFeature.value='<%=msgres.getMessage(
                    "int.proteins.button.feature.add")%>';
        }
        else {
            document.forms[0].dispatchFeature.value='<%=msgres.getMessage(
                    "int.proteins.button.feature.delete")%>';
        }
        //window.alert(document.forms[0].dispatchFeature.value);
    }
</script>

<h3>Interactors</h3>

<c:if test="${not empty intForm.components}">

    <table width="100%" border="0" cellspacing="1" cellpadding="2" id="proteins">
        <%-- Protein headings --%>
        <tr class="tableRowHeader">
            <th class="tableCellHeader" width="2%" rowspan="2"></th>
            <th class="tableCellHeader" width="2%" rowspan="2"></th>
            <th class="tableCellHeader" width="10%" rowspan="2">Interactors</th>
            <th class="tableCellHeader" width="10%">
                <bean:message key="label.shortlabel"/>
            </th>
            <th class="tableCellHeader" width="10%">SP AC</th>
            <th class="tableCellHeader" width="10%">
                <bean:message key="label.ac"/>
            </th>
            <th class="tableCellHeader" width="10%">Gene Name</th>
            <th class="tableCellHeader" width="50%">
                <bean:message key="label.fullname"/>
            </th>
        </tr>
        <tr class="tableRowHeader">
            <th class="tableCellHeader">Role*</th>
            <th class="tableCellHeader">Stoichiometry</th>
            <th class="tableCellHeader">ExpressedIn</th>
            <th class="tableCellHeader">Organism</th>
            <th class="tableCellHeader">Interactor Type</th>
        </tr>

        <%-- Feature headings --%>
        <tr class="tableFeatureRowHeader">
            <th class="tableCellHeader" width="2%" rowspan="2"></th>
            <th class="tableCellHeader" width="2%" rowspan="2"></th>
            <th class="tableCellHeader" width="10%" rowspan="2">Features</th>
            <th class="tableCellHeader" width="10%">Feature Short Label</th>
            <th class="tableCellHeader" width="10%">Feature AC</th>
            <th class="tableCellHeader" width="10%" rowspan="2">Range</th>
            <th class="tableCellHeader" width="10%" rowspan="2">Interacts With</th>
            <th class="tableCellHeader" width="50%" rowspan="2">Feature Full Name</th>
        </tr>
        <tr class="tableFeatureRowHeader">
            <th class="tableCellHeader">Feature Type</th>
            <th class="tableCellHeader">Feature Detection</th>
        </tr>

        <%-- To calculate row or even row --%>
        <c:set var="row"/>
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
                        onclick="setProteinDispatch('delete');"
                        titleKey="int.proteins.button.delete.titleKey">
                        <bean:message key="int.interactors.button.delete"/>
                    </html:submit>
                </td>

                <%-- View Data --%>
                <td class="tableCell">
                    <nested:write property="shortLabelLink" filter="false"/>
                </td>
                <td class="tableCell">
                    <nested:write property="spAc"/>
                </td>
                <td class="tableCell">
                    <nested:write property="interactorAc"/> 
                </td>
                <td class="tableCell">
                    <nested:write property="geneName"/>
                </td>
                <td class="tableCell">
                    <nested:write property="fullName"/>
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
                            onclick="setProteinDispatch('edit');"
                            titleKey="int.proteins.button.edit.titleKey">
                            <bean:message key="int.interactors.button.edit"/>
                        </html:submit>
                    </c:if>

                    <c:if test="${notEdit}">
                        <html:submit indexed="true" property="protCmd"
                            onclick="setProteinDispatch('save');"
                            titleKey="int.proteins.button.save.titleKey">
                            <bean:message key="int.interactors.button.save"/>
                        </html:submit>
                    </c:if>
                </td>

                <%-- View Data --%>
                <c:if test="${edit}">
                    <td class="tableCell">
                        <nested:write property="role"/>
                    </td>
                    <td class="tableCell">
                        <nested:write property="stoichiometry"/>
                    </td>
                    <td class="tableCell">
                        <nested:write property="expressedIn"/>
                    </td>
                </c:if>

                <c:if test="${save}">
                    <td class="tableCell">
                        <nested:select property="role" styleClass="inputRequired">
                            <html:options name="rolelist" />
                        </nested:select>
                    </td>
                </c:if>

                <c:if test="${saveNew or error}">
                    <td class="tableCell">
                        <nested:select property="role" styleClass="inputRequired">
                            <html:options name="rolelist_" />
                        </nested:select>
                    </td>
                </c:if>

                <c:if test="${save or saveNew or error}">
                    <%-- Stoichiometry --%>
                    <td class="tableCell">
                        <nested:text size="5" property="stoichiometry"/>
                    </td>

                    <%-- Expressed In --%>
                    <td class="tableCell">
                        <nested:select property="expressedIn">
                            <html:options name="biosrclist_" />
                        </nested:select>
                    </td>
                </c:if>
                <td class="tableCell">
                    <nested:write property="organism"/>
                </td>
                <td class="tableCell">
                    <nested:write property="type"/>
                </td>
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
                        onclick="setFeatureDispatch('add');"
                        titleKey="int.proteins.button.feature.add.titleKey">
                        <bean:message key="int.proteins.button.feature.add"/>
                    </html:submit>
                </td>

                <%-- Empty cell spanning many cells --%>
                <td class="tableCell"  colspan="5"/>
                </tr>


            <%-- Loop though ranges for each component --%>
            <nested:iterate property="features">

                <%-- ==============================================================
                     Start of the fourth row. Prints Ranges for each component.
                     ==============================================================
                --%>
                <tr class="tableFeatureRow">

                    <td class="tableCell" rowspan="2"/>

                    <%-- Empty cells for error/link boxes --%>
                    <td class="tableCell" rowspan="2">
                        <nested:checkbox property="checked"/>
                    </td>

                   <%-- Edit feature button --%>
                    <td class="tableCell" rowspan="2">
                        <nested:submit property="featureCmd" onclick="setFeatureDispatch('edit');"
                            titleKey="int.proteins.button.feature.edit.titleKey">
                            <bean:message key="int.proteins.button.feature.edit"/>
                        </nested:submit>
                    </td>

                    <td class="tableCell">
                        <nested:write property="shortLabel"/>
                    </td>

                    <td class="tableCell">
                        <nested:write property="ac"/>
                    </td>
                    <td class="tableCell" rowspan="2">
                        <nested:write property="ranges"/>
                    </td>
                    <td class="tableCell" rowspan="2">
                        <nested:write property="boundDomain"/>
                    </td>
                    <td class="tableCell" rowspan="2">
                        <nested:write property="fullName"/>
                    </td>
                </tr>

                <%-- ==============================================================
                     Start of the fifth row. This row prints feature type and detection
                     ==============================================================
                --%>
                <tr class="tableFeatureRow">

                    <td class="tableCell">
                        <nested:write property="type"/>
                    </td>
                    <td class="tableCell">
                        <nested:write property="detection"/>
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
