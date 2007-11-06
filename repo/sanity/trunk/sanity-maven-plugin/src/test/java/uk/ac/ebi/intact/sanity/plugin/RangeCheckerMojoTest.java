package uk.ac.ebi.intact.sanity.plugin;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

import java.io.File;

import junit.framework.Assert;

/**
 * RangeCheckerMojo Tester
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class RangeCheckerMojoTest extends AbstractMojoTestCase {

    @Test
    public void testAssigner() throws Exception {
        String filename = SanityCheckMojoTest.class.getResource( "/plugin-configs/simple-config.xml" ).getFile();
        Assert.assertNotNull( filename );
        File pluginXmlFile = new File( filename );

        RangeCheckerMojo mojo = ( RangeCheckerMojo ) lookupMojo( "range-check", pluginXmlFile );
        mojo.setLog( new SystemStreamLog() );
        mojo.hibernateConfig = new File( RangeCheckerMojoTest.class.getResource( "/test-hibernate.cfg.xml" ).getFile() );

        mojo.execute();
    }
}