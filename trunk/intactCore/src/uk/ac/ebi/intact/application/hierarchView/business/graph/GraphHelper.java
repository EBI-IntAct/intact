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
import uk.ac.ebi.intact.simpleGraph.Node;

import java.util.*;

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
     * Create an interaction graph according to a query string.
     *
     * @param queryString the protein criteria from which we expect to find Interactor in the database.
     * @param depth The level of BAIT-BAIT interaction in the interaction graph.
     * @return the corresponding in teraciton network
     */
    public InteractionNetwork addInteractionNetwork (InteractionNetwork in, String queryString, int depth)
            throws ProteinNotFoundException,
                   SearchException,
                   IntactException,
                   MultipleResultException  {

        // Retreiving interactor from the database according to the given AC
        logger.info ("retrieving Interactor ...");
        Collection results = null;

        results = find (queryString);

        switch (results.size()) {
            case 0 :
                logger.error ("nothing found for: " + queryString);
                throw new ProteinNotFoundException ();

            case 1 :
                Interactor interactor = (Interactor) results.iterator().next();
                InteractionNetwork tmp = null;
                if (in != null) {
                    // in the case that interactor is already registered as central in
                    // the current network, no need to retrieve the network, just send
                    // back the current one.
                    String ac = interactor.getAc();
                    ArrayList centrals = in.getCentralProteins();
                    int max = centrals.size();
                    for (int i=0; i<max; i++) {
                        Node node = (Node) centrals.get(i);
                        if (ac.equals(node.getAc())) {
                           return in;
                        }
                    }

                    // this is not the case, so retreive that network and fusion them.
                    tmp = in;
                }

                in = new InteractionNetwork (interactor);

                // foundBy has been updated by doLookup
                in.addCriteria (queryString, foundBy);

                in = this.user.subGraph (in,
                                         depth,
                                         null,
                                         uk.ac.ebi.intact.model.Constants.EXPANSION_BAITPREY);

                if (tmp != null) {
                    logger.info ("Fusion interaction network");
                    tmp.fusion(in);
                    in = tmp;
                }

                break;

            default : // more than 1
                logger.error (queryString + " gave us multiple results");
                throw new MultipleResultException();
        }

        return in;
    }


    /**
     * Search in the database Interactor related to the query string.
     *
     * @param queryString the criteria to search for.
     * @return a collection of interactor or empty if none are found.
     * @throws IntactException in case of search error.
     */
    private Collection find (String queryString) throws IntactException {

        Collection results;

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

        return results;
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


    /**
     * update the interaction network given in parameter, by reloading the whole set of
     * entry point with the given depth.
     *
     * @param network the interaction network to update
     * @param depth the depth to update to.
     * @return the new interaction network.
     * @throws ProteinNotFoundException
     * @throws SearchException
     * @throws IntactException
     * @throws MultipleResultException
     */
    public InteractionNetwork updateInteractionNetwork (InteractionNetwork network, int depth)
            throws ProteinNotFoundException,
                   SearchException,
                   IntactException,
                   MultipleResultException {

        // Retreiving interactor from the database according to the given AC
        logger.info ("updating interaction network ...");
        Collection results = null;

        InteractionNetwork newNetwork = null;

        ArrayList centrals = network.getCentralProteins();
        String ac;
        int max = centrals.size();
        for (int i=0; i<max; i++) {
            Node node = (Node) centrals.get(i);
            ac = node.getAc();

            logger.info ("Retrieving Interactor "+ ac +" ...");
            results = find (ac);

            switch (results.size()) {
                case 0 : // should not happen
                    logger.error ("nothing found for ac: " + ac);
                    throw new ProteinNotFoundException ();

                case 1 :
                    Interactor interactor = (Interactor) results.iterator().next();
                    InteractionNetwork tmp = new InteractionNetwork (interactor);

                    tmp = this.user.subGraph (tmp,
                                              depth,
                                              null,
                                              uk.ac.ebi.intact.model.Constants.EXPANSION_BAITPREY);
                    if (newNetwork == null) {
                        // first network
                        newNetwork = tmp;
                    } else {
                        logger.info ("Fusion interaction network");
                        newNetwork.fusion (tmp);
                    }

                    // add the central node to the new network
                    HashMap nodes = newNetwork.getNodes();
                    Node aNode = (Node) nodes.get(node.getAc());
                    newNetwork.addCentralProtein(aNode);

                    break;

                default : // more than 1 - should not happen
                    logger.error (ac + " gave us multiple results");
                    throw new MultipleResultException();
            } // switch
        } // for each central proteins

        // to finish, copy criterias
        newNetwork.getCriteria().addAll (network.getCriteria());

        return newNetwork;
    }
}







