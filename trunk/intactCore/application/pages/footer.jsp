<%@ page import="java.io.File,
                 java.util.Date"%>
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
        <i>Last Modified:
        <%
            String jspPath = application.getRealPath(request.getServletPath());
            // Check the cache first.
            Date lastMod = (Date) application.getAttribute(jspPath);
            if (lastMod == null) {
                // Cache the last mod value.
                File jspFile = new File(jspPath);
                lastMod = new Date(jspFile.lastModified());
                application.setAttribute(jspPath, lastMod);
            }
            out.println(lastMod);
        %>
        </i>
    </td>
    <td class="tableCell">
        <img border="0" align="right" src="<%=request.getContextPath()%>/images/struts-power.gif">
    </td>
</tr>
</table>
</span>