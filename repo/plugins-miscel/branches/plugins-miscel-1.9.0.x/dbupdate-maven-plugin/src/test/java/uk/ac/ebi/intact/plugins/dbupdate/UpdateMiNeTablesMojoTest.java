package uk.ac.ebi.intact.plugins.dbupdate;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterHelper;

import java.io.File;

/**
 * UpdateMiNeTablesMojo Tester.
 *
 * @author <Authors name>
 * @since <pre>11/10/2006</pre>
 * @version 1.0
 */
public class UpdateMiNeTablesMojoTest extends UpdateAbstractMojoTestCase {

    @Test
    public void testExecution() throws Exception {
        PersisterHelper.saveOrUpdate(getMockBuilder().createExperimentRandom(5));

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/simple-mine-config.xml" );

        UpdateMiNeTablesMojo mojo = (UpdateMiNeTablesMojo) lookupMojo( "mine", pluginXmlFile );
        mojo.setHibernateConfig(getHibernateConfigFile());
        mojo.setLog( new SystemStreamLog() );

        mojo.execute();
    }
}
