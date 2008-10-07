package uk.ac.ebi.intact.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

/**
 * DatabaseMitabExporterMojo Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.9.0
 */
public class DatabaseMitabExporterMojoTest extends AbstractMojoTestCase {

    public void testExecute() throws Exception {

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/log4j.db-mitab-exporter-simple-test.xml" );

        DatabaseMitabExporterMojo mojo = ( DatabaseMitabExporterMojo ) lookupEmptyMojo( "db-mitab-export", pluginXmlFile );

        File target = new File( getBasedir(), "target" );
        Assert.assertTrue( target.exists() );

        File mitabFile = new File( target, "export.mitab" );
        File interactionIndexPath = new File( target, "interactionIndex" );
        File interactorIndexPath = new File( target, "interactorIndex" );
        File ontologyIndexPath = new File( target, "ontologyIndex" );
        File logPath = new File( target, "export.log" );

        // set mojo variables
        setVariableValueToObject( mojo, "tabFile", mitabFile.getAbsolutePath() );
        setVariableValueToObject( mojo, "interactionIndexPath", interactionIndexPath.getAbsolutePath() );
        setVariableValueToObject( mojo, "interactorIndexPath", interactorIndexPath.getAbsolutePath() );
        setVariableValueToObject( mojo, "ontologyIndex", ontologyIndexPath.getAbsolutePath() );
        setVariableValueToObject( mojo, "logFilePath", logPath.getAbsolutePath() );
        setVariableValueToObject( mojo, "overwrite", true );

        mojo.setLog( new SystemStreamLog() );

        try {
            mojo.execute();
        } catch ( MojoExecutionException e ) {
            e.printStackTrace();
            fail();
        }
    }
}