<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - The second panel consists of host organism and 
  --%>

<%@ page language="java"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>

<jsp:include page="../js.jsp" />

<%-- Include javascript for show user functionality --%>
<jsp:include page="../misc/user_js.jsp"/>

<html:form action="/seq/dispatch" onsubmit="return validateSeqForm(this)">
    <html:hidden property="anchor" />

    <jsp:include page="info.jsp" />
    <jsp:include page="../annots.jsp" />
    <jsp:include page="../addAnnots.jsp" />
    <jsp:include page="../xrefs.jsp" />
    <jsp:include page="../addXrefs.jsp" />

    <p></p>
    <jsp:include page="../action.jsp" />

</html:form>
<html:javascript formName="seqForm"/>