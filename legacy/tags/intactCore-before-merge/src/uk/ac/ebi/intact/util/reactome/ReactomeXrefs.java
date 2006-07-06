/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.reactome;

import org.apache.commons.cli.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvDatabase;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    private static final String NEW_LINE = System.getProperty( "line.separator" );

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

    /**
     * Retreives web content from a URL.
     *
     * @param url the URL we want to download data from.
     *
     * @return the content as a String.
     */
    public static List getUrlData( URL url ) throws IOException {

        List beans = new ArrayList( 256 );

        System.out.println( "Retreiving content for: " + url );

        StringBuffer content = new StringBuffer( 4096 );

        // Read all the text returned by the server
        BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );
        String str;
        while ( ( str = in.readLine() ) != null ) {
            // str is one line of text; readLine() strips the newline character(s)

            if ( str.startsWith( "#" ) ) {
                continue;
            }

            StringTokenizer stringTokenizer = new StringTokenizer( str, "\t" );
            String InteractionAc = stringTokenizer.nextToken();
            String reactomeId = stringTokenizer.nextToken();

            ReactomeBean reactomeBean = new ReactomeBean();
            reactomeBean.setReactomeID( reactomeId );
            reactomeBean.setInteractionAC( InteractionAc );

            beans.add( reactomeBean );
        }
        in.close();

        return beans;
    }

    /**
     * Extract IntAct IDs from from the given list.
     *
     * @param xrefsFromIntact the given list of Xrefs
     *
     * @return a new instance of List containing all IntAct IDs.
     */
    private static final List getIntactIDs( List xrefsFromIntact ) {
        List ids = new ArrayList( xrefsFromIntact.size() );

        for ( Iterator iterator = xrefsFromIntact.iterator(); iterator.hasNext(); ) {
            ReactomeBean reactomeBean = (ReactomeBean) iterator.next();
            ids.add( reactomeBean.getInteractionAC() );
        }

        return ids;
    }

    /**
     * Extract reactome IDs from from the given list.
     *
     * @param reactomeBeans the given list of Xrefs
     *
     * @return a new instance of List containing all reactome IDs.
     */
    private static final List getReactomeIDs( List reactomeBeans ) {

        List ids = new ArrayList( reactomeBeans.size() );

        for ( Iterator iterator = reactomeBeans.iterator(); iterator.hasNext(); ) {
            ReactomeBean reactomeBean = (ReactomeBean) iterator.next();
            ids.add( reactomeBean.getReactomeID() );
        }

        return ids;
    }

    /////////////////////////
    // M A I N

    public static void main( String[] args ) throws IntactException, SQLException {

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

        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            System.out.println( "Database: " + helper.getDbName() );

            Connection connection = helper.getJDBCConnection();

//            String test = "SELECT i.ac as interactionAC, x.primaryId as reactomeID \n" +
//                          "FROM ia_experiment e,\n" +
//                          "     ia_int2exp i2e,\n" +
//                          "     ia_exp2annot e2a,\n" +
//                          "     ia_annotation a,\n" +
//                          "     ia_controlledvocab topic_i,\n" +
//                          "     ia_interactor i, \n" +
//                          "     ia_xref x, \n" +
//                          "     ia_controlledvocab db \n" +
//                          "WHERE i.objclass LIKE '%Interaction%' \n" +
//                          "      AND i.ac = i2e.interaction_ac \n" +
//                          "      AND i2e.experiment_ac = e.ac\n" +
//                          "      AND e2a.annotation_ac = a.ac \n" +
//                          "      AND a.topic_ac = topic_i.ac\n" +
//                          "      AND topic_i.shortlabel LIKE '%' -- should be curated-complex\n" +
//                          "      AND i.ac = x.parent_ac \n" +
//                          "      AND x.database_ac = db.ac \n" +
//                          "      AND db.shortlabel = 'reactome complex'";
//
//            System.out.println( test );

            final String sql = "SELECT p.ac as interactionAC, x.primaryId as reactomeID " +
                               "FROM ia_interactor p, ia_xref x, ia_controlledvocab db " +
                               "WHERE p.objclass LIKE '%Interaction%' AND" +
                               "      p.ac = x.parent_ac AND" +
                               "      x.database_ac = db.ac AND" +
                               "      db.shortlabel = '" + CvDatabase.REACTOME_COMPLEX + "'";

            System.out.println( "sql = " + sql );

            QueryRunner queryRunner = new QueryRunner();
            ResultSetHandler handler = new BeanListHandler( ReactomeBean.class );
            List xrefsFromIntact = (List) queryRunner.query( connection, sql, handler );

            if ( xrefsFromIntact == null ) {
                System.err.println( "Error: could not retreive any data about Reactome Xrefs in IntAct." );
                System.exit( 1 );
            }

            System.out.println( "IntAct maintain " + xrefsFromIntact.size() + " Xref" +
                                ( xrefsFromIntact.size() > 1 ? "s" : "" ) + " to Reactome." );


            try {
                BufferedWriter out = new BufferedWriter( new FileWriter( outputFile ) );

                System.out.println( xrefsFromIntact.size() + " Reactome xref" + ( xrefsFromIntact.size() > 1 ? "s" : "" ) + " found." );
                for ( Iterator iterator = xrefsFromIntact.iterator(); iterator.hasNext(); ) {
                    ReactomeBean reactomeBean = (ReactomeBean) iterator.next();

                    out.write( reactomeBean.toSingleLine() );
                    out.write( NEW_LINE );
                }

                out.close();
                System.out.println( "File closed." );
            } catch ( IOException e ) {
                e.printStackTrace();
                System.exit( 1 );
            }

            // if everything went fine, then check if we have some Xref from the reactome URL to compare ours with.
            if ( urlParam != null ) {
                try {
                    URL url = new URL( urlParam );
                    List xrefsFromReactome = getUrlData( url );

                    System.out.println( "Reactome maintain " + xrefsFromReactome.size() + " Xref" +
                                        ( xrefsFromReactome.size() > 1 ? "s" : "" ) + " to IntAct." );

                    // intact's reactome ID MINUS Reactome's
                    Collection reactomeIDs = CollectionUtils.subtract( getReactomeIDs( xrefsFromIntact ),
                                                                       getReactomeIDs( xrefsFromReactome ) );

                    if ( ! reactomeIDs.isEmpty() ) {
                        System.err.println( "We have found " + reactomeIDs.size() + " Reactome ID that are used in IntAct but not existing in Reactome anymore." );

                        // print the list out.
                        for ( Iterator iterator = reactomeIDs.iterator(); iterator.hasNext(); ) {
                            String reactomeId = (String) iterator.next();
                            System.out.println( reactomeId );
                        }
                    } else {
                        System.out.println( "All Reactome Xref maintained in IntAct are Live in Reactome." );
                    }

                    // Reactome's IntAct ID MINUS IntAct's
                    Collection intactIDs = CollectionUtils.subtract( getIntactIDs( xrefsFromReactome ),
                                                                     getIntactIDs( xrefsFromIntact ) );

                    if ( ! intactIDs.isEmpty() ) {
                        System.err.println( "We have found " + intactIDs.size() + " Intact Interaction AC that are used in Reactome but not existing in IntAct anymore." );

                        // print the list out.
                        for ( Iterator iterator = intactIDs.iterator(); iterator.hasNext(); ) {
                            String reactomeId = (String) iterator.next();
                            System.out.println( reactomeId );
                        }
                    } else {
                        System.out.println( "All Reactome Xref maintained in Reactome are Live in IntAct." );
                    }

                } catch ( MalformedURLException e ) {
                    e.printStackTrace();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }


        } finally {
            if ( helper == null ) {
                System.out.println( "Closing database connection." );
                helper.closeStore();
            }
        }
    }

}