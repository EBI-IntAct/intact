/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion;

import org.apache.commons.cli.*;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.File;
import java.io.Writer;
import java.io.IOException;
import java.io.FileWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * TODO comment this
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28-Jul-2005</pre>
 */
public class ExperimentListGenerator {

    // if an experiment has more than this many interactions it is considered to be large scale.
    //NB changed for testing - usually 100
    public static final int SMALLSCALELIMIT = 500;
    public static final int LARGESCALESIZE = 2500;
    public static final int MAX_EXPERIMENT_PER_FILE = 300;

    public static final String NEW_LINE = System.getProperty( "line.separator" );

    // in the end the sum of small scale interaction coun should be under LARGESCALESIZE, otherwise, split.

    /**
     * Holds the shortLabels of any Experiments found to contain Interactions with 'negative' information. It has to be
     * a static because the method used for writing the classifications is a static...
     */
    private static List negExpLabels = new ArrayList();

    /**
     * Classify experiments matching searchPattern into a data structure according to species and experiment size.
     *
     * @param searchPattern
     *
     * @return HashMap of HashMap of ArrayLists of Experiments: {species}{scale}[n]
     *
     * @throws uk.ac.ebi.intact.business.IntactException
     *
     */
    public static HashMap classifyExperiments( String searchPattern ) throws IntactException {
        IntactHelper helper = new IntactHelper();

        //obtain data, probably experiment by experiment, build
        //PSI data for it then write it to a file....
        FileGenerator generator = new FileGenerator( helper );
        Collection searchResults = generator.getDbData( searchPattern );

        HashMap allExp = new HashMap();

        // Split the list of experiments into species- and size-specific files
        for ( Iterator iterator = searchResults.iterator(); iterator.hasNext(); ) {
            Experiment exp = (Experiment) iterator.next();

            // Get the species of one of the interactors of the experiment.
            // The bioSource of the Experiment is irrelevant, as it may be an
            // auxiliary experimental system.
            BioSource source = null;
            int size = 0;
            try {

                Collection interactions = exp.getInteractions();
                size = interactions.size();
                System.out.println( "Classifying " + exp.getShortLabel() + " ("+ size +" interaction"+( size>1 ? "s" : "" )+")");

                //THIS is where OJB loads ALL the Interactions - a call to Iterator..
                Interaction interaction = (Interaction) interactions.iterator().next();
                Collection components = interaction.getComponents();
                Interactor protein = ( (Component) components.iterator().next() ).getInteractor();
                source = protein.getBioSource();

                size = interactions.size();
            } catch ( Exception e ) {
                /* If anything goes wrong in determining the source,
                experiments without interactions etc are the most likely cause.
                Just ignore these in the classification and therefore in the output.
                */
                continue;
            }

            // fully initialize the data structure for a new BioSource
            if ( null == allExp.get( source ) ) {
                HashMap speciesMap = new HashMap();
                speciesMap.put( "small", new ArrayList() );
                speciesMap.put( "large", new ArrayList() );
                allExp.put( source, speciesMap );
            }

            // Sort experiment into appropriate bin
            if ( size > SMALLSCALELIMIT ) {
                ( (ArrayList) ( (HashMap) allExp.get( source ) ).get( "large" ) ).add( exp );
            } else {
                ( (ArrayList) ( (HashMap) allExp.get( source ) ).get( "small" ) ).add( exp );
            }
        }

        // Now all experiments have been sorted into allExp - check for those containing
        //'negative' results...
        classifyNegatives();
        return allExp;
    }

    //----------------------- private helper methods -------------------------------------

