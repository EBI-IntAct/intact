/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.dbutil.reactome;

import org.apache.commons.cli.*;
import uk.ac.ebi.intact.context.IntactContext;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Produce a file containing all intact Xrefs to Reactome.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>26-Jan-2006</pre>
 */
public class ReactomeXrefs {

    ////////////////////////
    // Constants

    private static final String FILE_OPTION = "file";
    private static final String URL_OPTION = "url";

    ///////////////////////////
    // COmmand line support

    private final static CommandLine setupCommandLine( String[] args ) {
        // if only one argument, then dump the matching experiment classified by specied into a file

        // create Option objects
        Option helpOpt = new Option( "help", "print this message." );

        Option fileOpt = OptionBuilder.withArgName( "xrefFilename" )
                .hasArg()
                .withDescription( "output filename" )
                .create( "file" );
        fileOpt.setRequired( false );

        Option urlOpt = OptionBuilder.withArgName( "reactomeURL" )
                .hasArg()
                .withDescription( "URL or reactome file to check against" )
                .create( "url" );
        urlOpt.setRequired( false );

        Options options = new Options();
        options.addOption( helpOpt );
        options.addOption( fileOpt );
        options.addOption( urlOpt );

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

        return line;
    }

    private final static void displayUsage( Options options ) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "ReactomeXrefs -file [filename] -url [url] ",
                             options );
    }

    private final static String getDefaultFilename() {
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd@HH.mm" );
        String time = formatter.format( new Date() );
        return "reactomeXrefs." + time + ".txt";
    }

    



    /////////////////////////
    // M A I N

    public static void main( String[] args ) throws ReactomeException, SQLException {

        /////////////////////////////
        // Command line management

        CommandLine commandLine = setupCommandLine( args );

        String filename = commandLine.getOptionValue( FILE_OPTION );
        if ( filename == null ) {
            String defaultFilename = getDefaultFilename();
            System.out.println( "No filename given, set it to default value: " + defaultFilename );
            filename = defaultFilename;
        }

        System.out.println( "Opening file: " + filename );
        File outputFile = new File( filename );

        if ( outputFile.exists() && ! outputFile.canWrite() ) {
            String defaultFilename = getDefaultFilename();
            System.out.println( "Cannot write on " + outputFile.getAbsolutePath() + ", set it to default value." );
            filename = defaultFilename;
            System.out.println( "Opening file: " + filename );
            outputFile = new File( filename );
        }


        String urlParam = commandLine.getOptionValue( URL_OPTION );
        if ( urlParam != null ) {
            System.out.println( "URL: " + urlParam );
        }

        /////////////////////////////
        // Here the program starts
            
         System.out.println( "Database: " + IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao().getDbName() );

        List<ReactomeBean> reactomeBeans = ReactomeExport.createReactomXrefsFromIntactList();
        ReactomeExport.exportToReactomeFile(reactomeBeans, outputFile);

        try
        {
            ReactomeValidationReport report = ReactomeExport.areXrefsFromIntactValid(reactomeBeans, new URL(urlParam));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }


    }

}