package uk.ac.ebi.intact.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * ConvertXmlPublicationToTabMojo Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version 1.0
 * @since <pre>01/05/2007</pre>
 */
public class ConvertXmlPublicationToTabMojoTest extends AbstractMojoTestCase {

    public void testExecute() throws Exception {

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/pub2tab-simple-test.xml" );

        ConvertXmlPublicationToTabMojo mojo =
                ( ConvertXmlPublicationToTabMojo ) lookupEmptyMojo( "pub2tab", pluginXmlFile );

        File rootDir =
                new File( getBasedir(), "target" + File.separator + "test-classes" + File.separator + "xml-samples" );

        setVariableValueToObject( mojo, "directoryPath", rootDir.getAbsolutePath() );

        mojo.setLog( new SystemStreamLog() );

        try {
            mojo.execute();
        } catch ( MojoExecutionException e ) {
            e.printStackTrace();
            fail();
        }

        // now check if all files were created sucessfuly

        assertTrue( new File( rootDir, "11283351.xls" ).exists() );
        assertTrue( new File( rootDir, "7568142.xls" ).exists() );
        assertTrue( new File( rootDir, "9070862.xls" ).exists() );

        File subDir = new File( rootDir, "sub-dir" );
        assertTrue( new File( subDir, "10366597.xls" ).exists() );
        assertTrue( new File( subDir, "9686597.xls" ).exists() );
    }
}
