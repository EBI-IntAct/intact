/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.uniprotExport;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * That class .
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class LineExport {

    protected static String TIME;

    static {
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd@HH.mm" );
        TIME = formatter.format( new Date() );
        formatter = null;
    }

    //////////////////////////
    // Constants

    protected static final String METHOD_EXPORT_KEYWORK_EXPORT = "yes";
    protected static final String METHOD_EXPORT_KEYWORK_DO_NOT_EXPORT = "no";

    protected static final String EXPERIMENT_EXPORT_KEYWORK_EXPORT = "yes";
    protected static final String EXPERIMENT_EXPORT_KEYWORK_DO_NOT_EXPORT = "no";

    protected static final String NEW_LINE = System.getProperty( "line.separator" );
    protected static final char TABULATION = '\t';

    ////////////////////////////
    // Inner Class

    public class ExperimentStatus {

        // Experiment status
        public static final int EXPORT = 0;
        public static final int DO_NOT_EXPORT = 1;
        public static final int NOT_SPECIFIED = 2;
        public static final int LARGE_SCALE = 3;

        private int status;
        private Collection keywords;

        public ExperimentStatus( int status ) {
            this.status = status;
        }

        public void setStatus( int status ) {
            this.status = status;
        }

        public Collection getKeywords() {
            return keywords;
        }

        public boolean doExport() {
            return status == EXPORT;
        }

        public boolean doNotExport() {
            return status == DO_NOT_EXPORT;
        }

        public boolean isNotSpecified() {
            return status == NOT_SPECIFIED;
        }

        public boolean isLargeScale() {
            return status == LARGE_SCALE;
        }

        public void addKeywords( Collection keywords ) {
            if ( keywords == null ) {
                throw new IllegalArgumentException( "Keywords must not be null" );
            }
            this.keywords = keywords;
        }

        public String toString() {

            StringBuffer sb = new StringBuffer( 128 );

            sb.append( "ExperimentStatus{ keywords= " );
            if ( keywords != null ) {
                for ( Iterator iterator = keywords.iterator(); iterator.hasNext(); ) {
                    String kw = (String) iterator.next();
                    sb.append( kw ).append( ' ' );
                }
            }

            sb.append( " status=" );
            switch ( status ) {
                case EXPORT:
                    sb.append( "EXPORT" );
                    break;
                case DO_NOT_EXPORT:
                    sb.append( "DO_NOT_EXPORT" );
                    break;
                case NOT_SPECIFIED:
                    sb.append( "NOT_SPECIFIED" );
                    break;
                case LARGE_SCALE:
                    sb.append( "LARGE_SCALE" );
                    break;
                default:
                    sb.append( "UNKNOWN VALUE !!!!!!!!!!!!!!!!!" );
            }
            sb.append( " }" );

            return sb.toString();
        }
    }


    public class CvInteractionStatus {

        // Method status
        public static final int EXPORT = 0;
        public static final int DO_NOT_EXPORT = 1;
        public static final int NOT_SPECIFIED = 2;
        public static final int CONDITIONAL_EXPORT = 3;

        private int status;
        private int minimumOccurence = 1;

        public CvInteractionStatus( int status ) {
            this.status = status;
        }

        public CvInteractionStatus( int status, int minimumOccurence ) {
            this.minimumOccurence = minimumOccurence;
            this.status = status;
        }

        public int getMinimumOccurence() {
            return minimumOccurence;
        }

        public boolean doExport() {
            return status == EXPORT;
        }

        public boolean doNotExport() {
            return status == DO_NOT_EXPORT;
        }

        public boolean isNotSpecified() {
            return status == NOT_SPECIFIED;
        }

        public boolean isConditionalExport() {
            return status == CONDITIONAL_EXPORT;
        }

        public String toString() {

            StringBuffer sb = new StringBuffer( 128 );

            sb.append( "CvInteractionStatus{ minimumOccurence=" ).append( minimumOccurence );

            sb.append( " status=" );
            switch ( status ) {
                case EXPORT:
                    sb.append( "EXPORT" );
                    break;
                case DO_NOT_EXPORT:
                    sb.append( "DO_NOT_EXPORT" );
                    break;
                case NOT_SPECIFIED:
                    sb.append( "NOT_SPECIFIED" );
                    break;
                case CONDITIONAL_EXPORT:
                    sb.append( "CONDITIONAL_EXPORT" );
                    break;
                default:
                    sb.append( "UNKNOWN VALUE !!!!!!!!!!!!!!!!!" );
            }
            sb.append( " }" );

            return sb.toString();
        }
    }


    protected class DatabaseContentException extends Exception {

        public DatabaseContentException( String message ) {
            super( message );
        }
    }


    /**
     * Service termination hook (gets called when the JVM terminates from a signal). eg.
     * <pre>
     * IntactHelper helper = new IntactHelper();
     * DatabaseConnexionShutdownHook dcsh = new DatabaseConnexionShutdownHook( helper );
     * Runtime.getRuntime().addShutdownHook( sh );
     * </pre>
     */
    protected static class DatabaseConnexionShutdownHook extends Thread {

        private IntactHelper helper;

        public DatabaseConnexionShutdownHook( IntactHelper helper ) {
            super();
            this.helper = helper;
            System.out.println( "Database Connexion Shutdown Hook installed." );
        }

        public void run() {
            System.out.println( "JDBCShutdownHook thread started" );
            if ( helper != null ) {
                try {
                    helper.closeStore();
                    System.out.println( "Connexion to the database closed." );
                } catch ( IntactException e ) {
                    System.err.println( "Could not close the connexion to the database." );
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Service termination hook (gets called when the JVM terminates from a signal). eg.
     * <pre>
     * IntactHelper helper = new IntactHelper();
     * DatabaseConnexionShutdownHook dcsh = new DatabaseConnexionShutdownHook( helper );
     * Runtime.getRuntime().addShutdownHook( sh );
     * </pre>
     */
    protected static class CloseFileOnShutdownHook extends Thread {

        private BufferedWriter outputBufferedWriter;
        private FileWriter outputFileWriter;

        public CloseFileOnShutdownHook( BufferedWriter outputBufferedWriter, FileWriter outputFileWriter ) {
            super();
            this.outputBufferedWriter = outputBufferedWriter;
            this.outputFileWriter = outputFileWriter;

            System.out.println( "Output File close on Shutdown Hook installed." );
        }

        public void run() {
            if ( outputFileWriter != null ) {
                try {
                    outputFileWriter.close();
                } catch ( IOException e ) {
                    System.out.println( "An error occured when trying to close the output file" );
                    return;
                }
            }

            if ( outputFileWriter != null ) {
                try {
                    outputFileWriter.close();
                } catch ( IOException e ) {
                    System.out.println( "An error occured when trying to close the output file" );
                    return;
                }
            }
            System.out.println( "Output file is now closed." );
        }
    }

    //////////////////////////////
    // Attributes

    // Vocabulary that we need for processing
    protected CvXrefQualifier identityXrefQualifier = null;
    protected CvXrefQualifier isoformParentQualifier = null;
    protected CvXrefQualifier primaryReferenceQualifier = null;

    protected CvDatabase uniprotDatabase = null;
    protected CvDatabase intactDatabase = null;
    protected CvDatabase pubmedDatabase = null;

    protected CvTopic uniprotDR_Export = null;
    protected CvTopic uniprotCC_Export = null;
    protected CvTopic authorConfidenceTopic = null;
    protected CvTopic negativeTopic = null;
    protected CvTopic ccNoteTopic = null;
    protected CvTopic noUniprotUpdate = null;

    protected CvAliasType geneNameAliasType;
    protected CvAliasType locusNameAliasType; // locus-name
    protected CvAliasType orfNameAliasType; // orf-name

    /**
     * Cache the CvInteraction property for the export. CvInteraction.ac -> Boolean.TRUE or Boolean.FALSE
     */
    protected HashMap cvInteractionExportStatusCache = new HashMap();

    /**
     * Cache the Experiment property for the export. Experiment.ac -> Integer (EXPORT, DO_NOT_EXPORT, NOT_SPECIFIED)
     */
    protected HashMap experimentExportStatusCache = new HashMap();

    protected boolean debugEnabled = false; // protected to allow the testcase to modify it.

    protected boolean debugFileEnabled = false;

    protected BufferedWriter outputBufferedWriter;
    protected FileWriter outputFileWriter;

    /////////////////////////////
    // Methods

    /**
     * Load the minimal Controlled vocabulary needed during the processing. It at least one term is missing, an
     * Exception is thrown.
     *
     * @param helper data access
     *
     * @throws uk.ac.ebi.intact.business.IntactException
     *          search error
     * @throws uk.ac.ebi.intact.util.uniprotExport.CCLineExport.DatabaseContentException
     *          if at least one term is missing
     */
    public void init( IntactHelper helper ) throws IntactException, DatabaseContentException {

        uniprotDatabase = (CvDatabase) getCvObject( helper, CvDatabase.class, CvDatabase.UNIPROT_MI_REF, CvDatabase.UNIPROT );
        intactDatabase = (CvDatabase) getCvObject( helper, CvDatabase.class, CvDatabase.INTACT_MI_REF, CvDatabase.INTACT );
        pubmedDatabase = (CvDatabase) getCvObject( helper, CvDatabase.class, CvDatabase.PUBMED_MI_REF, CvDatabase.PUBMED );

        identityXrefQualifier = (CvXrefQualifier) getCvObject( helper, CvXrefQualifier.class, CvXrefQualifier.IDENTITY_MI_REF, CvXrefQualifier.IDENTITY );
        isoformParentQualifier = (CvXrefQualifier) getCvObject( helper, CvXrefQualifier.class, CvXrefQualifier.ISOFORM_PARENT_MI_REF, CvXrefQualifier.ISOFORM_PARENT );
        primaryReferenceQualifier = (CvXrefQualifier) getCvObject( helper, CvXrefQualifier.class, CvXrefQualifier.PRIMARY_REFERENCE_MI_REF, CvXrefQualifier.PRIMARY_REFERENCE );

        // note: CvTopic do not have MI references.
        uniprotDR_Export = (CvTopic) getCvObject( helper, CvTopic.class, CvTopic.UNIPROT_DR_EXPORT );
        uniprotCC_Export = (CvTopic) getCvObject( helper, CvTopic.class, CvTopic.UNIPROT_CC_EXPORT );
        authorConfidenceTopic = (CvTopic) getCvObject( helper, CvTopic.class, CvTopic.AUTHOR_CONFIDENCE );
        negativeTopic = (CvTopic) getCvObject( helper, CvTopic.class, CvTopic.NEGATIVE );
        ccNoteTopic = (CvTopic) getCvObject( helper, CvTopic.class, CvTopic.CC_NOTE );
        noUniprotUpdate = (CvTopic) getCvObject( helper, CvTopic.class, CvTopic.NON_UNIPROT );

        geneNameAliasType = (CvAliasType) getCvObject( helper, CvAliasType.class, CvAliasType.GENE_NAME_MI_REF, CvAliasType.GENE_NAME );
        locusNameAliasType = (CvAliasType) getCvObject( helper, CvAliasType.class, CvAliasType.LOCUS_NAME_MI_REF, CvAliasType.LOCUS_NAME );
        orfNameAliasType = (CvAliasType) getCvObject( helper, CvAliasType.class, CvAliasType.ORF_NAME_MI_REF, CvAliasType.ORF_NAME );
    }

    /**
     * Get a CvObject based on its class name and its shortlabel.
     *
     * @param helper     database access
     * @param clazz      the Class we are looking for
     * @param shortlabel the shortlabel of the object we are looking for
     *
     * @return the CvObject of type <code>clazz</code> and having the shortlabel <code>shorltabel<code>.
     *
     * @throws IntactException          if the search failed
     * @throws DatabaseContentException if the object is not found.
     */
    private CvObject getCvObject( IntactHelper helper, Class clazz, String shortlabel )
            throws IntactException,
                   DatabaseContentException {

        return getCvObject( helper, clazz, null, shortlabel );
    }

    /**
     * Get a CvObject based on its class name and its shortlabel. <br>
     * If specified, the MI reference will be used first, then only the shortlabel.
     *
     * @param helper     database access
     * @param clazz      the Class we are looking for
     * @param mi         the mi reference of the CvObject (if any, otherwise: null)
     * @param shortlabel the shortlabel of the object we are looking for
     *
     * @return the CvObject of type <code>clazz</code> and having the shortlabel <code>shorltabel<code>.
     *
     * @throws IntactException          if the search failed
     * @throws DatabaseContentException if the object is not found.
     */
    private CvObject getCvObject( IntactHelper helper, Class clazz, String mi, String shortlabel )
            throws IntactException,
                   DatabaseContentException {

        if( mi == null && shortlabel == null ) {
            throw new IllegalArgumentException( "You must give at least a MI reference or a shortlabel of the CV you are looking for." );
        }

        CvObject cv = null;

        if( mi != null ) {
            cv = (CvObject) helper.getObjectByPrimaryId( clazz, mi );
            if( cv == null ) {
                System.err.println( "The MI reference you gave doesn't exists. Using the shortlabel instead." );
            }
        }

        if( cv == null ) {
            cv = (CvObject) helper.getObjectByLabel( clazz, shortlabel );
        }

        if ( cv == null ) {
            StringBuffer sb = new StringBuffer( 128 );
            sb.append( "Could not find " );
            sb.append( clazz.getName() );
            sb.append( ' ' );
            sb.append( shortlabel );
            if( mi != null ) {
                sb.append( ' ' );
                sb.append( '(' ).append( mi ).append( ')' );
            }
            sb.append( " in your IntAct node" );

            throw new DatabaseContentException( sb.toString() );
        }

        return cv;
    }

    /**
     * @param flag
     */
    protected void setDebugEnabled( boolean flag ) {
        debugEnabled = flag;
    }

    /**
     * @param enabled
     */
    protected void setDebugFileEnabled( boolean enabled ) {
        debugFileEnabled = enabled;

        if ( enabled ) {
            // create the output file if the user requested it.
            String filename = "export2uniprot_verboseOutput_" + TIME + ".txt";
            File file = new File( filename );
            System.out.println( "Save verbose output to: " + file.getAbsolutePath() );
            outputBufferedWriter = null;
            outputFileWriter = null;
            try {
                outputFileWriter = new FileWriter( file );
                outputBufferedWriter = new BufferedWriter( outputFileWriter );

                Runtime.getRuntime().addShutdownHook( new CloseFileOnShutdownHook( outputBufferedWriter, outputFileWriter ) );

            } catch ( IOException e ) {
                e.printStackTrace();
                debugFileEnabled = false;
            }
        }
    }

    /**
     * Get the uniprot primary ID from Protein and Splice variant.
     *
     * @param protein the Protein for which we want the uniprot ID.
     *
     * @return the uniprot ID as a String or null if none is found (should not occur)
     */
    public final String getUniprotID( final Protein protein ) {

        String uniprot = null;

        Collection xrefs = protein.getXrefs();
        boolean found = false;
        for ( Iterator iterator = xrefs.iterator(); iterator.hasNext() && !found; ) {
            Xref xref = (Xref) iterator.next();

            if ( uniprotDatabase.equals( xref.getCvDatabase() ) &&
                 identityXrefQualifier.equals( xref.getCvXrefQualifier() ) ) {
                uniprot = xref.getPrimaryId();
                found = true;
            }
        }

        return uniprot;
    }

    /**
     * Get the intact master AC from the given Splice variant.
     *
     * @param protein the splice variant (Protein) for which we its intact master AC.
     *
     * @return the intact AC
     */
    public final String getMasterAc( final Protein protein ) {

        String ac = null;

        Collection xrefs = protein.getXrefs();
        boolean found = false;
        for ( Iterator iterator = xrefs.iterator(); iterator.hasNext() && !found; ) {
            Xref xref = (Xref) iterator.next();

            if ( intactDatabase.equals( xref.getCvDatabase() ) &&
                 isoformParentQualifier.equals( xref.getCvXrefQualifier() ) ) {
                ac = xref.getPrimaryId();
                found = true;
            }
        }

        return ac;
    }

    /**
     * Fetches the master protein of a splice variant. <br> Nothing is returned if the protein is not a splice variant
     * or if the splice variant doesn't have a valid Xref to the IntAct database.
     *
     * @param protein the protein for which we want a splice variant
     * @param helper  the data source
     *
     * @return a Protein or null is nothing is found or an error occurs.
     */
    protected Protein getMasterProtein( Protein protein, IntactHelper helper ) {

        Protein master = null;

        String ac = getMasterAc( protein );

        if ( ac != null ) {
            // search for that Protein
            Collection proteins = null;
            try {
                proteins = helper.search( Protein.class, "ac", ac );
            } catch ( IntactException e ) {
                e.printStackTrace();
            }

            if ( proteins != null ) {
                if ( !proteins.isEmpty() ) {
                    master = (Protein) proteins.iterator().next();
                } else {
                    System.err.println( "Could not find the master protein (AC: " + ac +
                                        " ) of the splice variant AC: " + protein.getAc() );
                }
            } else {
                System.err.println( "An error occured when searching the IntAct database for Protein having the AC: " + ac );
            }
        } else {
            System.err.println( "Could not find a master protein AC in the Xrefs of the protein: " +
                                protein.getAc() + ", " + protein.getShortLabel() );
        }

        return master;
    }

    /**
     * Get all interaction related to the given Protein.
     *
     * @param protein the protein of which we want the interactions.
     *
     * @return a Collection if Interaction.
     */
    protected final List getInteractions( final Protein protein ) {
        Collection components = protein.getActiveInstances();
        List interactions = new ArrayList( components.size() );

        for ( Iterator iterator = components.iterator(); iterator.hasNext(); ) {
            Component component = (Component) iterator.next();

            Interaction interaction = component.getInteraction();

            if ( !interactions.contains( interaction ) ) {
                interactions.add( interaction );
            }
        }

        return interactions;
    }

    /**
     * Tells us if an Interaction is binary or not.<br>
     * <pre>
     *      Rules:
     *         - the sum of the stoichiometry of the components must be 2 (2*1 or 1*2)
     *         - the interacting partner must be UniProt proteins
     * </pre>
     *
     * @param interaction the interaction we are interrested in.
     *
     * @return true if the interaction is binary, otherwise false.
     */
    public boolean isBinary( Interaction interaction ) {

        boolean isBinaryInteraction = false;
        // if that interaction has not exactly 2 interactors, it is not taken into account

        if ( interaction.getComponents() != null ) {

            int componentCount = interaction.getComponents().size();

            if ( componentCount == 2 ) {

                // check that the stochiometry is 1 for each component
                Iterator iterator1 = interaction.getComponents().iterator();

                Component component1 = (Component) iterator1.next();
                float stochio1 = component1.getStoichiometry();

                Component component2 = (Component) iterator1.next();
                float stochio2 = component2.getStoichiometry();

                if ( stochio1 == 1 && stochio2 == 1 ) {

                    isBinaryInteraction = true;

                } else {

                    log( "\t\t\t Interaction has 2 interactors but stochio are " + stochio1 + " and " + stochio2 + ", we don't export it." );
                    isBinaryInteraction = false;
                }

            } else if ( componentCount == 1 ) {

                // check that the stochiometry is 2
                Iterator iterator1 = interaction.getComponents().iterator();
                Component component1 = (Component) iterator1.next();
                if ( component1.getStoichiometry() == 2 ) {

                    isBinaryInteraction = true;

                } else {

                    log( "\t\t\t Interaction has 1 interactors but stochio is " + component1.getStoichiometry() + " (should be 1), we don't export it." );
                    isBinaryInteraction = false;
                }
            } else {

                log( "\t\t\t Interaction (" + interaction.getShortLabel() + ") is not binary (" + componentCount +
                     " component(s)), we don't export it." );
                isBinaryInteraction = false;
            }
        }

        if ( isBinaryInteraction ) {
            // then test if all interactors are UniProt Proteins
            for ( Iterator iterator = interaction.getComponents().iterator(); iterator.hasNext()
                                                                              && isBinaryInteraction; ) {
                Component component = (Component) iterator.next();

                Interactor interactor = component.getInteractor();
                if ( interactor instanceof Protein ) {

                    Protein protein = (Protein) interactor;

                    // check that the protein is a UniProt protein
                    String uniprotID = getUniprotID( protein );
                    if ( uniprotID == null ) {
                        isBinaryInteraction = false; // stop the loop, involve a protein without uniprot ID

                        log( "\t\t\t Interaction is binary but doesn't involve only UniProt proteins (eg. "+
                             protein.getAc() +" / "+ protein.getShortLabel() +"). " );
                    } else {

                        // check that the protein doesn't have a no-uniprot-update annotation
                        if( ! needsUniprotUpdate( protein ) ) {
                            isBinaryInteraction = false; // stop the loop, Protein having no-uniprot-update involved

                            log( "\t\t\t Interaction is binary but at least one UniProt protein is flagged '"+
                                 CvTopic.NON_UNIPROT +"' (eg. "+ protein.getAc() +" / "+ protein.getShortLabel() +"). " );
                        }
                    }

                } else {
                    isBinaryInteraction = false; // stop the loop, that component doesn't involve a Protein

                    log( "\t\t\t Interaction is binary but at least one partner is not a Protein (ie. "+
                         interactor.getAc() +" / "+ interactor.getShortLabel() + " / " + component.getClass() +"). " );
                }
            } // components
        }

        return isBinaryInteraction;
    }

    /**
     * Log the message in STDOUT [AND/OR] a file.
     *
     * @param message
     */
    protected final void log( String message ) {

        if ( debugEnabled ) {
            System.out.println( message );
        }

        if ( debugFileEnabled ) {
            try {
                outputBufferedWriter.write( message );
                outputBufferedWriter.write( NEW_LINE );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Answers the question: does that technology is to be exported to SwissProt ? <br> Do give that answer, we check
     * that the CvInteration has an annotation having <br> <b>CvTopic</b>: uniprot-dr-export <br> <b>text</b>: yes [OR]
     * no [OR] &lt;integer value&gt;
     * <p/>
     * if <b>yes</b>: the method is meant to be exported <br> if <b>no</b>: the method is NOT meant to be exported <br>
     * if <b>&lt;integer value&gt;</b>: the protein linked to that method are ment to be exported only if they are seen
     * in a minimum number interactions belonging to distinct experiment. eg. let's set that value to 2 for Y2H. To be
     * eligible for export a protein must have been seen in at least 2 Y2H distinct experiment. </p>
     *
     * @param cvInteraction the method to check
     * @param logPrefix     indentatin of the log
     *
     * @return the status of that method (EXPORT, DO_NOT_EXPORT, NOT_SPECIFIED, CONDITIONAL_EXPORT) with an optional
     *         count.
     */
    public final CvInteractionStatus getMethodExportStatus( final CvInteraction cvInteraction, String logPrefix ) {

        CvInteractionStatus status = null;

        // cache the CvInteraction status
        if ( null != cvInteraction ) {

            CvInteractionStatus cache = (CvInteractionStatus) cvInteractionExportStatusCache.get( cvInteraction.getAc() );
            if ( null != cache ) {

//                log( logPrefix + "\t\t\t\t CvInteraction: Status already processed, retreived from cache." );
                status = cache;

            } else {

                boolean found = false;
                boolean multipleAnnotationFound = false;
                Collection annotations = null;

                annotations = cvInteraction.getAnnotations();

                log( logPrefix + "\t\t\t " + annotations.size() + " annotations found." );
                Annotation annotation = null;
                for ( Iterator iterator = annotations.iterator(); iterator.hasNext() && !multipleAnnotationFound; ) {
                    Annotation _annotation = (Annotation) iterator.next();
                    if ( uniprotDR_Export.equals( _annotation.getCvTopic() ) ) {

                        log( logPrefix + "\t\t\t\t Found uniprot-dr-export annotation: " + _annotation );

                        if ( true == found ) {
                            multipleAnnotationFound = true;
                            System.err.println( "There are multiple annotation having Topic:" + CvTopic.UNIPROT_DR_EXPORT +
                                                " in CvInteraction: " + cvInteraction.getShortLabel() +
                                                ". \nWe do not export." );
                        } else {
                            found = true;
                            annotation = _annotation;
                        }
                    }
                }


                if ( multipleAnnotationFound ) {

                    status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                    log( logPrefix + "\t\t\t multiple annotation found: do not export " );

                } else {

                    if ( true == found ) {

                        String text = annotation.getAnnotationText();
                        if ( null != text ) {
                            text = text.toLowerCase().trim();
                        }

                        if ( METHOD_EXPORT_KEYWORK_EXPORT.equals( annotation.getAnnotationText() ) ) {

                            status = new CvInteractionStatus( CvInteractionStatus.EXPORT );
                            log( logPrefix + "\t\t\t " + METHOD_EXPORT_KEYWORK_EXPORT + " found: export " );

                        } else if ( METHOD_EXPORT_KEYWORK_DO_NOT_EXPORT.equals( annotation.getAnnotationText() ) ) {

                            status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                            log( logPrefix + "\t\t\t " + METHOD_EXPORT_KEYWORK_DO_NOT_EXPORT + " found: do not export " );

                        } else {

                            log( logPrefix + "\t\t\t neither YES or NO found: should be an integer value... " );

                            // it must be an integer value, let's check it.
                            try {
                                Integer value = new Integer( text );
                                int i = value.intValue();

                                if ( i >= 2 ) {

                                    // value is >= 2
                                    status = new CvInteractionStatus( CvInteractionStatus.CONDITIONAL_EXPORT, i );
                                    log( logPrefix + "\t\t\t " + i + " found: conditional export " );

                                } else if ( i == 1 ) {

                                    String err = cvInteraction.getShortLabel() + " having annotation (" + CvTopic.UNIPROT_DR_EXPORT +
                                                 ") has an annotationText like <integer value>. Value was: " + i +
                                                 ", We consider it as to be exported.";
                                    log( err );

                                    status = new CvInteractionStatus( CvInteractionStatus.EXPORT );
                                    log( logPrefix + "\t\t\t integer == " + i + " found: export " );

                                } else {
                                    // i < 1

                                    String err = cvInteraction.getShortLabel() + " having annotation (" + CvTopic.UNIPROT_DR_EXPORT +
                                                 ") has an annotationText like <integer value>. Value was: " + i +
                                                 " However, having a value < 1 is not valid, We consider it as to be NOT exported.";
                                    System.err.println( err );

                                    status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                                    log( logPrefix + "\t\t\t integer < 1 (" + i + ") found: do not export " );
                                }

                            } catch ( NumberFormatException e ) {
                                // not an integer !
                                System.err.println( cvInteraction.getShortLabel() + " having annotation (" + CvTopic.UNIPROT_DR_EXPORT +
                                                    ") has an annotationText different from yes/no/<integer value> !!!" +
                                                    " value was: '" + text + "'." );
                                log( logPrefix + "\t\t\t not an integer:(" + text + ") found: do not export " );

                                status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                            }
                        }
                    } else {
                        // no annotation implies NO EXPORT !
                        System.err.println( cvInteraction.getShortLabel() +
                                            " doesn't have an annotation: " + CvTopic.UNIPROT_DR_EXPORT );
                        log( logPrefix + "\t\t\t not annotation found: do not export " );

                        status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                    }
                }

                // cache it !
                cvInteractionExportStatusCache.put( cvInteraction.getAc(), status );
            }
        }

        log( "\t\t CvInteractionExport status: " + status );

        return status;
    }

    /**
     * Checks if there is a uniprot-cc-exportt annotation defined at the experiment level. if set to yes, export. if set
     * to no, do not export. if no set or the keyword is not yes, no, relies on the standart method.
     *
     * @param experiment the experiment for which we check if we have to export it.
     * @param logPrefix  for all logging messages
     *
     * @return an Integer that has 4 possible value based on constant value: EXPORT, DO_NOT_EXPORT, NOT_SPECIFIED,
     *         LARGE_SCALE. and a list of keywords that is set in case of large scale experiment.
     */
    public final ExperimentStatus getCCLineExperimentExportStatus( final Experiment experiment, String logPrefix ) {

        ExperimentStatus status = null;

        // cache the cvInteraction
        ExperimentStatus cache = (ExperimentStatus) experimentExportStatusCache.get( experiment.getAc() );
        if ( null != cache ) {

            status = cache;
            return status;

        } else {
            Collection annotations = experiment.getAnnotations();
            boolean yesFound = false;
            boolean noFound = false;


            for ( Iterator iterator = annotations.iterator(); iterator.hasNext(); ) {
                Annotation _annotation = (Annotation) iterator.next();
                if ( uniprotCC_Export.equals( _annotation.getCvTopic() ) ) {

                    log( logPrefix + _annotation );

                    String text = _annotation.getAnnotationText();
                    if ( text != null ) {
                        text = text.trim().toLowerCase();
                    }

                    if ( EXPERIMENT_EXPORT_KEYWORK_EXPORT.equals( text ) ) {
                        yesFound = true;
                        log( logPrefix + "\t\t\t\t '" + EXPERIMENT_EXPORT_KEYWORK_EXPORT + "' found" );

                    } else {
                        if ( EXPERIMENT_EXPORT_KEYWORK_DO_NOT_EXPORT.equals( text ) ) {
                            noFound = true;
                            log( logPrefix + "\t\t\t\t '" + EXPERIMENT_EXPORT_KEYWORK_DO_NOT_EXPORT + "' found" );

                        } else {

                            log( logPrefix + "\t\t\t\t '" + text + "' found, that keyword wasn't recognised." );
                        }
                    }
                }
            } // annotations

            if ( yesFound ) {
                status = new ExperimentStatus( ExperimentStatus.EXPORT );
            } else if ( noFound ) {
                status = new ExperimentStatus( ExperimentStatus.DO_NOT_EXPORT );
            }
        }

        if ( status != null ) {

            // cache it.
            experimentExportStatusCache.put( experiment.getAc(), status );

            return status;
        }

        return getExperimentExportStatus( experiment, logPrefix );
    }


    /**
     * Answers the question: does that experiment is to be exported to SwissProt ? <br> Do give that answer, we check
     * that the Experiment has an annotation having <br> <b>CvTopic</b>: uniprot-dr-export <br> <b>text</b>: yes [OR] no
     * [OR] &lt;keyword list&gt;
     * <p/>
     * if <b>yes</b>: the experiment is meant to be exported <br> if <b>no</b>: the experiment is NOT meant to be
     * exported <br> if <b>&lt;keyword list&gt;</b>: the experiment is meant to be exported but only interactions that
     * have an annotation with, as text, one of the keyword specified in the list. This is considered as a Large Scale
     * experiment. </p>
     *
     * @param experiment the experiment for which we check if we have to export it.
     * @param logPrefix  for all logging messages
     *
     * @return an Integer that has 4 possible value based on constant value: EXPORT, DO_NOT_EXPORT, NOT_SPECIFIED,
     *         LARGE_SCALE. and a list of keywords that is set in case of large scale experiment.
     */
    public final ExperimentStatus getExperimentExportStatus( final Experiment experiment, String logPrefix ) {

        ExperimentStatus status = null;

        // cache the cvInteraction
        ExperimentStatus cache = (ExperimentStatus) experimentExportStatusCache.get( experiment.getAc() );
        if ( null != cache ) {

            status = cache;

        } else {

            boolean yesFound = false;
            boolean noFound = false;
            boolean keywordFound = false;

            // most experiment won't need that, so we jsut allocate the collection when needed
            Collection keywords = null;

            Collection annotations = experiment.getAnnotations();
            log( logPrefix + annotations.size() + " annotation(s) found" );

            for ( Iterator iterator = annotations.iterator(); iterator.hasNext(); ) {
                Annotation _annotation = (Annotation) iterator.next();
                if ( uniprotDR_Export.equals( _annotation.getCvTopic() ) ) {

                    log( logPrefix + _annotation );

                    String text = _annotation.getAnnotationText();
                    if ( text != null ) {
                        text = text.trim().toLowerCase();
                    }

                    if ( EXPERIMENT_EXPORT_KEYWORK_EXPORT.equals( text ) ) {
                        yesFound = true;
                        log( logPrefix + "'" + EXPERIMENT_EXPORT_KEYWORK_EXPORT + "' found" );

                    } else {
                        if ( EXPERIMENT_EXPORT_KEYWORK_DO_NOT_EXPORT.equals( text ) ) {
                            noFound = true;
                            log( logPrefix + "'" + EXPERIMENT_EXPORT_KEYWORK_DO_NOT_EXPORT + "' found" );

                        } else {
                            if ( keywords == null ) {
                                keywords = new ArrayList( 2 );
                            }
                            keywordFound = true;

                            log( logPrefix + "'" + text + "' keyword found" );
                            keywords.add( text );
                        }
                    }
                }
            }


            if ( yesFound && !keywordFound ) { // if at least one keyword found, set to large scale experiment.
                status = new ExperimentStatus( ExperimentStatus.EXPORT );
            } else if ( noFound ) {
                status = new ExperimentStatus( ExperimentStatus.DO_NOT_EXPORT );
            } else if ( keywordFound ) {
                status = new ExperimentStatus( ExperimentStatus.LARGE_SCALE );
                status.addKeywords( keywords );
            } else {
                status = new ExperimentStatus( ExperimentStatus.NOT_SPECIFIED );
            }

            // cache it.
            experimentExportStatusCache.put( experiment.getAc(), status );
        }

        log( logPrefix + "Experiment status: " + status );
        return status;
    }

    /**
     * Answers the question: is that AnnotatedObject (Interaction, Experiment) annotated as negative ?
     *
     * @param annotatedObject the object we want to introspect
     *
     * @return true if the object is annotated with the 'negative' CvTopic, otherwise false.
     */
    public boolean isNegative( AnnotatedObject annotatedObject ) {

        boolean isNegative = false;

        Collection annotations = annotatedObject.getAnnotations();
        for ( Iterator iterator = annotations.iterator(); iterator.hasNext() && false == isNegative; ) {
            Annotation annotation = (Annotation) iterator.next();

            if ( negativeTopic.equals( annotation.getCvTopic() ) ) {
                isNegative = true;
            }
        }

        return isNegative;
    }

    /**
     * @param interaction
     *
     * @return
     */
    protected Collection getCCnote( Interaction interaction ) {
        Collection notes = null;

        for ( Iterator iterator = interaction.getAnnotations().iterator(); iterator.hasNext(); ) {
            Annotation annotation = (Annotation) iterator.next();

            if ( ccNoteTopic.equals( annotation.getCvTopic() ) ) {
                if ( notes == null ) {
                    notes = new ArrayList( 2 ); // should rarely have more than 2
                }

                notes.add( annotation.getAnnotationText() );
            }
        }

        return notes;
    }


    /**
     * Assess if a protein is a aplice variant on the basis of its shortlabel as we use the following format SPID-# and
     * if it has a isoform-parent cross reference. <br> Thought it doesn't mean we will find a master protein for it.
     *
     * @param protein the protein we are interrested in knowing if it is a splice variant.
     *
     * @return true if the name complies to the splice variant format.
     */
    protected boolean isSpliceVariant( Protein protein ) {

        // TODO check here is it has a master or not.

        if ( protein.getShortLabel().indexOf( "-" ) != -1 ) {
            // eg. P12345-2

            if ( getMasterAc( protein ) != null ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Retreive the gene name of a protein, if this is a splice variant, get it from its master protein.
     *
     * @param protein the protein from which we want to get a gene name.
     *
     * @return a gene name or null if non could be found.
     */
    public String getGeneName( Protein protein, IntactHelper helper ) {

        String geneName = null;

        // Take into account that a Protein object can be either a protein or a splice variant,
        // in the case of a splice variant, we should pick the gene name from the master protein.
        Protein queryProtein = null;

        if ( isSpliceVariant( protein ) ) {

            // get the master protein.
            queryProtein = getMasterProtein( protein, helper );
            if ( queryProtein == null ) {

                queryProtein = protein;
            }

        } else {

            queryProtein = protein;
        }

        // look first for gene-name
        List geneNames = selectAliasByCvTopic( queryProtein.getAliases(), geneNameAliasType );

        if ( geneNames.isEmpty() ) {

            // then look for locus
            geneNames = selectAliasByCvTopic( queryProtein.getAliases(), locusNameAliasType );

            if ( geneNames.isEmpty() ) {

                // then look for orf
                geneNames = selectAliasByCvTopic( queryProtein.getAliases(), orfNameAliasType );

                if ( geneNames.isEmpty() ) {

                    // no gene-name, locus or orf for that protein, will display a dash ( '-' ) instead.

                } else {
                    geneName = ( (Alias) geneNames.get( 0 ) ).getName();
                }

            } else {
                geneName = ( (Alias) geneNames.get( 0 ) ).getName();
            }

        } else {
            geneName = ( (Alias) geneNames.get( 0 ) ).getName();
        }

//        // search for a gene name in the aliases of that protein - stop when we find one.
//        for( Iterator iterator = queryProtein.getAliases().iterator(); iterator.hasNext() && null == geneName; ) {
//            Alias alias = (Alias) iterator.next();
//
//            if( geneNameAliasType.equals( alias.getCvAliasType() ) ) {
//                geneName = alias.getName();
//            }
//        }

        return geneName;
    }

    public List selectAliasByCvTopic( Collection aliases, CvAliasType aliasType ) {

        List result = null;

        for ( Iterator iterator = aliases.iterator(); iterator.hasNext(); ) {
            Alias alias = (Alias) iterator.next();

            if ( aliasType.equals( alias.getCvAliasType() ) ) {

                if ( result == null ) {
                    result = new ArrayList( 4 );
                }

                result.add( alias );
            }
        }

        if ( result == null ) {

            result = Collections.EMPTY_LIST;

        } else {

            Comparator c = new Comparator() {
                public int compare( Object o1, Object o2 ) {

                    Alias alias1 = (Alias) o1;
                    Alias alias2 = (Alias) o2;

                    return alias1.getName().compareTo( alias2.getName() );
                }
            };

            if ( result.size() > 1 ) {
                Collections.sort( result, c );
            }
        }

        return result;
    }

    /**
     * Checks if the protein has been annotated with the no-uniprot-update CvTopic, if so, return false, otherwise true.
     * That flag is added to a protein when created via the editor. As some protein may have a UniProt ID as identity we
     * don't want those to be overwitten.
     *
     * @param protein the protein to check
     *
     * @return false if no Annotation having CvTopic( no-uniprot-update ), otherwise true.
     */
    protected boolean needsUniprotUpdate( final Protein protein ) {

        boolean needsUpdate = true;

        if ( null == noUniprotUpdate ) {
            // in case the term hasn't been created, assume there are no proteins created via editor.
            return true;
        }

        for ( Iterator iterator = protein.getAnnotations().iterator(); iterator.hasNext() && true == needsUpdate; ) {
            Annotation annotation = (Annotation) iterator.next();

            if ( noUniprotUpdate.equals( annotation.getCvTopic() ) ) {
                needsUpdate = false;
            }
        }

        return needsUpdate;
    }
}
