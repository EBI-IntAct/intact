/*
 * Created on 19.04.2004
 */
package uk.ac.ebi.intact.application.mine.business.graph.model;

import jdsl.core.api.Sequence;

/**
 * The class <tt>SearchObject</tt> is used as template for the search in the
 * graph. It stores the shortest path and some additional information to enable
 * a better information for the search.
 * 
 * @author Andreas Groscurth
 */
public class SearchObject {
    private int bitID;
    private int index;
    private Sequence path;

    /**
     * Creates a new SearchObject
     * 
     * @param index the index represents the index of the object in the search
     *            order. this is needed to handle to bit operations correctly.
     */
    public SearchObject(int index) {
        if (index < 0) {
            throw new IllegalArgumentException(
                    "index is not allowed to be negative !");
        }
        this.index = index;
        bitID = 1 << index;
    }

    /**
     * Returns the bitid of the object. <br>
     * Its used as a flag to avoid too many searches in a graph
     * 
     * @return Returns the bitid
     */
    public int getBitID() {
        return bitID;
    }

    /**
     * Returns the index of the object.
     * 
     * @return Returns the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Execute a bit or operation with the given id and the bitid of the object.
     * 
     * @param id the id for the or operation
     */
    public void or(int id) {
        bitID |= id;
    }

    /**
     * Returns the shortest path to the object or to the vertex the search
     * object is representing.
     * 
     * @return Returns the shortest path
     */
    public Sequence getPath() {
        return path;
    }

    /**
     * Returns the length of the current shortest path
     * 
     * @return Integer.MAX_VALUE if no path is given or the length of the path
     */
    public int getPathLength() {
        return path == null ? Integer.MAX_VALUE : path.size();
    }

    /**
     * Sets the shortest path
     * 
     * @param sequence the shortest path
     */
    public void setPath(Sequence sequence) {
        path = sequence;
    }
}