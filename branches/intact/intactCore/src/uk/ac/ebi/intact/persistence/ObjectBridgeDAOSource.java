/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.persistence;

import java.util.*;
import java.io.*;

import org.apache.ojb.broker.*;
import org.apache.ojb.broker.util.logging.*;

import uk.ac.ebi.intact.model.Constants;

/**
 *  <p>This class effectively wraps an ObjectBridge broker factory instance and abstracts
 *  away the details of the ObjectBridge creation mechanism.</p>
 *
 * @author Chris Lewington
 *
 */

public class ObjectBridgeDAOSource implements DAOSource {


    //holds the filename containing the OR mapping data
    private String repositoryFile;

    public ObjectBridgeDAOSource() {

        //don't need to do anything!
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

            broker = PersistenceBrokerFactory.createPersistenceBroker(repositoryFile);
        }
        catch(Exception e) {

            //could be any of parser, SAX or IO exceptions.....
            String msg = "error - unable to create a DAO object; see stack trace...";

            throw new DataSourceException(e.toString() + e.getLocalizedMessage() + e.getMessage() + e.getClass().getName());
        }

        //create an ObjectBridgeDAO, passing the initialised broker as a param
        //NB can we set a logfile too *NO!*? Need to change the DAO I/F to take a
        //Logger instead...And what about the following methods -
        //they aren't supported in the OJB factory.......
        return new ObjectBridgeDAO(broker);

    }


    public String getDataSourceName() {

        //not supported by OJB broker interface
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

        //not supported by OJB broker interface
    }

    public boolean isAutoSaveSet() {

        //not supported by OJB broker interface
        return false;
    }

    public void setLogger(org.apache.ojb.broker.util.logging.Logger l) {

        //not required...
    }

    public Logger getLogger() {

        //NB the Castor datasource will need changing too..
        try {

            return LoggerFactory.getLogger(Class.forName("org.apache.ojb.broker.util.logging.Log4jLoggerImpl"));
        }
        catch(ClassNotFoundException c) {

            //set up a basic logger and return that instead (TBD...)
            return null;
        }

    }






}
