/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.BusinessConstants;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import org.apache.commons.cli.*;

/**
 * Insert complex data for Ho and Gavin publications.
 * Data is read from an input text file.
 *
 * Input file format:
 * Line records, elements are space-delimited:
 * Interaction Bait   Preys         Experiment
 * number
 * 12          Q05524 P00330 Q05524 gavin
 *
 * @author Henning Hermjakob, hhe@ebi.ac.uk
 */
public class InsertComplex {

    public static final String NEW_LINE = System.getProperty("line.separator");

    private IntactHelper helper;
    private UpdateProteins proteinFactory;

    /** All proteins which have been created for the current complex.
     */
    private HashMap createdProteins = null;

    /**
     * basic constructor - sets up intact helper and protein factory
     */
    public InsertComplex() throws Exception {
        try {
            helper = new IntactHelper();
        }
        catch (IntactException ie) {

            //something failed with type map or datasource...
            String msg = "unable to create intact helper class";
            System.err.println(msg);
            ie.printStackTrace();
        }
        try {
            proteinFactory = new UpdateProteins( helper );

            // Transactions are controlled by this class, not by UpdateProteins.
            // Set local transaction control to false.
            proteinFactory.setLocalTransactionControl( false );
        }
        catch (UpdateProteinsI.UpdateException e) {
            //something failed with type map or datasource...
            String msg = "unable to create protein factory";
            System.err.println( msg );
            e.printStackTrace();
        }
    }

    /**
     * Close the database connexion if it has been opened.
     */
    public void closeHelper() {
        try {
            if (helper != null) helper.closeStore();
        } catch ( IntactException e ) {
            e.printStackTrace ();
        }
    }

    /**
     * Insert a Component object linking an Interactor to an Interaction.
     *
     * @param act The interaction to add the Interactor to
     * @param spAc Swiss-Prot accession number of the Protein to add.
     *             If the protein does not yet exist, it will be created.
     * @param taxId The tax Id of the target proteins.
     * @param role Role of the protein in the interaction.
     * @throws Exception
     */
    public void insertComponent(Interaction act,
                                String spAc,
                                String taxId,
                                CvComponentRole role) throws Exception {

        Component comp = new Component();
        comp.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
        comp.setInteraction(act);

        Collection proteins = null;

        // The relevant proteins might already have been created for the current complex.
        if (createdProteins.containsKey(spAc)) {
            proteins = (Collection) createdProteins.get(spAc);
        }
        else {
            proteins = helper.getObjectsByXref(Protein.class, spAc);

            if (0 == proteins.size()) {
                // * If the protein does not exist, create it
                System.err.print("P");

                // if it is an sptr protein, create a full protein object
                proteins.addAll(proteinFactory.insertSPTrProteins(spAc));

                // if it looks like an sgd protein, create it with an xref to sgd
                if ((0 == proteins.size()) && (spAc.substring(0, 1).equals("S"))) {
                    proteins.add(
                            proteinFactory.insertSimpleProtein(spAc,
                                    (CvDatabase) helper.getObjectByLabel(CvDatabase.class, "sgd"),
                                    taxId));
                }
            }
            else {
                System.err.print("p");
            }

            // Save the created proteins for further use
            createdProteins.put(spAc, proteins);
        }
        Protein targetProtein = null;

        // Filter for the correct protein
        for (Iterator i = proteins.iterator(); i.hasNext();) {
            Protein tmp = (Protein) i.next();
            if (tmp.getBioSource().getTaxId().equals( taxId )) {
                if (null == targetProtein) {
                    targetProtein = tmp;
                }
                else {
                    throw new IntactException("More than one target protein found for: " + spAc);
                }
            }
        }

        if (null == targetProtein) {
            throw new IntactException("No target protein found for: " + spAc);
        }

        // Complete the component
        comp.setInteractor(targetProtein);
        comp.setCvComponentRole(role);
        helper.create(comp);
    }

