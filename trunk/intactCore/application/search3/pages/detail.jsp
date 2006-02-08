<%--
    Page to display a 'detail' view of search results. Current specification is as per the
    mockups August 2004. Details for both Experiment and Interaction  results are displayed using
    the page format defined in this JSP. As such the view beans used may wrap either Experiments
    or Interactions, however the view displayed will be the same. This is because Interaction
    results are always displayed in the context of their Experiments.

    Note that this page will not be accessed directly - an initial search will have the results
    displayed in the new 'simple' JSP view, and so this 'detail' view will be used when a link
    is clicked from that 'simple' results page to view more detail of either an Experiment
    or an Interaction. In the case of Interactions this may mean that a number of tables
    are displayed on this page since (at least in theory) an Interaction may be related to more
    than a single Experiment.

    Furthermore there are a number of Experiments (for example Giot) which have such a large
    number of Interactions that they cannot be displayed on a single page. Thus for such Experiments
    this 'detail' JSP is also responsible for displaying the results in a tabbed view - although
    the actual content for each tab will be provided from the view bean itself.
--%>

<!--
    @author Chris Lewington, Samuel Kerrien (skerrien@ebi.ac.uk)
    @version $Id$
-->

<%-- need to provide an error page here to catch unhandled failures --%>
<%@ page language="java"  %>
<%@ page buffer="none"    %>
<%@ page autoFlush="true" %>

<%-- Intact classes needed --%>
<%@ page import="uk.ac.ebi.intact.application.search3.struts.util.SearchConstants,
                 uk.ac.ebi.intact.application.search3.business.IntactServiceIF,
                 uk.ac.ebi.intact.application.search3.business.Constants,
                 uk.ac.ebi.intact.application.search3.struts.view.beans.MainDetailViewBean,
                 uk.ac.ebi.intact.business.IntactException,
                 uk.ac.ebi.intact.model.*,
                 uk.ac.ebi.intact.application.search3.struts.view.beans.FeatureViewBean,
                 uk.ac.ebi.intact.application.search3.struts.util.SearchConstants"%>

<%-- Standard Java classes --%>
<%@ page import="java.util.*"%>

<%-- may make use of these later to tidy up the JSP a little --%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c"%>

<%
    // To allow access hierarchView properties. Used only by the javascript.
    IntactServiceIF service = (IntactServiceIF) application.getAttribute( SearchConstants.INTACT_SERVICE );

    //build the absolute path out of the context path for 'search'
    String ctxtPath = ( request.getContextPath() );
    String relativePath = ctxtPath.substring( 0, ctxtPath.lastIndexOf("search") );

    //build the URL for hierarchView from the absolute path and the relative beans..
    String hvPath = relativePath.concat( service.getHierarchViewProp( "hv.url" ) );
    String minePath = relativePath.concat( "mine/display.jsp" );

    //The List of view beans used to provide the data for this JSP.
    List viewBeanList = (List)request.getAttribute( SearchConstants.VIEW_BEAN_LIST );

    //the list of shortlabels for the search matches - need to be highlighted
    //NB the SearchAction ensures (in most cases!) this will not be null
    List highlightList = (List) request.getAttribute( SearchConstants.HIGHLIGHT_LABELS_LIST );
    if( highlightList == null ) {
        highlightList = new ArrayList();  //avoids null checks everywhere!
    }
%>

<%-- The javascript for the button bars.... --%>
<%@ include file="jscript.html" %>

<!-- top line info -->
<%--    <span class="middletext">Search Results for <%=session.getAttribute( SearchConstants.SEARCH_CRITERIA )%> <br></span>--%>

<span class="smalltext">Search Results for

    <%
        String params = (String) session.getAttribute(SearchConstants.SEARCH_CRITERIA);

        if( params.length() > 30 ) {

            // split the params and display 10 per lines.
            StringTokenizer st = new StringTokenizer( params, "," );
            int count = 0;
            while( st.hasMoreTokens() ) {
                out.write( st.nextToken() );
                out.write( ',' );
                count++;
                if( (count % 10) == 0 ) {
                    out.write( "<br>" );
                }
            }

        } else {
            out.write( params );
        }

    %>

</span>

<br/>

    <span class="verysmalltext">(short labels of search criteria matches are
        <span style="color: rgb(255, 0, 0);">highlighted</span>
    </span><span class="verysmalltext">)<br></span>
    <span class="verysmalltext"><br></span>

