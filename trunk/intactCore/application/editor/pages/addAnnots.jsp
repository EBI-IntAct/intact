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
<%@ taglib uri="/WEB-INF/tld/intact.tld" prefix="intact"%>
<%@ taglib uri="/WEB-INF/tld/struts-logic.tld" prefix="logic"%>

<%-- The list of topics --%>
<c:set var="topiclist" value="${user.view.addTopicMenu}"/>

<%-- Errors are displayed in its own table if present --%>
<%--<logic:messagesPresent name="annotation">--%>
<%--    <table width="100%" border="0" cellspacing="1" cellpadding="2">--%>
        <%-- Error messages --%>
<%--        <html:messages id="annotation">--%>
<%--            <tr class="tableRowEven">--%>
<%--                <td class="tableErrorCell"><bean:write name="annotation" filter="false"/></td>--%>
<%--            </tr>--%>
<%--        </html:messages>--%>
<%--    </table>--%>
<%--</logic:messagesPresent>--%>

<%-- The anchor name for this page --%>
<a name="annotation"/>

<table class="table" width="100%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableRowHeader">
        <th class="tableCellHeader">Action</th>
        <th class="tableCellHeader">Topic</th>
        <th class="tableCellHeader">Description</th>
        <th>
            <intact:documentation section="editor.annotations"/>
        </th>
    </tr>
    <tr class="tableRowOdd">
        <td class="tableCell" align="left" valign="bottom">
            <html:submit property="dispatch"
                titleKey="annotations.button.add.titleKey">
                <bean:message key="annotations.button.add"/>
            </html:submit>
        </td>

        <td class="tableCell" align="left" valign="top">
            <html:select property="newAnnotation.topic">
                <html:options name="topiclist"/>
            </html:select>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:textarea property="newAnnotation.description" rows="3" cols="70"/>
        </td>
    </tr>
<%----%>
<%--    <!-- Displays error messages for the short label -->--%>
<%--    <logic:messagesPresent>--%>
<%--        <table width="100%" border="0" cellspacing="1" cellpadding="2">--%>
<%--            <html:messages id="topic">--%>
<%--                <tr class="tableRowEven">--%>
<%--                    <td class="tableErrorCell"><bean:write name="topic"/></td>--%>
<%--                </tr>--%>
<%--            </html:messages>--%>
<%--        </table>--%>
<%--    </logic:messagesPresent>--%>
</table>
<html:errors property="annotation"/>
