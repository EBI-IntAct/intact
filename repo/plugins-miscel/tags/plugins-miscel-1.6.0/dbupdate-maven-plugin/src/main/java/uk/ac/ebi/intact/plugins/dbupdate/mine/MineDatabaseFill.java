/*
 * Created on 27.05.2004
 */

package uk.ac.ebi.intact.plugins.dbupdate.mine;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.CvExperimentalRole;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.persistence.dao.BaseDao;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.io.PrintStream;
import java.sql.*;
import java.util.*;

/**
 * The class <tt>MineDatabaseFill</tt> is a utility class to fill the database table <tt>ia_interactions</tt> which is
 * used for the <tt>MINE</tt> application. <br> It provides two methods: <ol> <li><tt>buildDatabase</tt>: <ul>
 * <li>collects the data from the database and inserts the interaction of two components into
 * <tt>ia_interactions</tt></li> </ul> </li> <li><tt>setGraphid</tt>: <ul> <li>computes the different connecting graphis
 * in a biosource</li> </ul> </li> </ol>
 *
 * @author Andreas Groscurth
 */
public class MineDatabaseFill {

    ///////////////////////
    // Inner class

    /**
     * Wrapper class to store the accessionNumber and the shortLabel of a protein
     */
    private static class InteractorData implements Comparable {
        private String ac;
        private String shortLabel;

        public InteractorData( String ac, String shortLabel ) {
            this.ac = ac;
            this.shortLabel = shortLabel;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo( Object o ) {
            InteractorData otherInteractor = (InteractorData) o;
            return ac.compareTo( otherInteractor.ac );
        }
    }

    //////////////////////
    // Constants

    private static final Integer DEFAULT_GRAPH_ID = 0;

    public static final String INTERACTION_TABLE = "ia_interactions";

    public static final String COLUMN_PROTEIN1_AC = "protein1_ac";
    public static final String COLUMN_SHORTLABEL1 = "shortlabel1";
    public static final String COLUMN_PROTEIN2_AC = "protein2_ac";
    public static final String COLUMN_SHORTLABEL2 = "shortlabel2";
    public static final String COLUMN_TAXID = "taxid";
    public static final String COLUMN_INTERACTION_AC = "interaction_ac";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_GRAPH_ID = "graphid";

    /**
     * SQL statement to select all interactors from a specific interaction
     */
    private static final String SELECT_INTERACTOR = "SELECT C.interactor_ac, C.experimentalrole_ac, I.objclass, I.shortLabel "
                                                    + "FROM ia_component C, ia_interactor I "
                                                    + "WHERE C.interaction_ac=? AND C.interactor_ac = I.ac";

    /**
     * SQL statement to select the taxid for an interactor
     */
    private static final String SELECT_TAXID = "SELECT B.taxid "
                                               + "FROM ia_biosource B, ia_interactor I "
                                               + "WHERE B.ac = I.biosource_ac AND I.ac=?";

    /**
     * SQL statement to insert data in the MiNe database table
     */
    private static final String INSERT_QUERY = "INSERT INTO "
                                               + INTERACTION_TABLE
                                               + " (" + COLUMN_PROTEIN1_AC
                                               + ", " + COLUMN_SHORTLABEL1
                                               + ", " + COLUMN_PROTEIN2_AC
                                               + ", " + COLUMN_SHORTLABEL2
                                               + ", " + COLUMN_TAXID
                                               + ", " + COLUMN_INTERACTION_AC
                                               + ", " + "experiment_ac"
                                               + ", " + "detectmethod_ac"
                                               + ", " + "pubmed_id"
                                               + ", " + COLUMN_WEIGHT
                                               + ", " + COLUMN_GRAPH_ID
                                               + ")"
                                               + " VALUES (?, ?, ?, ?, ?, ?,?,?,?, 1, "+DEFAULT_GRAPH_ID+")";

