/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.unit;

import uk.ac.ebi.intact.commons.dataset.TestDatasetProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD})
public @interface IntactUnitDataset {

    /**
     * Provider to use (must contain the dataset)
     */
    Class<? extends TestDatasetProvider> provider();

    /**
     * Dataset to import
     */
    String dataset();

}



