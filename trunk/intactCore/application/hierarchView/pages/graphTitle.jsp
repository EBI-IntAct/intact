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
                 java.util.Iterator" %>

<%
    /**
     * Retreive user's data from the session
     */
    IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);
    if (user == null) return ;

    InteractionNetwork in = user.getInteractionNetwork();
    if (in == null) return ;

    String selectedKey = user.getSelectedKey();
    String keys = "";
    String prefix = "<b>";
    String suffix = "</b>";
    if (selectedKey != null) {
        Collection c = user.getKeys();

        if (c != null && !c.isEmpty()) {
            c.remove (selectedKey);

            Iterator i = c.iterator();
            StringBuffer sb = new StringBuffer();
            String separator = ", ";

            while (i.hasNext()) {
                sb.append(prefix).append((String)i.next()).append(suffix).append(separator);
            }

            if (sb.length() > 0) {
                sb.insert (0,", children term : ");
                keys = sb.substring(0, sb.length()-separator.length());
            } else {
                keys = "";
            }
        }
    }

    if (selectedKey == null) selectedKey = "";
    else {
        selectedKey = "<br>Highlight by " + prefix + selectedKey + suffix;
    }
%>

<table border="0" cellspacing="3" cellpadding="3" width="100%" heigth="100%">

      <tr>
             <td>
                   <!-- displays the interaction network -->
                   Interaction network for AC: <b><%= user.getAC() %></b>
                   <!-- display the highlight context -->
                   <%= selectedKey %><%= keys %>
                   <br>
                   #nodes:<b><%= in.sizeNodes() %></b>  #edges:<b><%= in.sizeEdges() %></b>
             </td>
      </tr>

</table>