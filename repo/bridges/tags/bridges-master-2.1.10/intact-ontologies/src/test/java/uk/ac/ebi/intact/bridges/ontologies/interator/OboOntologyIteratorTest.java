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
package uk.ac.ebi.intact.bridges.ontologies.interator;

import junit.framework.Assert;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.bridges.ontologies.OntologyDocument;
import uk.ac.ebi.intact.bridges.ontologies.iterator.OboOntologyIterator;
import uk.ac.ebi.intact.bridges.ontologies.iterator.OntologyIterator;

import java.net.URL;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OboOntologyIteratorTest {

    private Directory directory;

    @Before
    public void before() throws Exception {
        directory = new RAMDirectory();
    }

    @After
    public void after() throws Exception {
        directory.close();
        directory = null;
    }
    
    @Test
    public void next() throws Exception {
        final URL goUrl = OboOntologyIteratorTest.class.getResource("/META-INF/goslim_generic.obo");

        OntologyIterator ontologyIterator = new OboOntologyIterator("go", goUrl);

        int count = 0;

        while (ontologyIterator.hasNext()) {
            OntologyDocument document = ontologyIterator.next();
            Assert.assertNotNull(document);
            count++;
        }

        Assert.assertEquals(241, count);
    }
}