<%--
    Page to display a 'simple' initial table view of search results. Current
    specification is as per the mockups June 2004. This will be the first page a user sees
    after performing a search - the links provided here will take them to the more
    specific views as defined in the other search mockup pages.

    NOTE:
    Design decision to be made - this view will allow multiple type matches to be displayed,
    so for example if the search string matches Proteins, Experiments AND interactions then
    a simple table can be displayed for each result type. Is this required, or does it have
    to be as now ie only a single type search match?

    DECISION: provide multiple type results.


IMPORTANT: This will now require a change to the search app logic, as currently
it assumes that no Protein type specified means 'show a binary view'. With this view now as the
first one, any subsequeent views will have the type specified anyway, so we must distinguish
between a single Protein detail request and a request for a partners view. This has an impact on
other types too. Maybe this JSP has to set an additional request/form parameter, 'front',
to identify the source page of the request to the Action classes.

    @author Chris Lewington
    @version $Id$
--%>

<%-- need to provide an error page here to catch unhandled failures --%>
<%@ page language="java"  %>
<%@ page buffer="none"    %>
<%@ page autoFlush="true" %>

<%-- Intact classes needed --%>
<%@ page import="uk.ac.ebi.intact.application.search3.struts.framework.util.SearchConstants,
                 uk.ac.ebi.intact.application.search3.business.IntactServiceIF,
                 uk.ac.ebi.intact.application.search3.struts.view.beans.SimpleViewBean,
                 uk.ac.ebi.intact.model.Experiment,
                 uk.ac.ebi.intact.model.Interaction,
                 uk.ac.ebi.intact.model.Protein,
                 uk.ac.ebi.intact.model.CvObject,
                 uk.ac.ebi.intact.business.IntactException"%>

<%-- Standard Java classes --%>
<%@ page import="java.util.*"%>

<%-- may make use of these later to tidy up the JSP a little --%>
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

    //The List of view beans used to provide the data for this JSP. This is in fact
    //a List of sublists, partitioned by result type.
    List partitionList = (List)request.getAttribute(SearchConstants.VIEW_BEAN_LIST);

    //get the maximum size beans from the context for later use
    Map sizeMap = (Map)session.getServletContext().getAttribute(SearchConstants.MAX_ITEMS_MAP);

%>

<%-- The javascript for the button bars.... --%>
<%@ include file="jscript.html" %>

<!-- top line info -->
<h3>Search Results for
    <%=session.getAttribute(SearchConstants.SEARCH_CRITERIA) %>
</h3>
<span class="smalltext">(short labels of search criteria matches are
    <span style="color: rgb(255, 0, 0);">highlighted</span>
</span><span class="smalltext">)<br></span></p>


<br>

<%-- Firstly need to check that we have at least one set of results that we can display,
because if not then we should NOT display the message below..
--%>
<%
    //go through the partitioned list and check to see if at least one of the sublists
    //is valid for display, by checking the types against corresponding max values....

    boolean areValidItems = false;  //used to flag if we have something to display
    Collection notDisplayed = new ArrayList();  //used to hold the lists that are too big for display
    for(Iterator it = partitionList.iterator(); it.hasNext();) {

        List listToCheck = (List)it.next();
        SimpleViewBean item = (SimpleViewBean)listToCheck.iterator().next();
        String listType = item.getIntactType();

        //NB the 'size' here is obtained from the context map of types to max sizes
        //NOTE that they are String representations of ints in the map as they are used
        //mainly for display - need to convert to an int to use it in code...
        String mapValue = (String)sizeMap.get(listType);
        if(mapValue == null) throw new IntactException("Unknown display type ('"
            + listType + "') for initial search view!");

        int maxSizeForType = Integer.parseInt(mapValue);
        if(listToCheck.size() > maxSizeForType) {

            //cache it ready for removal from the display list
            notDisplayed.add(listToCheck);
%>

        <%-- this gets displayed INSTEAD if the result set was 'too big' --%>
        </span></big></big></big><span class="largetext">Your search matched more than
        <%= maxSizeForType + " " + listType + "s"%>, please refine your search.</span>
        <br style="color: rgb(255, 0, 0); font-weight: bold;">
        <br>

<%
        }
        else areValidItems = true;  //only need ONE in range to show a 'friendly' message


    } //ends List loop

    //now need to remove the sublists that are too large from the list of displayable items..
    partitionList.removeAll(notDisplayed);
%>

<%
    if(areValidItems)  {

        //display the 'friendly' message at the top of the page..
%>
<p>
    <span class="largetext">Please click on any name to view more detail.<br></span>
    <span class="smalltext"> </span>
</p>
<%
    }
%>


<!-- the main form for the page -->
<form name="viewForm">

<br>

<%-- process each remaining partitioned List in turn and display its results in the appropriate
table.....
--%>

