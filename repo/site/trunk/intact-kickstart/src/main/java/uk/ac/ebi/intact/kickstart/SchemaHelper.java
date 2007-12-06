package uk.ac.ebi.intact.kickstart;

import uk.ac.ebi.intact.core.util.SchemaUtils;

/**
 * Helps to create the DDL
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SchemaHelper {

    public static void main(String[] args) {
        // print to the console the DDL to create the schema with postgreSQL
        printPostgreSQLSchema();
        
        // print to the console the DDL to create the schema with Oracle
        //printOracleSchema();
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

}
