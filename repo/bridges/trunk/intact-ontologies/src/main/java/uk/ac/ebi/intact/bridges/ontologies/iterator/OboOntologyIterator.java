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
import uk.ac.ebi.intact.bridges.ontologies.OntologyDocument;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * TODO comment that class header
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

    public OntologyDocument nextOntologyDocument() {
        if (!documentPoolIterator.hasNext()) {
            throw new NoSuchElementException();
        }

        return documentPoolIterator.next();
    }

    public boolean hasNext() {
        if (documentPoolIterator != null && documentPoolIterator.hasNext()) {
            return true;
        } else if (oboClassesIterator.hasNext()){
            List<OntologyDocument> documentPool = new ArrayList<OntologyDocument>();

            OBOClassImpl oboClass = oboClassesIterator.next();

            String id = oboClass.getID();
            String name = oboClass.getName();

            // a root term?
            if (oboClass.getParents().isEmpty()) {
                OntologyDocument doc = new OntologyDocument(ontology, null, null, id, name);
                documentPool.add(doc);
            }

            // get parents
            for (Link link : oboClass.getParents()) {
                LinkedObject oboParent = link.getParent();

                String parentId = oboParent.getID();
                String parentName = oboParent.getName();

                OntologyDocument doc = new OntologyDocument(ontology, parentId, parentName, id, name);
                documentPool.add(doc);
            }

            documentPoolIterator = documentPool.iterator();

           return true;
        }
        return false;
    }
}
