package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.business.graphdraw.graph;

import java.awt.*;

/**
 * Edge with layout information
 */
public interface LayoutEdge extends Edge {

    /**
     * Set the location
     */
    void setRoute(Shape route);

}
