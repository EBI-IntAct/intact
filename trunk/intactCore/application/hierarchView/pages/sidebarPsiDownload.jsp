<%@ page language="java" %>

<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - This layout displays the PSI download button.
   - This is displayed only if the user has already requested the display
   - of an interaction network.
   -
   - @author Samuel Kerrien (skerrien@ebi.ac.uk)
   - @version $Id$
-->

<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.IntactUserI,
                 uk.ac.ebi.intact.application.hierarchView.business.Constants,
                 java.util.Collection,
                 java.util.Iterator,
                 uk.ac.ebi.intact.model.Interactor,
                 java.util.ArrayList"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/intact.tld"      prefix="intact"%>

<%
    /**
     * Retreive user's data from the session
     */
    IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);

    if (user == null) {
        // no user in the session, don't display anything
        return;
    }
%>

<%
   if (user.InteractionNetworkReadyToBeDisplayed()) {
       String graph2mif = user.GRAPH2MIF_PROPERTIES.getProperty( "graph2mif.url" );
       StringBuffer ac = new StringBuffer( 32 );
       Iterator iterator = user.getInteractionNetwork().getCentralInteractors().iterator();
       while ( iterator.hasNext() ) {
           Interactor interactor = (Interactor) iterator.next();
           ac.append( interactor.getAc() ).append( "%2C" ); // %2C <=> ,
       }
       int l = ac.length();
       if (l > 0)
           ac.delete( l-3, l ); // the 3 last caracters (%2C)
       String url = graph2mif  + "?ac=" +  ac.toString()
                               + "&depth=" + user.getCurrentDepth()
                               + "&strict=false";
%>

<hr>

    <table width="100%">
        <tr>
          <th colspan="2">
             <div align="left">
                <strong><bean:message key="sidebar.psi.section.title"/></strong>
                <intact:documentation section="hierarchView.PPIN.download" />
             </div>
          </th>
        </tr>

        </tr>
            <td>
                <input type="button"
                       value="Download"
                       onClick="w=window.open('<%= url %>', 'graph2mif');w.focus();">
            </td>
        </tr>
    </table>

<%
   } // if InteractionNetworkReady
%>