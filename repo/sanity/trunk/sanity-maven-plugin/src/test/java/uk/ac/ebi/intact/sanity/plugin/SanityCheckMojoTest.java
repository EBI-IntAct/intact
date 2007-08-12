package uk.ac.ebi.intact.sanity.plugin;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SanityCheckMojoTest extends AbstractMojoTestCase
{

    public void testUpdate() throws Exception {
        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/simple-config.xml" );

        SanityCheckMojo mojo = (SanityCheckMojo) lookupMojo( "sanity-check", pluginXmlFile );
        mojo.setLog( new SystemStreamLog() );
        mojo.hibernateConfig = new File(SanityCheckMojoTest.class.getResource("/test-hibernate.cfg.xml").getFile());

        mojo.execute();
    }
}