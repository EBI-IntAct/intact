/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.graph;

// hierarchView
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.simpleGraph.Graph;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;

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

    private IntactHelper helper;

    /**
     * basic constructor - sets up (hard-coded) data source and an intact helper.
     */
    public GraphHelper (IntactHelper intactHelper)  throws Exception {
        helper = intactHelper;
    } // GraphHelper



    /**
     *
     *
     */
    public Collection doSearch(String className, String searchParam, String searchValue)
            throws Exception {
        Collection resultList = null;
        if (helper != null) {
            //NB assumes full java className supplied...
            resultList = helper.search (className, searchParam, searchValue);
            return resultList;
        } else {
            logger.error ("Couldn't create a helper class to access the data!!");
            throw new Exception();
        }
    } // doSearch




    /**
     * Create an interaction graph according to a protein AC
     *
     * @param anAC the given protein AC.
     * @param depth The level of BAIT-BAIT interaction in the interaction graph.
     */
    public InteractionNetwork getInteractionNetwork (String anAC, int depth) throws Exception {

        InteractionNetwork in = new InteractionNetwork ();

        // Retreiving interactor from the database according to the given AC
        logger.info ("retrieving Interactor ...");
        Collection results = this.doSearch ("uk.ac.ebi.intact.model.Interactor", "ac", anAC);
        Iterator iter1     = results.iterator ();

        //there is at most one - ac is unique
        if (iter1.hasNext()) {
            logger.info ("Starting graph generation ... ");
            Interactor interactor = (Interactor) iter1.next();
            Graph graph = in;
            graph = this.helper.subGraph (interactor,
                    depth,
                    null,
                    uk.ac.ebi.intact.model.Constants.EXPANSION_BAITPREY,
                    graph);

            logger.info ("Generated graph : " + in);
        } else {
            in = null;
            logger.error ("AC not found: " + anAC);
        }

        return in;

    } // getInteractionNetwork

} // GraphHelper







