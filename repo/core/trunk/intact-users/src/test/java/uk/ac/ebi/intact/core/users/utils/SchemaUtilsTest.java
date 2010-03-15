package uk.ac.ebi.intact.core.users.utils;

import junit.framework.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.users.unit.UsersBasicTestCase;

/**
 * SchemaUtils Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class SchemaUtilsTest extends UsersBasicTestCase {
    
    @Test
    public void generateCreateSchemaDDLForOracle() throws Exception {
        final String[] ddls = SchemaUtils.generateCreateSchemaDDLForOracle();
        Assert.assertNotNull( ddls );
        Assert.assertTrue( ddls.length > 0 );

        printDDLs( ddls );
    }

    private void printDDLs( String[] ddls ) {
        System.out.println( "=========================== START ==============================" );
        for ( int i = 0; i < ddls.length; i++ ) {
            String ddl = ddls[i];
            System.out.println( ddl );
        }
        System.out.println( "============================ END ===============================" );
    }

    @Test
    public void generateCreateSchemaDDLForPostgreSQL() throws Exception {
        final String[] ddls = SchemaUtils.generateCreateSchemaDDLForPostgreSQL();
        Assert.assertNotNull( ddls );
        Assert.assertTrue( ddls.length > 0 );
        printDDLs( ddls );
    }

    @Test
    public void generateCreateSchemaDDLForHSQL() throws Exception {
        final String[] ddls = SchemaUtils.generateCreateSchemaDDLForHSQL();
        Assert.assertNotNull( ddls );
        Assert.assertTrue( ddls.length > 0 );
        printDDLs( ddls );
    }

    @Test
    public void generateCreateSchemaDDLForH2() throws Exception {
        final String[] ddls = SchemaUtils.generateCreateSchemaDDLForH2();
        Assert.assertNotNull( ddls );
        Assert.assertTrue( ddls.length > 0 );
        printDDLs( ddls );
    }
}