    private Experiment getExperiment( String experimentLabel ) throws IntactException {

        // Get experiment from the local node
        Experiment ex = (Experiment) helper.getObjectByLabel(Experiment.class, experimentLabel);
        if (null == ex) {
            // create it
            ex = new Experiment();
            ex.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));
            helper.create(ex);
        }
        return ex;
    }

    /**
     * Inserts a complex into the database
     * If the complex already exists, it is skipped!
     *
     * @param interactionNumber The number of the interaction in the publication.
     *                          Used for the shortLabel.
     * @param bait Swiss-Prot accession number of the bait protein.
     * @param preys Swiss-Prot accession numbers of the prey proteins.
     * @param experimentLabel The short label of the experiment the complex belongs to.
     * @param taxId The tax id of the target proteins.
     * @throws Exception
     */
    public void insertComplex(String interactionNumber,
                              String bait,
                              Vector preys,
                              String taxId,
                              String experimentLabel,
                              String interactionTypeLabel) throws Exception {

        Experiment ex = getExperiment( experimentLabel );

        // Get Interaction
        // The label is the first two letters of the experiment label plus the interaction number
        String actLabel = experimentLabel.substring(0, 2) + "-" + interactionNumber;
        Interaction act = (Interaction) helper.getObjectByLabel(Interaction.class, actLabel);
        if (null == act) {
            act = new Interaction();
            // TODO: should rely on the helper to get the default Institution
            act.setOwner((Institution) helper.getObjectByLabel(Institution.class, "EBI"));

            // if requested, try to set the CvInteractionType.
            if ( interactionTypeLabel != null ) {
                try {
                    CvInteractionType cvInteractionType = (CvInteractionType) helper.getObjectByLabel(
                            CvInteractionType.class, interactionTypeLabel);
                    if ( cvInteractionType == null ) {
                        // TODO: what is the CvInteractionType is not existing.
                        System.out.println ( interactionTypeLabel + " is not known as a CvInteractionType shortLabel." );
                    } else {
                        act.setCvInteractionType( cvInteractionType );
                    }
                } catch ( IntactException ie ) {
                    // this is not mandatory, skip it.
                }
            }

            act.setShortLabel(actLabel);
            helper.create( act );

            // Initialise list of proteins created
            createdProteins = new HashMap();

            // add bait
            insertComponent(act, bait, taxId,
                    (CvComponentRole) helper.getObjectByLabel(CvComponentRole.class, "bait"));

            CvComponentRole role = (CvComponentRole) helper.getObjectByLabel(
                    CvComponentRole.class, "prey");
            // add preys
            for (int i = 0; i < preys.size(); i++) {
                String prey = (String) preys.elementAt( i );
                insertComponent( act, prey, taxId, role );
            }

            // link interaction to experiment
            ex.addInteraction( act );

            // No need to do an update here because we have created a new Interaction.
            // In fact, it is an error to do so because you can only update objects that
            // are already in the DB.
//            helper.update(act);
            System.err.print("C");
        }
        else {
            System.err.print("c");
        }
        // Only update if the object exists in the database. Since
        // the transaction is outside this method, do nothing for creation as it
        // is handled upon committing the transaction.
        // NOTE: This update causes problem with the postgres driver. It seems to be OK
        // with the oracle though. The indirection table is properly updated irrespective
        // of this statement (probably by adding interaction to the experiment).
//        if (helper.isPersistent(ex)) {
//            helper.update(ex);
//        }
    }


    /**
     *
     * @param filename the filename to parse
     * @param taxId the taxId
     * @param interactionType the CvInteractionType shortlabel which will
     *                        allow to retreive the right object from the
     *                        database and then to link it to the created
     *                        interactions.
     * @throws Exception
     */
    public void insert( String filename, String taxId, String interactionType ) throws Exception {

        // Parse input file line by line

        BufferedReader file = new BufferedReader( new FileReader( filename ) );
        String line;
        int lineCount = 0;

        System.out.print("Lines processed: ");

        while ( null != (line = file.readLine()) ) {

            // Tokenize lines
            StringTokenizer st = new StringTokenizer(line);
            String interactionNumber = st.nextToken();
            String bait = st.nextToken();
            Vector preys = new Vector();

            while ( st.hasMoreTokens() ) {
                preys.add(st.nextToken());
            }

            // remove last element from preys vector, it is the experiment identifier.
            String experimentLabel = (String) preys.lastElement();
            preys.removeElement( preys.lastElement() );

            // Insert results into database
            try {
                helper.startTransaction(BusinessConstants.OBJECT_TX);
                insertComplex( interactionNumber, bait, preys, taxId, experimentLabel, interactionType );
                helper.finishTransaction();
            }
            catch (Exception ie) {
                ie.printStackTrace();
                System.err.println();
                System.err.println("Error while processing input line: ");
                System.err.println(line);
                System.err.println(ie.getMessage());
            }

            // Progress report
            if ((++lineCount % 1) == 0) {
                System.out.print(lineCount + " ");
            }
            else {
                System.out.println(".");
            }
        }
        System.out.println( NEW_LINE );
    }

    private static void displayUsage( Options options ) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "InsertComplex -file <filename> " +
                             "-taxId <biosource.taxId> " +
                             "[-interactionType <CvInteractionType.shortLabel>]",
                             options );
    }

    private static void displayLegend( ){
        System.out.println( "Legend:" );
        System.out.println( "C: new Complex created." );
        System.out.println( "c: Complex already existing." );
        System.out.println( "P: new Protein created." );
        System.out.println( "p: Protein already existing." );
    }



    /**
     * Read complex data from flat file and insert it into the database.
     *
     * @param args the command line arguments. The first argument is the
     * InputFileName and the second argument is the the tax id of the target
     * proteins.
     * @throws Exception for any errors.
     */
    public static void main(String[] args) throws Exception{

        /* Usage: InsertComplex -file <filename>
         *                      -taxid <biosource.taxid>
         *                      [-interactionType <CvInteractionType.shortLabel>]
         */

        // create Option objects
        Option helpOpt = new Option( "help", "print this message" );

        Option filenameOpt = OptionBuilder.withArgName( "filename" )
                                          .hasArg()
                                          .withDescription( "use given buildfile" )
                                          .create( "file" );
        filenameOpt.setRequired( true );

        Option taxidOpt = OptionBuilder.withArgName( "biosource.taxid" )
                                       .hasArg()
                                       .withDescription( "taxId of the BioSource to link to that Complex" )
                                       .create( "taxId" );
        taxidOpt.setRequired( true );

        Option interactionTypeOpt = OptionBuilder.withArgName( "CvInteractionType.shortLabel" )
                                                 .hasArg()
                                                 .withDescription ( "Shortlabel of the existing " +
                                                                    "CvInteractionType to link to that Complex" )
                                                 .create( "interactionType" );
        // Not mandatory.
        // interactionTypeOpt.setRequired( true );

        Options options = new Options();

        options.addOption( helpOpt );
        options.addOption( filenameOpt );
        options.addOption( taxidOpt );
        options.addOption( interactionTypeOpt );

        // create the parser
        CommandLineParser parser = new BasicParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args, true );

            if( line.hasOption( "help" ) ) {
                displayUsage( options );
                System.exit( 0 );
            }

            // These argument are mandatory.
            String filename        = line.getOptionValue( "file" );
            String taxid           = line.getOptionValue( "taxId" );
            String interactionType = line.getOptionValue( "interactionType" );

            try {
                HttpProxyManager.setup();
            } catch ( HttpProxyManager.ProxyConfigurationNotFound proxyConfigurationNotFound ) {
                proxyConfigurationNotFound.printStackTrace ();
            }

            displayLegend( );

            InsertComplex tool = new InsertComplex();
            tool.insert( filename, taxid, interactionType );
            tool.closeHelper();
            System.exit( 0 );
        }
        catch( ParseException exp ) {
            // Oops, something went wrong

            displayUsage(options);

            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            System.exit( 1 );
        }
    }
}
