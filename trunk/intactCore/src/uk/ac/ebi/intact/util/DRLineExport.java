/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util;

import org.apache.commons.cli.*;
import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
    public static final String AUTHOR_CONFIDENCE = "author-confidence";
    public static final String NEGATIVE = "negative";
    public static final String UNIPROT = "uniprot";
    public static final String IDENTITY = "identity";

    private static final String NEW_LINE = System.getProperty( "line.separator" );

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
    protected CvTopic negativeTopic = null;

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

    protected boolean debugEnabled = false; // protected to allow the testcase to modify it.


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

        uniprotDatabase = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, UNIPROT );
        if( uniprotDatabase == null ) {
            throw new DatabaseContentException( "Unable to find the '" + UNIPROT + "' database in your IntAct node" );
        }

        identityXrefQualifier = (CvXrefQualifier) helper.getObjectByLabel( CvXrefQualifier.class, IDENTITY );
        if( identityXrefQualifier == null ) {
            throw new DatabaseContentException( "Unable to find the '" + IDENTITY + "' CvXrefQualifier in your IntAct node" );
        }

        uniprotDR_Export = (CvTopic) helper.getObjectByLabel( CvTopic.class, UNIPROT_DR_EXPORT );
        if( uniprotDR_Export == null ) {
            throw new DatabaseContentException( "Unable to find the '" + UNIPROT_DR_EXPORT + "' CvTopic in your IntAct node" );
        }

        authorConfidenceTopic = (CvTopic) helper.getObjectByLabel( CvTopic.class, AUTHOR_CONFIDENCE );
        if( authorConfidenceTopic == null ) {
            throw new DatabaseContentException( "Unable to find the '" + AUTHOR_CONFIDENCE + "' CvTopic in your IntAct node" );
        }

        negativeTopic = (CvTopic) helper.getObjectByLabel( CvTopic.class, NEGATIVE );
        if( negativeTopic == null ) {
            throw new DatabaseContentException( "Unable to find the '" + NEGATIVE + "' CvTopic in your IntAct node" );
        }
    }


    public void setDebugEnabled( boolean flag ) {
        debugEnabled = flag;
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
                if( debugEnabled ) {
                    System.out.println( "\t\t\t\t CvInteraction: Status already processed, retreived from cache." );
                }
                status = cache;

            } else {

                boolean found = false;
                boolean multipleAnnotationFound = false;
                Collection annotations = null;

                annotations = cvInteraction.getAnnotations();
                if( debugEnabled ) {
                    System.out.println( "\t\t\t\t\t " + annotations.size() + " annotations found." );
                }
                Annotation annotation = null;
                for ( Iterator iterator = annotations.iterator(); iterator.hasNext() && !multipleAnnotationFound; ) {
                    Annotation _annotation = (Annotation) iterator.next();
                    if( uniprotDR_Export.equals( _annotation.getCvTopic() ) ) {

                        if( debugEnabled ) {
                            System.out.println( "\t\t\t\t\t Found uniprot-dr-export annotation: " + _annotation );
                        }

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
                    if( debugEnabled ) {
                        System.out.println( "\t\t\t multiple annotation found: do not export " );
                    }

                } else {

                    if( true == found ) {

                        String text = annotation.getAnnotationText();
                        if( null != text ) {
                            text = text.toLowerCase().trim();
                        }

                        if( "yes".equals( annotation.getAnnotationText() ) ) {

                            status = new CvInteractionStatus( CvInteractionStatus.EXPORT );
                            if( debugEnabled ) {
                                System.out.println( "\t\t\t YES found: export " );
                            }

                        } else if( "no".equals( annotation.getAnnotationText() ) ) {

                            status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                            if( debugEnabled ) {
                                System.out.println( "\t\t\t NO found: do not export " );
                            }

                        } else {

                            if( debugEnabled ) {
                                System.out.println( "\t\t\t neither YES or NO found: should be an integer value... " );
                            }

                            // it must be an integer value, let's check it.
                            try {
                                Integer value = new Integer( text );
                                int i = value.intValue();

                                if( i >= 2 ) {

                                    // value is >= 2
                                    status = new CvInteractionStatus( CvInteractionStatus.CONDITIONAL_EXPORT, i );
                                    if( debugEnabled ) {
                                        System.out.println( "\t\t\t " + i + " found: conditional export " );
                                    }

                                } else if( i == 1 ) {

                                    String err = cvInteraction.getShortLabel() + " having annotation (" + UNIPROT_DR_EXPORT +
                                                 ") has an annotationText like <integer value>. Value was: " + i +
                                                 ", We consider it as to be exported.";
                                    if( debugEnabled ) {
                                        System.out.println( err );
                                    }

                                    status = new CvInteractionStatus( CvInteractionStatus.EXPORT );
                                    if( debugEnabled ) {
                                        System.out.println( "\t\t\t integer == " + i + " found: export " );
                                    }

                                } else {
                                    // i < 1

                                    String err = cvInteraction.getShortLabel() + " having annotation (" + UNIPROT_DR_EXPORT +
                                                 ") has an annotationText like <integer value>. Value was: " + i +
                                                 " However, having a value < 1 is not valid, We consider it as to be NOT exported.";
                                    System.err.println( err );

                                    status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                                    if( debugEnabled ) {
                                        System.out.println( "\t\t\t integer < 1 (" + i + ") found: do not export " );
                                    }
                                }

                            } catch ( NumberFormatException e ) {
                                // not an integer !
                                System.err.println( cvInteraction.getShortLabel() + " having annotation (" + UNIPROT_DR_EXPORT +
                                                    ") has an annotationText different from yes/no/<integer value> !!!" +
                                                    " value was: '" + text + "'." );
                                if( debugEnabled ) {
                                    System.out.println( "\t\t\t not an integer:(" + text + ") found: do not export " );
                                }

                                status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                            }
                        }
                    } else {
                        // no annotation implies NO EXPORT !
                        System.err.println( cvInteraction.getShortLabel() +
                                            " doesn't have an annotation: " + UNIPROT_DR_EXPORT );
                        if( debugEnabled ) {
                            System.out.println( "\t\t\t not annotation found: do not export " );
                        }

                        status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                    }
                }

                // cache it !
                cvInteractionExportStatusCache.put( cvInteraction.getAc(), status );
            }
        }

        if( debugEnabled ) {
            System.out.println( "\t\t CvInteractionExport status: " + status );
        }

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
            if( debugEnabled ) {
                System.out.println( "\t\t\t\t Experiment: Status already processed, retreived from cache." );
            }
            status = cache;

        } else {

            boolean yesFound = false;
            boolean noFound = false;
            boolean keywordFound = false;

            // most experiment won't need that, so we jsut allocate the collection when needed
            Collection keywords = null;

            Collection annotations = experiment.getAnnotations();
            if( debugEnabled ) {
                System.out.println( "\t\t\t\t " + annotations.size() + " annotation(s) found" );
            }

            for ( Iterator iterator = annotations.iterator(); iterator.hasNext(); ) {
                Annotation _annotation = (Annotation) iterator.next();
                if( uniprotDR_Export.equals( _annotation.getCvTopic() ) ) {

                    if( debugEnabled ) {
                        System.out.println( "\t\t\t\t " + _annotation );
                    }

                    String text = _annotation.getAnnotationText();
                    if( text != null ) {
                        text = text.trim().toLowerCase();
                    }

                    if( "yes".equals( text ) ) {
                        yesFound = true;
                        if( debugEnabled ) {
                            System.out.println( "\t\t\t\t 'yes' found" );
                        }

                    } else {
                        if( "no".equals( text ) ) {
                            noFound = true;
                            if( debugEnabled ) {
                                System.out.println( "\t\t\t\t 'no' found" );
                            }

                        } else {
                            if( keywords == null ) {
                                keywords = new ArrayList( 2 );
                            }
                            keywordFound = true;
                            if( debugEnabled ) {
                                System.out.println( "\t\t\t\t '" + text + "' keyword found" );
                            }
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

        if( debugEnabled ) {
            System.out.println( "\t\t\t\t Experiment status: " + status );
        }
        return status;
    }


    /**
     * Implement the set of rules that check if Proteins are eligible for export.
     *
     * @param proteins The set of proteins to be checked for eligibility to export in Swiss-Prot.
     * @return a Set of Uniprot Protein ID that are eligible for export in Swiss-Prot.
     */
    public final Set getProteinEligibleForExport( Collection proteins ) {

        return getProteinEligibleForExport( proteins, null );
    }


    /**
     * Implement the set of rules that check if Proteins are eligible for export.
     *
     * @param proteins          The set of proteins to be checked for eligibility to export in Swiss-Prot.
     * @param selectedUniprotID the set of eligible protein ID to update.
     * @return a Set of Uniprot Protein ID that are eligible for export in Swiss-Prot.
     */
    public final Set getProteinEligibleForExport( Collection proteins, Set selectedUniprotID ) {

        if( selectedUniprotID == null ) {
            selectedUniprotID = new HashSet();
        }

        //map: method  ===>  count of distinct experiment in which the protein has been seen
        HashMap conditionalMethods = new HashMap();

        for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
            Protein protein = (Protein) iterator.next();
            String uniprotID = getUniprotID( protein );

            if( debugEnabled ) {
                System.out.println( "\n\n" + uniprotID + " Shortlabel:" + protein.getShortLabel() + "  AC: " + protein.getAc() );
            }

            /**
             * In order to export a protein, it has to:
             *   - get its interactions
             *      - get its experiment
             *          - if the experiment has a local annotation allowing the export
             *          - if nothing specified: check if the CvInteraction has a local annotation allowing the export
             *          - if yes, the protein become eligible to export.
             */
            Collection interactions = getInteractions( protein );
            if( debugEnabled ) {
                System.out.println( "\t related to " + interactions.size() + " interactions." );
            }
            boolean export = false;
            boolean stop = false;
            conditionalMethods.clear();

            for ( Iterator iterator1 = interactions.iterator(); iterator1.hasNext() && !stop; ) {
                Interaction interaction = (Interaction) iterator1.next();

                if( debugEnabled ) {
                    System.out.println( "\t Interaction: Shortlabel:" + interaction.getShortLabel() +
                                        "  AC: " + interaction.getAc() );
                }

                // if that interaction is flagged as negative, we don't take it into account
                if( isNegative( interaction ) ) {
                    if( debugEnabled ) {
                        System.out.println( "\t\t Interaction is flagged as negative, we don't take it into account." );
                    }

                    continue; // loop to the next interaction
                } else {
                    if( debugEnabled ) {
                        System.out.println( "\t\t Interaction is NOT flagged as negative." );
                    }
                }

                Collection experiments = interaction.getExperiments();
                if( debugEnabled ) {
                    int count = experiments.size();
                    System.out.println( "\t\t interaction related to " + count + " experiment" + ( count > 1 ? "s" : "" ) + "." );
                }

                for ( Iterator iterator2 = experiments.iterator(); iterator2.hasNext() && !stop; ) {
                    Experiment experiment = (Experiment) iterator2.next();

                    if( debugEnabled ) {
                        System.out.println( "\t\t\t Experiment: Shortlabel:" + experiment.getShortLabel() +
                                            "  AC: " + experiment.getAc() );
                    }

                    // if that experiment is flagged as negative, we don't take it into account
                    if( isNegative( experiment ) ) {
                        if( debugEnabled ) {
                            System.out.println( "\t\t\t\t Experiment is flagged as negative, we don't take it into account." );
                        }

                        continue; // loop to the next experiment
                    } else {
                        if( debugEnabled ) {
                            System.out.println( "\t\t\t\t Experiment is NOT flagged as negative." );
                        }
                    }

                    ExperimentStatus experimentStatus = getExperimentExportStatus( experiment );
                    if( experimentStatus.doNotExport() ) {

                        // forbid export for all interaction of that experiment (and their proteins).
                        if( debugEnabled ) {
                            System.out.println( "\t\t\t\t No interaction of that experiment will be exported." );
                        }
                        export = false;
                        stop = true;

                        continue;  // go to next experiment

                    } else if( experimentStatus.doExport() ) {

                        // Authorise export for all interactions of that experiment (and their proteins),
                        // This overwrite the setting of the CvInteraction concerning the export.
                        if( debugEnabled ) {
                            System.out.println( "\t\t\t\t All interaction of that experiment will be exported." );
                        }
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

                                if( debugEnabled ) {
                                    System.out.println( "\t\t\t\t Interaction has uniprot-dr-export: '" + text + "'" );
                                }

                                if( text != null ) {
                                    text = text.trim();
                                }

                                for ( Iterator iterator4 = keywords.iterator(); iterator4.hasNext() && !found; ) {
                                    String kw = (String) iterator4.next();
                                    // NOT case sensitive

                                    if( debugEnabled ) {
                                        System.out.println( "\t\t\t\t\t Compare it with '" + kw + "'" );
                                    }

                                    if( kw.equalsIgnoreCase( text ) ) {
                                        found = true;
                                        if( debugEnabled ) {
                                            System.out.println( "\t\t\t\t\t Equals !" );
                                        }
                                    }
                                }
                            }
                        }

                        if( found ) {

                            /*
                             * We don't need to check an eventual threshold on the method level because
                             * in the current state, the annotation is on the experiment level that is
                             * lower and hence is dominant on the method's one.
                             */

                            export = true;
                            stop = true;

                            if( debugEnabled ) {
                                System.out.println( "\t\t\t that interaction is eligible for export in the context of a large scale experiment" );
                            }

                        } else {
                            if( debugEnabled ) {
                                System.out.println( "\t\t\t interaction not eligible" );
                            }
                        }

                    } else if( experimentStatus.isNotSpecified() ) {

                        if( debugEnabled ) {
                            System.out.println( "\t\t\t No experiment status, check the experimental method." );
                        }

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

                            if( debugEnabled ) {
                                System.out.println( "\t\t\t As conditional export, check the count of distinct experiment for that method." );
                            }

                            // non redundant set of experiment AC.
                            HashSet experimentAcs = (HashSet) conditionalMethods.get( cvInteraction );
                            int threshold = methodStatus.getMinimumOccurence();

                            if( null == experimentAcs ) {
                                // at most we will need to store $threshold Experiments.
                                experimentAcs = new HashSet( threshold );

                                // stores it back in the collection ... needs to be done only once ! We are using reference ;o)
                                conditionalMethods.put( cvInteraction, experimentAcs );
                                if( debugEnabled ) {
                                    System.out.println( "\t\t\t Created a container for experiment ID for that method" );
                                }
                            }

                            // add the experiment ID
                            experimentAcs.add( experiment ); // we could store here the hasCode instead !!!

                            if( experimentAcs.size() == threshold ) {
                                // We reached the threshold, export allowed !
                                if( debugEnabled ) {
                                    System.out.println( "\t\t\t Count of distinct experiment reached for that method" );
                                }
                                export = true;
                                stop = true;
                            }

                            if( debugEnabled ) {
                                System.out.println( "\t\t\t " + cvInteraction.getShortLabel() + ", threshold: " +
                                                    threshold + " #experiment: " +
                                                    ( experimentAcs == null ? "none" : "" + experimentAcs.size() ) );
                            }
                        }
                    } // experiment status not specified
                } // experiments
            } // interactions

            if( export ) {
                // That protein is eligible for export.
                // The ID will be still unique even if it already exists.
                if( debugEnabled ) {
                    System.out.println( "Protein exported to Swiss-Prot" );
                }
                selectedUniprotID.add( uniprotID );
            } else {
                if( debugEnabled ) {
                    System.out.println( "Protein NOT exported to Swiss-Prot" );
                }
            }
        } // proteins

        return selectedUniprotID;
    }


    /**
     * Answers the question: is that AnnotatedObject (Interaction, Experiment) annotated as negative ?
     *
     * @param annotatedObject the object we want to introspect
     * @return true if the object is annotated with the 'negative' CvTopic, otherwise false.
     */
    public boolean isNegative( AnnotatedObject annotatedObject ) {

        boolean isNegative = false;

        Collection annotations = annotatedObject.getAnnotations();
        for ( Iterator iterator = annotations.iterator(); iterator.hasNext() && false == isNegative; ) {
            Annotation annotation = (Annotation) iterator.next();

            if( negativeTopic.equals( annotation.getCvTopic() ) ) {
                isNegative = true;
            }
        }

        return isNegative;
    }


    public static String formatProtein( String uniprotID ) {
        StringBuffer sb = new StringBuffer();

        sb.append( uniprotID ).append( '\t' );
        sb.append( "Intact" ).append( '\t' );
        sb.append( uniprotID ).append( '\t' );
        sb.append( '-' );

        return sb.toString();
    }


    public static void display( Set proteins ) {
        for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
            String uniprotID = (String) iterator.next();
            System.out.println( formatProtein( uniprotID ) );
        }
    }


    private static void writeToFile( Set proteins, Writer out ) throws IOException {
        for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
            String uniprotID = (String) iterator.next();
            out.write( formatProtein( uniprotID ) );
            out.write( NEW_LINE );
        }
    }


    private static void displayUsage( Options options ) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "DRLineExport [-debug]", options );
    }


    public static void main( String[] args ) throws IntactException, SQLException, LookupException,
                                                    DatabaseContentException {

        // create Option objects
        Option helpOpt = new Option( "help", "print this message" );

        Option debugOpt = OptionBuilder.withDescription( "Shows debugging messages." ).create( "debug" );
        debugOpt.setRequired( false );

        Options options = new Options();

        options.addOption( helpOpt );
        options.addOption( debugOpt );

        // create the parser
        CommandLineParser parser = new BasicParser();
        CommandLine line = null;
        try {
            // parse the command line arguments
            line = parser.parse( options, args, true );
        } catch ( ParseException exp ) {
            // Oops, something went wrong

            displayUsage( options );

            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            System.exit( 1 );
        }

        if( line.hasOption( "help" ) ) {
            displayUsage( options );
            System.exit( 0 );
        }

        DRLineExport exporter = new DRLineExport();

        boolean debugEnabled = line.hasOption( "debug" );
        exporter.setDebugEnabled( debugEnabled );


        IntactHelper helper = new IntactHelper();
        System.out.println( "Database instance: " + helper.getDbName() );
        System.out.println( "User: " + helper.getDbUserName() );

        exporter.init( helper );

        /**
         *    1         2  	      3  	       4  	        5  	      6
         *[A-N,R-Z]   [0-9]     [A-Z]      [A-Z, 0-9]   [A-Z, 0-9]  [0-9]
         * [O,P,Q] 	  [0-9]   [A-Z, 0-9]   [A-Z, 0-9]   [A-Z, 0-9]  [0-9]
         *
         * which is equivalent to:
         *
         *[A-Z]   [0-9]     [A-Z]      [A-Z, 0-9]   [A-Z, 0-9]  [0-9]
         *                [A-Z, 0-9]
         *
         * so, for a start, we will perform the following kind of queries:
         *
         *
         */

        CvDatabase uniprot = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, "uniprot" );
        if( uniprot == null ) {
            System.err.println( "Could not find uniprot in the current intact node, abort." );
            System.exit( 0 );
        }

        Set proteinEligible = new HashSet( 1024 );
        Chrono globalChrono = new Chrono();
        globalChrono.start();
        Collection proteins = null;
        int proteinCount = 0;

        for ( char i = 'A'; i <= 'Z'; i++ ) {  // O, P, Q
            for ( char j = '0'; j <= '0'; j++ ) {  // 0,1,2,3,4,5,6,7,8,9
                for ( char k = '0'; k <= '9'; k++ ) {  // 0,1,2,3,4,5,6,7,8,9

                    String searchString = i + "" + j + "*" + k;

                    System.out.println( "\nexport protein matching: " + searchString );

                    // need to select only uniprot Xref !!!!

                    proteins = helper.getObjectsByXref( Protein.class, uniprot, searchString );
                    int count = proteins.size();


                    System.out.println( count + " protein" + ( count > 1 ? "s" : "" ) + " selected." );
                    if( count > 0 ) {
                        proteinCount += count;
                        Chrono chrono = new Chrono();
                        chrono.start();

                        proteinEligible = exporter.getProteinEligibleForExport( proteins, proteinEligible );
                        chrono.stop();

                        proteins.clear();

                        count = proteinEligible.size();
                        float percentage = ( (float) count / (float) proteinCount ) * 100;
                        System.out.println( count + " protein" + ( count > 1 ? "s" : "" ) +
                                            " eligible for export out of " + proteinCount +
                                            " processed (" + percentage + "%)." );
                        System.out.println( "Time elapsed to process them: " + chrono + " total: " + globalChrono );
                    } else {
                        System.out.println( "No protein found." );
                    }
                } // 0..9 - last char of the ID
            } // 0..9 - second char of the ID
        } // O,P,Q - first letter

        globalChrono.stop();
        System.out.println( "Total time elapsed: " + globalChrono );

        helper.closeStore();

        // save it file.
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd@HH.mm" );
        String time = formatter.format( new Date() );
        formatter = null;

        String filename = "export2uniprot_" + time + ".txt";
        File file = new File( filename );
        System.out.println( "Try to save to: " + file.getAbsolutePath() );
        BufferedWriter out = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter( file );
            out = new BufferedWriter( fw );

            writeToFile( proteinEligible, out );

        } catch ( IOException e ) {
            e.printStackTrace();
            System.out.println( "" );

            System.err.println( "Could not save the result to :" + filename );
            System.err.println( "Displays the result on STDOUT:\n\n\n" );

            display( proteinEligible );
            System.exit( 1 );

        } finally {
            if( out != null ) {
                try {
                    out.close();
                } catch ( IOException e ) {
                    System.exit( 1 );
                }
            }

            if( fw != null ) {
                try {
                    fw.close();
                } catch ( IOException e ) {
                    System.exit( 1 );
                }
            }
        }

        System.exit( 0 );
    }
}