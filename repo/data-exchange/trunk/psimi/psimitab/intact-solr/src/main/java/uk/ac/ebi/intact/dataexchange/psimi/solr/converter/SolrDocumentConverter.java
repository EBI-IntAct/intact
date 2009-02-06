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
package uk.ac.ebi.intact.dataexchange.psimi.solr.converter;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import psidev.psi.mi.tab.model.builder.*;
import psidev.psi.mi.tab.model.BinaryInteraction;
import uk.ac.ebi.intact.psimitab.IntactDocumentDefinition;

import java.util.Collection;

/**
 * Converts from Row to SolrDocument and viceversa.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SolrDocumentConverter {

    private DocumentDefinition documentDefintion;

    public SolrDocumentConverter(DocumentDefinition documentDefintion) {
        this.documentDefintion = documentDefintion;
    }

    public SolrInputDocument toSolrDocument(String mitabLine) {
        Row row = documentDefintion.createRowBuilder().createRow(mitabLine);
        return toSolrDocument(row, mitabLine);
    }

    public SolrInputDocument toSolrDocument(BinaryInteraction binaryInteraction) {
        Row row = documentDefintion.createInteractionRowConverter().createRow(binaryInteraction);
        return toSolrDocument(row);
    }

    public SolrInputDocument toSolrDocument(Row row) {
        return toSolrDocument(row, row.toString());
    }

    protected SolrInputDocument toSolrDocument(Row row, String mitabLine) {
        SolrInputDocument doc = new SolrInputDocument();

        // store the mitab line
        doc.addField("line", mitabLine);

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
        if (documentDefintion instanceof IntactDocumentDefinition) {
            addColumnToDoc(doc, row, "experimentalRoleA", IntactDocumentDefinition.EXPERIMENTAL_ROLE_A);
            addColumnToDoc(doc, row, "experimentalRoleB", IntactDocumentDefinition.EXPERIMENTAL_ROLE_B);
            addColumnToDoc(doc, row, "biologicalRoleA", IntactDocumentDefinition.BIOLOGICAL_ROLE_A);
            addColumnToDoc(doc, row, "biologicalRoleB", IntactDocumentDefinition.BIOLOGICAL_ROLE_B);
            addColumnToDoc(doc, row, "propertiesA", IntactDocumentDefinition.PROPERTIES_A);
            addColumnToDoc(doc, row, "propertiesB", IntactDocumentDefinition.PROPERTIES_B);
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
        }

        // ac
        Column interactionAcs = row.getColumnByIndex( IntactDocumentDefinition.INTERACTION_ID );
        doc.addField("ac", interactionAcs.getFields().iterator().next().getValue());

        return doc;
    }

    public BinaryInteraction toBinaryInteraction(SolrDocument doc) {
        return documentDefintion.interactionFromString(toMitabLine(doc));
    }

    public Row toRow(SolrDocument doc) {
        return documentDefintion.createRowBuilder().createRow(toMitabLine(doc));
    }

    public String toMitabLine(SolrDocument doc) {
        return (String) doc.getFieldValue("line");
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

        if (column != null) {
            row.appendColumn(column);
        }
    }

    private Column createColumn(SolrDocument doc, String fieldName, FieldBuilder fieldBuilder) {
        Collection<Object> values = doc.getFieldValues(fieldName);

        Column column = null;

        for (Object value : values) {
            if (column == null) column = new Column();

            column.getFields().add(fieldBuilder.createField((String)value));
        }

        return column;
    }
}
