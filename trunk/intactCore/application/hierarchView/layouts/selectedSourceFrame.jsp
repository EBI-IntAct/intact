<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles"%>

<%--
    Content of the seleted source frame, it rely on the Tiles configuration.

    Author: Samuel Kerrien (skerrien@ebi.ac.uk)
    Version: $Id$
--%>

<%--   <tiles:insert attribute="hierarchView.seletedSource.layout" ignore="true"/>--%>
<!-- Before insert definition -->
<tiles:insert definition="hierarchView.selectedSource.layout" ignore="true"/>
