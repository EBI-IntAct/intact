<%@ page import="uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory"%>
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

<script language="JavaScript" type="text/javascript">

    function validateAnnotation() {
    //window.alert('I am here');
    var v = document.forms[0].elements['dispatch'].value;
    window.alert(v);
        if (dispatch == "<%= EditorMenuFactory.SELECT_LIST_ITEM%>") {
            alert("Please select an item from the list first!");
            return;
        }
        return window.confirm("Do you want to delete this CV? Press OK to confirm");
    }

</script>

<%-- The list of topics --%>
<c:set var="topiclist" value="${user.view.menus['Topic_']}"/>

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
</table>
<html:errors property="annotation"/>
<html:errors property="new.annotation"/>
