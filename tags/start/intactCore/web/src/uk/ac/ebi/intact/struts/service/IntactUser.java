/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.service;

/**
 * Represents an Intact Web user. This is an immutable class.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntactUser {

    /**
     * The full name of this user.
     */
    private String myFullName;

    /**
     * The password (in clear text).
     */
    private String myPassword;

    /**
     * The username (must be unique).
     */
    private String myUserName;

    // Constructors

    /**
     * Constructs a user with given user name and password.
     * @param userName the user name of the user; shouldn't be null.
     * @para password the password for the user; shouldn't be null.
     */
    public IntactUser(String userName, String password) {
        myUserName = userName;
        myPassword = password;
    }

    // Public methods

    /**
     * Return the full name.
     * @return the full name.
     */
    public String getFullName() {
        return myFullName;
    }

    /**
     * Return the password.
     * @return the password in plain text.
     */
    public String getPassword() {
        return myPassword;
    }

    /**
     * Return the username.
     * @return the user name.
     */
    public String getUserName() {
        return myUserName;
    }
}