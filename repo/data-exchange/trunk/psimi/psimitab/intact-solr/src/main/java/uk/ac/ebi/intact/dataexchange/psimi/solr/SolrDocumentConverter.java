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

import psidev.psi.mi.tab.model.builder.Column;
import psidev.psi.mi.tab.model.builder.Row;
import psidev.psi.mi.tab.model.builder.Field;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrDocument;
import uk.ac.ebi.intact.psimitab.IntactDocumentDefinition;

/**
 * Converts from Row to SolrDocument and viceversa.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SolrDocumentConverter {

    public SolrDocumentConverter() {
    }

    public SolrInputDocument toSolrDocument(Row row) {
        SolrInputDocument doc = new SolrInputDocument();

        addColumnToDoc(doc, row, "idA", IntactDocumentDefinition.ID_INTERACTOR_A);
        addColumnToDoc(doc, row, "idB", IntactDocumentDefinition.ID_INTERACTOR_B);
        addColumnToDoc(doc, row, "altidA", IntactDocumentDefinition.ALTID_INTERACTOR_A);
        addColumnToDoc(doc, row, "altidB", IntactDocumentDefinition.ALTID_INTERACTOR_B);
        addColumnToDoc(doc, row, "aliasA", IntactDocumentDefinition.ALIAS_INTERACTOR_A);
        addColumnToDoc(doc, row, "aliasB", IntactDocumentDefinition.ALIAS_INTERACTOR_B);
        addColumnToDoc(doc, row, "detmethod_exact", IntactDocumentDefinition.INT_DET_METHOD);
        addColumnToDoc(doc, row, "pubauth", IntactDocumentDefinition.PUB_AUTH);
        addColumnToDoc(doc, row, "pubid", IntactDocumentDefinition.PUB_ID);
        addColumnToDoc(doc, row, "taxidA", IntactDocumentDefinition.TAXID_A);
        addColumnToDoc(doc, row, "taxidB", IntactDocumentDefinition.TAXID_B);
        addColumnToDoc(doc, row, "type_exact", IntactDocumentDefinition.INT_TYPE);
        addColumnToDoc(doc, row, "source", IntactDocumentDefinition.SOURCE);
        addColumnToDoc(doc, row, "interaction_id", IntactDocumentDefinition.INTERACTION_ID);
        addColumnToDoc(doc, row, "confidence", IntactDocumentDefinition.CONFIDENCE);

        addColumnToDoc(doc, row, "propertiesA", IntactDocumentDefinition.PROPERTIES_A);
        addColumnToDoc(doc, row, "propertiesB", IntactDocumentDefinition.PROPERTIES_B);

        // ac
        Column interactionAcs = row.getColumnByIndex( IntactDocumentDefinition.INTERACTION_ID );
        doc.addField("ac", interactionAcs.getFields().iterator().next().getValue());

        return doc;
    }

    public Row toBinaryInteraction(SolrDocument doc) {
        // TODO to implement
        return null;
    }

    private static void addColumnToDoc(SolrInputDocument doc, Row row, String fieldName, int columnIndex) {
        // do not process columns not found in the row
        if (row.getColumnCount() <= columnIndex) {
            return;
        }

        Column column = row.getColumnByIndex( columnIndex );

        for (Field field : column.getFields()) {
            doc.addField(fieldName, field.toString());

            if (field.getType() != null) {
                doc.addField(field.getType()+"_xref", field.getValue());
            }

            if ("go".equals(field.getType()) || "interpro".equals(field.getType()) || "psi-mi".equals(field.getType())) {
                doc.addField(field.getType(), field.getValue());

                if (field.getDescription() != null) {
                    doc.addField("spell", field.getDescription());
                }
            }
        }
    }
}
