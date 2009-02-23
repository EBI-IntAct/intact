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

import psidev.psi.mi.tab.model.builder.Row;

/**
 * Gets specific data from a row and returns it as a String. Used to
 * index specific data created using many fields or data transformations.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface RowDataExtractor {

    /**
     * Extracts or calculates a value using the provided data
     * @param row The row containing the data
     * @return can return null if it was not possible to extract a value with the provided data.
     */
    String extractValue(Row row);

    String getFieldName();
}
