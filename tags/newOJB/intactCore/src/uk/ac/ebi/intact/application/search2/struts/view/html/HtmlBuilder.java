/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search2.struts.view.html;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.search2.business.Constants;
import uk.ac.ebi.intact.application.search2.struts.view.details.BinaryDetailsViewBean;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.proxy.InteractionProxy;
import uk.ac.ebi.intact.model.proxy.ProteinProxy;
import uk.ac.ebi.intact.util.SearchReplace;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * This class generates an HTML view of search results.
 *
 * <p>
 * This should be used from within the Web framework. The main method only serves
 * as a usage example and for quick development.
 * </p>
 *
 * <p>
 * For each request, one HtmlBuilder object should be instantiated.
 * </p>
 *
 * <p>
 * There is one public htmlView method for each of the major IntAct classes:<br>
 * AnnotatedObject, Experiment, Interaction, Protein.<br>
 * Each of these normally call<br>
 * htmlViewHead: Display the object's "administrative data"<br>
 * htmlViewData: Display the object's attributes<br>
 * htmlViewAnnotation: Display annotation <br>
 * htmlViewXref: Display xrefs<br>
 * Rest of the htmlView method: Display additional "bulk" data,<br>
 * e.g. the list of interactors for an Interaction, or
 * the amino acid sequence for a Protein.
 * </p>
 *
 * <p>
 * Private html* methods display partial objects which need to be
 * surrounded by the appropriate context.
 * </p>
 *
 * <p>
 * htmlViewPartial methods indicate that this method only displays an object
 * partially, usually used in the context of another htmlView.
 * Example: htmlViewPartial(CvObject) will only display the hyperlinked
 * shortLabel, while htmlView(CvObject) shows the full object on its own.
 * </p>
 *
 * <p>
 * Layout:
 * The Layout is based on a table layout with four columns in all tables.
 * </p>
 *
 * <p>
 * Status: The "experiment" view is produced.
 * </p>
 *
 *
 * todo: specific htmlView for BioSource, CvDagObject
 *
 * @author Henning Hermjakob, hhe@ebi.ac.uk
 * @version $Id$
 */
public class HtmlBuilder {

    private transient static final Logger logger = Logger.getLogger( Constants.LOGGER_NAME );


    // Cache database access URLs
    // These are frequenly used, so cache them.
    private static HashMap dbUrls = new HashMap();

    /**
     *  The length of one block of amino acids.
     */
    private static final int SEQBLOCKLENGTH = 10;

    /**
     * The "normal" lenght of a protein short label, derived from the
     * Swiss-Prot convention of xxxx_yyyyy for Swiss-Prot IDs.
     * This is only an approximation producing reasonable formatting in most
     * cases.
     */
    private static final int FORMATTED_LABEL_LENGTH = 11;

    /**
     * The link to the online help system.
     */
    private static String helpLink; // = "/intact/displayDoc.jsp?section=";

    /**
     * The context of the application.
     */
    private String contextPath;

    // The destination writer to write HTML to.
    // All output is sent to this writer, which produces fast and
    // streamed output in most situations.
    private Writer rs = null;

    // Shortlabels which should be highlighted
    private Set toHighlight = null;

    // The class of the object for which a table has been started
    private Class tableLevel = null;

    // Color mapping
    private static final String tableBackgroundColor="336666";
    private static final String tableHeaderColor="eeeeee";
    private static final String tableCellColor="white";


    /**
     * Instantiate a new view object
     * @param writer The Writer all html output is written to.
     * @param highlight A HashSet containing all shortLabels which should be
     *                  highlighted in the result set.
     * @param link the link to the help page.
     */
    public HtmlBuilder( Writer writer,
                        Set highlight,
                        String link,
                        String contextPath) {
        rs = writer;
        toHighlight = highlight;
        helpLink = link;
        this.contextPath = contextPath;
    }

    /**
     * Writes info to the output stream. Usually direct, but if the
     * argument is null (eg no full names in objects) then a 'no data'
     * indication is written (output stream can't handle nulls)
     * @param aString The String to write out
     */
    private void write(String aString) throws IOException {
        if(aString == null) {
            rs.write( "-" );
        }
        else {
            rs.write( aString );
        }

    }

