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
package uk.ac.ebi.intact.view.webapp.controller.browse;

import org.apache.lucene.search.IndexSearcher;
import uk.ac.ebi.intact.bridges.ontologies.term.OntologyTerm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Ontology term wrapper.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OntologyTermWrapper {

    private OntologyTerm term;
    private int interactionCount;
    private int interactorCount;
    private IndexSearcher interactionIndexSearcher;
    private IndexSearcher interactorIndexSearcher;
    private String baseQuery;
    private String interactorSearchQuery;
    private String interactionSearchQuery;
    private String luceneQuery;

    private OntologyTermWrapper parent;

    private List<OntologyTermWrapper> children;

    private String interactionColour;
    private String interactorColour;

    protected OntologyTermWrapper(OntologyTerm term, IndexSearcher interactionIndexSearcher, IndexSearcher interactorIndexSearcher, String baseQuery,String luceneQuery) {
        this(term, interactionIndexSearcher, interactorIndexSearcher, baseQuery,luceneQuery, true);
    }

    protected OntologyTermWrapper(OntologyTerm term, IndexSearcher interactionIndexSearcher, IndexSearcher interactorIndexSearcher, String baseQuery, String luceneQuery, boolean countInteractors) {
        this.term = term;
        this.interactionIndexSearcher = interactionIndexSearcher;
        this.interactorIndexSearcher = interactorIndexSearcher;
        this.baseQuery = baseQuery;
        this.luceneQuery = luceneQuery;

        this.interactorSearchQuery = prepareProteinQuery(term.getId(), luceneQuery);
        this.interactionSearchQuery = prepareInteractionQuery(term.getId(), luceneQuery);

        if (term == null) throw new NullPointerException("Term is necessary");

    }

    public OntologyTerm getTerm() {
        return term;
    }

    public int getInteractorCount() {
        return interactorCount;
    }

    public void setInteractorCount(int interactorCount) {
        this.interactorCount = interactorCount;
    }

    public void add(int countToAdd) {
        this.interactorCount = this.interactorCount + countToAdd;
    }

    public List<OntologyTermWrapper> getChildren() {
        if (children != null) {
            return children;
        }

        children = new ArrayList<OntologyTermWrapper>();

        for (OntologyTerm child : term.getChildren()) {
            OntologyTermWrapper otwChild = new OntologyTermWrapper(child, interactionIndexSearcher, interactorIndexSearcher, baseQuery, luceneQuery);

            children.add(otwChild);
            otwChild.setParent(this);
        }

        Collections.sort(children, new OntologyTermWrapperComparator());

        return children;
    }

    private String prepareInteractionQuery( String id, String luceneQuery ) {
        return prepareQuery( "properties", id, luceneQuery );
    }

    private String prepareProteinQuery( String id, String luceneQuery ) {
        return prepareQuery( "propertiesA", id, luceneQuery );
    }

    private String prepareQuery(String field, String id, String baseQuery) {
        StringBuilder query = new StringBuilder( (baseQuery == null ? 0 : baseQuery.length()) + 32);

        if (baseQuery != null && !baseQuery.isEmpty() &&
            !baseQuery.equals("*") && !baseQuery.equals("?")) {
            query.append("(").append(baseQuery).append(") AND ");
        }

        query.append(field + ":\"").append(id).append("\"");

        return query.toString();
    }

    public OntologyTermWrapper getParent() {
        return parent;
    }

    public void setParent(OntologyTermWrapper parent) {
        this.parent = parent;
    }

    public Integer getChildrenInteractorTotalCount() {
        int totalCount = 0;

        for (OntologyTermWrapper child : getChildren()) {
            totalCount = totalCount + child.getInteractorCount();
        }

        return totalCount;
    }

    public int getChildrenInteractionTotalCount() {
        int totalCount = 0;

        for (OntologyTermWrapper child : getChildren()) {
            totalCount = totalCount + child.getInteractionCount();
        }

        return totalCount;
    }

    public String getInteractorColour() {
        return interactorColour;
    }

    public void setInteractorColour(String interactorColour) {
        this.interactorColour = interactorColour;
    }

    public String getInteractionColour() {
        return interactionColour;
    }

    public void setInteractionColour(String interactionColour) {
        this.interactionColour = interactionColour;
    }

    public int getInteractionCount() {
        return interactionCount;
    }

    public void setInteractionCount(int interactionCount) {
        this.interactionCount = interactionCount;
    }

    public String getInteractorSearchQuery() {
        return interactorSearchQuery;
    }

    public void setInteractorSearchQuery(String searchQuery) {
        this.interactorSearchQuery = searchQuery;
    }

    public String getInteractionSearchQuery() {
        return interactionSearchQuery;
    }

    public void setInteractionSearchQuery(String searchQuery) {
        this.interactionSearchQuery = searchQuery;
    }

    public String getBaseQuery() {
        return baseQuery;
    }

    public void setBaseQuery( String baseQuery ) {
        this.baseQuery = baseQuery;
    }

    public String getLuceneQuery() {
        return luceneQuery;
    }

    public void setLuceneQuery( String luceneQuery ) {
        this.luceneQuery = luceneQuery;
    }

    private class OntologyTermWrapperComparator implements Comparator<OntologyTermWrapper> {

        public int compare(OntologyTermWrapper o1, OntologyTermWrapper o2) {
            if (o1.getInteractorCount() > o2.getInteractorCount()) {
                return -1;
            } else {
                return +1;
            }
        }
    }

    @Override
    public String toString() {
        return "OntologyTermWrapper{"+term.getId()+"}";
    }
}
