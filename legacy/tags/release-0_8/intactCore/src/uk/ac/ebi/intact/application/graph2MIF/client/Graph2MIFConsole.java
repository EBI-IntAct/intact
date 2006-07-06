/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.graph2MIF.client;

import org.apache.commons.cli.*;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import uk.ac.ebi.intact.application.graph2MIF.conversion.Graph2FoldedMIF;
import uk.ac.ebi.intact.application.graph2MIF.exception.GraphNotConvertableException;
import uk.ac.ebi.intact.application.graph2MIF.exception.MIFSerializeException;
import uk.ac.ebi.intact.application.graph2MIF.exception.NoGraphRetrievedException;
import uk.ac.ebi.intact.application.graph2MIF.exception.NoInteractorFoundException;
import uk.ac.ebi.intact.application.graph2MIF.GraphFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Constants;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.simpleGraph.Graph;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Collection;

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
                // call  getMIF  & give out with params to retrieve data
                Graph2MIFConsole graph2MIF = new Graph2MIFConsole();
                //call getMIF to retrieve and convert
                String mif = graph2MIF.getMIF(ac, depth, strictmif);
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
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (GraphNotConvertableException e) {
            System.err.println("ERROR: Graph failed requirements of MIF. (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (NoGraphRetrievedException e) {
            System.err.println("ERROR: Could not retrieve graph from interactor (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (MIFSerializeException e) {
            System.err.println("ERROR: DOM-Object could not be serialized (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (DataSourceException e) {
            System.err.println("ERROR: IntactHelper could not be created (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (NoInteractorFoundException e) {
            System.err.println("ERROR: No Interactor found for this ac (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }

    }

    /**
     * getMIF is the only method which is necessary for access.
     * @param ac String ac in IntAct
     * @param depth Integer of the depth the graph should be expanded
     * @return String including a XML-Document in PSI-MIF-Format
     * @exception IntactException thrown if search for interactor failed
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.GraphNotConvertableException thrown if Graph failed requirements of MIF.
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.NoGraphRetrievedException thrown if DOM-Object could not be serialized
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.MIFSerializeException thrown if IntactHelper could not be created
     * @exception uk.ac.ebi.intact.application.graph2MIF.exception.NoInteractorFoundException thrown if no Interactor found for ac
     */
    private String getMIF(String ac, Integer depth, Boolean strictmif)
            throws IntactException,
            GraphNotConvertableException,
            NoGraphRetrievedException,
            MIFSerializeException,
            NoInteractorFoundException {
        // the serialised DOM-Object - so the returned string.
        String mif = "";
        Graph graph = new Graph();
        // retrieve Graph
        graph = GraphFactory.getGraph(ac, depth); //NoGraphRetrievedExceptioni, IntactException and NoInteractorFoundException possible
        //convert graph to DOM Object
        Graph2FoldedMIF convert = new Graph2FoldedMIF(strictmif);
        Document mifDOM = null;
        mifDOM = convert.getMIF(graph); // GraphNotConvertableException possible
        // serialize the DOMObject
        StringWriter w = new StringWriter();
        OutputFormat of = new OutputFormat(mifDOM, "UTF-8", true); //(true|false) for (un)formated  output
        XMLSerializer xmls = new XMLSerializer(w, of);
        try {
            xmls.serialize(mifDOM);
        } catch (IOException e) {
            System.out.println("msg:" + e.getMessage());
            throw new MIFSerializeException();
        }
        mif = w.toString();
        //return the PSI-MIF-XML
        return mif;
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