<%@ page language="java" import="java.util.Collection,
                                 uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants,
                                 uk.ac.ebi.intact.application.cvedit.struts.view.CvViewBean"
%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/display.tld" prefix="display"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%--
    Presents a form for user to edit.
    Author Sugath Mudali (smudali@ebi.ac.uk)
    Version $Id$
--%>

<script language="JavaScript" type="text/javascript">

    function set(target) {
        // The last form.
        document.forms[2].dispatch.value=target;
        if (target == "delete") {
            return confirmDelete();
        }
    }

    function confirmDelete() {
        var label = document.forms[2].shortlabel.value;
        return window.confirm("Delete " + label + "? Press OK to confirm");
    }

</script>

<jsp:useBean id="intactuser" scope="session"
    class="uk.ac.ebi.intact.application.cvedit.business.IntactUserImpl"/>

<c:set var="viewbean" value="${intactuser.view}"/>
<c:set var="emptycoll" value="${viewbean.emptyCollection}"/>

<!-- Set the drop down lists -->
<c:set var="topiclist" value="${intactuser.topicList}"/>
<c:set var="dblist" value="${intactuser.databaseList}"/>
<c:set var="qlist" value="${intactuser.qualifierList}"/>

<%
    // The keys to save annotations and xrefs.
    String annotations = "annotations";
    String xrefs = "xrefs";

    CvViewBean viewbean = intactuser.getView();

    // Need to save in a session to access them later.
    Collection annotcoll = viewbean.getAnnotations();
    session.setAttribute(annotations, annotcoll);

    // Save x'refereneces in a session object to access them later.
    Collection xrefcoll = viewbean.getXrefs();
    session.setAttribute(xrefs, xrefcoll);

    // The number of rows per page.
    int rowspp = 10;

    // Need a string version for display tag.
    String rowspps = Integer.toString(rowspp);

    // Compute the max number of page numbers for annotations.
    int annotsize = annotcoll.size();
    // The number of pages required to display annotations.
    int nofap = annotsize / rowspp;
    if (annotsize % rowspp != 0) {
        nofap++;
    }
    // Save the maximum number of annotation pages in a session.
    session.setAttribute("maxannotpages", Integer.toString(nofap));

    // Compute the max number of page numbers for xref.
    int xrefsize = xrefcoll.size();
    // The number of pages required to display xrefs.
    int nofxp = xrefsize / rowspp;
    if (xrefsize % rowspp != 0) {
        nofxp++;
    }
    // Save the maximum number of xref pages in a session.
    session.setAttribute("maxxrefpages", Integer.toString(nofxp));
%>

<!--
    Using the page parameter, check and see whether we have enough
    data to display or else print an empty table.
-->
<c:if test="${not empty param.page}">
    <fmt:parseNumber var="pno" value="${param.page}" />
    <fmt:parseNumber var="maxpages" value="${maxannotpages}"/>
    <!-- Has the current page exceeded the maxmium pages we can display? -->
    <c:if test="${pno > maxpages}">
        <c:set var="annotations" value="${emptycoll}"/>
    </c:if>
</c:if>

<!--
    Using the page parameter, check and see whether we have enough
    data to display or else print an empty table.
-->
<c:if test="${not empty param.page}">
    <fmt:parseNumber var="pno" value="${param.page}" />
    <fmt:parseNumber var="maxpages" value="${maxxrefpages}"/>
    <!-- Has the current page exceeded the maxmium pages we can display? -->
    <c:if test="${pno > maxpages}">
        <c:set var="xrefs" value="${emptycoll}"/>
    </c:if>
</c:if>

<h2><html:errors/></h2>

Topic: <b><c:out value="${viewbean.topic}"/></b>
&nbsp;&nbsp;AC: <b><c:out value="${viewbean.ac}"/></b>
&nbsp;&nbsp;Short Label: <b><c:out value="${viewbean.shortLabel}"/></b>

<!-- a line to separate the header -->
<hr size=2>

<h3>Comments</h3>

<display:table width="100%" name="<%=annotations%>" pagesize="<%=rowspps%>"
    decorator="uk.ac.ebi.intact.application.cvedit.struts.view.CommentBeanWrapper">
    <display:column property="topic" title="Topic"/>
    <display:column property="description" title="Description"/>
    <display:column property="deleteLink" title="Delete"/>
</display:table>

<hr></hr>

<!-- Adds a new comment. This will invoke addComment action. -->
<html:form action="/cv/comment/add">
    <table class="table" width="100%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableRowHeader">
        <th width="15%" class="tableCellHeader">Topic</th>
        <th width="85%" class="tableCellHeader">Description</th>
    </tr>
    <tr class="tableRowOdd">
        <td class="tableCell" align="left" valign="top">
            <html:select property="topic">
                <html:options name="topiclist" />
            </html:select>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:textarea property="description" rows="3" cols="80"/>
        </td>
        <td class="tableCell" align="right" valign="top">
            <html:submit property="action" value="Add" />
        </td>
        <td class="tableCell" align="right" valign="top">
            <html:reset/>
        </td>
    </tr>
    </table>
</html:form>

<hr></hr>

<h3>Xreferences</h3>

<display:table width="100%" name="<%=xrefs%>" pagesize="<%=rowspps%>"
    decorator="uk.ac.ebi.intact.application.cvedit.struts.view.XreferenceBeanWrapper">
    <display:column property="database" title="Database" width="11%"/>
    <display:column property="primaryId" title="Primary ID" width="20%"/>
    <display:column property="secondaryId" title="Secondary ID" width="20%"/>
    <display:column property="releaseNumber" title="Release Number" width="20%"/>
    <display:column property="qualifier" title="Reference Qualifier" width="20%"/>
    <display:column property="deleteLink" title="Delete" width="10%"/>
</display:table>

<hr></hr>

<!-- Adds a new xreferece. This will invoke addXref action. -->
<html:form action="/cv/xref/add">
    <table class="table" width="100%" border="0">
    <tr class="tableRowHeader">
        <th width="11%" class="tableCellHeader">Database</th>
        <th width="19%" class="tableCellHeader">Primary ID</th>
        <th width="20%" class="tableCellHeader">Secondary ID</th>
        <th width="20%" class="tableCellHeader">Release Number</th>
        <th width="20%" class="tableCellHeader">Reference Qualifier</th>
    </tr>
    <tr class="tableRowOdd">
        <td class="tableCell" align="left" valign="top">
            <html:select property="database">
                <html:options name="dblist" />
            </html:select>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:text property="primaryId" size="21"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:text property="secondaryId" size="21"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:text property="releaseNumber" size="21"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:select property="qualifier">
                <html:options name="qlist" />
            </html:select>
        </td>
        <td class="tableCell" align="right" valign="top">
            <html:submit property="action" value="Add" />
        </td>
        <td class="tableCell" align="right" valign="top">
            <html:reset/>
        </td>
    </tr>
    </table>
</html:form>

<hr size=2>

<html:form action="/cv/edit">
    <html:hidden property="shortlabel" value="<%=viewbean.getShortLabel()%>"/>
    <html:hidden property="dispatch" value="error"/>

    <html:submit onclick="return set('submit')">
        <bean:message key="button.submit"/>
    </html:submit>

    <html:submit onclick="return set('delete')">
        <bean:message key="button.delete"/>
    </html:submit>

    <html:submit onclick="return set('cancel')">
        <bean:message key="button.cancel"/>
    </html:submit>
</html:form>
