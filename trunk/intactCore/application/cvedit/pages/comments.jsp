<%@ page language="java"%>
<%@ page import="uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants,
                 uk.ac.ebi.intact.application.cvedit.struts.view.EditBean"%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/struts-nested.tld" prefix="nested"%>

<%--
    Presents comments (annotations) information for a CV object.
    Author: Sugath Mudali (smudali@ebi.ac.uk)
    Version: $Id$
--%>

<jsp:useBean id="intactuser" scope="session"
    class="uk.ac.ebi.intact.application.cvedit.business.IntactUserImpl"/>

<c:set var="viewbean" value="${intactuser.view}"/>

<!-- Set the drop down lists -->
<c:set var="topiclist" value="${intactuser.topicList}"/>


<c:if test="${not empty viewbean.annotations}">
    <h3>Annotations</h3>

    <html:form action="/cv/comment/edit">
        <table width="80%" border="0" cellspacing="1" cellpadding="2">
            <tr class="tableRowHeader">
                <th class="tableCellHeader" colspan="2">Action</th>
                <th class="tableCellHeader">Topic</th>
                <th class="tableCellHeader">Description</th>
            </tr>
            <%-- To calculate row or even row --%>
            <c:set var="row"/>
            <nested:iterate name="<%=CvEditConstants.COMMENT_EDIT_FORM%>"
                property="items">
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

                    <%-- Buttons; Edit or Save depending on the bean state;
                         Delete is visible regardless of the state.
                     --%>
                    <td class="tableCell">
                        <nested:equal name="<%=CvEditConstants.COMMENT_EDIT_FORM%>"
                            property="editState" value="<%=EditBean.VIEW%>">
                            <html:submit indexed="true" property="cmd">
                                <bean:message key="button.edit"/>
                            </html:submit>
                        </nested:equal>

                        <nested:equal property="editState" value="<%=EditBean.SAVE%>">
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

                <%-- The following loop is under <tr> tag --%>
                    <nested:equal name="<%=CvEditConstants.COMMENT_EDIT_FORM%>"
                        property="editState" value="<%=EditBean.VIEW%>">
                        <td class="tableCell">
                            <nested:write property="topic"/>
                        </td>
                        <td class="tableCell">
                            <nested:write property="description"/>
                        </td>
                    </nested:equal>

                    <nested:equal property="editState" value="<%=EditBean.SAVE%>">
                        <td class="tableCell">
                            <nested:select property="topic">
                                <nested:options name="topiclist" />
                            </nested:select>
                        </td>
                        <td class="tableCell">
                            <nested:textarea cols="70" rows="3" property="description"/>
                        </td>
                    </nested:equal>
                </tr>
            </nested:iterate>
        </table>
    </html:form>

</c:if>

<c:if test="${empty viewbean.annotations}">
    <h3>No Annotations</h3>
</c:if>
