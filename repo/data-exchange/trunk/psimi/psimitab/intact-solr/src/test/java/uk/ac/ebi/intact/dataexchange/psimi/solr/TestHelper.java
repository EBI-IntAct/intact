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

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.obo.dataadapter.OBOParseException;
import uk.ac.ebi.intact.bridges.ontologies.OntologyDocument;
import uk.ac.ebi.intact.bridges.ontologies.OntologyIndexWriter;
import uk.ac.ebi.intact.bridges.ontologies.iterator.OboOntologyIterator;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class TestHelper {

    public static Directory buildOntologiesIndex(Map<String,URL> urls) throws Exception {

        Directory ontologyDirectory = new RAMDirectory();
        OntologyIndexWriter writer = new OntologyIndexWriter( ontologyDirectory, true );

        for (Map.Entry<String,URL> entry : urls.entrySet()) {
            addOntologyToIndex( entry.getValue(), entry.getKey() ,writer );
        }

        writer.flush();
        writer.optimize();
        writer.close();

        return ontologyDirectory;
    }

    public static Directory buildDefaultOntologiesIndex() throws Exception {
        Map<String,URL> urls = new HashMap<String,URL>();
        urls.put("go", new URL("http://www.geneontology.org/GO_slims/goslim_generic.obo"));
        urls.put("psi-mi", new URL("http://psidev.sourceforge.net/mi/rel25/data/psi-mi25.obo"));

        return buildOntologiesIndex(urls);
    }

    private static void addOntologyToIndex( URL goUrl, String ontology, OntologyIndexWriter writer ) throws OBOParseException,
                                                                                                            IOException {
        OboOntologyIterator iterator = new OboOntologyIterator( ontology, goUrl );
        while ( iterator.hasNext() ) {
            final OntologyDocument ontologyDocument = iterator.next();
            writer.addDocument( ontologyDocument );
        }
    }

}
