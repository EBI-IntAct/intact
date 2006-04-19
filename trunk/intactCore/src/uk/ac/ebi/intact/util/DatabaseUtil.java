/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

/**
 * Utility class to deal with the database, for instance, to get the database used.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18-Apr-2006</pre>
 */
public class DatabaseUtil
{

    // Supported Platforms
    private static final String ORACLE_PLATFORM = "Oracle";
    private static final String POSTGRESQL_PLATFORM = "PostgreSQL";

    private enum Database  { ORACLE, POSTGRES }


    /**
     * Returns database specific SQL statement for retreiving the next value of a sequence.
     *
     * @param connection the database conenction.
     * @return the Database being used
     * @throws java.sql.SQLException         if an error occurs.
     * @throws UnsupportedOperationException if the current database plateform of something else than Oracle or
     *                                       PostgreSQL.
     */
    private static Database getDatabase(Connection connection) throws SQLException
    {

        if (connection == null)
        {
            throw new IllegalArgumentException("You must give a non null Connection.");
        }

        DatabaseMetaData metaData = connection.getMetaData();
        String databaseProductName = metaData.getDatabaseProductName();

        if (POSTGRESQL_PLATFORM.equals(databaseProductName))
        {

            return Database.POSTGRES;

        }
        else if (ORACLE_PLATFORM.equals(databaseProductName))
        {

            return Database.ORACLE;

        }
        else
        {

            throw new UnsupportedOperationException("We do not support " + databaseProductName + " database.");
        }
    }

    /**
     * Creates the corresponding SQL statements to limit the results
     * @param sqlQuery The query to limit
     * @param firstResult The first result to be retrieved, being 0 the default
     * @param maxResults Number of results to retrieve from the first result
     * @param connection The connection being used, to determine the database and create the SQL in the correspondent dialect
     * @return the SQL with the limit statement
     * @throws SQLException Thrown when there is a problem accessing the data to determine the database
     */
    public static String wrapWithLimitSql(String sqlQuery, int firstResult, int maxResults, Connection connection) throws SQLException
    {
        Database db = getDatabase(connection);

        String limitSql = null;

        switch (db) {
            case ORACLE:
                String rowNumAlias = "rn";
                sqlQuery = sqlQuery.replaceAll("(from)|(FROM)", ", ROWNUM "+rowNumAlias+" FROM");

                limitSql = "SELECT * from ("+sqlQuery+") WHERE "+rowNumAlias+" BETWEEN "+(firstResult+1)+ " AND "+(firstResult+maxResults);
                break;
            case POSTGRES:
                limitSql = sqlQuery + " LIMIT "+maxResults+" OFFSET "+firstResult;
                break;
        }

        return limitSql;
    }


}
