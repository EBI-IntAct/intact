<%@ page import="uk.ac.ebi.intact.application.cvedit.business.IntactUserIF,
                 uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants"%>
<%@ page language="java" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/display" prefix="display" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<%--
    Displays the results of the search query.
    Author: Sugath Mudali (smudali@ebi.ac.uk)
    Version: $Id$
--%>

<jsp:useBean id="intactuser" scope="session"
    class="uk.ac.ebi.intact.application.cvedit.business.IntactUserImpl"/>

<%-- Need to to save this in a page context for display library to access --%>
<% pageContext.setAttribute("matchlist", intactuser.getCacheSearchResult()); %>

Search class: <c:out value="${intactuser.lastSearchClass}"/>
&nbsp;Query: <c:out value="${intactuser.lastSearchQuery}"/>

<display:table width="100%" name="matchlist"
    decorator="uk.ac.ebi.intact.application.cvedit.struts.view.Wrapper">
    <display:column property="ac" title="AC"
        href="../do/cv/results" paramId="shortLabel" paramProperty="shortLabel" />
    <display:column property="shortLabel" title="Short Label" />
</display:table>
