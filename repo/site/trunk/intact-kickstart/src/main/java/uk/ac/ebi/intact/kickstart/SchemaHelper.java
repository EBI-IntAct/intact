package uk.ac.ebi.intact.kickstart;

import org.hibernate.dialect.HSQLDialect;
import uk.ac.ebi.intact.core.util.SchemaUtils;

/**
 * Helps to create the DDL. This will output the file that you can use to create
 * the tables in your database.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SchemaHelper {

    public static void main(String[] args) {
        // print to the console the DDL to create the schema with postgreSQL
        //printPostgreSQLSchema();
        
        // print to the console the DDL to create the schema with Oracle
        //printOracleSchema();

        // print to the console the DDL to create the schema with HSQL database
        printHSQLSchema();
    }

    public static void printPostgreSQLSchema() {
        for (String str : SchemaUtils.generateCreateSchemaDDLForPostgreSQL()) {
            System.out.println(str);
        }
    }

    public static void printOracleSchema() {
        for (String str : SchemaUtils.generateCreateSchemaDDLForOracle()) {
            System.out.println(str);
        }
    }

    public static void printHSQLSchema() {
        for (String str : SchemaUtils.generateCreateSchemaDDL(HSQLDialect.class.getName())) {
            System.out.println(str);
        }
    }

}
