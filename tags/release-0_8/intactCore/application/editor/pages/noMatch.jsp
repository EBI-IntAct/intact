<!--
  - Author: Chris Lewington, Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - This page is displayed when a search failes to return any matching record.
  --%>

<%@ page language="java"%>

<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<%
    String info = user.getSearchQuery();
%>

<h1>Search Results: No Matches!</h1>

<h2>Sorry - could not find any <%= user.getSelectedTopic() %>(s)
  by trying to match <span style="color: red;"><%= info.substring(info.indexOf('=') + 1) %></span> with: </h2>
<ul>
  <li>AC,
  <li>short label,
  <li>xref Primary ID or
  <li>a full name.
</ul>

<h2>Please try again!</h2>
