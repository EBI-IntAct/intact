package uk.ac.ebi.intact.application.mine.business.graph.model;

/*
 * Created on 08.04.2004
 */

/**
 * A <tt>EdgeObject</tt> is attached to the edge of the graph to store
 * additional information for the edge.
 * 
 * @author Andreas Groscurth
 */
public class EdgeObject implements EdgeElement {
    private double weight;
    private String interactionAcc;

    /**
     * Creates a new EdgeObject
     * 
     * @param inAcc the accnr of the interaction the edge represents
     * @param weight the weight of the edge
     */
    public EdgeObject(String inAcc, double weight) {
        interactionAcc = inAcc;
        this.weight = weight;
    }

    /**
     * Creates a new EdgeObject with an edge weight of 1
     * 
     * @param inAcc the accnr of the interaction the edge represents
     */
    public EdgeObject(String inAcc) {
        this(inAcc, 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see EdgeElement#getWeight()
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Returns the accnr of the interaction the edge represents
     * 
     * @return Returns the accrnr
     */
    public String getInteractionAcc() {
        return interactionAcc;
    }

    /**
     * Returns the interaction accnr
     */
    public String toString() {
        return interactionAcc;
    }
}