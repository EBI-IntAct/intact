/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.bridges.ontologies;

import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.*;
import org.obo.dataadapter.OBOParseException;
import uk.ac.ebi.intact.bridges.ontologies.iterator.OboOntologyIterator;
import uk.ac.ebi.intact.bridges.ontologies.iterator.OntologyIterator;

import java.io.IOException;
import java.net.URL;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OntologyIndexSearcherTest {

    private static Directory directory;
    private OntologyIndexSearcher searcher;

    @BeforeClass
    public static void beforeClass() throws Exception {
        directory = new RAMDirectory();
        createIndex();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        directory.close();
        directory = null;
    }

    @Before
    public void before() throws Exception {
        searcher = new OntologyIndexSearcher(directory);
    }

    @After
    public void after() throws Exception {
        searcher.close();
        searcher = null;
    }

    private static void createIndex() throws OBOParseException, IOException {
        final URL goSlimUrl = OntologyIndexSearcherTest.class.getResource("/META-INF/goslim_generic.obo");

        OntologyIterator ontologyIterator = new OboOntologyIterator("go", goSlimUrl);

        OntologyIndexWriter indexer = new OntologyIndexWriter(directory,true);

        while (ontologyIterator.hasNext()) {
            OntologyDocument document = ontologyIterator.next();
            indexer.addDocument(document);
        }

        indexer.flush();
        indexer.optimize();
        indexer.close();
    }

    @Test
    public void searchByParent_default() throws Exception {
        final OntologyHits ontologyHits = searcher.searchByParentId("GO:0008150", new Sort(FieldName.CHILDREN_NAME_SORTABLE));
        Assert.assertEquals(24, ontologyHits.length());

        Assert.assertEquals("GO:0009653", ontologyHits.doc(0).getChildId());
        Assert.assertEquals("GO:0007610", ontologyHits.doc(1).getChildId());
        Assert.assertEquals("GO:0007154", ontologyHits.doc(2).getChildId());
        Assert.assertEquals("cell cycle", ontologyHits.doc(3).getChildName());
        Assert.assertEquals("cell differentiation", ontologyHits.doc(4).getChildName());
        Assert.assertEquals("GO:0016032", ontologyHits.doc(23).getChildId());
    }

    @Test
    public void searchByChild_default() throws Exception {
        final OntologyHits ontologyHits = searcher.searchByChildId("GO:0030154", new Sort(FieldName.CHILDREN_NAME_SORTABLE));
        Assert.assertEquals(1, ontologyHits.length());

        Assert.assertEquals("GO:0008150", ontologyHits.doc(0).getParentId());
        Assert.assertEquals("biological_process", ontologyHits.doc(0).getParentName());

    }

    @Test
    public void searchByChild_checkSynonyms() throws Exception {
        final OntologyHits ontologyHits = searcher.searchByChildId("GO:0007010", new Sort(FieldName.CHILDREN_NAME_SORTABLE));
        Assert.assertEquals(1, ontologyHits.length());

        OntologyDocument doc = ontologyHits.doc(0);

        Assert.assertEquals(3, doc.getChildSynonyms().size());
    }
}