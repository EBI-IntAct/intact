<%--
    Page to display a search summary view (used to be the 'binary' view). Current
    specification is as per the mockups June 2004. This page is typically used when a
    Protein is searched for - a summary of the relevant information (eg Interactions
    etc) is displayed.

    @author Chris Lewington
    @version $Id$
--%>

<!-- need to provide an error page here to catch unhandled failures -->
<%@ page language="java"  %>
<%@ page buffer="none"    %>
<%@ page autoFlush="true" %>

<!-- Intact classes needed -->
<%@ page import="uk.ac.ebi.intact.application.search3.struts.framework.util.SearchConstants,
                 uk.ac.ebi.intact.application.search3.business.IntactServiceIF,
                 uk.ac.ebi.intact.application.search3.struts.view.beans.PartnersViewBean"%>

<!-- Standard Java classes -->
<%@ page import="java.util.*"%>

<!-- may make use of these later to tidy up the JSP a little -->
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

<%
    // To allow access hierarchView properties. Used only by the javascript.
    IntactServiceIF service = (IntactServiceIF) application.getAttribute(
            SearchConstants.INTACT_SERVICE);

    //build the absolute path out of the context path for 'search'
    String ctxtPath = (request.getContextPath());
    String relativePath = ctxtPath.substring(0, ctxtPath.lastIndexOf("search"));

    //build the URL for hierarchView from the absolute path and the relative beans..
    String hvPath = relativePath.concat(service.getHierarchViewProp("hv.url"));
    String minePath = relativePath.concat("mine/display.jsp");

    //The List of view beans used to provide the data for this JSP. Each
    //bean in the List should be an instance of SummaryViewBean, and corresponds to
    //a single search result.
    List viewBeans = (List)session.getAttribute(SearchConstants.VIEW_BEAN_LIST);

    //the list of shortlabels for the search matches - need to be highlighted
    //NB the SearchAction ensures this will never be null
    List highlightList = (List)request.getAttribute(SearchConstants.HIGHLIGHT_LABELS_LIST);
%>

<%-- The javascript for the button bars.... --%>
<%@ include file="jscript.html" %>

<h3>Search Results for
    <%=session.getAttribute(SearchConstants.SEARCH_CRITERIA) %>
</h3>

<span class="smalltext">(short labels of search criteria matches are
    <span style="color: rgb(255, 0, 0);">highlighted</span>
</span><span class="smalltext">)<br></span></p>

<!-- the main form for the page -->
<form name="viewForm">


<!-- we need the buttons at the top as well as the bottom now -->
<%@ include file="buttonBar.html" %>


