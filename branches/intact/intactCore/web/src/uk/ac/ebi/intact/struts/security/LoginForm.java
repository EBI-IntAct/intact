/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.security;

import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Form bean for the user logon page.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class LoginForm extends ActionForm {

    /**
     * The user name.
     */
    private String myUsername;

    /**
     * The password.
     */
    private String myPassword;

    /**
     * The topic selected.
     */
    private String myTopic;

    /**
     * Return the username.
     * @return the username.
     */
    public String getUsername() {
        return myUsername;
    }

    /**
     * Set the username.
     * @param username the new username
     */
    public void setUsername(String username) {
        myUsername = username;
    }

    /**
     * Return the password.
     * @return the password as a string.
     */
    public String getPassword() {
        return myPassword;
    }

    /**
     * Set the password.
     * @param password the new password
     */
    public void setPassword(String password) {
        myPassword = password;
    }

    /**
     * Sets the topic.
     * @param topic the topic to set.
     */
    public void setTopic(String topic) {
        myTopic = topic;
    }

    /**
     * Return the selected topic.
     * @return the selected topic.
     */
    public String getTopic() {
        return myTopic;
    }

    /**
     * Validate the properties that have been set from the HTTP request.
     *
     * @param mapping the mapping used to select this instance
     * @param request the servlet request we are processing
     * @return <tt>ActionErrors</tt> object that contains validation errors. If
     * no errors are found, <tt>null</tt> or an empty <tt>ActionErrors</tt>
     * object is returned.
     */
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if (myUsername == null || myUsername.length() < 1) {
            errors.add(ActionErrors.GLOBAL_ERROR,
                new ActionError("global.required", "username"));
        }
        if (myPassword == null || myPassword.length() < 1) {
            errors.add(ActionErrors.GLOBAL_ERROR,
                new ActionError("global.required", "password"));
        }
        if (myTopic == null || myTopic.length() < 1) {
            errors.add("topic", new ActionError("error.topic.required"));
        }
        return errors;
    }

    /**
     * Reset all properties to their default values.
     *
     * @param mapping the mapping used to select this instance
     * @param request the servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        myUsername = null;
        myPassword = null;
        myTopic = null;
    }
}
