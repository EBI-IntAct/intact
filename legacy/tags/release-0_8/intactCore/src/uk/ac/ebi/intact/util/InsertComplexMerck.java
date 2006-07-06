/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.apache.commons.cli.*;

/**
 * Insert binary iteraction data (customize to MERCK format).
 * Custom version for IntAct Merck installation for initial * insertion of Merck and Mint data.
 * Data is read from an input text file.
 *
 * Input file format:
 * Line records, elements are space-delimited:
 * Prey   Bait ExperimentLabel addionalInformation
 * * Q05524 P00330 hyb Various data additions, e.g. full Mint record.
 * * The experimentLabel may be a pubmed id.
 * IMPORTANT! The order is prey bait
 *
 * @author Henning Hermjakob, hhe@ebi.ac.uk
 * @author Samuel Kerrien, skerrien@ebi.ac.uk
 *
 * @version $Id$
 */
public class InsertComplexMerck {

    // plateform independant '\n'
    public static final String NEW_LINE = System.getProperty("line.separator");

    IntactHelper helper;
    UpdateProteinsI proteinFactory;

    /**
     * All proteins which have been created for the current complex.
     */
    HashMap createdProteins = null;

    /**
     * The owner (EBI) of the DB - kept here to avoid lots of
     * unnecessary DB calls. Needs to be set for everything anyway
     */
    private Institution owner;

    /**
     * The BioSource used for everything - kept here to avoid lots of
     * unnecessary DB calls. Needs to be set for everything anyway
     */
    private BioSource bioSource;

    private CvDatabase uniprot;
    private CvDatabase pubmed;
    CvComponentRole bait, prey;

    /**
     * The interaction type used for everything - kept here to avoid lots of
     * unnecessary DB calls. Needs to be set for everything anyway
     */
    private CvInteractionType cvInteractionType;


    /**
     * basic constructor - sets up intact helper and protein factory
     */
    public InsertComplexMerck() throws Exception {
        try {
            helper = new IntactHelper();
        }
        catch (IntactException ie) {

            //something failed with type map or datasource...
            String msg = "unable to create intact helper class";
            System.out.println( msg );
            ie.printStackTrace();
        }

        collectCommonObject();

        try {
            proteinFactory = new UpdateProteins( helper );

            // Transactions are controlled by this class, not by UpdateProteins.
            // Set local transaction control to false.
            proteinFactory.setLocalTransactionControl( false );
        }
        catch ( UpdateProteinsI.UpdateException e) {
            //something failed with type map or datasource...
            String msg = "unable to create protein factory";
            System.out.println( msg );
            e.printStackTrace();
        }
    }

    private void collectCommonObject() throws IntactException {

        owner = (Institution) helper.getObjectByLabel( Institution.class, "EBI" );
        if ( owner == null ){
            throw new IntactException ( "Could not find the Institution: EBI. Stop processing." );
        }

        uniprot = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, "uniprot" );
        if ( uniprot == null ){
            throw new IntactException ( "Could not find the CvDatabase: uniprot. Stop processing." );
        }

        pubmed = (CvDatabase) helper.getObjectByLabel ( CvDatabase.class, "pubmed" );
        if ( pubmed == null ){
            throw new IntactException ( "Could not find the CvDatabase: pubmed. Stop processing." );
        }

        bait = (CvComponentRole) helper.getObjectByLabel( CvComponentRole.class, "bait" );
        if ( bait == null ){
            throw new IntactException ( "Could not find the CvComponentRole: bait. Stop processing." );
        }

