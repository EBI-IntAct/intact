<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - This page consists of action buttons at the bottom of the editing page.
  --%>

<%@ page language="java"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<script language="JavaScript" type="text/javascript">

    function confirmDelete() {
        return window.confirm("Do you want to delete this CV? Press OK to confirm");
    }

</script>

<table class="table" width="100%" cellspacing="1" cellpadding="2">
    <tr class="tableRowOdd">
        <td align="center" bgcolor="palegreen">
            <html:submit property="dispatch">
                <bean:message key="button.submit"/>
            </html:submit>
            <br/>Saves the current object to the database
        </td>

        <td align="center" bgcolor="yellow">
            <html:submit property="dispatch">
                <bean:message key="button.cancel"/>
            </html:submit>
            <br/>Abandons editing of the current object
        </td>

        <td align="center" bgcolor="red">
            <html:submit property="dispatch" onclick="return confirmDelete()">
                <bean:message key="button.delete"/>
            </html:submit>
            <br/>Deletes the current object from the database
        </td>
    </tr>
</table>
