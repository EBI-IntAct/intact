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

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import psidev.psi.mi.tab.model.builder.*;
import uk.ac.ebi.intact.psimitab.IntactDocumentDefinition;

import java.util.Collection;

/**
 * Converts from Row to SolrDocument and viceversa.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactSolrDocumentConverter implements SolrDocumentConverter {

    public IntactSolrDocumentConverter() {
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

        // extended
        addColumnToDoc(doc, row, "experimentalRoleA", IntactDocumentDefinition.EXPERIMENTAL_ROLE_A);
        addColumnToDoc(doc, row, "experimentalRoleB", IntactDocumentDefinition.EXPERIMENTAL_ROLE_B);
        addColumnToDoc(doc, row, "biologicalRoleA", IntactDocumentDefinition.BIOLOGICAL_ROLE_A);
        addColumnToDoc(doc, row, "biologicalRoleB", IntactDocumentDefinition.BIOLOGICAL_ROLE_B);
        addColumnToDoc(doc, row, "typeA", IntactDocumentDefinition.INTERACTOR_TYPE_A);
        addColumnToDoc(doc, row, "typeB", IntactDocumentDefinition.INTERACTOR_TYPE_B);
        addColumnToDoc(doc, row, "hostOrganism", IntactDocumentDefinition.HOST_ORGANISM);
        addColumnToDoc(doc, row, "expansion", IntactDocumentDefinition.EXPANSION_METHOD);
        addColumnToDoc(doc, row, "dataset", IntactDocumentDefinition.DATASET);
        addColumnToDoc(doc, row, "annotationA", IntactDocumentDefinition.ANNOTATIONS_A);
        addColumnToDoc(doc, row, "annotationB", IntactDocumentDefinition.ANNOTATIONS_B);
        addColumnToDoc(doc, row, "parameterA", IntactDocumentDefinition.PARAMETERS_A);
        addColumnToDoc(doc, row, "parameterB", IntactDocumentDefinition.PARAMETERS_B);
        addColumnToDoc(doc, row, "parameterInteraction", IntactDocumentDefinition.PARAMETERS_INTERACTION);

        // ac
        Column interactionAcs = row.getColumnByIndex( IntactDocumentDefinition.INTERACTION_ID );
        doc.addField("ac", interactionAcs.getFields().iterator().next().getValue());

        return doc;
    }

    public Row toRow(SolrDocument doc) {
        Row row = new Row();

        FieldBuilder xrefFieldBuilder = new CrossReferenceFieldBuilder();
        FieldBuilder plainFieldBuilder = new PlainTextFieldBuilder();

        row.appendColumn(createColumn(doc, "idA", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "idB", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "altidA", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "altidB", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "aliasA", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "aliasB", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "detmethod_exact", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "pubauth", plainFieldBuilder));
        row.appendColumn(createColumn(doc, "pubid", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "taxidA", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "taxidB", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "type_exact", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "source", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "interaction_id", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "confidence", xrefFieldBuilder));

        row.appendColumn(createColumn(doc, "experimentalRoleA", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "experimentalRoleB", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "biologicalRoleA", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "biologicalRoleB", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "typeA", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "typeB", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "hostOrganism", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "expansion", plainFieldBuilder));
        row.appendColumn(createColumn(doc, "dataset", plainFieldBuilder));
        row.appendColumn(createColumn(doc, "parameterA", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "parameterB", xrefFieldBuilder));
        row.appendColumn(createColumn(doc, "parameterInteraction", xrefFieldBuilder));

        return row;
    }

    private void addColumnToDoc(SolrInputDocument doc, Row row, String fieldName, int columnIndex) {
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

    private void addColumnToRow(Row row, SolrDocument doc, String fieldName, FieldBuilder fieldBuilder) {
        Column column = createColumn(doc, fieldName, fieldBuilder);

    }

    private Column createColumn(SolrDocument doc, String fieldName, FieldBuilder fieldBuilder) {
        Collection<Object> values = doc.getFieldValues(fieldName);

        Column column = new Column();

        for (Object value : values) {
            column.getFields().add(fieldBuilder.createField((String)value));
        }

        return column;
    }
}
