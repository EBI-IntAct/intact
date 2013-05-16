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
package uk.ac.ebi.intact.bridges.ontologies.iterator;

import org.obo.dataadapter.DefaultOBOParser;
import org.obo.dataadapter.OBOParseEngine;
import org.obo.dataadapter.OBOParseException;
import org.obo.datamodel.IdentifiedObject;
import org.obo.datamodel.Link;
import org.obo.datamodel.LinkedObject;
import org.obo.datamodel.OBOSession;
import org.obo.datamodel.impl.OBOClassImpl;
import org.obo.datamodel.impl.SynonymImpl;
import uk.ac.ebi.intact.bridges.ontologies.OntologyDocument;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Reads an OBO File and creates the <code>OntologyDocument<code> iterator.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OboOntologyIterator implements OntologyIterator {

    private String ontology;

    private Iterator<OBOClassImpl> oboClassesIterator;

    private Iterator<OntologyDocument> documentPoolIterator;

    public OboOntologyIterator(String ontology, URL path) throws OBOParseException, IOException {
        this.ontology = ontology;

        DefaultOBOParser parser = new DefaultOBOParser();
        OBOParseEngine engine = new OBOParseEngine(parser);
        engine.setPaths(Arrays.asList(path.toString()));
        engine.parse();

        OBOSession session = parser.getSession();

        List<OBOClassImpl> filteredList = new ArrayList<OBOClassImpl>(session.getObjects().size());

        for (IdentifiedObject idObj : session.getObjects()) {
            if (idObj instanceof OBOClassImpl && !idObj.getID().startsWith("obo:")) {
                filteredList.add((OBOClassImpl)idObj);
            }
        }

        this.oboClassesIterator = filteredList.iterator();
    }

    public OntologyDocument next() {
        if (!documentPoolIterator.hasNext()) {
            throw new NoSuchElementException();
        }

        return documentPoolIterator.next();
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove");
    }

    @SuppressWarnings("unchecked")
    public boolean hasNext() {
        if (documentPoolIterator != null && documentPoolIterator.hasNext()) {
            return true;
        } else if (oboClassesIterator.hasNext()){
            List<OntologyDocument> documentPool = new ArrayList<OntologyDocument>();

            OBOClassImpl oboClass = oboClassesIterator.next();

            String id = oboClass.getID();
            String name = oboClass.getName();

            Set<SynonymImpl> oboSynonyms = oboClass.getSynonyms();
            Set<String> synonyms = synonymsToString(oboSynonyms);

            // a root term?
            if (isARootTerm(oboClass)) {
                OntologyDocument doc = createRootRelationship(id, name, synonyms);
                documentPool.add(doc);
            } else {
                // parents
                for (Link link : oboClass.getParents()) {
                    LinkedObject oboParent = link.getParent();

                    String parentId = oboParent.getID();
                    String parentName = oboParent.getName();

                    String relationshipType = link.getType().getID();
                    boolean cyclicRelationship = link.getType().isCyclic();

                    Set<String> parentSynonyms = getSynonyms(oboParent);

                    OntologyDocument doc = createParentChildRelationship(id, name, synonyms, parentId, parentName, parentSynonyms, relationshipType, cyclicRelationship);
                    documentPool.add(doc);
                }

                // if it is a leaf, add itself to the index as parent with no children
                if (isALeafTerm(oboClass)) {
                    OntologyDocument doc = createLeafRelationship(id, name, synonyms);
                    documentPool.add(doc);
                }
            }

            documentPoolIterator = documentPool.iterator();

           return true;
        }
        return false;
    }



    @SuppressWarnings("unchecked")
    private Set<String> getSynonyms(LinkedObject oboParent) {
        Set<String> parentSynonyms;
        if (oboParent instanceof OBOClassImpl) {
            OBOClassImpl parentOboClass = (OBOClassImpl) oboParent;
            parentSynonyms = synonymsToString(parentOboClass.getSynonyms());
        } else {
            parentSynonyms = Collections.EMPTY_SET;
        }

        return parentSynonyms;
    }

    private Set<String> synonymsToString(Set<SynonymImpl> oboSynonyms) {
        Set<String> synonyms = new HashSet<String>(oboSynonyms.size());

        for (SynonymImpl oboSynonym : oboSynonyms) {
            synonyms.add(oboSynonym.getText());
        }

        return synonyms;
    }

    private OntologyDocument createParentChildRelationship(String id, String name, Set<String> childSynonyms,
                                                           String parentId, String parentName, Set<String> parentSynonyms,
                                                           String relationshipType, boolean cyclicRelationship) {
        final OntologyDocument ontologyDocument = new OntologyDocument(ontology, parentId, parentName, id, name, relationshipType, cyclicRelationship);
        ontologyDocument.addAllChildSynonyms(childSynonyms);
        ontologyDocument.addAllParentSynonyms(parentSynonyms);

        return ontologyDocument;
    }

    @SuppressWarnings("unchecked")
    private OntologyDocument createLeafRelationship(String id, String name, Set<String> synonyms) {
        return createParentChildRelationship(null, null, Collections.EMPTY_SET, id, name, synonyms, null, false);
    }

    @SuppressWarnings("unchecked")
    private OntologyDocument createRootRelationship(String id, String name, Set<String> synonyms) {
        return createParentChildRelationship(id, name, synonyms, null, null, Collections.EMPTY_SET, null, false);
    }

    private boolean isARootTerm(OBOClassImpl oboClass) {
        return oboClass.getParents().isEmpty();
    }

    private boolean isALeafTerm(OBOClassImpl oboClass) {
        return oboClass.getChildren().isEmpty();
    }
}
