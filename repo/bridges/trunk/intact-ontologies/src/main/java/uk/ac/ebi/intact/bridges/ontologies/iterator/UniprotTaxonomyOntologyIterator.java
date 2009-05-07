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

import uk.ac.ebi.intact.bridges.ontologies.OntologyDocument;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

/**
 * Example URL: http://www.uniprot.org/taxonomy/?query=*&limit=10&format=list
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class UniprotTaxonomyOntologyIterator extends LineOntologyIterator {

    private static String BASE_URL = "http://www.uniprot.org/taxonomy/?format=tab&query=";

    public UniprotTaxonomyOntologyIterator(URL url) throws IOException {
        super(url);
    }

    public UniprotTaxonomyOntologyIterator(InputStream is) {
        super(is);
    }

    public UniprotTaxonomyOntologyIterator(Reader reader) {
        super(reader);
    }

    public UniprotTaxonomyOntologyIterator() throws IOException {
       this("*", 0, -1);
    }

    public UniprotTaxonomyOntologyIterator(int offset, int limit) throws IOException {
       this("*", offset, limit);
    }

    public UniprotTaxonomyOntologyIterator(String query, int offset, int limit) throws IOException {
       this(query, offset, limit, true);
    }

    public UniprotTaxonomyOntologyIterator(String query, int offset, int limit, boolean onlyReviewed) throws IOException {
       this(new URL(BASE_URL+query+(onlyReviewed? " AND reviewed:yes" : "")+
               "&offset="+offset+"&limit="+limit));
    }

    @Override
    public boolean skipLine(String line) {
        if (line.startsWith("Taxon")) {
            return true;
        }

        return super.skipLine(line);
    }

    /**
     * Expected tab-delimited columns:
     *
     * <pre>
     * 0 Taxon
     * 1 Mnemonic
     * 2 Scientific Name
     * 3 Common Name
     * 4 Synonym
     * 5 Other Names
     * 6 Reviewed
     * 7 Rank
     * 8 Lineage
     * 9 Parent
     * </pre>

     * @param line The line to process
     * @return the ontology document
     */
    protected OntologyDocument processLine(String line) {
        String[] cols = line.split("\t");

        String childId = cols[0];
        String childName = cols[2];

        String commonName = cols[3];

        if (commonName != null && commonName.length() > 0) {
            childName = childName + " ("+commonName+")";
        }

        String parentId = cols[9];
        String parentName = "";
        String lineage = cols[8];

        // the parent name is the last element in the lineage
        if (lineage.lastIndexOf(";") > -1) {
            parentName = lineage.substring(lineage.lastIndexOf(";", lineage.length())+1).trim();
        }

        OntologyDocument doc = new OntologyDocument("uniprot taxonomy", parentId, parentName,
                childId, childName, "is_a", false);

        return doc;
    }
}
