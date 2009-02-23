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
package uk.ac.ebi.intact.dataexchange.psimi.solr.converter.impl;

import psidev.psi.mi.tab.model.builder.Column;
import psidev.psi.mi.tab.model.builder.Field;
import psidev.psi.mi.tab.model.builder.Row;
import uk.ac.ebi.intact.dataexchange.psimi.solr.FieldNames;
import uk.ac.ebi.intact.dataexchange.psimi.solr.converter.RowDataExtractor;

/**
 * Creates a String that looks like "MI:1234 EBI-12345"
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class TypeAndAcRowDataExtractor implements RowDataExtractor {

    private String databaseLabel = "intact";
    private int columnId;
    private int columnInteractorType;

    private String fieldName;

    public TypeAndAcRowDataExtractor(int columnId, int columnInteractorType) {
        this.columnId = columnId;
        this.columnInteractorType = columnInteractorType;
    }

    public TypeAndAcRowDataExtractor(int columnId, int columnInteractorType, String databaseLabel) {
        this.columnId = columnId;
        this.columnInteractorType = columnInteractorType;
        this.databaseLabel = databaseLabel;
    }

    public String extractValue(Row row) {
        if (row.getColumnCount() <= columnInteractorType) {
            return null;
        }
        
        Column colId = row.getColumnByIndex(columnId);
        Column colType = row.getColumnByIndex(columnInteractorType);

        String ac = getAccession(colId);
        String typeMi = getInteractorTypeMi(colType);

        if (ac == null || typeMi == null) {
            return null;
        }

        // e.g. acByInteractorType_mi1234
        fieldName = FieldNames.AC_BY_INTERACTOR_TYPE_PREFIX+(typeMi.replaceAll(":", "").toLowerCase());

        return ac;
    }

    public String getFieldName() {
        if (fieldName == null) throw new IllegalStateException("Invoke extractValue(Row) first");
        return fieldName;
    }

    private String getAccession(Column colId) {
        for (Field field : colId.getFields()) {
            if (databaseLabel.equals(field.getType())) {
                return field.getValue();
            }
        }

        return null;
    }

    private String getInteractorTypeMi(Column colType) {
        for (Field field : colType.getFields()) {
            if ("psi-mi".equals(field.getType())) {
                return field.getValue();
            }
        }

        return null;
    }
}