<!-- line seperator -->
    <hr size="2">


    <!-- NB the display is basically one table definition, repeated for each Protein
    search match ..... -->

    <!-- interate through the viewbean List and display each one in a new table... -->

    <%
        for(Iterator it = viewBeans.iterator(); it.hasNext();) {

            Object item = it.next();
            if(item instanceof PartnersViewBean) {
                //OK to carry one - otherwise skip the bean
                PartnersViewBean bean = (PartnersViewBean)item;
    %>
    <!-- the main data table -->
    <table style="width: 100%; background-color: rgb(51, 102, 102);">
        <tbody>

            <!-- header row -->
            <tr>
                <!-- padding cell -->
                <td class="headermid"><br>
                </td>

                <td class="headerlight"
                    rowspan="1" colspan="1">IntAct name<br>
                </td>

                <td class="headerlight"
                    colspan="1">IntAct Ac<br>
                </td>

                <td class="headerlight"
                    colspan="1"><span style="color: rgb(0, 0, 0);">Number of</span>
                    interactions<br>
                </td>

                <td class="headerlight"
                    colspan="1">UniProt Ac<br>
                </td>

                <td class="headerlight"
                    colspan="1">Gene name<br>
                </td>

                <td class="headerlight">
                Description<br>
                </td>
            </tr>


            <!-- first row of data (ie the Protein search match) -->
            <tr bgcolor="#eeeeee">

                <!-- checkbox: NB search results to be checked by default -->
                <td class="headermid">
                    <code><input type="checkbox" name="<%= bean.getAc()%>" checked></code>
                </td>

                <!-- shortlabel with link: seems to be back to this page (!!)... -->
                <td class="objectClass" style="background-color: rgb(255, 255, 255);">
                    <code><a href="<%= bean.getSimpleSearchURL()%>">
                        <% if(highlightList.contains(bean.getIntactName())) { %>
                            <b><span style="color: rgb(255, 0, 0);"><%= bean.getIntactName()%></span></b>
                        <%
                            }
                            else {
                                //no highlighting
                        %>
                            <%= bean.getIntactName()%>
                        <%
                            }
                        %>
                    </a></code>
                </td>

                <!-- AC, with link to single Protein details page -->
                <td style="background-color: rgb(255, 255, 255);">
                    <a href="<%= bean.getProteinSearchURL()%>"><%= bean.getAc()%></a><br>
                </td>

                <!-- number of Interactions, link to a'simple' result page for the Interactions
                -->
                <%--
                    ISSUE: The mockup says to go to 'detail-blue', but this is clearly
                    unworkable for even moderate Interaction lists across multiple Experiments,
                    because the 'detail-blue' view will contain far too much information.
                    DECISION: link to the 'front' search page instead, then users can choose
                    what detail they want.
                --%>
                <td style="background-color: rgb(255, 255, 255);">
                    <a href="<%= bean.getInteractionsSearchURL()%>"><%= bean.getNumberOfInteractions()%></a><br>
                </td>

                <!-- Uniprot AC, not linked -->
                <td rowspan="1" style="background-color: rgb(255, 255, 255);">
                    <%= bean.getUniprotAc()%><br>
                </td>

                <!-- gene name, not linked -->
                <td style="vertical-align: top; background-color: rgb(255, 255, 255);"
                    rowspan="1" colspan="1">
                    <%= bean.getGeneNames()%><br>
                </td>

                <!-- description, not linked -->
                <td style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <%= bean.getDescription()%><br>
                </td>

            </tr>


            <!-- seperating row, containing the 'interacts with' header -->
            <tr bgcolor="#eeeeee">

                <!-- single cell padding -->
                <td class="headerlight"><br>
                </td>

                <!-- heading, spans the table width (6 columns) -->
                <td rowspan="1" colspan="6" style="background-color: rgb(255, 255, 255);">
                    interacts with<br>
                </td>

            </tr>

            <!-- partner rows:
                NB: Each interaction partner needs to be displayed with a summary
                viewbean format itself - we can get a Set of view beans from the
                main view bean for the search result.

            -->
            <%
                Set partners = bean.getInteractionPartners();
                for(Iterator iter = partners.iterator(); iter.hasNext();) {
                    PartnersViewBean partner = (PartnersViewBean)iter.next();
            %>
            <tr>

                <!-- checkbox -->
                <td class="headermid">
                    <code><input type="checkbox" name="<%= partner.getAc()%>"></code>
                </td>

                <!-- shortlabel, linked back to this view for the partner instead -->
                <td style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <code><a href="<%= partner.getProteinPartnerURL()%>"><nobr><%= partner.getIntactName() %></nobr></a></code>
                </td>

                <!-- AC, linked to single Protein details page -->
                <td style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <a href="<%= partner.getProteinSearchURL()%>"><%= partner.getAc()%></a><br>
                </td>

                <!-- number of Interactions, linked new detail page -->
                <td style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <a href="<%= partner.getInteractionsSearchURL()%>"><%= partner.getNumberOfInteractions() %></a>
                </td>

                <!-- Uniprot AC, BUT:
                    The mockup says the FIRST partner ONLY is linked to Uniprot (TBD),
                    whilst the others are not. Is this correct? If so then how is
                    the order of partners determined?
                    -->
                <td style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <a href="<%= partner.getUniprotURL()%>"><%= partner.getUniprotAc() %></a>
                </td>

                <!-- gene name, not linked -->
                <td style="vertical-align: top; background-color: rgb(255, 255, 255);"
                    rowspan="1" colspan="1"><%= partner.getGeneNames() %>
                </td>

                <!-- description, not linked -->
                <td style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <%= partner.getDescription() %>
                </td>

            </tr>

            <%
                }   //done the partners
            %>
        </tbody>
    </table>

    <%
            } //done one result
    %>

             <!-- line break before next table match...... -->
    <br>
    <%
        }   //ends the loop
    %>


    <!-- line break before the button bar -->
    <br>

    <!-- line seperator -->
    <hr size="2">

    <!-- same buttons as at the top of the page -->
<%@ include file="buttonBar.html" %>


</form>