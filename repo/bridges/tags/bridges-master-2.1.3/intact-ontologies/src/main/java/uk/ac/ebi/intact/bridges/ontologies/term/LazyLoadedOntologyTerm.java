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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import uk.ac.ebi.intact.bridges.ontologies.FieldName;
import uk.ac.ebi.intact.bridges.ontologies.OntologyDocument;
import uk.ac.ebi.intact.bridges.ontologies.OntologyHits;
import uk.ac.ebi.intact.bridges.ontologies.OntologyIndexSearcher;

import java.io.IOException;
import java.util.*;

/**
 * A term in an ontology, with parent and children lazy load.
 * When the parents or children are invoked, the data is loaded from the index using an <code>OntologySearcher</code>.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class LazyLoadedOntologyTerm implements OntologyTerm{

    private OntologyIndexSearcher searcher;

    private String id;
    private String name;

    private List<OntologyTerm> parents;
    private List<OntologyTerm> children;

    public LazyLoadedOntologyTerm(OntologyIndexSearcher searcher, String id) {
        this.searcher = searcher;
        this.id = id;

        try {
            final OntologyHits ontologyHits = searcher.searchByParentId(id);

            if (ontologyHits.length() > 0) {
                this.name = ontologyHits.doc(0).getParentName();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem loading name for term: "+id, e);
        }
    }

    public LazyLoadedOntologyTerm(OntologyIndexSearcher searcher, String id, String name) {
        this.searcher = searcher;
        this.id = id;
        this.name = name;
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
            final OntologyHits ontologyHits = searchQuery(FieldName.CHILDREN_ID, includeCyclic);
            parents.addAll(processParentsHits(ontologyHits, id));
        } catch (IOException e) {
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
            final OntologyHits ontologyHits = searchQuery(FieldName.PARENT_ID, includeCyclic);
            children.addAll(processChildrenHits(ontologyHits, id));
        } catch (IOException e) {
            throw new IllegalStateException("Problem getting children for document: "+id, e);
        }

        return children;
    }

    private OntologyHits searchQuery(String idFieldName, boolean includeCyclic) throws IOException {
        BooleanQuery query = new BooleanQuery();
        query.add(new TermQuery(new Term(idFieldName, id)), BooleanClause.Occur.MUST);
        query.add(new TermQuery(new Term(FieldName.RELATIONSHIP_CYCLIC, String.valueOf(includeCyclic))), BooleanClause.Occur.MUST);
        query.add(new TermQuery(new Term(FieldName.RELATIONSHIP_TYPE, "OBO_REL:is_a")), BooleanClause.Occur.MUST);

        if (!includeCyclic) {
            query.add(new TermQuery(new Term(FieldName.RELATIONSHIP_TYPE, "disjoint_from")), BooleanClause.Occur.MUST_NOT);
        }

        final Hits hits = searcher.search(query, new Sort(FieldName.CHILDREN_NAME_SORTABLE));
        final OntologyHits ontologyHits = new OntologyHits(hits);
        return ontologyHits;
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
            return HashMultimap.create();
        }

        Multimap<Integer,OntologyTerm> terms = HashMultimap.create();
        terms.put(currentDepth, term);

        for (OntologyTerm child : term.getChildren()) {
            terms.putAll(getChildren(child, currentDepth+1, maxDepth));
        }

        return terms;
    }

    private List<OntologyTerm> processParentsHits(OntologyHits ontologyHits, String id) throws IOException {
        List<OntologyTerm> terms = new ArrayList<OntologyTerm>();

        List<String> processedIds = new ArrayList<String>();
        processedIds.add(id);

        for (int i=0; i<ontologyHits.length(); i++) {
            final OntologyDocument document = ontologyHits.doc(i);

            if (document.getParentId() != null && !processedIds.contains(document.getParentId())) {
                terms.add(newInternalOntologyTerm(searcher, document.getParentId(), document.getParentName()));
                processedIds.add(document.getParentId());
            }
        }

        return terms;
    }

    private List<OntologyTerm> processChildrenHits(OntologyHits ontologyHits, String id) throws IOException {
        List<OntologyTerm> terms = new ArrayList<OntologyTerm>();

        List<String> processedIds = new ArrayList<String>();
        processedIds.add(id);

        for (int i=0; i<ontologyHits.length(); i++) {
            final OntologyDocument document = ontologyHits.doc(i);

            if (document.getChildId() != null && !processedIds.contains(document.getChildId())) {
                terms.add(newInternalOntologyTerm(searcher, document.getChildId(), document.getChildName()));
                processedIds.add(document.getParentId());
            }
        }

        return terms;
    }

    protected OntologyTerm newInternalOntologyTerm(OntologyIndexSearcher searcher, String id, String name) {
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
