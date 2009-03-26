package uk.ac.ebi.intact.bridges.taxonomy;

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.*;

/**
 * TaxonomyTerm Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public class TaxonomyTermTest {

    @Test
    public void TaxonomyTerm() {
        TaxonomyTerm t = new TaxonomyTerm( 123 );
        assertNotNull( t );
        assertEquals( 123, t.getTaxid() );
        assertNull( t.getScientificName() );
        assertNull( t.getCommonName() );
        assertTrue( t.getChildren().isEmpty() );
        assertTrue( t.getParents().isEmpty() );
    }

    @Test public void SetGetCommonName() throws Exception {
        TaxonomyTerm t = new TaxonomyTerm( 4 );
        t.setCommonName( "common" );
        assertEquals( "common", t.getCommonName() );
        t.setCommonName( null );
        assertEquals( null, t.getCommonName() );
    }

    @Test public void SetGetScientificName() throws Exception {
        TaxonomyTerm t = new TaxonomyTerm( 4 );
        t.setScientificName( "common" );
        assertEquals( "common", t.getScientificName() );
        t.setScientificName( null );
        assertEquals( null, t.getScientificName() );
    }

    @Test public void SetGetTaxid() throws Exception {
        TaxonomyTerm t = new TaxonomyTerm( 3 );
        assertEquals( 3, t.getTaxid() );
        t.setTaxid( 2 );
        assertEquals( 2, t.getTaxid() );
        t.setTaxid( -1 );
        assertEquals( -1, t.getTaxid() );
        t.setTaxid( -2 );
        assertEquals( -2, t.getTaxid() );
        t.setTaxid( -3 );
        assertEquals( -3, t.getTaxid() );
        t.setTaxid( -4 );
        assertEquals( -4, t.getTaxid() );
        t.setTaxid( -5 );
        assertEquals( -5, t.getTaxid() );

        try {
            t.setTaxid( -6 );
            fail( "-6 is not a valid taxid" );
        } catch ( Exception e ) {
            // ok
        }
        try {
            t.setTaxid( -7 );
            fail( "-7 is not a valid taxid" );
        } catch ( Exception e ) {
            // ok
        }
    }

    @Test public void GetChildren() throws Exception {
        TaxonomyTerm t = new TaxonomyTerm( 7 );
        t.addChild( new TaxonomyTerm( 2 ) );
        assertEquals( new TaxonomyTerm( 2 ), t.getChildren().iterator().next() );

        t.addChild( new TaxonomyTerm( 2 ) );
        assertEquals( 1, t.getChildren().size() );

        t.addChild( new TaxonomyTerm( 3 ) );
        assertEquals( 2, t.getChildren().size() );
    }

    @Test public void GetParents() throws Exception {
        TaxonomyTerm t = new TaxonomyTerm( 7 );
        t.addParent( new TaxonomyTerm( 2 ) );
        assertEquals( new TaxonomyTerm( 2 ), t.getParents().iterator().next() );

        t.addParent( new TaxonomyTerm( 2 ) );
        assertEquals( 1, t.getParents().size() );

        t.addParent( new TaxonomyTerm( 3 ) );
        assertEquals( 2, t.getParents().size() );
    }
}