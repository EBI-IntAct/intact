<%@ page language="java" %>

<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - @author Samuel Kerrien (skerrien@ebi.ac.uk)
   - @version $Id$
-->

<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.IntactUserI,
                 uk.ac.ebi.intact.application.hierarchView.business.Constants"%>

<%@ taglib uri="/WEB-INF/tld/hierarchView.tld" prefix="hierarchView" %>

<%-- hierarchView seleted source page

     This should be displayed in the content part of the IntAct layout,
     it displays the highlightment sources.

     author : Samuel Kerrien (skerrien@ebi.ac.uk)
 --%>

<%
   /**
    * Retreive user's data from the session
    */
   IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);
%>


<!-- Displays Http content if URL updated in the session -->
<%
    if (user.hasSourceUrlToDisplay()) {
        out.print("<!-- "+ user.getSourceURL() +" -->");
        response.sendRedirect (user.getSourceURL());
%>
        <!--hierarchView:displayHttpContent/-->
<%
    }
%>
