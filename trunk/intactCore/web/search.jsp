<%--
/**
* This page allows a user to select a CV object.
*
* @author Sugath Mudali (smudali@ebi.ac.uk)
* @version $Id$
*/
--%>

<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>


<jsp:include page="header.jsp" flush="true" />

    <html:form action="/search" focus="AC">
        <table>
            <tr>
                <td nowrap align="right"><bean:message key="search.ac"/></td>
                <td><html:text property="acNumber" size="50" maxlength="50"/></td>
                <td>
                    <html:submit property="action" value="Search (AC)"></html:submit>
                </td>
            </tr>
            <tr>
                <td nowrap align="right"><bean:message key="search.label"/></td>
                <td><html:text property="label" size="50" maxlength="50"/></td>
                <td>
                    <html:submit property="action" value="Search (Label)"></html:submit>
                </td>
            </tr>
            <tr>
                <td>
                    <html:submit property="action" value="Create New"></html:submit>
                </td>
                <td>
                    <html:reset/>
                </td>
            </tr>
        </table>
    </html:form>

<jsp:include page="footer.jsp" flush="true" />
