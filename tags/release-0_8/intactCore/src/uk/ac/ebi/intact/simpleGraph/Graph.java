/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.simpleGraph;

import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.model.BasicObject;

import java.util.*;

/**
 * A simple graph class for temporary processing, for example to prepare output for graph analysis packages.
 */
public class Graph extends BasicGraph implements GraphI {

    /**
     * This is used in toString() in order to be platform compatible.
     */
    private final static String NEW_LINE = System.getProperty("line.separator");

    ///////////////////////////////////////
    // attributes

    /* Nodes are unique. */
    private HashMap nodes = new HashMap();

    /* Edges can be repeated */
    private ArrayList edges = new ArrayList();

    /* Record visited Components */
    private HashSet visited = new HashSet();

    ///////////////////////////////////////
    // access methods for attributes
    public void addNode(NodeI aNode) {
        nodes.put(aNode.getAc(), aNode);
    }

    public Node addNode(Interactor anInteractor){
        Node node = (Node) nodes.get(anInteractor.getAc());
        if (null == node) {
            node = new Node (anInteractor);
            node.setLabel (anInteractor.getShortLabel());
            this.addNode (node);
        }
        return node;
    }


    public void addEdge(EdgeI anEdge) {
        if ( ! edges.contains( anEdge ) )
            edges.add(anEdge);
    }

    public HashMap getNodes() {
        return nodes;
    }

    public Collection getEdges() {
        return edges;
    }

    /** record that a Component has been visited during
     *  graph exploration.
     */
    public void addVisited(BasicObject anElement){
        visited.add(anElement.getAc());
    }

    /** return true if a Component has been visited during graph exploration.
     *
     */
    public boolean isVisited(BasicObject anElement){
        return visited.contains(anElement.getAc());
    }

    public String toString() {
        StringBuffer s = new StringBuffer( 256 );

        final int count = edges.size();
        for ( int i = 0; i < count; i++ ) {
            EdgeI e = (EdgeI) edges.get( i );
            s.append( e.getNode1().getAc() );
            s.append( '(' );
            s.append( e.getComponent1().getCvComponentRole().getShortLabel() );
            s.append( ')' );
            s.append( "-> " );
            s.append( e.getNode2().getAc() );
            s.append( '(' );
            s.append( e.getComponent2().getCvComponentRole().getShortLabel() );
            s.append( ')' );
            s.append( NEW_LINE );
        }
        s.append( NEW_LINE );
        return s.toString();
    }
}
