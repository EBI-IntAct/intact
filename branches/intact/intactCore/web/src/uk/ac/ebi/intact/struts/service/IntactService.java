/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.service;

import uk.ac.ebi.intact.struts.framework.exceptions.InvalidLoginException;
import uk.ac.ebi.intact.struts.framework.exceptions.MissingIntactTypesException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.persistence.DAO;
import uk.ac.ebi.intact.persistence.DataSourceException;

import java.util.ResourceBundle;
import java.util.Map;
import java.beans.IntrospectionException;

/**
 * The business interface for the Web Intact application. This defines
 * methods that a client (servlet) may call on the application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface IntactService {

    /**
     * Authenticate the user's credentials.
     *
     * @param username the user name; must not be null.
     * @param password the password for the user; must not be null.
     * @return an intact user object.
     *
     * @exception InvalidLoginException thrown for un unauthorized login.
     */
    public IntactUser authenticate(String username, String password)
        throws InvalidLoginException;

    /**
     * Loads the Intact Types from a resource file.
     *
     * @param types the name of the Intact types resource file.
     *
     * @exception MissingIntactTypesException for errors with IntactTypes
     * resource file. This may be for not able to find the resource file or
     * for an empty resource file.
     * @exception ClassNotFoundException unable to load the class.
     * @exception IntrospectionException error thrown when getting bean info.
     */
    public void setIntactTypes(String types) throws MissingIntactTypesException,
        ClassNotFoundException, IntrospectionException;

    /**
     * Returns the handler to the Intact Helper to search the database.
     *
     * @return reference to the <code>IntactHelper</code>.
     *
     * @exception IntactException thrown for any errors in getting the helper.
     */
    public IntactHelper getIntactHelper() throws IntactException;

    /**
     * Returns the class name associated with the give topic.
     *
     * @param topic the topic to search in the Intact types resource.
     * @return the classname saved under <code>topic</code>.
     */
    public String getClassName(String topic);

    /**
     * Returns the reference to the DAO object. Just a wrapper for
     * <code>getDAO</code> method of <code>DAOSource</code> class.
     *
     * @exception DataSourceException for errors is retrieving <code>DAO</code>
     * instance.
     */
    public DAO getDAO() throws DataSourceException;
}
