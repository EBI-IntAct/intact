<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-nested.tld" prefix="nested"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%--
    Displays the results of the search query.
    Author: Sugath Mudali (smudali@ebi.ac.uk)
    Version: $Id$
--%>

<jsp:useBean id="intactuser" scope="session"
    class="uk.ac.ebi.intact.application.cvedit.business.IntactUserImpl"/>

Search class: <c:out value="${intactuser.lastSearchClass}"/>
&nbsp;Query: <c:out value="${intactuser.lastSearchQuery}"/>

<html:form action="/cv/results">

<table width="100%">
    <tr class="tableRowHeader">
        <th class="tableCellHeader" width="10%">AC</th>
        <th class="tableCellHeader" width="20%">Short Label</th>
        <th class="tableCellHeader" width="30%">Full Name</th>
        <th class="tableCellHeader" width="10%">Date Updated</th>
        <th class="tableCellHeader" width="10%">Date Created</th>
        <th class="tableCellHeader" width="10%">Owner</th>
    </tr>
    <nested:iterate property="items">
    <tr>
        <td class="tableCell">
            <nested:link page="/do/cv/results"
                paramId="shortLabel" paramProperty="shortLabel">
                <nested:write property="ac"/>
            </nested:link>
        </td>
        <td class="tableCell"><nested:write property="shortLabel"/></td>
        <td class="tableCell"><nested:write property="fullName"/></td>
        <td class="tableCell"><nested:write property="updated"/></td>
        <td class="tableCell"><nested:write property="created"/></td>
        <td class="tableCell"><nested:write property="owner"/></td>
<%--        <td class="tableCell">--%>
<%--            <nested:link page="/do/cv/results"--%>
<%--                paramId="shortLabel" paramProperty="shortLabel">Edit</nested:link>--%>
<%--            <html:link page="/do/cv/results" onclick="return xxx();" paramId="shortLabel" paramProperty="shortLabel">Delete</html:link>--%>
<%--        </td>--%>
    <%--    <td><html:submit property="cmd" value="Edit" indexed="true"/></td>--%>
    </tr>
    </nested:iterate>
</table>
</html:form>
