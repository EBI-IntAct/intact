/*
 * Created on 18.06.2004
 *
 */
package uk.ac.ebi.intact.application.mine.business.graph.model;

/**
 * The class <tt>NetworkKey</tt> is a wrapper class which stores the taxid and
 * the graphid of a graph. It is used as a key in the map to retrieve a graph
 * for the specific taxid / graphid.
 * 
 * @author Andreas Groscurth
 */
public class GraphKey {
    private String taxid;
    private int graphID;

    /**
     * Creates a new <tt>NetworkKey</tt> for the given taxid and graphid.
     * 
     * @param taxid the biosource
     * @param graphID the graphid
     */
    public GraphKey(String taxid, int graphID) {
        this.taxid = taxid;
        this.graphID = graphID;
    }

    /**
     * Returns the taxid
     * 
     * @return Returns the taxid.
     */
    public String getTaxID() {
        return taxid;
    }

    /**
     * Returns the graphID.
     * 
     * @return Returns the graphID.
     */
    public int getGraphID() {
        return graphID;
    }

    /**
     * Returns wether the two objects are equal. <br>
     * This is the case if the taxids are equal and the graphids are equal
     * 
     * @param o the object to compare with
     * @return whether the objects are equal
     */
    public boolean equals(Object o) {
        if (!(o instanceof GraphKey)) {
            return false;
        }
        GraphKey dw = (GraphKey) o;
        // return true if the taxids are equal and the graphids are equal
        return taxid.equals(dw.taxid) && graphID == dw.graphID;
    }

    /**
     * Returns the hash code of the object. <br>
     * The hash code is the hashcode from the taxid plus the graphID (which is
     * an integer)
     * 
     * @return the hashCode of the object
     */
    public int hashCode() {
        return taxid.hashCode() + graphID;
    }

    /**
     * toString method
     */
    public String toString() {
        return "biosource taxid: " + taxid + " / graphid " + graphID;
    }
}