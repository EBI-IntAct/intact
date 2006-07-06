/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.controlledVocab;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Xref;

import java.sql.*;
import java.util.Collection;
import java.util.Iterator;

/**
 * Utility tool to query Sequence object via JDBC on either Oracle or PostgreSQL.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>03-Mar-2006</pre>
 */
public class SequenceManager {

    // Supported Plateform
    public static final String ORACLE_PLATEFORM = "Oracle";
    public static final String POSTGRESQL_PLATEFORM = "PostgreSQL";

    public static final String SEQUENCE_NAME = "CVOBJECT_ID";

    // SQL queries for supported backend
    public static final String ORACLE_QUERY = "SELECT INTACT." + SEQUENCE_NAME + ".nextval FROM dual";
    public static final String POSTGRES_QUERY = "SELECT nextval('" + SEQUENCE_NAME + "')";

    /**
     * Checks if the given sequence name exists.
     *
     * @param connection   the connection to the database.
     * @param sequenceName the name of the sequence to check upon.
     *
     * @return true if the sequence exists, false otherwise.
     *
     * @throws SQLException if an error occurs.
     */
    private static boolean sequenceExists( Connection connection, String sequenceName ) throws SQLException {

        if ( sequenceName == null ) {
            throw new IllegalArgumentException();
        }

        DatabaseMetaData metadata = connection.getMetaData();

        // We choose to select all sequences so that there is no bias related to the case
        // postgres seesm to store it lowercase while Oracle uppercase, we get them all and
        // compare name in a non case sentitive manner.
        final String schemaPattern = "%";
        final String tableNamePattern = "%";
        final String[] names = { "SEQUENCE" };
        ResultSet tableNames = metadata.getTables( null, schemaPattern, tableNamePattern, names );
        try {
            while ( tableNames.next() ) {
                String table = tableNames.getString( "TABLE_NAME" );

                if ( table.equalsIgnoreCase( sequenceName ) ) {
                    return true;
                }
            }

            System.out.println( "How to create a sequence ?" );
            System.out.println();
            System.out.println( "Oracle:" );
            System.out.println( "                CREATE SEQUENCE cvobject_id START WITH 51 INCREMENT BY 1;" );
            System.out.println( "                GRANT SELECT ON cvobject_id TO INTACT_SELECT ;" );
            System.out.println();
            System.out.println( "On PostgreSQL:" );
            System.out.println( "                CREATE SEQUENCE cvobject_id MINVALUE 1 INCREMENT 1;" );
            System.out.println( "                GRANT ALL ON cvobject_id TO PUBLIC;  " );

            return false;
        } finally {
            if ( tableNames != null ) {
                tableNames.close();
            }
        }
    }

    /**
     * Returns database specific SQL statement for retreiving the next value of a sequence.
     *
     * @param connection the database conenction.
     *
     * @return an SQL statement.
     *
     * @throws SQLException                  if an error occurs.
     * @throws UnsupportedOperationException if the current database plateform of something else than Oracle or
     *                                       PostgreSQL.
     */
    private static String getBackendSpecificSql( Connection connection ) throws SQLException {

        if ( connection == null ) {
            throw new IllegalArgumentException( "You must give a non null Connection." );
        }

        DatabaseMetaData metaData = connection.getMetaData();
        String databaseProductName = metaData.getDatabaseProductName();

        String sql = null;
        if ( POSTGRESQL_PLATEFORM.equals( databaseProductName ) ) {

            sql = POSTGRES_QUERY;

        } else if ( ORACLE_PLATEFORM.equals( databaseProductName ) ) {

            sql = ORACLE_QUERY;

        } else {

            throw new UnsupportedOperationException( "We do not support " + databaseProductName + " database." );
        }

        return sql;
    }

