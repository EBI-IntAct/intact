package uk.ac.ebi.intact.bridge.model;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * UniprotFeatureChain Tester.
 *
 * @author <Authors name>
 * @since <pre>10/23/2006</pre>
 * @version 1.0
 */
public class UniprotFeatureChainTest extends TestCase {
    public UniprotFeatureChainTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite(UniprotFeatureChainTest.class);
    }

    ////////////////////
    // Tests
    
    public void testId() throws Exception {
        UniprotFeatureChain ufc = new UniprotFeatureChain( "PRO_123", new Organism( 1 ), "ABCD" );
        assertEquals( "PRO_123", ufc.getId() );

        try {
            new UniprotFeatureChain( "", new Organism( 1 ), "ABCD" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            new UniprotFeatureChain( null, new Organism( 1 ), "ABCD" );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testSequence() throws Exception {
        UniprotFeatureChain ufc = new UniprotFeatureChain( "PRO_123", new Organism( 1 ), "ABCD" );
        assertEquals( "ABCD", ufc.getSequence() );
        ufc.setSequence( "ACBEDFG" );
        assertEquals( "ACBEDFG", ufc.getSequence() );
    }

    public void testSetGetOrganism() throws Exception {
        UniprotFeatureChain ufc = new UniprotFeatureChain( "PRO_123", new Organism( 1 ), "ABCD" );
        assertEquals( new Organism( 1 ), ufc.getOrganism() );

        try {
            new UniprotFeatureChain( "PRO_123", null, "ABCD" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            ufc.setOrganism( null );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testStart() throws Exception {
        UniprotFeatureChain ufc = new UniprotFeatureChain( "PRO_123", new Organism( 1 ), "ABCD" );
        ufc.setStart( 2 );
        assertEquals( 2, ufc.getStart() );
        try {
            ufc.setStart( -1 );
            fail();
        } catch ( Exception e ) {
            // ok
        }
        try {
            ufc.setEnd( 4 );
            ufc.setStart( 5 );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testEnd() throws Exception {
        UniprotFeatureChain ufc = new UniprotFeatureChain( "PRO_123", new Organism( 1 ), "ABCD" );
        ufc.setEnd( 5 );
        assertEquals( 5, ufc.getEnd() );
        try {
            ufc.setEnd( -1 );
            fail();
        } catch ( Exception e ) {
            // ok
        }
        try {
            ufc.setStart( 3 );
            ufc.setEnd( 2 );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }
}
