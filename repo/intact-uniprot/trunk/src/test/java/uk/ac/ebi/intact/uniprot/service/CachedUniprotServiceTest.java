package uk.ac.ebi.intact.bridge.adapters;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import uk.ac.ebi.intact.bridge.model.UniprotProtein;

import java.util.Collection;

/**
 * CachedUniprotService Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since <pre>02/22/2007</pre>
 * @version TODO artifact version
 */
public class CachedUniprotServiceTest extends TestCase {
    
    public CachedUniprotServiceTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite(CachedUniprotServiceTest.class);
    }

    ////////////////////
    // Tests

    public void testConstructor() {
        UniprotService service = new CachedUniprotService( new DummyUniprotService( ) );
        Collection<UniprotProtein> proteins;

        proteins = service.retreive( "P12345" );
        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );
        UniprotProtein p1 = proteins.iterator().next();

        proteins = service.retreive( "P12345" );
        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );
        UniprotProtein p2 = proteins.iterator().next();

        assertEquals( p1, p2);
        assertSame( p1, p2 );
    }
}
