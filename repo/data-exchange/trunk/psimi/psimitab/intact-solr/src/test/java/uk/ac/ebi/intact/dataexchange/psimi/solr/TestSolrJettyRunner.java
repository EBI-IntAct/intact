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

import uk.ac.ebi.intact.dataexchange.psimi.solr.server.SolrJettyRunner;
import org.junit.Before;
import org.junit.After;
import org.apache.solr.client.solrj.SolrServer;

import java.io.IOException;

/**
 * TODO comment that class header
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since TODO specify the maven artifact version
 */
public class TestSolrJettyRunner {

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


    public void indexFromClasspath(String resource, boolean hasHeader) throws IOException, IntactSolrException {
        IntactSolrIndexer indexer = new IntactSolrIndexer(getTestSolrServer());
        indexer.indexMitabFromClasspath(resource, hasHeader);
    }

    public SolrServer getTestSolrServer(){
        return solrJettyRunner.getSolrServer(CoreNames.CORE_PUB );
    }

}
