package uk.ac.ebi.intact.util.protein;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * ProteinLoaderServiceFactory Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since <pre>02/09/2007</pre>
 * @version 1.0
 */
public class ProteinLoaderServiceFactoryTest extends TestCase {
    
    public ProteinLoaderServiceFactoryTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite(ProteinLoaderServiceFactoryTest.class);
    }

    ////////////////////
    // Tests
    
    public void testGetInstance() throws Exception {
        ProteinLoaderServiceFactory f1 = ProteinLoaderServiceFactory.getInstance();
        assertNotNull( f1 );

        ProteinLoaderServiceFactory f2 = ProteinLoaderServiceFactory.getInstance();
        assertNotNull( f2 );

        // the factory is a singleton
        assertTrue( "The factory is not a singleton", f1 == f2 );
    }

    public void testBuildProteinLoaderService() {
        ProteinLoaderServiceFactory factory = ProteinLoaderServiceFactory.getInstance();
        assertNotNull( factory );

        ProteinLoaderService service = factory.buildProteinLoaderService();
        assertNotNull( service );
    }
}
