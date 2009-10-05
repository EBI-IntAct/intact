package uk.ac.ebi.intact.sanity.plugin;

import junit.framework.Assert;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.io.File;

/**
 * RangeCheckerMojo Tester
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class RangeCheckerMojoTest extends AbstractSanityMojoTestCase {

    @Test
    public void testAssigner() throws Exception {
        String filename = SanityCheckMojoTest.class.getResource( "/plugin-configs/simple-config.xml" ).getFile();
        Assert.assertNotNull( filename );
        File pluginXmlFile = new File( filename );

        RangeCheckerMojo mojo = ( RangeCheckerMojo ) lookupMojo( "range-check", pluginXmlFile );
        mojo.setLog( new SystemStreamLog() );
        mojo.hibernateConfig = new File( RangeCheckerMojoTest.class.getResource( "/test-hibernate.cfg.xml" ).getFile() );

        initializeDatabaseContent( mojo.hibernateConfig );

        mojo.execute();
    }
}