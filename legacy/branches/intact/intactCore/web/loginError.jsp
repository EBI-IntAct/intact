<%--
/**
 * Displays the error message.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
--%>

<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>

<html:html locale="true">
<head>
    <title><bean:message key="login.title"/></title>
    <html:base/>
</head>

<body bgcolor="white">
    <html:errors/>
</body>

</html:html>
