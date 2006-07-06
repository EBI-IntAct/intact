package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

/**
 * Utility class to perform some sanity checks on the DB. Mainly for use by curators.
 * A report of anomolies detected (as per the list of checks) is sent via email to
 * the appropriate people.
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class SanityChecker {

    //Keep the helper at object level - may need it for other tests later
    private PrintWriter writer;

    //Holds the statements for finding userstamps in various tables
    private PreparedStatement expStmt;
    private PreparedStatement intStmt;

    //holds the accumulated results of the test executions
    private StringBuffer expCheck;
    private StringBuffer expCheckNoPubmed;
    private StringBuffer expCheckNoPubmedWithPrimaryReference;

    private StringBuffer interactionCheck;


    private StringBuffer interactionWithMixedComponentCategoriesCheck;
    private StringBuffer interactionWithNoCategoriesCheck;

    private StringBuffer interactionWithNoBaitCheck;
    private StringBuffer interactionWithNoPreyCheck;

    private StringBuffer interactionWithNoTargetCheck;
    private StringBuffer interactionWithNoAgentCheck;
    private StringBuffer interactionWithOnlyOneNeutralCheck;

    private StringBuffer interactionWithProteinCountLowerThan2;

    private StringBuffer interactionWithSelfProteinAndStoichiometryLowerThan2;
    private StringBuffer interactionWithMoreThan2SelfProtein;

    private StringBuffer singleProteinCheck;
    private StringBuffer noProteinCheck;

    //The Experiments and Interactions - may be used in more than one test
    private Collection experiments;
    private Collection interactions;

    private final String NEW_LINE = System.getProperty( "line.separator" );



    // TODO: We could check if some Xrefs are orphan (check is the parent_ac is found in IA_INTERACTOR, IA_EXPERIMENT, IA_CONTROLLEDVOCAB)


    public SanityChecker(IntactHelper helper, PrintWriter writer) throws IntactException, SQLException {
        this.writer = writer;

        //set up statements to get user info...
        //NB remember the Connection belongs to the helper - don't close it anywhere but
        //let the helper do it at the end!!
        Connection conn = helper.getJDBCConnection();
        intStmt = conn.prepareStatement("SELECT userstamp, timestamp FROM ia_interactor WHERE ac=?");
        expStmt = conn.prepareStatement("SELECT userstamp, timestamp FROM ia_experiment WHERE ac=?");

        //get the Experiment and Interaction info from the DB for later use..
        experiments = helper.search(Experiment.class.getName(), "ac", "*");
        interactions = helper.search(Interaction.class.getName(), "ac", "*");

        //initialize buffers that will accumulate the test results..
        expCheck = new StringBuffer("Experiments with no Interactions" +
                                    "\n" + "------------------------------------------------" + NEW_LINE);

        expCheckNoPubmed = new StringBuffer("Experiments with no pubmed id" +
                                            "\n" + "------------------------------------------------" + NEW_LINE);

        expCheckNoPubmedWithPrimaryReference =
                new StringBuffer("Experiments with no pubmed id (with 'primary-reference' as qualifier)" +
                                 "\n" + "------------------------------------------------" + NEW_LINE);

        interactionCheck = new StringBuffer("Interactions with no Experiment" +
                                            "\n" + "------------------------------------------------" + NEW_LINE);

        interactionWithNoCategoriesCheck =
                new StringBuffer("Interactions with no categories (bait-prey, target-agent, neutral, complex, self, unspecified)" +
                                 "\n" + "---------------------------------------------------------------------" + NEW_LINE);

        interactionWithMixedComponentCategoriesCheck =
                new StringBuffer("Interactions with mixed categories (bait-prey, target-agent, neutral, complex, self, unspecified)" +
                                 "\n" + "---------------------------------------------------------------------" + NEW_LINE);

        interactionWithNoBaitCheck = new StringBuffer("Interactions with no bait" +
                                                      "\n" + "------------------------------------------------" + NEW_LINE);

        interactionWithNoPreyCheck = new StringBuffer("Interactions with no prey" +
                                                      "\n" + "------------------------------------------------" + NEW_LINE);

        interactionWithNoTargetCheck = new StringBuffer("Interactions with no target" +
                                                        "\n" + "------------------------------------------------" + NEW_LINE);

        interactionWithNoAgentCheck = new StringBuffer("Interactions with no agent" +
                                                       "\n" + "------------------------------------------------" + NEW_LINE);

        interactionWithOnlyOneNeutralCheck =
                new StringBuffer("Interactions with only one neutral component" +
                                 "\n" + "------------------------------------------------" + NEW_LINE);

        interactionWithProteinCountLowerThan2 = new StringBuffer("Interactions with less than 2 proteins (Role = complex)" +
                                    "\n" + "------------------------------------------------" + NEW_LINE);

        interactionWithSelfProteinAndStoichiometryLowerThan2 =
                new StringBuffer("Interactions with protein having their role set to self and its stoichiometry lower than 2.0" +
                                 "\n" + "------------------------------------------------" + NEW_LINE);

        interactionWithMoreThan2SelfProtein =
                new StringBuffer("Interactions with more than one protein having their role set to self" +
                                 "\n" + "------------------------------------------------" + NEW_LINE);

        singleProteinCheck = new StringBuffer("Interactions with only One Protein" +
                                              "\n" + "------------------------------------------------" + NEW_LINE);

        noProteinCheck = new StringBuffer("Interactions with No Components" +
                                          "\n" + "------------------------------------------------" + NEW_LINE);


    }

    /*--------------------- Work Methods ------------------------------------------------
    *
    * Checks We have so far:
    * -----------------------
    *
    *   1. Any Experiment lacking a PubMed ID
    *   2. Any PubMed ID in Experiment DBXref without qualifier=Primary-reference
    *   3. Any Interaction containing a bait but not a prey protein
    *   4. Any Interaction containing a prey but not a bait protein
    *   5. Any interaction with no protein attached
    *   6. Any interaction with 1 protein attached, stoichiometry=1
    *   7. Any Interaction missing a link to an Experiment
    *   8. Any experiment with no Interaction linked to it
    *
    * To perform these checks we need to enhance the Helper/persistence code to
    * handle more complex queries, ie to be able to build Criteria and Query objects
    * probably used in OJB (easiest to do). This is going to be needed anyway so
    * that we can handle more complex search queries later....
    *
    */

    /**
     * Performs checks on Experiments.
     * @throws IntactException Thrown if there was a Helper problem
     * @throws SQLException Thrown if there was a DB access problem
     */
    public void checkExperiments() throws IntactException, SQLException  {

        System.out.println ( "Checking on Experiment (rule 8) ..." );

        //check 8
        for(Iterator it = experiments.iterator(); it.hasNext();) {
            Experiment exp = (Experiment)it.next();
            if(exp.getInteractions().size() < 1) {
                //record it.....
                getUserInfo(expCheck, exp);
            }
        }
        writeResults(expCheck);
        writer.println();

    }

    /**
     * Performs checks on Experiments.
     * @throws IntactException Thrown if there was a Helper problem
     * @throws SQLException Thrown if there was a DB access problem
     */
    public void checkExperimentsPubmedIds() throws IntactException, SQLException  {

        System.out.println ( "Checking on Experiment and their pubmed IDs (rules 1 and 2) ..." );

        //check 1 and 2
        for(Iterator it = experiments.iterator(); it.hasNext();) {
            Experiment exp = (Experiment)it.next();
            int pubmedCount = 0;
            int pubmedPrimaryCount = 0;
            Collection Xrefs = exp.getXrefs();
            for ( Iterator iterator = Xrefs.iterator (); iterator.hasNext (); ) {
                Xref xref = (Xref) iterator.next ();
                if (xref.getCvDatabase().getShortLabel().equals("pubmed")) {
                    pubmedCount++;
                    if (xref.getCvXrefQualifier().getShortLabel().equals("primary-reference")) {
                        pubmedPrimaryCount++;
                    }
                }
            }

            if(pubmedCount == 0) {
                //record it.....
                getUserInfo(expCheckNoPubmed, exp);
            }

            if (pubmedPrimaryCount != 1) {
                //record it.....
                getUserInfo(expCheckNoPubmedWithPrimaryReference, exp);
            }
        }

        writeResults(expCheckNoPubmed);
        writeResults(expCheckNoPubmedWithPrimaryReference);
        writer.println();

    }

    /**
     * Performs Interaction checks.
     * @exception uk.ac.ebi.intact.business.IntactException thrown if there was a search problem
     */
    public void checkInteractions() throws IntactException, SQLException  {

        System.out.println ( "Checking on Interactions (rule 7) ..." );

        //check 7
        for (Iterator it = interactions.iterator(); it.hasNext();) {
            Interaction interaction = (Interaction) it.next();

            if (interaction.getExperiments().size() < 1) {
                //record it.....
                getUserInfo(interactionCheck, interaction);
            }
        }
        //now dump the results...
        writeResults(interactionCheck);
        writer.println();

    }

    /**
     * Performs Interaction checks.
     * @exception uk.ac.ebi.intact.business.IntactException thrown if there was a search problem
     */
    public void checkInteractionsBaitAndPrey() throws IntactException, SQLException  {

        System.out.println ( "Checking on Interactions (rule 6) ..." );

        //check 7
        for (Iterator it = interactions.iterator(); it.hasNext();) {
            Interaction interaction = (Interaction) it.next();

            Collection components = interaction.getComponents();
            int preyCount        = 0,
                baitCount        = 0,
                agentCount       = 0,
                targetCount      = 0,
                neutralCount     = 0,
                selfCount        = 0,
                complexCount     = 0,
                unspecifiedCount = 0;
            float selfStoichiometry = 0;
            float neutralStoichiometry = 0;

            for ( Iterator iterator = components.iterator (); iterator.hasNext (); ) {
                Component component = (Component) iterator.next ();
                //record it.....

                if (component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "bait" )) {
                    baitCount++;
                } else if (component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "prey" )) {
                    preyCount++;
                }  else if (component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "target" )) {
                    targetCount++;
                } else if (component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "agent" )) {
                    agentCount++;
                } else if (component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "neutral" )) {
                    neutralCount++;
                    neutralStoichiometry = component.getStoichiometry();
                } else if (component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "self" )) {
                    selfCount++;
                    selfStoichiometry = component.getStoichiometry();
                } else if (component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "complex" )) {
                    complexCount++;
                } else if (component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "unspecified" )) {
                    unspecifiedCount++;
                }
            }


            /**
             * We have to consider Components as 3 distinct groups: bait-prey, agent-target and neutral
             * We are not allowed to mix categories,
             * if you have a bait you must have at least one prey
             * if you have a target you must have at least one agent ----- NOT DONE YET
             * if you have neutral component you must have at least 2
             * if you have complex you must have at least 2
             * if you have self you must have only one protein with Stochiometry >= 2
             */

            int baitPrey    = (baitCount + preyCount > 0 ? 1 : 0);
            int targetAgent = (targetCount + agentCount > 0 ? 1 : 0);
            int neutral     = (neutralCount > 0 ? 1 : 0);
            int self        = (selfCount > 0 ? 1 : 0);
            int complex     = (complexCount > 0 ? 1 : 0);
            int unspecified = (unspecifiedCount > 0 ? 1 : 0);

            int categoryCount = baitPrey + targetAgent + neutral + self + complex + unspecified;

            switch ( categoryCount ) {
                case 0:
                    // none of those categories
                    getUserInfo(interactionWithNoCategoriesCheck, interaction);
                    break;

                case 1:
                    // exactly 1 category
                    if (baitPrey == 1) {
                        // bait-prey
                        if ( baitCount == 0 ) {
                            getUserInfo(interactionWithNoBaitCheck, interaction);
                        } else if ( preyCount == 0 ) {
                            getUserInfo(interactionWithNoPreyCheck, interaction);
                        }

                    } else if (targetAgent == 1) {
                        // target-agent
                        if ( targetCount == 0 ) {
                            getUserInfo(interactionWithNoTargetCheck, interaction);
                        } else if ( agentCount == 0 ) {
                            getUserInfo(interactionWithNoAgentCheck, interaction);
                        }

                    } else if (self == 1) {
                        // it has to be > 1
                        if (selfCount > 1) {
                           getUserInfo(interactionWithMoreThan2SelfProtein, interaction);
                        } else { // = 1
                            if (selfStoichiometry < 2F) {
                                getUserInfo(interactionWithSelfProteinAndStoichiometryLowerThan2, interaction);
                            }
                        }

                    } else if (complex == 1) {
                        // it has to be > 1
                        if( complexCount < 2 ) {
                            getUserInfo(interactionWithProteinCountLowerThan2, interaction);
                        }

                    } else {
                        // neutral
                        if( neutralCount == 1) {
                           if ( neutralStoichiometry < 2 ) {
                               getUserInfo(interactionWithOnlyOneNeutralCheck, interaction);
                           }
                        }
                    }
                    break;

                default:
                    // > 1 : mixed up categories !
                    getUserInfo(interactionWithMixedComponentCategoriesCheck, interaction);

            } // switch

            // What about self or the unknown category ?
        }

        //now dump the results...
        writeResults( interactionWithNoCategoriesCheck );
        writeResults( interactionWithMixedComponentCategoriesCheck );

        writeResults( interactionWithNoAgentCheck);
        writeResults( interactionWithNoTargetCheck );

        writeResults( interactionWithNoBaitCheck );
        writeResults( interactionWithNoPreyCheck );

        writeResults( interactionWithOnlyOneNeutralCheck );
        writer.println();

    }

    /**
     * Performs checks against Proteins.
     * @throws IntactException Thrown if there were Helper problems
     * @throws SQLException thrown if there were DB access problems
     */
    public void checkProteins() throws IntactException, SQLException {

        System.out.println ( "Checking on Proteins (rules 5 and 6) ..." );

        //checks 5 and 6 (easier if done together)
        for (Iterator it = interactions.iterator(); it.hasNext();) {

            Interaction interaction = (Interaction) it.next();
            Collection components = interaction.getComponents();
            int originalSize = components.size();
            int matchCount = 0;
            Protein proteinToCheck = null;
            if (components.size() > 0) {
                Component firstOne = (Component) components.iterator().next();

                if (firstOne.getInteractor() instanceof Protein) {
                    proteinToCheck = (Protein) firstOne.getInteractor();
                    components.remove(firstOne); //don't check it twice!!
                } else {
                    //not interested (for now) in Interactions that have
                    //interactors other than Proteins (for now)...
                    return;
                }

                for (Iterator iter = components.iterator(); iter.hasNext();) {
                    Component comp = (Component) iter.next();
                    Interactor interactor = comp.getInteractor();
                    if (interactor.equals(proteinToCheck)) {
                        //check it against the first one..
                        matchCount++;
                    }
                }
                //now compare the count and the original - if they are the
                //same then we have found one that needs to be flagged..
                if (matchCount == originalSize) {
                    getUserInfo(singleProteinCheck, interaction);
                }

            } else {
                //Interaction has no Components!! This is in fact test 5...
                getUserInfo(noProteinCheck, interaction);
            }
        }

        writeResults(singleProteinCheck);
        writer.println();
        writeResults(noProteinCheck);
        writer.println();

    }

    /**
     * tidies up the DB statements.
     */
    public void cleanUp() {

        try {
            if(expStmt != null) expStmt.close();
            if(intStmt != null) intStmt.close();
        }
        catch(SQLException se) {
            System.out.println("failed to close statement!!");
            se.printStackTrace();
        }
    }

    //--------------------------- private methods ------------------------------------------

    /**
     * Helper method to obtain userstamp info from a given record, and
     * then if it has any to append the details to a result buffer.
     * @param buf The result buffer we want the info put into (may want more than one
     * result displayed for a single Intact type)
     * @param obj The Intact object that user info is required for.
     * @throws SQLException thrown if there were DB problems
     */
    private void getUserInfo(StringBuffer buf, IntactObject obj) throws SQLException {

        String user = null;
        Timestamp date = null;
        ResultSet results = null;

        if(obj instanceof Experiment) {
            expStmt.setString(1, obj.getAc());
            results = expStmt.executeQuery();

        }
        if(obj instanceof Interaction) {
            intStmt.setString(1, obj.getAc());
            results = intStmt.executeQuery();

        }
        //Connection conn = null;
        //stmt = conn.prepareStatement(sql);
        if(results.next()) {
            user = results.getString("userstamp");
            date = results.getTimestamp("timestamp");
        }

        buf.append("AC: " + obj.getAc() + "\t" + " User: " + user
                   + "\t" + "When: " + date + NEW_LINE);

    }

    /**
     * Handles dumping results to a file. If no results were found
     * then an apporpiate message is printed instead.
     * @param buf The data to be dumped to the file
     */
    private void writeResults(StringBuffer buf) {

        writer.println(buf);
        if (buf.indexOf("User") == -1) {
            //none found - write useful message
            writer.println("No matches for this test.");
            writer.println();
        }

    }

    public static void main(String[] args) throws Exception {

        IntactHelper helper = null;
        PrintWriter out = null;
        SanityChecker checker = null;

        try {

            String filename = null;
            if (args.length != 1) {

                java.util.Date date = new java.util.Date();
                SimpleDateFormat formatter = new SimpleDateFormat( "yyyy.MM.dd@HH:mm" );
                String time = formatter.format( date );
                filename = "sanityCheck-" + time + ".txt";

                System.out.println("Usage: dbCheck.sh SanityChecker <filename>");
                System.out.println("<filename> automatically set to: " + filename );
            } else {
                filename = args[0];
            }

            out = new PrintWriter(new BufferedWriter(new FileWriter( filename )));
            helper = new IntactHelper();
            System.out.println("Helper created (User: " + helper.getDbUserName() + " " +
                               "Database: " + helper.getDbName() + ")");
            System.out.println("results filename: " + filename);
            out.println("Checks against Database " + helper.getDbName());
            out.println("----------------------------------");
            out.println();
            System.out.print("checking data integrity...");
            checker = new SanityChecker(helper, out);

            long start = System.currentTimeMillis();
            //do checks here.....
            checker.checkExperiments();
            checker.checkExperimentsPubmedIds();
            checker.checkInteractions();
            checker.checkInteractionsBaitAndPrey();
            checker.checkProteins();

            long end = System.currentTimeMillis();
            long total = end - start;
            System.out.println("....Done. ");
            System.out.println();
            System.out.println("Total time to perform checks: " + total / 1000 + "s");

        }
        catch (IntactException e) {
            System.out.println("Root cause: " + e.getRootCause());
            e.printStackTrace();
            System.exit(1);
        }
        catch(EOFException fe) {
            System.err.println("End of stream");
        }
        catch(SQLException sqe) {
            System.out.println("DB error!");
            sqe.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(checker != null) checker.cleanUp();
            if (helper != null) helper.closeStore();
            if(out != null) out.close();
        }
    }

}
