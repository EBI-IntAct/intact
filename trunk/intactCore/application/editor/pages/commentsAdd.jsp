<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Presents the page to add an annotation to an Annotated object.
  --%>

<%@ page language="java"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/editor.tld" prefix="editor"%>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<%-- The list of topics --%>
<c:set var="topiclist" value="${user.view.addAnnotationMenus}"/>

<!-- Adds a new comment. This will invoke addComment action. -->
<html:form action="/comment/add">
    <table class="table" width="80%" border="0" cellspacing="1" cellpadding="2">
        <tr class="tableRowHeader">
            <th class="tableCellHeader" colspan="2">Action</th>
            <th class="tableCellHeader">Topic</th>
            <th class="tableCellHeader">Description</th>
            <th>
                <editor:helpLink tag="annotations"/>
            </th>
        </tr>
        <tr class="tableRowOdd">
            <td class="tableCell" align="left" valign="bottom">
                <html:submit titleKey="annotations.button.add.titleKey">
                    <bean:message key="button.add"/>
                </html:submit>
            </td>
            <td class="tableCell" align="left" valign="bottom">
                <html:reset/>
            </td>
            <td class="tableCell" align="left" valign="top">
                <html:select property="topic">
                    <html:options name="topiclist" />
                </html:select>
                <html:errors property="comment.topic"/>
            </td>
            <td class="tableCell" align="left" valign="top">
                <html:textarea property="description" rows="3" cols="70"/>
            </td>
        </tr>
    </table>
</html:form>
