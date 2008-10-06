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

import uk.ac.ebi.intact.bridges.ontologies.OntologyDocument;
import uk.ac.ebi.intact.bridges.ontologies.OntologyHits;
import uk.ac.ebi.intact.bridges.ontologies.OntologyIndexSearcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * When the parents or children are called, the data is loaded from the index using the searcher.
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
        if (parents != null) {
            return parents;
       }

        this.parents = new ArrayList<OntologyTerm>();

        try {
            final OntologyHits ontologyHits = searcher.searchByChildId(id);
             parents.addAll(processParentsHits(ontologyHits, id));
        } catch (IOException e) {
            throw new IllegalStateException("Problem getting parents for document: "+id, e);
        }

        return parents;
    }



    public List<OntologyTerm> getChildren() {
        if (children != null) {
            return children;
        }

        this.children = new ArrayList<OntologyTerm>();

        try {
            final OntologyHits ontologyHits = searcher.searchByParentId(id);
            children.addAll(processChildrenHits(ontologyHits, id));
        } catch (IOException e) {
            throw new IllegalStateException("Problem getting children for document: "+id, e);
        }

        return children;
    }

    private List<OntologyTerm> processParentsHits(OntologyHits ontologyHits, String id) throws IOException {
        List<OntologyTerm> terms = new ArrayList<OntologyTerm>();

        List<String> processedIds = new ArrayList<String>();
        processedIds.add(id);

        for (int i=0; i<ontologyHits.length(); i++) {
            final OntologyDocument document = ontologyHits.doc(i);

            if (!processedIds.contains(document.getParentId())) {
                terms.add(new LazyLoadedOntologyTerm(searcher, document.getParentId(), document.getParentName()));
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

            if (!processedIds.contains(document.getChildId())) {
                terms.add(new LazyLoadedOntologyTerm(searcher, document.getChildId(), document.getChildName()));
                processedIds.add(document.getParentId());
            }
        }

        return terms;
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
