package uk.ac.ebi.intact.util;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;

import java.util.Collection;
import java.util.Iterator;
import java.io.EOFException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.sql.*;

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
    private IntactHelper helper;
    private PrintWriter writer;

    //Holds the statements for finding userstamps in various tables
    private PreparedStatement expStmt;
    private PreparedStatement intStmt;

    //holds the accumulated results of the test executions
    private StringBuffer expCheck;
    private StringBuffer expCheckNoPubmed;
    private StringBuffer expCheckNoPubmedWithPrimaryReference;
    private StringBuffer interactionCheck;
    private StringBuffer interactionWithNoBaitCheck;
    private StringBuffer interactionWithNoPreyCheck;
    private StringBuffer interactionWithNeitherPreyNorBaitCheck;
    private StringBuffer singleProteinCheck;
    private StringBuffer noProteinCheck;

    //The Experiments and Interactions - may be used in more than one test
    private Collection experiments;
    private Collection interactions;

    private final String NEW_LINE = System.getProperty("line.separator");

    public SanityChecker(IntactHelper helper, PrintWriter writer) throws IntactException, SQLException {
        this.helper = helper;
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
        expCheckNoPubmedWithPrimaryReference = new StringBuffer("Experiments with no pubmed id (with 'primary-reference' as qualifier)" +
                "\n" + "------------------------------------------------" + NEW_LINE);
        interactionCheck = new StringBuffer("Interactions with no Experiment" +
                "\n" + "------------------------------------------------" + NEW_LINE);
        interactionWithNoBaitCheck = new StringBuffer("Interactions with no bait" +
                "\n" + "------------------------------------------------" + NEW_LINE);
        interactionWithNoPreyCheck = new StringBuffer("Interactions with no prey" +
                "\n" + "------------------------------------------------" + NEW_LINE);
        interactionWithNeitherPreyNorBaitCheck = new StringBuffer("Interactions with neither prey nor Bait" +
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

        //check 7
        for (Iterator it = interactions.iterator(); it.hasNext();) {
            Interaction interaction = (Interaction) it.next();

            Collection components = interaction.getComponents();
            int preyCount    = 0,
                baitCount    = 0,
                neutralCount = 0;
            for ( Iterator iterator = components.iterator (); iterator.hasNext (); ) {
                Component component = (Component) iterator.next ();
                //record it.....

                if (component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "bait" )) {
                    baitCount++;
                }
                if (component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "prey" )) {
                    preyCount++;
                }
                if (component.getCvComponentRole().getShortLabel().equalsIgnoreCase( "neutral" )) {
                    neutralCount++;
                }
            }

            // TODO: we have to consider Components as 3 distinct groups: bait-prey, agent-target and neutral
            // TODO: we are not allowed to mix categories,
            // TODO: if you have a bait you must have at least one prey
            // TODO: if you have a target you must have at least one agent ----- NOT DONE YET
            // TODO: else you must have at least 2 neutral components
            if ( preyCount == 0 && baitCount == 0 ) {
                if ( neutralCount <= 1 ) {
                    getUserInfo(interactionWithNeitherPreyNorBaitCheck, interaction);
                }
            } else {
                if (baitCount == 0) {
                    getUserInfo(interactionWithNoBaitCheck, interaction);
                } else if (preyCount == 0) {
                    getUserInfo(interactionWithNoPreyCheck, interaction);
                }
            }
        }
        //now dump the results...
        writeResults(interactionWithNeitherPreyNorBaitCheck);
        writeResults(interactionWithNoBaitCheck);
        writeResults(interactionWithNoPreyCheck);
        writer.println();

    }

    /**
     * Performs checks against Proteins.
     * @throws IntactException Thrown if there were Helper problems
     * @throws SQLException thrown if there were DB access problems
     */
    public void checkProteins() throws IntactException, SQLException {

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

            if (args.length != 1) {

                System.out.println("Usage: dbCheck.sh SanityChecker <filename>");
                System.exit(1);
            }

            out = new PrintWriter(new BufferedWriter(new FileWriter(args[0])));
            helper = new IntactHelper();
            System.out.println("Helper created (User: " + helper.getDbUserName() + " " +
                    "Database: " + helper.getDbName() + ")");
            System.out.println("results filename: " + args[0]);
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
