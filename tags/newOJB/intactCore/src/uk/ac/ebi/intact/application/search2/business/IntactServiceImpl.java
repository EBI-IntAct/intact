/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.business;

import uk.ac.ebi.intact.application.search2.struts.framework.util.SearchConstants;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Implments the IntactService interface.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntactServiceImpl implements IntactServiceIF {

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
     */
    public IntactServiceImpl(String configdir) throws IOException,
            MissingResourceException {
        myHvProps = ResourceBundle.getBundle(configdir + SearchConstants.HV_PROPS);
    }

    // Implements business methods

    public String getHierarchViewProp(String key) {
        return myHvProps.getString(key);
    }
}
