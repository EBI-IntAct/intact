<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Presents Interaction information for an experiment. The form name is hard
  - coded (expForm).
  --%>

<%@ page language="java"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<h3>Interactions</h3>

<c:if test="${not empty expForm.map.ints}">

    <table width="100%" border="0" cellspacing="1" cellpadding="2">
        <tr class="tableRowHeader">
            <th class="tableCellHeader" width="20%">
                <bean:message key="label.action"/>
            </th>
            <th class="tableCellHeader" width="10%">
                <bean:message key="label.shortlabel"/>
            </th>
            <th class="tableCellHeader" width="10%">
                <bean:message key="label.ac"/>
            </th>
            <th class="tableCellHeader" width="60%">
                <bean:message key="label.fullname"/>
            </th>
        </tr>
        <%-- To calculate row or even row --%>
        <c:set var="row"/>
        <c:forEach var="interaction" items="${expForm.map.ints}">
            <%-- Different styles for even or odd rows --%>
            <c:choose>
                <c:when test="${row % 2 == 0}">
                    <tr class="tableRowEven">
                </c:when>
                <c:otherwise>
                    <tr class="tableRowOdd">
                </c:otherwise>
            </c:choose>

                <td class="tableCell">
                    <html:submit indexed="true" property="intCmd"
                        titleKey="int.exp.button.del.titleKey">
                        <bean:message key="int.exp.button.del"/>
                    </html:submit>
                </td>

                <td class="tableCell">
                    <bean:write name="interaction" property="shortLabelLink" filter="false"/>
                </td>
                <td class="tableCell">
                    <bean:write name="interaction" property="ac"/>
                </td>
                <td class="tableCell">
                    <bean:write name="interaction" property="fullName"/>
                </td>
            </tr>
        </c:forEach>
    </table>
</c:if>