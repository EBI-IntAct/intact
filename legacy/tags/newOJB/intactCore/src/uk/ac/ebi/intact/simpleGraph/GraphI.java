/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.simpleGraph;

import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.BasicObject;

import java.util.Collection;
import java.util.HashMap;

/**
 * A simple graph class for temporary processing, for example to prepare output for graph analysis packages.
 */
public interface GraphI extends BasicGraphI {

    public void addNode(NodeI aNode);

    public Node addNode(Interactor anInteractor);

    public void addEdge(EdgeI anEdge);

    public HashMap getNodes();

    public Collection getEdges();

    /** record that a Component has been visited during
     *  graph exploration.
     */
    public void addVisited(BasicObject graphElement);

    /** return true if a Component has been visited during graph exploration.
     *
     */
    public boolean isVisited(BasicObject graphElement);
}
