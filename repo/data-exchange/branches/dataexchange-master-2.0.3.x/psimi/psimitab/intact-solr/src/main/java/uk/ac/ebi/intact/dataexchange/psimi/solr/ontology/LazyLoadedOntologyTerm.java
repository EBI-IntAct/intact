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
package uk.ac.ebi.intact.dataexchange.psimi.solr.ontology;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import uk.ac.ebi.intact.bridges.ontologies.FieldName;
import uk.ac.ebi.intact.bridges.ontologies.term.OntologyTerm;

import java.io.IOException;
import java.util.*;

/**
 * A term in an ontology, with parent and children lazy load.
 * When the parents or children are invoked, the data is loaded from the index using an <code>OntologySearcher</code>.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class LazyLoadedOntologyTerm implements OntologyTerm {

    private OntologySearcher searcher;

    private String id;
    private String name;

    private List<OntologyTerm> parents;
    private List<OntologyTerm> children;

    public LazyLoadedOntologyTerm(OntologySearcher searcher, String id) throws SolrServerException {
        this( searcher, id, null );
    }

    public LazyLoadedOntologyTerm(OntologySearcher searcher, String id, String name) throws SolrServerException {
        this.searcher = searcher;
        this.id = id;
        this.name = findName(searcher, id, name);
    }

    private String findName(OntologySearcher searcher, String id, String defaultValue) throws SolrServerException {
        QueryResponse queryResponse = searcher.searchByChildId(id, 0, 1);

        String parentName = null;
        if (queryResponse.getResults().getNumFound() > 0) {
            parentName = (String) queryResponse.getResults().iterator().next().getFieldValue( OntologyFieldNames.CHILD_NAME);
        }

        return parentName == null ? defaultValue : parentName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<OntologyTerm> getParents() {
        return getParents(false);
    }

    public List<OntologyTerm> getParents(boolean includeCyclic) {
        if (parents != null) {
            return parents;
       }

        this.parents = new ArrayList<OntologyTerm>();

        try {
            final QueryResponse queryResponse = searchQuery(FieldName.CHILDREN_ID, includeCyclic);

            parents.addAll(processParentsHits(queryResponse, id));
        } catch (Exception e) {
            throw new IllegalStateException("Problem getting parents for document: "+id, e);
        }

        return parents;
    }

    public List<OntologyTerm> getChildren() {
        return getChildren(false);
    }

    public List<OntologyTerm> getChildren(boolean includeCyclic) {
        if (children != null) {
            return children;
        }

        this.children = new ArrayList<OntologyTerm>();

        try {
            final QueryResponse queryResponse = searchQuery(FieldName.PARENT_ID, includeCyclic);
            children.addAll(processChildrenHits(queryResponse, id));
        } catch (Exception e) {
            throw new IllegalStateException("Problem getting children for document: "+id, e);
        }

        return children;
    }

    private QueryResponse searchQuery(String idFieldName, boolean includeCyclic) throws SolrServerException {
//        BooleanQuery query = new BooleanQuery();
//        query.add(new TermQuery(new Term(idFieldName, id)), BooleanClause.Occur.MUST);
//        query.add(new TermQuery(new Term(FieldName.RELATIONSHIP_CYCLIC, String.valueOf(includeCyclic))), BooleanClause.Occur.MUST);
//        query.add(new TermQuery(new Term(FieldName.RELATIONSHIP_TYPE, "OBO_REL:is_a")), BooleanClause.Occur.MUST);
//
//        if (!includeCyclic) {
//            query.add(new TermQuery(new Term(FieldName.RELATIONSHIP_TYPE, "disjoint_from")), BooleanClause.Occur.MUST_NOT);
//        }

        SolrQuery query = new SolrQuery(idFieldName+":\""+id+"\"");
        query.setRows(Integer.MAX_VALUE);
        query.addFilterQuery("+"+OntologyFieldNames.CYCLIC+":"+String.valueOf(includeCyclic));
        query.addFilterQuery("+"+OntologyFieldNames.RELATIONSHIP_TYPE+":\"OBO_REL:is_a\"");

        if (!includeCyclic) {
            query.addFilterQuery("-"+OntologyFieldNames.RELATIONSHIP_TYPE+":disjoint_from");
        }

        query.setSortField(OntologyFieldNames.CHILD_NAME+"_s", SolrQuery.ORDER.asc);

        return searcher.search(query);
    }

    public Set<OntologyTerm> getAllParentsToRoot() {
        return getAllParentsToRoot(this);
    }

    protected Set<OntologyTerm> getAllParentsToRoot(OntologyTerm ontologyTerm) {
        Set<OntologyTerm> parents = new HashSet<OntologyTerm>();

        for (OntologyTerm parent : ontologyTerm.getParents()) {
            parents.add(parent);
            parents.addAll(getAllParentsToRoot(parent));
        }

        return parents;
    }

    public Collection<OntologyTerm> getChildrenAtDepth(int depth) {
        return getChildren(this, 0, depth).get(depth);
    }

    protected Multimap<Integer, OntologyTerm> getChildren(OntologyTerm term, int currentDepth, int maxDepth) {
        if (currentDepth > maxDepth) {
            return new HashMultimap<Integer,OntologyTerm>();
        }

        Multimap<Integer,OntologyTerm> terms = new HashMultimap<Integer,OntologyTerm>();
        terms.put(currentDepth, term);

        for (OntologyTerm child : term.getChildren()) {
            terms.putAll(getChildren(child, currentDepth+1, maxDepth));
        }

        return terms;
    }

    private List<OntologyTerm> processParentsHits(QueryResponse queryResponse, String id) throws IOException, SolrServerException {
        List<OntologyTerm> terms = new ArrayList<OntologyTerm>();

        List<String> processedIds = new ArrayList<String>();
        processedIds.add(id);

        for (SolrDocument solrDocument : queryResponse.getResults()) {

            String parentId = (String) solrDocument.getFieldValue(OntologyFieldNames.PARENT_ID);
            String parentName = (String) solrDocument.getFieldValue(OntologyFieldNames.PARENT_NAME);

            if (parentId != null && !processedIds.contains(parentId)) {
                terms.add(newInternalOntologyTerm(searcher, parentId, parentName));
                processedIds.add(parentId);
            }
        }

        return terms;
    }

    private List<OntologyTerm> processChildrenHits(QueryResponse queryResponse, String id) throws IOException, SolrServerException {
        List<OntologyTerm> terms = new ArrayList<OntologyTerm>();

        List<String> processedIds = new ArrayList<String>();
        processedIds.add(id);

        for (SolrDocument solrDocument : queryResponse.getResults()) {

            String childId = (String) solrDocument.getFieldValue(OntologyFieldNames.CHILD_ID);
            String childName = (String) solrDocument.getFieldValue(OntologyFieldNames.CHILD_NAME);

            if (childId != null && !processedIds.contains(childId)) {
                terms.add(newInternalOntologyTerm(searcher, childId, childName));
                processedIds.add(childId);
            }
        }

        return terms;
    }

    protected OntologyTerm newInternalOntologyTerm(OntologySearcher searcher, String id, String name) throws SolrServerException {
        return new LazyLoadedOntologyTerm(searcher, id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LazyLoadedOntologyTerm that = (LazyLoadedOntologyTerm) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LazyLoadedOntologyTerm");
        sb.append("{id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", parents=").append((parents == null)? "[NOT LOADED]" : parents);
        sb.append(", children=").append((children == null)? "[NOT LOADED]" : children);
        sb.append('}');
        return sb.toString();
    }
}

