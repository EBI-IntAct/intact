/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.uniprotExport;

import org.apache.commons.cli.*;
import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * That class allow to create a flat file containing the CC line to export to Uniprot.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class CCLineExport extends LineExport {

    ///////////////////////////////
    // Inner class

    public class ExportableInteraction {

        private Interaction interaction;
        private int experimentalSupportCount;

        public ExportableInteraction( Interaction interaction, int experimentalSupportCount ) {
            this.interaction = interaction;
            this.experimentalSupportCount = experimentalSupportCount;
        }

        public int getExperimentalSupportCount() {
            return experimentalSupportCount;
        }

        public Interaction getInteraction() {
            return interaction;
        }
    }

    private class CcLine implements Comparable {

        private final String ccLine;
        private final String geneName;

        public CcLine( String ccLine, String geneName ) {

            if( geneName == null ) {

            }

            this.ccLine = ccLine;
            this.geneName = geneName;
        }

        public String getCcLine() {
            return ccLine;
        }

        public String getGeneName() {
            return geneName;
        }

        public int compareTo( Object o ) {
            CcLine cc2 = null;
            cc2 = (CcLine) o;

            final String gene1 = getGeneName();
            final String gene2 = cc2.getGeneName();

            // the current string comes first if it's before in the alphabetical order

            if( gene1 == null ) {
                System.out.println( this );
            }

            if( gene1.equals( "Self" ) ) {

                return -1;

            } else if( gene2.equals( "Self" ) ) {

                return 1;

            } else {

                return gene1.compareTo( gene2 );
            }
        }


        public String toString() {
            return "CcLine{" +
                   "ccLine='" + ccLine + "'" +
                   ", geneName='" + geneName + "'" +
                   "}";
        }
    }


    ///////////////////////////////
    // Instance variables

    private IntactHelper helper;

    /**
     * Storage of the CC lines per protein.
     * Structure: Map( ProteinAC, Collection( CCLine ) )
     */
    private Map ccLines = new HashMap( 4096 );

    /**
     * Store Interaction's AC of those that have been already processed.
     */
    private HashSet alreadyProcessedInteraction = new HashSet( 4096 );

    /**
     * Use to out the CC lines in a file.
     */
    private Writer writer;


    ///////////////////////////////
    // Constructor

    public CCLineExport( IntactHelper helper, Writer writer ) throws IntactException, DatabaseContentException {
        super();
        init( helper );
        this.helper = helper;
        this.writer = writer;
    }


    ///////////////////////////////
    // Methods

    private boolean isNegative( Interaction interaction ) {

        boolean negative = false;

        if( super.isNegative( interaction ) ) {
            negative = true;
        } else {
            //check its experiments
            for ( Iterator iterator = interaction.getExperiments().iterator(); iterator.hasNext() && !negative; ) {
                Experiment experiment = (Experiment) iterator.next();

                if( isNegative( experiment ) ) {
                    negative = true;
                }
            }
        }

        return negative;
    }

    private Collection spliceVariants = new ArrayList( 16 );

    /**
     * retreives using the provided helper a Protein based on its Xref (uniprot, identity).
     *
     * @param helper    the data source
     * @param uniprotID the primary id of the cross reference
     * @return a Protein having Xref( uniprotId, uniprot, identity )
     * @throws IntactException if none or more than 2 proteins are found.
     */
    private Collection getProteinFromIntact( IntactHelper helper, String uniprotID ) throws IntactException {

        Collection proteins = helper.getObjectsByXref( Protein.class,
                                                       uniprotDatabase,
                                                       identityXrefQualifier,
                                                       uniprotID );

        if( proteins.size() == 0 ) {
            throw new IntactException( "the ID " + uniprotID + " didn't returned the expected number of proteins: " +
                                       proteins.size() + ". Abort." );
        }

        spliceVariants.clear();

        // now from that try to get splice variants (if any)
        for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
            Protein protein = (Protein) iterator.next();

            String ac = protein.getAc();
            Collection sv = helper.getObjectsByXref( Protein.class,
                                                     intactDatabase,
                                                     isoformParentQualifier,
                                                     ac );

            spliceVariants.addAll( sv );
        }

        proteins.addAll( spliceVariants );

        return proteins;
    }

    private void flushCCLine( String id ) throws IOException {

        List cc4protein = (List) ccLines.remove( id );

        if( null != cc4protein && !cc4protein.isEmpty() ) {

            // the the CC lines
            Collections.sort( cc4protein );

            StringBuffer sb = new StringBuffer( 128 * cc4protein.size() );

            sb.append( "AC" ).append( "   " ).append( id );
            sb.append( NEW_LINE );

            sb.append( "CC   -!- INTERACTION:" );
            sb.append( NEW_LINE );

            for ( Iterator iterator = cc4protein.iterator(); iterator.hasNext(); ) {
                CcLine ccLine = (CcLine) iterator.next();

                sb.append( ccLine.getCcLine() );
            }

            sb.append( "//" );
            sb.append( NEW_LINE );

            String ccs = sb.toString();

            // D E B U G
            System.out.println( ccs );

            // write the content in the output file.
            writer.write( ccs );
            writer.flush();

            // clean the cache.
//            ccLines.remove( id );
        }
    }

    /**
     * create the output of a CC line for a set of exportable interactions.
     *
     * @param uniprotID1      the uniprot AC of the protein to which that CC lines will be attached.
     * @param uniprotID2      the uniprot AC of the protein which interacts with the protein to which that CC
     *                        lines will be attached.
     * @param experimentCount count of distinct experimental support for that CC line.
     */
    private void createCCLine( String uniprotID1, Protein protein1,
                               String uniprotID2, Protein protein2,
                               int experimentCount ) {

        // produce the CC lines for the 1st protein
        CcLine cc1 = formatCCLines( uniprotID1, protein1, uniprotID2, protein2, experimentCount );
        List cc4protein1 = (List) ccLines.get( uniprotID1 );
        if( null == cc4protein1 ) {
            cc4protein1 = new ArrayList();
            ccLines.put( uniprotID1, cc4protein1 );
        }
        cc4protein1.add( cc1 );

        // produce the CC lines for the 2nd protein
        if( !uniprotID1.equals( uniprotID2 ) ) {
            CcLine cc2 = formatCCLines( uniprotID2, protein2, uniprotID1, protein1, experimentCount );
            List cc4protein2 = (List) ccLines.get( uniprotID2 );
            if( null == cc4protein2 ) {
                cc4protein2 = new ArrayList();
                ccLines.put( uniprotID2, cc4protein2 );
            }
            cc4protein2.add( cc2 );
        }
    }

    /**
     * Generate the CC line content based on the Interaction and its two interactor.
     * <br>
     * protein1 is the entry in which that CC content will appear.
     * <p/>
     * <pre>
     *          <font color=gray>ID   X_HUMAN     STANDARD;      PRT;   208 AA.</font>
     *          <font color=gray>AC   P45594</font>
     *          <font color=gray>DE   blablabla.</font>
     *          <font color=gray>GN   X OR Y.</font>
     *          CC   -!- INTERACTION:
     *          CC       Self; Experiments=1; AC=EBI-307456,EBI-307456;
     *          CC       P10981:tsr; Experiments=4; AC=EBI-307456,EBI-234567;
     *          CC       P01232:rr44; Experiments=3; AC=EBI-307456,EBI-237;
     *          <font color=gray>DR   IntAct; P45594, -.</font>
     * </pre>
     *
     * @param uniprotID1      uniprot ID of the protein in which we will export that CC line
     * @param protein1        IntAct associated protein
     * @param uniprotID2      uniprot ID of the protein with which interacts protein 1
     * @param protein2        IntAct associated protein
     * @param experimentCount count of distinct experimental support
     * @return a CCLine
     */
    private CcLine formatCCLines( String uniprotID1, Protein protein1,
                                  String uniprotID2, Protein protein2,
                                  int experimentCount ) {

        StringBuffer buffer = new StringBuffer( 128 ); // average size is 160 char (without NEGATIVE, CAUTION, NOTES)

        buffer.append( "CC       " );

        String geneName = null;
        if( uniprotID1.equals( uniprotID2 ) ) {

            geneName = "Self";
            buffer.append( geneName );

        } else {

            // A gene must be there ... it must have been checked before.
            geneName = getGeneName( protein2 );
            buffer.append( uniprotID2 ).append( ':' ).append( geneName );
        }

        buffer.append( ';' ).append( ' ' ).append( "Experiments=" ).append( experimentCount ).append( ';' ).append( ' ' );
        buffer.append( "AC=" ).append( protein1.getAc() ).append( ',' ).append( protein2.getAc() );
        buffer.append( NEW_LINE );

        // generated warning message if the two protein are from different organism
        if( !protein1.getBioSource().equals( protein2.getBioSource() ) ) {
            buffer.append( "**   CAUTION: Species" );
            buffer.append( NEW_LINE );
        }

        System.out.println( "\t\t\t\t" + buffer.toString() );

        return new CcLine( buffer.toString(), geneName );
    }

    /**
     * process a set of interaction ACs in order to find out which interaction are eligible for CC export.
     * <br>
     * Note 1: those interactions are specific of 2 known proteins.
     * Note 2: an interaction is eligible onl in the context of the given interactions.
     * Note 3: all given interactions are binary, not need to check again.
     * <br>
     * eg. if an interaction has a conditional export (eg. Y2H's experiment) we would look for a number of other
     * interaction having the same experimental method but in different experiment. We would only search in
     * the set of given interaction
     * <br>
     * <p/>
     * <pre>
     * Algorithm sketch
     * ----------------
     *   let INTERACTIONS be the collection of given interactions.
     * <p/>
     *   For each Interaction i in INTERACTIONS do
     *       skip (i) if i is not a binary interaction
     *       For each experiment e attached to Interaction i
     *           skip (e) if status is not exportable
     *           if status is export:
     *               keeps track of it
     *           if no status specified:
     *               Check the CvInteraction cv status
     *               skip (e) if status is no exportable
     *               if status of cv is export: validate the experiment e as exportable
     *               if status is conditional
     *                   let t be the threshold of distinct experiment needed to validate that status
     *                   check in the current interaction if an other experiment can validate the export
     *                   if not
     *                       For each Interaction ii in INTERACTIONS do (each but i)
     *                           if ii.experiment.cvInteraction = cv and ii.experiment not equals to e.
     *                              count it.
     *                           if count >= t, status of cvInteraction gets validated.
     * <p/>
     * </pre>
     *
     * @param interactions a collection of interaction AC
     * @return Collection( ExportableInteraction ) or can be null if no interaction have been found to
     *         be eligible for CC export
     */
    private int getEligibleExperimentCount( List interactions ) {

        Collection eligibleExperiments = new HashSet();

        final int interactionCount = interactions.size();
        for ( int i = 0; i < interactionCount; i++ ) {

            Interaction interaction = (Interaction) interactions.get( i );

            log( "\t\t\t\t Interaction: Shortlabel:" + interaction.getShortLabel() + "  AC: " + interaction.getAc() );

            if( isNegative( interaction ) ) {

                log( "\t\t\t\t\t That interaction or at least one of its experiments is negative, skip it." );
                continue; // skip that interaction
            }

            Collection experiments = interaction.getExperiments();

            int expCount = experiments.size();
            log( "\t\t\t\t\t interaction related to " + expCount + " experiment" + ( expCount > 1 ? "s" : "" ) + "." );

            for ( Iterator iterator2 = experiments.iterator(); iterator2.hasNext(); ) {
                Experiment experiment = (Experiment) iterator2.next();

                boolean experimentExport = false;
                log( "\t\t\t\t\t\t Experiment: Shortlabel:" + experiment.getShortLabel() + "  AC: " + experiment.getAc() );

                ExperimentStatus experimentStatus = getExperimentExportStatus( experiment, "\t\t\t" );
                if( experimentStatus.doNotExport() ) {
                    // forbid export for all interactions of that experiment (and their proteins).
                    log( "\t\t\t\t\t\t\t No interactions of that experiment will be exported." );

                } else if( experimentStatus.doExport() ) {
                    // Authorise export for all interactions of that experiment (and their proteins),
                    // This overwrite the setting of the CvInteraction concerning the export.
                    log( "\t\t\t\t\t\t\t All interaction of that experiment will be exported." );

                    experimentExport = true;

                } else if( experimentStatus.isLargeScale() ) {

                    // if my interaction has one of those keywords as annotation for DR line export, do export.
                    Collection keywords = experimentStatus.getKeywords();
                    Collection annotations = interaction.getAnnotations();
                    boolean annotationFound = false;

                    // We assume here that an interaction has only one Annotation( uniprot-dr-export ).
                    for ( Iterator iterator3 = annotations.iterator(); iterator3.hasNext() && !annotationFound; ) {
                        final Annotation annotation = (Annotation) iterator3.next();

                        if( authorConfidenceTopic.equals( annotation.getCvTopic() ) ) {
                            String text = annotation.getAnnotationText();

                            log( "\t\t\t\t Interaction has " + authorConfidenceTopic.getShortLabel() + ": '" + text + "'" );

                            if( text != null ) {
                                text = text.trim();
                            }

                            for ( Iterator iterator4 = keywords.iterator(); iterator4.hasNext() && !annotationFound; ) {
                                String kw = (String) iterator4.next();
                                // NOT case sensitive

                                log( "\t\t\t\t\t Compare it with '" + kw + "'" );

                                if( kw.equalsIgnoreCase( text ) ) {
                                    annotationFound = true;
                                    log( "\t\t\t\t\t Equals !" );
                                }
                            }
                        }
                    }

                    if( annotationFound ) {

                        /*
                        * We don't need to check an eventual threshold on the method level because
                        * in the current state, the annotation is on the experiment level that is
                        * lower and hence is dominant on the method's one.
                        */

                        experimentExport = true;
                        log( "\t\t\t that interaction is eligible for export in the context of a large scale experiment" );

                    } else {

                        log( "\t\t\t interaction not eligible" );
                    }

                } else if( experimentStatus.isNotSpecified() ) {

                    log( "\t\t\t\t\t\t No experiment status, check the experimental method." );

                    // Then check the experimental method (CvInteraction)
                    // Nothing specified at the experiment level, check for the method (CvInteraction)
                    CvInteraction cvInteraction = experiment.getCvInteraction();

                    if( null == cvInteraction ) {
                        // we need to check because cvInteraction is not mandatory in an experiment.
                        continue; // skip it, go to next experiment
                    }

                    CvInteractionStatus methodStatus = getMethodExportStatus( cvInteraction, "\t\t" );

                    if( methodStatus.doExport() ) {

                        experimentExport = true;

                    } else if( methodStatus.doNotExport() ) {

                        // do nothing

                    } else if( methodStatus.isNotSpecified() ) {

                        // we should never get in here but just in case...
                        // do nothing

                    } else if( methodStatus.isConditionalExport() ) {

                        log( "\t\t\t\t\t\t As conditional export, check the count of distinct experiment for that method." );

                        // if the threshold is not reached, iterates over all available interactions to check if
                        // there is (are) one (many) that could allow to reach the threshold.

                        int threshold = methodStatus.getMinimumOccurence();

                        // we create a non redondant set of experiment identifier
                        Set experimentAcs = new HashSet( threshold );

                        // check if there are other experiments attached to the current interaction that validate it.
                        boolean enoughExperimentFound = false;
                        for ( Iterator iterator = experiments.iterator(); iterator.hasNext(); ) {
                            Experiment experiment1 = (Experiment) iterator.next();

                            log( "\t\t\t\t\t\t Experiment: Shortlabel:" + experiment1.getShortLabel() + "  AC: " + experiment1.getAc() );

                            CvInteraction method = experiment1.getCvInteraction();

                            if( cvInteraction.equals( method ) ) {
                                experimentAcs.add( experiment1.getAc() );

                                // we only update if we found one
                                enoughExperimentFound = ( experimentAcs.size() >= threshold );
                            }
                        }

                        log( "\t\t\t\t\tLooking for other interaction that support that method in other experiemnts..." );

                        for ( int j = 0; j < interactionCount && !enoughExperimentFound; j++ ) {

                            if( i == j ) {
                                continue;
                            }

                            //
                            // Have that conditionalMethods at the interaction scope.
                            //
                            // for a interaction
                            //      for each experiment e
                            //          if e.CvInteraction <> cvInteraction -> continue
                            //          else is experiment already processed ? if no, add and check the count >= threashold.
                            //                                                 if reached, stop, esle carry on.
                            //

                            Interaction interaction2 = (Interaction) interactions.get( j );

                            log( "\t\t\t\t Interaction: Shortlabel:" + interaction2.getShortLabel() + "  AC: " + interaction2.getAc() );

                            Collection experiments2 = interaction2.getExperiments();

                            for ( Iterator iterator6 = experiments2.iterator(); iterator6.hasNext() && !enoughExperimentFound; ) {
                                Experiment experiment2 = (Experiment) iterator6.next();
                                log( "\t\t\t\t\t\t Experiment: Shortlabel:" + experiment2.getShortLabel() + "  AC: " + experiment2.getAc() );

                                CvInteraction method = experiment2.getCvInteraction();

                                if( cvInteraction.equals( method ) ) {
                                    experimentAcs.add( experiment2.getAc() );
                                    // we only update if we found one
                                    enoughExperimentFound = ( experimentAcs.size() >= threshold );
                                }
                            } // j's experiments

                            log( "\t\t\t\t\t\t " + cvInteraction.getShortLabel() + ", threshold: " +
                                 threshold + " #experiment: " +
                                 ( experimentAcs == null ? "none" : "" + experimentAcs.size() ) );
                        } // j

                        if( enoughExperimentFound ) {
                            log( "\t\t\t\t\t\t Enough experiemnt found" );
                            experimentExport = true;
                        } else {
                            log( "\t\t\t\t\t\t Not enough experiemnt found" );
                        }

                    } // conditional status
                } // experiment status not specified

                if( experimentExport ) {
                    eligibleExperiments.add( experiment );
                }

            } // i's experiments

        } // i

        return eligibleExperiments.size();
    }

    /**
     * Write the CC line export in an output file describe by the given filename.
     * <pre>
     * algo sketch
     * -----------
     * <p/>
     * For each protein eligible for DR export do
     *   - let's call the selected protein P1
     *   - get a set of IntAct proteins from P1's uniprot ID
     *   - get interactions of P1
     *   - while( collection not empty )
     *      - take the first interaction [P1, P2]
     *      - skip it if it is not a binary interaction
     *      - skip it if P2 is not eligible for DR export
     *      - get a set of IntAct proteins from P2's uniprot ID
     *      - get from the interaction of P1, all interaction that involve both P1 and P2. Let call it i
     *      - run algo that select from the set i which protein are to be exported as CC line
     *      - generate the CC line for each Interaction of the resulting set and associate it to P1 and P2
     *   - Write the CC lines of P1 in a file (P2 is not complete yet)
     * <p/>
     * Note: an interaction that as been already processed won't be a second time.
     * </pre>
     *
     * @param uniprotIDs a set of protein ID (uniprot AC)
     * @throws IntactException
     * @throws SQLException
     */
    public void generateCCLines( Set uniprotIDs ) throws IntactException,
                                                         SQLException, IOException {

        int count = uniprotIDs.size();
        int idProcessed = 0;
        int percentProteinProcessed;
        List potentiallyEligibleInteraction = new ArrayList( 16 );

        // iterate over the Uniprot ID of the protein that have been selected for DR export.
        for ( Iterator iteratorUniprotId = uniprotIDs.iterator(); iteratorUniprotId.hasNext(); ) {

            String uniprotID_1 = (String) iteratorUniprotId.next();

            idProcessed++;

            log( NEW_LINE + "Protein selected: " + uniprotID_1 );

            percentProteinProcessed = (int) ( ( (float) idProcessed / (float) count ) * 100 );
            log( "Protein processed: " + percentProteinProcessed + "% (" + idProcessed + " out of " + count + ")" );
            log( "Interaction processed: " + alreadyProcessedInteraction.size() );

            // get the protein's interactions
            Collection proteinSet_1 = getProteinFromIntact( helper, uniprotID_1 ); // might be enough to go through SQL

            for ( Iterator iteratorP = proteinSet_1.iterator(); iteratorP.hasNext(); ) {
                Protein protein1 = (Protein) iteratorP.next();

                if( getGeneName( protein1 ) == null ) {

                    System.err.println( protein1.getShortLabel() + " has no gene name." );
                    continue;
                }

                Collection interactionP1 = getInteractions( protein1 );

                Protein protein2 = null;
                String uniprotID_2 = null;

                // while( collection not empty )
                //     take the first interaction
                //     get 2 sets of proteins (interaction partner)
                //     get from the interaction set all interaction that have both interactor (remove them)
                //     run algo

                log( "\t" + uniprotID_1 + "(" + protein1.getBioSource().getShortLabel() + ")" + " has " + interactionP1.size() + " interaction(s)." );


                while ( !interactionP1.isEmpty() ) {

                    Iterator iterator = interactionP1.iterator(); // can't be ouside the loop
                    Interaction interaction = (Interaction) iterator.next();

                    log( NEW_LINE + "\t Process interaction : " + interaction.getShortLabel() + " (" + interaction.getAc() + ")" );

                    // Let's say we process P1 and P2, the subset of the interactions of P1 if then stored in
                    // the cache of already processed interactions. There is no reason why those interaction
                    // would be processed again since they are binary, hence specific to P1 and P2
                    // So when we process P2 and load again those same interactions from the DB, we can skip them.
                    if( alreadyProcessedInteraction.contains( interaction.getAc() ) ) {

                        log( "\t\t That interaction has been processed already ... skip it." );
                        iterator.remove(); // remove it.

                    } else {
                        alreadyProcessedInteraction.add( interaction.getAc() );

                        if( false == isBinary( interaction ) ) {

                            iterator.remove();

                        } else {

                            Component component1 = null;
                            Component component2 = null;

                            if( interaction.getComponents().size() == 1 ) {
                                Iterator iteratorC = interaction.getComponents().iterator();
                                component2 = component1 = (Component) iteratorC.next();
                            } else {
                                // must be 2
                                Iterator iteratorC = interaction.getComponents().iterator();
                                component1 = (Component) iteratorC.next();
                                component2 = (Component) iteratorC.next();
                            }

                            log( "\t\t Interaction has exactly 2 interactors." );

                            // now get the other protein ( other than uniprotID_1 )

                            /**
                             * Uniprot ID -> Collection(Protein) (ie. all species + splice variants)
                             *
                             * in the interaction, for each component, if the protein is part of the collection, take the protein
                             * attached to the other component. If still the same, that means that we have the same protein
                             * interacting as self. could be SV+P or different species though
                             */

                            // we assume that they carry only Protein
                            log( "\t\t Check what is the partner of " + uniprotID_1 );

                            Protein p1 = (Protein) component1.getInteractor();
                            log( "\t\t 1st Partner found: " + p1.getShortLabel() );

                            Protein p2 = (Protein) component2.getInteractor();
                            log( "\t\t 2nd Partner found: " + p2.getShortLabel() );

                            if( protein1.equals( p1 ) ) {
                                protein2 = p2;
                            } else {
                                protein2 = p1;
                            }

                            if( getGeneName( protein2 ) == null ) {

                                System.err.println( protein2.getShortLabel() + " has no gene name, skip that interaction." );
                                iterator.remove(); // remove it.
                                continue;
                            }

                            uniprotID_2 = getUniprotID( protein2 );

                            // We don't take into account interactions that involve proteins not eligible for DR export.
                            if( !uniprotIDs.contains( uniprotID_2 ) ) {

                                log( "\n\t\t " + uniprotID_2 + " was not eligible for DR export, that interaction is not taken into account." );
                                iterator.remove();

                            } else {

                                log( "\n\t\t Look for interactions amongst " + protein1.getShortLabel() + " and " + protein2.getShortLabel() + "." );

                                // extract all interactions have partner in protein1 and proteins2
                                potentiallyEligibleInteraction.clear();

                                // add the current one
                                potentiallyEligibleInteraction.add( interaction );
                                iterator.remove();
                                log( "\t\t Add the current one: " + interaction.getShortLabel() + "(" + interaction.getAc() + ")" );

                                for ( Iterator iterator2 = interactionP1.iterator(); iterator2.hasNext(); ) {
                                    Interaction interaction1 = (Interaction) iterator2.next();

                                    if( alreadyProcessedInteraction.contains( interaction1.getAc() ) ) {

                                        log( "\t\t That interaction " + interaction1.getShortLabel() + "(" +
                                             interaction1.getAc() + ") has been processed already ... skip it." );
                                        iterator2.remove(); // remove it.

                                    } else {

                                        if( !isBinary( interaction1 ) ) {

                                            alreadyProcessedInteraction.add( interaction1.getAc() );
                                            iterator2.remove(); // remove using the local iterator

                                        } else {

                                            Component c1 = null;
                                            Component c2 = null;

                                            if( interaction1.getComponents().size() == 1 ) {
                                                Iterator iteratorC = interaction1.getComponents().iterator();
                                                c2 = c1 = (Component) iteratorC.next(); // same component, hence interactor
                                            } else {
                                                // must be 2
                                                Iterator iteratorC = interaction1.getComponents().iterator();
                                                c1 = (Component) iteratorC.next();
                                                c2 = (Component) iteratorC.next();
                                            }

                                            Protein p1_ = (Protein) c1.getInteractor();
                                            Protein p2_ = (Protein) c2.getInteractor();

                                            boolean normal = protein1.equals( p1_ ) && protein2.equals( p2_ );
                                            boolean reverse = protein1.equals( p2_ ) && protein2.equals( p1_ );

                                            if( normal || reverse ) {

                                                alreadyProcessedInteraction.add( interaction1.getAc() );

                                                // select that interaction
                                                potentiallyEligibleInteraction.add( interaction1 );
                                                iterator2.remove();

                                                log( "\t\t Add: " + interaction1.getShortLabel() + "(" + interaction1.getAc() + ")" );
                                            }
                                        } // else
                                    } // else
                                } // selection of the interactions

                                log( "\t\t\t They have " + potentiallyEligibleInteraction.size() + " interaction(s) in common" );

                                // run the algo
                                int experimentCount = getEligibleExperimentCount( potentiallyEligibleInteraction );

                                if( experimentCount > 0 ) {

                                    createCCLine( uniprotID_1, protein1,
                                                  uniprotID_2, protein2,
                                                  experimentCount );
                                }

                            } // else (uniprotID2 is eligible for DR export)
                        } // else (interaction is binary)
                    } // else (interaction hasn't been processed yet)
                } // while there is interactions
            } // proteins associated to UniprotID_1

            // write the CC content of protein still designated by index 'i' as its processing is finished.
            flushCCLine( uniprotID_1 );
        } // i (all eligible uniprot IDs)

        // flush and close output file
        if( null != writer ) {
            try {
                writer.close();
                System.out.println( "Output file closed." );
            } catch ( IOException e ) {
                System.err.println( "Could not close the output file." );
            }
        }
    }


    /**
     * Convert the DR line export file to an String array of Uniprot IDs.
     *
     * @param file the DR line export file.
     * @return a String array of Uniprot IDs.
     * @throws IOException if the file handling goes wrong.
     */
    public static Set getEligibleProteinsFromFile( String file ) throws IOException {

        InputStream is = new FileInputStream( file );
        InputStreamReader isr = new InputStreamReader( is );
        BufferedReader reader = new BufferedReader( isr );

        // Using a HashSet will avoid redundancy
        Set proteins = new HashSet( 4096 );

        String line = null;
        int count = 0;
        while ( ( line = reader.readLine() ) != null ) {
            count++;
            line = line.trim();
            if( "".equalsIgnoreCase( line ) ) {
                continue;
            }

            // format of that line is: UniprotID \t IntAct \t UniprotId \t -
            // eg. P000001 Intact P00001 -

            StringTokenizer st = new StringTokenizer( line, "\t" ); // tab delimited field.
            String uniprotID;
            if( st.hasMoreTokens() ) {
                uniprotID = st.nextToken();
                proteins.add( uniprotID );
            } else {
                System.err.println( "Could not parse line " + count + ":" + line );
            }
        }

        is.close();

        // the given parameter is just needed to type the returned collection.
        // @see java.util.Collection.toArray(Object a[])
        return proteins;
    }


    private static void displayUsage( Options options ) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "CCLineExport <DR export file> [-debug] [-debugFile]", options );
    }

    public static void main( String[] args )
            throws IntactException,
                   SQLException, IOException, DatabaseContentException {

        // create Option objects
        Option helpOpt = new Option( "help", "print this message" );

        Option drExportOpt = OptionBuilder.withArgName( "drExportFilename" ).hasArg().withDescription( "DR export output file." ).create( "drExport" );
        drExportOpt.setRequired( true );

        Option debugOpt = OptionBuilder.withDescription( "Shows verbose output." ).create( "debug" );
        debugOpt.setRequired( false );

        Option debugFileOpt = OptionBuilder.withDescription( "Store verbose output in the specified file." ).create( "debugFile" );
        debugFileOpt.setRequired( false );

        Options options = new Options();

        options.addOption( helpOpt );
        options.addOption( drExportOpt );
        options.addOption( debugOpt );
        options.addOption( debugFileOpt );

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

        boolean debugEnabled = line.hasOption( "debug" );
        boolean debugFileEnabled = line.hasOption( "debugFile" );
        String drExportFilename = line.getOptionValue( "drExport" );

        System.out.println( "Try to upen: " + drExportFilename );
        Set uniprotIDs = getEligibleProteinsFromFile( drExportFilename );
        System.out.println( "DR proteins loaded from file: " + drExportFilename );

        // create a database accessI
        IntactHelper helper = new IntactHelper();
        try {
            System.out.println( "Database instance: " + helper.getDbName() );
            System.out.println( "User: " + helper.getDbUserName() );
        } catch ( LookupException e ) {
            System.err.println( "Could not get database information (instance name and username)." );
        }


        // Prepare output file.
        String filename = "CCLineExport_" + TIME + ".txt";
        File file = new File( filename );
        System.out.println( "Try to save to: " + file.getAbsolutePath() );
        BufferedWriter out = null;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter( file );
            out = new BufferedWriter( fileWriter );

            CCLineExport exporter = new CCLineExport( helper, (Writer) fileWriter );
            exporter.setDebugEnabled( debugEnabled );
            exporter.setDebugFileEnabled( debugFileEnabled );

            // launch the CC export
            exporter.generateCCLines( uniprotIDs );

        } catch ( IOException e ) {
            e.printStackTrace();
            System.err.println( "Could not create the output file:" + filename );
            System.exit( 1 );

        } finally {
            if( out != null ) {
                try {
                    out.close();
                } catch ( IOException e ) {
                    System.exit( 1 );
                }
            }

            if( fileWriter != null ) {
                try {
                    fileWriter.close();
                } catch ( IOException e ) {
                    System.exit( 1 );
                }
            }
        }

        System.exit( 0 );
    }
}
