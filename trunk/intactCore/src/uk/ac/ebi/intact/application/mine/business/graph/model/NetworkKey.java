/*
 * Created on 18.06.2004
 *
 */
package uk.ac.ebi.intact.application.mine.business.graph.model;

/**
 * The class <tt>NetworkKey</tt> is a wrapper class to store a biosource and
 * graphid.
 * 
 * @author Andreas Groscurth
 */
public class NetworkKey {
    private String bioSourceTaxID;
    private int graphID;

    /**
     * Creates a new Object for the given biosource and graphid.
     * 
     * @param b the biosource
     * @param g the graphid
     */
    public NetworkKey(String b, int g) {
        bioSourceTaxID = b;
        graphID = g;
    }

    /**
     * Returns the biosource
     * 
     * @return Returns the bioSource.
     */
    public String getBioSourceTaxID() {
        return bioSourceTaxID;
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
     * Returns wether the two objects are equal
     */
    public boolean equals(Object o) {
        if (!(o instanceof NetworkKey)) {
            return false;
        }
        NetworkKey dw = (NetworkKey) o;

        return bioSourceTaxID.equals(dw.bioSourceTaxID) && graphID == dw.graphID;
    }

    public int hashCode() {
        return bioSourceTaxID.hashCode() + graphID;
    }

    public String toString() {
        return "biosource taxid: " + bioSourceTaxID + " / graphid " + graphID;
    }
}