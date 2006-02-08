<%--
    Page to display a search summary view (used to be the 'binary' view). Current
    specification is as per the mockups June 2004. This page is typically used when a
    Protein is searched for - a summary of the relevant information (eg Interactions
    etc) is displayed.
--%>

<!--
    @author Chris Lewington
    @version $Id$
--%>

<!-- need to provide an error page here to catch unhandled failures -->
<%@ page language="java"  %>
<%@ page buffer="none"    %>
<%@ page autoFlush="true" %>

<!-- Intact classes needed -->
<%@ page import="uk.ac.ebi.intact.application.search3.struts.util.SearchConstants,
                 uk.ac.ebi.intact.application.search3.business.IntactServiceIF,
                 uk.ac.ebi.intact.application.search3.struts.view.beans.PartnersViewBean,
                 uk.ac.ebi.intact.application.search3.struts.util.SearchConstants"%>

<!-- Standard Java classes -->
<%@ page import="java.util.*"%>

<!-- may make use of these later to tidy up the JSP a little -->
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>

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
    //List viewBeans = (List)session.getAttribute(SearchConstants.VIEW_BEAN_LIST);
    List viewBeans = (List)request.getAttribute(SearchConstants.VIEW_BEAN_LIST);

    //the list of shortlabels for the search matches - need to be highlighted
    //NB the SearchAction ensures this will never be null
    List highlightList = (List)request.getAttribute(SearchConstants.HIGHLIGHT_LABELS_LIST);
%>

<%-- The javascript for the button bars.... --%>
<%@ include file="jscript.html" %>

<span class="smalltext">Search Results for <%= session.getAttribute(SearchConstants.SEARCH_CRITERIA) %> </span>
<br/>
<span class="verysmalltext">(short labels of search criteria matches are
    <span style="color: rgb(255, 0, 0);">highlighted</span>
</span><span class="verysmalltext">)<br></span></p>

