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
import uk.ac.ebi.intact.util.Chrono;
import uk.ac.ebi.intact.util.MemoryMonitor;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * That class performs the export of the IntAct DR lines to UniProt. <br> We export all proteins that have been seen in
 * at least one high confidence interaction. <br> Only protein imported directly from UniProt are taken into account
 * <br> note: the proteins created via the editor should have an Annotation( CvTopic( no-uniprot-update ) )  attached to
 * it.
 * <p/>
 * <br> <br> Now I'll explain the details of how we decide whether or not an interaction is of high confidence.
 * <pre>
 * <p/>
 *   Here is a sketch of the algorithm for the DR lines ( I hope I will be clear enough ;) ):
 *      - let P be a Protein (and we are trying to determinate if it should be exported) imported from UniProtKB.
 *        note: in the case of splice variant (which are individual Protein in IntAct), if it is found to
 *              be eligible, it's parent is exported in the DR file instead. (eg. if P12345-1 were to be eligible,
 *              we would put P12345 as eligible without further checking).
 *      - let I be all non negative, binary interactions in which P interacts
 *        (negative interaction being defined as: it was demonstrated in this paper that these interactions
 *         DO NOT occur under the experimental conditions described.)
 *      - IntAct's experiment contain information about the method that was used to detect interactions, these
 *        methods are controlled vocabularies (namely CvInteraction). Each CvInteraction is annotated with
 *        uniprot-dr-export and a value in: yes, no, integer (that defines how many distinct evidence are required
 *        to make a record eligible for export).
 *      - for each interaction i in I:
 *           - get i's related experiment and check if it has an explicit export flag (can be yes, no or an arbitrary
 *             word). This flag is stored in an Annotation( CvTopic( uniprot-dr-export ) ).
 *              - if it is flagged 'yes', the related interaction becomes eligible for export too.
 *              - if it is flagged 'no', the related interaction is declared not eligible.
 *              - if it is an arbitrary word (eg. 'high'), then we check if the interaction has it too,
 *                if so, the interaction becomes eligible for export
 *              - if no flag is found, we check the experiment's method (CvInteraction, eg. y2h, ...) of the
 * experiment.
 *                Here again we have several cases,
 *                  - if the flag is 'yes', then the experiment (and its interaction) becomes eligible for export,
 *                  - if the flag is 'no', then the experiment (and its interaction) becomes NOT eligible for export,
 *                  - if the flag is a numerical value that describes how many distinct experiment using that method
 *                    should be found in order to get the interaction to be eligible.
 *                    eg. CvInteraction( two hybrid ) has a threshold of 2, let's say we have 2 experiments E1 and E2
 *                        having interaction list E1{I1, I2} and E2{I3}. We also know that P is interacting in I1 and
 * I3.
 *                        Now let's say we first look at I1, unfold the algorithm and end up checking on the
 * interaction
 *                        detection method in I1's respective experiments, Y2H having a threshold of 2, we need to
 * check
 *                        all interaction of P1 (ie. I1 and I3). If an other experiment's detection method has either
 *                        an explicit yes flag or if the count of the experiments having that method allow to reach
 *                        the defined threshold, then we declare the interaction I1 eligible.
 *                        In our example, the threshold is 2 and we have E1 and E2 having Y2H, so P becomes eligible.
 * <p/>
 *         So in a nustshell, if at least one of the interactions of P is declared eligible for export, the protein
 *         becomes itself eligible for DR export.
 * </pre>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class DRLineExport extends LineExport {

    /**
     * Define if a Protein object is link to at least one high confidence Interaction.
     *
     * @param protein the protein we are interrested in.
     *
     * @return true is the protein is plays a role in at least one high confidence interaction, otherwise false.
     */
    private final boolean isHighConfidence( final Protein protein ) {

        boolean isHighConfidence = false;

        String uniprotID = getUniprotID( protein );
        log( "\n\nChecking on Protein: " + uniprotID + "(" + protein.getAc() + ", " + protein.getShortLabel() + ") ..." );

        // getting all interactions in which that protein plays a role.
        List interactions = getInteractions( protein );
        log( interactions.size() + " interactions found." );

        for ( int i = 0; i < interactions.size() && isHighConfidence == false; i++ ) {

            Interaction interaction = (Interaction) interactions.get( i );

            log( "  (" + ( i + 1 ) + ") Interaction: Shortlabel:" + interaction.getShortLabel() + "  AC: " + interaction.getAc() );

            if ( isNegative( interaction ) ) {

                log( "\t That interaction or at least one of its experiments is negative, skip it." );
                continue; // skip that interaction
            }

            Collection experiments = interaction.getExperiments();

            int expCount = experiments.size();
            log( "\t interaction related to " + expCount + " experiment" + ( expCount > 1 ? "s" : "" ) + "." );

            // TODO add shortcut here on && isHighConfidence == false as we stop as soon as we find a high confidence.
            for ( Iterator iterator2 = experiments.iterator(); iterator2.hasNext() && isHighConfidence == false; ) {
                Experiment experiment = (Experiment) iterator2.next();

                log( "\t Experiment: Shortlabel:" + experiment.getShortLabel() + "  AC: " + experiment.getAc() );

                ExperimentStatus experimentStatus = getExperimentExportStatus( experiment, "\t\t" );
                if ( experimentStatus.doNotExport() ) {

                    // forbid export for all interactions of that experiment (and their proteins).
                    log( "\t\t No interactions of that experiment will be exported: protein non exportable" );

                } else if ( experimentStatus.doExport() ) {

                    log( "\t\t All interaction of that experiment will be exported." );
                    isHighConfidence = true;

                } else if ( experimentStatus.isLargeScale() ) {

                    // if my interaction has one of those keywords as annotation for DR line export, do export.
                    Collection keywords = experimentStatus.getKeywords();
                    Collection annotations = interaction.getAnnotations();
                    boolean annotationFound = false;

                    // We assume here that an interaction has only one Annotation( uniprot-dr-export ).
                    for ( Iterator iterator3 = annotations.iterator(); iterator3.hasNext() && !annotationFound; ) {
                        final Annotation annotation = (Annotation) iterator3.next();

                        if ( authorConfidenceTopic.equals( annotation.getCvTopic() ) ) {
                            String text = annotation.getAnnotationText();

                            log( "\t Interaction has " + authorConfidenceTopic.getShortLabel() + ": '" + text + "'" );

                            if ( text != null ) {
                                text = text.trim();
                            }

                            for ( Iterator iterator4 = keywords.iterator(); iterator4.hasNext() && !annotationFound; ) {
                                String kw = (String) iterator4.next();
                                // NOT case sensitive

                                log( "\t\t Compare it with '" + kw + "'" );

                                if ( kw.equalsIgnoreCase( text ) ) {
                                    annotationFound = true;
                                    log( "\t\t\t Equals !" );
                                }
                            }
                        }
                    }

                    if ( annotationFound ) {

                        /*
                        * We don't need to check an eventual threshold on the method level because
                        * in the current state, the annotation is on the experiment level that is
                        * lower and hence is dominant on the method's one.
                        */
                        log( "\t that interaction is eligible for export in the context of a large scale experiment" );
                        isHighConfidence = true;

                    } else {

                        log( "\t interaction not eligible: protein non exportable" );
                    }

                } else if ( experimentStatus.isNotSpecified() ) {

                    log( "\t\t No experiment status, check the experimental method." );

                    // Then check the experimental method (CvInteraction)
                    // Nothing specified at the experiment level, check for the method (CvInteraction)
                    CvInteraction cvInteraction = experiment.getCvInteraction();

                    if ( null == cvInteraction ) {
                        // we need to check because cvInteraction is not mandatory in an experiment.

                    } else {

                        CvInteractionStatus methodStatus = getMethodExportStatus( cvInteraction, "" );

                        if ( methodStatus.doExport() ) {

                            // do nothing
                            isHighConfidence = true;

                        } else if ( methodStatus.doNotExport() ) {

                            log( "\t\t Method specified as non exportable" );

                        } else if ( methodStatus.isNotSpecified() ) {

                            // we should never get in here but just in case...
                            System.out.println( "\t\t No Method specified: protein non exportable" );

                        } else if ( methodStatus.isConditionalExport() ) {

                            log( "\t\t As conditional export, check the count of distinct experiment for that method." );

                            // if the threshold is not reached, iterates over all available interactions to check if
                            // there is (are) one (many) that could allow to reach the threshold.

                            int threshold = methodStatus.getMinimumOccurence();

                            // we create a non redondant set of experiment identifier
                            // TODO couldn't that be a static collection that we empty regularly ?
                            Set experimentAcs = new HashSet( threshold );

                            // check if there are other experiments attached to the current interaction that validate it.
                            boolean enoughExperimentFound = false;
                            for ( Iterator iterator4 = experiments.iterator(); iterator4.hasNext(); ) {
                                Experiment experiment1 = (Experiment) iterator4.next();

                                log( "\t\t Experiment: Shortlabel:" + experiment1.getShortLabel() + "  AC: " + experiment1.getAc() );

                                CvInteraction method = experiment1.getCvInteraction();

                                if ( cvInteraction.equals( method ) &&
                                     false == experimentAcs.contains( experiment1.getAc() ) ) {
                                    experimentAcs.add( experiment1.getAc() );

                                    // we only update if we found one
                                    enoughExperimentFound = ( experimentAcs.size() >= threshold );
                                    log( "\t\t\t That experiment add an other evidence with that same CvInteraction( " + cvInteraction.getShortLabel() + " )." );

                                } else {

                                    log( "\t\t\t That experiment doesn't consolidate what we have, discard it." );
                                }
                            }

                            log( "\t Looking for other interactions that support that method in other experiments..." );

                            for ( int j = 0; j < interactions.size() && !enoughExperimentFound; j++ ) {

                                if ( i == j ) {
                                    // don't re-process the current interaction, we are interrested in the others.
                                    continue;
                                }

                                // Have that conditionalMethods at the interaction scope.
                                //
                                // for a interaction
                                //      for each experiment e
                                //          if e.CvInteraction <> cvInteraction -> continue
                                //          else is experiment already processed ? if no, add and check the count >= threashold.
                                //                                                 if reached, stop, esle carry on.
                                //

                                Interaction interaction2 = (Interaction) interactions.get( j );

                                log( "\t\t Interaction: Shortlabel:" + interaction2.getShortLabel() + "  AC: " + interaction2.getAc() );

                                Collection experiments2 = interaction2.getExperiments();

                                for ( Iterator iterator5 = experiments2.iterator(); iterator5.hasNext() && !enoughExperimentFound; )
                                {
                                    Experiment experiment2 = (Experiment) iterator5.next();
                                    log( "\t\t\t Experiment: Shortlabel:" + experiment2.getShortLabel() + "  AC: " + experiment2.getAc() );

                                    CvInteraction method = experiment2.getCvInteraction();


                                    if ( cvInteraction.equals( method ) &&
                                         false == experimentAcs.contains( experiment2.getAc() ) ) {
                                        experimentAcs.add( experiment2.getAc() );

                                        // we only update if we found one
                                        enoughExperimentFound = ( experimentAcs.size() >= threshold );
                                        log( "\t\t\t\t That experiment add an other evidence with that same CvInteraction( " + cvInteraction.getShortLabel() + " )." );

                                    } else {

                                        log( "\t\t\t\t That experiment doesn't consolidate what we have, discard it." );
                                    }

                                } // j's experiments

                                log( "\t\t\t " + cvInteraction.getShortLabel() + ", threshold: " +
                                     threshold + " #experiment: " +
                                     ( experimentAcs == null ? "none" : "" + experimentAcs.size() ) );
                            } // for( j in interactions )

                            if ( enoughExperimentFound ) {
                                log( "\t\t\t Enough experiment found." );
                                isHighConfidence = true;
                            } else {
                                log( "\t\t\t Not enough experiment found: protein non exportable." );
                            }

                        } // conditional status
                    } // cvInteraction != null
                } // experiment status not specified
            } // i's experiments
        } // interactions

        return isHighConfidence;
    }

    /**
     * Implement the set of rules that check if a Protein are eligible for export.
     *
     * @param protein The protein to be checked for eligibility to export in Swiss-Prot.
     * @param master  if protein is a splice variant, master is its master protein.
     *
     * @return the ID to be exported to Swiss-Prot or null if it has not to be exported.
     */
    public final String getProteinExportStatus( Protein protein, Protein master ) {

        String selectedUniprotID = null;

        //map: method  ===>  count of distinct experiment in which the protein has been seen
        HashMap conditionalMethods = new HashMap();

        String uniprotID = getUniprotID( protein );
        log( "\n\n" + uniprotID + " Shortlabel:" + protein.getShortLabel() + "  AC: " + protein.getAc() );

        String masterUniprotID = null;
        if ( null != master ) {
            masterUniprotID = getUniprotID( master );
            log( "\n\nhaving master protein " + masterUniprotID + " Shortlabel:" + master.getShortLabel() + "  AC: " + master.getAc() );
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
        log( "\t related to " + interactions.size() + " interactions." );

        boolean export = false;
        boolean stop = false;

        for ( Iterator iterator1 = interactions.iterator(); iterator1.hasNext() && !stop; ) {
            Interaction interaction = (Interaction) iterator1.next();

            log( "\t Interaction: Shortlabel:" + interaction.getShortLabel() + "  AC: " + interaction.getAc() );

            // if that interaction is flagged as negative, we don't take it into account
            if ( isNegative( interaction ) ) {
                log( "\t\t Interaction is flagged as negative, we don't take it into account." );
                continue; // loop to the next interaction
            } else {
                log( "\t\t Interaction is NOT flagged as negative." );
            }

            // if that interaction has not exactly 2 interactors, it is not taken into account
            if ( !isBinary( interaction ) ) {

                log( "\t\t Interaction has not exactly 2 interactors (" + interaction.getComponents().size() +
                     "), we don't take it into account." );
                continue; // loop to the next interaction

            } else {
                log( "\t\t Interaction is binary." );
            }

            Collection experiments = interaction.getExperiments();

            int count = experiments.size();
            log( "\t\t interaction related to " + count + " experiment" + ( count > 1 ? "s" : "" ) + "." );

            for ( Iterator iterator2 = experiments.iterator(); iterator2.hasNext() && !stop; ) {
                Experiment experiment = (Experiment) iterator2.next();

                log( "\t\t\t Experiment: Shortlabel:" + experiment.getShortLabel() + "  AC: " + experiment.getAc() );

                // if that experiment is flagged as negative, we don't take it into account
                if ( isNegative( experiment ) ) {
                    log( "\t\t\t\t Experiment is flagged as negative, we don't take it into account." );
                    continue; // loop to the next experiment
                } else {
                    log( "\t\t\t\t Experiment is NOT flagged as negative." );
                }

                ExperimentStatus experimentStatus = getExperimentExportStatus( experiment, "" );
                if ( experimentStatus.doNotExport() ) {

                    // forbid export for all interactions of that experiment (and their proteins).

                    log( "\t\t\t\t No interactions of that experiment will be exported." );
                    export = false;
                    stop = true;

                    continue;  // go to next experiment

                } else if ( experimentStatus.doExport() ) {

                    // Authorise export for all interactions of that experiment (and their proteins),
                    // This overwrite the setting of the CvInteraction concerning the export.

                    log( "\t\t\t\t All interaction of that experiment will be exported." );
                    export = true;
                    stop = true;

                    continue; // go to next experiment

                } else if ( experimentStatus.isLargeScale() ) {

                    // if my interaction has one of those keywords as annotation for DR line export, do export.
                    Collection keywords = experimentStatus.getKeywords();
                    Collection annotations = interaction.getAnnotations();
                    boolean found = false;

                    // We assume here that an interaction has only one Annotation( uniprot-dr-export ).
                    for ( Iterator iterator3 = annotations.iterator(); iterator3.hasNext() && !found; ) {
                        final Annotation annotation = (Annotation) iterator3.next();

                        if ( authorConfidenceTopic.equals( annotation.getCvTopic() ) ) {
                            String text = annotation.getAnnotationText();


                            log( "\t\t\t\t Interaction has " + authorConfidenceTopic.getShortLabel() +
                                 ": '" + text + "'" );

                            if ( text != null ) {
                                text = text.trim();
                            }

                            for ( Iterator iterator4 = keywords.iterator(); iterator4.hasNext() && !found; ) {
                                String kw = (String) iterator4.next();
                                // NOT case sensitive

                                log( "\t\t\t\t\t Compare it with '" + kw + "'" );

                                if ( kw.equalsIgnoreCase( text ) ) {
                                    found = true;
                                    log( "\t\t\t\t\t Equals !" );
                                }
                            }
                        }
                    }

                    if ( found ) {

                        /*
                        * We don't need to check an eventual threshold on the method level because
                        * in the current state, the annotation is on the experiment level that is
                        * lower and hence is dominant on the method's one.
                        */

                        export = true;
                        stop = true;

                        log( "\t\t\t that interaction is eligible for export in the context of a large scale experiment" );

                    } else {

                        log( "\t\t\t interaction not eligible" );
                    }

                } else if ( experimentStatus.isNotSpecified() ) {

                    log( "\t\t\t No experiment status, check the experimental method." );

                    // Then check the experimental method (CvInteraction)
                    // Nothing specified at the experiment level, check for the method (CvInteraction)
                    CvInteraction cvInteraction = experiment.getCvInteraction();
                    if ( null == cvInteraction ) {
                        // we need to check because cvInteraction is not mandatory in an experiment.
                        continue; // skip it, go to next experiment
                    }

                    CvInteractionStatus methodStatus = getMethodExportStatus( cvInteraction, "" );

                    if ( methodStatus.doExport() ) {

                        export = true;
                        stop = true;

                    } else if ( methodStatus.doNotExport() ) {

                        export = false;

                    } else if ( methodStatus.isNotSpecified() ) {

                        // we should never get in here but just in case...
                        export = false;

                    } else if ( methodStatus.isConditionalExport() ) {

                        log( "\t\t\t As conditional export, check the count of distinct experiment for that method." );

                        // non redundant set of experiment AC.
                        HashSet experimentAcs = (HashSet) conditionalMethods.get( cvInteraction );
                        int threshold = methodStatus.getMinimumOccurence();

                        if ( null == experimentAcs ) {
                            // at most we will need to store $threshold Experiments.
                            experimentAcs = new HashSet( threshold );

                            // stores it back in the collection ... needs to be done only once ! We are using reference ;o)
                            conditionalMethods.put( cvInteraction, experimentAcs );
                            log( "\t\t\t Created a container for experiment ID for that method" );
                        }

                        // add the experiment ID
                        experimentAcs.add( experiment ); // we could store here the hasCode instead !!!

                        if ( experimentAcs.size() == threshold ) {
                            // We reached the threshold, export allowed !
                            log( "\t\t\t Count of distinct experiment reached for that method" );
                            export = true;
                            stop = true;
                        }

                        log( "\t\t\t " + cvInteraction.getShortLabel() + ", threshold: " +
                             threshold + " #experiment: " +
                             ( experimentAcs == null ? "none" : "" + experimentAcs.size() ) );
                    } // conditional status
                } // experiment status not specified
            } // experiments
        } // interactions

        if ( export ) {
            // That protein is eligible for export.
            // The ID will be still unique even if it already exists.

            if ( null != masterUniprotID ) {
                log( "As the protein is a splice variant, we export the Swiss-Prot AC of its master." );
                selectedUniprotID = masterUniprotID;
            } else {
                selectedUniprotID = uniprotID;
            }

            log( "Protein exported to Swiss-Prot (AC: " + selectedUniprotID + ")" );

        } else {

            log( "Protein NOT exported to Swiss-Prot" );
        }

        return selectedUniprotID;
    }

    /**
     * Get a distinct set of Uniprot ID of the protein eligible to export in Swiss-Prot.
     *
     * @param helper access to the database
     *
     * @return a distinct set of Uniprot ID of the protein eligible to export in Swiss-Prot.
     *
     * @throws java.sql.SQLException error when handling the JDBC connection or query.
     * @throws uk.ac.ebi.intact.business.IntactException
     *
     * @throws uk.ac.ebi.intact.util.uniprotExport.CCLineExport.DatabaseContentException
     *                               if the initialisation process failed (CV not found)
     */
    public final Set getElibibleProteins( IntactHelper helper )
            throws SQLException,
                   IntactException,
                   DatabaseContentException {

        Connection connection = helper.getJDBCConnection();
        Statement statement = connection.createStatement();

        // select the protein ordered by Uniprot identity.
        // TODO update that query so that it uses the MI references of uniprot and identity.
        String sql = "SELECT distinct P.ac, x.primaryId\n" +
                     "FROM   ia_interactor P, ia_component C, ia_xref x, ia_controlledvocab q, ia_controlledvocab db\n" +
                     "WHERE  P.objclass like '%Protein%' and\n" +
                     "       P.ac IN C.interactor_ac and\n" +
                     "       p.ac = x.parent_ac and\n" +
                     "       x.database_ac = db.ac and\n" +
                     "       x.qualifier_ac = q.ac and\n" +
                     "       db.shortlabel = '" + CvDatabase.UNIPROT + "' and\n" +
                     "       q.shortlabel = '" + CvXrefQualifier.IDENTITY + "' and\n" +

                     // filtering TREMBL entries: AAF.., BAA..., ...
                     "       x.primaryId not like 'A%' and \n" +
                     "       x.primaryId not like 'B%' and \n" +
                     "       x.primaryId not like 'C%'\n" +
                     "ORDER BY x.primaryId ";

        System.out.println( "Executing the following query: " );
        System.out.println( sql );
        ResultSet proteinAcs = statement.executeQuery( sql );
        Runtime.getRuntime().addShutdownHook( new CCLineExport.DatabaseConnexionShutdownHook( helper ) );

        // fetch necessary vocabulary
        init( helper );

        Set proteinEligible = new HashSet( 4096 );
        Chrono globalChrono = new Chrono();
        globalChrono.start();
        Collection proteins = null;
        int proteinCount = 0;

        // Process the proteins one by one.
        while ( proteinAcs.next() ) {

            proteinCount++;

            if ( ( proteinCount % 100 ) == 0 ) {
                System.out.print( "..." + proteinCount );

                if ( ( proteinCount % 1000 ) == 0 ) {
                    System.out.println( "" );
                } else {
                    System.out.flush();
                }
            }

            String ac = proteinAcs.getString( 1 );
            proteins = helper.search( Protein.class.getName(), "ac", ac );

            if ( proteins == null || proteins.isEmpty() ) {
                System.err.println( "Could not find a Protein in IntAct for AC: " + ac );
                continue; // process next AC
            }

            // only used in case the current protein is a splice variant
            Protein master = null;

            Protein protein = (Protein) proteins.iterator().next();

            // Skip proteins annotated no-uniprot-update
            if ( false == needsUniprotUpdate( protein ) ) {
                log( protein.getAc() + " " + protein.getShortLabel() + " is not from UniProt, skip it." );
                continue; // process next AC
            }

            String uniprotId = null;

            if ( isHighConfidence( protein ) ) {

                log( "Wasn't low confidence ... export it ;o)" );

                // means a protein is low-confidence if it has at least one low-confidence interaction.

                // if this is a splice variant, we try to get its master protein
                if ( protein.getShortLabel().indexOf( '-' ) != -1 ) {

                    String masterAc = getMasterAc( protein );

                    if ( masterAc == null ) {

                        System.err.println( "The splice variant having the AC(" + protein.getAc() + ") doesn't have it's master AC." );

                    } else {

                        Collection c = null;
                        try {
                            c = helper.search( Protein.class, "ac", masterAc );
                        } catch ( IntactException e ) {
                            e.printStackTrace();
                        }

                        if ( c == null || c.size() == 0 ) {
                            System.err.println( "Could not find the master protein of splice variant (" +
                                                protein.getAc() + ") having the AC(" + ac + ")" );
                        } else {
                            // it must be one only
                            master = (Protein) c.iterator().next();

                            // check that the master hasn't been processed already
                            uniprotId = getUniprotID( master );
                        }
                    }
                } else {

                    uniprotId = getUniprotID( protein );
                }
            }

            if ( uniprotId != null && !proteinEligible.contains( uniprotId ) ) {
                log( "Exporting UniProt( " + uniprotId + " )" );
                proteinEligible.add( uniprotId );
            }

            int count = proteinEligible.size();
            float percentage = ( (float) count / (float) proteinCount ) * 100;
            log( count + " protein" + ( count > 1 ? "s" : "" ) +
                 " eligible for export out of " + proteinCount +
                 " processed (" + percentage + "%)." );

        } // all proteins

        try {
            statement.close();
            proteinAcs.close();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }

        globalChrono.stop();
        System.out.println( "Total time elapsed: " + globalChrono );

        return proteinEligible;
    }

    public static String formatProtein( String uniprotID ) {
        StringBuffer sb = new StringBuffer();

        sb.append( uniprotID ).append( '\t' );
        sb.append( "IntAct" ).append( '\t' );
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
        formatter.printHelp( "DRLineExport [-file <filename>] [-debug] [-debugFile]", options );
    }

    public static void main( String[] args ) throws IntactException, SQLException, LookupException,
                                                    DatabaseContentException {

        MemoryMonitor memoryMonitor = new MemoryMonitor();

        // create Option objects
        Option helpOpt = new Option( "help", "print this message" );

        Option drExportOpt = OptionBuilder.withArgName( "drExportFilename" ).hasArg().withDescription( "DR export output filename." ).create( "drExport" );

        Option debugOpt = OptionBuilder.withDescription( "Shows verbose output." ).create( "debug" );
        debugOpt.setRequired( false );

        Option debugFileOpt = OptionBuilder.withDescription( "Store verbose output in the specified file." ).create( "debugFile" );
        debugFileOpt.setRequired( false );

        Options options = new Options();

        options.addOption( drExportOpt );
        options.addOption( helpOpt );
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

        if ( line.hasOption( "help" ) ) {
            displayUsage( options );
            System.exit( 0 );
        }

        DRLineExport exporter = new DRLineExport();

        boolean debugEnabled = line.hasOption( "debug" );
        boolean debugFileEnabled = line.hasOption( "debugFile" );
        exporter.setDebugEnabled( debugEnabled );
        exporter.setDebugFileEnabled( debugFileEnabled );

        boolean filenameGiven = line.hasOption( "drExport" );
        String filename = null;
        if ( filenameGiven == true ) {
            filename = line.getOptionValue( "drExport" );
        }

        // Prepare CC output file.
        File file = null;
        if ( filename != null ) {
            try {
                file = new File( filename );
                if ( file.exists() ) {
                    System.err.println( "Please give a new file name for the DR output file: " + file.getAbsoluteFile() );
                    System.err.println( "We will use the default filename instead (instead of overwritting the existing file)." );
                    filename = null;
                    file = null;
                }
            } catch ( Exception e ) {
                // nothing, the default filename will be given
            }
        }

        if ( filename == null || file == null ) {
            filename = "DRLineExport_" + TIME + ".txt";
            System.out.println( "Using default filename for the DR export: " + filename );
            file = new File( filename );
        }

        System.out.println( "DR export will be saved in: " + filename );


        IntactHelper helper = new IntactHelper();
        System.out.println( "Database instance: " + helper.getDbName() );
        System.out.println( "User: " + helper.getDbUserName() );

        // get the set of Uniprot ID to be exported to Swiss-Prot
        Set proteinEligible = exporter.getElibibleProteins( helper );

        System.out.println( proteinEligible.size() + " protein(s) selected for export." );

        // save it to a file.

        BufferedWriter out = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter( file );
            out = new BufferedWriter( fw );
            writeToFile( proteinEligible, out );
            out.flush();

        } catch ( IOException e ) {
            e.printStackTrace();
            System.err.println( "Could not save the result to :" + filename );
            System.err.println( "Displays the result on STDOUT:\n\n\n" );

            display( proteinEligible );
            System.exit( 1 );

        } finally {
            if ( out != null ) {
                try {
                    out.close();
                } catch ( IOException e ) {
                    System.exit( 1 );
                }
            }

            if ( fw != null ) {
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