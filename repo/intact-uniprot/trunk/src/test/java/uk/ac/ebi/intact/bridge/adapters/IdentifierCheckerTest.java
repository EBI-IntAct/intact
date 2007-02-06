package uk.ac.ebi.intact.bridge.adapters;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * IdentifierChecker Tester.
 *
 * @author <Authors name>
 * @since <pre>10/26/2006</pre>
 * @version 1.0
 */
public class IdentifierCheckerTest extends TestCase {
    public IdentifierCheckerTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite(IdentifierCheckerTest.class);
    }

    ////////////////////
    // Tests

    public void testProteinId() {
        // positive tests
        assertTrue( IdentifierChecker.isProteinId( "P12345" ) );
        assertTrue( IdentifierChecker.isProteinId( "Q98765" ) );

        // negative tests
        assertFalse( IdentifierChecker.isProteinId( "Z12345" ) );
        assertFalse( IdentifierChecker.isProteinId( "111111" ) );
        assertFalse( IdentifierChecker.isProteinId( "111111111" ) );
        assertFalse( IdentifierChecker.isProteinId( "1" ) );
        assertFalse( IdentifierChecker.isProteinId( "AAAAAA" ) );
        assertFalse( IdentifierChecker.isProteinId( "AAAAAA" ) );

        // exceptions
        try {
            IdentifierChecker.isProteinId( null );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }
    
    public void testSpliceVariantId() {
        // positive test
        assertTrue( IdentifierChecker.isSpliceVariantId( "P12345-1" ) );
        assertTrue( IdentifierChecker.isSpliceVariantId( "Q98765-1" ) );

        // negative tests
        assertFalse( IdentifierChecker.isSpliceVariantId( " P12345-1" ) );  // spaces
        assertFalse( IdentifierChecker.isSpliceVariantId( "P12345-1 " ) );  // spaces
        assertFalse( IdentifierChecker.isSpliceVariantId( " P12345-1 " ) ); // spaces
        assertFalse( IdentifierChecker.isSpliceVariantId( "P12345" ) );     // protein id
        assertFalse( IdentifierChecker.isSpliceVariantId( "P12345-PRO_1234567890" ) ); // feature chain id
        assertFalse( IdentifierChecker.isSpliceVariantId( "PRO_1234567890" ) );        // feature chain id

        // exceptions
        try {
            IdentifierChecker.isSpliceVariantId( null );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testFeatureChainId() {
        // positive test
        assertTrue( IdentifierChecker.isFeatureChainId( "P12345-PRO_1234567890" ) );
        assertTrue( IdentifierChecker.isFeatureChainId( "PRO_1234567890" ) );

        // negative tests
        assertFalse( IdentifierChecker.isFeatureChainId( "P12345" ) );   // protein id
        assertFalse( IdentifierChecker.isFeatureChainId( "P12345-1" ) ); // splice variant id
        assertFalse( IdentifierChecker.isFeatureChainId( " PRO_1234567890" ) );  // spaces
        assertFalse( IdentifierChecker.isFeatureChainId( " PRO_1234567890 " ) ); // spaces
        assertFalse( IdentifierChecker.isFeatureChainId( "PRO_1234567890 " ) );  // spaces
        assertFalse( IdentifierChecker.isFeatureChainId( "PRO_12345678900" ) );        // 11 digits at the end
        assertFalse( IdentifierChecker.isFeatureChainId( "P12345-PRO_12345678900" ) ); // 11 digits at the end
        assertFalse( IdentifierChecker.isFeatureChainId( "P123456-PRO_12345678900" ) ); // wrong prot prefix
        assertFalse( IdentifierChecker.isFeatureChainId( "P123456-XXX_12345678900" ) ); // wrong PRO

        // exceptions
        try {
            IdentifierChecker.isFeatureChainId( null );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }
}
