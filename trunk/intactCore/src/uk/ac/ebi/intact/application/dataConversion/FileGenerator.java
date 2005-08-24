package uk.ac.ebi.intact.application.dataConversion;

import org.apache.commons.cli.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.CvMapping;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.UserSessionDownload;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Interaction2xmlFactory;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Interaction2xmlI;
import uk.ac.ebi.intact.application.dataConversion.util.DisplayXML;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;

import java.io.*;
import java.util.*;


/**
 * This class is the main application class for generating a flat file format from the contents of a database. Currently
 * the file format is PSI, and the DBs are postgres or oracle, though the DB details are hidden behind the
 * IntactHelper/persistence layer as usual.
 *
 * @author Chris Lewington
 * @version $id$
 */
public class FileGenerator {

    private IntactHelper helper;


    public FileGenerator( IntactHelper helper ) {
        this.helper = helper;
    }

    /**
     * Obtains the data from the dataSource, in preparation for the flat file generation.
     *
     * @param searchPattern for search by shortLabel. May be a comma-separated list.
     *
     * @throws IntactException thrown if there was a search problem
     */
    public Collection getDbData( String searchPattern ) throws IntactException {

        //try this for now, but it may be better to use SQL and get the ACs,
        //then cycle through them and generate PSI one by one.....
        ArrayList searchResults = new ArrayList();
        System.out.print( "Retrieving data from DB store..." );
        System.out.flush();

        StringTokenizer patterns = new StringTokenizer( searchPattern, "," );

        while ( patterns.hasMoreTokens() ) {
            String experimentShortlabel = patterns.nextToken().trim();
//            System.out.println( "Searching for: '" + experimentShortlabel + "'" );
            searchResults.addAll( helper.search( Experiment.class, "shortLabel", experimentShortlabel ) );
        }

        int resultSize = searchResults.size();
        System.out.println( "done (found " + resultSize + " experiment" + ( resultSize > 1 ? "s" : "" ) + ")" );

        return searchResults;
    }

    /**
     * Generates a PSI MI formatted file for a searchPattern. Large scale Experiments will typically be a searchpattern
     * of a single shortlabel, and these are processed as chunks. Small scale ones will generally have a searchpattern
     * consisting of multiple shortlabels, and these will be placed into a single file.
     *
     * @param searchPattern a comma separated list of experiment's shortlabel.
     * @param fileName      the file in which we will store the generated XML.
     * @param version       the PSI version of the generated XML output.
     *
     * @throws Exception
     */
    public static void generatePsiData( String searchPattern, String fileName, PsiVersion version, File reverseCvFilename ) throws Exception {

        IntactHelper helper = null;

        try {
            helper = new IntactHelper();
            CvMapping mapping = null;

            if ( reverseCvFilename != null ) {
                // try to load the reverse mapping
                mapping = new CvMapping();
                mapping.loadFile( reverseCvFilename, helper );
            }

            FileGenerator generator = new FileGenerator( helper );

            //get all of the Experiment shortlabels and process them according
            //to the size of their interaction list..
            //NB this currently means that if the search result size
            //is one and the number of interactions is large then we process seperately.
            Collection searchResults = generator.getDbData( searchPattern );


            if ( searchResults.size() == 1 ) {
                //may be a large experiment - check and process if necessary
                Experiment exp = (Experiment) searchResults.iterator().next();

                if ( exp.getInteractions().size() > ExperimentListGenerator.LARGESCALESIZE ) {
                    System.out.println( "processing large experiment " + exp.getShortLabel() + " ...." );

                    FileGenerator.processLargeExperiment( exp, fileName, version, mapping );

                    return;     //done
                }
            }

            //not a large experiment - may be a single small one or a set of small ones,
            //so process 'normally' into a single file...

            UserSessionDownload session = new UserSessionDownload( version );
            if( mapping != null ) {
                session.setReverseCvMapping( mapping );
            }

            Document psiDoc = generator.generateData( searchResults, session );
            session.printMessageReport( System.err );
            write( psiDoc.getDocumentElement(), new File( fileName ) );

        } finally {
            if ( helper != null ) {
                helper.closeStore();
            }
        }
    }

