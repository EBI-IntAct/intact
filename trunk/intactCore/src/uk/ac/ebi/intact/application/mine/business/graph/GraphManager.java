/*
 * Created on 22.07.2004
 */

package uk.ac.ebi.intact.application.mine.business.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Stack;

import org.shiftone.cache.Cache;
import org.shiftone.cache.policy.lru.LruCacheFactory;

import uk.ac.ebi.intact.application.mine.business.IntactUserI;
import uk.ac.ebi.intact.application.mine.business.graph.model.GraphData;

/**
 * @author Andreas Groscurth
 */
public class GraphManager extends Thread {
    private static GraphManager INSTANCE;
    // the timeout for the cache after which elements are erazed
    // the time is measured in milliseconds and therefore
    // 5 minutes are written in this way !
    private static final long TIME_OUT = 1000 * 60 * 5;
    // the maximal size of the cache
    private static final int MAX_SIZE = 10;

    // a cache structure to store the graphs efficiently
    private Cache cache;
    // the collection stores all ids which are currently proceeded
    private Collection running;
    // the stack stores all ids which have to be proceeded
    private Stack incoming;

    /**
     * Creates a new GraphManager.
     * 
     * @param user the intact user
     */
    private GraphManager(IntactUserI user) {
        // the cache strucuture is created with
        // a dummy name
        // the time_out which indicates after which time the least valueable
        // object is removed
        // the maximal size of the cache
        cache = new LruCacheFactory().newInstance( "mineCache", TIME_OUT,
                MAX_SIZE );
        running = new HashSet();

        // the structures are set for the GraphBuilder Thread to avoid
        // initialising a new thread with always the same data.
        GraphBuildThread.intactUser = user;
        GraphBuildThread.cache = cache;
        GraphBuildThread.running = running;

        incoming = new Stack();

        // the monitoring of the incoming stack starts
        start();
    }

    /**
     * Called by the client to retrieve a graphdata object for the given id.
     * 
     * @param id the graphid
     * @return the graphData or null if not avaiable
     */
    public GraphData getGraphData(Integer id) {
        GraphData graphData = (GraphData) cache.getObject( id );

        // no data available for the given id
        if ( graphData == null ) {
            // the id is pushed into the incoming stack
            incoming.push( id );
            // because its no data available yet null is returned
            return null;
        }
        // the graphdata for the given id is returned
        return graphData;
    }

    /**
     * returns the instance of this class (singleton implementation). <br>
     * Method is synchronized to avoid that two threads are creating an instance
     * simultaneously... <br>
     * <ol>
     * The other possibilty: private static final GraphManager INSTANCE = new
     * GraphManager(); <br>
     * [....] public static GraphManager getInstance() { return INSTANCE; }<br>
     * </ol>
     * would be thread safe but the class needs the IntactUser as argument !!
     * 
     * @return
     */
    public static synchronized GraphManager getInstance(IntactUserI user) {
        if ( INSTANCE == null ) {
            INSTANCE = new GraphManager( user );
        }
        return INSTANCE;
    }

    /**
     * Method checks every 25 ms if a new id is in the incoming stack. <br>
     * If a new id is in the stack it is popped out and tested whether:
     * <ol>
     * <li>one can find it already in the cache (which means a graph was
     * already built)</li>
     * <li>one can find it in the running structure (which means a graph is
     * built currently)</li>
     * </ol>
     */
    public void run() {
        try {
            while ( true ) {
                // if there are graphids to work with
                if ( !incoming.isEmpty() ) {
                    // get the next graphid for which the graph should be
                    // built
                    final Integer toProcceed = (Integer) incoming.pop();

                    synchronized ( running ) {
                        // if for the given ID nothing can be found in the
                        // cache and in the running structure -> a new thread
                        // starts for building the graph for the given ID
                        if ( cache.getObject( toProcceed ) == null
                                && !running.contains( toProcceed ) ) {
                            running.add( toProcceed );
                            // a new Thread builds the graph
                            new GraphBuildThread( toProcceed ).start();
                        }
                    }
                }
                Thread.sleep( 25 );
            }
        }
        catch ( InterruptedException e ) {
            e.printStackTrace();
        }
    }
}