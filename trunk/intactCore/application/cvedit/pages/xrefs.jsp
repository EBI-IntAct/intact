<%@ page language="java"%>
<%@ page import="uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants,
                 uk.ac.ebi.intact.application.cvedit.struts.view.EditBean"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/struts-nested.tld" prefix="nested"%>

<%--
    Presents xrefrence information for a CV object.
    Author: Sugath Mudali (smudali@ebi.ac.uk)
    Version: $Id$
--%>

<jsp:useBean id="intactuser" scope="session"
    class="uk.ac.ebi.intact.application.cvedit.business.IntactUserImpl"/>

<c:set var="viewbean" value="${intactuser.view}"/>
<c:set var="dblist" value="${intactuser.databaseList}"/>
<c:set var="qlist" value="${intactuser.qualifierList}"/>

<c:if test="${not empty viewbean.xrefs}">
    <h3>Xreferences</h3>

    <html:form action="/cv/xref/edit">
        <table width="80%">
            <tr class="tableRowHeader">
                <th class="tableCellHeader" colspan="2">Action</th>
                <th class="tableCellHeader">Database</th>
                <th class="tableCellHeader">Primary ID</th>
                <th class="tableCellHeader">Secondary ID</th>
                <th class="tableCellHeader">Release Number</th>
                <th class="tableCellHeader">Reference Qualifier</th>
            </tr>
            <%-- To calculate row or even row --%>
            <c:set var="row"/>
            <nested:iterate name="<%=CvEditConstants.XREF_EDIT_FORM%>" property="items">
                <!-- Different styles for even or odd rows -->
                <c:choose>
                    <c:when test="${row % 2 == 0}">
                        <tr class="tableRowEven">
                    </c:when>
                    <c:otherwise>
                        <tr class="tableRowOdd">
                    </c:otherwise>
                </c:choose>
                <c:set var="row" value="${row + 1}"/>
                <%-- The following loop is under <tr> tag --%>

                    <%-- Buttons; Edit or Save depending on the bean state;
                         Delete is visible regardless of the state.
                     --%>
                    <td class="tableCell">
                        <nested:equal name="<%=CvEditConstants.XREF_EDIT_FORM%>"
                            property="editState" value="<%=EditBean.VIEW%>">
                            <html:submit indexed="true" property="cmd">
                                <bean:message key="button.edit"/>
                            </html:submit>
                        </nested:equal>

                        <nested:equal name="<%=CvEditConstants.XREF_EDIT_FORM%>"
                            property="editState" value="<%=EditBean.SAVE%>">
                            <html:submit indexed="true" property="cmd">
                                <bean:message key="button.save"/>
                            </html:submit>
                        </nested:equal>
                    </td>

                    <td class="tableCell">
                        <html:submit indexed="true" property="cmd">
                            <bean:message key="button.delete"/>
                        </html:submit>
                    </td>

                    <%-- In view mode --%>
                    <nested:equal name="<%=CvEditConstants.XREF_EDIT_FORM%>"
                        property="editState" value="<%=EditBean.VIEW%>">
                        <td class="tableCell">
                            <nested:write property="database"/>
                        </td>
                        <td class="tableCell">
                            <nested:write property="primaryId"/>
                        </td>
                        <td class="tableCell">
                            <nested:write property="secondaryId"/>
                        </td>
                        <td class="tableCell">
                            <nested:write property="releaseNumber"/>
                        </td>
                        <td class="tableCell">
                            <nested:write property="qualifier"/>
                        </td>
                    </nested:equal>

                    <%-- In save mode --%>
                    <nested:equal name="<%=CvEditConstants.XREF_EDIT_FORM%>"
                        property="editState" value="<%=EditBean.SAVE%>">
                        <td class="tableCell">
                            <nested:select property="database">
                                <nested:options name="dblist" />
                            </nested:select>
                        </td>
                        <td class="tableCell">
                            <nested:text size="15" property="primaryId"/>
                        </td>
                        <td class="tableCell">
                            <nested:text size="15" property="secondaryId"/>
                        </td>
                        <td class="tableCell">
                            <nested:text size="15" property="releaseNumber"/>
                        </td>
                        <td class="tableCell">
                            <nested:select property="qualifier">
                                <nested:options name="qlist" />
                            </nested:select>
                        </td>
                    </nested:equal>
                </tr>
            </nested:iterate>
        </table>
    </html:form>
</c:if>

<c:if test="${empty viewbean.xrefs}">
    <h3>No Xreferences</h3>
</c:if>