<%-- The header should be displayed for each table - need to iterate over each
partitioned list to do this --%>
<%
    List displayList = null;     //use this as each iteration holder
    for(Iterator it1 = partitionList.iterator(); it1.hasNext();) {
        displayList = (List)it1.next();

        //need a handle on this to write out some header info and typecheck...
        SimpleViewBean firstItem = (SimpleViewBean)displayList.iterator().next();
%>

<%-- If we get to here we know we have something to show, so we need a button bar...

(top) button bar: NB will include an 'XML' button when that functionality is integrated
NB DON'T want buttons for CvObjects...(so put this one inside the loop...)

--%>
<%@ include file="buttonBar.html" %>

<!-- result table -->
<table style="background-color: rgb(241, 245, 248);"
       border="1" cellpadding="5" cellspacing="0" bordercolor="#4b9996">

    <tbody>

        <!-- header row: cells contain only text and help links, self-explanatory -->
        <tr>
            <%
                //just get hold of the intact type and display, UNLESS it is CvObject
                //in which case we display 'controlled vocabularies'
                if(CvObject.class.isAssignableFrom(firstItem.getObject().getClass())) {
            %>
            <td style="vertical-align: top;">Controlled Vocabulary Terms<br>
            </td>
            <%
                }
                else {
                    //need plurals - appending 's' works in most cases..
            %>
            <td class="headerdark">

              <nobr>  <span class="whiteheadertext"><%= firstItem.getIntactType() + "s" %></span>

                <a href="<%=  firstItem.getHelpLink() + "AnnotatedObject.shortLabel"%>" target="new"
                   class="whitelink"><sup>?</sup> </nobr>
                </a>

            </td>
        
            <%
                }
            %>

            <td nowrap="nowrap" class="headerdarkmid" rowspan="1" colspan="1">
                <a href="<%= firstItem.getHelpLink() + "AnnotatedObject.shortLabel"%>" target="new"
                   class="tdlink">Name
                </a>&nbsp;
            </td>

            <td nowrap="nowrap" class="headerdarkmid">
                <a href="<%= firstItem.getHelpLink() + "BasicObject.ac"%>" target="new"
                   class="tdlink">Ac
                </a>
            </td>

            <td nowrap="nowrap" class="headerdarkmid" rowspan="1" colspan="3">
                <a href="<%= firstItem.getHelpLink() + "AnnotatedObject.fullName"%>" target="new"
                   class="tdlink">Description
                </a>
            </td>

            <%-- now for Interactions and Experiments we need an extra header.. --%>
            <%
                if(Experiment.class.isAssignableFrom(firstItem.getObject().getClass())) {
            %>
            <td class="headerdarkmid">Interactions
            </td>
            <%
                }
                else if(Interaction.class.isAssignableFrom(firstItem.getObject().getClass())) {
            %>
            <td colspan="2" nowrap="nowrap" class="headerdarkmid">Proteins
            </td>
            <%
                }
            %>

        </tr>


<%-- now iterate through the list to display the table rows... --%>
<%
        //Each table is roughly the same format - but for eg Experiments and Interactions
        //we display some extra info. Simplest way is to iterate through a List, swapping
        //the List over after each partitioned List has been processed.
        for(Iterator it = displayList.iterator(); it.hasNext();) {

            //know it is the correct type by the time we get here
            SimpleViewBean bean = (SimpleViewBean)it.next();

            //set up the searchURL - this is different depending upon the display type,
            //so for Experiments and Interactions this should link to 'detail-blue',
            //but for Proteins it should go to the partners view and for CvObjects
            //it should do a 'single CVObject' view (ie what is in results.jsp now)
            String searchURL = bean.getObjSearchURL();
%>


        <!-- data row: -->
        <tr>

            <!-- checkbox - presumably checked by default? NOT NEEDED for CvObjects and Experiments -->
            <%
                if(CvObject.class.isAssignableFrom(firstItem.getObject().getClass()) ||
                        Experiment.class.isAssignableFrom(firstItem.getObject().getClass()) ) {
            %>
            <!-- Single cell padding -->
            <td rowspan ="1"></td>
            <%
                } else {
            %>

            <!-- checkbox -->
            <td>
                <input name="<%= bean.getObjAc()%>" type="checkbox" class="text" checked>
            </td>
            <%
                }
            %>

            <%-- name (ie Intact shortlabel), and linked to a suitable view -
                need to set a value in the request to identify this JSP, so that the struts
                Action classes know what to do with eg Protein search requests..
            --%>
            <td nowrap="nowrap" style="vertical-align: top;">
                <a href="<%= searchURL + "&" + SearchConstants.PAGE_SOURCE + "=simple"%>">
                    <b><span style="color: rgb(255, 0, 0);"><%= bean.getObjIntactName() %></span></b>
                </a><br>
            </td>

            <!-- AC, not linked -->
            <td nowrap="nowrap" class="lefttop"><%= bean.getObjAc() %>
            </td>

            <!-- Description (ie full name - or a dash if there isn't one), not linked -->
            <td class="lefttop" rowspan="1" colspan="3">
                <%= bean.getObjDescription() %><br>
            </td>

            <!-- 'number of related items', ie interactions for Experiments,
                    Proteins for Interactions. This is not linked to anything either -->
            <%
                if((Experiment.class.isAssignableFrom(bean.getObject().getClass())) ||
                    (Interaction.class.isAssignableFrom(bean.getObject().getClass()))) {
            %>
            <td class="data">
                <nobr><%= bean.getRelatedItemsSize() %><br></nobr>
            </td>
            <%
                }
            %>
        </tr>

        <%
        }   //closes the row loop
        %>

    </tbody>

</table>
<br>

<%-- processed one item type - now need its bottom button bar and line seperator..
NB don't want one for CvObjects
--%>
<!-- same buttons as at the top of the page -->
<%@ include file="buttonBar.html" %>

<!-- line seperator -->
<hr size="2">


<%

    }   //ends the partition loop - now do the next group...

%>

<br>

</form> <%-- the end of the page details --%>