    /**
     * SQL statement to select all interactions
     */
    private static final String SELECT_INTERACTIONS = " SELECT INTR.ac interaction_ac   "
                                                      + "      ,EXP.ac              experiment_ac  "
                                                      + "      ,EXP.detectmethod_ac detectmethod_ac"
                                                      + "      ,XRF.primaryid       pubmed_id   "
                                                      + "  FROM IA_INTERACTOR       INTR   "
                                                      + "        ,IA_INT2EXP         I2E 	"
                                                      + "        ,IA_EXPERIMENT		 EXP	"
                                                      + "        ,IA_EXPERIMENT_XREF XRF    "     // -- to get the pub med id
                                                      + "        ,IA_CONTROLLEDVOCAB CNT1     "    //-- define the ac of the database
                                                      + "        ,IA_CONTROLLEDVOCAB CNT2    "     //-- define the qualifier for xref, ic primary-reference
                                                      + "   WHERE UPPER(INTR.objclass) 	 = 'UK.AC.EBI.INTACT.MODEL.INTERACTIONIMPL'  "
                                                      + "   AND   INTR.AC 		  	 	 = I2E.INTERACTION_AC  "
                                                      + "   AND   I2E.EXPERIMENT_AC  	 = EXP.AC             "
                                                      + "   AND   XRF.PARENT_AC	  	 	 = EXP.AC             "
                                                      + "   AND   XRF.database_AC	 	 = CNT1.AC            "
                                                      + "   AND	  UPPER(CNT1.OBJCLASS)   =  'UK.AC.EBI.INTACT.MODEL.CVDATABASE'  "
                                                      + "   AND   UPPER(CNT1.SHORTLABEL)  = 'PUBMED'   "
                                                      + "   AND   XRF.qualifier_AC	 	 = CNT2.AC   "
                                                      + "   AND   UPPER(CNT2.OBJCLASS)	 = 'UK.AC.EBI.INTACT.MODEL.CVXREFQUALIFIER'  "
                                                      + "   AND	  UPPER(CNT2.SHORTLABEL) = 'PRIMARY-REFERENCE'       ";

    /**
     * SQL statement to select all negativ annotated interactions
     */
    private static final String DELETE_NEGATIV_ANNOTATION = "SELECT I.ac "
                                                            + "FROM ia_interactor I, ia_annotation A, ia_int2annot I2A, ia_controlledvocab CV "
                                                            + "WHERE I.objclass like '%Interaction%' AND  "
                                                            + "      I.ac = I2A.interactor_ac AND "
                                                            + "      I2A.annotation_ac = A.ac AND "
                                                            + "      CV.ac = A.topic_ac AND "
                                                            + "      CV.shortlabel = 'negative'";

    /**
     * SQL statement to select all interactions in a negativ experiment
     */
    private static final String DELETE_NEGATIV_EXPERIMENTS = "SELECT I2E.interaction_ac "
                                                             + "FROM ia_int2exp I2E, ia_experiment E, ia_annotation A, ia_exp2annot E2A, ia_controlledvocab CV "
                                                             + "WHERE I2E.experiment_ac = E.ac AND "
                                                             + "      E.ac = E2A.experiment_ac AND "
                                                             + "      E2A.annotation_ac = A.ac AND "
                                                             + "      CV.ac = A.topic_ac AND "
                                                             + "      CV.shortlabel = 'negative'";

    /**
     * SQL statement to delete interactions from the MiNe database table
     */
    private static final String DELETE_FROM_TABLE = "DELETE FROM "
                                                    + INTERACTION_TABLE + " " + "WHERE "
                                                    + COLUMN_INTERACTION_AC + "=?";

    /**
     * SQL statement to select all proteins for a given interactor to get the connecting network
     */
    private static final String SELECT_PROT = "SELECT "
                                              + COLUMN_PROTEIN1_AC + ", "
                                              + COLUMN_PROTEIN2_AC + " FROM "
                                              + INTERACTION_TABLE + " WHERE ("
                                              + COLUMN_PROTEIN1_AC + "=? OR "
                                              + COLUMN_PROTEIN2_AC + "=?) AND " + "       "
                                              + COLUMN_TAXID + "=? AND " + "       "
                                              + COLUMN_GRAPH_ID + " = "+DEFAULT_GRAPH_ID;

    /**
     * SQL statement to update the table for the minimal connecting network
     */
    private static final String UPDATE_GRAPHID = "UPDATE "
                                                 + INTERACTION_TABLE + " SET " + COLUMN_GRAPH_ID
                                                 + "=? " + "WHERE (" + COLUMN_PROTEIN1_AC + "=? OR "
                                                 + COLUMN_PROTEIN2_AC + "=?) AND " + "      "
                                                 + COLUMN_TAXID + "=? AND " + "      "
                                                 + COLUMN_GRAPH_ID + " = "+DEFAULT_GRAPH_ID;

    /**
     * the graphid is initialised with 0 because with every call of the setGraphBio method graphid is increased before
     * something else happenes
     */
    private static int graphid = 0;

    /**
     * stores the EBI accession number for the shortlabel 'bait'
     */
    private static String bait_id;

    /**
     * stores all accession numbers of a connecting network which are already processed
     */
    private static Collection proccessedAcs = new HashSet();

