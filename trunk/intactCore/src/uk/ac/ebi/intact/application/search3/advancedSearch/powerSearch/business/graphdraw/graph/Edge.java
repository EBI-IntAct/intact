package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.business.graphdraw.graph;




/**
 * General graph edge
 */
public interface Edge {

    /**
     * Parent
     */
    Node getParent();

    /**
     * Child
     */
    Node getChild();

}
