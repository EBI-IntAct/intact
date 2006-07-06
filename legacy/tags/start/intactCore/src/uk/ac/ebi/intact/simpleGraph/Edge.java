/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.simpleGraph;

public class Edge extends BasicGraph implements EdgeI {

    ///////////////////////////////////////
    // attributes
    private NodeI node1;
    private NodeI node2;

    ///////////////////////////////////////
    // access methods for attributes
    public NodeI getNode1() {
        return node1;
    }

    public void setNode1(NodeI node1) {
        this.node1 = node1;
    }

    public NodeI getNode2() {
        return node2;
    }

    public void setNode2(NodeI node2) {
        this.node2 = node2;
    }
}
