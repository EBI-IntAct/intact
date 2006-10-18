/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.business;

import uk.ac.ebi.intact.searchengine.SearchEngineConstants;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Implements the IntactService interface.
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
     *
     * @throws MissingResourceException unable to load a resource file.
     */
    public IntactServiceImpl( String configdir ) throws MissingResourceException {
        myHvProps = ResourceBundle.getBundle( configdir + SearchEngineConstants.HV_PROPS );
    }

    /**
     * no arg constructor disabled.
     */
    private IntactServiceImpl() {
    }

    // Implements business methods

    /**
     * Get the HierarchView property from the given key.
     *
     * @param key the key giving access to the property.
     *
     * @return a hierarchView property.
     */
    public String getHierarchViewProp( String key ) {
        return myHvProps.getString( key );
    }
}
