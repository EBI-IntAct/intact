/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;

import java.util.*;
import java.io.*;

import org.apache.ojb.broker.*;
// import org.apache.ojb.broker.metadata.*;

import org.apache.log4j.Logger;

import uk.ac.ebi.intact.model.Constants;

/**
 *  <p>This class effectively wraps an ObjectBridge broker factory instance and abstracts
 *  away the details of the ObjectBridge creation mechanism.</p>
 *
 * @author Chris Lewington
 *
 */

public class ObjectBridgeDAOSource implements DAOSource, Serializable {

    public static final String OJB_LOGGER_NAME = "ojb";


    //holds the filename containing the OR mapping data
    private String repositoryFile;

    //holds any username that overrides a default user
    private String user;

    //used to override any default password specified
    private String password;

    private transient Logger logger;

    public ObjectBridgeDAOSource() {

        //just set up a logger
        try {
//            logger = LoggerFactory.getLogger(Class.forName("org.apache.ojb.broker.util.logging.Log4jLoggerImpl"));
            logger = Logger.getLogger(OJB_LOGGER_NAME);

        }
        catch(Exception ce) {

            // do nothing - not finding a logger is not a failure condition
        }
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
     * @param configFiles - a collection of config filenames (this is only one for ObjectBridge)
     *
     * @exception DataSourceException - thrown if there are problems obtaining
     * configuration data
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
        if (repositoryFile == null) {

            throw new DataSourceException("cannot obtain a data connection - no mapping file!");
        }
        try {

            broker = PersistenceBrokerFactory.createPersistenceBroker(new PBKey(repositoryFile));
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
     * are invalid then a default connection will be attempted.
     *
     * @param user - the username to connect with
     * @param password - user's password (null values allowed)
     * @return a Data Access Object (connection)
     */
     public DAO getDAO(String user, String password) throws DataSourceException {

        PersistenceBroker broker = null;
        if (repositoryFile == null) {

            throw new DataSourceException("cannot obtain a data connection - no mapping file!");
        }
        if(user == null) {

            //no valid username supplied - will connect with default user..
            logger.debug("cannot connect to data store with username of null - using default user details..");
            return this.getDAO();
        }
        try {

            //if username is non-null, just get a broker based on supplied details..
            //NB null passwords allowed
            PBKey key = new PBKey(repositoryFile, user, password);
            broker = PersistenceBrokerFactory.createPersistenceBroker(key);
        }
        catch(Exception e) {

            logger.debug("failed to obtain a connection with username [" + user + "] and password [" + password + "]; exception details:");
            logger.debug(e.toString());
            logger.debug("attempting default connection instead...");
            //first try and get a default connection
            try {
                return this.getDAO();
            }
            catch(DataSourceException de) {

                String msg = "invalid user details supplied - even failed to obtain a default connection!";
                throw new DataSourceException(msg, de);
            }
        }

        //create an ObjectBridgeDAO, passing the initialised broker as a param
        return new ObjectBridgeDAO(broker);

    }


    public String getDataSourceName() {

        //not important - will implement later if required...
        return null;
    }

    public ClassLoader getClassLoader() {

        //not supported by OJB broker interface
        return null;
    }

    public String getConfig() {

        return "configuration file is " + repositoryFile;
    }

    public void setAutoSave(boolean val) {

        //not important - will implement if required later...
    }

    public boolean isAutoSaveSet() {

        //not important - will implement later if required...
        return false;
    }

    public void setLogger(Logger l) {

        this.logger = l;
    }

    public Logger getLogger() {

        return logger;
    }






}
