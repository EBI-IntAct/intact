package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.business.graphdraw.graph;

/**
 * Node with layout information
 */

public interface LayoutNode extends Node {


    /**
     * Get node width
     */
    int getWidth();

    /**
     * Get node height
     */
    int getHeight();

    /**
     * Set the location.
     * The layout algorithm assumes x and y will be the centre of the node.
     */
    void setLocation(int x, int y);
}
