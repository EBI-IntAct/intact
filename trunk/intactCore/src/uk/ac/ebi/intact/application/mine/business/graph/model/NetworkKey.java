/*
 * Created on 18.06.2004
 *
 */
package uk.ac.ebi.intact.application.mine.business.graph.model;

import java.util.Collection;
import java.util.Iterator;

/**
 * The class <tt>NetworkKey</tt> is a wrapper class to store the shortest paths
 * of a biosource and graphid.
 * 
 * @author Andreas Groscurth
 */
public class NetworkKey implements Comparable {
    private String bioSource;
    private int graphID;
    private Collection shortestPaths;

    /**
     * Creates a new Object for the given biosource and graphid.
     * 
     * @param b the biosource
     * @param g the graphid
     */
    public NetworkKey(String b, int g) {
        bioSource = b;
        graphID = g;
    }

    /**
     * Returns the biosource
     * 
     * @return Returns the bioSource.
     */
    public String getBioSource() {
        return bioSource;
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
        return bioSource.equals(dw.bioSource) && graphID == dw.graphID;
    }

    public int hashCode() {
        return bioSource.hashCode() + graphID;
    }

    /**
     * @return Returns the shortestPath.
     */
    public Collection getShortestPath() {
        return shortestPaths;
    }

    /**
     * @param shortestPath The shortestPath to set.
     */
    public void setShortestPath(Collection shortestPath) {
        this.shortestPaths = shortestPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        NetworkKey other = (NetworkKey) o;
        int comp = other.bioSource.compareTo(bioSource);
        if (comp == 0) {
            return other.graphID - graphID;
        }
        return comp;
    }

    /**
     * Returns the length of the shortestPath
     * 
     * @return the length of the path
     */
    public int getPathSize() {
        return shortestPaths == null ? 0 : shortestPaths.size();
    }

    /**
     * Returns the shortest path as a string reprasentation.
     * 
     * @return The string with all elements of the path seperated by comma
     */
    public String printShortestPath() {
        if (shortestPaths == null) {
            return "";
        }
        Iterator pathIter = shortestPaths.iterator();
        Object o;
        StringBuffer hv = new StringBuffer();
        while (pathIter.hasNext()) {
            o = pathIter.next();
            hv.append(o);
            if (pathIter.hasNext()) {
                hv.append(",");
            }
        }
        return hv.toString();
    }
}