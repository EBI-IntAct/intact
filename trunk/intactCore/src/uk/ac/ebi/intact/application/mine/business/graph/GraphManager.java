/*
 * Created on 22.07.2004
 */

package uk.ac.ebi.intact.application.mine.business.graph;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

import jdsl.graph.api.Vertex;
import jdsl.graph.ref.IncidenceListGraph;
import uk.ac.ebi.intact.application.mine.business.IntactUserI;
import uk.ac.ebi.intact.application.mine.business.graph.model.EdgeObject;
import uk.ac.ebi.intact.application.mine.business.graph.model.GraphData;

/**
 * @author Andreas Groscurth
 */
public class GraphManager {
    private static GraphManager INSTANCE;

    private IntactUserI intactUser;
    private Hashtable cache;
    private GraphBuilder builder;

    private GraphManager(IntactUserI user) {
        intactUser = user;
        // cache stores the actual mapping of a graphid to the graphData
        // Integer -> GraphData
        //TODO: TEST STRUCTURE !! HAS TO BE CHANGED TO A LRU OR SOMETHING
        // SIMILAR
        cache = new Hashtable();
        builder = new GraphBuilder();
        builder.start();
    }

    /**
     * Called by the client to retrieve a graphdata object for the given id.
     * 
     * @param id
     * @return
     */
    public GraphData getGraphData(Integer id) {
        GraphData graphData = (GraphData) cache.get( id );

        // no data available for the given id
        if ( graphData == null ) {
            // the id is pushed into the incoming stack of the builder thread
            // it checks every 25 ms if the stack contains data and proceess it
            // if so
            builder.queue( id );
            // because its no data available yet null is returned
            return null;
        }
        // the graphdata for the given id is returned
        return graphData;
    }

    /**
     * returns the instance of this class (singleton implementation). Method is
     * synchronized to avoid that two threads are creating an instance
     * simultaneously... The other possibilty: private static final GraphManager
     * INSTANCE = new GraphManager(); [....] public static GraphManager
     * getInstance() { return INSTANCE; } would be thread safe but the class
     * needs the IntactUser as argument !!
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
     * The GraphBuilder is the Demon to handle the building of the graph
     */
    private class GraphBuilder extends Thread {
        private Stack incoming;
        private HashSet running;

        public GraphBuilder() {
            // the stack stores all graphids which have to be procceeded
            incoming = new Stack();
            // the set stores all graphids which are proccessed currently
            // TODO: TEST STRUCTURE !! MAYBE CHANGE THE STRUCTURE !!
            running = new HashSet();
        }

        public void queue(Integer ac) {
            incoming.push( ac );
        }

        public void run() {
            try {
                while ( true ) {
                    // if there are graphids to work with
                    if ( !incoming.isEmpty() ) {
                        // get the next graphid for which the graph should be
                        // built
                        final Integer toProcceed = (Integer) incoming.pop();

                        synchronized ( incoming ) {
                            // if the running structure does not have the
                            // graphid to
                            // procceed -> no one is building a graph for it
                            if ( !running.contains( toProcceed ) ) {
                                running.add( toProcceed );
                                //TODO: WORK(ER) THREAD SHOULD DO THIS TEST
                                // STRUCTURE !!
                                // a new Thread builds the graph
                                new Thread() {
                                    public void run() {
                                        GraphData gd;
                                        try {
                                            gd = buildGraph( toProcceed );

                                            // is 'synchronized( incoming )'
                                            // needed because the lock above
                                            // ????

                                            // the number is removed from the
                                            // running elements
                                            running.remove( toProcceed );

                                            // the data is stored in the cache
                                            cache.put( toProcceed, gd );
                                        }
                                        catch ( SQLException e ) {
                                            // TODO WHAT TO DO IF BUILDING
                                            // FAILED
                                            // ???
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
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

        private GraphData buildGraph(Integer graphid) throws SQLException {
            Statement stm = intactUser.getDBConnection().createStatement();
            ResultSet set = null;
            IncidenceListGraph graph = null;
            Vertex v1, v2;
            String protein1_ac, protein2_ac, interaction_ac;
            Map nodeLabelMap = new Hashtable();

            set = stm
                    .executeQuery( "SELECT * FROM ia_interactions WHERE graphid="
                            + graphid );
            // the graph is initialised
            graph = new IncidenceListGraph();
            while ( set.next() ) {
                // the two interactors are fetched
                protein1_ac = set.getString( 1 ).trim().toUpperCase();
                protein2_ac = set.getString( 2 ).trim().toUpperCase();
                // the interaction_ac of the interactions
                interaction_ac = set.getString( 5 );

                // if the map does not contain the interactor_ac
                // this means the interactor is not yet in the graph
                if ( !nodeLabelMap.containsKey( protein1_ac ) ) {
                    v1 = graph.insertVertex( protein1_ac );
                    nodeLabelMap.put( protein1_ac, v1 );
                }
                // if the map does not contain the interactor_ac
                // this means the interactor is not yet in the graph
                if ( !nodeLabelMap.containsKey( protein2_ac ) ) {
                    v2 = graph.insertVertex( protein2_ac );
                    nodeLabelMap.put( protein2_ac, v2 );
                }
                // because it can happens that just one of the if tests
                // is succesful which means the protein1_ac may be old and
                // different
                // to the node v1 - the correct node for the given
                // interactor_ac has to be fetched from the map
                v1 = (Vertex) nodeLabelMap.get( protein1_ac );
                v2 = (Vertex) nodeLabelMap.get( protein2_ac );
                // the edge between these two nodes is inserted
                graph.insertEdge( v1, v2, new EdgeObject( interaction_ac, set
                        .getDouble( 6 ) ) );
            }
            set.close();
            stm.close();
            return new GraphData( graph, nodeLabelMap );
        }
    }
}