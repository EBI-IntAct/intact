package uk.ac.ebi.intact.externalservices.searchengine;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;

/**
 * InteractorIndexExporter Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>11/23/2006</pre>
 */
public class InteractorIndexExporterTest extends TestCase {
    public InteractorIndexExporterTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( InteractorIndexExporterTest.class );
    }

    ////////////////////
    // Tests

    public void testBuildIndex() throws Exception {
        File aFile = new File( InteractorIndexExporterTest.class.getResource( "/hibernate.cfg.xml" ).getFile() );
        File outputDir = aFile.getParentFile();
        System.out.println( "Directory: " + outputDir.getAbsolutePath() );
        if ( !outputDir.exists() ) {
            outputDir.mkdirs();
        }

        File f = new File( outputDir.getAbsolutePath() + File.separator + "interactor.xml" );

        if ( f.exists() ) {
            f.delete();
        }

        IndexExporter exporter = new InteractorIndexExporter( f, "1" );
        exporter.buildIndex();
    }
}
