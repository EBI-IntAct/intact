/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.simpleGraph;

import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.BasicObject;

import java.util.*;

/**
 * A simple graph class for temporary processing, e.g. to prepare output for graph analysis packages.
 */
public class Graph extends BasicGraph implements GraphI {

    ///////////////////////////////////////
    // attributes

    /* Nodes are unique. */
    private HashMap nodes = new HashMap();

    /* Edges can be repeated */
    private Vector edges = new Vector();

    /* Record visited Components */
    private HashSet visited = new HashSet();

    ///////////////////////////////////////
    // access methods for attributes
    public void addNode(NodeI aNode) {
        nodes.put(aNode.getAc(),aNode);
    };

    public Node addNode(Interactor anInteractor){
        Node node = (Node) nodes.get(anInteractor.getAc());
        if (null == node) {
            node = new Node();
            node.setAc(anInteractor.getAc());
            node.setLabel(anInteractor.getShortLabel());
            this.addNode(node);
        }
        return node;
    }


    public void addEdge(EdgeI anEdge) {
        edges.add(anEdge);
    };

    public HashMap getNodes() {
        return nodes;
    }

    public Collection getEdges() {
        return (Collection) edges;
    }

    /** record that a Component has been visited during
     *  graph exploration.
     */
    public void addVisited(BasicObject anElement){
        visited.add(anElement.getAc());
        System.out.println("Marking: " + anElement.getAc());
    };

    /** return true if a Component has been visited during graph exploration.
     *
     */
    public boolean isVisited(BasicObject anElement){
        return visited.contains(anElement.getAc());
    }

    public String toString(){
        StringBuffer s = new StringBuffer();

        for (int i = 0; i < edges.size(); i++) {
            EdgeI e = (EdgeI) edges.elementAt(i);
            s.append(e.getNode1().getAc());
            s.append("-> ");
            s.append(e.getNode2().getAc());
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }
}
