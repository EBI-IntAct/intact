/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;

import java.util.*;

import org.apache.ojb.broker.util.logging.*;

/**
 *  <p>This interface defines the functionality usually provided by
 * a specific data source, eg Castor, oracle etc. In particular
 * it allows clients to obtain a Data Access Object (a connection rather than object-specific DAOs).
 * Note that only what might be termed "common" persistence layer
 * methods are presently included. </p>
 *
 * @author Chris Lewington
 *
 */
public interface DAOSource {


    /**
     *  <p>sets the configuration data for the data source. If configuration
     * fails then an exception is thrown back to the caller.</p>
     *
     * @param configFiles - the collection of configuration files to be used
     *
     * @exception DataSourceException - thrown if there are configuration problems
     *
     */
    public void setConfig(Map configFiles) throws DataSourceException;

    /**
     *  This method returns a connection to the data source.
     *
     * @return a Data Access Object (ie a connection)
     *
     * @exception DataSourceException - thrown if a DAO cannot be obtained
    */
    public DAO getDAO() throws DataSourceException;

    /**
     *  @return the name of the data source (as per configuration)
     */
    public String getDataSourceName();

    /**
     *   @return The class loader used to load the data source
     */
    public ClassLoader getClassLoader();

    /**
     *    @return the configuration information about the data source
     */
    public String getConfig();

    /**
     *   Defines whether or not data are saved automatically
     */
    public void setAutoSave(boolean val);

    /**
     *   @return true if data are set to be saved automatically, false otherwise
     */
    public boolean isAutoSaveSet();

    /**
     *  allows a logging destination to be specified
     *
     * @param p A <code>PrintWriter</code> for logging output
     */
    public void setLogger(Logger l);

    /**
     *   Returns the log destination being used for this data source
     *
     * @return PrintWriter a destination log interface
     */
    public Logger getLogger();
}
