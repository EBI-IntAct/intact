/*
 * Created on 15.07.2004
 *
 */
package uk.ac.ebi.intact.application.mine.business.graph.model;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import uk.ac.ebi.intact.application.mine.business.MineException;

/**
 * @author Andreas Groscurth
 *  
 */
public interface ShortestPathHelper {
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
    public Map getNetworkMap(Collection search) throws MineException;

    /**
     * Computes the minimal network for the given search ac.
     * 
     * @param key the wrapper class with the biosource and graphid
     * @param searchAc the ac to search for
     * @throws MineException
     */
    public Collection getMine(MineData data, Collection nodes)
            throws MineException;

    /**
     * Builds the graph for the given biosource taxid and graphid
     * 
     * @param key the wrapper class with the taxid and graphid
     * @return @throws SQLException
     */
    public MineData buildGraph(NetworkKey key) throws SQLException;

    /**
     * Returns the shortlabels of the found ac nr.
     * 
     * @param acs the collection with the search ac nr.
     * @return a collection with the shortlabels
     * @throws SQLException wether something failed with the db connection
     */
    public Collection getShortLabels(Collection acs) throws SQLException;
}