    /**
     * Start a new html table if currently no table is open.
     * Do nothing otherwise.
     *
     * @param anObject
     * @throws java.io.IOException
     */
    private void beginTable(AnnotatedObject anObject) throws IOException {

        if (null == tableLevel) {
            write("<table width=100% bgcolor=\""
                    + tableBackgroundColor
                    + "\">");
            tableLevel = anObject.getClass();
            logger.info("HtmlBuilder starts table level: " + tableLevel);
        }
    }

    /**
     * Close a table if anObject is of the same class in which the table has
     * been started.
     * Do nothing otherwise.
     *
     * @param anObject
     * @throws java.io.IOException
     */
    private void endTable(AnnotatedObject anObject) throws IOException {
        if (anObject.getClass().equals(tableLevel)) {
            write("</table>");
            logger.info("HtmlBuilder stops table level: " + tableLevel);
            tableLevel = null;
        }
    }

    /**
     * Write a checkbox for an annotatedObject
     */
    private void htmlCheckBox(AnnotatedObject anObject) throws IOException {

        write("<input type=\"checkbox\" name=\"");
        write(anObject.getAc());
        write("\"/>");
    }

    /**
     * Provide a context-sensitive link to the user manual.
     * Only a question mark is shown as visible text.
     */
    private void htmlHelp(String target) throws IOException {

        write( "<a href=\"" );
        write( helpLink );
        write( target );
        write( "\" target=\"new\"/><sup><b><font color=red>?</font></b></sup></a>" );
    }

    /**
     * Provide a context-sensitive link to the user manual.
     * The displayString is shown as visible text.
     */
    private void htmlHelp(String displayString, String target) throws IOException {

        write( "<a href=\"" );
        write( helpLink );
        write( target );
        write( "\" target=\"new\"/>" );
        write( displayString );
        write( "</a>" );
    }

    /**
     * Write one annotation to html
     */
    private void htmlView(Annotation anAnnotation) throws IOException {

        // Filter out remark and uniprot-dr-export (topics).
        // todo: Need to get the topics to mask from a properties file.
        String label = anAnnotation.getCvTopic().getShortLabel();
        if ((label.equals("remark")) || (label.equals("uniprot-dr-export"))) {
            return;
        }
        write("<tr bgcolor="
                + tableCellColor
                + ">");

        // Annotation topic
        write("<td>");
        htmlSearch(anAnnotation.getCvTopic());
        write("</td>");

        // Annotation description
        write("<td colspan=3>");

        if(label.equals("negative")) {
            //negative info needs highlighting...
            write("<font color=red>");
            write(anAnnotation.getAnnotationText());
            write("</font>");
        }
        else {
            write(anAnnotation.getAnnotationText());
        }
        write("</td>");

        write("</tr>\n");
    }

    /**
     * Write the annotation block to html
     */
    private void htmlViewAnnotation(AnnotatedObject anObject) throws IOException {

        Collection annot = anObject.getAnnotations();

        if (null != annot)
        {
            Iterator iter = annot.iterator();
            while(iter.hasNext()){
                htmlView((Annotation) iter.next());
            }
        } else {
        }
    }

    /**
     * Show the primaryid of a Xref in html.
     * It will be hyperlinked to the appropriate database
     * if the topic "search-url" in the corresponding
     * CvDatabase object is defined.
     */
    private void htmlViewPrimaryId(Xref anXref) throws IOException {

        write("<td>");

        String id = anXref.getPrimaryId();

        if (null != id) {

            // Check if the id can be hyperlinked

            String searchUrl = (String) dbUrls.get(anXref.getCvDatabase());
            if (null == searchUrl){
                // it has not yet been checked if there is a search-url for this db.
                Collection dbAnnotation = anXref.getCvDatabase().getAnnotations();
                if (null != dbAnnotation){
                    Iterator i = dbAnnotation.iterator();
                    while (i.hasNext()){
                        Annotation annot = (Annotation) i.next();
                        if(annot.getCvTopic().getShortLabel().equals("search-url")){
                            // save searchUrl for future use
                            searchUrl = annot.getAnnotationText();
                            break;
                        }
                    }
                }
                if (null == searchUrl) {
                    // The db has no annotation "search-url".
                    // Don't search again in the future.
                    searchUrl = "-";
                }
                dbUrls.put(anXref.getCvDatabase(), searchUrl);
            }
            if ( ! searchUrl.equals( "-" ) ) {
                // we have a proper search URL.
                // Hyperlink the id
                write("<a href=\"");
                write(SearchReplace.replace(searchUrl, "${ac}", id));
                write("\">");
                write(id);
                write("</a>");

            } else {
                // No hyperlinking possible
                write(id);
            }
        } else {
            write("-");
        }

        write("</td>");
    }

