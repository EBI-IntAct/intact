/*
 * Created on 27.05.2004
 */

package uk.ac.ebi.intact.util;

import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;

import java.sql.*;
import java.util.*;

/**
 * The class <tt>MineDatabaseFill</tt> is a utility class to fill the database
 * table <tt>ia_interactions</tt> which is used for the <tt>MINE</tt>
 * application. <br>
 * It provides two methods:
 * <ol>
 * <li><tt>buildDatabase</tt>:
 * <ul>
 * <li>collects the data from the database and inserts the interaction of two
 * components into <tt>ia_interactions</tt></li>
 * </ul>
 * </li>
 * <li><tt>setGraphid</tt>:
 * <ul>
 * <li>computes the different connecting graphis in a biosource</li>
 * </ul>
 * </li>
 * </ol>
 * 
 * @author Andreas Groscurth
 */
public class MineDatabaseFill {

    public static final String INTERACTION_TABLE = "ia_interactions";

	/**
	 * Fills the database needed for the MiNe project. <br>
	 * <br>
	 * <b>Currently all interactors of an interactions which are an interaction
	 * are not inserted ! </b>
	 * 
	 * @throws SQLException when something failed with the database connection
	 * @throws IntactException if the initiation of the intact helper failed
	 */
	public static void buildDatabase() throws SQLException, IntactException {
		// the helper and the database connection is fetched
		IntactHelper helper = new IntactHelper();

        // Displays the user and instance against which we are working.
        try {
            String db = helper.getDbName();
            System.out.println( "Database: " + db );
            System.out.println( "User:     " + helper.getDbUserName() );
        } catch ( LookupException e ) {
            e.printStackTrace();
        }

        Connection con = helper.getJDBCConnection();
		Statement stm = con.createStatement();

		// the existing data is truncated
		System.out.println("Truncate existing table");
		stm.executeUpdate("TRUNCATE TABLE " + INTERACTION_TABLE );
		// the inserSTM is a statement to insert the MINE relevant data
		PreparedStatement insertStm = con
				.prepareStatement("INSERT INTO " + INTERACTION_TABLE + " VALUES("
						+ "?, ?, ?, ?,1, null)");

		System.out.println("insert interaction data");
		// all interactions are fetched from the interactor table
		String query = "SELECT ac FROM ia_interactor WHERE objclass LIKE '%Interaction%'";
		ResultSet set = stm.executeQuery(query);
		// statement to get all interactors with its role of a particular
		// interaction
		PreparedStatement selectStm = con
				.prepareStatement("SELECT C.interactor_ac, C.role FROM ia_component C, "
						+ "ia_interactor I WHERE C.interaction_ac=? AND C.interactor_ac = I.ac "
						+ "AND I.objclass LIKE '%Protein%'");
		// statement to get the taxid for a particular interactor
		PreparedStatement taxidStm = con
				.prepareStatement("SELECT B.taxid FROM ia_biosource B, "
						+ "ia_interactor I WHERE B.ac = I.biosource_ac AND I.ac=?");
		boolean bait = false;
		List interactor;
		String interAc, first, taxID;
		ResultSet subSet, taxIDSet;
		Collection taxIDs = new HashSet();
		int j = 0;
		// goes through every interactor whichis an interaction
		while (set.next()) {
			// the interaction ac is stored
			interAc = set.getString(1).toUpperCase();
			selectStm.setString(1, interAc);

			if (j++ % 100 == 0) {
				System.out.print(".");
			}
			// every interactor of the current interaction
			subSet = selectStm.executeQuery();
			interactor = new ArrayList();

			while (subSet.next()) {
				// if the interactor is the bait it is inserted into
				// the first position of the interactor list
				if ("EBI-49".equalsIgnoreCase(subSet.getString(2))) {
					interactor.add(0, subSet.getString(1).toUpperCase());
					bait = true;
				}
				else {
					interactor.add(subSet.getString(1).toUpperCase());
				}
			}
			subSet.close();
			// if no bait is found the list is sorted and the first element
			// is taken as the bait
			if (!bait) {
				Collections.sort(interactor);
			}
			// the first interactor (bait) is taken
			first = interactor.get(0).toString();
			// the taxid for the bait is determined
			taxidStm.setString(1, first);
			taxIDSet = taxidStm.executeQuery();
			taxIDSet.next();

			taxID = taxIDSet.getString(1).toUpperCase();
			if (!taxIDs.contains(taxID)) {
				taxIDs.add(taxID);
			}

			insertStm.setString(1, first);
			insertStm.setString(3, taxID);
			insertStm.setString(4, interAc);
			taxIDSet.close();
			// every interactor is added with the bait as an interaction
			// in the table
			for (int i = 1; i < interactor.size(); i++) {
				insertStm.setString(2, interactor.get(i).toString());
				insertStm.executeUpdate();
			}
		}
		set.close();
		insertStm.close();
		selectStm.close();
		// delete all interactions which are annotated negativ
		System.out
				.println("\nDelete all interactions which have negative information");
		query = "SELECT I.ac FROM ia_interactor I, ia_annotation A, ia_int2annot I2A, "
				+ "ia_controlledvocab CV WHERE I.objclass like '%Interaction%' AND "
				+ "I.ac = I2A.interactor_ac AND I2A.annotation_ac = A.ac AND "
				+ "CV.ac = A.topic_ac AND CV.shortlabel = 'negative'";
		set = stm.executeQuery(query);

		PreparedStatement deleteStm = con
				.prepareStatement("DELETE FROM "+ INTERACTION_TABLE +" WHERE interaction_ac=?");
		j = 0;
		while (set.next()) {
			deleteStm.setString(1, set.getString(1));
			deleteStm.executeUpdate();
			if (j++ % 100 == 0) {
				System.out.print(".");
			}
		}
		set.close();
		// delete all interactions which are part of a negative experiment
		System.out
				.println("\nDelete all interactions which are part of a negative experiment");
		query = "SELECT I2E.interaction_ac FROM ia_int2exp I2E, ia_experiment E, "
				+ "ia_annotation A, ia_exp2annot E2A, ia_controlledvocab CV WHERE "
				+ "I2E.experiment_ac = E.ac AND E.ac = E2A.experiment_ac AND "
				+ "E2A.annotation_ac = A.ac AND CV.ac = A.topic_ac AND "
				+ "CV.shortlabel = 'negative'";
		set = stm.executeQuery(query);
		j = 0;
		while (set.next()) {
			deleteStm.setString(1, set.getString(1));
			deleteStm.executeUpdate();
			if (j++ % 100 == 0) {
				System.out.print(".");
			}
		}
		set.close();
		deleteStm.close();
		stm.close();

		System.out.println("\nCompute connecting graphs for each biosource");
		for (Iterator iter = taxIDs.iterator(); iter.hasNext();) {
			setGraphIDBio(con, iter.next().toString(), 1);

		}
		con.close();
		System.out.println();
	}

