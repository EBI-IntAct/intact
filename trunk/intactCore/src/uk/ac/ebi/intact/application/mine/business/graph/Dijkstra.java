/*
 * Created on 08.04.2004
 */

package uk.ac.ebi.intact.application.mine.business.graph;

import java.util.Map;

import jdsl.core.api.Locator;
import jdsl.core.api.Sequence;
import jdsl.core.ref.NodeSequence;
import jdsl.graph.algo.IntegerDijkstraPathfinder;
import jdsl.graph.algo.InvalidQueryException;
import jdsl.graph.api.Edge;
import jdsl.graph.api.Vertex;
import uk.ac.ebi.intact.application.mine.business.graph.model.EdgeElement;
import uk.ac.ebi.intact.application.mine.business.graph.model.SearchObject;

/**
 * The class <tt>Dijsktra</tt> implements the Dijkstra algorithm to find the
 * shortest path between two nodes. The class extends the
 * <tt>IntegerDijkstraPathfinder</tt> from the <tt>jdsl</tt> library and
 * uses its implementation. The class overrides several methods to provide its
 * own use.
 * 
 * @author Andreas Groscurth
 */
public class Dijkstra extends IntegerDijkstraPathfinder {
	// the maximal depth to search for the path
	private static final int MAX_LEVEL = 5;
	private Storage storage;
	private Map searches;
	private int currentLevel;
	private boolean unreachableFlag;
	
	/**
	 * Creates a new Dijkstra object.
	 * 
	 * @param storage the storage element to store the information for the algorithm
	 * @param searches search map maps the vertex of the graph to the search objects
	 */
	public Dijkstra(Storage storage, Map searches) {
		this.storage = storage;
		this.searches = searches;
		unreachableFlag = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdsl.graph.algo.IntegerDijkstraTemplate#weight(jdsl.graph.api.Edge)
	 */
	protected int weight(Edge edge) {
		return (int) ((EdgeElement) edge.element()).getWeight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdsl.graph.algo.IntegerDijkstraTemplate#vertexNotReachable(jdsl.graph.api.Vertex)
	 */
	protected void vertexNotReachable(Vertex v) {
		storage.setElement(v, Storage.DISTANCE, null);
		setEdgeToParent(v, null);
		unreachableFlag = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdsl.graph.algo.IntegerDijkstraTemplate#isFinished(jdsl.graph.api.Vertex)
	 */
	protected boolean isFinished(Vertex v) {
		boolean isFinished = storage.has(v, Storage.DISTANCE);
		if (isFinished) {
			getPath(v);
		}
		return isFinished;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdsl.graph.algo.IntegerDijkstraTemplate#setLocator(jdsl.graph.api.Vertex,
	 *      jdsl.core.api.Locator)
	 */
	protected void setLocator(Vertex v, Locator vLoc) {
		storage.setElement(v, Storage.LOCATOR, vLoc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdsl.graph.algo.IntegerDijkstraTemplate#getLocator(jdsl.graph.api.Vertex)
	 */
	protected Locator getLocator(Vertex v) {
		return (Locator) storage.getElement(v, Storage.LOCATOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdsl.graph.algo.IntegerDijkstraTemplate#setEdgeToParent(jdsl.graph.api.Vertex,jdsl.graph.api.Edge)
	 */
	protected void setEdgeToParent(Vertex v, Edge vEdge) {
		storage.setElement(v, Storage.DISTANCE, new Integer(currentLevel + 1));
		storage.setElement(v, Storage.EDGE_TO_PARENT, vEdge);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdsl.graph.algo.IntegerDijkstraTemplate#getEdgeToParent(jdsl.graph.api.Vertex)
	 */
	public Edge getEdgeToParent(Vertex v) {
		return (Edge) storage.getElement(v, Storage.EDGE_TO_PARENT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdsl.graph.algo.IntegerDijkstraPathfinder#shouldContinue()
	 */
	protected boolean shouldContinue() {
		Vertex v = (Vertex) pq_.min().element();
		Integer level = (Integer) storage.getElement(v, Storage.DISTANCE);
		currentLevel = level == null ? 0 : level.intValue();
		return super.shouldContinue() && currentLevel < MAX_LEVEL
				&& !unreachableFlag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdsl.graph.algo.IntegerDijkstraTemplate#shortestPathFound(jdsl.graph.api.Vertex,int)
	 */
	protected void shortestPathFound(Vertex v, int vDist) {
		storage.setElement(v, Storage.DISTANCE, new Integer(vDist));
		if (v != source_) {
			SearchObject so = (SearchObject) searches.get(v);
			if (so != null) {
				int bit1 = ((SearchObject) searches.get(source_)).getBitID();
				int bit2 = so.getBitID();
				boolean goOn = (bit1 | bit2) != (bit1 > bit2 ? bit1 : bit2);
				if (goOn) {
					getPath(v);
				}
			}
		}
	}

	/**
	 * Returns the path of the given vertex to the start vertex
	 * 
	 * @param end the destination
	 * @return the path
	 * @throws InvalidQueryException
	 */
	public Sequence getPath(Vertex end) {
		Sequence retval = new NodeSequence();
		Vertex currVertex = end;
		SearchObject searchEnd = (SearchObject) searches.get(end);
		SearchObject currentSearch;
		while (currVertex != source_) {
			Edge currEdge = getEdgeToParent(currVertex);
			retval.insertFirst(currEdge);
			currVertex = g_.opposite(currVertex, currEdge);
			currentSearch = (SearchObject) searches.get(currVertex);
			if (currentSearch != null) {
				int bit1 = currentSearch.getBitID();
				int bit2 = searchEnd.getBitID();
				boolean goOn = (bit1 | bit2) != (bit1 > bit2 ? bit1 : bit2);
				if (goOn) {
					currentSearch.or(1 << searchEnd.getIndex());
					searchEnd.or(1 << currentSearch.getIndex());
					checkShortestPath(retval, currentSearch, searchEnd);
				}
			}
		}
		return retval;
	}
	
	/**
	 * Checks wether a new shortest path was found.
	 *
	 * @param path the new found path
	 * @param search1 node one
	 * @param search2 node two
	 */
	private void checkShortestPath(Sequence path, SearchObject search1,
			SearchObject search2) {
		if (search1.getPathLength() > path.size()) {
			search1.setPath(path);
		}
		if (search2.getPathLength() > path.size()) {
			search2.setPath(path);
		}
	}
}