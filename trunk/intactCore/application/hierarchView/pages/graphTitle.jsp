<%@ page language="java" %>

<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - hierarchView graph title page
   - This should be displayed in the content part of the IntAct layout,
   - it displays the interaction network title.
   -
   - @author Samuel Kerrien (skerrien@ebi.ac.uk)
   - @version $Id$
-->

<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.IntactUserI,
                 uk.ac.ebi.intact.application.hierarchView.business.Constants,
                 uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork" %>

<%
   /**
    * Retreive user's data from the session
    */
   IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);
    if (user == null) return;
   InteractionNetwork in = user.getInteractionNetwork();
    if (in == null) return ;
%>

<table border="0" cellspacing="3" cellpadding="3" width="100%" heigth="100%">

      <tr>
             <td>
                   <!-- displays the interaction network -->
                   Interaction network for AC: <%= user.getAC() %>.
                   <br>
                   #nodes:<%= in.sizeNodes() %>  #edges:<%= in.sizeEdges() %>
             </td>
      </tr>

</table>