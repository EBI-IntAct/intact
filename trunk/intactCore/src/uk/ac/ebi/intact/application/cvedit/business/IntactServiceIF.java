/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.business;

import java.util.Collection;

/**
 * The business interface for the Web Intact application. This defines
 * methods that a client (servlet) may call on the application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface IntactServiceIF {

    /**
     * The key to access the CV object types.
     */
    public static final String INTACT_TYPES = "intactTypes";

    /**
     * Returns the class name associated with the give topic.
     * @param topic the topic to search in the Intact types resource.
     * @return the classname saved under <code>topic</code>.
     */
    public String getClassName(String topic);

    /**
     * Returns a collection of Intact types.
     * @return an <code>ArrayList</code> of Intact types. The list sorted in
     * alphabetical order.
     */
    public Collection getIntactTypes();
}