    /**
     * Write xref to html
     */
    private void htmlView(Xref anXref) throws IOException {

        write("<tr bgcolor="
                + tableCellColor
                + ">");

        // xref db
        write("<td>");
        htmlSearch(anXref.getCvDatabase());
        write("</td>");

        // xref primaryId
        htmlViewPrimaryId(anXref);

        // xref secondaryId
        write("<td>");
        if (null != anXref.getSecondaryId()) {
            write(anXref.getSecondaryId());
        } else {
            write("-");
        }
        write("</td>");

        // xref qualifier
        write("<td>");
        if (null != anXref.getCvXrefQualifier()) {
            htmlHelp("Type:", "Xref.cvXrefType");
            write(" ");
            htmlSearch(anXref.getCvXrefQualifier());
        } else {
            write("-");
        }
        write("</td>");
        write("</tr>\n");
    }

    /**
     * Write a complete crossreference block to html
     */
    private void htmlViewXref(AnnotatedObject anObject) throws IOException {

        Collection annot = anObject.getXrefs();

        if (null != annot)
        {
            Iterator iter = annot.iterator();
            while(iter.hasNext()){
                htmlView((Xref) iter.next());
            }
        } else {
        }
    }

    /**
     * The default html view for an AnnotatedObject.
     * @param anAnnotatedObject
     * @throws java.io.IOException
     */
    public void htmlView(AnnotatedObject anAnnotatedObject) throws IOException {
        // Start table
        beginTable(anAnnotatedObject);

        // Header
        htmlViewHead(anAnnotatedObject, false);

        // Data
        htmlViewData(anAnnotatedObject);

        // Annotation
        htmlViewAnnotation(anAnnotatedObject);

        // Xref
        htmlViewXref(anAnnotatedObject);

        // End table
        endTable(anAnnotatedObject);
    }

    /**
     * Display a table line for an interaction partner: <code>partner</code>.
     * We display a link allowing to search for the interactions <code>partner</code>
     * is appearing in.
     * <code>prefix</code> and <code>postfix</code> allows to wrap all string of that line
     * with HTML code (for highlight purpose).
     *
     * @param partner the partner we displays in that line
     * @param interactions all interactions that partner is interaction in.
     * @param rowColor the backgroung color of the row.
     * @param binaryLink if true, the link (Query with <code>Protein.shortLabel</code>) will
     *                   gives an other binary view. <b>false</b> will gives the Protein view.
     * @param prefix wrapping prefix for the text off all cells.
     * @param postfix wrapping postfix for the text off all cells.
     * @throws IOException
     */
    private void processLine (Interactor partner,
                              Collection interactions,
                              String rowColor,
                              boolean binaryLink,
                              String prefix,
                              String postfix)
            throws IOException {

        // build the column 'view # interactions'
        StringBuffer buf = new StringBuffer(128);
        int interactionCount;


        String acList = null;
        String text = null;
        if ( interactions != null ) { // we are processing a partner
            interactionCount = interactions.size();
            for ( Iterator iterator = interactions.iterator (); iterator.hasNext (); ) {
                Interaction interaction = (Interaction) iterator.next ();
                buf.append( interaction.getAc() );
                buf.append( ',' );
            }
            int length = buf.length() - 1;
            buf.deleteCharAt( length );
            acList = buf.toString();

            buf.append( prefix );
            buf.append( "View " + interactionCount +
                        " Interaction" + (interactionCount > 1 ? "s" : "") );
            buf.append( postfix );

            /* buf = "ebi-1,ebi2,ebi3" + "View # Interactions"
             *                         |
             *                       length
             */
            text = buf.substring( length, buf.length() );
        }

        // write the line
        write("<tr bgcolor=\"" + rowColor + "\">" );
        write( "<td>" );
        htmlCheckBox( partner );
        write( "</td><td>" );

        write( prefix );
        write( partner.getShortLabel() );
        write( postfix );

        write( "</td><td>" );

        write( prefix );
        write( partner.getFullName() );
        write( postfix );

        write( "</td><td>" );
        if (text != null) {
            htmlSearch( acList, "Interaction", text); // interaction link
        }
        write( "</td><td>" );

        write( prefix );
        if ( binaryLink == true ) {
            htmlSearch( partner.getShortLabel(), "Protein", "Query with " + partner.getShortLabel() );
        } else {
            htmlSearch( partner.getShortLabel(), null, "Query with " + partner.getShortLabel() );
        }
        write( postfix );

        write( "</td></tr>" );
        write( "\n" );
    }

