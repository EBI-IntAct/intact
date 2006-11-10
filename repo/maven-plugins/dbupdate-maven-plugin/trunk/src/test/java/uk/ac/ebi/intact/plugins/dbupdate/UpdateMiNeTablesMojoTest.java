package uk.ac.ebi.intact.plugins.dbupdate;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * UpdateMiNeTablesMojo Tester.
 *
 * @author <Authors name>
 * @since <pre>11/10/2006</pre>
 * @version 1.0
 */
public class UpdateMiNeTablesMojoTest extends AbstractMojoTestCase {

    public void testExecution() throws Exception {
        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/simple-mine-config.xml" );

        UpdateMiNeTablesMojo mojo = (UpdateMiNeTablesMojo) lookupMojo( "mine", pluginXmlFile );
        mojo.setLog( new SystemStreamLog() );

        mojo.execute();
    }
}
