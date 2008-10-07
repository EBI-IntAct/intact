package uk.ac.ebi.intact.plugins;

import junit.framework.Assert;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;

import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;

/**
 * DatabaseMitabExporterMojo Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.9.0
 */
public class DatabaseMitabExporterMojoTest extends AbstractMojoTestCase {

    public void testExecute() throws Exception {

        File hibernateConfig = new File( DatabaseMitabExporterMojoTest.class.getResource( "/test-hibernate.cfg.xml" ).getFile() );
        IntactContext.initStandaloneContext( hibernateConfig );

        PersisterHelper.saveOrUpdate(new IntactMockBuilder(IntactContext.getCurrentInstance().getInstitution())
                                        .createInteractionRandomBinary());


        // mojo
        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/db-mitab-exporter-simple-test.xml" );

        DatabaseMitabExporterMojo mojo = ( DatabaseMitabExporterMojo ) lookupMojo( "db-mitab-export", pluginXmlFile );

        File target = new File( getBasedir(), "target" );
        Assert.assertTrue( target.exists() );

        File mitabFile = new File( target, "export.mitab" );
        File interactionIndexPath = new File( target, "interactionIndex" );
        File interactorIndexPath = new File( target, "interactorIndex" );
        File ontologyIndexPath = new File( target, "ontologyIndex" );
        File logPath = new File( target, "export.log" );

        // set mojo variables
        setVariableValueToObject( mojo, "hibernateConfig", hibernateConfig );
        setVariableValueToObject( mojo, "mitabFilePath", mitabFile.getAbsolutePath() );
        setVariableValueToObject( mojo, "interactionIndexPath", interactionIndexPath.getAbsolutePath() );
        setVariableValueToObject( mojo, "interactorIndexPath", interactorIndexPath.getAbsolutePath() );
        setVariableValueToObject( mojo, "ontologyIndexPath", ontologyIndexPath.getAbsolutePath() );
        setVariableValueToObject( mojo, "logFilePath", logPath.getAbsolutePath() );

        mojo.setLog( new SystemStreamLog() );

        try {
            mojo.execute();
        } catch ( MojoExecutionException e ) {
            e.printStackTrace();
            fail();
        }
    }


    public void testRun() throws Exception {

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/db-mitab-exporter-simple-test.xml" );
        Assert.assertTrue( pluginXmlFile.exists() );
        DatabaseMitabExporterMojo mojo = ( DatabaseMitabExporterMojo ) lookupMojo( "db-mitab-export", pluginXmlFile );

        mojo.setLog( new SystemStreamLog() );

        try {
            mojo.execute();
        } catch ( MojoExecutionException e ) {
            e.printStackTrace();
            fail();
        }
    }
}