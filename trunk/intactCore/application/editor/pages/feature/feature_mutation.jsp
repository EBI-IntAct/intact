<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - Assembles various JSPs to present the Feature editor.
  --%>

<%@ page language="java"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>

<jsp:include page="../js.jsp" />

<%-- Include javascript for show user functionality --%>
<jsp:include page="../misc/user_js.jsp"/>

<html:form action="/featureDispatch" onsubmit="return validateFeatureForm(this)">
    <html:hidden property="anchor" />
    <%-- This is a hack to get through with the validation as mutation form has
         no short label
    --%>
    <html:hidden property="shortLabel" value="xy"/>

    <jsp:include page="parent_protein.jsp" />
    </p>
    <jsp:include page="new_feature.jsp" />
    </p>

    <p></p>
    <jsp:include page="../action.jsp" />

</html:form>
<html:javascript formName="featureForm"/>