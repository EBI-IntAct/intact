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
                 uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork,
                 java.util.Collection,
                 java.util.Iterator,
                 java.util.ArrayList,
                 uk.ac.ebi.intact.application.commons.search.CriteriaBean" %>

<%
    /**
     * Retreive user's data from the session
     */
    IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);
    if (user.getSearchUrl() == null) return ;

    InteractionNetwork in = user.getInteractionNetwork();
    if (in == null) return ;

    String prefix = "<b>";
    String suffix = "</b>";

    ArrayList criterias = in.getCriteria();
    StringBuffer context = new StringBuffer(512);
    for ( Iterator iterator = criterias.iterator (); iterator.hasNext (); ) {
        CriteriaBean aCriteria = (CriteriaBean) iterator.next ();

        context.append (prefix + aCriteria.getTarget() + ": ");
        context.append ("<a href=\"" + user.getSearchUrl(aCriteria.getQuery(), false) + "\" target=\"_blank\">" +
                        aCriteria.getQuery() + "</a>");
        context.append (suffix + ", ");
    }

    int max = criterias.size();
    // remove the last comma and white space
    String contextToDisplay = "";
    if ((max = context.length()) > 0) {
        contextToDisplay = context.substring (0, max-2);
    }

    String selectedKey = user.getSelectedKey();
    if (selectedKey == null) selectedKey = "";
    else {
        selectedKey = "<br>Highlight by " + prefix + selectedKey + suffix;
    }

%>

<table border="0" cellspacing="3" cellpadding="3" width="100%" heigth="100%">

      <tr>
             <td>
                   <!-- displays the interaction network -->
                   Interaction network for <%= contextToDisplay %>

                   <!-- display the highlight context -->
                   <%= selectedKey %>
                   <br>
                   #nodes:<b><%= in.sizeNodes() %></b>  #edges:<b><%= in.sizeEdges() %></b>
             </td>
      </tr>

</table>