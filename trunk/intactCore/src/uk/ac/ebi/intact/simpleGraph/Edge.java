/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.simpleGraph;

import uk.ac.ebi.intact.model.Component;

public class Edge extends BasicGraph implements EdgeI {

    ///////////////////////////////////////
    // attributes
    private NodeI node1;
    private NodeI node2;
    private Component Component1;
    private Component Component2;

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


    public Component getComponent1() {
        return Component1;
    }

    public void setComponent1(Component Component1) {
        this.Component1 = Component1;
    }

    public Component getComponent2() {
        return Component2;
    }

    public void setComponent2(Component Component2) {
        this.Component2 = Component2;
    }

    ///////////////////////////////////////////////////
    // Instance methods
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        if (!super.equals(o)) return false;

        // An edge object is symmetric. Check if either
        // this.attribute1 equals o.attribute1 or
        // this.attribute1 equals o.attribute2

        final Edge that = (Edge) o;

        return (((this.getNode1().getAc().equals(that.getNode1().getAc())) &&
                (this.getNode2().getAc().equals(that.getNode2().getAc())) &&
                (this.getComponent1().getAc().equals(that.getComponent1().getAc())) &&
                (this.getComponent2().getAc().equals(that.getComponent2().getAc()))
                )
                ||
                ((this.getNode1().getAc().equals(that.getNode2().getAc())) &&
                (this.getNode2().getAc().equals(that.getNode1().getAc())) &&
                (this.getComponent1().getAc().equals(that.getComponent2().getAc())) &&
                (this.getComponent2().getAc().equals(that.getComponent1().getAc()))
                ));
    }

}
