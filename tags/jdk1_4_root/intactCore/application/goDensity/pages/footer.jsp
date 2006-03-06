<%@ page language="java"%>

<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - The common footer for all the Intact web applications.
--%>

<hr>
<span class="footer">
<table width="100%">
<tr>
    <td>
        Please send any questions or suggestions to
            <a href="mailto:Intact-developers@lists.sourceforge.net">Intact-developers@lists.sourceforge.net</a>
        </br>
        Last modified: <i><%@ include file="../last-modified.txt" %></i>
    </td>
    <td class="tableCell">
        <img border="0" align="right" src="<%=request.getContextPath()%>/images/struts-power.gif">
    </td>
</tr>
</table>
</span>