    /**
     * Checks for a negative interaction. NB This will have to be done using SQL otherwise we end up materializing all
     * interactions just to do the check.
     * <p/>
     * Also the new intact curation rules specify that an Experiment should ONLY contain negative Interactions if it is
     * annotated as 'negative'. Thus to decide if an Experiment is classified as 'negative', the Annotations of that
     * Experiment need to be checked for one with a 'negative' Controlled Vocab attached to it as a topic. </p>
     * <p/>
     * However at some point in the future there may be a possibility that only the Interactions will be marked as
     * 'negative' (not the Experiment), and so these should be checked also, with duplicate matches being ignored. </p>
     * This method has to be static because it is called by the static 'classifyExperiments'.
     */
    private static void classifyNegatives() throws IntactException {

        //query to get at the Experiment ACs containing negative interaction annotations
        String sql = "select experiment_ac from ia_int2exp where interaction_ac in " +
                     "(select interactor_ac from ia_int2annot where annotation_ac in " +
                     "(select ac from ia_annotation where topic_ac in " +
                     "(select ac from ia_controlledvocab where shortlabel='negative')))";

        //query to obtain Experiment ACs by searching for an Annotation for the
        //Experiment classified as 'negative' itself
        String expSql = "select experiment_ac from ia_exp2annot where annotation_ac in " +
                        "(select ac from ia_annotation where topic_ac in " +
                        "(select ac from ia_controlledvocab where shortlabel='negative'))";

        IntactHelper helper = new IntactHelper();
        Set expAcs = new HashSet(); //used to collect ACs from a query - Set avoids duplicates

        Connection conn = null;
        Statement stmt = null;  //ordinary Statement will do - won't be reused
        PreparedStatement labelStmt = null; //needs a parameter
        ResultSet rs = null;
        try {
            //safest way to do this is directly through the Connection.....
            conn = helper.getJDBCConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery( expSql );
            while ( rs.next() ) {
                //stick them into the Set of ACs
                expAcs.add( rs.getString( "experiment_ac" ) );
            }
            rs.close();
            stmt.close();
            //System.out.println("DEBUG: size of AC List: " + expAcs.size());

            //now query via the Interactions...
            stmt = conn.createStatement();
            rs = stmt.executeQuery( sql );
            while ( rs.next() ) {
                //stick them into the Set of ACs
                expAcs.add( rs.getString( "experiment_ac" ) );
            }
            rs.close();
            stmt.close();

            //now get the shortlabels of the Experiments as these are what we need...
            for ( Iterator it = expAcs.iterator(); it.hasNext(); ) {
                labelStmt = conn.prepareStatement( "SELECT shortlabel FROM ia_experiment WHERE ac= ?" );
                labelStmt.setString( 1, (String) it.next() );
                rs = labelStmt.executeQuery();
                while ( rs.next() ) {
                    negExpLabels.add( rs.getString( "shortlabel" ) );
                }
            }

        } catch ( SQLException se ) {

            System.out.println( se.getSQLState() );
            System.out.println( se.getErrorCode() );
            se.printStackTrace();
            while ( ( se.getNextException() ) != null ) {
                System.out.println( se.getSQLState() );
                System.out.println( se.getErrorCode() );
                se.printStackTrace();
            }
        } finally {
            try {
                if ( stmt != null ) {
                    stmt.close();
                }
                if ( labelStmt != null ) {
                    labelStmt.close();
                }
                if ( conn != null ) {
                    conn.close();
                }
            } catch ( SQLException se ) {
                se.printStackTrace();
            }
        }
    }


