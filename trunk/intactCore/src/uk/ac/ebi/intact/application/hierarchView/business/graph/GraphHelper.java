/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.graph;

import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.exception.ProteinNotFoundException;
import uk.ac.ebi.intact.application.hierarchView.exception.MultipleResultException;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.business.IntactException;

import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;



/**
 * Allows to retreive an interraction network from intact API
 *
 * @author Samuel Kerrien
 * @version $Id$
 */

public class GraphHelper  {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    IntactUserI user;

    /**
     * basic constructor - sets up (hard-coded) data source and an intact helper.
     */
    public GraphHelper (IntactUserI intactUser) {
        user = intactUser;
    } // GraphHelper


    /**
     * Create an interaction graph according to a protein AC
     *
     * @param anAC the given protein AC.
     * @param depth The level of BAIT-BAIT interaction in the interaction graph.
     */
    public InteractionNetwork getInteractionNetwork (String anAC, int depth)
            throws ProteinNotFoundException, SearchException,
                   IntactException, MultipleResultException  {

        InteractionNetwork in = null;

        // Retreiving interactor from the database according to the given AC
        logger.info ("retrieving Interactor ...");
        Collection results = this.user.getHelper().search ("uk.ac.ebi.intact.model.Interactor", "ac", anAC);

        switch (results.size()) {
            case 0 :
                logger.error ("AC not found: " + anAC);
                throw new ProteinNotFoundException ();

            case 1 :
                Interactor interactor = (Interactor) results.iterator().next();

                in = this.user.subGraph (interactor,
                                         depth,
                                         null,
                                         uk.ac.ebi.intact.model.Constants.EXPANSION_BAITPREY);
                break;

            default : // more than 1
                logger.error (anAC + " gave us multiple results");
                throw new MultipleResultException();
        }

        return in;
    }
}







