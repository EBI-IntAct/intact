/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.webapp.search.business;



/**
 * The business interface for the Web Intact webapp. This defines
 * methods that a client (servlet) may call on the webapp.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface IntactServiceIF {

    /**
     * Returns a hierarchy view property for given key as a <code>String</code>.
     * @param key the key to return the property for.
     * @return the property associated with <code>key</code>
     */
    public String getHierarchViewProp(String key);
}
