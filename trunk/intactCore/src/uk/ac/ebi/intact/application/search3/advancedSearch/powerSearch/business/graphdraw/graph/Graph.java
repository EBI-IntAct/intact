package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.business.graphdraw.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Graph, contains the nodes, edges and utility methods
 */
public class Graph {

    public Set nodes = new HashSet();
    public Set edges = new HashSet();

    public Set parents(Node a) {
        Set results = new HashSet();
        for (Iterator i = edges.iterator(); i.hasNext();) {
            Edge e = (Edge) i.next();
            if (e.getChild() == a) results.add(e.getParent());
        }
        return results;
    }

    public Set children(Node a) {
        Set results = new HashSet();
        for (Iterator i = edges.iterator(); i.hasNext();) {
            Edge e = (Edge) i.next();
            if (e.getParent() == a) results.add(e.getChild());
        }
        return results;
    }

    public Edge findEdge(Node p, Node c) {
        for (Iterator i = edges.iterator(); i.hasNext();) {
            final Object o = i.next();

            Edge e = (Edge) o;
            if ((e.getParent() == p) && (e.getChild() == c)) return e;
        }
        return null;
    }

    public boolean connected(Node a, Node b) {
        return (findEdge(a, b) != null) || (findEdge(b, a) != null);
    }

}
