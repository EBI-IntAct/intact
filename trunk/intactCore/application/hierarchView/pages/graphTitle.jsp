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
                 java.util.StringTokenizer,
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
    StringBuffer contextToDisplay = new StringBuffer();
    if ((max = context.length()) > 0) {
       	Object network = session.getAttribute("network");
		Object singletons = session.getAttribute("singletons");
    	String tmp = context.substring (0, max-2);
    	
    	if("null".equals(network) && "null".equals(singletons)) {
	        contextToDisplay.append("Interaction network for ");
    	    contextToDisplay.append(tmp).append("<br>");
        }
        else {
			String net = "This is the minimal connecting network for ";
			
    	    StringTokenizer tokens = new StringTokenizer(network.toString(), ",");
        	int[] borders = new int[tokens.countTokens()];
        	int i = 0;
        	String tok;
	        while(tokens.hasMoreTokens()) {
    	      borders[i++] = Integer.parseInt(tokens.nextToken());
        	}

        	tokens = new StringTokenizer(tmp, ",");
			contextToDisplay.append(net);
			i = 1;
			int j = 0;
			while(tokens.hasMoreTokens()) {
				contextToDisplay.append(tokens.nextToken());
				if(i == borders[j]) {
				   if(tokens.hasMoreTokens()) {
				     contextToDisplay.append("<br>" + net);
				   }
				   j++;
				}
				i++;
			}
			if(!"null".equals(singletons)) {
				contextToDisplay.append("<br>The following proteins are not in "+
									"a connecting network: "); 
				tokens = new StringTokenizer(singletons.toString(),",");
				while(tokens.hasMoreTokens()) {
				  tok = tokens.nextToken().trim();
				  contextToDisplay.append(prefix + "shortLabel: ");
	              contextToDisplay.append("<a href=\"" + user.getSearchUrl(tok, false) + "\"");
	              contextToDisplay.append(" target=\"_blank\">" + tok + "</a>");
        		  contextToDisplay.append(suffix + " ");
        		}
			}
		}
		session.setAttribute("network", "null");
		session.setAttribute("singletons", "null");
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
			 <%= contextToDisplay.toString() %>
             </td>
      </tr>

</table>