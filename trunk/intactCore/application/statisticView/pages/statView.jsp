<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - That page is displaying some statistics about the curent intact node.
   -
   - @author Sophie Huet (shuet@ebi.ac.uk), Samuel Kerrien (skerrien@ebi.ac.uk)
   - @version $Id$
-->

<%@ page language="java"%>
<%@ page import="java.util.Vector,
                 java.util.Iterator,
                 java.util.Collection,
                 uk.ac.ebi.intact.application.statisticView.business.data.DataManagement,
                 java.util.ArrayList,
                 uk.ac.ebi.intact.business.IntactException,
                 uk.ac.ebi.intact.application.statisticView.business.servlet.StatBean"%>

<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/display.tld"     prefix="display" %>
<%@ taglib uri="/WEB-INF/tld/intact.tld"      prefix="intact" %>



<%
    String applicationPath = request.getContextPath();
    DataManagement dat = new DataManagement();
    boolean noData = false;
    String lastDate = "";

    try {
        lastDate = dat.getLastTimestamp();
    } catch (IntactException ie) {
        noData = true;
    }
%>

<table width="100%" height="100%">
    <tbody>

        <tr>
            <td valign="top" height="*">
            <br>
           <%
            if (noData) {
               out.println("Sorry, right now there is no statistics available in your IntAct node.");
               out.println("</td></tr></table>");
               return; // terminate right here the execution of the JSP.
            }
           %>

            <bean:message key="stat.present.content.string"/>
            <%= lastDate %>

            <br>
            </td>
        </tr>

        <tr>
            <td valign="top" height="*">

<%


    ArrayList rows = new ArrayList();
    try {
        Collection listDataTable = dat.getLastStatistics();
        Iterator iterator = listDataTable.iterator();

        rows.add (new StatBean ("Proteins",
                                ((Integer) iterator.next()).intValue(),
                                "Number of proteins in the database."));

        rows.add (new StatBean ("Interactions",
                                ((Integer) iterator.next()).intValue(),
                                "Number of distinct interactions"));

        rows.add (new StatBean ("thereof with 2 interactors",
                                ((Integer) iterator.next()).intValue(),
                                "Binary interactions"));

        rows.add (new StatBean ("thereof with more than 2 interactors",
                                ((Integer) iterator.next()).intValue(),
                                "Interactions with more than two interactors (complexes)."));

        rows.add (new StatBean ("Experiments",
                                ((Integer) iterator.next()).intValue(),
                                "Distincts experiments."));

        rows.add (new StatBean ("Terms",
                                ((Integer) iterator.next()).intValue(),
                                "Controlled vocabulary terms define possible choices for text attributes."));

        session.setAttribute("statistics", rows);
        dat.closeDataStore();
%>

       <!-- Displays the available highlightment source -->
       <display:table
            name="statistics" width="70%">
               <display:column property="object" title="Object" width="22% "/>
               <display:column property="count"                 width="8%" />
               <display:column property="description"           width="70%" />

               <display:setProperty name="basic.msg.empty_list"
                                    value="No statistics available..." />
       </display:table>


      <%
        // will be added to each servlet URL to avoid browser caching
        long time = System.currentTimeMillis();
      %>

            </td>
        </tr>

        <tr>
            <td>
                <br><br>
                <hr size="2">
                <br><br>
            </td>
        </tr>

        <tr>
            <td valign="top" height="*">
                <table width="100%" height="100%">
                    <tbody>
                        <tr>
                            <td valign="top" height="*">

                                <img src="<%= applicationPath %>/statisticGraphic?TYPE=Protein&time=<%= time %>">

                            </td>
                            <td valign="top" height="*">
                                <img src="<%= applicationPath %>/statisticGraphic?TYPE=Interaction&time=<%= time %>">
                            </td>
                        </tr>

                        <tr>
                            <td valign="top" height="*">
                                <img src="<%= applicationPath %>/statisticGraphic?TYPE=Experiment&time=<%= time %>">
                            </td>
                            <td valign="top" height="*">
                                <img src="<%= applicationPath %>/statisticGraphic?TYPE=Term&time=<%= time %>">
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>

    <%
   } catch (IntactException ie) {
        out.println ("Error when trying to get the latest statistics.");
   }
 %>

   </tbody>
</table>
