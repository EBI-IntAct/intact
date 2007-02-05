package uk.ac.ebi.intact.plugins;

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

        String root = "target" + File.separator + "test-classes";
        File srcDir = new File( getBasedir(), root + File.separator + "xml-samples" );
        File targetDir = new File( getBasedir(), root + File.separator + "tab-output" );

        targetDir.mkdirs();

        setVariableValueToObject( mojo, "sourceDirectoryPath", srcDir.getAbsolutePath() );
        setVariableValueToObject( mojo, "targetDirectoryPath", targetDir.getAbsolutePath() );
        setVariableValueToObject( mojo, "logFilePath", targetDir.getAbsolutePath() + File.separator + "mitab.log" );

        mojo.setLog( new SystemStreamLog() );

        mojo.execute();

        // now check if all files were created sucessfuly
        assertTrue( new File( targetDir, "11283351.txt" ).exists() );
        assertTrue( new File( targetDir, "7568142.txt" ).exists() );
        assertTrue( new File( targetDir, "9070862.txt" ).exists() );

        File subDir = new File( targetDir, "sub-dir" );
        assertTrue( new File( subDir, "10366597.txt" ).exists() );
        assertTrue( new File( subDir, "9686597.txt" ).exists() );
    }
}
