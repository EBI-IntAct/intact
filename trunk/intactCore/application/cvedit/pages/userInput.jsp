<%@ page language="java" %>
<%@ page import="uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<!--
    This layout displays the search box to search the CV database.
    Author: Sugath Mudali (smudali@ebi.ac.uk)
    Version: $Id$
-->

<script language="JavaScript" type="text/javascript">

    function set(target) {
        document.forms[0].dispatch.value=target;
        if (target == "create") {
            return validate();
        }
    }

    // Validate the short label.
    function validate() {
        var re = /\W+/;
        if (re.test(document.forms[0].shortLabel.value)) {
            window.alert("Only word characters are allowed for a short label");
            return false;
        }
    }

</script>

<html:form action="/cv/choice" focus="AC">
    <html:hidden property="dispatch" value="error"/>
    <table>
        <tr>
            <td align="left">
                <html:select property="topic">
                    <html:options name="<%=CvEditConstants.INTACT_TYPES%>"/>
                </html:select>
            </td>
        </tr>

        <tr>
            <td>
                <table>
                    <tr>
                        <td><html:text property="searchString" size="15"/></td>
                        <td>
                            <html:submit tabindex="1" onclick="return set('search')">
                                <bean:message key="button.search"/>
                            </html:submit>
                        </td>
                    </tr>
                    <tr>
                        <td><html:text property="shortLabel" size="15"/></td>
                        <td>
                            <html:submit onclick="return set('create')">
                                <bean:message key="button.create"/>
                            </html:submit>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</html:form>
