/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util;

import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.sql.SQLException;
import java.util.*;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class DRLineExport {

    //////////////////////////
    // Constants

    public static final String UNIPROT_DR_EXPORT = "uniprot-dr-export";


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
            if( keywords == null ) {
                throw new IllegalArgumentException( "Keywords must not be null" );
            }
            this.keywords = keywords;
        }

        public String toString() {

            StringBuffer sb = new StringBuffer( 128 );

            sb.append( "ExperimentStatus{ keywords= " );
            if( keywords != null ) {
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
                default:
                    sb.append( "UNKNOWN VALUE !!!!!!!!!!!!!!!!!" );
            }
            sb.append( " }" );

            return sb.toString();
        }
    }


    public class DatabaseContentException extends Exception {

        public DatabaseContentException( String message ) {
            super( message );
        }
    }


    //////////////////////////////
    // Attributes

    // Vocabulary that we need for processing
    protected CvXrefQualifier identityXrefQualifier = null;
    protected CvDatabase uniprotDatabase = null;
    protected CvTopic uniprotDR_Export = null;
    protected CvTopic authorConfidenceTopic = null;

    /**
     * Cache the CvInteraction property for the export.
     * CvInteraction.ac -> Boolean.TRUE or Boolean.FALSE
     */
    private HashMap cvInteractionExportStatusCache = new HashMap();

    /**
     * Cache the Experiment property for the export.
     * Experiment.ac -> Integer (EXPORT, DO_NOT_EXPORT, NOT_SPECIFIED)
     */
    private HashMap experimentExportStatusCache = new HashMap();


    /////////////////////////////
    // Methods

    /**
     * Load the minimal Controlled vocabulary needed during the processing.
     * It at least one term is missing, an Exception is thrown.
     *
     * @param helper data access
     * @throws IntactException          search error
     * @throws DatabaseContentException if at least one term is missing
     */
    public void init( IntactHelper helper ) throws IntactException, DatabaseContentException {

        uniprotDatabase = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, "uniprot" );
        if( uniprotDatabase == null ) {
            throw new DatabaseContentException( "Unable to find the 'uniprot' database in your IntAct node" );
        }

        identityXrefQualifier = (CvXrefQualifier) helper.getObjectByLabel( CvXrefQualifier.class, "identity" );
        if( identityXrefQualifier == null ) {
            throw new DatabaseContentException( "Unable to find the 'identity' CvXrefQualifier in your IntAct node" );
        }

        uniprotDR_Export = (CvTopic) helper.getObjectByLabel( CvTopic.class, UNIPROT_DR_EXPORT );
        if( uniprotDR_Export == null ) {
            throw new DatabaseContentException( "Unable to find the '" + UNIPROT_DR_EXPORT + "' CvTopic in your IntAct node" );
        }

        authorConfidenceTopic = (CvTopic) helper.getObjectByLabel( CvTopic.class, "author-confidence" );
        if( authorConfidenceTopic == null ) {
            throw new DatabaseContentException( "Unable to find the 'author-confidence' CvTopic in your IntAct node" );
        }
    }


    /**
     * Get the uniprot primary ID from Protein and Splice variant.
     *
     * @param protein the Protein for which we want the uniprot ID.
     * @return the uniprot ID as a String or null if none is found (should not occur)
     */
    public final String getUniprotID( final Protein protein ) {

        String uniprot = null;

        Collection xrefs = protein.getXrefs();
        boolean found = false;
        for ( Iterator iterator = xrefs.iterator(); iterator.hasNext() && !found; ) {
            Xref xref = (Xref) iterator.next();

            if( uniprotDatabase.equals( xref.getCvDatabase() ) &&
                identityXrefQualifier.equals( xref.getCvXrefQualifier() ) ) {
                uniprot = xref.getPrimaryId();
                found = true;
            }
        }

        return uniprot;
    }


    /**
     * Get all interaction related to the given Protein.
     *
     * @param protein the protein of which we want the interactions.
     * @return a Collection if Interaction.
     */
    private final Collection getInteractions( final Protein protein ) {
        Collection components = protein.getActiveInstances();
        Collection interactions = new ArrayList( components.size() );

        for ( Iterator iterator = components.iterator(); iterator.hasNext(); ) {
            Component component = (Component) iterator.next();
            interactions.add( component.getInteraction() );
        }

        return interactions;
    }


    /**
     * Answers the question: does that technology is to be exported to SwissProt ?
     * <br>
     * Do give that answer, we check that the CvInteration has an annotation having
     * <br>
     * <b>CvTopic</b>: uniprot-dr-export
     * <br>
     * <b>text</b>: yes [OR] no [OR] &lt;integer value&gt;
     * <p/>
     * if <b>yes</b>: the method is meant to be exported
     * <br>
     * if <b>no</b>: the method is NOT meant to be exported
     * <br>
     * if <b>&lt;integer value&gt;</b>: the protein linked to that method are ment to be exported only if they are seen
     * in a minimum number interactions belonging to distinct experiment.
     * eg. let's set that value to 2 for Y2H. To be eligible for export a protein must
     * have been seen in at least 2 Y2H distinct experiment.
     * </p>
     *
     * @param cvInteraction the method to check
     * @return the status of that method (EXPORT, DO_NOT_EXPORT, NOT_SPECIFIED, CONDITIONAL_EXPORT)
     *         with an optional count.
     */
    public final CvInteractionStatus getMethodExportStatus( final CvInteraction cvInteraction ) {

        CvInteractionStatus status = null;

        // cache the CvInteraction status
        if( null != cvInteraction ) {

            CvInteractionStatus cache = (CvInteractionStatus) cvInteractionExportStatusCache.get( cvInteraction.getAc() );
            if( null != cache ) {
                System.out.println( "\t\t CvInteraction: Status already processed, retreived from cache." );
                status = cache;

            } else {

                boolean found = false;
                boolean multipleAnnotationFound = false;
                Collection annotations = null;

                annotations = cvInteraction.getAnnotations();
                System.out.println( "\t\t\t " + annotations.size() + " annotations found." );
                Annotation annotation = null;
                for ( Iterator iterator = annotations.iterator(); iterator.hasNext() && !multipleAnnotationFound; ) {
                    Annotation _annotation = (Annotation) iterator.next();
                    if( uniprotDR_Export.equals( _annotation.getCvTopic() ) ) {

                        System.out.println( "\t\t\t Found uniprot-dr-export annotation: " + _annotation );

                        if( true == found ) {
                            multipleAnnotationFound = true;
                            System.err.println( "There are multiple annotation having Topic:" + UNIPROT_DR_EXPORT +
                                                " in CvInteraction: " + cvInteraction.getShortLabel() +
                                                ". \nWe do not export." );
                        } else {
                            found = true;
                            annotation = _annotation;
                        }
                    }
                }


                if( multipleAnnotationFound ) {

                    status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                    System.out.println( "\t\t\t multiple annotation found: do not export " );

                } else {

                    if( true == found ) {

                        String text = annotation.getAnnotationText();
                        if( null != text ) {
                            text = text.toLowerCase().trim();
                        }

                        if( "yes".equals( annotation.getAnnotationText() ) ) {

                            status = new CvInteractionStatus( CvInteractionStatus.EXPORT );
                            System.out.println( "\t\t\t YES found: export " );

                        } else if( "no".equals( annotation.getAnnotationText() ) ) {

                            status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                            System.out.println( "\t\t\t NO found: do not export " );

                        } else {

                            System.out.println( "\t\t\t neither YES or NO found: should be an integer value... " );

                            // it must be an integer value, let's check it.
                            try {
                                Integer value = new Integer( text );
                                int i = value.intValue();

                                if( i >= 2 ) {

                                    // value is >= 2
                                    status = new CvInteractionStatus( CvInteractionStatus.CONDITIONAL_EXPORT, i );
                                    System.out.println( "\t\t\t " + i + " found: conditional export " );

                                } else if( i == 1 ) {

                                    String err = cvInteraction.getShortLabel() + " having annotation (" + UNIPROT_DR_EXPORT +
                                                 ") has an annotationText like <integer value>. Value was: " + i +
                                                 ", We consider it as to be exported.";
                                    System.out.println( err );

                                    status = new CvInteractionStatus( CvInteractionStatus.EXPORT );
                                    System.out.println( "\t\t\t integer == " + i + " found: export " );

                                } else {
                                    // i < 1

                                    String err = cvInteraction.getShortLabel() + " having annotation (" + UNIPROT_DR_EXPORT +
                                                 ") has an annotationText like <integer value>. Value was: " + i +
                                                 " However, having a value < 1 is not valid, We consider it as to be NOT exported.";
                                    System.err.println( err );

                                    status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                                    System.out.println( "\t\t\t integer < 1 (" + i + ") found: do not export " );
                                }

                            } catch ( NumberFormatException e ) {
                                // not an integer !
                                System.err.println( cvInteraction.getShortLabel() + " having annotation (" + UNIPROT_DR_EXPORT +
                                                    ") has an annotationText different from yes/no/<integer value> !!!" +
                                                    " value was: '" + text + "'." );
                                System.out.println( "\t\t\t not an integer:(" + text + ") found: do not export " );

                                status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                            }
                        }
                    } else {
                        // no annotation implies NO EXPORT !
                        System.err.println( cvInteraction.getShortLabel() +
                                            " doesn't have an annotation: " + UNIPROT_DR_EXPORT );
                        System.out.println( "\t\t\t not annotation found: do not export " );

                        status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                    }
                }

                // cache it !
                cvInteractionExportStatusCache.put( cvInteraction.getAc(), status );
            }
        }

        System.out.println( "\t\t CvInteractionExport status: " + status );

        return status;
    }


    /**
     * Answers the question: does that experiment is to be exported to SwissProt ?
     * <br>
     * Do give that answer, we check that the Experiment has an annotation having
     * <br>
     * <b>CvTopic</b>: uniprot-dr-export
     * <br>
     * <b>text</b>: yes [OR] no [OR] &lt;keyword list&gt;
     * <p/>
     * if <b>yes</b>: the experiment is meant to be exported
     * <br>
     * if <b>no</b>: the experiment is NOT meant to be exported
     * <br>
     * if <b>&lt;keyword list&gt;</b>: the experiment is meant to be exported but only interactions that have
     * an annotation with, as text, one of the keyword specified in the list.
     * This is considered as a Large Scale experiment.
     * </p>
     *
     * @param experiment the experiment for which we check if we have to export it.
     * @return an Integer that has 4 possible value based on constant value: EXPORT, DO_NOT_EXPORT, NOT_SPECIFIED,
     *         LARGE_SCALE. and a list of keywords that is set in case of large scale experiment.
     */
    public final ExperimentStatus getExperimentExportStatus( final Experiment experiment ) {

        ExperimentStatus status = null;

        // cache the cvInteraction
        ExperimentStatus cache = (ExperimentStatus) experimentExportStatusCache.get( experiment.getAc() );
        if( null != cache ) {
            System.out.println( "\t\t Experiment: Status already processed, retreived from cache." );
            status = cache;

        } else {

            boolean yesFound = false;
            boolean noFound = false;
            boolean keywordFound = false;

            // most experiment won't need that, so we jsut allocate the collection when needed
            Collection keywords = null;

            Collection annotations = experiment.getAnnotations();
            System.out.println( "\t\t " + annotations.size() + " annotation(s) found" );

            for ( Iterator iterator = annotations.iterator(); iterator.hasNext(); ) {
                Annotation _annotation = (Annotation) iterator.next();
                if( uniprotDR_Export.equals( _annotation.getCvTopic() ) ) {

                    System.out.println( "\t\t " + _annotation );

                    String text = _annotation.getAnnotationText();
                    if( text != null ) {
                        text = text.trim().toLowerCase();
                    }

                    if( "yes".equals( text ) ) {
                        yesFound = true;
                        System.out.println( "\t\t 'yes' found" );

                    } else {
                        if( "no".equals( text ) ) {
                            noFound = true;
                            System.out.println( "\t\t 'no' found" );

                        } else {
                            if( keywords == null ) {
                                keywords = new ArrayList( 2 );
                            }
                            keywordFound = true;
                            System.out.println( "\t\t '" + text + "' keyword found" );
                            keywords.add( text );
                        }
                    }
                }
            }


            if( yesFound && !keywordFound ) { // if at least one keyword found, set to large scale experiment.
                status = new ExperimentStatus( ExperimentStatus.EXPORT );
            } else if( noFound ) {
                status = new ExperimentStatus( ExperimentStatus.DO_NOT_EXPORT );
            } else if( keywordFound ) {
                status = new ExperimentStatus( ExperimentStatus.LARGE_SCALE );
                status.addKeywords( keywords );
            } else {
                status = new ExperimentStatus( ExperimentStatus.NOT_SPECIFIED );
            }

            // cache it.
            experimentExportStatusCache.put( experiment.getAc(), status );
        }

        System.out.println( "\t\t Experiment status: " + status );
        return status;
    }


    /**
     * Implement the set of rules that check if Proteins are eligible for export.
     *
     * @param proteins The set of proteins to be checked for eligibility to export in Swiss-Prot.
     * @return a Set of Uniprot Protein ID that are eligible for export in Swiss-Prot.
     */
    public final Set getProteinEligibleForExport( Collection proteins ) {

        Set selectedUniprotID = new HashSet();

        //map: method -> count of experiment in which the protein has been seen
        HashMap conditionalMethods = new HashMap();

        for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
            Protein protein = (Protein) iterator.next();
            String uniprotID = getUniprotID( protein );

            System.out.println( "\n\n" + uniprotID + " Shortlabel:" + protein.getShortLabel() + "  AC: " + protein.getAc() );

            /**
             * In order to export a protein, it has to:
             *   - get its interactions
             *      - get its experiment
             *          - if the experiment has a local annotation allowing the export
             *          - if nothing specified: check if the CvInteraction has a local annotation allowing the export
             *          - if yes, the protein become eligible to export.
             */
            Collection interactions = getInteractions( protein );
            System.out.println( "\t related to " + interactions.size() + " interactions." );
            boolean export = false;
            boolean stop = false;
            conditionalMethods.clear();

            for ( Iterator iterator1 = interactions.iterator(); iterator1.hasNext() && !stop; ) {
                Interaction interaction = (Interaction) iterator1.next();

                System.out.println( "\t Interaction: Shortlabel:" + interaction.getShortLabel() +
                                    "  AC: " + interaction.getAc() );


                Collection experiments = interaction.getExperiments();

                for ( Iterator iterator2 = experiments.iterator(); iterator2.hasNext() && !stop; ) {
                    Experiment experiment = (Experiment) iterator2.next();

                    System.out.println( "\t\t Experiment: Shortlabel:" + experiment.getShortLabel() +
                                        "  AC: " + experiment.getAc() );

                    ExperimentStatus experimentStatus = getExperimentExportStatus( experiment );
                    if( experimentStatus.doNotExport() ) {

                        // forbid export for all interaction of that experiment (and their proteins).
                        System.out.println( "\t\t No interaction of that experiment will be exported." );
                        export = false;
                        stop = true;

                        continue;  // go to next experiment

                    } else if( experimentStatus.doExport() ) {

                        // Authorise export for all interactions of that experiment (and their proteins),
                        // This overwrite the setting of the CvInteraction concerning the export.
                        System.out.println( "\t\t All interaction of that experiment will be exported." );
                        export = true;
                        stop = true;

                        continue; // go to next experiment

                    } else if( experimentStatus.isLargeScale() ) {

                        // if my interaction has one of those keywords as annotation for DR line export, do export.
                        Collection keywords = experimentStatus.getKeywords();
                        Collection annotations = interaction.getAnnotations();
                        boolean found = false;

                        // We assume here that an interaction has only one Annotation( uniprot-dr-export ).
                        for ( Iterator iterator3 = annotations.iterator(); iterator3.hasNext() && !found; ) {
                            final Annotation annotation = (Annotation) iterator3.next();

                            if( authorConfidenceTopic.equals( annotation.getCvTopic() ) ) {
                                String text = annotation.getAnnotationText();

                                System.out.println( "\t\t Interaction has uniprot-dr-export: '" + text + "'" );

                                if( text != null ) {
                                    text = text.trim();
                                }

                                for ( Iterator iterator4 = keywords.iterator(); iterator4.hasNext() && !found; ) {
                                    String kw = (String) iterator4.next();
                                    // NOT case sensitive

                                    System.out.println( "\t\t\t Compare it with '" + kw + "'" );

                                    if( kw.equalsIgnoreCase( text ) ) {
                                        found = true;
                                        System.out.println( "\t\t\t Equals !" );
                                    }
                                }
                            }
                        }

                        if( found ) {
                            // TODO then do export but check first if this is not a method that requires a number of occurences.
                            export = true;
                            stop = true;

                            System.out.println( "\t\t that interaction is eligible for export in the context of a large scale experiment" );

                        } else {
                            System.out.println( "\t\t interaction not eligible" );
                        }

                    } else if( experimentStatus.isNotSpecified() ) {

                        System.out.println( "\t\t No experiment status, check the experimental method." );

                        // Then check the experimental method (CvInteraction)
                        // Nothing specified at the experiment level, check for the method (CvInteraction)
                        CvInteraction cvInteraction = experiment.getCvInteraction();
                        if( null == cvInteraction ) {
                            // we need to check because this is not mandatory.
                            continue; // skip it, go to next experiment
                        }

                        CvInteractionStatus methodStatus = getMethodExportStatus( cvInteraction );

                        if( methodStatus.doExport() ) {

                            export = true;
                            stop = true;

                        } else if( methodStatus.doNotExport() ) {

                            export = false;
                            stop = true;

                        } else if( methodStatus.isNotSpecified() ) {

                            // we should never get in here but just in case...
                            export = false;
                            stop = true;

                        } else if( methodStatus.isConditionalExport() ) {

                            System.out.println( "\t\t As conditional export, check the count of distinct experiment for that method." );

                            // non redundant set of experiment AC.
                            HashSet experimentAcs = (HashSet) conditionalMethods.get( cvInteraction );
                            int threshold = methodStatus.getMinimumOccurence();

                            if( null == experimentAcs ) {
                                // at most we will need to store $threshold Experiments.
                                experimentAcs = new HashSet( threshold );

                                // stores it back in the collection ... needs to be done only once ! We are using reference ;o)
                                conditionalMethods.put( cvInteraction, experimentAcs );
                                System.out.println( "\t\t Created a container for experiment ID for that method" );
                            }

                            // add the experiment ID
                            experimentAcs.add( experiment ); // we could store here the hasCode instead !!!

                            if( experimentAcs.size() == threshold ) {
                                // We reached the threshold, export allowed !
                                System.out.println( "\t\t Count of distinct experiment reached for that method" );
                                export = true;
                                stop = true;
                            }

                            System.out.println( "\t\t " + cvInteraction.getShortLabel() + ", threshold: " +
                                                threshold + " #experiment: " +
                                                ( experimentAcs == null ? "none" : "" + experimentAcs.size() ) );
                        }
                    } // experiment status not specified
                } // experiments
            } // interactions

            if( export ) {
                // That protein is eligible for export.
                // The ID will be still unique even if it already exists.
                System.out.println( "Protein exported to Swiss-Prot" );
                selectedUniprotID.add( uniprotID );
            } else {
                System.out.println( "Protein NOT exported to Swiss-Prot" );
            }
        } // proteins

        return selectedUniprotID;
    }


    public static void display( Set proteins ) {
        for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
            String uniprotID = (String) iterator.next();
            System.out.println( uniprotID + "\t" + "Intact" + "\t" + uniprotID + "\t" + "-" );
        }
    }


    public static void main( String[] args ) throws IntactException, SQLException, LookupException,
                                                    DatabaseContentException {

        DRLineExport exporter = new DRLineExport();

        IntactHelper helper = new IntactHelper();
        System.out.println( "Database instance: " + helper.getDbName() );
        System.out.println( "User: " + helper.getDbUserName() );

        exporter.init( helper );

        // get all proteins
        Collection proteins = helper.search( Protein.class.getName(), "shortlabel", "a*" );
        int count = proteins.size();
        System.out.println( count + " protein" + ( count > 1 ? "s" : "" ) + " selected." );

        Set proteinEligible = exporter.getProteinEligibleForExport( proteins );
        helper.closeStore();

        count = proteinEligible.size();
        System.out.println( count + " protein" + ( count > 1 ? "s" : "" ) + " eligible for export to Swiss-Prot." );
        display( proteinEligible );
    }
}