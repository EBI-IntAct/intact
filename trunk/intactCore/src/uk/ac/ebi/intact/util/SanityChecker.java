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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Utility class to perform some sanity checks on the DB. Mainly for use by curators.
 * A report of anomolies detected (as per the list of checks) is sent via email to
 * the appropriate people.
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class SanityChecker {

    private IntactHelper helper;
    private PrintWriter writer;

    public SanityChecker(IntactHelper helper, PrintWriter writer) {
        this.helper = helper;
        this.writer = writer;

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
     * Performs Experiment checks.
     * @exception uk.ac.ebi.intact.business.IntactException thrown if there was a search problem
     */
    public void checkExpsAndInts() throws IntactException {

        System.out.print("checking Experiment data...");
        String expSql = "SELECT userstamp FROM ia_experiment WHERE ac='";
        String intSql = "SELECT userstamp FROM ia_interactor WHERE ac='";
        PreparedStatement expStmt = null;
        PreparedStatement intStmt = null;
        Connection conn = null;

        try {
            conn = helper.getJDBCConnection();

            //Doing 7 and 8 first as they are the easiest......
            Collection experiments = helper.search(Experiment.class.getName(), "ac", "*");
            Collection interactions = helper.search(Interaction.class.getName(), "ac", "*");
            ResultSet results = null;
            String user = null;

            //check 8
            writer.println("Experiments with no Interactions");
            writer.println("--------------------------------");
            for(Iterator it = experiments.iterator(); it.hasNext();) {
                Experiment exp = (Experiment)it.next();
                if(exp.getInteractions().size() < 1) {
                    //record it in a file.....
                    expStmt = conn.prepareStatement(expSql + exp.getAc() + "'");
                    results = expStmt.executeQuery();
                    if(results.next()) user = results.getString("userstamp");
                    writer.println("AC: " + exp.getAc() + "\t"+ " User: " + user);
                }
            }

            //check 7
            writer.println();
            writer.println("Interactions with no Experiment");
            writer.println("--------------------------------");
            for(Iterator it = interactions.iterator(); it.hasNext();) {
                Interaction interaction = (Interaction)it.next();


                if(interaction.getExperiments().size() < 1) {
                    //record it in a file.....
                    //and also get the userStamp info...
                    intStmt = conn.prepareStatement(intSql + interaction.getAc() + "'");
                    results = intStmt.executeQuery();
                    if(results.next()) user = results.getString("userstamp");
                    writer.println("AC: " + interaction.getAc() + "\t" + " User: " + user);
                }
            }

            //check 6 (modified) - flag all interactions which have only
            //one Protein....
            writer.println();
            writer.println("Interactions with only One Protein");
            writer.println("--------------------------------");
            for(Iterator it = interactions.iterator(); it.hasNext();) {

                Interaction interaction = (Interaction)it.next();

                Collection components = interaction.getComponents();
                int originalSize = components.size();
                int matchCount = 0;
                Protein proteinToCheck = null;
                if(components.size() > 0) {
                    Component firstOne = (Component)components.iterator().next();

                    if(firstOne.getInteractor() instanceof Protein) {
                        proteinToCheck = (Protein)firstOne.getInteractor();
                        components.remove(firstOne); //don't check it twice!!
                    }
                    else {
                        //not interested (for now) in Interactions without
                        //Proteins...
                        return;
                    }

                    for(Iterator iter = components.iterator(); iter.hasNext();) {
                        Component comp = (Component)iter.next();
                        Interactor interactor = comp.getInteractor();
                        if(interactor.equals(proteinToCheck)) {
                            //check it against the first one..
                            matchCount++;
                        }
                    }
                    //now compare the count and the original - if they are the
                    //same then we have found one that needs to be flagged..
                    if(matchCount == originalSize) {
                        intStmt = conn.prepareStatement(intSql + interaction.getAc() + "'");
                        results = intStmt.executeQuery();
                        if(results.next()) user = results.getString("userstamp");
                        writer.println("AC: " + interaction.getAc() + "\t" + " User: " + user);
                    }

                }
                else {
                    //Interaction has no Components!! This is in fact test 5...
                    writer.println("Unexpected error - Interaction " + interaction.getAc()
                            + " has no Components!!");
                }

            }

            System.out.println("....Done. ");
            System.out.println();
        }
        catch(SQLException sqe) {
            sqe.printStackTrace();
        }
        finally {
            try {
                if(expStmt != null) expStmt.close();
                if(intStmt != null) intStmt.close();
            }
            catch(SQLException se) {
                System.out.println("failed to close statement!!");
                se.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws Exception {

        IntactHelper helper = null;
        PrintWriter out = null;

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
            SanityChecker checker = new SanityChecker(helper, out);

            long start = System.currentTimeMillis();
            //do checks here.....
            checker.checkExpsAndInts();

            long end = System.currentTimeMillis();
            long total = end - start;
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
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (helper != null) helper.closeStore();
            if(out != null) out.close();
        }
    }

}
