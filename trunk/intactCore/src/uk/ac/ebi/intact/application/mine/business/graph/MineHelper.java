/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.application.mine.business.graph;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import jdsl.core.api.ObjectIterator;
import jdsl.core.api.Sequence;
import jdsl.graph.api.Edge;
import jdsl.graph.api.Graph;
import jdsl.graph.api.Vertex;
import jdsl.graph.ref.IncidenceListGraph;
import uk.ac.ebi.intact.application.mine.business.Constants;
import uk.ac.ebi.intact.application.mine.business.IntactUserI;
import uk.ac.ebi.intact.application.mine.business.MineException;
import uk.ac.ebi.intact.application.mine.business.graph.model.EdgeObject;
import uk.ac.ebi.intact.application.mine.business.graph.model.MineData;
import uk.ac.ebi.intact.application.mine.business.graph.model.NetworkKey;
import uk.ac.ebi.intact.application.mine.business.graph.model.SearchObject;

/**
 * The <tt>MineHelper</tt> provides all methods to coordinate between the web
 * application and the algorithm. It contains several methods which work on
 * database level.
 * 
 * @author Andreas Groscurth
 */
public class MineHelper {
    private IntactUserI intactUser;

    /**
     * Creates a new MineHelper which organise the search for the minimal
     * connection network.
     * 
     * @param helper the intacthelper
     */
    public MineHelper(IntactUserI user) {
        this.intactUser = user;
    }

    /**
     * Creates a map which maps a vertex of the graph to a <tt>SearchObject</tt>.
     * The vertex is found via searching the given accMap which contains for
     * each search accnr the corresponding vertex in the graph.
     * 
     * @param accMap the map which maps the search ac with the vertex of the
     *            graph
     * @return @throws MineException if for a given search ac no data was found
     *         in the database
     */
    private Map getSearchNodes(Map accMap, Collection searchAc)
            throws MineException {
        Map map = new Hashtable();
        Object key;
        Vertex vertex;
        int i = 0;
        for (Iterator iter = searchAc.iterator(); iter.hasNext();) {
            key = iter.next();
            // the vertex is fetched
            vertex = (Vertex) accMap.get(key);
            // if no vertex could be found for the given accnr
            if (vertex == null) {
                throw new MineException("No node in the graph could be "
                        + "found for the accession number " + key + " ");
            }
            // the vertex is stored with the corresponding search object
            map.put(vertex, new SearchObject(i++));
        }
        return map;
    }

    /**
     * Fetchs for all provided accession numbers the biosource taxid and the
     * graphid where the acnr can be found and stores this data in a map. The
     * key of the map is a wrapper class to store the taxid and the graphid, the
     * value is a collection of search accession numbers belonging to the
     * biosource and graphid.
     * 
     * @param searchAc the search accession numbers
     * @return @throws MineException
     */
    public Map getNetworkMap(Collection searchFor) throws MineException {
        Map networks = new Hashtable();
        try {
            // statement to select the biosource and graphid for a specific
            // interactor
            PreparedStatement pstm = intactUser.getDBConnection()
                    .prepareStatement(
                            "SELECT DISTINCT bac, graphid FROM ia_interactions "
                                    + "WHERE p1ac=? or p2ac=?");

            String ac;
            NetworkKey key;
            ResultSet set;
            Collection search;

            for (Iterator iter = searchFor.iterator(); iter.hasNext();) {
                ac = iter.next().toString();
                pstm.setString(1, ac);
                pstm.setString(2, ac);
                set = pstm.executeQuery();

                // if something was found in the database a new key is created
                // with the database information, otherwise the dummy key is
                // taken
                key = !set.next() ? Constants.DUMMY_KEY : new NetworkKey(set
                        .getString(1), set.getInt(2));

                // if the network map contains the current key the search ac is
                // added to the collection of the current key, otherwise a new
                // collection is added to the map.
                if (networks.containsKey(key)) {
                    ((Collection) networks.get(key)).add(ac);
                }
                else {
                    search = new HashSet();
                    search.add(ac);
                    networks.put(key, search);
                }
                set.close();
            }
            pstm.close();
        }
        catch (Exception e) {
            throw new MineException(e);
        }
        return networks;
    }

