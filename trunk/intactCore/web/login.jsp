<%--
   /**
    * Logon page. Prompts a user with with user name and password.
    *
    * @author Chris Lewington, Sugath Mudali (smudali@ebi.ac.uk)
    * @version $Id$
    */
--%>

<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>

<!-- Our own tags to display CV topics -->
<%@ taglib uri="/WEB-INF/tld/intact.tld" prefix="intact" %>

<html:html locale="true">
    <head>
        <title><bean:message key="login.title"/></title>
        <html:base/>
    </head>

    <body bgcolor="white">

        <html:form action="/login" focus="username">
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
                    <th align="right">
                        <bean:message key="login.topics"/>
                    </th>
                    <td align="left">
                        <intact:listIntactTypes resource="config/IntactTypes"/>
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
            </table>
        </html:form>
    </body>
</html:html>
