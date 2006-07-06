/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.goDensity.business.data;

import org.apache.ojb.broker.accesslayer.LookupException;
import uk.ac.ebi.intact.persistence.DAOFactory;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.persistence.ObjectBridgeDAO;
import uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used for db-connection
 * (it's a Singleton!)
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public class DbTools {

    /**
     * instance of this class (singleton pattern')
     */
    private static DbTools _instance = null;

    /**
     * jdbc connection
     */
    private Connection _con;

    // =======================================================================
    // getInstance (Singleton) - private Constructor
    // =======================================================================

   static public synchronized DbTools getInstance() {
        if (null == _instance) {
            _instance = new DbTools();
        }
        return _instance;
    }
    /**
     * establishing the db connection @ first call of DbTools
     */
    private DbTools() {

        //initialise variables used for persistence
        ObjectBridgeDAO dao = null;
        ObjectBridgeDAOSource dataSource = null;

        try {

            dataSource = (ObjectBridgeDAOSource) DAOFactory.getDAOSource("uk.ac.ebi.intact.persistence.ObjectBridgeDAOSource");

            //set the setupGoDensity details, ie repository file for OJB in this case
            Map config = new HashMap();
            config.put("mappingfile", "config/repository.xml");
            dataSource.setConfig(config);

            //get a DAO so some work can be done!!
            dao = (ObjectBridgeDAO) dataSource.getDAO();

            _con = dao.getJDBCConnection();

        } catch (DataSourceException e) {
            e.printStackTrace();
        } catch (LookupException e) {
            e.printStackTrace();
        }

        /* manual login instead using ObjectBridgeDAO - for debugging - don't delete
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("cannot load postgres driver: " + e);
            e.printStackTrace();
        }

        try {
            _con =
                    DriverManager.getConnection(
                            "jdbc:postgresql://localhost:5432/intactDB",
                            "username",
                            "password");
        } catch (SQLException e) {
            System.out.println("cannot open connection to db: " + e);
            e.printStackTrace();
        }
        */

    }

    // =======================================================================
    // Public interface
    // =======================================================================

    /**
     * @return a jdbc connection
     */
    public Connection getCon() {
        return _con;
    }

    /**
     * if connection to db is closed, the connection will be established again
     */
    public void updateCon() {
        try {
            if (_con.isClosed()) {
                _instance = new DbTools();
            }
        } catch (SQLException e) {
        }
    }

    /**
     * close jdbc connection
     */
    public void closeCon() {
        finalize();
    }

    /**
     * if instance of DbTools.java is no longer in use
     */
    public void finalize() {
        try {
            _con.close();
        } catch (SQLException e) {
        }
    }

    /**
     * generation of all the tables for goDensity
     */
    public void generateTables() {
        Statement st = null;



        try {
            st = DbTools.getInstance().getCon().createStatement();
            st.executeUpdate("DROP TABLE ia_goDens_binary");
        } catch (SQLException e) {
        } // nothing, if there is not yet the table, ignore exception
        try {
            st.executeUpdate("CREATE TABLE ia_goDens_binary (" +
                             "bait VARCHAR(10) NOT NULL, " +
                             "prey VARCHAR(10) NOT NULL, " +
                             "goBait VARCHAR(10) NOT NULL, " +
                             "goPrey VARCHAR(10) NOT NULL, " +
                             "PRIMARY KEY (bait, prey, goBait, goPrey))");
			//st.executeUpdate("CREATE INDEX igokey ON ia_goDens_binary (gokey)");
			st.executeUpdate("CREATE INDEX igoBaitkey ON ia_goDens_binary (goBait)");
			st.executeUpdate("CREATE INDEX igoPreykey ON ia_goDens_binary (goPrey)");
            //TODO: ?more index useful?

        } catch (SQLException e) {
            System.out.println("create table failed: " + e);
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
            }
        }



        try {
            st = DbTools.getInstance().getCon().createStatement();
            try {
                st.executeUpdate("DROP TABLE ia_goDens_GoDag");
            } catch (SQLException e) {
            }
            try {
                st.executeUpdate("DROP TABLE ia_goDens_GoDagDenorm");
            } catch (SQLException e) {
            }
            try {
                st.executeUpdate("DROP TABLE ia_goDens_GoProt");
            } catch (SQLException e) {
            }

            st.executeUpdate(
                    "CREATE TABLE ia_goDens_GoDag ("
                    + "parent VARCHAR(10), "
                    + "child VARCHAR(10), "
                    + "parentDepth INTEGER NOT NULL, "
                    + "childDepth INTEGER NOT NULL, "
                    + "PRIMARY KEY (parent, child))");
            st.executeUpdate("CREATE INDEX iGoParent ON ia_goDens_GoDag (parent)");
            st.executeUpdate("CREATE INDEX iGoChild ON ia_goDens_GoDag (child)");

            st.executeUpdate("CREATE TABLE ia_goDens_GoDagDenorm ("
                             + "parent VARCHAR(10), "
                             + "child VARCHAR(10), "
                             + "PRIMARY KEY (parent, child))");
            st.executeUpdate("CREATE INDEX iGoParentDenorm ON ia_goDens_GoDagDenorm (parent)");

            st.executeUpdate(
                    "CREATE TABLE ia_goDens_GoProt ("
                    + "goid VARCHAR(10), "
                    + "interactor VARCHAR(10), "
                    + "PRIMARY KEY (goid, interactor))");
            st.executeUpdate("CREATE INDEX iGoId ON ia_goDens_GoProt (goid)");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
            }
        }



        try {
            st = DbTools.getInstance().getCon().createStatement();
            st.executeUpdate("DROP TABLE ia_goDens_density");
        } catch (SQLException e) {
        }

        // nothing, if there is not yet the table, ignore exception
        try {
            st.executeUpdate(
                    "CREATE TABLE ia_goDens_density ("
                    + "goid1 VARCHAR(10) NOT NULL, "
                    + "goid2 VARCHAR(10) NOT NULL, "
                    + "pos_IA INTEGER NOT NULL, "
                    + "is_IA INTEGER NOT NULL, "
                    + "PRIMARY KEY (goid1, goid2))");
            st.executeUpdate("CREATE INDEX igoId1 ON ia_goDens_density(goid1)");
            st.executeUpdate("CREATE INDEX igoId2 ON ia_goDens_density(goid2)");
        } catch (SQLException e) {
            System.out.println("create table density failed: " + e);
        } finally {
            try {
                st.close();
            } catch (SQLException e) {
            }
        }
    }

    // =======================================================================
    // Test methods
    // =======================================================================

    /**
     * main
     * @param args
     */
    public static void main(String[] args) {
        DbTools.getInstance().generateTables();
    }
}
