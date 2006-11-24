package uk.ac.ebi.intact.externalservices.searchengine;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.io.File;

/**
 * ExperimentIndexExporter Tester.
 *
 * @author <Authors name>
 * @since <pre>11/24/2006</pre>
 * @version 1.0
 */
public class ExperimentIndexExporterTest extends TestCase {
    public ExperimentIndexExporterTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite(ExperimentIndexExporterTest.class);
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

        File f = new File( outputDir.getAbsolutePath() + File.separator + "experiment.xml" );

        if ( f.exists() ) {
            f.delete();
        }

        IndexExporter exporter = new ExperimentIndexExporter( f, "1" );
        exporter.buildIndex();
    }

}
