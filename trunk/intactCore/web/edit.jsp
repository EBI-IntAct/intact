<%--
/**
 * Presents a form for user to edit.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
--%>

<%@ page language="java" import="java.util.Collection,
    uk.ac.ebi.intact.struts.framework.util.WebIntactConstants,
    java.util.ArrayList,
    uk.ac.ebi.intact.struts.view.CommentBean"
%>
<!-- JSTL tag libraries-->
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/display" prefix="display" %>

<jsp:include page="header.jsp" flush="true" />

<jsp:useBean id="viewbean" scope="session"
    class="uk.ac.ebi.intact.struts.view.CvViewBean"/>

<%
    // Aliases to save us from typing the full name.
    String annotations = WebIntactConstants.ANNOTATIONS;
    String xrefs = WebIntactConstants.XREFS;

    // Need to save in a session to access them later.
    Collection annotcoll = viewbean.getAnnotations();
    session.setAttribute(annotations, annotcoll);

    // Save x'refereneces in a session object to access them later.
    Collection xrefcoll = viewbean.getXrefs();
    session.setAttribute(xrefs, xrefcoll);

    // To display an empty table when we have no annotations or xrefs.
    session.setAttribute("emptycoll", viewbean.getEmptyCollection());

    // The number of rows per page.
    int rowspp = 2;

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
        <c:set var="annotations" value="${emptycoll}" scope="request"/>
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
        <c:set var="xrefs" value="${emptycoll}" scope="request"/>
    </c:if>
</c:if>

<h3><%=viewbean.getTopic()%></h3>

<hr size=2>

Accession Number: <b><c:out value="${viewbean.ac}" /></b>
&nbsp;&nbsp;Short Label:
<html:text property="shortLabel" value="<%=viewbean.getShortLabel()%>" size="16" />

<!-- a line to separate the header -->
<hr size=2>

<h3>Comments</h3>

<display:table width="100%" name="<%=annotations%>" pagesize="<%=rowspps%>"
    decorator="uk.ac.ebi.intact.struts.view.CommentBeanWrapper">
    <display:column property="topic" title="Topic"/>
    <display:column property="description" title="Description"/>
    <display:column property="deleteLink" title="Delete"/>
</display:table>

<hr></hr>

<!-- Adds a new comment. This will invoke addComment action. -->
<html:form action="/addComment">
    <table class="table" width="90%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableRowHeader">
        <th width="15%" class="tableCellHeader">Topic</th>
        <th width="85%" class="tableCellHeader">Description</th>
    </tr>
    <tr class="tableRowOdd">
        <td class="tableCell" align="left" valign="top">
            <html:text property="topic" size="15"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:textarea property="text" rows="3" cols="80"/>
        </td>
    </tr>
    <tr class="tableRowEven">
        <td class="tableCell" align="right" valign="top" colspan="2">
            <html:submit property="action" value="Add" />
            <html:reset/>
        </td>
    </tr>
    </table>
</html:form>

<hr size=2>

<h3>Xreferences</h3>

<%--
<% String pno = (String) request.getParameter("page");
   if (pno != null) {
       int p = Integer.parseInt(pno);
       if (p > ((Integer) session.getAttribute("maxpages")).intValue()) {
           session.setAttribute(xrefs, viewbean.getEmptyXrefs());
       }
   }
%>
--%>

<display:table width="100%" name="<%=xrefs%>" pagesize="<%=rowspps%>"
    decorator="uk.ac.ebi.intact.struts.view.XreferenceBeanWrapper">
    <display:column property="database" title="Database" width="10%"/>
    <display:column property="primaryId" title="Primary ID" width="20%"/>
    <display:column property="secondaryId" title="Secondary ID" width="20%"/>
    <display:column property="releaseNumber" title="Release Number" width="20%"/>
    <display:column property="qualifier" title="Reference Qualifier" width="20%"/>
    <display:column property="deleteLink" title="Delete" width="10%"/>
</display:table>

<hr></hr>

<!-- Adds a new xreferece. This will invoke addXref action. -->
<html:form action="/addXref">
    <table class="table" width="90%" border="0" cellspacing="1" cellpadding="2">
    <tr class="tableRowHeader">
        <th width="10%" class="tableCellHeader">Database</th>
        <th width="20%" class="tableCellHeader">Primary ID</th>
        <th width="20%" class="tableCellHeader">Secondary ID</th>
        <th width="20%" class="tableCellHeader">Release Number</th>
        <th width="20%" class="tableCellHeader">Reference Qualifier</th>
    </tr>
    <tr class="tableRowOdd">
        <td class="tableCell" align="left" valign="top">
            <html:text property="database" size="9"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:text property="primaryId" size="20"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:text property="secondaryId" size="19"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:text property="releaseNumber" size="20"/>
        </td>
        <td class="tableCell" align="left" valign="top">
            <html:text property="qualifer" size="20"/>
        </td>
    </tr>
    <tr class="tableRowEven">
        <td></td>
        <td class="tableCell" align="right" valign="top" colspan="4">
            <html:submit property="action" value="Add" />
            <html:reset/>
        </td>
    </tr>
    </table>
</html:form>

<hr size=2>

<html:form action="/editCv">
    <html:submit property="action" value="Submit" />
    <html:submit property="action" value="Delete" />
    <html:submit property="action" value="Cancel" />
</html:form>

<jsp:include page="footer.jsp" flush="true" />