        prey = (CvComponentRole) helper.getObjectByLabel( CvComponentRole.class, "prey" );
        if ( prey == null ){
            throw new IntactException ( "Could not find the CvComponentRole: prey. Stop processing." );
        }
    }


    /**
     * Retreive a collection of proteins from IntAct, the criteria is a SPTR accession number.
     * <br>
     * A distinction is done on the accession number because it can be a protein or s splice
     * variant.
     * <pre>
     * eg: P123456 is a protein
     *     P123456-2 is a splice variant of P123456
     * </pre>
     * In the case of a protein, they are searched in Intact by cross reference,
     * however, splice variants are searched by shortlabel.
     *
     * @param sptrAC SPTR accession number
     * @return Collection of Protein found.
     * @throws IntactException
     */
    private Collection getProteinsBySPTRAccessionNumber ( String sptrAC ) throws IntactException {
        Collection proteins = null;

        if ( isSpliceVariantAC( sptrAC ) ) {
            // this is a splice variant
            proteins = new ArrayList( 1 );
            Protein protein = (Protein) helper.getObjectByLabel( Protein.class, sptrAC );
            if ( protein != null )
                proteins.add( protein );
        } else {
            // this is a protein
            proteins = helper.getObjectsByXref( Protein.class, sptrAC );
        }

        return proteins;
    }

    /**
     * Check if the given ac describe a splice variant of a master protein.
     * <br>
     * It basically checks if there if a '-' in the AC, if so, that's a splice variant.
     *
     * @param ac the Accession number to check
     * @return true if it is a splice variant accession number, else false.
     */
    private boolean isSpliceVariantAC( String ac ) {
        int index = ac.indexOf( "-" ); // search for splice variant (eg: P123456-1)
        if ( index != -1 )
            return true;
        else
            return false;
    }

    /**
     * Check if a protein (either master of splice variant) can be described by the given SPTR accession number.
     *
     * @param protein the Protein we are checking in
     * @param ac the ac with
     * @return true if the given protein can be described by the given SPTR accession number, else false.
     */
    private boolean isThatACBelongsToThatProtein( Protein protein, String ac ) {
        if ( isSpliceVariantAC( ac ) ) {
            // Check for the shortLabel
            if ( protein.getShortLabel().equals( ac ) )
                return true;
        } else {
            // check for a SPTR cross reference qith the ac as promaryId
            for ( Iterator iterator = protein.getXrefs ().iterator (); iterator.hasNext (); ) {
                Xref xref = (Xref) iterator.next ();
                if ( xref.getCvDatabase().equals( uniprot ) && xref.getPrimaryId().equals( ac ))
                    return true;
            }
        }

        return false;
    }

    /**
     * Insert a Component object linking an Interactor to an Interaction.
     *
     * @param interaction The interaction to add the Interactor to
     * @param spAc Swiss-Prot accession number of the Protein to add.
     *             If the protein does not yet exist, it will be created.
     * @param taxId The tax Id of the target proteins.
     * @param role Role of the protein in the interaction.
     * @throws Exception
     */
    public void insertComponent(Interaction interaction,
                                String spAc,
                                String taxId,
                                CvComponentRole role) throws Exception {

        Collection proteins = null;

        // The relevant proteins might already have been created for the current complex.
        if (createdProteins.containsKey( spAc )) {
            proteins = (Collection) createdProteins.get( spAc );
        }
        else {
            proteins = getProteinsBySPTRAccessionNumber( spAc );

            if ( 0 == proteins.size() ) {
                // * If the protein does not exist, create it

                // if it is an uniprot protein, create a full protein object
                proteins.addAll(proteinFactory.insertSPTrProteins( spAc, taxId, true ));

                System.err.print("P");

                // if it looks like an sgd protein, create it with an xref to sgd
                if ((0 == proteins.size()) && (spAc.substring(0, 1 ).equals ( "S" )) ) {
                    proteins.add ( proteinFactory.insertSPTrProteins( spAc, taxId, true ) );
                }
            }
            else {
                System.err.print( "p" );
            }

            // Save the created proteins for further use
            createdProteins.put( spAc, proteins );
        }

        Protein targetProtein = null;
        // Filter for the correct protein
        for (Iterator i = proteins.iterator(); i.hasNext();) {
            Protein tmp = (Protein) i.next();
            if (tmp.getBioSource().getTaxId().equals( taxId ) && isThatACBelongsToThatProtein( tmp, spAc ) ) {
                if (null == targetProtein) {
                    targetProtein = tmp;
                }
                else {
                    // should not happen
                    throw new IntactException("More than one target protein found for: " + spAc );
                }
            }
        }

        if (null == targetProtein) {
            throw new IntactException("No target protein found for: " + spAc );
        }

        // Complete the component
        Component component = new Component( owner, interaction, targetProtein, role );

        persist( component );
    }


    /**
     * Choose for the user if an update or a create is needed to persist the current state
     * of the object o in the database.
     *
     * @param o the object to persist, a mapping MUST exist for that object type.
     * @throws IntactException
     */
    private void persist( Object o ) throws IntactException {
        if ( helper.isPersistent( o ) ) {
            helper.update( o );
        } else {
            helper.create( o );
        }
    }

    /**
     * Inserts a complex into the database
     * If the complex already exists, it is skipped!
     *
     * @param baitAC Swiss-Prot accession number of the bait protein.
     * @param preyACs Swiss-Prot accession numbers of the prey proteins.
     * @param experimentLabel The short label of the experiment the complex
     belongs to.
     * @throws Exception
     */
    public void insertComplex(String baitAC,
                              ArrayList preyACs,
                              String experimentLabel,
                              String commentTopicLabel,
                              String commentTopicDescription
                              ) throws Exception {
        // Get experiment
        Experiment experiment = (Experiment) helper.getObjectByLabel( Experiment.class, experimentLabel );
        if( null == experiment ) {
            // doesn't exists, create it.
            experiment = new Experiment( owner, experimentLabel, bioSource );

            persist( experiment );

            // Create the pubmed reference if the experiment Label is just a number.
            try {
                Integer.parseInt( experimentLabel );
                Xref pubmedRef = new Xref( owner, pubmed, experimentLabel, null, null, null );

                persist( pubmedRef );

                experiment.addXref( pubmedRef );

                persist( experiment );
            } catch (Exception e) {
                // tough luck, we don't create that Xref and keep it quiet ...
            }
        }

        // Get Interaction
        // The label is the first two letters of the experiment label plus the interaction number
        String actLabel = baitAC + "-" + preyACs.get( 0 );
        Interaction interaction = (Interaction) helper.getObjectByLabel( Interaction.class, actLabel );
        if ( null == interaction ) {
            Collection experiments = new ArrayList();
            experiments.add( experiment );

            Collection components = new ArrayList();

            interaction = new InteractionImpl( experiments, components, cvInteractionType, actLabel, owner );
            persist( interaction );

            // Initialise list of proteins created
            createdProteins = new HashMap();

            // add bait

            insertComponent( interaction, baitAC, bioSource.getTaxId(), bait);

            // add preys
            for (int i = 0; i < preyACs.size(); i++) {
                String preyAc = (String) preyACs.get( i );
                insertComponent( interaction, preyAc, bioSource.getTaxId(), prey );
            }
            // Add auxiliary data as comment
            if ( commentTopicDescription != null && ! commentTopicDescription.trim().equals( "" ) ) {
                CvTopic topic = (CvTopic) helper.getObjectByLabel( CvTopic.class, commentTopicLabel );
                Annotation comment = new Annotation( owner, topic );
                comment.setAnnotationText( commentTopicDescription );
                persist( comment );
                interaction.addAnnotation( comment );
            }

            // link interaction to experiment
            experiment.addInteraction( interaction );

            helper.update( interaction );

            System.err.print("C");
        }
        else {
            System.err.print("c");
        }

        persist( experiment );
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

        // get a valid Biosource from either Intact or Newt
        BioSourceFactory bioSourceFactory = new BioSourceFactory( helper, owner );
        bioSource = bioSourceFactory.getValidBioSource( taxId );

        if ( bioSource == null ) {
            System.out.println ( "Cound not find that bioSource (taxid="+ taxId +"). exit..." );
            return;
        }

        cvInteractionType = (CvInteractionType) helper.getObjectByLabel(CvInteractionType.class, interactionType);
        if ( cvInteractionType == null ) {
            System.out.println ( "Cound not find that interaction type (cvInteractionType.shortlabel="+
                                 interactionType +"). exit..." );
            return;
        }

        // TODO: REFACTOR THIS TO USE THE BeanReader

        FileReader fr       = null;
        BufferedReader file = null;
        try {
            fr = new FileReader( filename );
            file = new BufferedReader( fr );

            String line;
            int lineCount = 0;

            System.out.print("Lines processed: ");

            while (null != (line = file.readLine())) {

                if ( line.trim().equals("") ) continue; // skip blank line

                // Tokenize lines
                StringTokenizer st = new StringTokenizer( line );
                ArrayList preys = new ArrayList();
                preys.add( st.nextToken() );
                String baitAC = st.nextToken();
                String experimentLabel = st.nextToken();

                // Read the remainder of the line
                StringBuffer comment = new StringBuffer();
                while (st.hasMoreTokens()) {
                    comment.append(st.nextToken());
                    comment.append(" ");
                }

                // Insert results into database
                try {
                    insertComplex( baitAC, preys, experimentLabel, "remark", comment.toString() );
                }
                catch (Exception ie) {
                    ie.printStackTrace();
                    System.err.println();
                    System.err.println( "Error while processing input line lineCount: " );
                    System.err.println( line );
                    System.err.println( ie.getMessage() );
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
        } finally {
            // close opened streams.
            if(file != null) {
                try {
                    file.close();
                } catch( IOException ioe) {}
            }
            if( fr != null ){
                try {
                    fr.close();
                } catch( IOException ioe) {}
            }
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

    private static void displayUsage( Options options ) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "InsertComplexMerck -file <filename> " +
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
     * <p>
     * Format of the file: One line describe one binary interaction as follow,
     *                     PREY BAIT EXPERIMENT DESCRIPTION,
     *                     PREY and BAIT are swiss-prot ACs,
     *                     EXPERIMENT is preferably a PUBMED id,
     *                     DESCRIPTION is the rest of the line (stored as an Annotation).
     * </p>
     *
     * <p>
     * Warning: there is no managment of transaction isolation, they have been removed because
     *          nothing were written in the database.
     *          So, if anything goes wrong during the loading, the program doesn't stop and
     *          some objects may hang around.
     * </p>
     *
     * <p>
     * Usage of the program:
     *
     *          InsertComplexMerck -file <your_file> -taxId <taxId> -interactionType <type>
     * </p>
     *
     * <p>
     * Developper note: the command line interface uses common-cli (jakarta)
     * </p>
     *
     * @param args [0] InputFileName,
     *             [1] the tax id of the target proteins,
     *             [2] the interaction type of the experiment.
     *
     * @throws Exception for any errors.
     */
    public static void main(String[] args) throws Exception {

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

            InsertComplexMerck tool = new InsertComplexMerck();
            tool.insert( filename, taxid, interactionType );
            tool.closeHelper();
        }
        catch( ParseException exp ) {
            // Oops, something went wrong

            displayUsage(options);

            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
            System.exit( 1 );
        } catch (Exception e) {
            e.printStackTrace();
            System.exit( 1 );
        }

        System.exit( 0 );
    }
} // InsertComplexMerck