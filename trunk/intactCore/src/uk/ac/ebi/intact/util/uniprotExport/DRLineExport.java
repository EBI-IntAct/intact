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

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * That class .
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class DRLineExport extends LineExport {

    /**
     * Implement the set of rules that check if a Protein are eligible for export.
     *
     * @param protein The protein to be checked for eligibility to export in Swiss-Prot.
     * @param master  if protein is a splice variant, master is its master protein.
     * @return the ID to be exported to Swiss-Prot or null if it has not to be exported.
     */
    public final String getProteinExportStatus( Protein protein, Protein master ) {

        String selectedUniprotID = null;

        //map: method  ===>  count of distinct experiment in which the protein has been seen
        HashMap conditionalMethods = new HashMap();

        String uniprotID = getUniprotID( protein );
        log( "\n\n" + uniprotID + " Shortlabel:" + protein.getShortLabel() + "  AC: " + protein.getAc() );

        String masterUniprotID = null;
        if( null != master ) {
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
            if( isNegative( interaction ) ) {
                log( "\t\t Interaction is flagged as negative, we don't take it into account." );
                continue; // loop to the next interaction
            } else {
                log( "\t\t Interaction is NOT flagged as negative." );
            }

            // if that interaction has not exactly 2 interactors, it is not taken into account
            if( !isBinary( interaction ) ) {

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
                if( isNegative( experiment ) ) {
                    log( "\t\t\t\t Experiment is flagged as negative, we don't take it into account." );
                    continue; // loop to the next experiment
                } else {
                    log( "\t\t\t\t Experiment is NOT flagged as negative." );
                }

                ExperimentStatus experimentStatus = getExperimentExportStatus( experiment, "" );
                if( experimentStatus.doNotExport() ) {

                    // forbid export for all interactions of that experiment (and their proteins).

                    log( "\t\t\t\t No interactions of that experiment will be exported." );
                    export = false;
                    stop = true;

                    continue;  // go to next experiment

                } else if( experimentStatus.doExport() ) {

                    // Authorise export for all interactions of that experiment (and their proteins),
                    // This overwrite the setting of the CvInteraction concerning the export.

                    log( "\t\t\t\t All interaction of that experiment will be exported." );
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


                            log( "\t\t\t\t Interaction has " + authorConfidenceTopic.getShortLabel() +
                                 ": '" + text + "'" );

                            if( text != null ) {
                                text = text.trim();
                            }

                            for ( Iterator iterator4 = keywords.iterator(); iterator4.hasNext() && !found; ) {
                                String kw = (String) iterator4.next();
                                // NOT case sensitive

                                log( "\t\t\t\t\t Compare it with '" + kw + "'" );

                                if( kw.equalsIgnoreCase( text ) ) {
                                    found = true;
                                    log( "\t\t\t\t\t Equals !" );
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

                        log( "\t\t\t that interaction is eligible for export in the context of a large scale experiment" );

                    } else {

                        log( "\t\t\t interaction not eligible" );
                    }

                } else if( experimentStatus.isNotSpecified() ) {

                    log( "\t\t\t No experiment status, check the experimental method." );

                    // Then check the experimental method (CvInteraction)
                    // Nothing specified at the experiment level, check for the method (CvInteraction)
                    CvInteraction cvInteraction = experiment.getCvInteraction();
                    if( null == cvInteraction ) {
                        // we need to check because cvInteraction is not mandatory in an experiment.
                        continue; // skip it, go to next experiment
                    }

                    CvInteractionStatus methodStatus = getMethodExportStatus( cvInteraction, "" );

                    if( methodStatus.doExport() ) {

                        export = true;
                        stop = true;

                    } else if( methodStatus.doNotExport() ) {

                        export = false;

                    } else if( methodStatus.isNotSpecified() ) {

                        // we should never get in here but just in case...
                        export = false;

                    } else if( methodStatus.isConditionalExport() ) {

                        log( "\t\t\t As conditional export, check the count of distinct experiment for that method." );

                        // non redundant set of experiment AC.
                        HashSet experimentAcs = (HashSet) conditionalMethods.get( cvInteraction );
                        int threshold = methodStatus.getMinimumOccurence();

                        if( null == experimentAcs ) {
                            // at most we will need to store $threshold Experiments.
                            experimentAcs = new HashSet( threshold );

                            // stores it back in the collection ... needs to be done only once ! We are using reference ;o)
                            conditionalMethods.put( cvInteraction, experimentAcs );
                            log( "\t\t\t Created a container for experiment ID for that method" );
                        }

                        // add the experiment ID
                        experimentAcs.add( experiment ); // we could store here the hasCode instead !!!

                        if( experimentAcs.size() == threshold ) {
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

        if( export ) {
            // That protein is eligible for export.
            // The ID will be still unique even if it already exists.

            if( null != masterUniprotID ) {
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
     * @return a distinct set of Uniprot ID of the protein eligible to export in Swiss-Prot.
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


//        String oldSql = "SELECT ac " +
//                         "FROM IA_INTERACTOR " +
//                         "WHERE objclass like '%Protein%' ";

        String sql = "SELECT distinct P.ac " +
                     "FROM   ia_interactor P, ia_component C " +
                     "WHERE  P.objclass like '%Protein%' and " +
                     "       P.ac IN C.interactor_ac";

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

            if( ( proteinCount % 100 ) == 0 ) {
                System.out.print( "..." + proteinCount );

                if( ( proteinCount % 1000 ) == 0 ) {
                    System.out.println( "" );
                } else {
                    System.out.flush();
                }
            }

            String ac = proteinAcs.getString( 1 );
            proteins = helper.search( Protein.class.getName(), "ac", ac );

            if( proteins == null || proteins.isEmpty() ) {
                System.err.println( "Could not find a Protein in IntAct for AC: " + ac );
                continue; // process next AC
            }

            // only used in case the current protein is a splice variant
            Protein master = null;

            Protein protein = (Protein) proteins.iterator().next();

            String uniprotId = null;

            // if this is a splice variant, we try to get its master protein
            if( protein.getShortLabel().indexOf( '-' ) != -1 ) {

                String masterAc = getMasterAc( protein );

                if( masterAc == null ) {

                    System.err.println( "The splice variant having the AC(" + protein.getAc() + ") doesn't have it's master AC." );

                } else {

                    Collection c = null;
                    try {
                        c = helper.search( Protein.class.getName(), "ac", masterAc );
                    } catch ( IntactException e ) {
                        e.printStackTrace();
                    }

                    if( c == null || c.size() == 0 ) {
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

            if( uniprotId != null && !proteinEligible.contains( uniprotId ) ) {

                proteinEligible.add( uniprotId );

                int count = proteinEligible.size();
                float percentage = ( (float) count / (float) proteinCount ) * 100;
                log( count + " protein" + ( count > 1 ? "s" : "" ) +
                     " eligible for export out of " + proteinCount +
                     " processed (" + percentage + "%)." );
            }
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

        // create Option objects
        Option helpOpt = new Option( "help", "print this message" );

        Option drExportOpt = OptionBuilder.withArgName( "drExportFilename" ).hasArg().withDescription( "DR export output filename." ).create( "file" );

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

        if( line.hasOption( "help" ) ) {
            displayUsage( options );
            System.exit( 0 );
        }

        DRLineExport exporter = new DRLineExport();

        boolean debugEnabled = line.hasOption( "debug" );
        boolean debugFileEnabled = line.hasOption( "debugFile" );
        exporter.setDebugEnabled( debugEnabled );
        exporter.setDebugFileEnabled( debugFileEnabled );

        boolean filenameGiven = line.hasOption( "file" );
        String filename = null;
        if( filenameGiven ) {
            filename = line.getOptionValue( "file" );
        } else {
            filename = "DRLineExport_" + TIME + ".txt";
        }

        System.out.println( "DR export will be saved in: " + filename );


        IntactHelper helper = new IntactHelper();
        System.out.println( "Database instance: " + helper.getDbName() );
        System.out.println( "User: " + helper.getDbUserName() );

        // get the set of Uniprot ID to be exported to Swiss-Prot
        Set proteinEligible = exporter.getElibibleProteins( helper );

        System.out.println( proteinEligible.size() + " protein(s) selected for export." );

        // save it to a file.
        File file = new File( filename );
        System.out.println( "Try to save to: " + file.getAbsolutePath() );
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