<%@ page language="java"%>

 <%--
    This page is displayed when a search failes to return any matching record.
    Author Chris Lewington, Sugath Mudali (smudali@ebi.ac.uk)
    Version $Id$
--%>

<jsp:useBean id="intactuser" scope="session"
    class="uk.ac.ebi.intact.application.cvedit.business.IntactUserImpl"/>

<%
    String info = intactuser.getLastSearchQuery();
%>

<h1>Search Results: No Matches!</h1>

<h2>Sorry - could not find any <%= intactuser.getLastSearchClass() %>
  by trying to match <%= info.substring(info.indexOf('=') + 1) %> with: </h2>
<ul>
  <li>AC,
  <li>short label,
  <li>xref Primary ID or
  <li>a full name.
</ul>

<h2>Please try again!</h2>
