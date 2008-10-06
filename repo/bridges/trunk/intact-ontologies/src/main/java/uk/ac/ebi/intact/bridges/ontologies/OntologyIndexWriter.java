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

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OntologyIndexWriter {

    private IndexWriter indexWriter;

    public OntologyIndexWriter(Directory directory, boolean create) throws IOException{
        this.indexWriter = new IndexWriter(directory, new StandardAnalyzer(), create);
    }

    public void addDocument(OntologyDocument ontologyDoc) throws IOException {
        Document doc = new Document();

        doc.add(new Field(FieldName.ONTOLOGY, ontologyDoc.getOntology(), Field.Store.YES, Field.Index.UN_TOKENIZED));

        if (ontologyDoc.getParentId() != null) {
            doc.add(new Field(FieldName.PARENT_ID, ontologyDoc.getParentId(), Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field(FieldName.PARENT_NAME, ontologyDoc.getParentName(), Field.Store.YES, Field.Index.TOKENIZED));
            doc.add(new Field(FieldName.PARENT_NAME_SORTABLE, ontologyDoc.getParentName(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        }

        if (ontologyDoc.getChildId() != null) {
            doc.add(new Field(FieldName.CHILDREN_ID, ontologyDoc.getChildId(), Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field(FieldName.CHILDREN_NAME, ontologyDoc.getChildName(), Field.Store.YES, Field.Index.TOKENIZED));
            doc.add(new Field(FieldName.CHILDREN_NAME_SORTABLE, ontologyDoc.getChildName(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        }

        if (ontologyDoc.getRelationshipType() != null) {
            doc.add(new Field(FieldName.RELATIONSHIP_TYPE, ontologyDoc.getRelationshipType(), Field.Store.YES, Field.Index.UN_TOKENIZED));
        }

        doc.add(new Field(FieldName.RELATIONSHIP_CYCLIC, String.valueOf(ontologyDoc.isCyclicRelationship()),
                          Field.Store.YES, Field.Index.UN_TOKENIZED));


        this.indexWriter.addDocument(doc);
    }

    public IndexWriter getIndexWriter() {
        return indexWriter;
    }

    public void flush() throws IOException {
        indexWriter.flush();
    }

    public void optimize() throws IOException {
        indexWriter.optimize();
    }

    public void close() throws IOException {
        indexWriter.close();
    }
}
