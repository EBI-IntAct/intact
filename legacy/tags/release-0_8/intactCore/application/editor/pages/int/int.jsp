<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - This page accepts changes to an Annotated object's short label and full name.
  --%>

<%@ page language="java"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>

<jsp:include page="../js.jsp" />

<html:form action="/intDispatch" focus="shortLabel">

    <jsp:include page="info.jsp" />
    <p></p>
    <jsp:include page="exps.jsp" />
    <p></p>
    <jsp:include page="expsHold.jsp" />
    <jsp:include page="expSearch.jsp" />
    <p></p>
    <jsp:include page="proteins.jsp" />
    <jsp:include page="proteinSearch.jsp" />
    <p></p>
    <jsp:include page="annots.jsp" />
    <jsp:include page="../addAnnots.jsp" />
    <jsp:include page="xrefs.jsp" />
    <jsp:include page="../addXrefs.jsp" />

    <p></p>
    <jsp:include page="../action.jsp" />

</html:form>
