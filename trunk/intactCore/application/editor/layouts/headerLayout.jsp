<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
   - Header layout for the Editor.
  --%>

<%@ page language="java"%>

<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tld/editor.tld" prefix="editor"%>

<span class="header">
    <tiles:getAsString name="header.title"/>
</span>

<div style="text-align: right;">
    <editor:helpLink tag="toc" title="Help Topics"/>
    <editor:helpLink tag="faq" title="FAQ"/>
    <br>
</div>
<hr>