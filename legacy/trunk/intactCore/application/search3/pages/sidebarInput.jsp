<%@ page language="java"  %>
<%@ page buffer="none"    %>
<%@ page autoFlush="true" %>

<%@ page import="uk.ac.ebi.intact.application.search3.struts.util.SearchConstants"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%--
    This layout displays the search box to search the CV database.
    Author: Sugath Mudali (smudali@ebi.ac.uk)
    Version: $Id$
--%>

<html:form action="/search" focus="searchString">
    <table>
        <tr>
            <td><html:text property="searchString" size="16"/></td>
        </tr>
        <tr>
            <td>
                <html:submit titleKey="sidebar.button.search.title">
                    <bean:message key="sidebar.button.search"/>
                </html:submit>
            </td>
        </tr>
    </table>
</html:form>
