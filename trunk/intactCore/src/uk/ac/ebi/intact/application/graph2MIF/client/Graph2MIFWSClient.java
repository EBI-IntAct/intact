/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.graph2MIF.client;

import org.apache.axis.client.*;

import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.cli.*;
import uk.ac.ebi.intact.util.PropertyLoader;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.application.graph2MIF.GraphNotConvertableException;
import uk.ac.ebi.intact.application.graph2MIF.NoGraphRetrievedException;
import uk.ac.ebi.intact.application.graph2MIF.MIFSerializeException;
import uk.ac.ebi.intact.application.graph2MIF.NoInteractorFoundException;
import uk.ac.ebi.intact.persistence.DataSourceException;

/**
 * Graph2MIFWSClient
 *
 * This is an example implementation of accessing the WebService "Graph2MIFWS" for export of
 * the IntAct Database as PSI-MIF Format.
 * The resulting PSI-XML will simply writen to STDOUT while occuring errors ging to STDERR
 *
 * See http://psidev.sourceforge.net/ for further information on the PSI-MIF-Format
 *
 * @author Henning Mersch <hmersch@ebi.ac.uk>
 * @version $Id$
 */

public class Graph2MIFWSClient {


    //line separator for giving out usage
    public static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * main Method, which will be called if the client is started.
     * Prints the PSI-XML data to STDOUT and errors to STDERR.
     *
     * @param args [0] ac: String of the Ac in IntAct Database
     * @param args [1] depth: Integer of the depth the graph should be expanded
     * @param args [2] strict: (true|false) if only strict MIF should be produce
     */
    public static void main(String[] args) {
        try {
            //loading properties
            Properties props = PropertyLoader.load("/graph2MIF.properties");
            if (props != null) {
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
                Options options = new Options();
                options.addOption(helpOpt);
                options.addOption(acOpt);
                options.addOption(depthOpt);
                options.addOption(strictOpt);
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
                    Boolean strictmif;
                    if (line.getOptionValue("strict").equals("true")) {
                        strictmif = new Boolean(true);
                    } else {
                        strictmif = new Boolean(false);
                    }

                    try {
                        Integer depth = new Integer(line.getOptionValue("depth"));
                        URL url = new URL(props.getProperty("webservice.location"));
                        // prepare the call (the same for all called methods)
                        Call call = (Call) new Service().createCall();
                        call.setTargetEndpointAddress(url);
                        // if no args are submitted give out usage.
                        // call  getMIF  & give out with params to retrieve data
                        call.setMaintainSession(false);
                        call.setOperationName("getMIF");
                        System.out.println(call.invoke(new Object[]{ac, depth, strictmif}));
                    } catch (NumberFormatException e) {
                        System.err.println("depth sould be an integer");
                        System.exit(1);
                    }
                } catch (ParseException exp) {
                    // oops, something went wrong
                    displayUsage(options);
                    System.err.println("Parsing failed.  Reason: " + exp.getMessage());
                    System.exit(1);
                }
                // error handling with proper information for the user
            } else { //end probs!=null
                System.err.println("Unable to open the properties file.");
                System.exit(1);
            }

        } catch (RemoteException e) {
            //Using this kind of Webservice there is only one one field for giving back a
            //error message. When an axception occours, the client side of Axis will throw
            //an RemoteException which includes the class name of the thrown exception.
            //There is no way to get more information like the original stacktrace !!!
            if (e.toString().equals(IntactException.class.getName())) {
                System.err.println("ERROR: Search for interactor failed (" + e.toString() + ")");
                System.exit(1);
            } else if (e.toString().equals(GraphNotConvertableException.class.getName())) {
                System.err.println("ERROR: Graph failed requirements of MIF. (" + e.toString() + ")");
                System.exit(1);
            } else if (e.toString().equals(NoGraphRetrievedException.class.getName())) {
                System.err.println("ERROR: Could not retrieve graph from interactor (" + e.toString() + ")");
                System.exit(1);
            } else if (e.toString().equals(MIFSerializeException.class.getName())) {
                System.err.println("ERROR: DOM-Object could not be serialized (" + e.toString() + ")");
                System.exit(1);
            } else if (e.toString().equals(DataSourceException.class.getName())) {
                System.err.println("ERROR: IntactHelper could not be created (" + e.toString() + ")");
                System.exit(1);
            } else if (e.toString().equals(NoInteractorFoundException.class.getName())) {
                System.err.println("ERROR: No Interactor found for this ac (" + e.toString() + ")");
                System.exit(1);
            } else { //should never occour
                System.err.println("ERROR: unexpected RemoteException (" + e.toString() + ")");
                System.exit(1);
            }
        } catch (ServiceException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (MalformedURLException e) {
            System.err.println("Wrong URL Format - change graph2MIF.properties");
            System.exit(1);
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