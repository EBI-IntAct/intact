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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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


    ///////////////////////////////
    // Instance variables

    private IntactHelper helper;

    private static final String INTERATION_FROM_PROTEIN = "select interaction_ac " +
                                                          "from ia_component " +
                                                          "where interactor_ac = ?";
    private PreparedStatement interactionSelect = null;

    /**
     * Storage of the CC lines per protein.
     * Structure: Map( ProteinAC, Collection( CCLine ) )
     */
    private Map ccLines = new HashMap( 4096 );

    // in order to avoid repetive, yet, unuseful SQL query, we cache the interactions' ac to which a protien is linked.
    private Map proteinInteractionsCache = new HashMap();

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

    /**
     * get the stochiometry of the two components.
     * It creates an arrays of size two and the first element is the stochiometry of the protein given in parameter.
     * If both interactor
     *
     * @param interaction
     * @param protein
     * @return
     */
    private float[] getStoichiometry( Interaction interaction, Protein protein ) {

        float[] sto = new float[ 2 ];

        Iterator iterator = interaction.getComponents().iterator();
        Component component1 = (Component) iterator.next();
        Component component2 = (Component) iterator.next();

        if( protein.equals( component1.getInteractor() ) ) {
            sto[ 0 ] = component1.getStoichiometry();
            sto[ 1 ] = component2.getStoichiometry();
        } else {
            //
            sto[ 1 ] = component1.getStoichiometry();
            sto[ 0 ] = component2.getStoichiometry();
        }

        return sto;
    }

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

        // TODO we might have to add the splice variant to that collection later
        // (ie. iterate over proteins and search by Xref for the AC)

        if( proteins == null ) {
            throw new IntactException( "Could not retreive data from the helper (received: null) using the ID: " +
                                       uniprotID + ". Abort." );
        }

        if( proteins.size() == 0 ) {
            throw new IntactException( "the ID " + uniprotID + " didn't returned the expected number of proteins: " +
                                       proteins.size() + ". Abort." );
        }

        return proteins;
    }

    private void flushCCLine( String id ) throws IOException {

        Collection cc4protein = (Collection) ccLines.get( id );

        if( null != cc4protein && !cc4protein.isEmpty() ) {
            // write the content in the output file.

            StringBuffer sb = new StringBuffer( 128 * cc4protein.size() );
            sb.append( "AC" ).append( "   " ).append( id );
            sb.append( NEW_LINE );

            for ( Iterator iterator = cc4protein.iterator(); iterator.hasNext(); ) {
                String cc = (String) iterator.next();

                sb.append( cc );

            } // all CCs

            sb.append( "//" );
            sb.append( NEW_LINE );

            // D E B U G
            String ccs = sb.toString();
            System.out.println( ccs );
            writer.write( ccs );
            writer.flush();

            // clean the cache.
            ccLines.remove( id );
        }
    }

    /**
     * create the output of a CC line for a set of exportable interactions.
     *
     * @param uniprotID1           the uniprot AC of the protein to which that CC lines will be attached.
     * @param uniprotID2           the uniprot AC of the protein which interacts with the protein to which that CC
     *                             lines will be attached.
     * @param eligibleInteractions interactions for which we have to generate a CC line content (1 per interaction).
     */
    private void createCCLine( String uniprotID1, String uniprotID2, Collection eligibleInteractions ) {

        for ( Iterator iterator = eligibleInteractions.iterator(); iterator.hasNext(); ) {
            ExportableInteraction exportableInteraction = (ExportableInteraction) iterator.next();

            // get the protein 1 and 2 from the interaction
            Interaction interaction = exportableInteraction.getInteraction();
            Iterator iterator2 = interaction.getComponents().iterator();

            Component component1 = (Component) iterator2.next();
            Protein protein1 = (Protein) component1.getInteractor();

            Component component2 = (Component) iterator2.next();
            Protein protein2 = (Protein) component2.getInteractor();

            // check if protein1 is related to uniprotID1
            String uniprotID = getUniprotID( protein1 );
            if( !uniprotID.equals( uniprotID1 ) ) {
                // then just reverse the reference of the proteins
                Protein tmp = protein1;
                protein1 = protein2;
                protein2 = tmp;
            }

            // produce the CC lines for the 1st protein
            String cc1 = createCCLines( uniprotID1, protein1, uniprotID2, protein2, exportableInteraction );
            Collection cc4protein1 = (Collection) ccLines.get( uniprotID1 );
            if( null == cc4protein1 ) {
                cc4protein1 = new ArrayList();
                ccLines.put( uniprotID1, cc4protein1 );
            }
            cc4protein1.add( cc1 );

            // produce the CC lines for the 2nd protein
            if( !uniprotID1.equals( uniprotID2 ) ) {
                String cc2 = createCCLines( uniprotID2, protein2, uniprotID1, protein1, exportableInteraction );
                Collection cc4protein2 = (Collection) ccLines.get( uniprotID2 );
                if( null == cc4protein2 ) {
                    cc4protein2 = new ArrayList();
                    ccLines.put( uniprotID2, cc4protein2 );
                }
                cc4protein2.add( cc2 );
            }
        }
    }

    /**
     * Generate the CC line content based on the Interaction and its two interactor.
     * <br>
     * protein1 is the entry in which that CC content will appear.
     * <p/>
     * <pre>
     *          <font color=gray>ID   RAC1_HUMAN     STANDARD;      PRT;   192 AA.</font>
     *          <font color=gray>AC   P15154; O95501; Q9BTB4;</font>
     *          CC   -!- INTERACTION: IntAct=EBI-00011, EBI-00012;
     *          CC       Type=aggregation; Experimental=multiple;
     *          CC       Participants=P15154:self[1], Q14185:DOCK180[1];
     *          <font color=gray>DR   IntAct; P15154, -.</font>
     *          //
     * <p/>
     *          <font color=gray>ID   RB6A_HUMAN     STANDARD;      PRT;   208 AA.</font>
     *          <font color=gray>AC   P20340; Q9UBE4;</font>
     *          <font color=gray>DE   Ras-related protein Rab-6A (Rab-6).</font>
     *          <font color=gray>GN   RAB6A OR RAB6.</font>
     *          <font color=gray>CC   -!- SUBUNIT: Isoform 1 interacts with RAB6KIFL but not isoform 2.</font>
     *          CC   -!- INTERACTION: IntAct=EBI-00014, EBI-32323;
     *          CC       Type=aggregation; Experimental=multiple;
     *          CC       Participants=P20340-1:self[1], O95235:RAB6KIFL[1];
     *          CC       Note=Activated by bound GTP;
     *          CC   -!- INTERACTION: IntAct=EBI-00015, EBI-323222;
     *          CC       Type=aggregation; Experimental=multiple;
     *          CC       Participants=P20340-2:self[1], O95235:RAB6KIFL[1];
     *          CC       NEGATIVE=true;
     *          <font color=gray>DR   IntAct; P20340, -.</font>
     * </pre>
     *
     * @param protein1
     * @param protein2
     * @param exportableInteraction
     * @return
     */
    private String createCCLines( String uniprotID1, Protein protein1,
                                  String uniprotID2, Protein protein2,
                                  ExportableInteraction exportableInteraction ) {

        StringBuffer buffer = new StringBuffer( 256 ); // average size is 160 char (without NEGATIVE, CAUTION, NOTES)

        buffer.append( "CC   -!- INTERACTION: IntAct=" ).append( protein1.getAc() );
        buffer.append( ',' ).append( ' ' ).append( protein2.getAc() ).append( ';' );
        buffer.append( NEW_LINE );

        Interaction interaction = exportableInteraction.getInteraction();
        int experimentalSupportCount = exportableInteraction.getExperimentalSupportCount();

        buffer.append( "CC       Type=" ).append( interaction.getCvInteractionType().getShortLabel() ).append( ';' );
        buffer.append( " Experimental=" ).append( ( experimentalSupportCount > 1 ? "multiple" : "one" ) ).append( ';' );
        buffer.append( NEW_LINE );

        buffer.append( "CC       Participants=" );
        float[] stoichio = getStoichiometry( interaction, protein1 );
        buffer.append( uniprotID1 ).append( ':' ).append( "self" ).append( '[' ).append( stoichio[ 0 ] ).append( ']' );
        buffer.append( ',' ).append( ' ' );

        String geneName = null;
        if( !uniprotID1.equals( uniprotID2 ) ) {
            stoichio = getStoichiometry( interaction, protein2 );
            geneName = getGeneName( protein2 );
        } else {
            geneName = "self";
        }

        buffer.append( uniprotID2 ).append( ':' ).append( geneName ).append( '[' ).append( stoichio[ 1 ] ).append( ']' );
        buffer.append( ';' );
        buffer.append( NEW_LINE );

        // generated Note(s) if any
        Collection notes = getCCnote( interaction );
        if( null != notes ) {
            for ( Iterator iterator = notes.iterator(); iterator.hasNext(); ) {
                String note = (String) iterator.next();

                buffer.append( "CC       Note=" ).append( note ).append( ';' );
                buffer.append( NEW_LINE );
            }
        }

        // generates the NEGATIVE flag if needed
        if( isNegative( interaction ) ) {
            buffer.append( "CC       NEGATIVE=true;" );
            buffer.append( NEW_LINE );
        }

        // generated warning message if the two protein are from different organism
        if( !protein1.getBioSource().equals( protein2.getBioSource() ) ) {
            buffer.append( "**   CAUTION: Species" );
            buffer.append( NEW_LINE );
        }

        // System.out.println( "\t\t\t\t" + buffer.toString() );

        return buffer.toString();
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
    private Collection getEligibleInteractions( List interactions ) {

        // interaction that will be found eligible.
        Collection eligibleInteractions = null;

        // select an eligible interaction
        final int count = interactions.size();
        for ( int i = 0; i < count; i++ ) {

            Interaction interaction = (Interaction) interactions.get( i );

            log( "\t\t\t\t Interaction: Shortlabel:" + interaction.getShortLabel() + "  AC: " + interaction.getAc() );

            Collection experiments = interaction.getExperiments();
            int experimentalSupportCount = 0;

            int expCount = experiments.size();
            log( "\t\t\t\t\t interaction related to " + expCount + " experiment" + ( expCount > 1 ? "s" : "" ) + "." );

            boolean export = false;

            for ( Iterator iterator2 = experiments.iterator(); iterator2.hasNext(); ) {
                Experiment experiment = (Experiment) iterator2.next();
                log( "\t\t\t\t\t\t Experiment: Shortlabel:" + experiment.getShortLabel() + "  AC: " + experiment.getAc() );

                ExperimentStatus experimentStatus = getExperimentExportStatus( experiment, "\t\t\t" );
                if( experimentStatus.doNotExport() ) {
                    // forbid export for all interactions of that experiment (and their proteins).
                    log( "\t\t\t\t\t\t\t No interactions of that experiment will be exported." );

                } else if( experimentStatus.doExport() ) {
                    // Authorise export for all interactions of that experiment (and their proteins),
                    // This overwrite the setting of the CvInteraction concerning the export.
                    log( "\t\t\t\t\t\t\t All interaction of that experiment will be exported." );

                    export = true;
                    experimentalSupportCount++;

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

                        export = true;
                        experimentalSupportCount++;

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

                        export = true;
                        experimentalSupportCount++;

                    } else if( methodStatus.doNotExport() ) {

                        export = export || false;

                    } else if( methodStatus.isNotSpecified() ) {

                        // we should never get in here but just in case...
                        export = export || false;

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

                        log( "Looking for other interaction that support that method in other experiemnts..." );

                        for ( int j = 0; j < count && !enoughExperimentFound; j++ ) {

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
                            export = true;
                            experimentalSupportCount++;
                        }

                    } // conditional status
                } // experiment status not specified
            } // i's experiments

            if( export ) {
                // eligible for CC export
                if( null == eligibleInteractions ) {
                    eligibleInteractions = new ArrayList();
                }

                eligibleInteractions.add( new ExportableInteraction( interaction, experimentalSupportCount ) );
            }

        } // i

        return eligibleInteractions; // can be null if no interaction have been found to be eligible for CC export
    }

    /**
     * From a collection of Proteins, retreive from the database a collection of interaction AC.
     *
     * @param proteins a set of IntAct proteins
     * @return a collection of interaction AC.
     */
    private Collection getInteractionsAC( Collection proteins ) throws SQLException, IntactException {

        // TODO TEST if it is faster to use the getActiveInstance(components) instead of an SQL query.

        if( interactionSelect == null ) {
            Connection connection = helper.getJDBCConnection();
            interactionSelect = connection.prepareStatement( INTERATION_FROM_PROTEIN );
        }

        Collection allInteractionACs = new ArrayList();

        for ( Iterator iterator = proteins.iterator(); iterator.hasNext(); ) {
            Protein protein = (Protein) iterator.next();

            String ac = protein.getAc();
            Collection interactionACs = (Collection) proteinInteractionsCache.get( ac );

            if( null == interactionACs ) {
                // we don't have it in cache

                interactionSelect.setString( 1, ac );
                ResultSet resultSet = interactionSelect.executeQuery();

                interactionACs = new ArrayList( 4 );
                while ( resultSet.next() ) {
                    interactionACs.add( resultSet.getString( 1 ) );
                }

                resultSet.close();

                // cache it
                proteinInteractionsCache.put( ac, interactionACs );
            }

            allInteractionACs.addAll( interactionACs );
        }

        return allInteractionACs;
    }

    /**
     * Gives a Collection of IntAct Interaction from the given collection of interaction AC.
     *
     * @param interactionsAC a collection of interaciton ACs.
     * @return a collection of Interaction object. Can be empty.
     * @see uk.ac.ebi.intact.model.Interaction
     */
    private ArrayList getRealInteractions( Collection interactionsAC ) throws IntactException {

        ArrayList realInteractions = new ArrayList( interactionsAC.size() );

        for ( Iterator iterator = interactionsAC.iterator(); iterator.hasNext(); ) {
            String ac = (String) iterator.next();

            Collection i = helper.search( Interaction.class.getName(), "ac", ac );
            if( null == i || i.size() != 1 ) {
                throw new IntactException( "Could not retreive interaction having the AC: " + ac );
            }

            realInteractions.add( i.iterator().next() );
        }

        return realInteractions;
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
        int eligibleInteractionsCount = 0;
        int percentEligibleInteraction;
        int percentProteinProcessed;

        // iterate over the Uniprot ID of the protein that have been selected for DR export.
        for ( Iterator iteratorUniprotId = uniprotIDs.iterator(); iteratorUniprotId.hasNext(); ) {

            String uniprotID_1 = (String) iteratorUniprotId.next();
            idProcessed++;
            log( NEW_LINE + "Protein selected: " + uniprotID_1 );

            percentProteinProcessed = (int) ( ( (float) idProcessed / (float) count ) * 100 );
            log( "Protein processed: " + percentProteinProcessed + "% (" + idProcessed + " out of " + count + ")" );

            int interactionProcessed = alreadyProcessedInteraction.size();
            if( interactionProcessed > 0 ) {
                percentEligibleInteraction = (int) ( ( (float) eligibleInteractionsCount / (float) interactionProcessed ) * 100 );
            } else {
                percentEligibleInteraction = 0;
            }
            log( "Eligible interaction: " + percentEligibleInteraction + "%  (" + eligibleInteractionsCount + " out of " + interactionProcessed + ")" );

            // get the protein's interactions
            Collection proteinSet_1 = getProteinFromIntact( helper, uniprotID_1 ); // might be enough to go through SQL
            Collection interactionsP1 = getInteractionsAC( proteinSet_1 );
            Collection realInteractionP1 = getRealInteractions( interactionsP1 );

            Collection proteinSet_2 = null;
            String uniprotID_2 = null;

            // while( collection not empty )
            //     take the first interaction
            //     get 2 sets of proteins (interaction partner)
            //     get from the interaction set all interaction that have both interactor (remove them)
            //     run algo

            log( "\t" + uniprotID_1 + " has " + realInteractionP1.size() + " interaction(s)." );

            while ( !realInteractionP1.isEmpty() ) {

                Iterator iterator = realInteractionP1.iterator();
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

                    // if that interaction has not exactly 2 interactors, it is not taken into account
                    if( interaction.getComponents().size() != 2 ) {

                        // TODO find out if (getComponents().size() == 1 && stochiometry == 2) is valid too.

                        log( "\t\t Interaction has not exactly 2 interactors (" + interaction.getComponents().size() +
                             "), we don't export it." );
                        iterator.remove();

                    } else {

                        log( "\t\t Interaction has exactly 2 interactors." );

                        // now get the other protein ( other than uniprotID_1 )

                        /**
                         * Uniprot ID -> Collection(Protein) (ie. all species + splice variants)
                         *
                         * in the interaction, for each component, if the protein is part of the collection, take the protein
                         * attached to the other component. If still the same, that means that we have the same protein
                         * interacting as self. could be SV+P or different species though
                         */

                        // we know that interaction.getComponents().size() == 2 we assume that they carry only Protein
                        log( "\t\t Check what is the partner of " + uniprotID_1 );
                        Iterator iterator1 = interaction.getComponents().iterator();

                        Component component1 = (Component) iterator1.next();
                        Protein p1 = (Protein) component1.getInteractor();
                        log( "\t\t 1st Partner found: " + getUniprotID( p1 ) + " (" + p1.getBioSource().getShortLabel() + ")" );

                        Component component2 = (Component) iterator1.next();
                        Protein p2 = (Protein) component2.getInteractor();
                        log( "\t\t 2nd Partner found: " + getUniprotID( p2 ) + " (" + p2.getBioSource().getShortLabel() + ")" );

                        if( proteinSet_1.contains( p1 ) ) {

                            if( proteinSet_1.contains( p2 ) ) {

                                log( "\t\t\t That's the same protein !!" );
                                // same protein interacting together (could be different species though)
                                proteinSet_2 = proteinSet_1; // beware: same collection reference !!
                                uniprotID_2 = uniprotID_1;

                            } else {

                                // different proteins interacting together
                                // get the set of IntAct proteins
                                uniprotID_2 = getUniprotID( p2 );
                                proteinSet_2 = getProteinFromIntact( helper, uniprotID_2 );
                            }

                        } else {
                            // p2 must be in proteinSet_1
                            Protein tmp = p1;
                            p1 = p2;
                            p2 = tmp;

                            uniprotID_2 = getUniprotID( p2 );
                            proteinSet_2 = getProteinFromIntact( helper, uniprotID_2 );
                        }

                        System.out.print( "\t\t\tProtein set1 (" + uniprotID_1 + "): " );
                        for ( Iterator iterator2 = proteinSet_1.iterator(); iterator2.hasNext(); ) {
                            Protein protein = (Protein) iterator2.next();
                            System.out.print( protein.getShortLabel() + ", " );
                        }

                        System.out.print( "\n\t\t\tProtein set2: (" + uniprotID_2 + "): " );
                        for ( Iterator iterator2 = proteinSet_2.iterator(); iterator2.hasNext(); ) {
                            Protein protein = (Protein) iterator2.next();
                            System.out.print( protein.getShortLabel() );
                        }


                        // We don't take into account interactions that involve proteins not eligible for DR export.
                        if( !uniprotIDs.contains( uniprotID_2 ) ) {

                            log( "\n\t\t " + uniprotID_2 +
                                 " was not eligible for DR export, that interaction is not taken into account." );
                            iterator.remove();

                        } else {

                            log( "\n\t\t Look for interactions amongst " + uniprotID_1 + " and " + uniprotID_2 + "." );

                            // extract all interactions have partner in protein1 and proteins2
                            List potentiallyEligibleInteraction = new ArrayList( realInteractionP1.size() );
                            // add the current one
                            potentiallyEligibleInteraction.add( interaction );
                            iterator.remove();
                            log( "\t\t Add the current one: " + interaction.getShortLabel() + "(" + interaction.getAc() + ")" );

                            for ( Iterator iterator2 = realInteractionP1.iterator(); iterator2.hasNext(); ) {
                                Interaction interaction1 = (Interaction) iterator2.next();

                                Iterator iterator3 = interaction1.getComponents().iterator();
                                Component c1 = (Component) iterator3.next();
                                Component c2 = (Component) iterator3.next();

                                Protein p1_ = (Protein) c1.getInteractor();
                                Protein p2_ = (Protein) c2.getInteractor();

                                if( ( proteinSet_1.contains( p1_ ) && proteinSet_2.contains( p2_ ) )
                                    ||
                                    ( proteinSet_1.contains( p2_ ) && proteinSet_2.contains( p1_ ) ) ) {

                                    // select that interaction
                                    potentiallyEligibleInteraction.add( interaction1 );
                                    iterator2.remove();

                                    // TODO Check why we have duplicated interaction added here !!!

                                    log( "\t\t Add: " + interaction.getShortLabel() + "(" + interaction.getAc() + ")" );
                                }
                            } // selection of the interactions

                            log( "\t\t\t " + uniprotID_1 + " and " + uniprotID_2 + " have " + potentiallyEligibleInteraction.size() +
                                 " interaction(s) in common" );

                            // run the algo
                            Collection eligibleInteractions = getEligibleInteractions( potentiallyEligibleInteraction );

                            if( eligibleInteractions != null ) {

                                eligibleInteractionsCount += eligibleInteractions.size();
                                createCCLine( uniprotID_1, uniprotID_2, eligibleInteractions );

                            }

                        } // else (uniprotID2 is eligible for DR export)
                    } // else (interaction has exactly 2 interactor, hence is binary)
                } // else (interaction hasn't been processed yet)
            } // while there is interactions

            // write the CC content of protein still designated by index 'i' as its processing is finished.
            flushCCLine( uniprotID_1 );
        } // i (all eligible uniprot IDs)

        // flush and close output file
        if( null != writer ) {
            try {
                writer.close();
                System.out.println( "Output file closed." );
            } catch ( IOException e ) {
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

        Option drExportOpt = OptionBuilder.withArgName( "drExportFilename" )
                .hasArg()
                .withDescription( "DR export output file." )
                .create( "drExport" );
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
