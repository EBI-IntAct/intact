<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%--
    Presents information for the CV object.
    Author: Sugath Mudali (smudali@ebi.ac.uk)
    Version: $Id$
--%>

<html:form action="/cv/info/edit">
    <table width="80%" border="0" cellspacing="1" cellpadding="2">
        <tr class="tableRowHeader">
            <th class="tableCellHeader">Action</th>
            <th class="tableCellHeader">AC</th>
            <th class="tableCellHeader">Short Label</th>
            <th class="tableCellHeader">Full Name</th>
        </tr>
        <tr class="tableRowEven">
            <td class="tableCell">
                <html:submit>
                    <bean:message key="button.save"/>
                </html:submit>
            </td>
            <td class="tableCell">
                <bean:write property="ac" name="cvinfoForm"/>
            </td>
            <td class="tableCell">
                <html:text property="shortLabel" name="cvinfoForm" size="10" maxlength="16"/>
            </td>
            <td class="tableCell">
                <html:text property="fullName" name="cvinfoForm" size="80"/>
            </td>
        </tr>
    </table>
</html:form>
