/*
 * Created on 09.06.2004
 *
 */
package uk.ac.ebi.intact.application.mine.business.graph;

import jdsl.graph.api.Vertex;

/**
 * A class can implement the <tt>Storage</tt> interface if it is used as a
 * storage class in the mine project. The storage class stores different
 * informations for the jdsl algorithm which is used in the mine project. <br>
 * The algorithm needs additional information for each node in the graph (e.g.
 * the ancestor node, the distance from the source node and so on). <br>
 * To enable multiple simultaneous searches in the same graph these
 * informations are stored in an extra class and not directly in the graph.
 * 
 * @author Andreas Groscurth
 */
public interface Storage {
    public static final int DISTANCE = 0;
    public static final int LOCATOR = 1;
    public static final int EDGE_TO_PARENT = 2;

    /**
     * Returns a specific element for a given vertex
     * 
     * @param v the vertex
     * @param index the index of the element
     * @return the object at the given index
     */
    public Object getElement(Vertex v, int index);

    /**
     * Sets the given object for the vertex at the given index
     * 
     * @param v the vertex
     * @param index the index to store the element
     * @param o the element to store
     */
    public void setElement(Vertex v, int index, Object o);

    /**
     * Returns wether the vertex has an element at the given index.
     * 
     * @param v the vertex
     * @param index the index for the look up
     * @return wether an object exists
     */
    public boolean has(Vertex v, int index);

    /**
     * Cleans the storage
     */
    public void cleanup();
}