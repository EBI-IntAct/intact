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
        <%--
            <table border="0" width="100%">

                <tr>
                    <th align="right">
                        <bean:message key="login.username"/>
                    </th>
                    <td align="left">
                        <html:text property="username" size="16" maxlength="16"/>
                    </td>
                </tr>

                <tr>
                    <th align="right">
                        <bean:message key="login.password"/>
                    </th>
                    <td align="left">
                        <html:password property="password" size="16" maxlength="16"
                                redisplay="false"/>
                    </td>
                </tr>



                <tr>
                    <td align="right">
                        <html:submit property="submit">
                            <bean:message key="button.login"/>
                        </html:submit>
                    </td>
                    <td align="left">
                        <html:reset/>
                    </td>
                </tr>
                <tr>
                </tr>
            </table>
            --%>
   <%--         <h1 align="center"><html:link page="/search.jsp"><img src="intact-logo.jpg" border="0"></html:link></h1> --%>
            <h1 align="center"><input type="image" src="intact-logo.jpg" border="0" name="click here to begin"></h1>

        </html:form>
    </body>
</html:html>
