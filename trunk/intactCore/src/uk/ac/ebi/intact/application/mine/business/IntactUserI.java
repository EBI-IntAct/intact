/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */

package uk.ac.ebi.intact.application.mine.business;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collection;

import javax.servlet.http.HttpSessionBindingListener;

import uk.ac.ebi.intact.application.mine.business.graph.model.MineData;
import uk.ac.ebi.intact.application.mine.business.graph.model.NetworkKey;
import uk.ac.ebi.intact.business.IntactHelper;

/**
 * A can implement the interface <tt>IntactUserI</tt> if it wants to act as
 * utility class to store informations for the mine application.
 * 
 * @author Andreas Groscurth
 */
public interface IntactUserI extends Serializable, HttpSessionBindingListener,
        uk.ac.ebi.intact.application.commons.business.IntactUserI {
    /**
     * Returns the underlying database connection.
     * 
     * @return the database connection
     */
    public Connection getDBConnection();

    /**
     * Returns the <tt>MineData</tt> for the given key. The <tt>MineData</tt>
     * provides among other things the graph to search
     * 
     * @param key a key to access the data
     * @return the data according to the key
     */
    public MineData getMineData(NetworkKey key);

    /**
     * Adds a new minedata to the user
     * 
     * @param key the key
     * @param md the value
     */
    public void addToGraphMap(NetworkKey key, MineData md);

    /**
     * Returns the shortest paths found by the algorithm
     * 
     * @return the map containing the shortest paths
     */
    public Collection getPaths();

    /**
     * Clear all found paths
     */
    public void clearAll();

    /**
     * Adds a path to all found paths
     * 
     * @param path the new path
     */
    public void addToPath(Collection path);

    /**
     * Adds a new collection of singletons
     * 
     * @param col the singletons
     */
    public void addToSingletons(Collection col);

    /**
     * Returns the singletons
     * 
     * @return a collection of singletons
     */
    public Collection getSingletons();

    /**
     * Returns the link for the hierarchView link
     * 
     * @return a formatted string for the link
     */
    public String getHVLink();

    /**
     * Returns the intact helper
     * 
     * @return the intact helper
     */
    public IntactHelper getIntactHelper();
}