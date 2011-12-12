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
import java.net.URLEncoder;

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
       this(query, offset, limit, false);
    }

    public UniprotTaxonomyOntologyIterator(String query, int offset, int limit, boolean onlyReviewed) throws IOException {
       this(new URL(BASE_URL + URLEncoder.encode(query+(onlyReviewed? " AND reviewed:yes" : ""), "UTF-8") +
               "&offset=" + offset+
               "&limit=" + limit));
    }

    @Override
    public boolean skipLine(String line) {
        String[] cols = line.split("\t");

        if (line.startsWith("Taxon") || cols.length < 4) {
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

        String childId = safeGet(cols, 0);

        String scientificName = safeGet(cols, 2);
        String commonName = safeGet(cols, 3);
        String synonym = safeGet(cols, 4);
        String[] otherNames = split(safeGet(cols, 5));

        String childName;
        
        if (commonName != null && commonName.length() > 0) {
            childName = commonName;
        } else {
            childName = scientificName;
        }

        String parentId = safeGet(cols, 9);
        String parentName = "";
        String lineage = safeGet(cols, 8);

        // the parent name is the last element in the lineage
        if (lineage.lastIndexOf(";") > -1) {
            parentName = lineage.substring(lineage.lastIndexOf(";", lineage.length())+1).trim();
        }

        OntologyDocument doc = new OntologyDocument("uniprot taxonomy", parentId, parentName,
                childId, childName, "OBO_REL:is_a", false);

        doc.addChildSynonym(scientificName);
        doc.addChildSynonym(synonym);
        doc.addAllChildSynonyms(otherNames);

        return doc;
    }

    private String[] split(String s) {
        if (s == null || s.isEmpty()) return new String[0];
        
        return s.split("; ");
    }

    private String safeGet(String[] cols, int index) {
        if (cols.length > index) {
            return cols[index];
        } else {
            return "";
        }
    }
}