    /**
     * Computes the minimal network for the given search ac.
     * 
     * @param key the wrapper class with the biosource and graphid
     * @param searchAc the ac to search for
     * @throws MineException
     */
    public final Collection mine(MineData mineData, Collection searchAc)
            throws MineException {
        // the search nodes are creates
        Map searchMap = getSearchNodes(mineData.getAccMap(), searchAc);
        Graph graph = mineData.getGraph();
        // the storage class is created
        Storage storage = new MineStorage(graph.numVertices());
        Dijkstra d = new Dijkstra(storage, searchMap);
        // for easy access the nodes and the search objects are rearranged into
        // an array
        Vertex[] nodes = (Vertex[]) searchMap.keySet().toArray(
                new Vertex[searchMap.keySet().size()]);
        SearchObject[] se = (SearchObject[]) searchMap.values().toArray(
                new SearchObject[searchMap.values().size()]);

        for (int i = 0; i < nodes.length; i++) {
            for (int j = i + 1; j < nodes.length; j++) {
                // the bitid of the start node is fetched
                int bitID = se[i].getBitID();
                // if a path between the two of them is not already found
                if ((se[j].getBitID() & bitID) != bitID) {
                    // the path is searched
                    d.execute(graph, nodes[i], nodes[j]);
                    storage.cleanup();
                }
            }
        }

        Collection mine = new HashSet();
        Edge edge;
        Vertex[] v;
        for (int i = 0; i < se.length; i++) {
            Sequence seq = se[i].getPath();
            // if no path is found for the current entry
            if (seq == null) {
                continue;
            }
            for (ObjectIterator iter = seq.elements(); iter.hasNext();) {
                // the current edge of the path
                edge = (Edge) iter.nextObject();
                // the end nodes of the edges are fetched
                v = graph.endVertices(edge);
                mine.add(v[0].element());
                mine.add(v[1].element());
            }
        }
        try {
            return mine.size() == 0 ? mine : getShortLabels(mine);
        }
        catch (SQLException e) {
            throw new MineException(e);
        }
    }

    /**
     * Returns the shortlabels of the found ac nr.
     * 
     * @param acs the collection with the search ac nr.
     * @return a collection with the shortlabels
     * @throws SQLException wether something failed with the db connection
     */
    public Collection getShortLabels(Collection acs) throws SQLException {
        StringBuffer buf = new StringBuffer();
        String ac;
        for (Iterator iter = acs.iterator(); iter.hasNext();) {
            ac = iter.next().toString();
            buf.append("'" + ac + "'");
            if (iter.hasNext()) {
                buf.append(",");
            }
        }

        String query = "SELECT shortlabel FROM ia_interactor "
                + "WHERE ac IN (" + buf.toString() + ")";
        acs.clear();
        Statement stm = intactUser.getDBConnection().createStatement();
        ResultSet set = stm.executeQuery(query);
        while (set.next()) {
            acs.add(set.getString(1));
        }
        set.close();
        stm.close();

        return acs;
    }

    /**
     * Builds the graph for the given biosource taxid and graphid
     * 
     * @param key the wrapper class with the taxid and graphid
     * @return @throws SQLException
     */
    public MineData buildGraph(NetworkKey key) throws SQLException {
        Statement stm = intactUser.getDBConnection().createStatement();
        ResultSet set = null;
        IncidenceListGraph graph = null;
        Vertex v1, v2;
        String acc1, acc2, interAcc;
        Map bioMap = new Hashtable();

        String query = "SELECT * FROM ia_interactions WHERE bac='"
                + key.getBioSourceTaxID() + "' AND graphID=" + key.getGraphID();
        set = stm.executeQuery(query);
        graph = new IncidenceListGraph();
        while (set.next()) {
            acc1 = set.getString(1).trim().toUpperCase();
            acc2 = set.getString(2).trim().toUpperCase();
            interAcc = set.getString(5);
            if (!bioMap.containsKey(acc1)) {
                v1 = graph.insertVertex(acc1);
                bioMap.put(acc1, v1);
            }
            if (!bioMap.containsKey(acc2)) {
                v2 = graph.insertVertex(acc2);
                bioMap.put(acc2, v2);
            }
            v1 = (Vertex) bioMap.get(acc1);
            v2 = (Vertex) bioMap.get(acc2);
            graph
                    .insertEdge(v1, v2, new EdgeObject(interAcc, set
                            .getDouble(6)));
        }
        set.close();
        stm.close();
        return new MineData(graph, bioMap);
    }
}