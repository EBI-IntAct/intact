/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.graph;

import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.exception.ProteinNotFoundException;
import uk.ac.ebi.intact.application.hierarchView.exception.MultipleResultException;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.business.IntactException;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

import org.apache.log4j.Logger;



/**
 * Allows to retreive an interraction network from intact API
 *
 * @author Samuel Kerrien
 * @version $Id$
 */

public class GraphHelper  {

    public static final String AC          = "Accession number";
    public static final String SHORT_LABEL = "short label";
    public static final String XREF        = "cross reference";
    public static final String FULL_NAME   = "full name";
    public static final String NONE        = null;


    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    private IntactUserI user;

    /**
     * describe the found target during the last query, it can be :
     * AC, SHORT_LABEL, XREF, FULL_NAME or NONE.
     */
    private String foundBy;

    /**
     * basic constructor - sets up (hard-coded) data source and an intact helper.
     */
    public GraphHelper (IntactUserI intactUser) {
        user = intactUser;
    } // GraphHelper


    /**
     * Create an interaction graph according to a protein AC
     *
     * @param queryString the given protein AC.
     * @param depth The level of BAIT-BAIT interaction in the interaction graph.
     */
    public InteractionNetwork getInteractionNetwork (String queryString, int depth)
            throws ProteinNotFoundException, SearchException,
                   IntactException, MultipleResultException  {

        InteractionNetwork in = null;

        // Retreiving interactor from the database according to the given AC
        logger.info ("retrieving Interactor ...");
        Collection results = null;

//        results = user.getHelper().search("uk.ac.ebi.intact.model.Interactor", "ac", queryString);

        //first try search string 'as is' - some DBs allow mixed case....
        results = doLookup ("uk.ac.ebi.intact.model.Interactor", queryString);
        if (results.isEmpty()) {
            //try searching first using all uppercase, then all lower case if it returns nothing...
            //NB this would be better done at the DB level, but keep it here for now
            String upperCaseValue = queryString.toUpperCase();
            results = doLookup("uk.ac.ebi.intact.model.Interactor", upperCaseValue);
            if (results.isEmpty()) {

                //now try all lower case....
                String lowerCaseValue = queryString.toLowerCase();
                results = doLookup("uk.ac.ebi.intact.model.Interactor", lowerCaseValue);
                if (results.isEmpty()) {
                    //finished all current options, and still nothing - return a failure
                    logger.info ("No matches were found for the specified search criteria");
                }
            }
        }


        switch (results.size()) {
            case 0 :
                logger.error ("nothing found for: " + queryString);
                throw new ProteinNotFoundException ();

            case 1 :
                Interactor interactor = (Interactor) results.iterator().next();
                in = new InteractionNetwork(interactor);
                in.addCriteria(queryString, foundBy);
                in = this.user.subGraph (in,
                                         depth,
                                         null,
                                         uk.ac.ebi.intact.model.Constants.EXPANSION_BAITPREY);
                break;

            default : // more than 1
                logger.error (queryString + " gave us multiple results");
                throw new MultipleResultException();
        }

        return in;
    }


    /**
     * utility method to handle the logic for lookup, ie trying AC, label etc.
     * Isolating it here allows us to change initial strategy if we want to.
     * NB this will probably be refactored out into the IntactHelper class later on.
     *
     * @param className the intact type to search on
     * @param value the user-specified value
     *
     * @return Collection the results of the search - an empty Collection if no results found
     *
     * @exception IntactException thrown if there were any search problems
     */
    private Collection doLookup(String className, String value)
        throws IntactException {

        Collection results = new ArrayList();

        //try search on AC first...
        results = user.getHelper().search(className, "ac", value);
        if (results.isEmpty()) {
            // No matches found - try a search by label now...
            logger.info ("now searching for class " + className + " with label " + value);
            results = user.getHelper().search(className, "shortLabel", value);
            if (results.isEmpty()) {
                //no match on label - try by xref....
                logger.info ("no match on label - looking for: " + className + " with primary xref ID " + value);
                Collection xrefs = user.getHelper().search(Xref.class.getName(), "primaryId", value);

                //could get more than one xref, eg if the primary id is a wildcard search value -
                //then need to go through each xref found and accumulate the results...
                Iterator it = xrefs.iterator();
                Collection partialResults = new ArrayList();
                while (it.hasNext()) {
                    partialResults = user.getHelper().search(className, "ac", ((Xref) it.next()).getParentAc());
                    results.addAll(partialResults);
                }

                if (results.isEmpty()) {
                    //no match by xref - try finally by name....
                    logger.info ("no matches found using ac, shortlabel or xref - trying fullname...");
                    results = user.getHelper().search(className, "fullName", value);
                    if (results.isEmpty()) foundBy = NONE;
                    else foundBy = FULL_NAME;
                } else foundBy = XREF;
            } else foundBy = SHORT_LABEL;
        } else foundBy = AC;
        return results;
    }
}







