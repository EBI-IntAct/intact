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
package uk.ac.ebi.intact.psixml.generator.builder.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ModelClassMetadata {

    private Class modelClass;
    private List<CollectionMetadata> collections;
    private Set<Class> importedClasses;

    public ModelClassMetadata(Class modelClass) {
        this.modelClass = modelClass;

        this.collections = new ArrayList<CollectionMetadata>();
        this.importedClasses = new HashSet<Class>();

        addImportedClass(modelClass);
    }

    public Class getModelClass() {
        return modelClass;
    }

    public List<CollectionMetadata> getCollections() {
        return collections;
    }

    public void setCollections(List<CollectionMetadata> collections) {
        this.collections = collections;

        for (CollectionMetadata col : collections) {
            addImportedClass(col.getType());
        }
    }

    public Set<Class> getImportedClasses() {
        return importedClasses;
    }

    public boolean addImportedClass(Class o) {
        return importedClasses.add(o);
    }
}