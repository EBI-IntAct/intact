<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - This page displays the defined features for a feature.
  --%>

<%@ page language="java"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/display" prefix="display" %>

<script language="JavaScript" type="text/javascript">
    // Set the hidden interaction field when the user clicks on Auto-complete.
    function setDefinedFeature(label) {
        document.forms["featureForm"].definedFeature.value=label;
    }
</script>


<h3>Defined features</h3>

<%-- User to get the view --%>
<jsp:useBean id="user" scope="session"
    class="uk.ac.ebi.intact.application.editor.business.EditUser"/>

<%-- The service to access the number of itmes to display per page --%>
<jsp:useBean id="service" scope="application"
    class="uk.ac.ebi.intact.application.editor.business.EditorService"/>

<%-- The current view --%>
<bean:define id="view" name="user" property="view"
    type="uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureViewBean"/>

<%-- Store in the page scope for the display library to access it --%>
<bean:define id="features" name="view" property="definedFeatures"
    type="java.util.List"/>

<%-- Number of features allowed (equals to ints) to display per page --%>
<bean:define id="pageSize" name="service" property="interactionPageLimit"
    type="java.lang.String"/>

<%
    String uri = request.getContextPath() + "/do/next";
%>

<display:table width="100%" name="features" pagesize="2"
    requestURI="<%=uri%>"
    decorator="uk.ac.ebi.intact.application.editor.struts.view.feature.DefinedFeatureDisplayWrapper">
    <display:column property="action" title="Action" />
    <display:column property="shortLabel" title="Short Label"/>
    <display:column property="ranges" title="Range" />
    <display:column property="fullName" title="Full Name" />
    <display:column property="source" title="Source" />
</display:table>

</p>
<html:submit titleKey="feature.clone.button.titleKey" property="dispatch">
    <bean:message key="feature.clone.button"/>
</html:submit>

<%-- The text box to accept the clone AC --%>
<html:text property="cloneAc" size="15" maxlength="15" name="featureForm"/>

<%-- Error messages; need this check to display the error in the next line --%>
<html:messages id="error">
    </br>
    <html:errors property="featureClone"/>
</html:messages>