    /**
     * Displays a interaction partner table for <code>BinaryData</code> data structure.
     *
     * @param binaryData representation of the interaction partners.
     * @throws IOException
     */
    public void htmlView( BinaryDetailsViewBean.BinaryData binaryData ) throws IOException {

        HashMap results = binaryData.getData();

        write("<table width=100% bgcolor=\""
                    + tableBackgroundColor
                    + "\">");

        Iterator i = results.keySet().iterator();
        while (i.hasNext()){
            Interactor query = (Interactor) i.next();

            /* display that line with all field bold,
             * The last cell displays the Protein view.
             */
            processLine( query, null, tableHeaderColor, true, "<b>", "</b>" );
            write( "<tr bgcolor=\"" + tableHeaderColor + "\"><td colspan=\"5\">interacts with</td></tr>" );

            HashMap currentResults = (HashMap) results.get(query);
            Iterator j = currentResults.keySet().iterator();
            while(j.hasNext()){
                Interactor partner = (Interactor) j.next();
                /* display that line without highlight on the text,
                 * The last cell displays the binary view for that partner.
                 */
                processLine( partner, (Collection) currentResults.get(partner), tableCellColor, false, "", "" );
            }
        }

        write("</table>");
    }

    /**
     * HTML view of a CvObject. This is the view showing only the CvObject
     * shortLabel, with a hyperlink to the definition.
     * Usually used as a component in other htmlViews.
     *
     * @param term
     * @throws java.io.IOException
     */
    private void htmlViewPartial(CvObject term, String helpString, String helpTarget) throws IOException {

        write("<td>");

        if (null!= term){
            htmlHelp(helpString + ":", helpTarget);
            write(" ");
            htmlSearch(term);
        } else {
            write("-");
        }
        write("</td>");
    }


    /**
     * Write the header of an AnnotatedObject to html
     *
     */
    private void htmlViewHead( AnnotatedObject anAnnotatedObject,
                               boolean checkBox ) throws IOException {

        write("<tr bgcolor="
                + tableHeaderColor
                + ">");


        write("<td class=objectClass>");
        write("<nobr>");

        // Checkbox
        if (checkBox){
            htmlCheckBox(anAnnotatedObject);
        }

        // Class name
        write("<b>");

        String className = getObjectName( anAnnotatedObject );

        write( className );
        write("</b>");

        // Link to online help
        htmlHelp("search.TableLayout");

        write("</nobr>");
        write("</td>");

        // ac
        write("<td>");
        htmlHelp("Ac:", "BasicObject.ac");
        write(" ");
        write(anAnnotatedObject.getAc());
        write("</td>");

        // shortLabel
        write("<td>");
        htmlHelp("Name:", "AnnotatedObject.shortLabel");
        write(" ");
        htmlSearch(anAnnotatedObject);
        write("</td>");

        // fullName
        write("<td rowspan=2>");
        if (null!= anAnnotatedObject.getFullName()){
            write(anAnnotatedObject.getFullName());
        } else {
            write("-");
        }
        write("</td>");
        write("</tr>\n");
    }


