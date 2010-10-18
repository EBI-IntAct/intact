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
package uk.ac.ebi.intact.bridges.ontologies.util;

import org.apache.lucene.store.Directory;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.obo.dataadapter.OBOParseException;
import uk.ac.ebi.intact.bridges.ontologies.OntologyDocument;
import uk.ac.ebi.intact.bridges.ontologies.OntologyIndexWriter;
import uk.ac.ebi.intact.bridges.ontologies.OntologyMapping;
import uk.ac.ebi.intact.bridges.ontologies.iterator.OboOntologyIterator;

import java.io.IOException;

/**
 * Convenience methods.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public final class OntologyUtils {

    private static final Log log = LogFactory.getLog( OntologyUtils.class );

    private OntologyUtils() {}

    /**
     * Creates or adds documents to an index.
     * @param ontologyIndex The index to use
     * @param mappings Mappings of ontology name/urls
     * @param create If true, remove the index if existed. Otherwise, add to the index
     * @throws OBOParseException thrown if there is a problem parsing the OBO file
     * @throws IOException thrown if there is a problem writing to the index
     */
    public static void buildIndexFromObo(Directory ontologyIndex, OntologyMapping[] mappings, boolean create) throws OBOParseException,
                                                                                            IOException {
        OntologyIndexWriter writer = new OntologyIndexWriter( ontologyIndex, create );

        for (OntologyMapping mapping : mappings) {
            addOboOntologyToIndex( mapping, writer );
        }

        writer.flush();
        writer.optimize();
        writer.close();
    }

    private static void addOboOntologyToIndex( OntologyMapping mapping, OntologyIndexWriter writer ) throws OBOParseException,
                                                                                                            IOException {
        final long start = System.currentTimeMillis();
        OboOntologyIterator iterator = new OboOntologyIterator( mapping.getName(), mapping.getUrl() );
        if ( log.isDebugEnabled() ) log.debug( "Starting to index " + mapping.getName() + " (URL: "+ mapping.getUrl() +")");

        int count = 0;
        while ( iterator.hasNext() ) {
            count++;
            if ( log.isTraceEnabled() && ( (count % 1000 ) == 0 ) ) {
                log.trace( "Processed " + count  + " " + mapping.getName() + " terms" );
            }

            final OntologyDocument ontologyDocument = iterator.next();
            writer.addDocument( ontologyDocument );
        }

        final long stop = System.currentTimeMillis();
        if ( log.isDebugEnabled() ) log.debug( "Completed indexing of " + mapping.getName() + " in " + (stop - start) + "ms" );
    }
}
