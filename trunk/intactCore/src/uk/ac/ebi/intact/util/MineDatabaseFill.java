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
    // MiNe relevant database table
    private static final String INTERACTION_TABLE = "ia_interactions";
    // SQL statement to select all interactors from a specific interaction
    private static final String SELECT_INTERACTOR = "SELECT C.interactor_ac, C.role, I.objclass "
            + "FROM ia_component C, ia_interactor I WHERE C.interaction_ac=? AND C.interactor_ac = I.ac";
    // SQL statement to select the taxid for an interactor
    private static final String SELECT_TAXID = "SELECT B.taxid FROM ia_biosource B, "
            + "ia_interactor I WHERE B.ac = I.biosource_ac AND I.ac=?";
    // SQL statement to insert data in the MiNe database table
    private static final String INSERT_QUERY = "INSERT INTO "
            + INTERACTION_TABLE + " VALUES(" + "?, ?, ?, ?,1, null)";
    // SQL statement to select all interactions
    private static final String SELECT_INTERACTIONS = "SELECT ac FROM ia_interactor WHERE objclass LIKE "
            + "'%Interaction%'";
    // SQL statement to select all negativ annotated interactions
    private static final String DELETE_NEGATIV_ANNOTATION = "SELECT I.ac FROM ia_interactor I, "
            + "ia_annotation A, ia_int2annot I2A, ia_controlledvocab CV WHERE I.objclass like "
            + "'%Interaction%' AND  I.ac = I2A.interactor_ac AND I2A.annotation_ac = A.ac AND "
            + "CV.ac = A.topic_ac AND CV.shortlabel = 'negative'";
    // SQL statement to select all interactions in a negativ experiment
    private static final String DELETE_NEGATIV_EXPERIMENTS = "SELECT I2E.interaction_ac FROM ia_int2exp I2E, "
            + "ia_experiment E, ia_annotation A, ia_exp2annot E2A, ia_controlledvocab CV WHERE "
            + "I2E.experiment_ac = E.ac AND E.ac = E2A.experiment_ac AND "
            + "E2A.annotation_ac = A.ac AND CV.ac = A.topic_ac AND "
            + "CV.shortlabel = 'negative'";
    // SQL statement to delete interactions from the MiNe database table
    private static final String DELETE_FROM_TABLE = "DELETE FROM "
            + INTERACTION_TABLE + " WHERE interaction_ac=?";
    // SQL statement to get the accession number for a bait
    private static final String SELECT_BAIT_ID = "SELECT ac FROM ia_controlledvocab WHERE shortlabel='bait'";

    private static int graphid = 0;

    private static String bait_id;

    /**
     * Fills the database needed for the MiNe project. <br>
     * 
     * @throws SQLException when something failed with the database connection
     * @throws IntactException if the initiation of the intact helper failed
     */
    private static void buildDatabase() throws SQLException, IntactException {
        // the helper and the database connection is fetched
        IntactHelper helper = new IntactHelper();

        // Displays the user and instance against which we are working.
        try {
            System.out.println( "Database: " + helper.getDbName() );
            System.out.println( "User:     " + helper.getDbUserName() );
        }
        catch ( LookupException e ) {
            e.printStackTrace();
        }

        Connection con = helper.getJDBCConnection();
        Statement stm = con.createStatement();

        // get the EBI - ID for a bait
        ResultSet set = stm.executeQuery( SELECT_BAIT_ID );
        if ( set.next() ) {
            bait_id = set.getString( 1 );
            System.out.println( "bait id: " + bait_id );
        }
        else {
            System.err
                    .println( "no identifier for a bait could be found in the database !" );
            set.close();
            stm.close();
            con.close();
            System.exit( 0 );
        }
        set.close();

        // the existing data is truncated
        System.out.println( "Truncate existing table" );
        stm.executeUpdate( "TRUNCATE TABLE " + INTERACTION_TABLE );
        // the inserSTM is a statement to insert the MINE relevant data
        PreparedStatement insertDataStatement = con
                .prepareStatement( INSERT_QUERY );

        System.out.println( "insert interaction data" );
        // all interactions are fetched from the interactor table
        ResultSet interactionSet = stm.executeQuery( SELECT_INTERACTIONS );
        // statement to get all interactors with its role of a particular
        // interaction
        PreparedStatement interactorSelect = con
                .prepareStatement( SELECT_INTERACTOR );
        // statement to get the taxid for a particular interactor
        PreparedStatement taxidSelect = con.prepareStatement( SELECT_TAXID );

        // because it can happen that we want to have access to an element
        // via an index an arraylist is taken. The number of baits should be
        // small enough so no array copying is needed.
        List baits = new ArrayList();
        // because it can happen that we want to insert an element at a specific
        // position an arraylist is taken
        List interactors = new ArrayList();
        String interactionAC, bait, taxID;
        ResultSet taxIDSet;
        Collection taxIDs = new HashSet();

        int j = 0;
        // goes through every interactor which is an interaction
        while ( interactionSet.next() ) {
            // the interaction ac is stored
            interactionAC = interactionSet.getString( 1 ).toUpperCase();

            if ( j++ % 100 == 0 ) {
                System.out.print( "." );
            }
            // the lists with the interactors are cleared to reuse them for the
            // next interaction
            baits.clear();
            interactors.clear();

            // all interactors for the given interaction_ac are fetched
            getInteractors( interactors, baits, interactionAC, interactorSelect );

            // the number indicates at which position in the interactors list
            // the insertion into the MiNe table shall start
            // default value is of course 0.
            int interactorStart = 0;

            // if no bait was found
            if ( baits.isEmpty() ) {
                // the interactors are sorted alphanumerically
                Collections.sort( interactors );
                // the alphanumerically smallest is taken as bait
                bait = (String) interactors.get( 0 );
                // because the first of the interactors is taken as bait we have
                // to start with the second interactor to insert in the table
                interactorStart = 1;
            }
            // at least one bait was found
            else {
                // if there are more than one baits, they are sorted and the
                // alphanumerically smallest one is taken as bait
                if ( baits.size() > 1 ) {
                    Collections.sort( baits );
                }
                bait = (String) baits.get( 0 );
            }

            // the taxid for the bait is determined
            taxidSelect.setString( 1, bait );
            taxIDSet = taxidSelect.executeQuery();
            taxIDSet.next();
            taxID = taxIDSet.getString( 1 ).toUpperCase();
            // taxIDs is a HashSet -> so no test for duplicates are needed !
            // all taxids are stored for the computation of the different
            // connecting networks
            taxIDs.add( taxID );

            insertDataStatement.setString( 1, bait );
            insertDataStatement.setString( 3, taxID );
            insertDataStatement.setString( 4, interactionAC );
            taxIDSet.close();
            // every interactor is added with the bait as an interaction
            // in the table
            for (int i = interactorStart, n = interactors.size(); i < n; i++) {
                insertDataStatement
                        .setString( 2, (String) interactors.get( i ) );
                insertDataStatement.executeUpdate();
            }
            // if there are more than one baits, the interactions between
            // the protein used as bait and the remaining ones are added.
            for (int i = 1, n = baits.size(); i < n; i++) {
                insertDataStatement.setString( 2, (String) baits.get( i ) );
                insertDataStatement.executeUpdate();
            }
        }
        interactionSet.close();
        insertDataStatement.close();
        interactorSelect.close();
        stm.close();
        System.out.println();

        // all interaction which are annotated as negativ or which occur in a
        // negativ experiment are deleted.
        deleteInteractions( con );

        System.out.println( "Compute connecting graphs" );
        for (Iterator iter = taxIDs.iterator(); iter.hasNext();) {
            setGraphIDBio( con, (String) iter.next() );
        }
        con.close();
        System.out.println();
    }

    /**
     * Deletes all interaction in the table which are either annotated as
     * negativ interaction or occuring in a negativ experiment.
     * 
     * @param con the database connection
     * @throws SQLException if something failed on database level
     */
    private static void deleteInteractions(Connection con) throws SQLException {
        PreparedStatement deleteStm = con.prepareStatement( DELETE_FROM_TABLE );
        Statement stm = con.createStatement();
        // delete all interactions which are annotated negativ
        System.out.println( "Delete all interactions which have "
                + "negative information" );
        ResultSet set = stm.executeQuery( DELETE_NEGATIV_ANNOTATION );
        int j = 0;
        while ( set.next() ) {
            deleteStm.setString( 1, set.getString( 1 ) );
            deleteStm.executeUpdate();
            if ( j++ % 100 == 0 ) {
                System.out.print( "." );
            }
        }
        set.close();
        System.out.println();

        // delete all interactions which are part of a negative experiment
        System.out.println( "Delete all interactions which are part "
                + "of a negative experiment" );

        set = stm.executeQuery( DELETE_NEGATIV_EXPERIMENTS );
        j = 0;
        while ( set.next() ) {
            deleteStm.setString( 1, set.getString( 1 ) );
            deleteStm.executeUpdate();
            if ( j++ % 100 == 0 ) {
                System.out.print( "." );
            }
        }
        set.close();
        deleteStm.close();
        stm.close();
        System.out.println();
    }

    /**
     * Gets all interactors which take part in the same interaction identified
     * by the interaction_ac. <br>
     * If an interactor is a bait it is added to the baits list, otherwise its a
     * prey and therefore added to the preys list. <br>
     * If an interactor is an interaction the method calls itself recursively to
     * get all interactors from this interaction and so on.
     * 
     * @param preys the list storing all the preys
     * @param baits the list storing all the baits
     * @param interactionAC the accession number of the interaction
     * @param selectStm the select statement to get all interactors from one
     *            interaction
     * @throws SQLException if something failed on database level
     */
    private static void getInteractors(List preys, List baits,
            String interactionAC, PreparedStatement selectStm)
            throws SQLException {
        // set the current interaction_ac of the select statement
        selectStm.setString( 1, interactionAC );
        ResultSet resultSet = selectStm.executeQuery();

        String objClass;
        String interactor;
        while ( resultSet.next() ) {
            // the object class of the interactor is fetched
            objClass = resultSet.getString( 3 );
            // the ac of the interactor is fetched
            interactor = resultSet.getString( 1 ).toUpperCase();
            // if the objclass of the interactor is protein
            if ( objClass.indexOf( "Protein" ) != -1 ) {
                // if the interactor is a bait
                if ( bait_id.equalsIgnoreCase( resultSet.getString( 2 ) ) ) {
                    baits.add( interactor );
                }
                else {
                    preys.add( interactor );
                }
            }
            else {
                // the interactor is an interaction and therefore all
                // interactors of this interaction are fetched into the current
                // lists.
                //TODO: IS IT BETTER TO COLLECT ALL INTERACTIONS AND THEN CALL
                // THE METHOD RECURSIVELY ??? (SAM)
                getInteractors( preys, baits, interactor, selectStm );
            }
        }
        resultSet.close();
    }

    /**
     * Determines the different connecting graphs of one biosource. Every
     * connection graph gets its unique id
     * 
     * @param con the db conntection
     * @param taxid the taxid
     * @throws SQLException whether something failed with the db connection
     */
    private static void setGraphIDBio(Connection con, String taxid)
            throws SQLException {
        graphid++;
        System.out.print( "." );
        // query fetches all entries where the graphid is not set yet

        Statement stm = con.createStatement();
        // we are just interested in the first found entry
        stm.setFetchSize( 1 );
        ResultSet set = stm.executeQuery( "SELECT protein1_ac FROM "
                + INTERACTION_TABLE + " WHERE graphid IS NULL " + "AND taxid='"
                + taxid + "'" );
        // if no result is available the taxid completed
        if ( !set.next() ) {
            set.close();
            stm.close();
            return;
        }
        set.close();
        stm.close();
        String currentAC = set.getString( 1 );

        // the stack stores all accession numbers which are part of the current
        // connecting network.
        Stack acsToProcceed = new Stack();
        acsToProcceed.push( currentAC );

        PreparedStatement selectInteractors = con.prepareStatement( "SELECT "
                + "protein1_ac, protein2_ac FROM " + INTERACTION_TABLE
                + " WHERE protein1_ac =? OR protein2_ac=? " + "AND taxid='"
                + taxid + " AND graphid IS NULL" );

        PreparedStatement updateGraphid = con
                .prepareStatement( "UPDATE "
                        + INTERACTION_TABLE
                        + "SET graphID=? WHERE protein1_ac=? OR protein2_ac=? AND taxid='"
                        + taxid + "' AND graphid IS NULL" );

        String interactor1, interactor2;

        // the stack stores each element which is
        // part of the current connecting network
        // therefore: as long as there are elements in
        // the stack -> there are still elements in the connecting graph
        int j = 0;
        while ( !acsToProcceed.isEmpty() ) {
            if ( j++ % 50 == 0 ) {
                System.out.print( "." );
            }
            // get the current ac nr from the stack
            currentAC = (String) acsToProcceed.pop();
            selectInteractors.setString( 1, currentAC );
            selectInteractors.setString( 2, currentAC );

            // flag whether there are new elements in the stack
            boolean foundACs = false;

            set = selectInteractors.executeQuery();
            while ( set.next() ) {
                interactor1 = set.getString( 1 );
                interactor2 = set.getString( 2 );

                // if the found interactor is not the same as the current
                // interactor -> a new interactor was found -> the interactor is
                // pushed in the stack
                if ( !currentAC.equals( interactor1 ) ) {
                    acsToProcceed.push( interactor1 );
                    foundACs = true;
                }
                if ( !currentAC.equals( interactor2 ) ) {
                    acsToProcceed.push( interactor2 );
                    foundACs = true;
                }
            }

            set.close();

            // if new elements are in the stack we have to set the graphid for
            // all current found elements.
            // if no new elements were found no update is done so that no
            // conflicts can occur with overwriting a previous graphid.
            if ( foundACs ) {
                updateGraphid.setInt( 1, graphid );
                updateGraphid.setString( 2, currentAC );
                updateGraphid.setString( 3, currentAC );
                updateGraphid.executeUpdate();
            }
        }
        selectInteractors.close();
        updateGraphid.close();
        // the method is called recursively to check if there are more conneting
        // networks for the current taxid
        setGraphIDBio( con, taxid );
    }

    public static void main(String[] args) {
        try {
            buildDatabase();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}