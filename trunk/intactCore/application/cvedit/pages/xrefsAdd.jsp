<%@ page language="java"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%--
    Presents the page to add a xreference to a CV object.
    Author: Sugath Mudali (smudali@ebi.ac.uk)
    Version: $Id$
--%>

<jsp:useBean id="intactuser" scope="session"
    class="uk.ac.ebi.intact.application.cvedit.business.IntactUserImpl"/>

<c:set var="dblist" value="${intactuser.databaseList}"/>
<c:set var="qlist" value="${intactuser.qualifierList}"/>

<%-- Adds a new xreferece. This will invoke addXref action. --%>
<html:form action="/cv/xref/add">
    <table class="table" width="78%" border="0">
    <tr class="tableRowHeader">
                <th class="tableCellHeader" width="15%">Database</th>
                <th class="tableCellHeader" width="15%">Primary ID</th>
                <th class="tableCellHeader" width="15%">Secondary ID</th>
                <th class="tableCellHeader" width="15%">Release Number</th>
                <th class="tableCellHeader" width="17%">Reference Qualifier</th>
                <th class="tableCellHeader" width="5%" colspan="2">Action</th>
    </tr>
    <tr class="tableRowOdd">
        <td class="tableCell" align="left" valign="top">
            <html:select property="database">
                <html:options name="dblist" />
            </html:select>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:text property="primaryId" size="15"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:text property="secondaryId" size="15"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:text property="releaseNumber" size="15"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:select property="qualifier">
                <html:options name="qlist" />
            </html:select>
        </td>
        <td class="tableCell" align="right" valign="top">
            <html:submit>
                <bean:message key="button.add"/>
            </html:submit>
        </td>
        <td class="tableCell" align="right" valign="top">
            <html:reset/>
        </td>
    </tr>
    </table>
</html:form>
