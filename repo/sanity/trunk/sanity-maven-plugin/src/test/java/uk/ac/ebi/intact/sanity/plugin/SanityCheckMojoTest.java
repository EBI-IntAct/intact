package uk.ac.ebi.intact.sanity.plugin;

import junit.framework.Assert;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.io.File;

/**
 * SanityCheckMojo Tester.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SanityCheckMojoTest extends AbstractMojoTestCase {

    @Test
    public void testCheck() throws Exception {
        String filename = SanityCheckMojoTest.class.getResource( "/plugin-configs/simple-config.xml" ).getFile();
        Assert.assertNotNull( filename );
        File pluginXmlFile = new File( filename );

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();
        daoFactory.getBaseDao().getDbName();
        IntactContext.getCurrentInstance().getDataContext().commitTransaction();

        SanityCheckMojo mojo = (SanityCheckMojo) lookupMojo( "sanity-check", pluginXmlFile );
        mojo.setLog( new SystemStreamLog() );
        mojo.hibernateConfig = new File(SanityCheckMojoTest.class.getResource("/test-hibernate.cfg.xml").getFile());

        mojo.execute();
    }
}