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
                 java.util.ArrayList,
                 uk.ac.ebi.intact.business.IntactException,
                 uk.ac.ebi.intact.util.IntactStatistics,
                 java.io.PrintWriter,
                 uk.ac.ebi.intact.application.statisticView.business.graphic.ChartBuilder,
                 uk.ac.ebi.intact.application.statisticView.business.data.NoDataException,
                 uk.ac.ebi.intact.application.statisticView.business.data.StatisticsBean,
                 uk.ac.ebi.intact.application.statisticView.business.data.DisplayStatisticsBean,
                 uk.ac.ebi.intact.util.StatisticsDataSet,
                 uk.ac.ebi.intact.application.statisticView.business.Constants"%>

<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/display.tld"     prefix="display" %>

<%@ taglib uri="/WEB-INF/tld/intact.tld"      prefix="intact" %>

<%
    ////////////////////////
    // Collect parameters
    ////////////////////////
    final String start = request.getParameter( "start" );
    final String stop  = request.getParameter( "stop" );

    StatisticsBean bean = null;
    try {
        bean = StatisticsDataSet.getInstance( Constants.LOGGER_NAME ).getStatisticBean(start, stop);
    } catch (NoDataException nde) {
        // forward to an error page.
    }
    boolean noData = false;
    String lastDate = "";

    try {
        lastDate = bean.getMoreRecentStatisticsDate();
    } catch (IntactException ie) {
        ie.printStackTrace();
        noData = true;
    }
%>

<table width="100%" height="100%">
<tbody>

<tr>
<td valign="top" height="*">

<!--
        Comment for developpers.
        This is the Database from which we extracted the data.
        Database: <%= bean.getDatabaseName() %>
-->

            <%
                if (noData) {
                    out.println("Sorry, right now there is no statistics available in your IntAct node.");
                    out.println("</td></tr></table>");
                    return; // terminate right here the execution of the JSP.
                }
            %>

            <bean:message key="stat.present.content.string"/>
            <b><%= lastDate %></b>.

            <br>
            </td>
            </tr>

            <tr>
            <td valign="top" height="*">

<%
    ArrayList rows = new ArrayList( 5 );
    IntactStatistics latest = bean.getLastRow();

    rows.add (new DisplayStatisticsBean ("Proteins",
                                        latest.getNumberOfProteins(),
                                        "Number of proteins in the database"));

    rows.add (new DisplayStatisticsBean ("Interactions",
                                        latest.getNumberOfInteractions(),
                                        "Number of binary interactions and complexes"));

    rows.add (new DisplayStatisticsBean ("Binary interactions",
                                        latest.getNumberOfBinaryInteractions(),
                                        "Number of binary interactions, n-ary interactions expanded according to the \"spoke\" model"));

    rows.add (new DisplayStatisticsBean ("Experiments",
                                        latest.getNumberOfExperiments(),
                                        "Distinct experiments"));

    rows.add (new DisplayStatisticsBean ("Terms",
                                        latest.getNumberOfGoTerms(),
                                        "Controlled vocabulary terms"));

    request.setAttribute("statistics", rows);
%>

       <!-- Displays the available highlightment source -->
       <display:table name="statistics" width="80%">
               <display:column property="object" title="Object" width="22%" styleClass="tableCellAlignRight" />
               <display:column property="count"                 width="8%"  styleClass="tableCellAlignRight" />
               <display:column property="description"           width="70%"  />

               <display:setProperty name="basic.msg.empty_list"
                                    value="No statistics available..." />
       </display:table>

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
            <tr>
            <td valign="top" height="*">

            <%
            String filename = ChartBuilder.generateXYChart( bean,
                                                            ChartBuilder.ALL_INTERACTIONS_DATA,
                                                            "IntAct interactions","","",
                                                            session,
                                                            new PrintWriter( out ));
            String graphURL = request.getContextPath() + "/servlet/DisplayChart?filename=" + filename;
            %>

            <img src="<%= graphURL %>" width=500 height=300 border=0 usemap="#<%= filename %>">

            </td>
            </tr>
            <tr>
                <td valign="top" height="*">

                    <%
                        filename = ChartBuilder.generateXYChart( bean,
                                                                 ChartBuilder.BINARY_INTERACTIONS_DATA,
                                                                 "IntAct binary interactions","","",
                                                                 session,
                                                                 new PrintWriter( out ));
                        graphURL = request.getContextPath() + "/servlet/DisplayChart?filename=" + filename;
                    %>

                    <img src="<%= graphURL %>" width=500 height=300 border=0 usemap="#<%= filename %>">

                </td>
            </tr>

            <tr>
                <td valign="top" height="*">

                    <%
                        filename = ChartBuilder.generateXYChart( bean,
                                                                 ChartBuilder.EXPERIMENT_DATA,
                                                                 "IntAct experiments","","",
                                                                 session,
                                                                 new PrintWriter( out ));
                        graphURL = request.getContextPath() + "/servlet/DisplayChart?filename=" + filename;
                    %>

                    <img src="<%= graphURL %>" width=500 height=300 border=0 usemap="#<%= filename %>">

                </td>
            </tr>

            <tr>
                <td valign="top" height="*">

                    <%
                        filename = ChartBuilder.generateXYChart( bean,
                                                                 ChartBuilder.PROTEIN_DATA,
                                                                 "IntAct proteins","","",
                                                                 session,
                                                                 new PrintWriter( out ));
                        graphURL = request.getContextPath() + "/servlet/DisplayChart?filename=" + filename;
                    %>

                    <img src="<%= graphURL %>" width=500 height=300 border=0 usemap="#<%= filename %>">

                </td>
            </tr>

            <tr>
                <td valign="top" height="*">

                    <%
                        filename = ChartBuilder.generateXYChart( bean,
                                                                 ChartBuilder.TERM_DATA,
                                                                 "IntAct controlled vocabulary terms","","",
                                                                 session,
                                                                 new PrintWriter( out ));
                        graphURL = request.getContextPath() + "/servlet/DisplayChart?filename=" + filename;
                    %>

                    <img src="<%= graphURL %>" width=500 height=300 border=0 usemap="#<%= filename %>">

                </td>
            </tr>
        </table>
    </td>
    </tr>

    </tbody>
</table>
