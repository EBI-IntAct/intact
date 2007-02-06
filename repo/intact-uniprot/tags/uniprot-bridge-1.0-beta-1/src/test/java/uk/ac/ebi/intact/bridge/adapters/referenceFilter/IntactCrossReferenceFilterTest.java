package uk.ac.ebi.intact.bridge.adapters.referenceFilter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.bridge.adapters.referenceFilter.CrossReferenceFilter;
import uk.ac.ebi.intact.bridge.adapters.referenceFilter.IntactCrossReferenceFilter;

/**
 * IntactCrossReferenceSelector Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>10/19/2006</pre>
 */
public class IntactCrossReferenceFilterTest extends TestCase {
    public IntactCrossReferenceFilterTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( IntactCrossReferenceFilterTest.class );
    }

    public void testIsSelected() {

        CrossReferenceFilter filter = new IntactCrossReferenceFilter();
        assertTrue( filter.isSelected( "GO" ) );
        assertTrue( filter.isSelected( "go" ) );
        assertTrue( filter.isSelected( "Go" ) );
        assertTrue( filter.isSelected( " gO " ) );
        assertTrue( filter.isSelected( "interpro" ) );
        assertTrue( filter.isSelected( "SGD" ) );
        assertTrue( filter.isSelected( "FlyBase" ) );

        assertFalse( filter.isSelected( "GOGO" ) );
        assertFalse( filter.isSelected( "gogo" ) );
        assertFalse( filter.isSelected( "foobar" ) );

        try {
            filter.isSelected( "" );
            fail( "null is not allowed" );
        } catch ( Exception e ) {
            // expected
        }

        try {
            filter.isSelected( null );
            fail( "null is not allowed" );
        } catch ( Exception e ) {
            // expected
        }
    }
}
