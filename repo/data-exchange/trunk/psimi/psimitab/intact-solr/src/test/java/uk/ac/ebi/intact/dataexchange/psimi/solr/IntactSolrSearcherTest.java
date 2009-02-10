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
package uk.ac.ebi.intact.dataexchange.psimi.solr;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.dataexchange.psimi.solr.server.SolrJettyRunner;

import java.io.IOException;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactSolrSearcherTest {

    private SolrJettyRunner solrJettyRunner;

    @Before
    public void before() throws Exception {
        solrJettyRunner = new SolrJettyRunner();
        
        solrJettyRunner.start();

    }

    @After
    public void after() throws Exception {
        //solrJettyRunner.join();
        solrJettyRunner.stop();
        solrJettyRunner = null;
    }

    @Test
    public void search() throws Exception  {
        assertCount(0, "*:*");

        indexFromClasspath("/mitab_samples/intact200.txt", true);

        assertCount(200, "*:*");
        assertCount(100, "\"CHEBI:39112\"");
        assertCount(183L, "experimentalRole:bait");
        assertCount(197L, "experimentalRole:prey");
        assertCount(1L, "-biologicalRole:\"unspecified role\"");
        assertCount(1L, "properties:ENSG00000169047");
    }

    private void assertCount(Number count, String searchQuery) throws IntactSolrException {
        IntactSolrSearcher searcher = new IntactSolrSearcher(solrJettyRunner.getSolrServer(CoreNames.CORE_PUB));
        SolrSearchResult result = searcher.search(searchQuery, null, null);

        assertEquals(count, result.getTotalCount());
    }

    private void indexFromClasspath(String resource, boolean hasHeader) throws IOException, IntactSolrException {
        IntactSolrIndexer indexer = new IntactSolrIndexer(solrJettyRunner.getSolrServer(CoreNames.CORE_PUB));
        indexer.indexMitabFromClasspath(resource, hasHeader);
    }
}
