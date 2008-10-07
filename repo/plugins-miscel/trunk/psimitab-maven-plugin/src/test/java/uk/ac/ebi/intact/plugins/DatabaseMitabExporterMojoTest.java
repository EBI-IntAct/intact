package uk.ac.ebi.intact.plugins;

import junit.framework.Assert;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;

/**
 * DatabaseMitabExporterMojo Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.9.0
 */
public class DatabaseMitabExporterMojoTest extends AbstractMojoTestCase {

    public void testExecute() throws Exception {

        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/db-mitab-exporter-simple-test.xml" );

        DatabaseMitabExporterMojo mojo = ( DatabaseMitabExporterMojo ) lookupEmptyMojo( "db-mitab-export", pluginXmlFile );

        File target = new File( getBasedir(), "target" );
        Assert.assertTrue( target.exists() );

        File mitabFile = new File( target, "export.mitab" );
        File interactionIndexPath = new File( target, "interactionIndex" );
        File interactorIndexPath = new File( target, "interactorIndex" );
        File ontologyIndexPath = new File( target, "ontologyIndex" );
        File logPath = new File( target, "export.log" );

        List<OntologyMapping> mappings = new ArrayList<OntologyMapping>();
        mappings.add(new OntologyMapping("go", new URL("http://www.geneontology.org/ontology/gene_ontology_edit.obo")));
        mappings.add(new OntologyMapping("interpro", new URL("http://www.ebi.ac.uk/~intact/external/InterProHierarchyOBO.obo")));
        mappings.add(new OntologyMapping("psi-mi", new URL("http://psidev.sourceforge.net/mi/rel25/data/psi-mi25.obo")));

        // set mojo variables
        setVariableValueToObject( mojo, "mitabFilePath", mitabFile.getAbsolutePath() );
        setVariableValueToObject( mojo, "interactionIndexPath", interactionIndexPath.getAbsolutePath() );
        setVariableValueToObject( mojo, "interactorIndexPath", interactorIndexPath.getAbsolutePath() );
        setVariableValueToObject( mojo, "ontologyIndexPath", ontologyIndexPath.getAbsolutePath() );
        setVariableValueToObject( mojo, "logFilePath", logPath.getAbsolutePath() );
        setVariableValueToObject( mojo, "overwrite", true );
        setVariableValueToObject( mojo, "ontologyMappings", mappings);

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