/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.highlightment.behaviour;

import uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork;
import uk.ac.ebi.intact.simpleGraph.Node;

import java.util.Collection;
import java.util.Iterator;


/**
 * Abstract class allowing to deals with the Highlightment behaviour,
 * the implementation of that class would just specify the behaviour
 * of one node of the graph.
 *
 * @author Samuel KERRIEN
 */

public abstract class HighlightmentBehaviour {


    /**
     * Provides a implementation of HighlightmentBehaviour by its name.
     * for example you have an implementation of this abstract class called : <b>ColorHighlightmentBehaviour</b>.
     *      so, you could call the following method to get an instance of this class :
     *      <br>
     *      <b>HighlightmentBehaviour.getHighlightmentBehaviour ("mypackage.ColorHighlightmentBehaviour");</b>
     *      <br>
     *      then you're able to use methods provided by this abstract class without to know
     *      what implementation you are using.
     *
     * @param aClassName the name of the implementation class you want to get
     * @return an HighlightmentBehaviour object, or null if an error occurs.
     */
    public static HighlightmentBehaviour getHighlightmentBehaviour (String aClassName) {

        Object object = null;

        try {

            // create a class by its name
            Class cls = Class.forName(aClassName);

            // Create an instance of the class invoked
            object = cls.newInstance();

        } catch (Exception e) {
            // nothing to do, object is already setted to null
        }

        return (HighlightmentBehaviour) object;

    } // getHighlightmentBehaviour

    /**
     * Apply the implemented behaviour to the specific Node of the graph
     *
     * @param aProtein the node on which we want to apply the behaviour
     */
    abstract public void applyBehaviour (Node aProtein);

    /**
     * Allow to apply a modification on the collection.
     * for example Select all the graph protein which are not in the given collection
     *
     * The default behaviour of that method is to return the given Collection,
     * to change that you have to overwrite that method int your implementation.
     *
     * @param proteins
     * @param aGraph
     *
     * @return the new collection to highlight
     */
    public Collection modifyCollection (Collection proteins, InteractionNetwork aGraph) {
        return proteins;
    } // modifyCollection

    /**
     * Apply the implemented behaviour to a set of nodes.
     *
     * @param proteins
     * @param aGraph
     */
    public void apply (Collection proteins, InteractionNetwork aGraph) {

        proteins = modifyCollection (proteins, aGraph);

        if (null != proteins) {
            Iterator iterator = proteins.iterator();
            while (iterator.hasNext()) {
                Node protein = (Node) iterator.next();
                applyBehaviour (protein);
            }
        }

    } // apply

} // HighlightmentBehaviour






