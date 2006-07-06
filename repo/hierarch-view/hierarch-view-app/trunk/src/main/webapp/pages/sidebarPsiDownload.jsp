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
                 uk.ac.ebi.intact.simpleGraph.BasicGraphI,
                 java.util.Collection,
                 java.util.Iterator,
                 java.util.ArrayList"%>
<%@ page import="org.apache.taglibs.standard.lang.jpath.encoding.HtmlEncoder"%>

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
       Iterator iterator = user.getInteractionNetwork().getCentralProteins().iterator();
       while ( iterator.hasNext() ) {
           BasicGraphI interactor = (BasicGraphI) iterator.next();
           
           ac.append( interactor.getAc() ).append( "%2C" ); // %2C <=> ,
       }
       int l = ac.length();
       if (l > 0)
           ac.delete( l-3, l ); // the 3 last caracters (%2C)
       String url = graph2mif  + "?ac=" +  ac.toString()
                               + "&depth=" + user.getCurrentDepth()
                               + "&strict=false";
       String url10 = url+"&version=1";
       String url25 = url+"&version="+ HtmlEncoder.encode("2.5");
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

        <tr>
            <td valign="bottom" align="center">
                    <nobr>
                        <a href="<%= url10 %>"><img border="0" src="<%= request.getContextPath() %>/images/psi10.png"
                                                                alt="PSI-MI 1.0 Download"
                                                                onmouseover="return overlib('Download data from publication in PSI-MI XML 1.0', DELAY, 150, TEXTCOLOR, '#FFFFFF', FGCOLOR, '#EA8323', BGCOLOR, '#FFFFFF');"
                                                                onmouseout="return nd();"></a>
                    </nobr>
             </td>
        </tr>
        <tr>
            <td valign="bottom" align="center">
                    <nobr>
                        <a href="<%= url25 %>"><img border="0" src="<%= request.getContextPath() %>/images/psi25.png"
                                                                 alt="PSI-MI 2.5 Download"
                                                                 onmouseover="return overlib('Download data from publication in PSI-MI XML 2.5', DELAY, 150, TEXTCOLOR, '#FFFFFF', FGCOLOR, '#EA8323', BGCOLOR, '#FFFFFF');"
                                                                 onmouseout="return nd();"></a>
                    </nobr>
             </td>

        </tr>
    </table>

<%
   } // if InteractionNetworkReady
%>