    /**
     * Default method writing the attributes (data content) of an object
     * to html.
     *
     * @param anAnnotatedObject
     */
    private void htmlViewData(AnnotatedObject anAnnotatedObject)
            throws IOException {

        write("<tr bgcolor="
                + tableHeaderColor
                + ">");

        // empty cell to get to the total of three cells per row
        write("<td colspan=3>");
        write("&nbsp;");
        write("</td>");

        write("</tr>\n");
    }


    /**
     * Shows the attributes of a Protein object.
     *
     * @param aProtein
     * @throws java.io.IOException
     */
    private void htmlViewData(Protein aProtein) throws IOException {

        write("<tr bgcolor="
                + tableHeaderColor
                + ">");

        // Biosource
        htmlViewPartial(aProtein.getBioSource(), "Source:", "Interactor.bioSource");

        // CRC64
        write("<td>");
        if (null!= aProtein.getCrc64()){
            htmlHelp("Crc64:", "Protein.crc64");
            write(" ");
            write(aProtein.getCrc64());
        } else {
            write("-");
        }
        write("</td>");

        // One empty cell to get to the total of three cells per row
        write("<td>");
        write("&nbsp;");
        write("</td>");

        write("</tr>\n");
    }


    /**
     * Shows the attributes of an Interaction object.
     *
     * @param anInteraction
     * @throws java.io.IOException
     */
    private void htmlViewData(Interaction anInteraction) throws IOException {

        write("<tr bgcolor="
                + tableHeaderColor
                + ">");

        // kD
        write("<td>");
        if (null!= anInteraction.getKD()){
            htmlHelp("kD:", "Interaction.kD");
            write(" ");
            write(String.valueOf(anInteraction.getKD()));
        } else {
            write("-");
        }
        write("</td>");

        // Interaction Type
        htmlViewPartial(anInteraction.getCvInteractionType(), "Type", "Intearction.CvInteractionType");

        // Biosource
        // @todo The bioSource within an Interaction should for now not be displayed.
        // htmlViewPartial(anInteraction.getBioSource(), "", "");

        // One empty cell to get to the total of three cells per row
        write("<td>");
        write("&nbsp;");
        write("</td>");

        write("</tr>\n");
    }


    /**
     * Partial bioSource view.
     *
     * @param source
     * @throws java.io.IOException
     */
    private void htmlViewPartial(BioSource source, String helpString, String helpTarget) throws IOException {

        // Biosource
        write("<td>");
        if (null != source){
            htmlHelp(helpString, helpTarget);
            write(" ");
            htmlSearch(source);
        } else {
            write("-");
        }
        write("</td>");
    }

    /**
     * Writes attributes of an Experiment to html.
     *
     * @param exp
     * @throws java.io.IOException
     */
    private void htmlViewData(Experiment exp) throws IOException {

        write("<tr bgcolor="
                + tableHeaderColor
                + ">");

        htmlViewPartial(exp.getCvInteraction(), "Interaction identification", "Experiment.cvInteraction");
        htmlViewPartial(exp.getCvIdentification(), "Participant identification", "Experiment.cvIdentification");

        // Biosource
        htmlViewPartial(exp.getBioSource(), "Host:", "Experiment.bioSource");

        write("</tr>\n");
    }

    /**
     * Provides a link to the search application for an object.
     *
     * @param target Identifier to search for, e.g. accession number.
     * @param searchClass Class to search in, normally class of the object.
     * @param text Text to display as the hyperlink, e.g. shortLabel.
     * @throws java.io.IOException
     */
    private void htmlSearch(String target,
                            String searchClass,
                            String text) throws IOException {

        //This nobr adds too many....
        //write("<nobr>");
        // TODO: don't hard code the application path !! Try to give it in the constructor.
        write("<a href=\""+ contextPath +"/do/search?searchString=");
        write(target);

        if (searchClass != null) {
            write("&amp;" );
            write( "searchClass=");
            write(searchClass);
        }

        write("\">");

        boolean doHighlight = toHighlight.contains( text );
        if ( doHighlight ){
            write("<b><i>");
        }

        write(text);

        if ( doHighlight ){
            write("</i></b>");
        }
        write("</a>");
        //this nobr adds too many..
        //write("</nobr>");

    }

