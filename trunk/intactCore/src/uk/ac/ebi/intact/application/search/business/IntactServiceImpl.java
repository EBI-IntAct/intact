/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.business;

import java.util.*;
import java.io.IOException;

import uk.ac.ebi.intact.application.search.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search.exception.MissingIntactTypesException;

/**
 * Implments the IntactService interface.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntactServiceImpl implements IntactServiceIF {

    /**
     * Holds Intact Types.
     */
    private ResourceBundle myIntactTypeProps;

    /**
     * Holds HierarchView properties.
     */
    private ResourceBundle myHvProps;

    /**
     * Constructs an instance with given resource file.
     *
     * @param configdir the configuartion directory.
     * @exception IOException if the method fails to load the properties file.
     * @exception MissingResourceException unable to load a resource file.
     * @exception MissingIntactTypesException no keys found in the Intact Type
     * resource file.
     */
    public IntactServiceImpl(String configdir) throws IOException,
            MissingResourceException, MissingIntactTypesException {
        myIntactTypeProps = ResourceBundle.getBundle(
                configdir + SearchConstants.INTACT_TYPE_PROPS);
        // We must have Intact Types to search for; in other words, resource
        // bundle can't be empty.
        if (!myIntactTypeProps.getKeys().hasMoreElements()) {
            throw new MissingIntactTypesException(
                    "IntactTypes resource file cannot be empty");
        }
        myHvProps = ResourceBundle.getBundle(configdir + SearchConstants.HV_PROPS);
    }

    // Implements business methods

    public String getClassName(String topic) {
        return myIntactTypeProps.getString(topic);
    }

    public String getHierarchViewProp(String key) {
        return myHvProps.getString(key);
    }
}
