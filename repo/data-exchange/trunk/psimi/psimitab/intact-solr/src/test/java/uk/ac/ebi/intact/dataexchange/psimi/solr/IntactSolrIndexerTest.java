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

import java.io.ByteArrayInputStream;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactSolrIndexerTest {

    private SolrJettyRunner solrJettyRunner;
    private IntactSolrIndexer indexer;

    @Before
    public void before() throws Exception {
        solrJettyRunner = new SolrJettyRunner();
        solrJettyRunner.start();

        indexer = new IntactSolrIndexer(solrJettyRunner.getSolrServer(CoreNames.CORE_PUB));

    }

    @After
    public void after() throws Exception {
        solrJettyRunner.stop();
        solrJettyRunner = null;

        indexer = null;
    }

    @Test
    public void testIndexMitabFromClasspath() throws Exception {
        indexer.indexMitabFromClasspath("/mitab_samples/intact200.txt", true);
        assertCount(200, "*:*");
    }

    @Test
    public void testIndexMitabFromClasspath2() throws Exception {
        indexer.indexMitabFromClasspath("/mitab_samples/intact200.txt", true, 10, 20);
        assertCount(20, "*:*");
    }
    
    @Test
    public void testIndexMitabFromClasspath3() throws Exception {
        indexer.indexMitabFromClasspath("/mitab_samples/intact200.txt", true, 190, 20);
        assertCount(10, "*:*");
    }

    @Test
    public void index1() throws Exception {
        // mitab line with annotations
        String mitabLine = "uniprotkb:Q7Z4T9-4|intact:EBI-2119735\tuniprotkb:Q92667-2|intact:EBI-2120060\tuniprotkb:AAT1-alpha(isoform synonym)|uniprotkb:q7z4t9-4|irefindex:+ReKZkaGc+yy9DYMbJ/aHHCMHmw9606(rogid)\t" +
                           "uniprotkb:S-AKAP84(isoform synonym)|uniprotkb:q92667-2|irefindex:iV8hJ/bPZ/NLER8WZj02X6SD+mw9606(rogid)\t-\t-\t" +
                           "psi-mi:\"MI:0007\"(anti tag coip)\tYukitake et al. (2002)\tpubmed:12223483\ttaxid:9606(human)\t" +
                           "taxid:9606(human)\tpsi-mi:\"MI:0915\"(physical association)\tpsi-mi:\"MI:0469\"(IntAct)\t" +
                           "intact:EBI-2120363|irefindex:T3w4Q7GYZthAQDYDWB84nhCnkNQ(rigid)\t-\tpsi-mi:\"MI:0498\"(prey)\t" +
                           "psi-mi:\"MI:0496\"(bait)\tpsi-mi:\"MI:0499\"(unspecified role)\tpsi-mi:\"MI:0499\"(unspecified role)\t" +
                           "intact:EBI-2119657(isoform-parent)|uniprotkb:Q7Z4T9-4(identity)\t" +
                           "intact:EBI-2119593(isoform-parent)|uniprotkb:Q92667-2(identity)\tpsi-mi:\"MI:0326\"(protein)\t" +
                           "psi-mi:\"MI:0326\"(protein)\ttaxid:-1(in vitro)\t-\t-\t" +
                           "isoform-comment:May be produced by alternative promoter usage\t" +
                           "isoform-comment:May be produced at very low levels due to a premature stop codon in the mRNA, leading to nonsense-mediated mRNA decay\t-\t-\t-";

        indexer.indexMitab(new ByteArrayInputStream(mitabLine.getBytes()), false);

        assertCount(1, "*:*");
    }

    private void assertCount(Number count, String searchQuery) throws IntactSolrException {
        IntactSolrSearcher searcher = new IntactSolrSearcher(solrJettyRunner.getSolrServer(CoreNames.CORE_PUB));
        SolrSearchResult result = searcher.search(searchQuery, null, null);

        assertEquals(count, result.getTotalCount());
    }
}