    /**
     * Provides a hyperlinked shortLabel of an object, the link pointing to
     * a full representation of the object.
     *
     * @param obj
     * @throws java.io.IOException
     */
    private void htmlSearch( AnnotatedObject obj ) throws IOException {

        String className = getObjectName( obj );
        htmlSearch(obj.getAc(), className, obj.getShortLabel());
    }


    /**
     * From the real className of an object, gets a displayable name.
     *
     * @param obj the object for which we want the class name to display
     * @return the classname to display in the view.
     */
    private String getObjectName( Object obj ) {
        return IntactHelper.getDisplayableClassName( obj );
    }

    /**
     * Provies text as a hyperlink to the object obj.
     *
     * @param obj
     * @param text
     * @throws java.io.IOException
     */
    private void htmlSearch(AnnotatedObject obj, String text) throws IOException {

        String className = getObjectName( obj );
        htmlSearch(obj.getAc(), className, text);
    }

    /**
     * Shows a component as part of the htmlView of an Interaction.
     *
     * @param comp
     * @throws java.io.IOException
     */
    private void htmlViewPartial(Component comp, boolean hasNext) throws IOException {

        // Get data
        Interactor act = comp.getInteractor();
        String label   = act.getShortLabel();
        CvComponentRole componentRole = comp.getCvComponentRole();
        String role = componentRole.getShortLabel();

        // avoid that a checkbox appears at the end of a line
        write("<nobr>");

        // Checkbox
        htmlCheckBox( act );

        // Hyperlinked object reference
        htmlSearch( act );

        // The component role (e.g. bait or prey)
        htmlSearch( componentRole,
                    "<sup>" + role.substring( 0, 1 ) + "</sup>");

        // displays a comma only if there is an other one coming after.
        if ( hasNext ) write(",");

        // avoid that a checkbox appears at the end of a line
        write("</nobr>");


        // write spaces to fill up to the "normal" length of component shortlabels
        for(int i = label.length(); i<FORMATTED_LABEL_LENGTH; i++){
            write("&nbsp;");
        }
        write("\n");
    }

    /**
     * HTML view of a ProteinProxy object.
     * <br>
     * Just forward to the Protein view.
     *
     * @param aProtein
     * @throws java.io.IOException
     */
    public void htmlView(ProteinImpl aProtein) throws IOException {
        htmlView( (Protein) aProtein );
    }

    /**
     * HTML view of a Protein object.
     *
     * @param aProtein
     * @throws java.io.IOException
     */
    public void htmlView(Protein aProtein) throws IOException {

        // Start table
        beginTable(aProtein);

        htmlViewHead(aProtein, true);
        htmlViewData(aProtein);
        htmlViewAnnotation(aProtein);
        htmlViewXref(aProtein);

        // Sequence
        write("<tr bgcolor="
                + tableCellColor
                + ">");

        write("<td colspan=4><font face=\"Courier New, Courier, monospace\">");
        String seq = aProtein.getSequence();
        if (seq != null) {
            int blocks = seq.length() / SEQBLOCKLENGTH;
            for (int i = 0; i< blocks; i++){
                write(seq.substring( i * SEQBLOCKLENGTH,
                                        i * SEQBLOCKLENGTH + SEQBLOCKLENGTH ));
                write(" ");
            }
            write(seq.substring(blocks*SEQBLOCKLENGTH));

            write("</font></td>");
        } else {
            write ("<font color=\"#898989\">No sequence available for that protein.</font>");
        }

        write("</tr>\n");

        // End table
        endTable(aProtein);
    }

    /**
     * TODO as to be refined
     * @param cvDatabase
     * @throws IOException
     */
    public void htmlView (CvDatabase cvDatabase) throws IOException {

        htmlView ( (AnnotatedObject) cvDatabase );
    }

    /**
     * TODO as to be refined
     * @param qualifier
     * @throws IOException
     */
    public void htmlView (CvXrefQualifier qualifier) throws IOException {

        htmlView ( (AnnotatedObject) qualifier );
    }

    /**
     * TODO as to be refined
     * @param bioSource
     * @throws IOException
     */
    public void htmlView (BioSource bioSource) throws IOException {

        htmlView ( (AnnotatedObject) bioSource );
    }

