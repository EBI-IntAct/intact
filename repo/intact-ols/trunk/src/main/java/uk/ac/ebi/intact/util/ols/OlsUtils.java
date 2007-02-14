/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.ols;

import uk.ac.ebi.ook.web.services.Query;

import java.rmi.RemoteException;
import java.util.Map;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class OlsUtils {

    private OlsUtils() {
    }

    /**
     * Gets a term from the MI ontology using the provided MI term id
     *
     * @param miTermId the term id to look for
     *
     * @return the term, including the children
     */
    public static Term getMiTerm(String miTermId) throws RemoteException {
        OlsClient olsClient = new OlsClient();
        Query ontologyQuery = olsClient.getOntologyQuery();

        String termName = ontologyQuery.getTermById(miTermId, "MI");

        Term term = new Term(miTermId, termName);

        populateChildren(term, ontologyQuery);

        return term;
    }

    private static void populateChildren(Term term, Query ontologyQuery) throws RemoteException {
        Map<String, String> childrenMap = ontologyQuery.getTermChildren(term.getId(), "MI", 1, new int[]{1, 2, 3, 4, 5, 6});

        for (Map.Entry<String, String> entry : childrenMap.entrySet()) {
            Term child = new Term(entry.getKey(), entry.getValue());
            populateChildren(child, ontologyQuery);
            term.addChild(child);
        }
    }

}