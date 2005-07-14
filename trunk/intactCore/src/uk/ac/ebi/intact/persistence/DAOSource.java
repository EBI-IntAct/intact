/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;

import org.apache.log4j.Logger;

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

    public String getUser();
    public String getPassword();

    /**
     *  This method returns a connection to the data source.
     *
     * @return a Data Access Object (ie a connection)
     *
     * @exception DataSourceException - thrown if a DAO cannot be obtained
    */
    public DAO getDAO() throws DataSourceException;

    /**
     *  Returns a connection made using the specified user details. Note that
     * if either a null username is supplied, or a connection could not be made
     * with the supplied details, then a default connection will be attempted.
     *
     * @param user username to make a connection with
     * @param password the user's password (null values are allowed)
     * @return a Data Access Object
     * @exception DataSourceException - thrown if a DAO cannot be obtained
    */
    public DAO getDAO(String user, String password) throws DataSourceException;

    /**
     *  @return the name of the data source (as per configuration)
     */
    public String getDataSourceName();

    /**
     *   @return The class loader used to load the data source
     */
    public ClassLoader getClassLoader();

    /**
     *   Defines whether or not data are saved automatically.
     */
    public void setAutoSave(boolean val);

    /**
     *   @return true if data are set to be saved automatically, false otherwise
     */
    public boolean isAutoSaveSet();

    /**
     *  allows a logging destination to be specified
     *
     * @param logger A log4j <code>Logger</code> for logging output
     */
    public void setLogger(Logger logger);

    /**
     *   Returns the log destination being used for this data source
     *
     * @return PrintWriter a destination log interface
     */
    public Logger getLogger();
}
