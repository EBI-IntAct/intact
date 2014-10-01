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
package uk.ac.ebi.intact.bridges.ontologies.term;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Represents a term in an ontology.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public interface OntologyTerm {

    /**
     * Id of the term.
     * @return the id
     */
    String getId();

    /**
     * Name of the term.
     * @return the name
     */
    String getName();

    /**
     * Parents of the term.
     * @return the parents
     */
    List<OntologyTerm> getParents();

    /**
     * Children of the term.
     * @return the children
     */
    List<OntologyTerm> getChildren();

    /**
     * Parents of the term.
     * @param includeCyclic if true, include the cyclic relationships.
     * @return the parents
     */
    List<OntologyTerm> getParents(boolean includeCyclic);

    /**
     * Children of the term.
     * @param includeCyclic if true, include the cyclic relationships.
     * @return the children
     */
    List<OntologyTerm> getChildren(boolean includeCyclic);

    /**
     * Gets the synonyms for this ontology term.
     * @return the synonyms
     */
    Set<OntologyTerm> getSynonyms();

    /**
     * Gets a set that contains all the parent terms until the root is reached. The root is included.
     * @return The set with the parents
     */
    Set<OntologyTerm> getAllParentsToRoot();

    /**
     * Gets a set that contains all the parent terms until the root is reached. The root is included.
     * @param includeSynonyms
     * @return The set with the parents
     */
    Set<OntologyTerm> getAllParentsToRoot(boolean includeSynonyms);

    /**
     * Gets the children at a certain level of depth. For instance, if we use depth 2 the collection
     * returned by this method will contain the grandchildren of the term. Depth 1 would be the direct children,
     * 3 the grandgrandchildren and so on.
     * @param depth 1 children, 2 grandchildren, 3 grandgranchildren ... Depth 0 returns itself and higher depths may return empty lists.
     * @return The children at that level of depth.
     */
    Collection<OntologyTerm> getChildrenAtDepth(int depth);
}
