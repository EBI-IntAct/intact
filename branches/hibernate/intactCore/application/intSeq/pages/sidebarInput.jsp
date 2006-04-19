<%@ page language="java" %>
<%@ page import="uk.ac.ebi.intact.application.search.struts.framework.util.SearchConstants"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%--
    This layout displays the search box to search the CV database.
    Author: Sugath Mudali (smudali@ebi.ac.uk)
    Version: $Id$
--%>

<html:form action="/search" focus="AC">
    <table>
        <%--<tr>
            <td align="left">
                <html:select property="searchClass">
                    <html:options name="<%=SearchConstants.INTACT_TYPES%>"/>
                </html:select>
            </td>
        </tr>

        <tr>
            <td><html:text property="searchString" size="16"/></td>
        </tr>
        <tr>
            <td>
                <html:submit titleKey="sidebar.button.search.title">
                    <bean:message key="sidebar.button.search"/>
                </html:submit>
            </td>
        </tr> --%>
    </table>
</html:form>
