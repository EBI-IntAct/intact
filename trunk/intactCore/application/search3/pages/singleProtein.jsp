<%--
    Page to display a single Protein view. Current specification is as per
    the mockups June 2004. This page view will display ONLY single Proteins. O
    bjects such as BioSource and CvObjects are handled elsewhere (currently as before
    via the HtmlBuilder and results.jsp - this will be changed over to JSP-only when I
    have completed the main other views).

    This page will usually be used when a user clicks on a hyperlink of another
    page to view the specific details of a single Protein. Protein searches are handled
    by the overview.jsp page as the interaction partners are required in that case.

    @author Chris Lewington
    @version $Id$
--%>

<!-- really need to have an error page specified here to catch anything unhandled -->
<%@ page language="java"  %>
<%@ page buffer="none"    %>
<%@ page autoFlush="true" %>

<!-- Mostly used by the javascript for HV -->
<%@ page import="uk.ac.ebi.intact.application.search3.struts.framework.util.SearchConstants,
                 uk.ac.ebi.intact.application.search3.business.IntactServiceIF,
                 uk.ac.ebi.intact.application.search3.struts.view.beans.ProteinViewBean,
                 uk.ac.ebi.intact.model.Xref"%>

<!-- Standard Java imports -->
<%@ page import="java.util.*"%>

<!-- taglibs: maybe make use of these later to tidy up the JSP -->
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>

<%
    // To allow access hierarchView properties. Used only by the javascript.
    IntactServiceIF service = (IntactServiceIF) application.getAttribute(
            SearchConstants.INTACT_SERVICE);

    //build the absolute path out of the context path for 'search'
    //NB for eg HV and help pages, we have to use the relative path because the
    //ctxt path includes search
    String ctxtPath = (request.getContextPath());
    String relativePath = ctxtPath.substring(0, ctxtPath.lastIndexOf("search"));

    //build the URL for hierarchView from the absolute path and the relative beans..
    String hvPath = relativePath.concat(service.getHierarchViewProp("hv.url"));
    String minePath = relativePath.concat("mine/display.jsp");

    //The view bean used to provide the data for this JSP. Could probably use
    //the jsp:useBean tag instead, but do it simply for now...
    ProteinViewBean bean = (ProteinViewBean)session.getAttribute(SearchConstants.VIEW_BEAN);
%>


<%-- The javascript for the button bars.... --%>
<%@ include file="jscript.html" %>

<h1>Search Results for
    <%=session.getAttribute(SearchConstants.SEARCH_CRITERIA) %>
</h1>

<br>
<span class="smalltext">(short labels of search criteria matches are
    <span style="color: rgb(255, 0, 0);">highlighted</span>
</span><span class="smalltext">)<br></span></p>
<!--
The (repaired) HTML here is more or less what was specified in the Intact webpage
mockups, June 2004
-->

<form name="viewForm">

<!-- we need the buttons at the top as well as the bottom now -->
<%@ include file="buttonBar.html" %>


    <hr size="2">   <!-- line separator -->

    <!-- The main display table -->
    <table style="background-color: rgb(51, 102, 102); width: 100%;" cellpadding="2">
        <tbody>

            <!-- Row 1: top row Protein info NB the checkbox should be selected by default -->
            <tr bgcolor="white">

                <!-- checkbox and main label --->
                <td class="headerdark">
                    <nobr>
                    <input type="checkbox" name="<%= bean.getProteinAc()%>" checked>
                    <span class = "whiteheadertext">Protein</span>
                    <a href="<%= bean.getHelpLink() + "search.TableLayout"%>"
                    target="new"><sup><b><font color="white">?</font></b></sup></a></nobr>
                </td>

                <!-- shortlabel -->
                <td class="headerdarkmid"
                    rowspan="1" colspan="2">
                    <a href="<%= bean.getHelpLink() + "AnnotatedObject.shortLabel"%>"
                        class="tdlink"
                       target="new"><span style="color: rgb(102, 102, 204);">IntAct </span>name:</a>
                    <a href="<%= bean.getProteinSearchURL() %>" class="tdlink" style="font-weight: bold;">
                    <b><span style="color: rgb(255, 0, 0);"><%= bean.getProteinIntactName() %></span></b></a>
                </td>

                <!-- AC:- NB this doesn't appear to need a hyperlink... -->
                <td class="headerdarkmid">
                    <a href="<%= bean.getHelpLink() + "BasicObject.ac"%>"
                        target="new" class="tdlink">Ac:</a> <%= bean.getProteinAc() %>
                </td>

                <!-- Single cell padding -->
                <td rowspan ="1" class="headerdarkmid"></td>
            </tr>

           <!-- Rows 2 and 3: Biosource info -->
            <tr bgcolor="white">

                <!-- biosource label+ help link -->
                <td rowspan="1" colspan="1" class="headerdarkmid">&nbsp;Source
                    <a
                        href="<%= bean.getHelpLink() + "Interactor.bioSource"%>"
                        target="new"><br>
                    </a>
                </td>

                <td class="lefttop" rowspan="1" colspan="4">
                    <a href="<%= bean.getBioSearchURL()%>"><%= bean.getBioFullName()%></a>
                </td>
            </tr>

            <!-- Protein description + Biosource help link (again) -->
            <tr bgcolor="white">
                <td class="headerdarkmid">
                <a href="<%= bean.getHelpLink() + "Interactor.bioSource"%>"
                    target="new" class="tdlink">Description<br>
                </td>

                <!-- The text is the Protein description...-->
                <td class="lefttop" rowspan="1" colspan="4">
                   <%= bean.getProteinDescription() %>&nbsp;
                </td>
            </tr>

            <!-- Row 4: gene names row -->
            <tr bgcolor="white">
                <td class="headerdarkmid">Gene name(s)<br></td>
               <td class="lefttop" rowspan="1" colspan="4"><%= bean.getGeneNames() %></td>
            </tr>

            <!-- Xrefs rows (xN)... -->

