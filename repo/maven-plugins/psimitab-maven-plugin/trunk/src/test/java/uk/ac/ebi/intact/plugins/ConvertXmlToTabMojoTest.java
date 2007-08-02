package uk.ac.ebi.intact.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * ConvertXmlToTabMojo Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version 1.0
 * @since <pre>01/04/2007</pre>
 */
public class ConvertXmlToTabMojoTest extends AbstractMojoTestCase {

    public void testExecute() throws Exception {

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/xml2tab-simple-test.xml" );

        ConvertXmlToTabMojo mojo = ( ConvertXmlToTabMojo ) lookupEmptyMojo( "xml2tab", pluginXmlFile );

        File rootDir =
                new File( getBasedir(), "target" + File.separator + "test-classes" + File.separator + "xml-samples" );

        File tabFile = new File( rootDir.getAbsolutePath() + File.separator + "all.xls" );
        File logFile = new File( rootDir.getAbsolutePath() + File.separator + "output.log" );

        // set output file
        setVariableValueToObject( mojo, "tabFile", tabFile.getAbsolutePath() );

        // set input files
        Collection files = new ArrayList();
        files.add( rootDir.getAbsolutePath() );
        setVariableValueToObject( mojo, "files", files );

        mojo.setLog( new SystemStreamLog() );

        try {
            mojo.execute();
        } catch ( MojoExecutionException e ) {
            e.printStackTrace();
            fail();
        }

        assertTrue( tabFile.exists() );
    }
}
