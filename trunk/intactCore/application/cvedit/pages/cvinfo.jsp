<%@ page language="java"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<%--
    Presents information for the CV object.
    Author: Sugath Mudali (smudali@ebi.ac.uk)
    Version: $Id$
--%>

<jsp:useBean id="intactuser" scope="session"
    class="uk.ac.ebi.intact.application.cvedit.business.IntactUserImpl"/>

<c:set var="viewbean" value="${intactuser.view}"/>

Topic: <b><c:out value="${viewbean.topic}"/></b>
&nbsp;&nbsp;AC: <b><c:out value="${viewbean.ac}"/></b>
&nbsp;&nbsp;Short Label: <b><c:out value="${viewbean.shortLabel}"/></b>
