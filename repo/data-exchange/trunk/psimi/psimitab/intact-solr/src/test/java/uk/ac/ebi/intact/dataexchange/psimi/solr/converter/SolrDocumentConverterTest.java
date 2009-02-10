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
package uk.ac.ebi.intact.dataexchange.psimi.solr.converter;

import org.apache.lucene.store.Directory;
import org.apache.solr.common.SolrInputDocument;
import org.junit.*;
import uk.ac.ebi.intact.bridges.ontologies.OntologyIndexSearcher;
import uk.ac.ebi.intact.dataexchange.psimi.solr.TestHelper;
import uk.ac.ebi.intact.psimitab.IntactDocumentDefinition;

import java.util.Collection;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SolrDocumentConverterTest {

    private static Directory ontologyDir;

    @BeforeClass
    public static void prepareOntologyIndex() throws Exception{
        //ontologyDir = TestHelper.buildDefaultOntologiesIndex();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testToSolrDocument() throws Exception {
        Directory ontologyDir = TestHelper.buildDefaultOntologiesIndex();

        String goTermToExpand = "GO:0030246";
        String psiMiTabLine = "uniprotkb:P16884\tuniprotkb:Q60824\tuniprotkb:Nefh(gene name)\tuniprotkb:Dst(gene name)" +
                              "\tintact:Nfh\tintact:Bpag1\tMI:0018(2 hybrid)\tLeung et al. (1999)\tpubmed:9971739" +
                              "\ttaxid:10116(rat)\ttaxid:10090(mouse)\tMI:0218(physical interaction)\tMI:0469(intact)" +
                              "\tintact:EBI-446356\t-\tMI:0498(prey)\tMI:0496(bait)\tMI:0499(unspecified role)" +
                              "\tMI:0499(unspecified role)\tinterpro:IPR004829|\tgo:\""+goTermToExpand+"\"\tMI:0326(protein)\tMI:0326(protein)\tyeast:4932\t-\t-";

        OntologyIndexSearcher ontologySearcher = new OntologyIndexSearcher(ontologyDir);
        SolrDocumentConverter converter = new SolrDocumentConverter(new IntactDocumentDefinition(), ontologySearcher);

        SolrInputDocument doc = converter.toSolrDocument(psiMiTabLine);

        Collection<Object> expandedGoIds = doc.getFieldValues("go_expanded_id");

        Assert.assertEquals(3, expandedGoIds.size());
        Assert.assertTrue(expandedGoIds.contains(goTermToExpand));
        Assert.assertTrue(expandedGoIds.contains("GO:0003674"));
        Assert.assertTrue(expandedGoIds.contains("GO:0005488"));

    }
}