    /**
     * Fills the database needed for the MiNe project. <br>
     *
     * @throws SQLException    when something failed with the database connection
     * @throws IntactException if the initiation of the intact helper failed
     */
    public static MineDatabaseFillReport buildDatabase(PrintStream ps) throws SQLException, IntactException, IntactTransactionException {

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        BaseDao dao = daoFactory.getBaseDao();

        // Displays the user and instance against which we are working.
        ps.println( "Database: " + dao.getDbName() );
        ps.println( "User: " + dao.getDbUserName() );

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        bait_id = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getCvObjectDao().getByPsiMiRef(CvExperimentalRole.BAIT_PSI_REF).getAc();

        MineDatabaseFillReport report = new MineDatabaseFillReport();

        // the existing data is truncated
        getCurrentConnection().createStatement().executeUpdate( "DELETE FROM " + INTERACTION_TABLE );

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        // the inserSTM is a statement to insert the MINE relevant data
        PreparedStatement insertDataStatement = getCurrentConnection()
                .prepareStatement( INSERT_QUERY );
        ps.println( "insert interaction data" );

        // all interactions are fetched from the interactor table
        ResultSet interactionSet = getCurrentConnection().createStatement().executeQuery( SELECT_INTERACTIONS );
        // statement to get all interactors with its role of a particular
        // interaction
        PreparedStatement interactorSelect = getCurrentConnection()
                .prepareStatement( SELECT_INTERACTOR );

        // statement to get the taxid for a particular interactor
        PreparedStatement taxidSelect = getCurrentConnection().prepareStatement( SELECT_TAXID );

        // because it can happen that we want to have access to an element
        // via an index an arraylist is taken. The number of baits should be
        // small enough so no array copying is needed.
        List baits = new ArrayList();
        // because it can happen that we want to insert an element at a
        // specific position an arraylist is taken
        List interactors = new ArrayList();
        String interactionAc, taxID, experimentAc, detectmethodAc, publicmedId;
        InteractorData bait;
        ResultSet taxIDSet;
        Collection<String> taxIDs = new HashSet<String>();

        int j = 0;
        // goes through every interactor which is an interaction
        while ( interactionSet.next() ) {

            // the interaction ac is stored
            interactionAc = interactionSet.getString( "interaction_ac" ).toUpperCase();
            experimentAc = interactionSet.getString( "experiment_ac" ).toUpperCase();
            detectmethodAc = interactionSet.getString( "detectmethod_ac" ).toUpperCase();
            publicmedId = interactionSet.getString( "pubmed_id" ).toUpperCase();
            if ( j > 0 && j % 100 == 0 ) {
                ps.print( "." );
            }
            if ( j > 0 && j % 7000 == 0)
                {
                    ps.println(" "+j);
            }
            j++;

            // the lists with the nteractors are cleared to reuse them for the
            // next interaction
            baits.clear();
            interactors.clear();

            // all interactors for the given interaction_ac are fetched

            getInteractors( interactors, baits, interactionAc,
                            interactorSelect );

            // the number indicates at which position in the interactors list
            // the insertion into the MiNe table shall start // default value is
            // of course 0.
            int interactorStart = 0;

            if ( baits.isEmpty() ) {
                Collections.sort( interactors );
                // the alphanumerically smallest interactor is used as bait
                bait = (InteractorData) interactors.get( 0 );
                // to avoid the first interactor which is now the bait
                // the start position for the insertion in the table is
                // increased to 1
                interactorStart = 1;
            } else {
                // if more than one bait is found they are sorted
                if ( baits.size() > 1 ) {
                    Collections.sort( baits );
                }
                // get the only one bait or if there are more than one baits
                // get the alphanumerically smallest one
                bait = (InteractorData) baits.get( 0 );
            }

            // the taxid for the bait is determined
            taxidSelect.setString( 1, bait.ac );

            taxIDSet = taxidSelect.executeQuery();
            if (taxIDSet.next())
            {
                taxID = taxIDSet.getString( "taxid" ).toUpperCase();
                // the taxid is added without testing wether the id already exists
                // because a hashSet is used !
                taxIDs.add( taxID );
            }
            else
            {
                report.addNullTaxidInteraction(bait.ac);
                taxID = null;
            }

            taxIDSet.close();

            insertDataStatement.setString( 1, bait.ac );
            insertDataStatement.setString( 2, bait.shortLabel );
            insertDataStatement.setString( 5, taxID );
            insertDataStatement.setString( 6, interactionAc );

            insertDataStatement.setString( 7, experimentAc );
            insertDataStatement.setString( 8, detectmethodAc );
            insertDataStatement.setString( 9, publicmedId );

            // for every interactor an interaction between bait and the
            // interactor is added to the table
            InteractorData prey;
            for ( int i = interactorStart, n = interactors.size(); i < n; i++ ) {
                prey = (InteractorData) interactors.get( i );
                insertDataStatement.setString( 3, prey.ac );
                insertDataStatement.setString( 4, prey.shortLabel );

                try {
                    insertDataStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // if there are more than one bait the interaction between the used
            // bait and the rest baits are inserted into the table
            for ( int i = 1, n = baits.size(); i < n; i++ ) {
                prey = (InteractorData) baits.get( i );
                insertDataStatement.setString( 3, prey.ac );
                insertDataStatement.setString( 4, prey.shortLabel );
                insertDataStatement.executeUpdate();
            }
        }
        interactionSet.close();
        insertDataStatement.close();
        interactorSelect.close();
        ps.println();

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        // delete all interactions which are in any part part of a negative
        // annotation/experiment
        deleteInteractions( ps );

        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        ps.println( "Compute connecting graphs" );
        // compute the different connecting networks for each taxid
        for (String taxID1 : taxIDs)
        {
            IntactContext.getCurrentInstance().getDataContext().beginTransaction();
            setGraphIDBio(ps, taxID1);
            IntactContext.getCurrentInstance().getDataContext().commitTransaction();
        }

        ps.println();

        return report;
    }

    /**
     * Deletes all interaction in the table which are either annotated as negativ interaction or occuring in a negativ
     * experiment.
     *
     * @throws SQLException if something failed on database level
     */
    private static void deleteInteractions( PrintStream ps ) throws SQLException {
        PreparedStatement deleteStm = getCurrentConnection().prepareStatement( DELETE_FROM_TABLE );
        Statement stm = getCurrentConnection().createStatement();
        // delete all interactions which are annotated negativ
        ps
                .println( "Delete all interactions which have negative information" );
        ResultSet set = stm.executeQuery( DELETE_NEGATIV_ANNOTATION );
        int j = 0;
        while ( set.next() ) {
            deleteStm.setString( 1, set.getString( 1 ) );
            deleteStm.executeUpdate();
            if ( j > 0 && j % 100 == 0 ) {
                ps.print( "." );

            }
            if ( j > 0 && j % 7000 == 0)
            {
                ps.println(" "+j);
            }
            j++;
        }
        set.close();
        ps.println();

        // delete all interactions which are part of a negative experiment
        ps
                .println( "Delete all interactions which are part of a negative experiment" );

        set = stm.executeQuery( DELETE_NEGATIV_EXPERIMENTS );
        j = 0;
        while ( set.next() ) {
            deleteStm.setString( 1, set.getString( 1 ) );
            deleteStm.executeUpdate();
            if ( j > 0 && j % 100 == 0 ) {
                ps.print( "." );
            }
            if ( j> 0 && j % 7000 == 0)
            {
                ps.println(" "+j);
            }
            j++;
        }
        set.close();
        deleteStm.close();
        stm.close();
        ps.println();
    }


    private static Map classCache = new HashMap( 8 );

    /**
     * Gets all interactors which take part in the same interaction identified by the interaction_ac. <br> If an
     * interactor is a bait it is added to the baits list, otherwise its a prey and therefore added to the preys list.
     * <br> If an interactor is an interaction the method calls itself recursively to get all interactors from this
     * interaction and so on.
     *
     * @param preys                the list storing all the preys
     * @param baits                the list storing all the baits
     * @param interactionAC        the accession number of the interaction
     * @param selectInteractionStm the select statement to get all interactors from one interaction
     *
     * @throws SQLException if something failed on database level
     */
    private static void getInteractors( List preys, List baits,
                                        String interactionAC,
                                        PreparedStatement selectInteractionStm )
            throws SQLException, IntactException {
        // set the current interaction_ac of the select statement
        selectInteractionStm.setString( 1, interactionAC );
        ResultSet resultSet = selectInteractionStm.executeQuery();

        String objClass;
        String interactor_ac;
        InteractorData interactorData;
        while ( resultSet.next() ) {
            // the object class of the interactor is fetched
            objClass = resultSet.getString( "objclass" );
            // the ac of the interactor is fetched
            interactor_ac = resultSet.getString( "interactor_ac" )
                    .toUpperCase();
            // if the objclass of the interactor is protein
            Class aClass = null;
            if ( classCache.containsKey( objClass ) ) {
                aClass = (Class) classCache.get( objClass );

            } else {
                try {
                    aClass = Class.forName( objClass );
                } catch ( ClassNotFoundException e ) {
                    throw new IntactException( "Found as objectclass " + objClass + "this is not an intactClass " );
                }
                classCache.put( objClass, aClass );
            }
            if ( Interaction.class.isAssignableFrom( aClass ) ) {
                // the interactor is an interaction and therefore all
                // interactors of this interaction are fetched into the current
                // lists.
                getInteractors( preys, baits, interactor_ac, selectInteractionStm );
            } else if ( Interactor.class.isAssignableFrom( aClass ) ) {
//            if ( objClass.indexOf( "Protein" ) != -1 ) {
                // if the interactor is a bait
                interactorData = new InteractorData( interactor_ac, resultSet
                        .getString( "shortLabel" ) );
                if ( bait_id.equalsIgnoreCase( resultSet.getString( "experimentalrole_ac" ) ) ) {
                    baits.add( interactorData );
                } else {
                    preys.add( interactorData );
                }
            } else {
                throw new IllegalArgumentException( "Unsupported class " + objClass );
            }
//            else {
//                // the interactor is an interaction and therefore all
//                // interactors of this interaction are fetched into the current
//                // lists.
//                getInteractors( preys , baits , interactor_ac , selectInteractionStm );
//            }
        }
        resultSet.close();
    }

    /**
     * Determines the different connecting graphs of one biosource. Every connection graph gets its unique id
     *
     * @param con   the db conntection
     * @param taxid the taxid
     *
     * @throws SQLException whether something failed with the db connection
     */
    private static void setGraphIDBio( PrintStream ps, String taxid )
            throws SQLException {
        ps.print( "." );

        if (graphid > 0 && graphid % 70 == 0)
        {
            ps.println(" "+graphid);
        }

        graphid++;
        proccessedAcs.clear();
        // query fetches all entries where the graphid is not set yet
        Statement stm = getCurrentConnection().createStatement();
        ResultSet set = stm.executeQuery( "SELECT protein1_ac FROM "
                                          + INTERACTION_TABLE
                                          + " WHERE graphid IS NULL AND taxid='" + taxid + "'" );
        // if no result is available the biosource completed
        if ( !set.next() ) {
            return;
        }
        String currentAc = set.getString( 1 );
        Stack stack = new Stack();
        stack.push( currentAc );
        proccessedAcs.add( currentAc );

        // the statement to select all interactors of one protein
        // to get all interactors for a connecting network
        PreparedStatement selectProt = getCurrentConnection().prepareStatement( SELECT_PROT );
        selectProt.setString( 3, taxid );

        // the statement to update the ia_interactions table
        PreparedStatement updatePST = getCurrentConnection().prepareStatement( UPDATE_GRAPHID );
        updatePST.setString( 4, taxid );

        // the stack stores each element which is
        // part of the current connection network
        // therefore: as long as there are elements in
        // the stack -> there are still elements in the connection graph
        String i1, i2;
        while ( !stack.isEmpty() ) {
            // get the current ac nr from the stack
            currentAc = (String) stack.pop();
            selectProt.setString( 1, currentAc );
            selectProt.setString( 2, currentAc );
            set = selectProt.executeQuery();
            // the set stores all interactors which take part in an interaction
            // with the current accession number.
            while ( set.next() ) {
                i1 = set.getString( 1 );
                i2 = set.getString( 2 );
                // if the interactor has not proccessed yet
                // it is pushed in the stack to memorize it as part of the
                // connecting network and that it has to be proceessed to get
                // its interactors and so on.
                if ( !proccessedAcs.contains( i1 ) ) {
                    stack.push( i1 );
                    proccessedAcs.add( i1 );
                }
                if ( !proccessedAcs.contains( i2 ) ) {
                    stack.push( i2 );
                    proccessedAcs.add( i2 );
                }
            }
            set.close();
            // the graphid is set in every interaction where the current ac is
            // part of
            updatePST.setInt( 1, graphid );
            updatePST.setString( 2, currentAc );
            updatePST.setString( 3, currentAc );
            updatePST.executeUpdate();

        }
        selectProt.close();
        updatePST.close();
        stm.close();
        setGraphIDBio( ps, taxid );
    }

    private static Connection getCurrentConnection() throws SQLException {
        return IntactContext.getCurrentInstance().getDataContext().getDaoFactory().connection();
   }

    /**
     * Run the table update.
     *
     * @param args
     *
     * @throws SQLException
     */
    public static void main( String[] args ) throws Exception {
        buildDatabase(System.out);
    }
}