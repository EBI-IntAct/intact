/*
 * Created on 21.05.2004
 *
 */
package uk.ac.ebi.intact.application.mine.business.graph.model;

import java.util.Map;

import jdsl.graph.api.Graph;

/**
 * <tt>MineData</tt> is a wrapper class to store the graph and the map which
 * maps the accession numbers to the nodes in the graph.
 * 
 * @author Andreas Groscurth
 */
public class MineData {
    private Graph graph;
    private Map accMap;

    /**
     * Creates a new MineData object
     * 
     * @param g the graph
     * @param m the accession nr map
     */
    public MineData(Graph g, Map m) {
        if (g == null || m == null) {
            throw new IllegalArgumentException("neither the graph"
                    + "nor the map are allowed to be null !");
        }
        graph = g;
        accMap = m;
    }

    /**
     * Returns the accession nr map
     * 
     * @return the accnr map
     */
    public Map getAccMap() {
        return accMap;
    }

    /**
     * Returns the graph
     * 
     * @return the graph
     */
    public Graph getGraph() {
        return graph;
    }
}