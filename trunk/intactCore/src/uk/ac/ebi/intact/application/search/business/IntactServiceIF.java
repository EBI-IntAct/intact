/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.business;

/**
 * The business interface for the Web Intact application. This defines
 * methods that a client (servlet) may call on the application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface IntactServiceIF {

    /**
     * Authenticate the user's credentials.
     *
     * @param username the user name; must not be null.
     * @param password the password for the user; must not be null.
     * @return an intact user object.
     *
     * @exception InvalidLoginException thrown for un unauthorized login.
     */
//    public IntactUser authenticate(String username, String password)
//        throws InvalidLoginException;

    /**
     * Returns the class name associated with the give topic.
     *
     * @param topic the topic to search in the Intact types resource.
     * @return the classname saved under <code>topic</code>.
     */
    public String getClassName(String topic);
}
