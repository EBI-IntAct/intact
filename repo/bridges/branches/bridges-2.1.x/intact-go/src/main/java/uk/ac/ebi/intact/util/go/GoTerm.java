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
package uk.ac.ebi.intact.util.go;

/**
 * Represents a term from the GO
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class GoTerm {

    private String id;
    private String name;
    private String definition;
    private GoTerm category;

    public GoTerm(String id, String name, String definition) {
        this.id = id;
        this.name = name;
        this.definition = definition;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDefinition() {
        return definition;
    }

    public GoTerm getCategory() {
        return category;
    }

    public void setCategory(GoTerm category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return getClass().getName()+"{id="+id+"; name="+name+"; definition="+definition+" ; category="+category+"}";
    }
}
