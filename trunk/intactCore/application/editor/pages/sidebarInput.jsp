<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
    This layout displays the search box to search the CV database and another
    input box to create a new Annotated object.
--%>

<%@ page language="java" %>
<%@ page import="uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<script language="JavaScript" type="text/javascript">

    // Validate the short label.
    function validate() {
        var re = /\W+/;
        if (re.test(document.forms[0].shortLabel.value)) {
            window.alert("Only letters and/or numbers are allowed for a new short label.");
            return false;
        }
    }

</script>

<html:form action="/sidebar" focus="AC">
    <table>
        <tr>
            <td align="left">
                <html:select property="topic">
                    <html:options name="<%=EditorConstants.EDITOR_TOPICS%>"/>
                </html:select>
            </td>
        </tr>

        <tr>
            <td>
                <table>
                    <tr>
                        <td><html:text property="searchString" size="15"/></td>
                        <td>
                            <html:submit tabindex="1" property="dispatch">
                                <bean:message key="button.search"/>
                            </html:submit>
                        </td>
                    </tr>
                    <tr>
                        <td><html:text property="shortLabel" size="15"/></td>
                        <td>
                            <html:submit property="dispatch" onclick="return validate()">
                                <bean:message key="button.create"/>
                            </html:submit>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</html:form>
