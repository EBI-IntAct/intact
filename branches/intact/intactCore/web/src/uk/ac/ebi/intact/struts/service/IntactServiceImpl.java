/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.service;

import java.util.*;
import java.beans.*;

import uk.ac.ebi.intact.struts.framework.exceptions.InvalidLoginException;
import uk.ac.ebi.intact.struts.framework.exceptions.MissingIntactTypesException;
import uk.ac.ebi.intact.persistence.*;
import uk.ac.ebi.intact.model.Constants;
import uk.ac.ebi.intact.util.Key;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

import javax.servlet.ServletException;

/**
 * Implments the IntactService interface.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntactServiceImpl implements IntactService {

    /**
     * The data source.
     */
    private DAOSource myDataSource;

    /**
     * Intact Types.
     */
    private ResourceBundle myIntactTypes;

    /**
     * Intact types reflection map.
     */
    private Map myNameToClassInfo = new HashMap();

    /**
     * Reference to the Intact Helper.
     */
    private IntactHelper myIntactHelper;

    /**
     * Constructs an instance of this class with given mapping file and
     * the name of the data source class.
     *
     * @param mapping the name of the mapping file.
     * @param dsClass the class name of the Data Source.
     *
     * @exception DataSourceException for error in getting the data source.
     */
    public IntactServiceImpl(String mapping, String dsClass)
        throws DataSourceException {
        myDataSource = DAOFactory.getDAOSource(dsClass);

        // Pass config details to data source - don't need fast keys as only
        // accessed once
        Map fileMap = new HashMap();
        fileMap.put(Constants.MAPPING_FILE_KEY, mapping);
        myDataSource.setConfig(fileMap);
    }

    // Implements business methods

    public IntactUser authenticate(String username, String password)
        throws InvalidLoginException {

        // The intact user to return.
        IntactUser user;

        // Just a dummy validation.
        if (username.equals("abc") && password.equals("abc")) {
            user = new IntactUser(username, password);
        }
        else {
            throw new InvalidLoginException();
        }
        return user;
    }

    public void setIntactTypes(String types) throws MissingIntactTypesException,
        ClassNotFoundException, IntrospectionException {
        try {
            myIntactTypes = ResourceBundle.getBundle(types);
        }
        catch (MissingResourceException ex) {
            throw new MissingIntactTypesException(
                "Unable to find IntactTypes resource");
        }
        // We must have Intact Types to search for; in other words, resource
        // bundle can't be empty.
        if (!myIntactTypes.getKeys().hasMoreElements()) {
            throw new MissingIntactTypesException(
                "Can't build a map file from an empty IntactTypes resource file");
        }
//        HashMap map = new HashMap();
//
//        //don't want any other threads messing up the map....
//        synchronized(map) {
//            for (Enumeration enum = rb.getKeys(); enum.hasMoreElements();) {
//                String type = (String) enum.nextElement();
//                Key key = new Key(type);
//                map.put(key, rb.getString(type));
//            }
//        }
        // Loop through the resource bundle.
        for (Enumeration enum = myIntactTypes.getKeys(); enum.hasMoreElements();) {
            // Get the class type for the current key.
            String type = myIntactTypes.getString((String) enum.nextElement());
            // Create our own key to put into the map.
            Key key = new Key(type);
            BeanInfo info = Introspector.getBeanInfo(Class.forName(type));
            PropertyDescriptor[] fieldDescs = info.getPropertyDescriptors();
            myNameToClassInfo.put(key, fieldDescs);
        }
    }

    public IntactHelper getIntactHelper() throws IntactException {
        // Only create an instance if we don't have one.
        if (myIntactHelper == null) {
            createIntactHelper();
        }
        return myIntactHelper;
    }

    public String getClassName(String topic) {
        return myIntactTypes.getString(topic);
    }

    public DAO getDAO() throws DataSourceException {
        return myDataSource.getDAO();
    }

    /**
     * Create an instance of <code>IntactHelper</code> instance.
     *
     * @exception IntactException thrown for any errors creating an
     * <tt>IntactHelper</tt> instance.
     */
    private void createIntactHelper() throws IntactException {
        if (myNameToClassInfo == null) {
            myIntactHelper = new IntactHelper(myDataSource);
        }
        else {
            // pass in the pre-configured reflection data
            myIntactHelper = new IntactHelper(myNameToClassInfo, myDataSource);
        }
    }
}
