/*
 * Created on 16.07.2004
 */

package uk.ac.ebi.intact.application.mine.business.graph;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Map;

import jdsl.graph.api.Vertex;
import jdsl.graph.ref.IncidenceListGraph;
import uk.ac.ebi.intact.application.mine.business.IntactUserI;
import uk.ac.ebi.intact.application.mine.business.graph.model.EdgeObject;
import uk.ac.ebi.intact.application.mine.business.graph.model.GraphData;

/**
 * The <tt>GraphHelper</tt> stores the <tt>GraphData</tt> for the
 * application. <br>
 * It is responsible for building the graphs and storing them.
 * 
 * @author Andreas Groscurth
 */
public class GraphHelper {
    // Integer 'graphid' -> Graphdata 'data'
    private Map graphMap;
    private IntactUserI intactUser;

    /**
     * Creates a new <tt>GraphHelper</tt>
     * 
     * @param user the intactUser
     */
    public GraphHelper(IntactUserI user) {
        intactUser = user;
        graphMap = new Hashtable();
    }

    /**
     * Returns the <tt>GraphData</tt> for the given graphid. The
     * <tt>GraphData</tt> stores the actual graph to search in and the
     * graphmap which maps the nodes of the graph and the label attached to the
     * node.
     * 
     * @param graphid the id of the graph
     * @return the graphdata
     * @throws SQLException when the building failed
     */
    public GraphData getGraph(Integer graphid) throws SQLException {
        GraphData graphData = (GraphData) graphMap.get( graphid );
        // if no data was found for the graphid the graph is build
        // and stored in the map
        if ( null == graphData ) {
            // to avoid that two users asks for the same graph
            // and both wants to build the graph the building is
            // synchronized.
            synchronized ( this ) {
                graphData = buildGraph( graphid.intValue() );
                graphMap.put( graphid, graphData );
            }
        }
        return graphData;
    }

    /**
     * Builds the graph for the given graphid
     * 
     * @param graphid the graphid
     * @return @throws SQLException whether the building failed
     */
    private synchronized GraphData buildGraph(int graphid) throws SQLException {
        Statement stm = intactUser.getDBConnection().createStatement();
        ResultSet set = null;
        IncidenceListGraph graph = null;
        Vertex v1, v2;
        String protein1_ac, protein2_ac, interaction_ac;
        Map nodeLabelMap = new Hashtable();

        String query = "SELECT * FROM ia_interactions WHERE graphID=" + graphid;
        set = stm.executeQuery( query );
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
            // is succesful which means the protein1_ac may be old and different
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