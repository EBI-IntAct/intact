/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast.jdbc;

import java.io.File;
import java.sql.*;

/**
 * Class for connecting to the blastDb, for storing the blastJobs.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @since <pre>
 *               13 Sep 2007
 *               </pre>
 */
public class BlastDb {

    private Connection conn;
    private int nrConns;
    private static int maxConns = 5;
    private File dbFolder;

    public BlastDb( File dbFolder ) throws BlastJdbcException {
        try {
            Class.forName( "org.h2.Driver" );
            nrConns = 0;
            this.dbFolder = dbFolder;
        } catch ( ClassNotFoundException e ) {
            throw new BlastJdbcException( e );
        }
    }

    public void createJobTable( Connection conn, String tableName ) throws BlastJdbcException {
        try {
            String create = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + " JobId VARCHAR(255) PRIMARY KEY, "
                            + "uniprotAc VARCHAR(20), " + "sequence BLOB, " + "status VARCHAR(15), " + "resultPath VARCHAR(255), " + "timestamp TIMESTAMP);";
            Statement stat = conn.createStatement();
            stat.execute( create );
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }
    }

    public boolean jobTableExists(Connection conn, String tableName ) throws BlastJdbcException {
        try {
            DatabaseMetaData dbMeta = conn.getMetaData();

            // check if "job" table is there
            tableName = tableName.toUpperCase();
            ResultSet checkTable = dbMeta.getTables( null, null, tableName, null );
            String tableFound = null;
            while ( checkTable.next() ) {
                tableFound = checkTable.getString( "TABLE_NAME" );
            }
            return tableFound != null;
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }
    }

    public void dropJobTable( Connection conn, String tableName ) throws BlastJdbcException {
        try {
            String drop = "DROP TABLE IF EXISTS " + tableName + ";";
            Statement stat = conn.createStatement();
            stat.execute( drop );
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e );
        }
    }

    //TODO: remove if not needed
    public void closeDb() throws BlastJdbcException {
//        try {
//                                conn.close();
//        } catch ( SQLException e ) {
//            throw new BlastJdbcException( e );
//        }
    }

    public Connection getConnection() throws BlastJdbcException {
        if ( nrConns >= maxConns ) {
            return null;
        }
        Connection conn = null;
        try {
            nrConns++;

            conn = DriverManager.getConnection( "jdbc:h2:" + dbFolder.getPath() + "/blast", "sa", "" );
        } catch ( SQLException e ) {
            throw new BlastJdbcException( e);
        }
        return conn;
    }
}
