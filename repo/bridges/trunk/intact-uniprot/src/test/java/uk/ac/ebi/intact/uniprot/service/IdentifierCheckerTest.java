package uk.ac.ebi.intact.uniprot.service;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * IdentifierChecker Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class IdentifierCheckerTest {

    @Test
    public void ProteinId() {
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

    @Test
    public void SpliceVariantId() {
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

    @Test
    public void FeatureChainId() {
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
