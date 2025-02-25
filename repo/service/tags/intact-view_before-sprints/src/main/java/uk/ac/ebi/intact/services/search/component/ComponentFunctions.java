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
package uk.ac.ebi.intact.services.search.component;

import uk.ac.ebi.intact.services.search.component.resultpanel.CategoryItem;
import uk.ac.ebi.intact.services.search.component.resultpanel.CategoryModel;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment that class header
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public final class ComponentFunctions {

    private ComponentFunctions() {}

    public static Collection<CategoryItem> getItemsByCategory(CategoryModel model, String category) {
        if (model == null) {
            return new ArrayList<CategoryItem>();
        }
        return model.getResultItemsByCategory(category);
    }
}