    /**
     * TODO as to be refined
     * @param topic
     * @throws IOException
     */
    public void htmlView (CvTopic topic) throws IOException {

        htmlView ( (AnnotatedObject) topic );
    }

    /**
     * TODO as to be refined
     * @param interaction
     * @throws IOException
     */
    public void htmlView (CvInteraction interaction) throws IOException {

        htmlView ( (AnnotatedObject) interaction );
    }

    /**
     * TODO as to be refined
     * @param interactionType
     * @throws IOException
     */
    public void htmlView (CvInteractionType interactionType) throws IOException {

        htmlView ( (AnnotatedObject) interactionType );
    }

    /**
     * TODO as to be refined
     * @param componentRole
     * @throws IOException
     */
    public void htmlView (CvComponentRole componentRole) throws IOException {

        htmlView ( (AnnotatedObject) componentRole );
    }

    /**
     * TODO as to be refined
     * @param cvIdentification
     * @throws IOException
     */
    public void htmlView (CvIdentification cvIdentification) throws IOException {

        htmlView ( (AnnotatedObject) cvIdentification );
    }

    /**
     * HTML view of an InteractionProxy object.
     * <br>
     * Just forward to the Interaction view.
     * @param act
     * @throws java.io.IOException
     */
    public void htmlView(InteractionImpl act) throws IOException {
        htmlView( (Interaction) act );
    }

    /**
     * HTML view of an Interaction object.
     *
     * @param act
     * @throws java.io.IOException
     */
    public void htmlView(Interaction act) throws IOException {

        // Start table
        beginTable(act);

        htmlViewHead(act, true);
        htmlViewData(act);
        htmlViewAnnotation(act);
        htmlViewXref(act);

        // Interactors
        write("<tr bgcolor="
                + tableCellColor
                + ">");

        write("<td colspan=\"4\">");

        write("<code>");
        Iterator c = act.getComponents().iterator();
        while (c.hasNext()) {
            Component component = (Component) c.next();
            htmlViewPartial( component, c.hasNext() );
        }
        write("</code>");


        write("</td>");
        write("</tr>\n");

        // End table
        endTable(act);
    }


    /** HTML view of an experiment.
     *
     * @param ex
     * @throws java.io.IOException
     */
    public void htmlView(Experiment ex) throws IOException {

        // Start table
        beginTable(ex);

        htmlViewHead(ex, false);
        htmlViewData(ex);
        htmlViewAnnotation(ex);
        htmlViewXref(ex);

        Iterator i = ex.getInteractions().iterator();

        int count = 0;
        while (i.hasNext()) {
            Interaction act = (Interaction) i.next();
            htmlView(act);
            if ( ++count == 10 ) {
                endTable( ex );

                rs.flush();

                beginTable( ex );
                count = 0;
            }
        }

        // End table
        endTable(ex);
    }

    public static final int MAX_CHUNK_IN_INDEX = 20;

