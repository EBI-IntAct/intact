/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.business;

import java.util.*;

import uk.ac.ebi.intact.application.cvedit.exception.MissingIntactTypesException;
import org.apache.commons.collections.CollectionUtils;

/**
 * Implments the IntactService interface.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntactServiceImpl implements IntactServiceIF {

    /**
     * Intact Types.
     */
    private ResourceBundle myIntactTypes;

    /**
     * Constructs an instance with given resource file.
     *
     * @param types the name of the Intact types resource file.
     *
     * @exception MissingIntactTypesException for errors with IntactTypes
     * resource file. This may be for not able to find the resource file or
     * for an empty resource file.
     */
    public IntactServiceImpl(String types) throws MissingIntactTypesException {
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
    }

    public String getClassName(String topic) {
        return myIntactTypes.getString(topic);
    }

    public Collection getIntactTypes() {
        // The collection to return.
        List types = new ArrayList();
        CollectionUtils.addAll(types, myIntactTypes.getKeys());
        Collections.sort(types);
        return types;
    }
}
