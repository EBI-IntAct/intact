package uk.ac.ebi.intact.uniprot.model;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Organism Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class OrganismTest {

    @Test
    public void Name() throws Exception {
        Organism o = new Organism( 999 );
        assertNull( o.getName() );
        o.setName( "name" );
        assertEquals( "name", o.getName() );
    }

    @Test
    public void GetParents() throws Exception {
        Organism o = new Organism( 999 );
        assertNotNull( o.getParents() );
        assertTrue( o.getParents().isEmpty() );
        o.getParents().add( "p1" );
        assertEquals( 1, o.getParents().size() );
    }

    @Test
    public void Taxid() throws Exception {
        try {
            new Organism( -1 );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            new Organism( 0 );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        Organism o = new Organism( 1 );
        assertEquals( 1, o.getTaxid() );
        o.setTaxid( 2 );
        assertEquals( 2, o.getTaxid() );
    }
}