	/**
	 * Determines the different connecting graphs of one biosource. Every
	 * connection graph gets its unique id
	 * 
	 * @param con the db conntection
	 * @param bioAcc the biosource
	 * @param graphID the id for the current graph
	 * @throws SQLException wether something failed with the db connection
	 */
	public static void setGraphIDBio(Connection con, String bioAcc, int graphID)
			throws SQLException {
		System.out.print(".");
		// query fetches all entries where the graphid is not set yet
		String query = "SELECT protein1_ac FROM "+ INTERACTION_TABLE +" WHERE graphid IS NULL "
				+ "AND taxid='" + bioAcc + "'";
		Statement stm = con.createStatement();
		ResultSet set = stm.executeQuery(query);
		// if no result is available the biosource completed
		if (!set.next()) {
			return;
		}
		String ac = set.getString(1);

		Stack stack = new Stack();
		stack.push(ac);

		PreparedStatement selectprotein1_ac = con.prepareStatement("SELECT "
				+ "protein1_ac FROM "+ INTERACTION_TABLE +" WHERE protein2_ac=? " + "AND taxid='"
				+ bioAcc + "' AND graphid IS NULL");
		PreparedStatement selectprotein2_ac = con.prepareStatement("SELECT "
				+ "protein2_ac FROM "+ INTERACTION_TABLE +" WHERE protein1_ac=? " + "AND taxid='"
				+ bioAcc + "' AND graphid IS NULL");

		PreparedStatement updatePST = con
				.prepareStatement("UPDATE "+ INTERACTION_TABLE +
						"SET graphID=? WHERE protein1_ac=? OR protein2_ac=? AND taxid='" +
						bioAcc + "' AND graphid IS NULL");

		// the stack stores each element which is
		// part of the current connection network
		// therefore: as long as there are elements in
		// the stack -> there are still elements in the connection graph
		while (!stack.isEmpty()) {
			// get the current ac nr from the stack
			ac = stack.pop().toString();
			selectprotein1_ac.setString(1, ac);
			selectprotein2_ac.setString(1, ac);

			set = selectprotein1_ac.executeQuery();
			// select all acnr which have an interaction with
			// the current ac nr and push them onto the stack
			while (set.next()) {
				String protein1_ac = set.getString(1);
				if (!stack.contains(protein1_ac)) {
					stack.push(protein1_ac);
				}
			}
			set.close();

			// select all acnr which have an interaction with
			// the current ac nr and push them onto the stack
			set = selectprotein2_ac.executeQuery();
			while (set.next()) {
				String protein2_ac = set.getString(1);
				if (!stack.contains(protein2_ac)) {
					stack.push(protein2_ac);
				}
			}

			set.close();
			updatePST.setInt(1, graphID);
			updatePST.setString(2, ac);
			updatePST.setString(3, ac);
		}
		selectprotein1_ac.close();
		selectprotein2_ac.close();
		updatePST.close();
		stm.close();
		setGraphIDBio(con, bioAcc, graphID + 1);
	}

	public static void main(String[] args) {
		try {
			buildDatabase();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}