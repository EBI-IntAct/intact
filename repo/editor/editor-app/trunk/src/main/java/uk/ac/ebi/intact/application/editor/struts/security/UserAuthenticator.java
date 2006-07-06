/*
Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.security;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.editor.business.EditUser;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.AuthenticateException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.business.IntactException;

/**
 * The custom authenticator for Intact editor.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class UserAuthenticator {

    /**
     * Authenticate a user by accessing a persistent system using given
     * user name and password
     * @param username the user name
     * @param password the password
     * @return a user object if a user exists for <code>username</code> and
     * <code>password</code>.
     * @exception AuthenticateException invalid user for given
     * <code>username</code> and <code>password</code>.
     */
    public static EditUserI authenticate(String username, String password)
            throws AuthenticateException {
        try {
            return new EditUser(username, password);
        }
        catch (IntactException ie) {
            Logger.getLogger(EditorConstants.LOGGER).error(username + " disallowed");
            throw new AuthenticateException(username + " disallowed");
        }
    }
}
