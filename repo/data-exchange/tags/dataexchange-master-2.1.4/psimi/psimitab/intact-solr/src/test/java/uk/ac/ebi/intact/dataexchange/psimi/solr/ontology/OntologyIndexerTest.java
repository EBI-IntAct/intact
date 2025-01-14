/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.dataexchange.psimi.solr.ontology;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.StreamingUpdateSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.bridges.ontologies.iterator.UniprotTaxonomyOntologyIterator;
import uk.ac.ebi.intact.dataexchange.psimi.solr.CoreNames;
import uk.ac.ebi.intact.dataexchange.psimi.solr.server.SolrJettyRunner;


/**
 * OntologyIndexer Tester.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OntologyIndexerTest  {

    private SolrJettyRunner solrJettyRunner;

    /*@Before
    public void before() throws Exception {
        solrJettyRunner = new SolrJettyRunner();
        solrJettyRunner.start();
    }

    @After
    public void after() throws Exception {
//        solrJettyRunner.join();
        solrJettyRunner.stop();
        solrJettyRunner = null;
    }*/

    @Test
    @Ignore
    public void testIndexObo() throws Exception{
        StreamingUpdateSolrServer solrServer = solrJettyRunner.getStreamingSolrServer(CoreNames.CORE_ONTOLOGY_PUB);

        OntologyIndexer ontologyIndexer = new OntologyIndexer(solrServer);

        ontologyIndexer.indexObo("psi-mi", OntologyIndexerTest.class.getResource("/META-INF/psi-mi.obo"));

        SolrQuery query = new SolrQuery("*:*");
        QueryResponse queryResponse = solrServer.query(query);

        Assert.assertEquals(1745, queryResponse.getResults().getNumFound());
    }

    @Test
    @Ignore
    public void testTinyIndexObo() throws Exception{
        StreamingUpdateSolrServer solrServer = solrJettyRunner.getStreamingSolrServer(CoreNames.CORE_ONTOLOGY_PUB);

        OntologyIndexer ontologyIndexer = new OntologyIndexer(solrServer);

        ontologyIndexer.indexObo("psi-mi", OntologyIndexerTest.class.getResource( "/META-INF/psi-mi-tiny.obo" ) );

        SolrQuery query = new SolrQuery("*:*");
        QueryResponse queryResponse = solrServer.query(query);

        // 3 terms, 2 relationship (1 is_a, 1 part_of)
        Assert.assertEquals( 4, queryResponse.getResults().getNumFound() );
    }

    @Test
    @Ignore
    public void testSmallIndexObo() throws Exception{
        StreamingUpdateSolrServer solrServer = solrJettyRunner.getStreamingSolrServer(CoreNames.CORE_ONTOLOGY_PUB);

        OntologyIndexer ontologyIndexer = new OntologyIndexer(solrServer);

        ontologyIndexer.indexObo("psi-mi", OntologyIndexerTest.class.getResource( "/META-INF/psi-mi-small.obo" ) );

        SolrQuery query = new SolrQuery("*:*");
        QueryResponse queryResponse = solrServer.query(query);

        // 6 terms, 2 relationship (4 is_a, 2 part_of)

        //                   MI:0000
        //                  /       \
        //      ______MI:0001      MI:0002
        //     |       |    \     /
        // MI:0045  MI:0063  MI:0362

        // expected: root + count(is_a/part_of relationship) + count(leafs): 1 + 6 + 3
        Assert.assertEquals( 10, queryResponse.getResults().getNumFound() );
    }

    @Test
    @Ignore
    public void testIndexTaxonomy() throws Exception{
        StreamingUpdateSolrServer solrServer = solrJettyRunner.getStreamingSolrServer(CoreNames.CORE_ONTOLOGY_PUB);

        OntologyIndexer ontologyIndexer = new OntologyIndexer(solrServer);

        ontologyIndexer.indexOntology(new UniprotTaxonomyOntologyIterator(0, 3));
         
        SolrQuery query = new SolrQuery("*:*");
        QueryResponse queryResponse = solrServer.query(query);

        Assert.assertEquals(3, queryResponse.getResults().getNumFound());
    }
}
