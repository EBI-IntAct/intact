<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - This should be displayed in the content part of the IntAct layout,
   - it displays the Srs Search text field where the user should capture
   - a protein reference or a protein word.
   -
   - @author ${User} (shuet@ebi.ac.uk)
   - @version $Id$
-->

<%@ page language="java"%>
<%@ page import="java.util.Vector,
                 java.util.Iterator,
                 java.util.Collection,
                 uk.ac.ebi.intact.application.statisticView.business.data.DataManagement,
                 java.util.ArrayList"%>

<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>



<%
    String applicationPath = request.getContextPath();
%>

<table width="100%" height="100%">
    <tbody>

        <tr>
            <td valign="top" height="*">
            <br>
            <bean:message key="stat.present.content.string"/>
            <%
                DataManagement dat = new DataManagement();
                String lastDate = dat.getLastTimestamp();
            %>
            <%=lastDate%>
            <br>
            </td>
        </tr>

        <tr>
            <td valign="top" height="*">
                <table width="80%" border="1" cellspacing="0" cellpadding="1">
                    <tr class="tableRowHeader">
                        <th class="tableCellHeader" width="35%">Object</th>
                        <th class="tableCellHeader" width="15%">Number</th>
                        <th class="tableCellHeader" width="50%">Description</th>
                    </tr>

<%
    ArrayList leftColumnName = new ArrayList();
    leftColumnName.add("Proteins");
    leftColumnName.add("Interactions");
    leftColumnName.add("thereof with 2 interactors");
    leftColumnName.add("thereof with more than 2 interactors");
    leftColumnName.add("Experiments");
    leftColumnName.add("Terms");

    ArrayList rightColumnName = new ArrayList();
    rightColumnName.add("Number of proteins in the database.");
    rightColumnName.add("Number of distinct interactions.");
    rightColumnName.add("Binary interactions.");
    rightColumnName.add("Interactions with more than two interactors (complexes).");
    rightColumnName.add("Distincts experiments.");
    rightColumnName.add("Controlled vocabulary terms define possible choices for text attributes.");

    Collection listDataTable = dat.getLastStatistics();
    int j = 0;
    for (Iterator iterator = listDataTable.iterator(); iterator.hasNext();  ) {
        Integer i = (Integer) iterator.next();
        String objectColumnName = (String) leftColumnName.get(j);
        String descriptionColumnName = (String) rightColumnName.get(j);

%>
                    <tr class="tableRowHeader">

                        <td align="right">
                            <%= objectColumnName %>
                        </td>
                        <td  align="right">
                            <font size="2"> <%= i %> </font>
                        </td>
                        <td align="left">
                            <%= descriptionColumnName %>
                        </td>

                    </tr>
<%
        j++;
    }
%>

                </table>
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
                                <img src="<%= applicationPath %>/statisticGraphic?TYPE=Protein">
                            </td>
                            <td valign="top" height="*">
                                <img src="<%= applicationPath %>/statisticGraphic?TYPE=Interaction">
                            </td>
                        </tr>

                        <tr>
                            <td valign="top" height="*">
                                <img src="<%= applicationPath %>/statisticGraphic?TYPE=Experiment">
                            </td>
                            <td valign="top" height="*">
                                <img src="<%= applicationPath %>/statisticGraphic?TYPE=Term">
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </tr>

    </tbody>
</table>