    /**
     * Convert a list of experiment into PSI XML
     *
     * @param experiments a list of experiment to export in PSI XML
     * @param session     the PSI doanload session.
     *
     * @return the generated XML Document
     */
    public Document generateData( Collection experiments, UserSessionDownload session ) {

        Interaction2xmlI interaction2xml = Interaction2xmlFactory.getInstance( session );

        for ( Iterator iterator = experiments.iterator(); iterator.hasNext(); ) {

            Experiment experiment = (Experiment) iterator.next();

            System.out.println( "Processing: " + experiment.getShortLabel() + ", has " +
                                experiment.getInteractions().size() + " interaction(s)." );

            // in order to have them in that order, experimentList, then interactorList, at last interactionList.
            session.getExperimentListElement();
            session.getInteractorListElement();

            Collection interactions = experiment.getInteractions();
            int count = 0;
            for ( Iterator iterator1 = interactions.iterator(); iterator1.hasNext(); ) {

                Interaction interaction = (Interaction) iterator1.next();
                interaction2xml.create( session, session.getInteractionListElement(), interaction );

                System.out.print( "." );
                System.out.flush();
                count++;

                if ( ( count % 50 ) == 0 ) {
                    System.out.println( " " + count );
                }
            } // interactions

            if ( ( count % 50 ) != 0 ) {
                System.out.println( " " + count );
            }
        } // experiments

        return session.getPsiDocument();
    }

    public static void processLargeExperiment( Experiment exp, String fileName, PsiVersion version, CvMapping mapping ) throws Exception {

        PsiDataBuilder builder = new PsiDataBuilder();

        // Need to process the big ones chunk by chunk -
        // do this by splitting the Interactions into manageable pieces (LARGESCALESIZE each),
        // then building and writing some XML to seperate files....
        int startIndex = 0;
        int endIndex = ExperimentListGenerator.LARGESCALESIZE; //NB END INDEX IS EXCLUSIVE!!
        int chunkCount = 1;                                    // used to distinguish files
        String mainFileName = null;                            // filename in which the chunk will be saved
        List itemsToProcess = null;

        System.out.println( "generating Interaction files for experiment " + exp.getShortLabel() + ": Blocks completed: " );

        Collection interactions = exp.getInteractions();

        //Lists are easier to work with...
        if ( List.class.isAssignableFrom( interactions.getClass() ) ) {

            while ( startIndex < interactions.size() ) {

                if ( endIndex > interactions.size() ) {
                    endIndex = interactions.size(); //check for the end
                }

                itemsToProcess = ( (List) interactions ).subList( startIndex, endIndex );

                System.out.println( "Generating InteractionList for chunk " + chunkCount + "..." );
                System.out.println();

                // create the session, and if required, set a CV mapping.
                UserSessionDownload session = new UserSessionDownload( version );
                if ( mapping != null ) {
                    session.setReverseCvMapping( mapping );
                }

                Interaction2xmlI interaction2xml = Interaction2xmlFactory.getInstance( session );

                // in order to have them in that order, experimentList, then interactorList, at last interactionList.
                session.getExperimentListElement();
                session.getInteractorListElement();

                //now build the XML and generate a file for it...
                int count = 0;
                for ( int i = 0; i < itemsToProcess.size(); i++ ) {
                    Interaction interaction = (Interaction) itemsToProcess.get( i );

                    interaction2xml.create( session, session.getInteractionListElement(), interaction );

                    System.out.print( "." );
                    System.out.flush();
                    count++;

                    if ( ( count % 50 ) == 0 ) {
                        System.out.println( " " + count );
                    }
                }

                //add a leading zero to the chunk count and use the fileName
                //if it is given - otherwise use the shortlabel

                if ( fileName != null ) {
                    // we look for the suffix .xml otherwise if a directory has a . in its name we would truncate there.
                    mainFileName = fileName.substring( 0, fileName.lastIndexOf( ".xml" ) ) + "_0" + chunkCount + ".xml";
                } else {
                    mainFileName = mainFileName + "0" + chunkCount + ".xml";
                }

                write( session.getPsiDocument().getDocumentElement(), new File( mainFileName ) );
                System.out.println();

                chunkCount++;
                startIndex = endIndex;
                endIndex = endIndex + ExperimentListGenerator.LARGESCALESIZE;
            }
        } else {
            //This will only NOT be a List if someone changes the model
            //data types!!
            throw new Exception( "can't process large experiment - " +
                                 "the Collection of Interactions must be a List but is instead " +
                                 interactions.getClass().getName() );
        }
    }

    public static void write( Element root, File file ) throws IOException {

        System.out.print( "\nWriting DOM to " + file.getAbsolutePath() + " ... " );
        System.out.flush();

        // prepare a file writer.
        Writer writer = new BufferedWriter( new FileWriter( file ) );

        // Write the content in the file (indented !!)
        DisplayXML.write( root, writer, "   " );

        writer.flush();

        // Close the file
        writer.close();

        System.out.println( "done." );
    }

