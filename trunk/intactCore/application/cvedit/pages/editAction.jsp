<%@ page language="java"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%--
    Action buttons for a CV edit screen.
    Author: Sugath Mudali (smudali@ebi.ac.uk)
    Version: $Id$
--%>

<script language="JavaScript" type="text/javascript">

    function confirmDelete() {
        return window.confirm("Do you want to delete this CV? Press OK to confirm");
    }

</script>

<html:form action="/cv/edit">
    <html:submit property="dispatch">
        <bean:message key="button.submit"/>
    </html:submit>

    <html:submit property="dispatch" onclick="return confirmDelete()">
        <bean:message key="button.delete"/>
    </html:submit>

    <html:submit property="dispatch">
        <bean:message key="button.cancel"/>
    </html:submit>
</html:form>
