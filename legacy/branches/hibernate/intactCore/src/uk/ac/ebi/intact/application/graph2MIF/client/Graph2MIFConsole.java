/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.graph2MIF.client;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.application.graph2MIF.Graph2MIFWSService;
import uk.ac.ebi.intact.application.graph2MIF.exception.MIFSerializeException;
import uk.ac.ebi.intact.application.graph2MIF.exception.NoGraphRetrievedException;
import uk.ac.ebi.intact.application.graph2MIF.exception.NoInteractorFoundException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.persistence.DataSourceException;

import java.sql.SQLException;

/**
 *  Graph2MIFConsole
 *
 *  This is a stand alone client of Graph2MIF (without WebService).
 *
 *  @author Henning Mersch <hmersch@ebi.ac.uk>
 *  @version $Id$
 */
public class Graph2MIFConsole {

    //line separator for ggivcing out usage
    public static final String NEW_LINE = System.getProperty("line.separator");
    //The IntactHelper for retrieving information from IntAct Database.
    private IntactHelper helper;

    //Contructor creates the IntactHelper
    public Graph2MIFConsole() throws IntactException, DataSourceException {
        helper = new IntactHelper();
        try {
            System.out.println ( "Using DB:   " + helper.getDbName() );
            System.out.println ( "Using user: " + helper.getDbUserName() );
        } catch ( LookupException e ) {
            e.printStackTrace ();
        } catch ( SQLException e ) {
            e.printStackTrace ();
        }
    }

    /**
     * main Method, which will be called if the client is started.
     * Prints the PSI-XML data to STDOUT and errors to STDERR.
     *
     * @param args [0] ac: String of the Ac in IntAct Database
     * @param args [1] depth: Integer of the depth the graph should be expanded
     * @param args [2] strict: (true|false) if only strict MIF should be produce
     */
    public static void main(String[] args) {
        // setting/checking arguments
        Option helpOpt = new Option("help", "print this message");
                Option acOpt = OptionBuilder.withArgName("ac")
                        .hasArg()
                        .withDescription("accession number")
                        .create("ac");
                acOpt.setRequired(true);

                Option depthOpt = OptionBuilder.withArgName("depth")
                        .hasArg()
                        .withDescription("depth to expand")
                        .create("depth");
                depthOpt.setRequired(true);

                Option strictOpt = OptionBuilder.withArgName("strict")
                        .hasArg()
                        .withDescription("true or false for getting only strict MIF Default: false")
                        .create("strict");
                strictOpt.setRequired(false);

                Option psiVersionOpt = OptionBuilder.withArgName("version")
                        .hasArg()
                        .withDescription("possible values are 1, 2 or 2.5  Default: 1")
                        .create("version");
                psiVersionOpt.setRequired(false);

                Options options = new Options();
                options.addOption(helpOpt);
                options.addOption(acOpt);
                options.addOption(depthOpt);
                options.addOption(strictOpt);
                options.addOption(psiVersionOpt);
        // create the parser
        CommandLineParser parser = new BasicParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args, true);

            if (line.hasOption("help")) {
                displayUsage(options);
                System.exit(0);
            }

            // These argument are mandatory.
            String ac = line.getOptionValue("ac");
            Boolean strictmif = new Boolean(line.getOptionValue("strict").equals("true"));
            String psiVersion = line.getOptionValue("version");

            try {
                Integer depth = new Integer(line.getOptionValue("depth"));
                // call  getMIF  & give out with params to retrieve data
                Graph2MIFWSService ws = new Graph2MIFWSService();
                String mif = ws.getMIF(ac, depth, strictmif, psiVersion);
                //giving result to STDOUT
                System.out.println(mif);
                //or get errors and give out.
            } catch (NumberFormatException e) {
                System.err.println("depth sould be an integer");
                System.exit(1);
            }
        } catch (ParseException exp) {
            // oops, something went wrong
            displayUsage(options);
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            System.exit(1);


        } catch (IntactException e) {
            System.err.println("ERROR: Search for interactor failed (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Template
        } catch (NoGraphRetrievedException e) {
            System.err.println("ERROR: Could not retrieve graph from interactor (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (MIFSerializeException e) {
            System.err.println("ERROR: DOM-Object could not be serialized (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Templates
        } catch (NoInteractorFoundException e) {
            System.err.println("ERROR: No Interactor found for this ac (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }

    }

    /**
     * Will give proper usage, if requested or wrong arguments.
     *
     * @param options command line otions
     */

    private static void displayUsage(Options options) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Client -ac <ac> " +
                "-depth <depth> " +
                "-strict [true|false]",
                options);

    }


}