<%
    //first check to see if the bean list is null - if it is then it means that the
    //request is not a new one, but rather a tabbed page request. In this case get the
    //previously saved bean from the session and use that for the rest of this display....
    if(viewBeanList == null) {
        viewBeanList = new ArrayList();
        MainDetailViewBean existingBean = (MainDetailViewBean) session.getAttribute( SearchConstants.LARGE_EXPERIMENT_BEAN );
        viewBeanList.add( existingBean );
        //need to get its shortlabel for highlighting
        //(this comes from the request in all cases EXCEPT a tabbed view)
        highlightList.add( existingBean.getObjIntactName() );
    }

    //now go through the viewbean list and produce a table for each bean.....
    for( Iterator it = viewBeanList.iterator(); it.hasNext(); ) {
        MainDetailViewBean bean = (MainDetailViewBean) it.next();

        //first thing is to check for a 'large' Experiment view - if it is, then need to display
        //a table with tabbed pages...
        //NB Use the REAL size of the Interaction list here - the BEAN holds a SUBLIST
        if((bean.getObject().getInteractions().size() > Constants.MAX_PAGE_SIZE) &
            (!bean.isInteractionView())) {

            //put this particular viewbean into the SESSION (because the next tabbed
            //page request will be different to the current request) so that subsequent
            //Action classes can change its contents as per the requested next page
            //NOTE: ASSUMES THERE IS ONLY LIKELY TO BE A SINGLE LARGE EXPERIMENT PER VIEW -
            //IF NOT, THIS MAKES OTHER PROCESSING VERY COMPLICATED EG IN TRACKING WHAT
            //PAGE IS REQUESTED ON WHAT VIEWBEAN!!
            session.setAttribute(SearchConstants.LARGE_EXPERIMENT_BEAN, bean);

            int currentPage = 1;    //the 'real' page number - default is first page
            String pageParam = request.getParameter("selectedChunk");    //the String representation of it from the Request

            if((pageParam != null) && (!pageParam.equals("")))
                currentPage = Integer.parseInt(pageParam);  //got one in request - use it

            //work out the page numbers to display in the list...
            //NB currently work with a maximum offset of 10 pages either side of the
            //currently displayed page number
            int pageOffset = 10;

            // first page number of the page list (bounded below by 1)
            int firstPage = Math.max((currentPage - pageOffset), 1);

            // last page number of the page list (bounded above by the largest possible page no.)
            int lastPage = Math.min((currentPage + pageOffset), bean.getMaxPage());

            //now calculate the List indexes of the Interactions to be displayed in this page..
            //SOME MATHS:
            //To Calculate the Interaction DISPLAY indexes from the page number and page size:

            //  index of last Interaction on page = MIN((page number * page size), max list size)
            //  index of first Interaction on page = (index of last Interaction - page size) + 1

            //The REAL indexes are ONE LESS than these because the java Lists start at zero.
            int maxListSize = bean.getObject().getInteractions().size();
            int displayPageSize = Constants.MAX_PAGE_SIZE;  //the default size to display
            int pageSizeNeeded = bean.getInteractions().size(); //the size we have to display

            //check to see if we have less than the default page size left (eg on some
            //last pages)
            if( pageSizeNeeded < displayPageSize) displayPageSize = pageSizeNeeded;

            int lastDisplayIndex = Math.min((currentPage * Constants.MAX_PAGE_SIZE), maxListSize);
            int firstDisplayIndex = (lastDisplayIndex - displayPageSize) + 1;
%>

<!-- The summary message of pages of Interactions for the 'large' Experiment -->
<p>
<center>
Displaying <b><%= firstDisplayIndex %></b> to
<b><%= lastDisplayIndex %></b> of
<b><%= bean.getObject().getInteractions().size() %></b> Interactions <br>
</center>
<!-- the list of pages itself -->
<table width ="100%">
    <tr>
        <td width="100%" align="center">
            <%
                //display 'previous' and 'first' if the current page is in range...
                if(currentPage > 1) {
            %>
                    <a href="<%= bean.getObjSearchURL() +
                        "&selectedChunk=1" + "&"+ SearchConstants.PAGE_SOURCE + "=partner" %>">
                        <strong>&lt;First</strong>
                    </a>
                    &nbsp;&nbsp;
                    <a href="<%= bean.getObjSearchURL() +
                        "&selectedChunk=" + (currentPage -1) +
                        "&"+ SearchConstants.PAGE_SOURCE + "=partner" %>">
                        <strong>&lt;&lt;Previous</strong>
                    </a>&nbsp;
            <%
                }
                //now do the list itself...
                for(int i = firstPage; i <= lastPage; i++) {
                    if(i == currentPage) {
                        //don't link it
            %>
                       <font color="red"><strong><%= i%></strong></font>&nbsp;

            <%
                    }
                    else {
                    %>
                    <a href="<%= bean.getObjSearchURL() + "&selectedChunk=" + i +
                                "&"+ SearchConstants.PAGE_SOURCE + "=partner" %>"><%= i%></a>&nbsp;


            <%
                        }
                }   //end of loop to display the list

                //display 'next' and 'last' if the current page is in range...
                if(currentPage < (bean.getMaxPage())) {
            %>
                    <a href="<%= bean.getObjSearchURL() + "&selectedChunk=" + (currentPage +1) +
                                "&"+ SearchConstants.PAGE_SOURCE + "=partner" %>">
                        <strong>Next&gt;&gt;</strong>
                    </a>
                    &nbsp;&nbsp;
                    <a href="<%= bean.getObjSearchURL() + "&selectedChunk=" + bean.getMaxPage() +
                                "&"+ SearchConstants.PAGE_SOURCE + "=partner" %>">
                        <strong>Last&gt;</strong>
                    </a>
            <%
                }   //end of 'next' check
            %>
        </td>
    </tr>
</table>
</p>

<%
        }   //end of 'large' Exp size check
%>

<!-- the main form for the page -->
<form name="viewForm">

<!-- button bar for the table -->
<%@ include file="buttonBar.html" %>

<!-- main results tables -->
<%--<table style="width: 100%; background-color: rgb(241, 245, 248);"--%>
<table style="background-color: rgb(241, 245, 248);"
        border="1" cellpadding="5" cellspacing="0" bordercolor="#4b9996">

    <tbody>

        <!-- Experiment header row -->
        <tr>

            <!-- 'Experiment' title cell plus checkbox -->
            <!-- <td width="10%" rowspan="2" class="headerdark"> -->
            <td rowspan="2" class="headerdark">
                <table  width="100%" border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td valign="top">
                            <nobr><span class="whiteheadertext">Experiment</span>
                                <a href="<%= bean.getHelpLink() + "Experiment"%>" target="new" class="whitelink"><sup>
                                    ?</sup></a>

                            </nobr>
                        </td>
                    </tr>
                    <tr>
                        <td valign="bottom" align="center">
                            <nobr>
                                <% if ( bean.hasPsi1URL() ) { %>
                                <a href="<%= bean.getPsi1Url() %>"><img src="<%= request.getContextPath() %>/images/psi10.png"
                                                                        alt="PSI-MI 1.0 Download"
                                                                        onmouseover="return overlib('Download data from publication in PSI-MI XML 1.0', DELAY, 150, TEXTCOLOR, '#FFFFFF', FGCOLOR, '#EA8323', BGCOLOR, '#FFFFFF');"
                                                                        onmouseout="return nd();"></a>
                                <% } %>
                                <% if ( bean.hasPsi25URL() ) { %>
                                <a href="<%= bean.getPsi25Url() %>"><img src="<%= request.getContextPath() %>/images/psi25.png"
                                                                         alt="PSI-MI 2.5 Download"
                                                                         onmouseover="return overlib('Download data from publication in PSI-MI XML 2.5', DELAY, 150, TEXTCOLOR, '#FFFFFF', FGCOLOR, '#EA8323', BGCOLOR, '#FFFFFF');"
                                                                         onmouseout="return nd();"></a>
                                <% } %>
                            </nobr>
                        </td>
                    </tr>
                </table>
            </td>

            <!-- 'name' title, linked to help -->
            <%-- <td width="10%" nowrap="nowrap" class="headerdarkmid"> --%>
            <td nowrap="nowrap" class="headerdarkmid">
                <a href="<%= bean.getHelpLink() + "AnnotatedObject.shortLabel"%>" target="new"
                   class="tdlink">Name
                </a>
            </td>

            <!-- 'ac' title, linked to help -->
            <%-- <td width="10%" nowrap="nowrap" class="headerdarkmid"> --%>
            <td nowrap="nowrap" class="headerdarkmid">
                <a href="<%= bean.getHelpLink() + "BasicObject.ac"%>" target="new"
                   class="tdlink">Ac
                </a>
            </td>

            <!-- 'identification' title, linked to help -->
            <%-- <td width="20%" nowrap="nowrap" class="headerdarkmid"> --%>
            <td nowrap="nowrap" class="headerdarkmid">
                <a href="<%= bean.getHelpLink() + "CVINTERACTION_HELP_SECTION"%>" target="new"
                   class="tdlink">Interaction detection
                </a>
            </td>

            <!-- 'participant' title, linked to help -->
            <td rowspan="1" colspan="2" nowrap="nowrap" class="headerdarkmid">
                <a href="<%= bean.getHelpLink() + "CVIDENT_HELP_SECTION"%>" target="new"
                   class="tdlink"><nobr>Participant identification</nobr>
                </a>
            </td>

            <!-- 'host' title, linked to help -->
            <td colspan="3" nowrap="nowrap" class="headerdarkmid">
                <a href="<%= bean.getHelpLink() + "Interactor.bioSource"%>" target="new"
                   class="tdlink">Host
                </a>
            </td>
        </tr>

        <!-- Experiment first data row -->
        <tr>
            <%-- <td width="10%" class="lefttop"> --%>
            <td nowrap="nowrap" class="lefttop">
                <%
                    if(highlightList.contains(bean.getObjIntactName())) {
                %>
                    <b><span style="color: rgb(255, 0, 0);"><%= bean.getObjIntactName()%></span></b>
                <%
                    }
                    else {
                        //no highlighting
                %>
                    <%= bean.getObjIntactName()%>
                <%
                    }
                %>
            </td>

            <%-- <td width="10%" class="lefttop"><%= bean.getObjAc()%></td> --%>
            <td nowrap="nowrap" class="lefttop"><%= bean.getObjAc()%></td>

            <!-- linked to the CvInteraction search -->
            <%-- <td width="20%" class="lefttop"> --%>
            <td class="lefttop">
                <a href="<%= bean.getCvInteractionSearchURL() %>">
                    <%= bean.getObject().getCvInteraction().getShortLabel() %>
                </a>
            </td>

            <!-- linked to CvIdentification search -->
            <td style="vertical-align: top;" rowspan="1" colspan="2">
                <a href="<%= bean.getCvIdentificationSearchURL() %>">
                    <%= bean.getObject().getCvIdentification().getShortLabel() %>
                </a>
            </td>


            <% if(bean.getExperimentBioSourceName().equalsIgnoreCase("-"))  { %>
              <!-- linked to BioSource search -->
                <td colspan="4" class="lefttop">
                    <nobr><%= bean.getExperimentBioSourceName() %></nobr>
                </td>
            <% }  else { %>
                <td colspan="3" class="lefttop">
                    <a href="<%= bean.getBioSourceSearchURL() %>">
                        <nobr><%= bean.getExperimentBioSourceName() %></nobr>
                    </a>
                </td>
            <% } %>
        </tr>

        <!-- Experiment Description -->
        <tr>

            <!-- 'Description' title, linked to help -->
            <td class="headerdarkmid" style="font-weight: bold;">
                <a href="<%= bean.getHelpLink() + "AnnotatedObject.fullName" %>" class="tdlink">
                 Description
                </a>
            </td>

            <!-- the description itself -->
            <td colspan="8" class="lefttop"><%= bean.getObjDescription() %></td>
        </tr>

        <!-- Experiment Annotation row  -->
        <%
            Collection annotations = bean.getFilteredAnnotations();
            Collection xrefs = bean.getXrefs();
            int rowCount = 0;

            if(annotations.size() > 0) {
        %>
        <tr>

            <!-- 'Annotation' title cell (linked to help) -->
            <%-- <td width="10%" class="headerdarkmid" rowspan="<%= annotations.size() %>" colspan="1"> --%>
            <td class="headerdarkmid" rowspan="<%= annotations.size() %>" colspan="1">
                <a href="<%= bean.getHelpLink() + "AnnotatedObject.Annotation" %>" class="tdlink">
                    Annotation<br>
                </a>
            </td>

            <%-- for now do the simplest thing - display all of the Annotation details in turn...
                 (NB grouping individuals together requires a lot more logic!!)
                 NOTE: First Annotation has to go on the same row as the title cell..
            --%>

        <%
            rowCount = 0;
            for(Iterator iter = annotations.iterator(); iter.hasNext();) {
                Annotation annot = (Annotation)iter.next();
                rowCount++;
                if(rowCount > 1) {
                    //need a new <tr> tag for all beans except the first one..
        %>
        <tr>
        <%
                }
        %>

            <!-- annotation 'topic' title cell -->
            <td style="vertical-align: top;">
                <a href="<%= bean.getCvTopicSearchURL(annot)%>" style="font-weight: bold;">
                    <%= annot.getCvTopic().getShortLabel()%></a><br>
            </td>

            <!-- annotation text cell -->
            <td class="data" style="vertical-align: top;" rowspan="1" colspan="7">
                <%
                    //need to check for a 'url' annotation and hyperlink them if so...
                    if( annot.getCvTopic().getShortLabel().equals( CvTopic.URL ) ) {
                %>
                <a href="<%= annot.getAnnotationText() %>" target="_blank"><%= annot.getAnnotationText() %></a><br>
                <%
                    } else {
                            if( annot.getAnnotationText() != null) {  %>
                                <%= annot.getAnnotationText() %><br>
                        <%   }
                            else {
                            %>
                          - <br>
                            <% }
                    }

                %>
            </td>

      </tr>
        <%
                }   //end of annotations loop
            }   //end of annotations size check
        %>


        <!-- Xref information -->
        <%
            if(xrefs.size() > 0) {
        %>
        <tr>

            <!-- 'Xref' title cell - linked to help -->
            <%-- <td width="10%" class="headerdarkmid" rowspan="<%= xrefs.size()%>" colspan="1" --%>
            <td class="headerdarkmid" rowspan="<%= xrefs.size()%>" colspan="1"
                style="text-align: justify;">
                <a href="<%= bean.getHelpLink() + "AnnotatedObject.Xref"%>" class="tdlink">
                    Xref<br>
                </a>
            </td>

            <%

                rowCount = 0;   //reset rowcount and reuse it
                for(Iterator iter = xrefs.iterator(); iter.hasNext();) {
                    Xref xref = (Xref)iter.next();
                    rowCount++;
                    if(rowCount > 1) {
                        //need a new <tr> tag for all beans except the first one..
            %>
            <tr>
            <%
                    }
            %>

            <!-- link to the Xref CvDatabase details -->
            <%-- (I think - example is pubmed) --%>
            <%-- <td width="10%" class="lefttop" colspan="1"> --%>
            <td class="lefttop" colspan="1">
                <a href="<%= bean.getCvDbURL(xref) %>" class="tdlink">
                    <%= xref.getCvDatabase().getShortLabel() %>
                </a>
            </td>

            <!-- actual search link to the Xref-->
            <%-- ie the real URL filled with the ID - NB if it is null we can't write a link --%>

            <%-- <td width="10%" class="lefttop"> --%>
            <td class="lefttop">
            <%
                String idUrl = bean.getPrimaryIdURL(xref);
                if(idUrl != null) {
            %>
                <a href="<%= idUrl %>"><%= xref.getPrimaryId() %></a>
            <%
                }
                else {
                    out.write(xref.getPrimaryId());
                }
            %>
            </td>

            <!-- The Xref secondaryID, or a dash if there is none -->
            <td colspan="1" class="lefttop">
                <%= (xref.getSecondaryId() != null) ? xref.getSecondaryId() : "-" %>
            </td>

            <%-- CvXrefQualifier, linked to search for CV --%>
            <td style="vertical-align: top;" rowspan="1" colspan="5">
                <a href="<%= bean.getHelpLink() + "AnnotatedObject.Xref"%>" target="new">
                    Type:</a>
                &nbsp;
                <a href="<%= bean.getCvQualifierURL(xref)%>">
                    <%= xref.getCvXrefQualifier().getShortLabel() %></a><br>
            </td>
            </tr>

            <%
                    }   //end the Xref loop
                }   //end of xrefs count check
            %>


        <!-- Interaction details start here... -->
        <%
            Collection interactions = bean.getInteractions();
            for(Iterator iter = interactions.iterator(); iter.hasNext();) {
                Interaction interaction = (Interaction)iter.next();
        %>

        <!-- first row -->
        <tr>

            <!-- first cell - title plus checkbox -->
            <%-- <td width="10%" rowspan="2" class="headermid"> --%>
            <td rowspan="2" class="headermid">
                <nobr><input name="<%= interaction.getAc() %>" type="checkbox" class="text">
                    <span class="whiteheadertext">Interaction</span>
                    <a href="<%= bean.getHelpLink() + "Interaction"%>" target="new" class="whitelink"><sup>?</sup></a>
                </nobr>
            </td>

            <!-- 'name' title cell, linked to help -->
            <%-- <td width="10%" class="headerlight"> --%>
            <td class="headerlight">
                <a href="<%= bean.getHelpLink() + "AnnotatedObject.shortLabel"%>" target="new" class="tdlink">
                    Name
                </a>
            </td>

            <!-- 'ac' title cell, linked to help -->
            <%-- <td width="10%" class="headerlight"> --%>
            <td class="headerlight">
                <a href="<%= bean.getHelpLink() + "BasicObject.ac" %>"
                    target="new" class="tdlink">
                    Ac
                </a>
            </td>

            <!--'interaction type' header cell, linked to help -->
            <%-- <td width="20%" class="headerlight"> --%>
            <td class="headerlight">
                <a href="<%= bean.getHelpLink() + "Interaction.CvInteractionType" %>"
                    target="new" class="tdlink">
                    Interaction type
                </a>
            </td>

            <!-- 'dissociation constant' header cell, linked to help -->
            <%-- ** NB: make sure the text is 'Kd' and not 'kD' ** --%>
            <%-- <td style="vertical-align: top;" rowspan="1" colspan="4">  --%>
            <td style="vertical-align: top;" class="headerlight" rowspan="1" colspan="5">
                <a href="<%= bean.getHelpLink() + "Interaction.kD"%>"
                    target="new" class="tdlink">
                    Dissociation constant (Kd)</a><br>
            </td>

        </tr>

        <!-- first Interaction data row (relates to the above headers) -->
        <tr>

            <!-- shortlabel -->
            <%-- <td width="10%" class="lefttop"> --%>
            <td nowrap="nowrap" class="lefttop">
                <%
                    if(highlightList.contains(interaction.getShortLabel())) {
                %>
                    <b><span style="color: rgb(255, 0, 0);"><%= interaction.getShortLabel()%></span></b>
                <%
                    }
                    else {
                        //no highlighting
                %>
                    <%= interaction.getShortLabel() %>
                <%
                    }
                %>

            </td>

            <!-- ac -->
            <%-- <td width="10%" class="lefttop"><%= interaction.getAc() %></td> --%>
            <td nowrap="nowrap" class="lefttop"><%= interaction.getAc() %></td>

            <!-- interaction type, linked to CvInteractionType -->
            <%-- <td width="20%" class="lefttop"> --%>
            <td class="lefttop">
                <%
                //the CvInteractionType may be null - check for it...
                if(interaction.getCvInteractionType() == null) {
                    out.write("-");
                }
                else {
            %>
                <a href="<%= bean.getCvInteractionTypeSearchURL(interaction)%>">
                    <%= interaction.getCvInteractionType().getShortLabel() %>
                </a>
                <%
                }
                %>
            </td>

            <!-- dissociation constant -->
            <td class="data" style="vertical-align: top;" rowspan="1" colspan="5">
                <%= (interaction.getKD() != null) ? interaction.getKD().toString() : "-" %>
            </td>
        </tr>

        <!-- description info row -->
        <tr>

            <!-- 'description' title cell -->
            <%-- NB this was LINKED to help in Experiment!! --%>
            <%-- <td width="10%" class="headerlight">Description</td> --%>
            <td class="headerlight">
             <a href="<%= bean.getHelpLink() + "AnnotatedObject.fullName" %>" class="tdlink">
                 Description
                </a>
            </td>
            <td colspan="8" class="lefttop">
                <%= (interaction.getFullName() != null) ? interaction.getFullName() : "-" %>
            </td>

        </tr>

        <%-- NB: THERE MAY BE ANNOTATION AND XREF BLOCKS AT THIS POINT AS WELL... --%>
        <%
                Collection intAnnots = bean.getFilteredAnnotations(interaction);
                Collection intXrefs = interaction.getXrefs();
        %>

        <!-- Interaction Annotation row  -->
        <%
                if(intAnnots.size() > 0) {
        %>
        <tr>

            <!-- 'Annotation' title cell (linked to help) -->
            <%-- <td width="10%" class="headerlight" rowspan="<%= intAnnots.size() %>" colspan="1"> --%>
            <td class="headerlight" rowspan="<%= intAnnots.size() %>" colspan="1">
                <a href="<%= bean.getHelpLink() + "AnnotatedObject.shortLabel" %>" class="tdlink">
                    Annotation<br>
                </a>
            </td>

            <%-- for now do the simplest thing - display all of the Annotation details in turn...
                 (NB grouping individuals together requires a lot more logic!!)
                 NOTE: First Annotation has to go on the same row as the title cell..
            --%>

        <%
                rowCount = 0;  //reset rowcount and reuse it
                for(Iterator it2 = intAnnots.iterator(); it2.hasNext();) {
                    Annotation annot = (Annotation)it2.next();
                    rowCount++;
                    if(rowCount > 1) {
                        //need a new <tr> tag for all beans except the first one..
        %>
        <tr>
        <%
                    }
        %>

            <!-- annotation 'topic' title cell -->
            <td class="data" style="vertical-align: top;">
                <a href="<%= bean.getCvTopicSearchURL(annot)%>" style="font-weight: bold;">
                    <%= annot.getCvTopic().getShortLabel()%></a><br>
            </td>

            <!-- annotation text cell -->
            <%
                    //need to check for a 'url' annotation and hyperlink them if so...
                    if(annot.getCvTopic().getShortLabel().equals( CvTopic.URL )) {
            %>
            <td style="vertical-align: top;" rowspan="1" colspan="7">
                <a href="<%= annot.getAnnotationText() %>" class="tdlink" target="_blank">
                    <%= annot.getAnnotationText() %>
                </a><br>
            <%
                    }
                    else {
            %>
            <td style="vertical-align: top;" rowspan="1" colspan="7" class="data">
                <%= annot.getAnnotationText() %><br>
                <%
                    }
                %>
            </td>

      </tr>
        <%
                    }   //end of annotations loop
                }   //end of annotations count check
        %>


        <!-- Interaction Xref information -->
        <%
                if(intXrefs.size() > 0) {
        %>
        <tr>

            <!-- 'Xref' title cell - linked to help -->
            <%-- <td width="10%" class="headerlight" rowspan="<%= intXrefs.size()%>" colspan="1" --%>
            <td class="headerlight" rowspan="<%= intXrefs.size()%>" colspan="1"
                style="text-align: justify;">
                <a href="<%= bean.getHelpLink() + "AnnotatedObject.Xref"%>" class="tdlink">
                    Xref<br>
                </a>
            </td>

            <%
                    rowCount = 0;   //reset rowcount and reuse it
                    for(Iterator it3 = intXrefs.iterator(); it3.hasNext();) {
                        Xref xref = (Xref)it3.next();
                        rowCount++;
                        if(rowCount > 1) {
                            //need a new <tr> tag for all beans except the first one..
            %>
            <tr>
            <%
                        }
            %>

            <!-- link to the Xref CvDatabase details -->
            <%-- (I think - example is pubmed) --%>
            <%-- <td width="10%" class="lefttop" colspan="1"> --%>
            <td class="lefttop" colspan="1">
                <a href="<%= bean.getCvDbURL(xref) %>" class="tdlink">
                    <%= xref.getCvDatabase().getShortLabel() %>
                </a>
            </td>

            <!-- actual search link to the Xref-->
            <%-- ie the real URL filled with the ID - NB if it is null we can't write a link --%>

           <%-- <td width="10%" class="lefttop"> --%>
           <td class="lefttop">
            <%
                    String idUrl = bean.getPrimaryIdURL(xref);
                    if(idUrl != null) {
            %>
                <a href="<%= idUrl %>"><%= xref.getPrimaryId() %></a>
            <%
                    }
                    else {
                        out.write(xref.getPrimaryId());
                    }
            %>
            </td>

            <!-- The Xref secondaryID, or a dash if there is none -->
            <td colspan="1" class="lefttop">
                <%= (xref.getSecondaryId() != null) ? xref.getSecondaryId() : "-" %>
            </td>

            <%-- CvXrefQualifier, linked to search for CV --%>
            <td style="vertical-align: top;" rowspan="1" colspan="5">
                <a href="<%= bean.getHelpLink() + "Xref.cvXrefType"%>" target="new">
                    Type:</a>
                &nbsp;
                <a href="<%= bean.getCvQualifierURL(xref)%>">
                    <%= xref.getCvXrefQualifier().getShortLabel() %>
                </a><br>
            </td>
            </tr>

            <%
                    }   //end the Xref loop
                }   //end of xref count check
            %>


        <!-- Interacting molecules detail (ie the Protein information) -->
        <tr>

        <!-- title cell -->
        <%-- NB the span should be equal to the number of Proteins, +1 for the sub-headers --%>
            <td class="headerlight"
                rowspan="<%= interaction.getComponents().size() + 1%>" colspan="1">
                Interacting molecules<br>
            </td>

            <!-- 'name' title cell, linked to help -->
            <td class="headerlight"   >
                <a href="<%= bean.getHelpLink() + "AnnotatedObject.shortLabel"%>"
                    target="new" class="tdlink">Name</a>
            </td>

            <!-- 'ac' title cell, linked to help -->
            <td class="headerlight">
                <a href="<%= bean.getHelpLink() + "BasicObject.ac"%>"
                    target="new" class="tdlink">Ac</a>
            </td>

            <td nowrap="nowrap" class="headerlight">Interactor type<br></td>

            <!-- 'uniprot description' title cell -->
            <%-- *** NOTE ***
            This has been SWAPPED in position compared to the specification as it is not
            possible to format the 'role' cell properly in its current position in the
            specification. This is due to the fact that it appears in a column whose size
            is determined by a wider cell ('interaction identification'). The only other way
            is to split things up into sub-tables - this then gets very messy!
            --%>
            <%-- not linked again --%>
            <td nowrap="nowrap" class="headerlight">Interactor description<br></td>


            <!-- 'expression system' title cell -->
            <%-- NB this seems NOT to be linked --%>
            <td class="headerlight">Expression system<br></td>

            <!-- 'uniprot ac' title cell -->
            <%-- again seems to NOT be linked to help --%>
            <td class="headerlight">Identifier<br></td>

            <!-- 'gene name' title cell -->
            <%-- again NOT linked to help --%>
            <td class="headerlight">Gene name</td>

            <!-- 'role' title cell, linked to help -->
            <%-- *** ROW POSITION SWAPPED - SEE 'UNIPROT DESCRIPTION' COMMENT *** --%>
            <td class="headerlight">
                <a href="<%= bean.getHelpLink() + "Role"%>" class="tdlink">Role</a>
            </td>
        </tr>

        <!-- Protein data rows, to match the above title cells..... -->
        <%    /**
                Collection proteins = bean.getProteins(interaction);
                for(Iterator it1 = proteins.iterator(); it1.hasNext();) {
                Protein protein = (Protein)it1.next();

            **/

        %>


            <!-- Protein data rows, to match the above title cells..... -->
        <%

        for (Iterator iterator = interaction.getComponents().iterator(); iterator.hasNext();) {
            Component component = (Component) iterator.next();
            Interactor interactor =  component.getInteractor();
//            if (interactor instanceof Protein) {
//                Protein protein = (Protein) interactor;
                BioSource bioSource =  component.getExpressedIn();

        %>

        <%-- data row.... --%>
        <tr>

            <!-- shortlabel, linked to protein partners search -->
            <td class="data">
                <nobr>
                <input name="<%=interactor.getAc()%>" type="checkbox" class="text">
                <a href="<%= bean.getInteractorPartnerURL(interactor)%>"><%=interactor.getShortLabel()%></a></nobr>
                <br>
            </td>

            <!-- ac, linked to protein details view -->
            <td class="data">
                <a href="<%= bean.getInteractorSearchURL(interactor)%>"><%=interactor.getAc()%></a>
            </td>

            <!--interator type-->
            <td class="data">
                <%= bean.getInteractorType(interactor)%>
            </td>

            <!-- uniprot description -->
            <%-- ASSUME this is the same as the Protein fullName --%>
            <%-- *** ROW POSITION SWAPPED - SEE 'UNIPROT DESCRIPTION' COMMENT *** --%>
            <% if (interactor.getFullName() == null ){ %>
            <td class="data">-</td>
            <% } else { %>
            <td class="data"><%= interactor.getFullName() %></td>
            <% } %>
            <!-- expression system, (ie the BioSource Full Name), with a search link -->
           <%   if(!bean.getBiosourceURL(bioSource).equalsIgnoreCase("-")) { %>
            <td class="data">
                <a href="<%= bean.getBiosourceURL(bioSource)%>"><%= bean.getBioSourceName(bioSource)%></a>
            </td>
            <% } else { %>
                <td class="data">
                <%= bean.getBioSourceName(bioSource)%>
            </td>
          <% } %>

            <!-- uniprot ID, linked 'to biosource' (!) (Guess this should be search in uniprot..)-->
            <%-- This is actually an Xref of the Protein, ie its uniprot Xref... --%>
            <td class="data">
            <%
                    if(bean.getPrimaryIdFromXrefIdentity(interactor) != "-") {
                        //link it
            %>
                    <a href="<%= bean.getIdentityXrefSearchURL(interactor)%>"><%= bean.getPrimaryIdFromXrefIdentity(interactor) %></a>
                <%
                    }
                    else {
                        //don't
                %>
                    <%= bean.getPrimaryIdFromXrefIdentity(interactor) %>
                <%
                    }
                %>
                </td>

                <!-- gene name(s), not linked -->
                <td class="data"><%// bean.getGeneNames(protein)%>

                <%
                    Collection somePartnerGeneNames = bean.getGeneNames(interactor);

                    for (Iterator iteratorGene =  somePartnerGeneNames.iterator(); iteratorGene.hasNext();) {
                        String aGeneName =  (String) iteratorGene.next();
                        out.write( aGeneName );
                        if( iteratorGene.hasNext() ) {
                            out.write( ", " );
                        }
                    }
                %>
        </td>

            <!-- role, linked CvComponentRole search -->
            <%-- *** ROW POSITION SWAPPED - SEE 'UNIPROT DESCRIPTION' COMMENT *** --%>
            <%
                    //NB this should never be null....

                  //   Component comp = bean.getComponent(protein, interaction);
                    if(component != null) {
            %>

            <td class="data">
                <a href="<%= bean.getCvComponentRoleSearchURL(component)%>">
                    <%= component.getCvComponentRole().getShortLabel()%>
                </a>
            </td>
         <%
           } else {
         %>

                <td class="data">
                   -
                </td>
           <%  }  %>
        </tr>
        <%
//             } // end if
           }   //end of the proteins loop

        %>

        <!-- 'sequence features' details start here... -->

        <%
                // first get the linked and unlinked feature view beans....
                // note: we expect to get a non redondant set of linked feature. ie. we won't display F1-F2 and F2-F1 !
                Collection linkedFeatures = bean.getLinkedFeatures( interaction );
                Collection singleFeatures = bean.getSingleFeatures( interaction );

                int featureCount = linkedFeatures.size() + singleFeatures.size();

                if( featureCount > 0 ) {
        %>
                    <tr>
                        <%--
                            'sequence features' title cell, linked to help (spans all feature's rows)
                             The rowspan of this cell is equal to the sum of linked + unlinked Features
                          --%>
                        <td style="vertical-align: top;" class="headerlight" rowspan="<%= featureCount %>" colspan="1">
                        <a href="<%= bean.getHelpLink() + "FEATURES_HELP_SECTION"%>"
                           class="tdlink">Sequence features</a>
                        </td>
        <%
                    boolean firstItem = true;
                    while( linkedFeatures.size() + singleFeatures.size() > 0 ) {

                        boolean islinked = false;
                        Iterator iterator = null;

                        if( false == linkedFeatures.isEmpty() ) {
                            iterator = linkedFeatures.iterator();
                            islinked = true;
                        } else {
                            iterator = singleFeatures.iterator();
                        }

                        FeatureViewBean firstFeature = ( FeatureViewBean ) iterator.next();
                        iterator.remove(); // take it off the collection.

                        if( ! firstItem ) {
                            firstItem = false;
           %>
                           <tr>
           <%
                        }
                        // display that row
                        if( islinked ) {
           %>
                            <!-- seems to be some kind of 'summary' cell -->
                            <%-- ** WHERE DOES IT COME FROM?? **
                                ANS:
                                link 1: CvFeatureType, for the Feature that is 'linked' to another one.
                                text1: <Feature shortlabel> of <Protein shortlabel> <region data of the Feature>
                                link 2: Xref link for the feature (or rather, one primary ID link for each Feature Xref)
                                text2: detected by <link 3: CvfeatureDetection (!!)>, interacts with <above again, but
                                for the Feature that is 'linked to'>
                            --%>
                            <td class="data" style="vertical-align: top;" rowspan="1" colspan="7">
                                <%-- link 1 --%>
                                <a href="<%= firstFeature.getCvFeatureTypeSearchURL() %>"><%= firstFeature.getFeatureType() %></a>
                                <%=firstFeature.getFeatureName()%> of <%=firstFeature.getProteinName()%>
                                <%
                                     //now do the Ranges...
                                     Collection ranges = firstFeature.getFeature().getRanges();
                                     String rangeString = "";   //will hold the result (if there is one)
                                     if(!ranges.isEmpty()) {
                                        StringBuffer buf = new StringBuffer("["); //convenience object for building results
                                        for(Iterator it1 = ranges.iterator(); it1.hasNext();) {
                                            buf.append(it1.next().toString());  //The toString of Range does the display format for us
                                            if(it1.hasNext()) buf.append((","));
                                        }
                                        buf.append("]");
                                        rangeString = buf.toString();
                                        rangeString.trim();
                                         if(rangeString.equalsIgnoreCase("[?-?]")) {
                                            rangeString = "[range undetermined]";
                                        }
                                     }
                                %>
                                <%=rangeString %>
                                <%
                                     //only need some brackets if we have Xrefs to display..
                                     if(!firstFeature.getFeatureXrefs().isEmpty()) {
                                %>
                                (

                                <%-- link 2 --%>
                                <%
                                    for(Iterator iter1 = firstFeature.getFeatureXrefs().iterator(); iter1.hasNext();) {
                                        Xref xref = (Xref)iter1.next();
                                %>
                                <a href="<%= firstFeature.getPrimaryIdURL(xref) %>"><%=xref.getPrimaryId()%></a>
                                <%
                                    }   //end of Feature Xref loop
                                %>
                                ),
                                <%
                                     }  //end of Xref check

                                     if( firstFeature.hasCvFeatureIdentification() ) {
                                       // only display detected by XXX if there is a method
                                        %>
                                        detected by
                                        <%-- ** THERE IS NO CVFEATUREDETECTION CLASS!! - ASSUME it should be 'identification'..** --%>
                                        <a href="<%=firstFeature.getCvFeatureIdentSearchURL()%>"><%=firstFeature.getFeatureIdentificationName() %></a>
                                        <%-- Now repeat for the Feature it is linked to.... --%>
                                        <%
                                     }
                                    //there must be one as we are dealing with linked Features here..
                                    FeatureViewBean firstBoundFeature = firstFeature.getBoundFeatureView();
                                %>
                                , interacts with
                                <%-- link 1 --%>
                                <a href="<%= firstBoundFeature.getCvFeatureTypeSearchURL() %>"><%= firstBoundFeature.getFeatureType() %></a>
                                <%= firstBoundFeature.getFeatureName() %> of <%= firstBoundFeature.getProteinName() %>
                                <%
                                     //now do the Ranges for the linked Feature (may reuse the other vars)...
                                     ranges = firstBoundFeature.getFeature().getRanges();
                                     rangeString = "";   //will hold the result (if there is one)
                                     if(!ranges.isEmpty()) {
                                        StringBuffer buf = new StringBuffer("["); //convenience object for building results
                                        for(Iterator it1 = ranges.iterator(); it1.hasNext();) {
                                            buf.append(it1.next().toString());  //The toString of Range does the display format for us
                                            if(it1.hasNext()) buf.append((","));
                                        }
                                        buf.append("]");
                                        rangeString = buf.toString();
                                         if(rangeString.equalsIgnoreCase("[?-?]")) {
                                            rangeString = "[range undetermined]";
                                        }
                                     }
                                %>
                                <%= rangeString %> &nbsp;

                                <%
                                     //only need brackets if we have Xrefs to display..
                                     if(!firstBoundFeature.getFeatureXrefs().isEmpty()) {
                                %>
                                (

                                <%-- link 2 --%>
                                <%
                                    for(Iterator iter1 = firstBoundFeature.getFeatureXrefs().iterator(); iter1.hasNext();) {
                                        Xref xref = (Xref)iter1.next();
                                %>
                                <a href="<%= firstBoundFeature.getPrimaryIdURL(xref) %>"><%= xref.getPrimaryId()%></a>
                                &nbsp;
                                <%
                                    }   //end of Feature Xref loop
                                %>
                                ), &nbsp;
                                <%
                                     }  //end of Xref check

                                     if( firstFeature.hasCvFeatureIdentification() ) {
                                       // only display detected by XXX if there is a method
                                        %>
                                        detected by
                                        <%-- ** THERE IS NO CVFEATUREDETECTION CLASS!! - ASSUME it should be 'identification'..** --%>
                                        <a href="<%=firstFeature.getCvFeatureIdentSearchURL()%>"><%=firstFeature.getFeatureIdentificationName() %></a>
                                        <%-- Now repeat for the Feature it is linked to.... --%>
                                        <%
                                     }
                                %>
                            </td>
        <%
                 } else {
                     // must be a single Feature to display first...
        %>
                        <%-- feature type info, plus search link --%>
                        <td class="data" style="vertical-align: top;" rowspan="1" colspan="8">
                            <a href="<%= firstFeature.getCvFeatureTypeSearchURL() %>"><%= firstFeature.getFeatureType() %></a>
                            <%= firstFeature.getFeatureName() %> of <%= firstFeature.getProteinName() %>
                            <%
                             //now do the Ranges...
                             Collection ranges = firstFeature.getFeature().getRanges();
                             String rangeString = "";   //will hold the result (if there is one)
                             if(!ranges.isEmpty()) {
                                StringBuffer buf = new StringBuffer("["); //convenience object for building results
                                for(Iterator it1 = ranges.iterator(); it1.hasNext();) {
                                    buf.append(it1.next().toString());  //The toString of Range does the display format for us
                                    if(it1.hasNext()) buf.append((","));
                                }
                                buf.append("]");
                                rangeString = buf.toString();
                                 if(rangeString.equalsIgnoreCase("[?-?]")) {
                                    rangeString = "[range undetermined]";
                                }
                             }
                        %>
                            <%= rangeString %>
                        <%
                             //it seems sometimes the detection beans are not present!
                             if(!firstFeature.getCvFeatureIdentSearchURL().equals("")) {
                        %>
                                detected by
                                <%-- ** CVFEATUREDETECTION DOES NOT EXIST ** is this 'identification'? --%>
                                <a href="<%= firstFeature.getCvFeatureIdentSearchURL()%>"><%= firstFeature.getFeatureIdentFullName() %></a>.
                        <%
                             }
                        %>
                        </td>
                <%
                         }  // end of simple feature (no related feature)

                        %>
              </tr>
                        <%
                    } // while more feature to display
                } // if any feature available
        %>
         </tr>
        <%
             }   //end of interactions loop
        %>

    </tbody>
</table>

<!-- END of the main display table -->
<br>
<!-- button bar for the table -->
<%@ include file="buttonBar.html" %>

<%
    }   //end of viewbean list loop
%>
<br>

</form>

