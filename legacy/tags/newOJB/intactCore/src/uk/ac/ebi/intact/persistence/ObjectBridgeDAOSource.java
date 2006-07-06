/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;

import java.util.*;
import java.io.*;

import org.apache.ojb.broker.*;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
// import org.apache.ojb.broker.metadata.*;

import org.apache.log4j.Logger;

import uk.ac.ebi.intact.model.Constants;

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


    //holds the filename containing the OR mapping data
    //NB ** this is no longer required for OJB rc5+. **
    private String repositoryFile;

    //holds any username that overrides a default user
    private String user;

    //used to override any default password specified
    private String password;

    //OJB class holding configuration details
    private MetadataManager metaData;

    //Default broker key to connect to a DB using info specified in config files
    private PBKey defaultKey;

    //String used by OJB to identify connection descriptors
    private String jcdAlias;

    //The OJB connection descriptor for a default connection
    private JdbcConnectionDescriptor defaultDbDescriptor;

    private transient Logger logger;

    /**
     * Default constructor. Sets up the logger, metadata, default broker
     * key, DB connection details for a default DB connection as specified
     * in the OJB configuration files.
     */
    public ObjectBridgeDAOSource() {

        logger = Logger.getLogger(OJB_LOGGER_NAME);
        metaData = MetadataManager.getInstance();
        defaultKey = metaData.getDefaultPBKey();
        defaultDbDescriptor = metaData.connectionRepository().getDescriptor(defaultKey);
        jcdAlias = defaultDbDescriptor.getJcdAlias();
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
        logger = Logger.getLogger(OJB_LOGGER_NAME);
    }

    /**
     *  <p>This method sets up the configuration of ObjectBridge, based on
     * the content of the configuration file whose name is passed
     * in as a parameter.</p>
     *
     * @param configFiles - This should contain the configuration file name for OJB.
     * @exception DataSourceException - thrown if there are problems obtaining
     * configuration data
     * @deprecated This method is no longer required for use with OJB rc5+.
     *
     */
    public void setConfig(Map configFiles) throws DataSourceException {


        //get the filenames from the config map..NB don't like using the constant keys!!
        repositoryFile = (String)configFiles.get(Constants.MAPPING_FILE_KEY);

        if (repositoryFile == null) {

            throw new DataSourceException("failed to configure data source - no mapping file!");
        }

    }

    /**
     * Used to specify a user for connecting to the persistent store. If this
     * method is not used, default user details will be obtained from details supplied
     * in the setConfig method (eg from a supplied config file).
     * @param user the username to use for connection (overrides any default)
     */
    public void setUser(String user) {

        this.user = user;
    }

    public String getUser() {

        return user;
    }

    /**
     * Used to define a password which overrides any default supplied via config data.
     * Should typically be used in conjunction with the setUser method for consistency.
     * @param password the password to be used for persistent store connection.
     */
    public void setPassword(String password) {

        this.password = password;
    }

    public String getPassword() {

        return password;
    }

    /**
     *  This method returns a connection to the data source, ie in this case
     * a broker instance which provides database connection.
     *
     * @return a Data Access Object (connection)
    */
    public DAO getDAO() throws DataSourceException {

        PersistenceBroker broker = null;
        try {

            //NB for rc5 we do not need to pass the config file name - instead simply
            //use the metadatamanager to obtain the default PBKey, which is picked up
            //from the OJB default connection descriptor.
            PBKey key = null;
            if((user == null)) {
                //OK to use the default user/password to build the DAO
                key = defaultKey;
            }
            else {

                //assume the following:
                //1) Users only connect to the default DB specified in config files
                //2) the password to be used should go with the specified new user
                key = new PBKey(jcdAlias, user, password);
            }

            broker = PersistenceBrokerFactory.createPersistenceBroker(key);
        }
        catch(Exception e) {

            //could be any of parser, SAX or IO exceptions.....
            String msg = "error - unable to create a DAO object; see stack trace...";

            throw new DataSourceException(msg, e);
        }

        //create an ObjectBridgeDAO, passing the initialised broker as a param
        return new ObjectBridgeDAO(broker);

    }

    /**
     *  Returns a connection to the data source, and connects to it using
     * the supplied username and password. If the username is null or the details
     * are invalid then a default connection will be attempted. This method is
     * simply a convenience method so users may avoid first calling setUser and
     * setPassword.
     * @param user - the username to connect with
     * @param password - user's password (null values allowed)
     * @return a Data Access Object (connection)
     */
     public DAO getDAO(String user, String password) throws DataSourceException {

        setUser(user);
        setPassword(password);
        return getDAO();

    }

    /**
     * @deprecated The file name is not required for OJB rc5+.
     * @return
     */
    public String getConfig() {
        return "config file for OJB is internally specified for OJB rc5+";
    }

    /**
     * Provides the name of the data source for a default connection.
     * @return String the data source name.
     */
    public String getDataSourceName() {
        return defaultDbDescriptor.getDatasourceName();
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
            defaultDbDescriptor.setUseAutoCommit(JdbcConnectionDescriptor.AUTO_COMMIT_SET_FALSE);
        }
        else {
            //set to OJB default
            defaultDbDescriptor.setUseAutoCommit(JdbcConnectionDescriptor.AUTO_COMMIT_SET_TRUE_AND_TEMPORARY_FALSE);
        }
    }

    /**
     * Checks for auto-commit settings.
     * @return true if on, false otherwise.
     */
    public boolean isAutoSaveSet() {

        int commit = defaultDbDescriptor.getUseAutoCommit();
        if(commit == JdbcConnectionDescriptor.AUTO_COMMIT_SET_FALSE) return false;
        return true;
    }

    public void setLogger(Logger l) {

        this.logger = l;
    }

    public Logger getLogger() {

        return logger;
    }






}
