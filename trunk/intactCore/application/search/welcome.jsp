<%--
    Intact welcome page. Allows a user to enter the search application.

    @author Chris Lewington, Sugath Mudali (smudali@ebi.ac.uk)
    @version $Id$
--%>

<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>

<html:html locale="true">
    <head>
        <title><bean:message key="welcome.main.title"/></title>
        <html:base/>
    </head>

    <body bgcolor="#ffffff">

    <h1 align="center"><bean:message key="welcome.title1"/></h1>
    <h2 align="center"><bean:message key="welcome.title2"/></h1>
    </h2>
        <html:form action="/welcome">
            <h1 align="center">
                <html:image srcKey="welcome.logo" altKey="welcome.logo.alt"/>
            </h1>
        </html:form>
    </body>
</html:html>
