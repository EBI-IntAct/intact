/*
Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.application.editor.business.EditUser;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.AuthenticateException;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.UserContext;

/**
 * The custom authenticator for Intact editor.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class UserAuthenticator {

    private static final Log log = LogFactory.getLog(UserAuthenticator.class);

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
           IntactContext intactContext = IntactContext.getCurrentInstance();
            UserContext userContext = intactContext.getUserContext();
            userContext.setUserId(username.toUpperCase());
            userContext.setUserPassword(password);
            return new EditUser(username, password);
        }
        catch (Exception ie) {
            log.error(username + " disallowed", ie);
            throw new AuthenticateException(username + " disallowed");
        }
    }
}
