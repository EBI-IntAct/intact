package uk.ac.ebi.intact.sanity.plugin;

import junit.framework.Assert;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

import java.io.File;
import java.util.Iterator;

/**
 * AssignerMojo test.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AssignerMojoTest extends AbstractSanityMojoTestCase {

    @Test
    public void testAssigner() throws Exception {
        String filename = SanityCheckMojoTest.class.getResource( "/plugin-configs/simple-config.xml" ).getFile();
        Assert.assertNotNull( filename );
        File pluginXmlFile = new File( filename );

        AssignerMojo mojo = (AssignerMojo) lookupMojo( "assigner", pluginXmlFile );
        mojo.setLog( new SystemStreamLog() );
        mojo.hibernateConfig = new File( AssignerMojoTest.class.getResource( "/test-hibernate.cfg.xml" ).getFile() );

        initializeDatabaseContent( mojo.hibernateConfig );

        mojo.execute();
    }

    @Test
    public void testAssigner_config1() throws Exception {
        String filename = SanityCheckMojoTest.class.getResource( "/plugin-configs/assigner-config-1.xml" ).getFile();
        Assert.assertNotNull( filename );
        File pluginXmlFile = new File( filename );

        AssignerMojo mojo = ( AssignerMojo ) lookupMojo( "assigner", pluginXmlFile );

        Assert.assertEquals( 7, mojo.getCurators().size() );
        for ( Iterator iterator = mojo.getCurators().iterator(); iterator.hasNext(); ) {
            Object curator =  iterator.next();
            System.out.println( curator );
        }

        uk.ac.ebi.intact.sanity.plugin.SuperCurator orchard = new uk.ac.ebi.intact.sanity.plugin.SuperCurator();
        orchard.setName( "orchard" );
        orchard.setEmail( "orchard@ebi.ac.uk" );
        orchard.setAdmin( true );

        assertTrue( mojo.getCurators().contains( orchard ) );
        Assert.assertFalse( mojo.isEnableAdminEmails() );
        Assert.assertFalse( mojo.isEnableUserEmails() );
        Assert.assertEquals( "http://www.ebi.ac.uk/intact/editor", mojo.getEditorUrl() );

        mojo.setLog( new SystemStreamLog() );
        mojo.hibernateConfig = new File( AssignerMojoTest.class.getResource( "/test-hibernate.cfg.xml" ).getFile() );

        initializeDatabaseContent( mojo.hibernateConfig );

        mojo.execute();
    }
}