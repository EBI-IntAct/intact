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

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;
import uk.ac.ebi.intact.dataexchange.psimi.solr.server.SolrJettyRunner;

import java.io.File;
import java.util.List;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactSolrSearchTest {

     private SolrJettyRunner solrJettyRunner;

    @Before
    public void before() throws Exception {
        File solrHomeTemplate = new File("src/solr/home");
        File solrWar = new File("src/solr/apache-solr-nightly.war");
        File workingDir = new File("target/jetty");

        // TODO create a jar (intact-solr-home) with everything
        // and then the solrJettyRunner could be initialized with a parameter pointing to that jar file/url

        solrJettyRunner = new SolrJettyRunner(solrHomeTemplate, solrWar, workingDir);
        solrJettyRunner.start();
    }

    @Test
    public void lala() throws Exception {
        SolrQuery query = new SolrQuery()
            .setQuery("alias:brca2")
            .setFacet(true)
            .setFacetLimit(-1)
            .setFacetMinCount(1)
            //.setParam(FacetParams.FACET_OFFSET, "1")
            .addFacetField("idA")
        ;

//        long startTime = System.currentTimeMillis();

        QueryResponse response = solrJettyRunner.getSolrServer().query(query);
//
//        System.out.println("Search time: "+(System.currentTimeMillis()-startTime));

        SolrDocumentList docList = response.getResults();
//
//        System.out.println("Evidences: "+docList.getNumFound());

//        List<FacetField> facetFields = response.getFacetFields();
//
//        for (FacetField facetField : facetFields) {
//            if (facetField.getValues() != null) {
//                System.out.println("Facets: "+facetField.getValues().size());
//            }
//        }

        for (Object aDocList : docList) {
            SolrDocument solrDocument = (SolrDocument) aDocList;
            System.out.println(solrDocument.getFieldValue("ac"));
        }
    }

    @After
    public void after() throws Exception {
        solrJettyRunner.stop();
        solrJettyRunner = null;
    }
}