    private static void displayUsage( Options options ) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "FileGenerator " +
                             "-output <filename> " +
                             "-pattern <comma separated list of shortlabel> " +
                             "[-psiVersion <1 or 2 or 2.5>] " +
                             "[-cvMapping <filename>] ",
                             options );
    }

    // Main method

    /**
     * Main method for the PSI application. The application is typically run twice - firstly with a wildcard ('%')
     * argument to generate a file containing classifications into species of experiment labels, then secondly to use
     * that file to generate the PSI XML data for each classification. This second step is handled via a perl script
     * which repeatedly calls this application to generate the files. Note that the exceptions to the species
     * classification are large-scale experiments (as defined by the SMALLSCALELIMIIT constant) - these cannot be put
     * into XML files with other experiments due to size and memory constraints, and so they are generated in 'chunks'
     * of data divided by 'chunks' of interactions.
     *
     * @param args
     *
     * @throws Exception
     */
    public static void main( String[] args ) throws Exception {

        try {
            // create Option objects
            Option helpOpt = new Option( "help", "print this message." );

            Option outputOpt = OptionBuilder.withArgName( "outputFilename" )
                    .hasArg()
                    .withDescription( "output filename" )
                    .create( "output" );
            outputOpt.setRequired( true );

            Option patternOpt = OptionBuilder.withArgName( "list" )
                    .hasArg()
                    .withDescription( "comma separated list of experiment's shortlabel" )
                    .create( "pattern" );
            patternOpt.setRequired( true );

            Option versionOpt = OptionBuilder.withArgName( "version" )
                    .hasArg()
                    .withDescription( "PSI version to export (1 or 2 or 2.5)" )
                    .create( "psiVersion" );
            patternOpt.setRequired( false );

            Option mappingOpt = OptionBuilder.withArgName( "mappingFilename" )
                    .hasArg()
                    .withDescription( "File containing eventual CV mapping" )
                    .create( "cvMapping" );
            mappingOpt.setRequired( false );

            Options options = new Options();

            options.addOption( helpOpt );
            options.addOption( outputOpt );
            options.addOption( patternOpt );
            options.addOption( versionOpt );
            options.addOption( mappingOpt );


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
            final String fileName = line.getOptionValue( "output" );
            System.out.println( "FileName: " + fileName );

            final String searchPattern = line.getOptionValue( "pattern" );
            System.out.println( "SearchPattern: " + searchPattern );

            final String psiVersionArg = line.getOptionValue( "psiVersion" );
            PsiVersion psiVersion = null;

            if ( "1".equals( psiVersionArg ) ) {
                System.out.println( "PSI version 1 requested." );
                psiVersion = PsiVersion.VERSION_1;
            } else if ( "2".equals( psiVersionArg ) ) {
                System.out.println( "PSI version 2 requested." );
                psiVersion = PsiVersion.VERSION_2;
            } else if ( "2.5".equals( psiVersionArg ) ) {
                System.out.println( "PSI version 2.5 requested." );
                psiVersion = PsiVersion.VERSION_25;
            } else {
                System.err.println( "Unrecognized PSI version: " + psiVersionArg + " (expected: 1, 2 or 2.5)" );
            }

            if ( psiVersion == null ) {
                // set to the default PSI version
                System.out.println( "Use default PSI version 1." );
                psiVersion = PsiVersion.VERSION_1;
            }

            File cvMapping = null;
            if ( line.hasOption( "cvMapping" ) ) {
                String cvMappingFile = line.getOptionValue( "cvMapping" );
                cvMapping = new File( cvMappingFile );
                System.out.println( "Cv Mapping file: " + cvMappingFile );
            }

            if ( cvMapping != null ) {

                if ( false == cvMapping.exists() ) {
                    System.err.println( "The given file doesn't exist. ignore it." );
                    cvMapping = null;
                }

                if ( false == cvMapping.canRead() ) {
                    System.err.println( "The given file is not readable. ignore it." );
                    cvMapping = null;
                }
            }

            if ( fileName == null ) {

            }


            long start = System.currentTimeMillis();


            generatePsiData( searchPattern, fileName, psiVersion, cvMapping );

            long end = System.currentTimeMillis();
            long total = end - start;
            System.err.println( "Total time to build and generate file: " + total / 1000 + "s" );

        } catch ( IntactException e ) {
            System.err.println( "Root cause: " + e.getRootCause() );
            e.printStackTrace();
            System.exit( 1 );
        }
    }
}