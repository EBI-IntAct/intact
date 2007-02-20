package uk.ac.ebi.intact.util.taxonomy;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * NewtTerm Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version 1.0
 * @since <pre>01/17/2007</pre>
 */
public class NewtTermTest extends TestCase {

    public NewtTermTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( NewtTermTest.class );
    }

    ////////////////////
    // Tests

    public void testNewtTerm() {
        NewtTerm t = new NewtTerm( "123|common|sci|blabla" );
        assertNotNull( t );
        assertEquals( 123, t.getTaxid() );
        assertNotNull( t.getScientificName() );
        assertEquals( "sci", t.getScientificName() );

        assertNotNull( t.getCommonName() );
        assertEquals( "common", t.getCommonName() );

        assertTrue( t.getChildren().isEmpty() );
        assertTrue( t.getParents().isEmpty() );
    }
}