<%@ page language="java"%>

<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - The footer for miscellaneous the Editor popup windows.
--%>

<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>

<hr>
<span class="footer">
<table width="100%">
<tr>
    <td>
        Tip of the day: While curating please use the 'Save & Continue' button frequently.
        <br/>
        <%-- commented during the reorg to maven
        Last modified: <i><%@ include file="../last-modified.txt" %></i>
        <br/>
        --%>
         Version: <i><bean:message bundle="buildInfo" key="build.version"/></i> (Core: <i><bean:message bundle="buildInfo" key="core.version"/></i>, Bridges: <i><bean:message bundle="buildInfo" key="bridges.version"/></i>) -&nbsp;
        Build: <bean:message bundle="buildInfo" key="buildNumber"/> (<bean:message bundle="buildInfo" key="builtBy"/>)  <%=request.getSession().getId()%>
    </td>
    <td class="tableCell">
        <img border="0" align="right" src="<%=request.getContextPath()%>/images/struts-power.gif">
    </td>
</tr>
</table>
</span>