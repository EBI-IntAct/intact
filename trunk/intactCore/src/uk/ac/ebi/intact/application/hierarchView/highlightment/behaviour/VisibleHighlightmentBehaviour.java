/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.highlightment.behaviour;

import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork;
import uk.ac.ebi.intact.simpleGraph.Node;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract class allowing to deals with the Highlightment behaviour,
 * the implementation of that class would just specify the behaviour
 * of one node of the graph.
 *
 * @author Emilie FROT
 */

public class VisibleHighlightmentBehaviour
        extends HighlightmentBehaviour {
    /**
     * Select all the graph's protein which are not in the given collection
     * When we call apply, the proteins visibility of the new collection will be set to false.
     *
     * @param proteins
     * @param aGraph
     *
     * @return the new collection to highlight
     */
    public Collection modifyCollection (Collection proteins, InteractionNetwork aGraph) throws UnsupportedOperationException {

        /* Get the list of proteins in the current InteractionNetwork */
        ArrayList listAllProteins = new ArrayList (aGraph.getNodes().values());

        /* Make a clone of the list */
        Collection newList = (Collection) listAllProteins.clone();

        /* Remove all proteins of the collection "proteins" */
        newList.removeAll(proteins);

        return newList;
    } // modifyCollection


    /**
     * Apply the implemented behaviour to the specific Node of the graph.
     * Here, we change the visibility to false for the given node.
     *
     * @param aProtein the node on which we want to apply the behaviour
     */
    public void applyBehaviour (Node aProtein) {
        aProtein.put(Constants.ATTRIBUTE_VISIBLE, new Boolean (false));

    } // applyBehaviour

} // VisibleHighlightmentBehaviour









