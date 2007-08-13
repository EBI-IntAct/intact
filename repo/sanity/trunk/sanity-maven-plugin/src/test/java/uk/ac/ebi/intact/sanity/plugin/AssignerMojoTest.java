package uk.ac.ebi.intact.sanity.plugin;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

import java.io.File;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AssignerMojoTest extends AbstractMojoTestCase
{

    @Test
    public void testAssigner() throws Exception {
        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/simple-config.xml" );

        AssignerMojo mojo = (AssignerMojo) lookupMojo( "assigner", pluginXmlFile );
        mojo.setLog( new SystemStreamLog() );
        mojo.hibernateConfig = new File(AssignerMojoTest.class.getResource("/test-hibernate.cfg.xml").getFile());

        mojo.execute();
    }
}