    /**
     * Returns the next id for the given sequence.
     *
     * @param connection   the database connection.
     * @param sequenceName the name of the sequence.
     *
     * @return the next id.
     *
     * @throws SQLException if an error occur.
     */
    public static long getNextSequenceValue( Connection connection, String sequenceName ) throws IntactException {

        if ( connection == null ) {
            throw new IllegalArgumentException( "You must give a non null Connection." );
        }

        if ( sequenceName == null ) {
            throw new IllegalArgumentException( "You must give a non null sequence name." );
        }

        long newId = -1;
        try {
            if ( sequenceExists( connection, sequenceName ) ) {

                String sql = getBackendSpecificSql( connection );

                PreparedStatement pstmt = null;
                ResultSet rs = null;
                try {
                    pstmt = connection.prepareStatement( sql );
                    rs = pstmt.executeQuery();
                    while ( rs.next() ) {
                        newId = rs.getLong( 1 );
                    }

                    if ( newId < 50 ) {
                        throw new IntactException( "Range 1..50 of sequence(" + sequenceName + ") is reserved. " +
                                                   "Please create the sequence so the first id is 51." );
                    }
                }
                finally {
                    try {
                        if ( rs != null ) {
                            rs.close();
                        }
                    } catch ( Exception ignoreThis ) {
                    }
                    try {
                        if ( pstmt != null ) {
                            pstmt.close();
                        }
                    } catch ( Exception ignoreThis ) {
                    }
                }
            } else {
                throw new RuntimeException( "Could not find sequence " + sequenceName + " in the database." );
            }
        } catch ( SQLException sqle ) {
            throw new IntactException( "SQL Error while searching for the next value of sequence(" + sequenceName + ")", sqle );
        }

        return newId;
    }

    public static String getNextId( IntactHelper helper ) throws IntactException {
        Connection connection = helper.getJDBCConnection();
        String prefix = null;
        long id;
        String nextId = null;
        Collection cvObjects;

        do {
            id = getNextSequenceValue( connection, SEQUENCE_NAME );

            // format the id
            if ( id < 10 ) {
                prefix = "IA:000";
            } else if ( id < 100 ) {
                prefix = "IA:00";
            } else if ( id < 1000 ) {
                prefix = "IA:0";
            } else if ( id < 10000 ) {
                prefix = "IA:";
            } else {
                throw new IllegalStateException( "We have used all possible id space." );
            }

            nextId = prefix + id;

            // to be on the safe side, we make sure that the generated id is not in use in the database.
            // if it is found to be, messages will be displayed and the next id will be retreived until we
            // find a suitable one.
            // Given that we are relying on Database's sequences, we don't expect to encounter that issue,
            // that check may be removed later on.

            cvObjects = helper.getObjectsByXref( CvObject.class, nextId );

            if ( ! cvObjects.isEmpty() ) {
                // display error if the IA:xxxx was already in use.
                System.out.println( "=========================================================" );
                System.out.println( "--- ERROR ---" );
                System.out.println( "Generated the next IntAct id: " + nextId );
                System.out.println( "Though it was used by " + cvObjects.size() + " object(s) already:" );
                for ( Iterator iterator2 = cvObjects.iterator(); iterator2.hasNext(); ) {
                    CvObject cv = (CvObject) iterator2.next();
                    System.out.println( "* " + cv.getShortLabel() + " (" + cv.getAc() + ")" );
                    for ( Iterator iterator1 = cv.getXrefs().iterator(); iterator1.hasNext(); ) {
                        Xref xref = (Xref) iterator1.next();
                        System.out.println( "  " + xref );
                    }
                }
                System.out.println( "=========================================================" );
            }

        } while ( ! cvObjects.isEmpty() );

        return nextId;
    }

    // http://forum.java.sun.com/thread.jspa?threadID=633017&tstart=300

    /* On Oracle
       CREATE SEQUENCE cvobject_id START WITH 51 INCREMENT BY 1;
       GRANT SELECT ON cvobject_id TO INTACT_SELECT ;

       On PostgreSQL
       CREATE SEQUENCE cvobject_id MINVALUE 1 INCREMENT 1;
       GRANT ALL ON cvobject_id TO PUBLIC;
     */

    public static void main( String[] args ) throws IntactException, SQLException {
        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            System.out.println( "Database: " + helper.getDbName() );

            for ( int i = 0; i < 3; i++ ) {
                String id = getNextId( helper );
                System.out.println( "id = " + id );
            }

        } finally {
            if ( helper != null ) {
                helper.closeStore();
            }
        }
    }
}