    /**
     * Output the experiment classification, suitable for scripting
     *
     * @param allExp HashMap of HashMap of ArrayLists of Experiments: {species}{scale}[n]
     */
    public static void writeExperimentsClassification( HashMap allExp, Writer writer ) throws IOException {

        for ( Iterator iterator = allExp.keySet().iterator(); iterator.hasNext(); ) {

            BioSource bioSource = (BioSource) iterator.next();
            ArrayList smallScaleExp = (ArrayList) ( (HashMap) allExp.get( bioSource ) ).get( "small" );

            String fileNameRoot = bioSource.getShortLabel().replace( ' ', '-' );
            //create two filenames, one for usual exps and one for any 'negatives'
            String smallFile = fileNameRoot + "_small.xml";
            String negFile = fileNameRoot + "_small_negative.xml";

            //buffers to hold the labels for small and negative small exps
            StringBuffer negPattern = new StringBuffer();
            StringBuffer pattern = new StringBuffer();

            String label = null;
            for ( int i = 0; i < smallScaleExp.size(); i++ ) {
                Experiment experiment = (Experiment) smallScaleExp.get( i );
                label = experiment.getShortLabel();

                //put the Experiment label in the correct place, depending upon
                //its sub-classification (ie negative or not)
                if ( negExpLabels.contains( label ) ) {
                    negPattern.append( label + "," );
                } else {
                    pattern.append( label + "," );
                }
            }

            //strip off trailing comma - can't do this in advance as we don't
            //know how big the buffers can be now!
            if ( pattern.length() > 0 ) {
                pattern.deleteCharAt( pattern.length() - 1 );
            }

            if ( negPattern.length() > 0 ) {
                negPattern.deleteCharAt( negPattern.length() - 1 );
            }

            //classification for this BioSource is output as:
            //'<filename> <comma-seperated shortLabel list>'
            //only print patterns if they are non-empty
            if ( pattern.length() != 0 ) {
                String line = smallFile + " " + pattern.toString();
                System.out.println( line );
                writer.write( line );
                writer.write( NEW_LINE );
            }

            if ( negPattern.length() != 0 ) {
                String line = negFile + " " + negPattern.toString();
                System.out.println( line );
                writer.write( line );
                writer.write( NEW_LINE );
            }


            //Now do the large ones...
            //NB these are classified into single files, one for each shortLabel
            ArrayList largeScaleExp = (ArrayList) ( (HashMap) allExp.get( bioSource ) ).get( "large" );
            String fileName = fileNameRoot;
            for ( int i = 0; i < largeScaleExp.size(); i++ ) {
                Experiment experiment = (Experiment) largeScaleExp.get( i );

                label = experiment.getShortLabel();

                if ( negExpLabels.contains( label ) ) {
                    fileName = fileName + "_" + label + "_negative";
                } else {
                    fileName = fileName + "_" + label;
                }

                //dump out the generated <filename> <label> pair
                String line = fileName + ".xml" + " " + label;
                System.out.println( line );
                writer.write( line );
                writer.write( NEW_LINE );
                fileName = fileNameRoot;    //reset to Biosource for the next Experiment
            }
        }
    }


    private static void displayUsage( Options options ) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "ExperimentListGenerator " +
                             "-output <filename> " +
                             "-pattern <shortlabel pattern> ",
                             options );
    }

    protected static String TIME;
    static {
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd@HH.mm" );
        TIME = formatter.format( new Date() );
        formatter = null;
    }

    public static void main( String[] args ) throws IntactException, IOException {

        // if only one argument, then dump the matching experiment classified by specied into a file

        // create Option objects
        Option helpOpt = new Option( "help", "print this message." );

        Option outputOpt = OptionBuilder.withArgName( "outputFilename" )
                .hasArg()
                .withDescription( "output filename" )
                .create( "output" );
        outputOpt.setRequired( false );

        Option patternOpt = OptionBuilder.withArgName( "experimentPattern" )
                .hasArg()
                .withDescription( "experiment shortlabel pattern" )
                .create( "pattern" );
        outputOpt.setRequired( false );

        Options options = new Options();
        options.addOption( helpOpt );
        options.addOption( outputOpt );
        options.addOption( patternOpt );

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

        // Process arguments
        String filename = line.getOptionValue( "output" );
        File file = null;

        if ( filename != null ) {
            try {
                file = new File( filename );
                if ( file.exists() ) {
                    System.err.println( "Please give a new file name for the output file: " + file.getAbsoluteFile() );
                    System.err.println( "We will use the default filename instead (instead of overwritting the existing file)." );
                    filename = null;
                    file = null;
                }
            } catch ( Exception e ) {
                // nothing, the default filename will be given
            }
        }

        if ( filename == null || file == null ) {
            filename = "experimentList_" + TIME + ".txt";
            System.out.println( "Using default filename for the export: " + filename );
            file = new File( filename );
        }

        Writer writer = new FileWriter( file );

        System.out.println( "FileName: " + file.getAbsolutePath() );

        String pattern = line.getOptionValue( "pattern" );
        if ( pattern == null || pattern.trim().equals( "" ) ) {
            pattern = "%";
        }

        System.err.println( "Pattern: " + pattern );

        HashMap exps = classifyExperiments( pattern );
        writeExperimentsClassification( exps, writer );
    }
}