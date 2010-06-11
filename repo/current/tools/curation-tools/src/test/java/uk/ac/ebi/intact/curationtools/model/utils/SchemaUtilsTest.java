package uk.ac.ebi.intact.curationtools.model.utils;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Unit test for SchemaUtils
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18-May-2010</pre>
 */

public class SchemaUtilsTest {
    private void printDDLs( String[] ddls ) {
        System.out.println( "=========================== START ==============================" );
        for ( int i = 0; i < ddls.length; i++ ) {
            String ddl = ddls[i];
            System.out.println( ddl );
        }
        System.out.println( "============================ END ===============================" );
    }

    @Test
    public void generateCreateSchemaDDLForOracle() throws Exception {
        final String[] ddls = SchemaUtils.generateCreateSchemaDDLForOracle();
        Assert.assertNotNull( ddls );
        Assert.assertTrue( ddls.length > 0 );

//        printDDLs( ddls );
    }

    @Test
    public void generateCreateSchemaDDLForPostgreSQL() throws Exception {
        final String[] ddls = SchemaUtils.generateCreateSchemaDDLForPostgreSQL();
        Assert.assertNotNull( ddls );
        Assert.assertTrue( ddls.length > 0 );
//        printDDLs( ddls );
    }

    @Test
    public void generateCreateSchemaDDLForHSQL() throws Exception {
        final String[] ddls = SchemaUtils.generateCreateSchemaDDLForHSQL();
        Assert.assertNotNull( ddls );
        Assert.assertTrue( ddls.length > 0 );
//        printDDLs( ddls );
    }

    @Test
    public void generateCreateSchemaDDLForH2() throws Exception {
        final String[] ddls = SchemaUtils.generateCreateSchemaDDLForH2();
        Assert.assertNotNull( ddls );
        Assert.assertTrue( ddls.length > 0 );
//        printDDLs( ddls );
    }

}
