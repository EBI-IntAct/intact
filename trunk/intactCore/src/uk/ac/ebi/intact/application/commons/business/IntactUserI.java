/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.commons.business;

/**
 * This interface defines methods common to all Intact WEB applications. Typically,
 * each WEB application has its own user and that specific user must extend from this
 * class (this common interfaced is used for sidebar footer).
 *
 * @version $Id$
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 */
public interface IntactUserI {

    /**
     * Returns the Intact user.
     * @return the Intact user currently logged in. This methods could return null
     * if there is no user associated with the current session (e.g., Editor) or
     * for errors in retrieving user information from the database.
     */
    public String getUserName();

    /**
     * The name of the database connected to.
     * @return the name of the database. Could be null for an error in getting
     * the information from the database.
     */
    public String getDatabaseName();
}
