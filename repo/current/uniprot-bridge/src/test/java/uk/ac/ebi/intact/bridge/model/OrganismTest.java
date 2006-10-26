package uk.ac.ebi.intact.bridge.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Organism Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>10/23/2006</pre>
 */
public class OrganismTest extends TestCase {
    public OrganismTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( OrganismTest.class );
    }

    //////////////////////
    // Tests

    public void testName() throws Exception {
        Organism o = new Organism( 999 );
        assertNull( o.getName() );
        o.setName( "name" );
        assertEquals( "name", o.getName() );
    }

    public void testGetParents() throws Exception {
        Organism o = new Organism( 999 );
        assertNotNull( o.getParents() );
        assertTrue( o.getParents().isEmpty() );
        o.getParents().add( "p1" );
        assertEquals( 1, o.getParents().size() );
    }

    public void testTaxid() throws Exception {
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