    /**
     * Display the chunk index.
     * <br>
     * 1. displays the context: "Results x to y of about z objects."
     * 2. displays the chunk index table, it contains MAX_CHUNK_IN_INDEX chunk maximum.
     *
     * @param object the object for which we create that index of its chunk.
     * @param link the help link
     * @param currentChunk the user selected chunk to display
     * @param maxChunk the maximum number of chunk (ie. <code>objectCount /
     *                 uk.ac.ebi.intact.application.search2.business.Constants.MAX_CHUNK_SIZE)
     * @param objectCount the count of object (those we display in chunks).
     * @param chunkedObjectType the type of the object we chunk. Used to give a context to the user.
     * @throws IOException
     */
    public void buildChunkIndex ( Object object,
                                  String link,
                                  int currentChunk,
                                  int maxChunk,
                                  int objectCount,
                                  String chunkedObjectType ) throws IOException {

        int sub = MAX_CHUNK_IN_INDEX / 2; // count of chunk we display before and after the current one.

        // first chunk of the table
        int first = Math.max( ( currentChunk - sub ), 1 );

        // last chunk of the table
        int last  = Math.min( ( currentChunk + sub ), maxChunk );

        write("<p>");

        // give the range of object (those we decided to display in chaunk) we display in the page.
        // ie. if we process an Experiment, we give the range of Interactions.
        int from = ( (currentChunk - 1) * uk.ac.ebi.intact.application.search2.business.Constants.MAX_CHUNK_SIZE ) + 1;
        int to   = Math.min( (from + MAX_CHUNK_IN_INDEX), objectCount );

        // 1. context
        write( "Results <b>" + from + "</b> to <b>" + to + "</b> of <b>" +
               objectCount + "</b> " + chunkedObjectType );
        htmlHelp("search.display.chuncked");
        write( ".<br>" );

        // 2. the chunk index table
        write("<table width=100% bgcolor=\""
                    + tableBackgroundColor
                    + "\">");

        write("<tr bgcolor="
              + tableCellColor
              + ">");

        write("<td width=\"100%\" align=\"center\">");

        String prefixUrl = "<a href=\""+ contextPath +"/do/search?searchString=" + ((IntactObject)object).getAc() + "&selectedChunk=";

        if (currentChunk > 1) {
            write( prefixUrl + (currentChunk - 1) + "\">");
            write("<font color=\""+ tableBackgroundColor +"\"><strong>Previous</strong></font>");
            write("</a>");
            write("&nbsp;&nbsp;&nbsp;&nbsp;"); // spaces
        }

        for( int idx=first; idx <= last; idx++) {

            if( idx == currentChunk ) {
                // the selected chunk is not a link, and is highlighted
                write("<font color=\"red\"><strong>" + idx + "</strong></font>");
            } else {
                write( prefixUrl + idx + "\">");
                write("<font color=\""+ tableBackgroundColor +"\">" + idx + "</font>");
                write("</a>");
            }

            write("&nbsp;");
        }

        if ( currentChunk < maxChunk ) {
            write("&nbsp;&nbsp;&nbsp;&nbsp;"); // spaces
            write( prefixUrl + (currentChunk + 1) + "\">");
            write("<font color=\""+ tableBackgroundColor +"\"><strong>Next</strong></font>");
            write("</a>");
        }

        write("</td>");
        write("</tr>");
        write("</table>");
        write("</p>");
    }

    /** Write an experiment view to html
     *
     * @param args [0] The shortlabel of the experiment
     * @throws java.lang.Exception
     */
//    public static void main(String[] args) throws Exception {
//
//        IntactHelper helper = null;
//        try {
//            helper = new IntactHelper();
//
//        } catch (IntactException ie) {
//
//            //something failed with type map or datasource...
//            String msg = "unable to create intact helper class";
//            logger.info( msg, ie);
//        }
//
//        // Create a new Writer, associate it with system.out
//        Writer out = new BufferedWriter(new OutputStreamWriter(System.out));
//
//        long start = System.currentTimeMillis();
//
//        for (int count = 0; count < args.length; count++) {
//
//            Experiment ex = (Experiment) helper.getObjectByLabel(Experiment.class, args[count]);
//            if (null != ex) {
//                HashSet queryLabels = new HashSet();
//                queryLabels.add(ex.getShortLabel());
//                HtmlBuilder getView = new HtmlBuilder(out, queryLabels);
//
//                getView.htmlView(ex);
//            }
//
//            Protein p = (Protein) helper.getObjectByLabel(Protein.class, args[count]);
//            if (null != p) {
//                HashSet queryLabels = new HashSet();
//                queryLabels.add(p.getShortLabel());
//                HtmlBuilder htmlBuilder = new HtmlBuilder( out, queryLabels );
//
//                htmlBuilder.htmlView(p);
//            }
//
//            AnnotatedObject obj = (AnnotatedObject) helper.getObjectByLabel(AnnotatedObject.class, args[count]);
//            if (null != obj) {
//                HashSet queryLabels = new HashSet();
//                queryLabels.add(obj.getShortLabel());
//                HtmlBuilder getView = new HtmlBuilder(out, queryLabels);
//
//                getView.htmlView(obj);
//            }
//
//            long current = System.currentTimeMillis();
//            System.err.println(current - start);
//            start = current;
//
//            // Write after each data item
//            out.flush();
//        }
//    }
}
