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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hits;

import java.io.IOException;
import java.util.Iterator;

/**
 * Contains the hits of an ontology search. From the hits it is possible to get the <code>OntologyDocument</code>s hit.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OntologyHits {

    private Hits hits;

    public OntologyHits(Hits hits) {
        this.hits = hits;
    }

    public int length() {
        return hits.length();
    }

    public OntologyDocument doc(int i) throws IOException {
        Document doc = hits.doc(i);

        String ontology = doc.getField(FieldName.ONTOLOGY).stringValue();

        String parentId = null;
        String parentName = null;

        Field parentField = doc.getField(FieldName.PARENT_ID);

        if (parentField != null) {
            parentId = parentField.stringValue();
            parentName = doc.getField(FieldName.PARENT_NAME).stringValue();
        }

        String childrenId = null;
        String childrenName = null;

        Field childrenField = doc.getField(FieldName.CHILDREN_ID);

        if (childrenField != null) {
            childrenId = doc.getField(FieldName.CHILDREN_ID).stringValue();
            childrenName = doc.getField(FieldName.CHILDREN_NAME).stringValue();
        }

        String relationshipType = null;
        Field relationshipTypeField = doc.getField(FieldName.RELATIONSHIP_TYPE);

        if (relationshipTypeField != null) {
            relationshipType = relationshipTypeField.stringValue();
        }

        boolean cyclic = Boolean.valueOf(doc.getField(FieldName.RELATIONSHIP_CYCLIC).stringValue());

        return new OntologyDocument(ontology, parentId, parentName, childrenId, childrenName, relationshipType, cyclic);
    }

    public float score(int i) throws IOException {
        return hits.score(i);
    }

    public int id(int i) throws IOException {
        return hits.id(i);
    }

    public Iterator iterator() {
        return hits.iterator();
    }
}
