<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - The look & feel layout for editing a CV object.
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  --%>

<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles"%>
<%-- The plugin for editor specific info. --%>
<tiles:insert attribute="interaction-info"/>

<%-- The experiments --%>
<tiles:insert attribute="experiments"/>

<%-- The experiments (on hold)--%>
<tiles:insert attribute="experiments-hold"/>

<%-- The experiments (search) --%>
<tiles:insert attribute="experiments-search"/>

<%-- The proteins --%>
<tiles:insert attribute="proteins"/>

<%-- The protein search --%>
<tiles:insert attribute="proteinSearch"/>
