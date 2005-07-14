/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.metadata.MetadataManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 *  <p>This class effectively wraps an ObjectBridge broker factory instance and abstracts
 *  away the details of the ObjectBridge creation mechanism.</p>
 *
 * @author Chris Lewington
 * @version $Id$
 *
 */
public class ObjectBridgeDAOSource implements DAOSource, Serializable {

    public static final String OJB_LOGGER_NAME = "ojb";

    //holds any username that overrides a default user
    private String user;

    //used to override any default password specified
    private String password;

    private transient Logger logger;

    /**
     * Default constructor. Sets up the logger, metadata, default broker
     * key, DB connection details for a default DB connection as specified
     * in the OJB configuration files.
     */
    public ObjectBridgeDAOSource() {
        setLogger(Logger.getLogger(OJB_LOGGER_NAME));
    }

    // Methods to handle special serialization issues.

    /**
     * Logger is set in this method as it is declared as transient.
     * @param in the input stream
     * @throws IOException for errors in reading from the stream (required by
     * the method signature)
     * @throws ClassNotFoundException required by the method signature.
     */
    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        setLogger(Logger.getLogger(OJB_LOGGER_NAME));
    }

    public String getUser() {
        // Use the default name if not set.
        if (user == null) {
            setUser(getDefaultDbDescriptor().getUserName());
        }
        return user;
    }

    public String getPassword() {
        // Use the default password if not set.
        if (password == null) {
            setPassword(getDefaultDbDescriptor().getPassWord());
        }
        return password;
    }

    public DAO getDAO(String user, String password) throws DataSourceException {
        setUser(user);
        setPassword(password);
        return getDAO();
   }

    /**
     *  This method returns a connection to the data source, ie in this case
     * a broker instance which provides database connection.
     *
     * @return a Data Access Object (connection)
    */
    public DAO getDAO() throws DataSourceException {
        // Defaults to default key for a null user.
        MetadataManager metaData = MetadataManager.getInstance();

        PBKey key = (getUser() == null) ? metaData.getDefaultPBKey() :
                new PBKey(getJcdAlias(), getUser(), getPassword());

        PersistenceBroker broker = PersistenceBrokerFactory.createPersistenceBroker(key);
        //create an ObjectBridgeDAO, passing the initialised broker as a param
        return new ObjectBridgeDAO(broker);
    }

    /**
     * Provides the name of the data source for a default connection.
     * @return String the data source name.
     */
    public String getDataSourceName() {
        return getDefaultDbDescriptor().getDatasourceName();
    }

    /**
     * Provides access to the ClassLoader which was used to load up the
     * OJB classes themselves.
     * @return ClassLoader The ClassLoader used for OJB.
     */
    public ClassLoader getClassLoader() {
        return PersistenceBroker.class.getClassLoader();
    }

    /**
     * Sets the auto-commit value.
     * @param shouldSave true to have auto-commit on (default), false otherwise.
     */
    public void setAutoSave(boolean shouldSave) {
        if(!shouldSave) {
            getDefaultDbDescriptor().setUseAutoCommit(
                    JdbcConnectionDescriptor.AUTO_COMMIT_SET_FALSE);
        }
        else {
            //set to OJB default
            getDefaultDbDescriptor().setUseAutoCommit(
                    JdbcConnectionDescriptor.AUTO_COMMIT_SET_TRUE_AND_TEMPORARY_FALSE);
        }
    }

    /**
     * Checks for auto-commit settings.
     * @return true if on, false otherwise.
     */
    public boolean isAutoSaveSet() {
        int commit = getDefaultDbDescriptor().getUseAutoCommit();
        if(commit == JdbcConnectionDescriptor.AUTO_COMMIT_SET_FALSE) return false;
        return true;
    }

    public void setLogger(Logger l) {
        this.logger = l;
    }

    public Logger getLogger() {
        return logger;
    }

    // Helper Methods ----------------------------------------------------------

    private static JdbcConnectionDescriptor getDefaultDbDescriptor() {
        MetadataManager metaData = MetadataManager.getInstance();
        PBKey defaultKey = metaData.getDefaultPBKey();
        return metaData.connectionRepository().getDescriptor(defaultKey);
    }

    private static String getJcdAlias() {
        return getDefaultDbDescriptor().getJcdAlias();
    }

    /**
     * Used to specify a user for connecting to the persistent store. If this
     * method is not used, default user details will be obtained from details supplied
     * in the setConfig method (eg from a supplied config file).
     * @param user the username to use for connection (overrides any default)
     */
    private void setUser(String user) {
        this.user = user;
    }

    /**
     * Used to define a password which overrides any default supplied via config data.
     * Should typically be used in conjunction with the setUser method for consistency.
     * @param password the password to be used for persistent store connection.
     */
    private void setPassword(String password) {
        this.password = password;
    }
}
