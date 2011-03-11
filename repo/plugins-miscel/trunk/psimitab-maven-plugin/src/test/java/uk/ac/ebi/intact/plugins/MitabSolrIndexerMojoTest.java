package uk.ac.ebi.intact.plugins;

import junit.framework.Assert;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import uk.ac.ebi.intact.dataexchange.psimi.solr.server.SolrJettyRunner;
import uk.ac.ebi.intact.dataexchange.psimi.solr.*;

import java.io.File;

/**
 * DatabaseMitabExporterMojo Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.9.0
 */
public class MitabSolrIndexerMojoTest extends AbstractMojoTestCase {

    private SolrJettyRunner solrJettyRunner;

    public void setUp() throws Exception {
        super.setUp();
        solrJettyRunner = new SolrJettyRunner();
        solrJettyRunner.setPort( 18987 );
        solrJettyRunner.start();
    }

    public void tearDown() throws Exception {
        super.tearDown();

//        solrJettyRunner.join(); // block the server here

        solrJettyRunner.stop();
        solrJettyRunner = null;
    }

    public void testExecute() throws Exception {

        // mojo
        File pluginXmlFile = new File( getBasedir(), "src/test/plugin-configs/mitab-solr-indexer-simple-test.xml" );

        MitabSolrIndexerMojo mojo = ( MitabSolrIndexerMojo ) lookupMojo( "mitab-solr-indexer", pluginXmlFile );

        File target = new File( getBasedir(), "target" );
        Assert.assertTrue( target.exists() );

        File logPath = new File( target, "mitab-solr-indexer.log" );
        File mitabFile = new File( getBasedir(), "src/test/resources/mitab-samples/intact200.tsv" );

        // set mojo variables
        setVariableValueToObject( mojo, "solrServerUrl", solrJettyRunner.getSolrUrl( CoreNames.CORE_PUB ) );
        setVariableValueToObject( mojo, "ontologySolrServerUrl", solrJettyRunner.getSolrUrl( CoreNames.CORE_ONTOLOGY_PUB ) );
        setVariableValueToObject( mojo, "mitabFileUrl", mitabFile.toURI().toString() );
        setVariableValueToObject( mojo, "logFilePath", logPath.getAbsolutePath() );

        mojo.setLog( new SystemStreamLog() );

        try {
            mojo.execute();
        } catch ( MojoExecutionException e ) {
            e.printStackTrace();
            fail();
        }

        // wait for the children thread to complete
        Thread.sleep( 5000 );

        final SolrServer server = solrJettyRunner.getSolrServer( CoreNames.CORE_PUB );
        SolrQuery query = new SolrQuery();
        query.setStart( 0 );
        query.setRows( 100000 );
        query.setQuery( "*:*" );
        QueryResponse rsp = server.query( query );
        System.out.println( "========================================" );
        System.out.println( "Result count: " + rsp.getResults().size() );
        System.out.println( "========================================" );

        assertCount( CoreNames.CORE_PUB, 200, "*:*" );
    }
    
    private void assertCount( String core, Number expectedCount, String searchQuery ) throws IntactSolrException {
        IntactSolrSearcher searcher = new IntactSolrSearcher( solrJettyRunner.getSolrServer( core ) );
        SolrSearchResult result = searcher.search( searchQuery, 0, Integer.MAX_VALUE );

        System.out.println( "result.getTotalCount() = " + result.getTotalCount() );
        System.out.println( "result.getQueryResponse().getResults().size() = " + result.getQueryResponse().getResults().size() );

        assertEquals( expectedCount, result.getQueryResponse().getResults().size() );
    }
}