<%--
   /**
    * Intact welcome page. Allows a user to enter the search application.
    *
    * @author Chris Lewington
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

    <body bgcolor="#ffffff">

    <h1 align="center">Welcome to the Intact Database! </h1>
    <h2 align="center">Please click on the logo:</h1>
    </h2>
        <html:form action="/welcome">
            <h1 align="center"><input type="image" src="intact-logo.jpg" border="0" name="click here to begin"></h1>

        </html:form>
    </body>
</html:html>
