<%@ page language="java"
    import="uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants,
            uk.ac.ebi.intact.application.cvedit.struts.view.EditBean"
%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/struts-nested.tld" prefix="nested"%>

<%--
    Presents a form for user to edit.
    Author Sugath Mudali (smudali@ebi.ac.uk)
    Version $Id$
--%>

<jsp:useBean id="intactuser" scope="session"
    class="uk.ac.ebi.intact.application.cvedit.business.IntactUserImpl"/>

<c:set var="viewbean" value="${intactuser.view}"/>
<c:set var="emptycoll" value="${viewbean.emptyCollection}"/>

<!-- Set the drop down lists -->
<c:set var="topiclist" value="${intactuser.topicList}"/>
<c:set var="dblist" value="${intactuser.databaseList}"/>
<c:set var="qlist" value="${intactuser.qualifierList}"/>

<h2><html:errors/></h2>

Topic: <b><c:out value="${viewbean.topic}"/></b>
&nbsp;&nbsp;AC: <b><c:out value="${viewbean.ac}"/></b>
&nbsp;&nbsp;Short Label: <b><c:out value="${viewbean.shortLabel}"/></b>

<h3>Comments</h3>

<c:if test="${not empty viewbean.annotations}">
    <html:form action="/cv/comment/edit">
        <table width="70%">
            <tr class="tableRowHeader">
                <th class="tableCellHeader" width="10%">Topic</th>
                <th class="tableCellHeader" width="40%">Description</th>
                <th class="tableCellHeader" width="10%" colspan="2">Action</th>
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

                <%-- The following loop is under <tr> tag --%>
                    <nested:equal name="<%=CvEditConstants.COMMENT_EDIT_FORM%>"
                        property="editState" value="<%=EditBean.VIEW%>">
                        <td class="tableCell">
                            <nested:text size="17" readonly="true" property="topic"/>
                        </td>
                        <td class="tableCell">
                            <nested:textarea cols="70" rows="3" readonly="true" property="description"/>
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

                    <%-- Buttons; Edit or Save depending on the bean state;
                         Delete is visible regardless of the state.
                     --%>
                    <td class="tableCell">
                        <nested:equal name="<%=CvEditConstants.COMMENT_EDIT_FORM%>"
                            property="editState" value="<%=EditBean.VIEW%>">
                            <html:image indexed="true" property="cmd"
                                value="Edit" srcKey="button.edit.img"/>
                        </nested:equal>

                        <nested:equal property="editState" value="<%=EditBean.SAVE%>">
                            <html:image indexed="true" property="cmd"
                                value="Save" srcKey="button.save.img"/>
                        </nested:equal>
                    </td>

                    <td class="tableCell">
                        <html:image indexed="true" property="cmd"
                            value="Delete" srcKey="button.del.img"/>
                    </td>
                </tr>
            </nested:iterate>
        </table>
    </html:form>

</c:if>

<c:if test="${empty viewbean.annotations}">
    No Annotations
</c:if>

<hr></hr>

<!-- Adds a new comment. This will invoke addComment action. -->
<html:form action="/cv/comment/add">
    <table class="table" width="70%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableRowHeader">
        <th width="13%" class="tableCellHeader">Topic</th>
        <th width="70%" class="tableCellHeader">Description</th>
    </tr>
    <tr class="tableRowOdd">
        <td class="tableCell" align="left" valign="top">
            <html:select property="topic">
                <html:options name="topiclist" />
            </html:select>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:textarea property="description" rows="3" cols="70"/>
        </td>
        <td class="tableCell" align="left" valign="bottom">
            <html:submit>
                <bean:message key="button.add"/>
            </html:submit>
        <td class="tableCell" align="left" valign="bottom">
            <html:reset/>
        </td>
        </td>
    </tr>
    </table>
</html:form>

<hr></hr>

<h3>Xreferences</h3>

<c:if test="${not empty viewbean.xrefs}">
    <html:form action="/cv/xref/edit">
        <table width="78%">
            <tr class="tableRowHeader">
                <th class="tableCellHeader" width="15%">Database</th>
                <th class="tableCellHeader" width="15%">Primary ID</th>
                <th class="tableCellHeader" width="15%">Secondary ID</th>
                <th class="tableCellHeader" width="15%">Release Number</th>
                <th class="tableCellHeader" width="17%">Reference Qualifier</th>
                <th class="tableCellHeader" width="5%" colspan="2">Action</th>
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

                    <%-- In view mode --%>
                    <nested:equal name="<%=CvEditConstants.XREF_EDIT_FORM%>"
                        property="editState" value="<%=EditBean.VIEW%>">
                        <td class="tableCell">
                            <nested:text size="15" readonly="true" property="database"/>
                        </td>
                        <td class="tableCell">
                            <nested:text size="15" readonly="true" property="primaryId"/>
                        </td>
                        <td class="tableCell">
                            <nested:text size="15" readonly="true" property="secondaryId"/>
                        </td>
                        <td class="tableCell">
                            <nested:text size="15" readonly="true" property="releaseNumber"/>
                        </td>
                        <td class="tableCell">
                            <nested:text size="15" readonly="true" property="qualifier"/>
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

                    <%-- Buttons; Edit or Save depending on the bean state;
                         Delete is visible regardless of the state.
                     --%>
                    <td class="tableCell">
                        <nested:equal name="<%=CvEditConstants.XREF_EDIT_FORM%>"
                            property="editState" value="<%=EditBean.VIEW%>">
                            <html:image indexed="true" property="cmd"
                                value="Edit" srcKey="button.edit.img"/>
                        </nested:equal>

                        <nested:equal name="<%=CvEditConstants.XREF_EDIT_FORM%>"
                            property="editState" value="<%=EditBean.SAVE%>">
                            <html:image indexed="true" property="cmd"
                                value="Save" srcKey="button.save.img"/>
                        </nested:equal>
                    </td>

                    <td class="tableCell">
                        <html:image indexed="true" property="cmd"
                                    value="Delete" srcKey="button.del.img"/>
                    </td>
                </tr>
            </nested:iterate>
        </table>
    </html:form>
</c:if>

<c:if test="${empty viewbean.xrefs}">
    No Xreferences
</c:if>

<hr></hr>

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

<hr size=2>

<html:form action="/cv/edit">
    <html:submit property="dispatch">
        <bean:message key="button.submit"/>
    </html:submit>

    <html:submit property="dispatch">
        <bean:message key="button.delete"/>
    </html:submit>

    <html:submit property="dispatch">
        <bean:message key="button.cancel"/>
    </html:submit>
</html:form>
