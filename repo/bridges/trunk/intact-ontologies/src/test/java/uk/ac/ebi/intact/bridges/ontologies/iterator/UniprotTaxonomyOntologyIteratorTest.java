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

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;
import uk.ac.ebi.intact.bridges.ontologies.OntologyDocument;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.NoSuchElementException;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class UniprotTaxonomyOntologyIteratorTest {

    @Test
    public void testProcessLine() throws Exception {
        String line = "218834\t\tPseudoryzomys simplex\tBrazilian false rice rat\t\t\tannotated\tSpecies\tEukaryota; Metazoa; Chordata; Craniata; Vertebrata; Euteleostomi; Mammalia; Eutheria; Euarchontoglires; Glires; Rodentia; Sciurognathi; Muroidea; Cricetidae; Sigmodontinae; Pseudoryzomys\t218833";

        UniprotTaxonomyOntologyIterator iterator = new UniprotTaxonomyOntologyIterator();
        OntologyDocument ontologyDocument = iterator.processLine(line);

        Assert.assertEquals("218834", ontologyDocument.getChildId());
        Assert.assertEquals("Pseudoryzomys simplex (Brazilian false rice rat)", ontologyDocument.getChildName());
        Assert.assertEquals("218833", ontologyDocument.getParentId());
        Assert.assertEquals("Pseudoryzomys", ontologyDocument.getParentName());
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

    @Test (expected = NoSuchElementException.class)
    public void testProcessEmptyFile() throws Exception {
        String line = "Taxon\tMnemonic\tScientific Name\tCommon Name\tSynonym\tOther Names\tReviewed\tRank\tLineage\tParent";

         UniprotTaxonomyOntologyIterator iterator = new UniprotTaxonomyOntologyIterator(new ByteArrayInputStream(line.getBytes()));

         while (iterator.hasNext()) {
             iterator.next();
         }

     }

}
