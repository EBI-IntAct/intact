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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        if (!super.equals(o)) return false;

        final Edge edge = (Edge) o;
        final String edgeAC1 = edge.getNode1().getAc(),
                     edgeAC2 = edge.getNode2().getAc();
        final String AC1 = node1.getAc(),
                     AC2 = node2.getAc();

        return ((edgeAC1.equals(AC1) && edgeAC2.equals(AC2)) ||
                (edgeAC1.equals(AC2) && edgeAC2.equals(AC1)));
    }

}
