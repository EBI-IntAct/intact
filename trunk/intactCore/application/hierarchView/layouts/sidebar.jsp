<%@ page language="java"%>

<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - The layout for the side bar, which consists of intact logo, input dialog box
   - and menu. Except for the logo, other two components are optional.
   -
   - @author Samuel Kerrien (skerrien@ebi.ac.uk)
   - @version $Id$
-->

<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles"%>

<table width="100%" border="0" cellspacing="0" cellpadding="0">

    <%-- Sidebar logo --%>
	<tr>
        <td bgcolor="#boc4de" valign="top" align="left" width="113" height="75">
            <img src="<%= request.getContextPath() %>/images/logo_intact.gif" border="0">
        </td>
    </tr>

    <!-- The input dialog box -->
    <tr>
        <td>
            <tiles:insert attribute="search" ignore="true"/>
        </td>
    </tr>

    <%-- Graph menu --%>
    <tr>
		<td>
            <tiles:insert attribute="graph" ignore="true"/>
        </td>
	</tr>

    <%-- Sidebar menu links --%>
    <tr>
		<td>
            <br>
            <hr>
            <tiles:insert attribute="menu" ignore="true"/>
        </td>
	</tr>

</table>