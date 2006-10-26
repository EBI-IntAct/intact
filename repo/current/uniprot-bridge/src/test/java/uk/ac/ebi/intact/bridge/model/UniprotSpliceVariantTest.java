package uk.ac.ebi.intact.bridge.model;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * UniprotSpliceVariant Tester.
 *
 * @author <Authors name>
 * @since <pre>10/23/2006</pre>
 * @version 1.0
 */
public class UniprotSpliceVariantTest extends TestCase {
    public UniprotSpliceVariantTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite(UniprotSpliceVariantTest.class);
    }

    ////////////////////
    // Tests

    public void testPrimaryAc() throws Exception {
        UniprotSpliceVariant sv = new UniprotSpliceVariant( "P12345-1", new Organism( 1 ), "ABCD" );
        assertEquals( "P12345-1", sv.getPrimaryAc() );

        try {
            new UniprotSpliceVariant( "", new Organism( 1 ), "ABCD" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            new UniprotSpliceVariant( null, new Organism( 1 ), "ABCD" );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testSequence() throws Exception {
        UniprotSpliceVariant sv = new UniprotSpliceVariant( "P12345-1", new Organism( 1 ), "ABCD" );
        assertEquals( "ABCD", sv.getSequence() );
        sv.setSequence( "ACBEDFG" );
        assertEquals( "ACBEDFG", sv.getSequence() );
    }

    public void testOrganism() throws Exception {
        UniprotSpliceVariant sv = new UniprotSpliceVariant( "P12345-1", new Organism( 1 ), "ABCD" );
        assertEquals( new Organism( 1 ), sv.getOrganism() );

        try {
            new UniprotSpliceVariant( "PRO_123", null, "ABCD" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            sv.setOrganism( null );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testStart() throws Exception {
        UniprotSpliceVariant sv = new UniprotSpliceVariant( "P12345-1", new Organism( 1 ), "ABCD" );
        sv.setStart( 2 );
        assertEquals( new Integer( 2 ), sv.getStart() );
        try {
            sv.setStart( -1 );
            fail();
        } catch ( Exception e ) {
            // ok
        }
        try {
            sv.setEnd( 4 );
            sv.setStart( 5 );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testEnd() throws Exception {
        UniprotSpliceVariant sv = new UniprotSpliceVariant( "P12345-1", new Organism( 1 ), "ABCD" );
        sv.setEnd( 5 );
        assertEquals( new Integer( 5 ), sv.getEnd() );
        try {
            sv.setEnd( -1 );
            fail();
        } catch ( Exception e ) {
            // ok
        }
        try {
            sv.setStart( 3 );
            sv.setEnd( 2 );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testGetSecondaryAcs() throws Exception {
        UniprotSpliceVariant sv = new UniprotSpliceVariant( "P12345-1", new Organism( 1 ), "ABCD" );
        assertNotNull( sv.getSecondaryAcs() );
        assertEquals( 0, sv.getSecondaryAcs().size() );
        sv.getSecondaryAcs().add( "Q99999" );
        assertEquals( 1, sv.getSecondaryAcs().size() );
        assertTrue( sv.getSecondaryAcs().contains( "Q99999" ) );
    }


    public void testGetSynomyms() throws Exception {
        UniprotSpliceVariant sv = new UniprotSpliceVariant( "P12345-1", new Organism( 1 ), "ABCD" );
        assertNotNull( sv.getSynomyms() );
        assertEquals( 0, sv.getSynomyms().size() );
        sv.getSynomyms().add( "bla" );
        assertEquals( 1, sv.getSynomyms().size() );
        assertTrue( sv.getSynomyms().contains( "bla" ) );
    }

    public void testNote() throws Exception {
        UniprotSpliceVariant sv = new UniprotSpliceVariant( "P12345-1", new Organism( 1 ), "ABCD" );
        assertNull( sv.getNote() );
        sv.setNote( "a note" );
        assertEquals( "a note", sv.getNote() );
    }
}