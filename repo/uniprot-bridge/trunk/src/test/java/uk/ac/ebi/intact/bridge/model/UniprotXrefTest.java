package uk.ac.ebi.intact.bridge.model;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * UniprotXref Tester.
 *
 * @author <Authors name>
 * @since <pre>10/23/2006</pre>
 * @version 1.0
 */
public class UniprotXrefTest extends TestCase {
    public UniprotXrefTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite(UniprotXrefTest.class);
    }

    ////////////////////
    // Tests
    
    public void testSetGetAccession() throws Exception {
        UniprotXref x = new UniprotXref( "SAM:1", "sam" );
        assertNotNull( x );
        assertEquals( "SAM:1", x.getAccession() );
        assertEquals( "sam", x.getDatabase() );
        assertNull( x.getDescription() );

        try {
            new UniprotXref( "", "sam" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            new UniprotXref( null, "sam" );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testSetGetDatabase() throws Exception {
        UniprotXref x = new UniprotXref( "SAM:1", "sam" );
        assertNotNull( x );
        assertEquals( "SAM:1", x.getAccession() );
        assertEquals( "sam", x.getDatabase() );
        assertNull( x.getDescription() );

        try {
            new UniprotXref( "SAM:1", "" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            new UniprotXref( "SAM:1", null );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testSetGetDescription() throws Exception {
        UniprotXref x = new UniprotXref( "SAM:1", "sam", "blabla" );
        assertNotNull( x );
        assertEquals( "SAM:1", x.getAccession() );
        assertEquals( "sam", x.getDatabase() );
        assertEquals( "blabla", x.getDescription() );

        x.setDescription( "desc" );
        assertEquals( "desc", x.getDescription() );
    }
}