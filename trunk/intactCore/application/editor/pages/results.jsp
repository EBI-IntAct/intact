<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Displays the results data from the search query.
  --%>

<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/display" prefix="display" %>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

Search class: <c:out value="${user.searchClass}"/>
&nbsp;Query: <c:out value="${user.searchQuery}"/>

<%-- Need to to save this in a page context for display library to access --%>
<c:set var="searchlist" value="${user.searchResult}"/>

<%-- Javascript code to link to the search page. --%>
<jsp:include page="js.jsp" />

<html:form action="/results">
    <display:table width="100%" name="searchlist"
        decorator="uk.ac.ebi.intact.application.editor.struts.view.Wrapper">
        <display:column property="searchLink" title="AC" />
        <display:column property="editorLink" title="Short Label" />
        <display:column property="fullName" title="Full Name" />
    </display:table>
</html:form>
