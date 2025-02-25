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

import org.apache.solr.common.SolrInputDocument;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.bridges.ontologies.OntologyMapping;
import uk.ac.ebi.intact.bridges.ontologies.iterator.OntologyIterator;
import uk.ac.ebi.intact.bridges.ontologies.iterator.UniprotTaxonomyOntologyIterator;
import uk.ac.ebi.intact.dataexchange.psimi.solr.converter.SolrDocumentConverter;
import uk.ac.ebi.intact.dataexchange.psimi.solr.ontology.OntologySearcher;
import uk.ac.ebi.intact.dataexchange.psimi.solr.server.SolrJettyRunner;
import uk.ac.ebi.intact.psimitab.IntactBinaryInteraction;
import uk.ac.ebi.intact.psimitab.IntactDocumentDefinition;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;

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

        indexer = new IntactSolrIndexer(solrJettyRunner.getSolrServer(CoreNames.CORE_PUB),
                                        solrJettyRunner.getSolrServer(CoreNames.CORE_ONTOLOGY_PUB));

    }

    @After
    public void after() throws Exception {
        solrJettyRunner.stop();
        solrJettyRunner = null;

        indexer = null;
    }

    @Test
    public void indexMitabFromClasspath() throws Exception {
        indexer.indexMitabFromClasspath("/mitab_samples/intact200.txt", true);
        assertCount(200, "*:*");
    }

    @Test
    public void indexMitabFromClasspath2() throws Exception {
        indexer.indexMitabFromClasspath("/mitab_samples/intact200.txt", true, 10, 20);
        assertCount(20, "*:*");
    }
    
    @Test
    public void indexMitabFromClasspath3() throws Exception {
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

    @Test
    public void toSolrDocument_goExpansion() throws Exception {
        indexer.indexOntologies(new OntologyMapping[] {
                new OntologyMapping("go", IntactSolrIndexerTest.class.getResource("/META-INF/goslim_generic.obo"))
        });

        String goTermToExpand = "GO:0030246";
        String psiMiTabLine = "uniprotkb:P16884\tuniprotkb:Q60824\tuniprotkb:Nefh(gene name)\tuniprotkb:Dst(gene name)" +
                              "\tintact:Nfh\tintact:Bpag1\tMI:0018(2 hybrid)\tLeung et al. (1999)\tpubmed:9971739" +
                              "\ttaxid:10116(rat)\ttaxid:10090(mouse)\tMI:0218(physical interaction)\tMI:0469(intact)" +
                              "\tintact:EBI-446356\t-\tMI:0498(prey)\tMI:0496(bait)\tMI:0499(unspecified role)" +
                              "\tMI:0499(unspecified role)\tinterpro:IPR004829|\tgo:\""+goTermToExpand+"\"\tMI:0326(protein)\tMI:0326(protein)\tyeast:4932\t-\t-";

        OntologySearcher ontologySearcher = new OntologySearcher(solrJettyRunner.getSolrServer(CoreNames.CORE_ONTOLOGY_PUB));
        SolrDocumentConverter converter = new SolrDocumentConverter(new IntactDocumentDefinition(), ontologySearcher);

        SolrInputDocument doc = converter.toSolrDocument(psiMiTabLine);

        Collection<Object> expandedGoIds = doc.getFieldValues("go_expanded_id");

        Assert.assertEquals(3, expandedGoIds.size());
        Assert.assertTrue(expandedGoIds.contains(goTermToExpand));
        Assert.assertTrue(expandedGoIds.contains("GO:0003674"));
        Assert.assertTrue(expandedGoIds.contains("GO:0005488"));

    }

    @Test
    public void toSolrDocument_goDescriptionUpdate() throws Exception {
        indexer.indexOntologies(new OntologyMapping[] {
                new OntologyMapping("go", IntactSolrIndexerTest.class.getResource("/META-INF/goslim_generic.obo"))
        });

        String goTermToExpand = "go:\"GO:0030246\"(lalalala!)";
        String psiMiTabLine = "uniprotkb:P16884\tuniprotkb:Q60824\tuniprotkb:Nefh(gene name)\tuniprotkb:Dst(gene name)" +
                              "\tintact:Nfh\tintact:Bpag1\tMI:0018(2 hybrid)\tLeung et al. (1999)\tpubmed:9971739" +
                              "\ttaxid:10116(rat)\ttaxid:10090(mouse)\tMI:0218(physical interaction)\tMI:0469(intact)" +
                              "\tintact:EBI-446356\t-\tMI:0498(prey)\tMI:0496(bait)\tMI:0499(unspecified role)" +
                              "\tMI:0499(unspecified role)\tinterpro:IPR004829|\t"+goTermToExpand+"\tMI:0326(protein)\tMI:0326(protein)\tyeast:4932\t-\t-";

        OntologySearcher ontologySearcher = new OntologySearcher(solrJettyRunner.getSolrServer(CoreNames.CORE_ONTOLOGY_PUB));
        SolrDocumentConverter converter = new SolrDocumentConverter(new IntactDocumentDefinition(), ontologySearcher);

        SolrInputDocument doc = converter.toSolrDocument(psiMiTabLine);

        Collection<Object> expandedGoIds = doc.getFieldValues("go_expanded");
        Assert.assertEquals(3, expandedGoIds.size());
        Assert.assertTrue(expandedGoIds.contains("go:\"GO:0030246\"(carbohydrate binding)"));

        IntactBinaryInteraction binaryInteraction = (IntactBinaryInteraction) converter.toBinaryInteraction(doc);
        Assert.assertEquals("carbohydrate binding", binaryInteraction.getInteractorB().getProperties().iterator().next().getText());
    }

    @Test
    public void toSolrDocument_taxidUpdate() throws Exception {
         String mitab = "uniprotkb:P35568|intact:EBI-517592\tuniprotkb:Q08345-2|intact:EBI-711903\tuniprotkb:IRS1(gene name)" +
                "\t-\tintact:irs1_human(shortLabel)\tuniprotkb:CAK II(isoform synonym)|uniprotkb:Short(isoform synonym)|intact:Q08345-2(shortLabel)" +
                "\tMI:0424(protein kinase assay)\tBantscheff et al. (2007)" +
                "\tpubmed:17721511\ttaxid:9606(human)\ttaxid:9606(human)\tMI:0217(phosphorylation)" +
                "\tMI:0469(intact)\tintact:EBI-1381086\t-\t" +
                "MI:0499(unspecified role)\tMI:0499(unspecified role)" +
                "\tMI:0502(enzyme target)\tMI:0501(enzyme)" +
                "\tgo:\"GO:0030188\"|go:\"GO:0005159\"|" +
                "go:\"GO:0005158\"|interpro:IPR002404|" +
                "interpro:IPR001849|interpro:IPR011993|ensembl:ENSG00000169047|rcsb pdb:1IRS|rcsb pdb:1K3A|rcsb pdb:1QQG" +
                "\tintact:EBI-711879\tMI:0326(protein)\tMI:0326(protein)\ttaxid:-1(in vitro)\t-\t-\t-";

        OntologyIterator taxonomyIterator = new UniprotTaxonomyOntologyIterator(IntactSolrSearcherTest.class.getResourceAsStream("/META-INF/hominidae-taxonomy.tsv"));
        indexer.indexOntology(taxonomyIterator);

        OntologySearcher ontologySearcher = new OntologySearcher(solrJettyRunner.getSolrServer(CoreNames.CORE_ONTOLOGY_PUB));
        SolrDocumentConverter converter = new SolrDocumentConverter(new IntactDocumentDefinition(), ontologySearcher);

        SolrInputDocument doc = converter.toSolrDocument(mitab);

        Collection<Object> expandedTaxidA = doc.getFieldValues("taxidA_expanded");
        Assert.assertTrue(expandedTaxidA.contains("taxid:314295(Catarrhini)"));
        Assert.assertTrue(expandedTaxidA.contains("taxid:207598(Homininae)"));
        Assert.assertTrue(expandedTaxidA.contains("taxid:9605(Homo)"));
        Assert.assertTrue(expandedTaxidA.contains("taxid:9604(Hominidae)"));
        Assert.assertTrue(expandedTaxidA.contains("taxid:9606(Human)"));
    }

    @Test
    public void toSolrDocument_wildcard() throws Exception {
        String mitabLine = "uniprotkb:P16884\tuniprotkb:Q60824\tuniprotkb:Nefh(gene name)\tuniprotkb:Dst(gene name)" +
                              "\tintact:Nfh\tintact:Bpag1\tMI:0018(2 hybrid)\tLeung et al. (1999)\tpubmed:9971739" +
                              "\ttaxid:10116(rat)\ttaxid:10090(mouse)\tMI:0218(physical interaction)\tMI:0469(intact)" +
                              "\tintact:EBI-446356\t-\tMI:0498(prey)\tMI:0496(bait)\tMI:0499(unspecified role)" +
                              "\tMI:0499(unspecified role)\tinterpro:IPR004829|\tGO:012345\tMI:0326(protein)\tMI:0326(protein)\tyeast:4932\t-\t-";
        
        indexer.indexMitab(new ByteArrayInputStream(mitabLine.getBytes()), false);

        assertCount(1, "Nefh*");
    }

    private void assertCount(Number count, String searchQuery) throws IntactSolrException {
        IntactSolrSearcher searcher = new IntactSolrSearcher(solrJettyRunner.getSolrServer(CoreNames.CORE_PUB));
        SolrSearchResult result = searcher.search(searchQuery, null, null);

        assertEquals(count, result.getTotalCount());
    }

    @Test
    public void retrying() throws Exception {
        solrJettyRunner.stop();

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    indexer.indexMitabFromClasspath("/mitab_samples/intact200.txt", true);
                } catch (IOException e) {
                    Assert.fail("An IOException was thrown");
                }
            }
        };

        t.start();
        
        Thread.sleep(5*1000);

        solrJettyRunner.start();

        while (t.isAlive()) {
           Thread.sleep(500);
        }

        assertCount(200, "*:*");

    }
}
