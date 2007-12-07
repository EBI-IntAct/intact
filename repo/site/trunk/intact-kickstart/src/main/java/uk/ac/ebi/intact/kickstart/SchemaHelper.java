package uk.ac.ebi.intact.kickstart;

import uk.ac.ebi.intact.core.util.SchemaUtils;
import org.hibernate.dialect.H2Dialect;

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

        // print to the console the DDL to create the schema with H2 database
        //printH2Schema();
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

    public static void printH2Schema() {
        for (String str : SchemaUtils.generateCreateSchemaDDL(H2Dialect.class.getName())) {
            System.out.println(str);
        }
    }

}