<!-- the main form for the page -->
<form name="viewForm">

    <!-- NB the display is basically one table definition, repeated for each Protein
    search match ..... -->

    <!-- interate through the viewbean List and display each one in a new table... -->

    <%
        boolean hasPartners = true;  //decide whether or not to display button bar
        for(Iterator it = viewBeans.iterator(); it.hasNext();) {

            Object item = it.next();
            if(item instanceof PartnersViewBean) {
                //OK to carry one - otherwise skip the bean
                PartnersViewBean bean = (PartnersViewBean)item;

                //first check for 'orphan' Proteins and display an appropriate message...
                //(NB multiple Protein matches are handled by the 'main' view, but a single
                //match will come through to here so we need the check)
                if(bean.getInteractionPartners().isEmpty()) {
                    hasPartners = false;
                    %>
                    <br>
                     <h4>The Protein with Intact name
                        <b><span style="color: rgb(255, 0, 0);"><%= bean.getIntactName() %></span></b>
                        and AC <%= bean.getAc()%> has no Interaction partners </h4>
                     <br>
                    <%
                }
                else {
                    //process as normal...
    %>

    <!-- we need the buttons at the top as well as the bottom now -->
    <%@ include file="buttonBar.html" %>   

    <!-- the main data table -->
    <table style="width: 100%; background-color: rgb(51, 102, 102);" width="100%"  cellpadding="5">
        <tbody>

            <!-- header row -->
            <tr>
                <!-- padding cell -->
                <td class="headermid"><br>
                </td>

                <td nowrap="nowrap" class="headerlight" rowspan="1" colspan="1">
                    <a href="<%= bean.getHelpLink() + "AnnotatedObject.shortLabel"%>"
                    target="new" class="tdlink">IntAct name<br></a>
                </td>

                <td nowrap="nowrap" class="headerlight" colspan="1">
                    <a href="<%= bean.getHelpLink() + "BasicObject.ac"%>" target="new"
                        class="tdlink">IntAct Ac<br></a>
                </td>

                 <td  class="headerlight">
                   <nobr><a href="<%=bean.getHelpLink() + "search.TableLayout"%>" target="new" class="tdlink">Number of interactions<br></a></nobr>
                </td>

                <td nowrap="nowrap" class="headerlight" colspan="1">UniProtKB Ac<br></td>

                <td nowrap="nowrap" class="headerlight"colspan="1">Gene name<br></td>

                <td class="headerlight">Description<br></td>
            </tr>


            <!-- first row of data (ie the Protein search match) -->
            <tr bgcolor="#eeeeee">

                <!-- checkbox: NB search results to be checked by default -->
                <td class="headermid">
                    <code><input type="checkbox" name="<%= bean.getAc()%>" ></code>
                </td>

                <!-- shortlabel with link: seems to be back to this page (!!)... -->
                 <td nowrap="nowrap" style="vertical-align: top; background-color: rgb(255, 255, 255);">           
                    <code><a href="<%= bean.getInteractorPartnerURL()%>">
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
                 <td nowrap="nowrap" style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <a href="<%= bean.getInteractorSearchURL()%>"><%= bean.getAc()%></a><br>
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
                <td nowrap="nowrap"  align="center" style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <a href="<%= bean.getInteractionsSearchURL()%>"><%= bean.getNumberOfInteractions()%></a>
                </td>

                <!-- Uniprot AC-->
                 <td class="data" style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <% if( null == bean.getUniprotAc() ) { %>
                       -
                    <% } else { %>
                       <a href="<%= bean.getIdentityXrefURL() %>"><%= bean.getUniprotAc() %></a>
                    <% } %>
                </td>

                <!-- gene name, not linked -->
                <td class="data" style="vertical-align: top; background-color: rgb(255, 255, 255);"
                    rowspan="1" colspan="1">
                    <% Collection someGeneNames = bean.getGeneNames();
                    
                       for (Iterator iterator =  someGeneNames.iterator(); iterator.hasNext();) {
                           String aGeneName =  (String) iterator.next();
                           out.write( aGeneName );
                           if( iterator.hasNext() ) {
                               out.write( ", " );
                           }
                       }
                   %>
                </td>

                <!-- description, not linked -->
                <td class="data" style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <%= bean.getDescription()%><br>
                </td>

            </tr>


            <!-- seperating row, containing the 'interacts with' header -->
            <tr bgcolor="#eeeeee">

                <!-- single cell padding -->
                <td class="headerlight"><br>
                </td>

                <!-- heading, spans the table width (6 columns) -->
                <td class="data" rowspan="1" colspan="6" style="background-color: rgb(255, 255, 255);">
                    interacts with
                    <br>
                </td>

            </tr>

            <!-- 1. Protein is done, now look at the partners -->
            
            <!-- partner rows:
                NB: Each interaction partner needs to be displayed with a summary
                viewbean format itself - we can get a Set of view beans from the
                main view bean for the search result.

            -->
            <%
                Collection partners = bean.getInteractionPartners();
                for(Iterator iter = partners.iterator(); iter.hasNext();) {
                    PartnersViewBean partner = (PartnersViewBean)iter.next();
            %>
            <tr>

                <!-- checkbox -->
                <td class="headermid">
                    <code><input type="checkbox" name="<%= partner.getAc()%>"></code>
                </td>

                    <!-- shortlabel, linked back to this view for the partner instead -->
                <td nowrap="nowrap" style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <code><a href="<%= partner.getInteractorPartnerURL()%>"><nobr><%= partner.getIntactName() %></nobr></a></code>
                </td>

                <!-- AC, linked to single Protein details page -->
                <td nowrap="nowrap" style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <a href="<%= partner.getInteractorSearchURL()%>"><%= partner.getAc()%></a><br>
                </td>

                <!-- number of Interactions, linked new detail page -->

                <td align="center" style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <a href="<%= partner.getInteractionsSearchURL()%>"><%= partner.getNumberOfInteractions() %></a>
                </td>

                <!-- Uniprot AC, BUT:
                    The mockup says the FIRST partner ONLY is linked to Uniprot (TBD),
                    whilst the others are not. Is this correct? If so then how is
                    the order of partners determined?
                    -->
                <td class="data" style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <% if( null == partner.getUniprotAc() ) { %>
                       -
                    <% } else { %>
                       <a href="<%= partner.getIdentityXrefURL()%>"><%= partner.getUniprotAc() %></a>
                    <% } %>
                </td>

                <!-- gene name, not linked -->
                <td class="data" style="vertical-align: top; background-color: rgb(255, 255, 255);"
                    rowspan="1" colspan="1">

                    <% Collection somePartnerGeneNames = partner.getGeneNames();

                       for (Iterator iterator =  somePartnerGeneNames.iterator(); iterator.hasNext();) {
                           String aGeneName =  (String) iterator.next();
                           out.write( aGeneName );
                           if( iterator.hasNext() ) {
                               out.write( ", " );
                           }
                       }
                     %>

                </td>

                <!-- description, not linked -->
                <td class="data" style="vertical-align: top; background-color: rgb(255, 255, 255);">
                    <%= partner.getDescription() %>
                </td>

            </tr>

            <%
                }   //done the partners
            %>
        </tbody>
    </table>

    <%
                }   //done 'orphan' check
            } //done one result
    %>

             <!-- line break before next table match...... -->
    <br>
    <%
            //need button bar underneath too IF this is the last one to be processed...
            if((!it.hasNext()) & hasPartners) {
    %>
        <!-- line break before the bottom button bar -->
        <br>
        <!-- same buttons as at the top of the page -->
        <%@ include file="buttonBar.html" %>
    <%
            } //ends button bar check
        }   //ends the loop
    %>  

</form>