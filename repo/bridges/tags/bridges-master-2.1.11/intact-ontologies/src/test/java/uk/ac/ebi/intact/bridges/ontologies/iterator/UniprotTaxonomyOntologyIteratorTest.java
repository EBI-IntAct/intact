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
package uk.ac.ebi.intact.bridges.ontologies.iterator;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.bridges.ontologies.OntologyDocument;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class UniprotTaxonomyOntologyIteratorTest {

    @Test
    public void testProcessLine() throws Exception {
        String line = "218834\tLALARAT\tPseudoryzomys simplex\tBrazilian false rice rat\tHappyRat\tUglyRat; DeadRat\tannotated\tSpecies\tEukaryota; Metazoa; Chordata; Craniata; Vertebrata; Euteleostomi; Mammalia; Eutheria; Euarchontoglires; Glires; Rodentia; Sciurognathi; Muroidea; Cricetidae; Sigmodontinae; Pseudoryzomys\t218833";

        UniprotTaxonomyOntologyIterator iterator = new UniprotTaxonomyOntologyIterator();
        OntologyDocument ontologyDocument = iterator.processLine(line);

        Assert.assertEquals("218834", ontologyDocument.getChildId());
        Assert.assertEquals("Pseudoryzomys simplex", ontologyDocument.getChildName());
        Assert.assertEquals("218833", ontologyDocument.getParentId());
        Assert.assertEquals("Pseudoryzomys", ontologyDocument.getParentName());
        Assert.assertEquals(4, ontologyDocument.getChildSynonyms().size());
    }

    @Test
    public void testProcessFile() {
        InputStream is = UniprotTaxonomyOntologyIteratorTest.class.getResourceAsStream("/META-INF/rat_taxonomy_uniprot.tsv");

        UniprotTaxonomyOntologyIterator iterator = new UniprotTaxonomyOntologyIterator(is);

        int count = 0;

        while (iterator.hasNext()) {
            OntologyDocument ontologyDocument = iterator.next();
            count++;
        }
        
        Assert.assertEquals(117, count);
    }

    @Test
    public void testProcessEmptyFile() throws Exception {
        String line = "Taxon\tMnemonic\tScientific Name\tCommon Name\tSynonym\tOther Names\tReviewed\tRank\tLineage\tParent";

         UniprotTaxonomyOntologyIterator iterator = new UniprotTaxonomyOntologyIterator(new ByteArrayInputStream(line.getBytes()));

         Assert.assertFalse(iterator.hasNext());

     }

    @Test
    public void testProcessEmptyFile2() throws Exception {
        String line = "Taxon\tMnemonic\tScientific Name\tCommon Name\tSynonym\tOther Names\tReviewed\tRank\tLineage\tParent\n"+
                "44433";

         UniprotTaxonomyOntologyIterator iterator = new UniprotTaxonomyOntologyIterator(new ByteArrayInputStream(line.getBytes()));

         Assert.assertFalse(iterator.hasNext());

     }

    @Test
    public void testProcessLine_onlyTaxon() throws Exception {
        InputStream is = UniprotTaxonomyOntologyIteratorTest.class.getResourceAsStream("/META-INF/rat_taxonomy_uniprot2.tsv");

        UniprotTaxonomyOntologyIterator iterator = new UniprotTaxonomyOntologyIterator(is);

        int count = 0;

        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }

        Assert.assertEquals(1, count);
    }

    @Test
    public void processLine_recursive() throws Exception {
        StringBuilder sb = new StringBuilder();

        for (int i=0; i<100000; i++) {
            sb.append("12345\n");
        }

        UniprotTaxonomyOntologyIterator iterator = new UniprotTaxonomyOntologyIterator(new ByteArrayInputStream(sb.toString().getBytes()));

        int count = 0;

        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }

        Assert.assertEquals(0, count);
    }

}
