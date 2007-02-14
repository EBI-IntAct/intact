package uk.ac.ebi.intact.util.newt;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * NewtTerm Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since <pre>01/17/2007</pre>
 * @version 1.0
 */
public class NewtTermTest extends TestCase {
    
    public NewtTermTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite(NewtTermTest.class);
    }

    ////////////////////
    // Tests

    public void testNewtTerm() {
        NewtTerm t = new NewtTerm( 123 );
        assertNotNull( t );
        assertEquals( 123, t.getTaxid() );
        assertNull( t.getScientificName() );
        assertNull( t.getCommonName() );
        assertTrue( t.getChildren().isEmpty() );
        assertTrue( t.getParents().isEmpty() );
    }

    public void testSetGetCommonName() throws Exception {
        NewtTerm t = new NewtTerm( 4 );
        t.setCommonName( "common" );
        assertEquals( "common", t.getCommonName() );
        t.setCommonName( null );
        assertEquals( null, t.getCommonName() );
    }

    public void testSetGetScientificName() throws Exception {
        NewtTerm t = new NewtTerm( 4 );
        t.setScientificName( "common" );
        assertEquals( "common", t.getScientificName() );
        t.setScientificName( null );
        assertEquals( null, t.getScientificName() );
    }

    public void testSetGetTaxid() throws Exception {
        NewtTerm t = new NewtTerm( 3 );
        assertEquals( 3, t.getTaxid() );
        t.setTaxid( 2 );
        assertEquals( 2, t.getTaxid() );
        t.setTaxid( -1 );
        assertEquals( -1, t.getTaxid() );
        t.setTaxid( -2 );
        assertEquals( -2, t.getTaxid() );


        try {
            t.setTaxid( -3 );
            fail( "-3 is not a valid taxid" );
        } catch ( Exception e ) {
            // ok
        }
        try {
            t.setTaxid( 0 );
            fail( "0 is not a valid taxid" );
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testGetChildren() throws Exception {
        NewtTerm t = new NewtTerm( 7 );
        t.addChild( new NewtTerm( 2 ) );
        assertEquals( new NewtTerm( 2 ), t.getChildren().iterator().next() );

        t.addChild( new NewtTerm( 2 ) );
        assertEquals( 1, t.getChildren().size() );

        t.addChild( new NewtTerm( 3 ) );
        assertEquals( 2, t.getChildren().size() );
    }

    public void testGetParents() throws Exception {
        NewtTerm t = new NewtTerm( 7 );
        t.addParent( new NewtTerm( 2 ) );
        assertEquals( new NewtTerm( 2 ), t.getParents().iterator().next() );

        t.addParent( new NewtTerm( 2 ) );
        assertEquals( 1, t.getParents().size() );

        t.addParent( new NewtTerm( 3 ) );
        assertEquals( 2, t.getParents().size() );
    }
}