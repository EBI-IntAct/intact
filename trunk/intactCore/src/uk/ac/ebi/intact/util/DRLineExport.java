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

    private static class ExperimentStatus {

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
    }

    private static class CvInteractionStatus {

        // Method status
        public static final int EXPORT = 0;
        public static final int DO_NOT_EXPORT = 1;
        public static final int NOT_SPECIFIED = 2;
        public static final int CONDITIONAL_EXPORT = 3;

        private int status;
        private int minimumOccurence = 0;

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
    }

    private static class DatabaseContentException extends Exception {

        public DatabaseContentException( String message ) {
            super( message );
        }
    }



    //////////////////////////////
    // Attributes

    // Vocabulary that we need for processing
    static CvXrefQualifier identityXrefQualifier = null;
    static CvDatabase uniprotDatabase = null;
    static CvTopic uniprotDR_Export = null;

    /**
     * Cache the CvInteraction property for the export.
     * CvInteraction.ac -> Boolean.TRUE or Boolean.FALSE
     */
    static HashMap cvInteractionExportStatusCache = new HashMap();

    /**
     * Cache the Experiment property for the export.
     * Experiment.ac -> Integer (EXPORT, DO_NOT_EXPORT, NOT_SPECIFIED)
     */
    static HashMap experimentExportStatusCache = new HashMap();



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
    private static void init( IntactHelper helper ) throws IntactException, DatabaseContentException {

        uniprotDatabase = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, "uniprot" );
        if( uniprotDatabase == null ) {
            throw new DatabaseContentException( "Unable to find the UNIPROT database in your IntAct node" );
        }

        identityXrefQualifier = (CvXrefQualifier) helper.getObjectByLabel( CvXrefQualifier.class, "identity" );
        if( identityXrefQualifier == null ) {
            throw new DatabaseContentException( "Unable to find the identity CvXrefQualifier in your IntAct node" );
        }

        uniprotDR_Export = (CvTopic) helper.getObjectByLabel( CvTopic.class, UNIPROT_DR_EXPORT );
        if( uniprotDR_Export == null ) {
            throw new DatabaseContentException( "Unable to find the " + UNIPROT_DR_EXPORT + " CvTopic in your IntAct node" );
        }
    }

    /**
     * Get the uniprot primary ID from Protein and Splice variant.
     *
     * @param protein the Protein for which we want the uniprot ID.
     * @return the uniprot ID as a String or null if none is found (should not occur)
     */
    public static String getUniprotID( final Protein protein ) {

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
    private static Collection getInteractions( final Protein protein ) {
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
    private static CvInteractionStatus getMethodExportStatus( final CvInteraction cvInteraction ) {

        CvInteractionStatus status = null;

        // cache the CvInteraction status
        if( null != cvInteraction ) {

            CvInteractionStatus cache = (CvInteractionStatus) cvInteractionExportStatusCache.get( cvInteraction.getAc() );
            if( null != cache ) {

                status = cache;

            } else {

                boolean found = false;
                Collection annotations = null;

                annotations = cvInteraction.getAnnotations();
                Annotation annotation = null;
                for ( Iterator iterator = annotations.iterator(); iterator.hasNext(); ) {
                    Annotation _annotation = (Annotation) iterator.next();
                    if( uniprotDR_Export.equals( _annotation.getCvTopic() ) ) {
                        if( true == found ) {
                            // TODO Should we still export such method ? Could be correct still, eg yes + 3 !
                            System.err.println( "There are multiple annotation having Topic:" + UNIPROT_DR_EXPORT +
                                                " in CvInteraction: " + cvInteraction.getShortLabel() +
                                                ". \nWe will keep the first one: " + annotation );
                        } else {
                            found = true;
                            annotation = _annotation;
                        }
                    }
                }

                if( true == found ) {
                    if( "yes".equalsIgnoreCase( annotation.getAnnotationText() ) ) {

                        status = new CvInteractionStatus( CvInteractionStatus.EXPORT );

                    } else if( false == "no".equalsIgnoreCase( annotation.getAnnotationText() ) ) {

                        status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );

                    } else {

                        // it must be an integer value, let's check it.
                        String text = annotation.getAnnotationText();
                        try {
                            // TODO what if < 2 ?
                            Integer i = new Integer( text );
                            status = new CvInteractionStatus( CvInteractionStatus.CONDITIONAL_EXPORT,
                                                              i.intValue() );
                        } catch ( NumberFormatException e ) {
                            // not an integer !
                            System.err.println( cvInteraction.getShortLabel() + " having annotation (" + UNIPROT_DR_EXPORT +
                                                ") has an annotationText different from yes/no/<integer value> !!!" +
                                                " value was: " + text );
                            status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                        }
                    }
                } else {
                    // TODO no annotation == NO EXPORT ?
                    System.err.println( cvInteraction.getShortLabel() +
                                        " doesn't have an annotation: " + UNIPROT_DR_EXPORT );

                    status = new CvInteractionStatus( CvInteractionStatus.DO_NOT_EXPORT );
                }

                // cache it !
                cvInteractionExportStatusCache.put( cvInteraction.getAc(), status );
            }
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
    private static ExperimentStatus getExperimentExportStatus( final Experiment experiment ) {

        ExperimentStatus status = null;

        // cache the cvInteraction
        ExperimentStatus cache = (ExperimentStatus) experimentExportStatusCache.get( experiment.getAc() );
        if( null != cache ) {

            status = cache;

        } else {

            boolean yesFound = false;
            boolean noFound = false;
            boolean keywordFound = false;

            // most experiment won't need that, so we jsut allocate the collection when needed
            Collection keywords = null;

            Collection annotations = experiment.getAnnotations();
            for ( Iterator iterator = annotations.iterator(); iterator.hasNext(); ) {
                Annotation _annotation = (Annotation) iterator.next();
                if( uniprotDR_Export.equals( _annotation.getCvTopic() ) ) {
                    String text = _annotation.getAnnotationText();
                    // TODO case sensivity ?
                    if( "yes".equalsIgnoreCase( text ) ) {
                        yesFound = true;
                        System.out.println( "Experiment: " + experiment.getShortLabel() + ", yes" );

                    } else {
                        if( "no".equalsIgnoreCase( text ) ) {
                            noFound = true;
                            System.out.println( "Experiment: " + experiment.getShortLabel() + ", no" );

                        } else {
                            if( keywords == null ) {
                                keywords = new ArrayList( 2 );
                            }
                            keywordFound = true;
                            System.out.println( "Experiment: " + experiment.getShortLabel() + ", keyword: " + text );
                            keywords.add( text );
                        }
                    }
                }
            }

            // TODO mixed types ?
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

            experimentExportStatusCache.put( experiment.getAc(), status );
        }

        return status;
    }

    /**
     * Implement the set of rules that check if Proteins are eligible for export.
     *
     * @param helper data access
     * @return a Set of Protein ID (Uniprot) that are eligible for export
     * @throws IntactException
     */
    public static Set getProteinEligibleForExport( IntactHelper helper ) throws IntactException {

        Set selectedUniprotID = new HashSet();

        // get all proteins
        Collection proteins = helper.search( Protein.class.getName(), "shortlabel", "ab*" );
        System.out.println( proteins.size() + " protein(s) selected." );

        //map: method -> count of experiment in which the protein has been seen
        HashMap conditionalMethods = new HashMap();

        for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
            Protein protein = (Protein) iterator.next();
            String uniprotID = getUniprotID( protein );

            System.out.println( uniprotID + " Shortlabel:" + protein.getShortLabel() + "  AC: " + protein.getAc() );

            /**
             * In order to export a protein, it has to:
             *   - get its interactions
             *      - get its experiment
             *          - if the experiment has a local annotation allowing the export
             *          - if nothing specified: check if the CvInteraction has a local annotation allowing the export
             *          - if yes, the protein become eligible to export.
             *
             *
             */
            Collection interactions = getInteractions( protein );
            boolean export = false;
            boolean stop = false;
            conditionalMethods.clear();

            for ( Iterator iterator1 = interactions.iterator(); iterator1.hasNext() && !stop; ) {
                Interaction interaction = (Interaction) iterator1.next();

                Collection experiments = interaction.getExperiments();

                for ( Iterator iterator2 = experiments.iterator(); iterator2.hasNext() && !stop; ) {
                    Experiment experiment = (Experiment) iterator2.next();

                    ExperimentStatus experimentStatus = getExperimentExportStatus( experiment );
                    if( experimentStatus.doNotExport() ) {

                        // forbid export for all interaction of that experiment (and their proteins).
                        export = false;
                        stop = true;
                        continue;

                    } else if( experimentStatus.doExport() ) {

                        // Authorise export for all interactions of that experiment (and their proteins),
                        // even if method if Y2H.
                        export = true;
                        stop = true;

                        // TODO check if we need to check for method that requires a number of occurences.

                        continue;

                    } else if( experimentStatus.isLargeScale() ) {

                        // if my interaction has one of those keywords as annotation for DR line export, do export.
                        Collection keywords = experimentStatus.getKeywords();
                        Collection annotations = interaction.getAnnotations();
                        boolean found = false;
                        for ( Iterator iterator3 = annotations.iterator(); iterator3.hasNext() && !found; ) {
                            final Annotation annotation = (Annotation) iterator3.next();
                            String text = annotation.getAnnotationText();
                            for ( Iterator iterator4 = keywords.iterator(); iterator4.hasNext() && !found; ) {
                                String kw = (String) iterator4.next();
                                // TODO check with Henning about case sensitivity.
                                if( kw.equalsIgnoreCase( text ) ) {
                                    found = true;
                                }
                            }
                        }

                        if( found ) {
                            // TODO then do export but check first if this is not a method that requires a number of occurences.
                            export = true;
                            stop = true;
                        }

                    } else if( experimentStatus.isNotSpecified() ) {

                        // check the method
                        // Nothing specified at the experiment level, check for the method (CvInteraction)
                        CvInteraction cvInteraction = experiment.getCvInteraction();
                        if( null == cvInteraction ) {
                            // we need to check because this is not mandatory.
                            continue; // skip it
                        }

                        CvInteractionStatus methodStatus = getMethodExportStatus( cvInteraction );

                        if( methodStatus.doExport() ) {

                            export = true;
                            stop = true;

                        } else if( methodStatus.doNotExport() ) {

                            export = false;
                            stop = true;

                        } else if( methodStatus.isNotSpecified() ) {

                            export = false; // TODO: it should have been specified, isn't it ?
                            stop = true;

                        } else if( methodStatus.isConditionalExport() ) {

                            // TODO != papers or just experiments ?
                            // TODO it makes no sense if the threshold is < 2 ! Could be handled in the getMethodStatus method !

                            // non redundant set of experiment AC.
                            HashSet experimentAcs = (HashSet) conditionalMethods.get( cvInteraction.getAc() );
                            int threshold = methodStatus.getMinimumOccurence();

                            if( null == experimentAcs ) {
                                // not in yet
                                // at most we will need to store 'threshold' - 1 ACs. We don't store the last one.
                                experimentAcs = new HashSet( threshold - 1 );
                            }

                            if( experimentAcs.size() >= threshold ) {
                                // we still need to find more experiment to make that protein eligible for export
                                experimentAcs.add( experiment.getAc() );
                            } else {
                                // enough experiments found ... do export
                                export = true;
                                stop = true;
                            }
                        }
                    } // experiment status not specified
                } // experiments
            } // interactions

            if( export ) {
                // That protein is eligible for export.
                // The ID will be still unique even if it already exists.
                selectedUniprotID.add( uniprotID );
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

        IntactHelper helper = new IntactHelper();
        System.out.println( "Database instance: " + helper.getDbName() );
        System.out.println( "User: " + helper.getDbUserName() );

        init( helper );

        Set proteinEligible = getProteinEligibleForExport( helper );
        helper.closeStore();

        display( proteinEligible );
    }
}