<%
    //get the first one and process it on its own - it seems we need it to put a search
    //link for it into the first cell of the row, then process the others as per
    //'usual'..(NB assume we have at least one Xref)
    Xref firstXref = (Xref)bean.getXrefs().iterator().next();
%>

<!--
NOTE: For the first row the 'Xref' link is set up and then the 'loop' body is
executed to do the first (sgd) row, THEN the loop itself will generate the
other rows - so the first 'sgd' row is actually the same row as the 'Xref' row, and the
others are subsequent rows.

-->
        <tr bgcolor="white">

                <!-- label + CvDatabase link for primary ID (assumes first Xref is primary)-->
                <td class="headerdarkmid"
                rowspan="<%= bean.getXrefs().size() %>" colspan="1">
                <a href="<%= bean.getCvDbURL(firstXref)%>" class="tdlink">Xref<br></a></td>

        <!-- don't close the row tag here - it will be done in the Xref loop below
        for the first item....
        -->

 <!-- now do all the Xrefs in turn ......... -->
<%
    for(Iterator it = bean.getXrefs().iterator(); it.hasNext();) {
        Xref xref = (Xref)it.next();

    //NB HtmlBuilder assumes first Xref is the primary one..
   //I THINK a single Xref has 4 possible cells -
   //search link, primary ID, secondary Id, qualifier (which has search+help links?)

        if(!xref.equals(firstXref)) {
            //we need to have new rows for each Xref OTHER THAN the first..
%>
            <tr bgcolor="white">
<%
        }
%>
                <!-- cells for each Xref item, starting with the link to
                the Cvdatabase details (ie through search) -->
                <!-- NB the text is assumed to be the shortlabel of the CvDB -->
                <td style="vertical-align: top;">
                    <a href="<%= bean.getCvDbURL(xref)%>"><%= xref.getCvDatabase().getShortLabel()%></a>
                </td>

                <!-- a DB-sourced URL for the Primary Id (the text should be the primary Id)...
                NB if it is null we can't write a link
                -->
                <td>
                    <%
                        String idUrl = bean.getPrimaryIdURL(xref);
                        if(idUrl != null) {
                    %>
                    <a href="<%= idUrl %>"><%= xref.getPrimaryId()%></a>
                    <%
                        }
                        else {
                            out.write(xref.getPrimaryId());
                        }
                    %>
                </td>

                <!-- a secondary Id (no links, so simple) -->
                <td>
                    <%
                        if(xref.getSecondaryId() != null) out.write(xref.getSecondaryId());
                        else out.write("-");
                    %>
                </td>

                <!-- Xref qualifier (dash if null) -->
                <td>
                    <%
                        if(xref.getCvXrefQualifier() != null) {
                    %>
                    <!-- do some links for help and CV info (NB text = qualifier label) -->
                    <a href="<%= bean.getHelpLink() + "Xref.cvXrefType"%>" target="new">Type:</a>
                    <a href="<%= bean.getCvQualifierURL(xref)%>"><%= xref.getCvXrefQualifier().getShortLabel()%></a>

                    <%
                        }
                        else out.write("-");
                    %>
                </td>

        <!-- this tr closes the first (different) row on the first iteration,
            and 'normal' ones subsequently
            -->
        </tr>

<%
    }
%>

        <!-- sequence info (2 rows plus block display) -->
        <tr bgcolor="white">

            <!-- sequence length label -->
            <td class="headerdarkmid">Sequence length<br></td>

            <!-- seq length itself -->
            <td
                style="vertical-align: top; background-color: rgb(255, 255, 255);"
                rowspan="1" colspan="4"><%= bean.getSeqLength() %><br>
            </td>
        </tr>

        <!-- checksum -->
        <tr bgcolor="white">

            <!-- label -->
            <td class="headerdarkmid">CRC64 checksum<br></td>

            <!-- value -->
            <td
                style="vertical-align: top; background-color: rgb(255, 255, 255);"
                rowspan="1" colspan="4"><%= bean.getCheckSum() %><br>
            </td>

        </tr>

        <!-- the sequence itself, written as blocks... -->
        <tr bgcolor="white">

            <td colspan="5" style="background-color: rgb(255, 255, 255);">

<%
    //Write out a formatted sequence, if there is one...

    //The length of one block of amino acids.
    int SEQBLOCKLENGTH = 10;

    // Sequence itself
    String seq = bean.getSequence();
    if (seq != null) {
        out.write("<font face=\"Courier New, Courier, monospace\">");
        int blocks = seq.length() / SEQBLOCKLENGTH;
        for (int i = 0; i< blocks; i++){
            out.write(seq.substring( i * SEQBLOCKLENGTH,
                                i * SEQBLOCKLENGTH + SEQBLOCKLENGTH ));
            out.write(" ");
        }
        out.write(seq.substring(blocks*SEQBLOCKLENGTH));
        out.write("</font>");

    }
    else {
        out.write ("<font color=\"#898989\">No sequence available for this protein.</font>");
    }

%>
            </td>
        </tr>
    </tbody>

</table>
<!-- end of main data table -->

<!-- line spacer -->
<hr size="2">

    <!-- button table, like the one at the top of the page -->
<%@ include file="buttonBar.html" %>

    <!-- the (real) end!! -->
</form>