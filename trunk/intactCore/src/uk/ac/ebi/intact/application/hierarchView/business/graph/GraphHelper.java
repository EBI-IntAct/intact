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

import java.util.Collection;
import java.util.Iterator;



/**
 * Allows to retreive an interraction network from intact API
 *
 * @author Samuel Kerrien
 * @version $Id$
 */

public class GraphHelper  {

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
    public Collection doSearch(String className, String searchParam, String searchValue) throws Exception {
        Collection resultList = null;
        if (helper != null) {
            //NB assumes full java className supplied...
            resultList = helper.search(className, searchParam, searchValue);
            return resultList;
        } else {
            System.out.println("something failed - couldn't create a helper class to access the data!!");
            throw new Exception();
        }
    } // doSearch




    /**
     * Create a graph ...
     *
     *
     */
    public InteractionNetwork getInteractionNetwork (String anAC, int depth) throws Exception {

        InteractionNetwork in = new InteractionNetwork ();

        //get a complete Institution, then add it to an Experiment..
        System.out.println ("retrieving Interactor ...");
        Collection results = this.doSearch ("uk.ac.ebi.intact.model.Interactor", "ac", anAC);
        Iterator iter1     = results.iterator ();

        //there is at most one - ac is unique
        if (iter1.hasNext()) {
            System.out.println ("Starting graph generation ... ");
            Interactor interactor = (Interactor) iter1.next();
            Graph graph = in;
            graph = this.helper.subGraph (interactor,
                    depth,
                    null,
                    uk.ac.ebi.intact.model.Constants.EXPANSION_BAITPREY,
                    graph);

            System.out.println (in);
        } else {
            in = null;
            System.out.println ("AC not found: " + anAC + "\n");
        }

        return in;

    } // getInteractionNetwork

} // GraphHelper







