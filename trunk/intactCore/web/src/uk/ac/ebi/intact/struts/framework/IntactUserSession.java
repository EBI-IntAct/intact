/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.framework;

import java.util.Date;
import java.util.Calendar;

import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;

import uk.ac.ebi.intact.struts.service.IntactUser;

/**
 * This class stores information about an Intact Web user session. Instead of
 * binding multiple objects, only an object of this class is bound to a session,
 * thus serving a single access point for multiple information.
 * <p>
 * This class implements the <tt>ttpSessionBindingListener</tt> interface for it
 * can be notified of session time outs.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntactUserSession implements HttpSessionBindingListener {

    /**
     * The Intact user.
     */
    private IntactUser myIntactUser;

    /**
     * The session started time.
     */
    private Date myStartTime;

    /**
     * The selected topic.
     */
    private String mySelectedTopic;

    /**
     * Default constructor.
     */
    public IntactUserSession() {
        myStartTime = Calendar.getInstance().getTime();
    }

    // Implements ttpSessionBindingListener

    /**
     * Will call this method when an object is bound to a session.
     * Not doing anything.
     */
    public void valueBound(HttpSessionBindingEvent event) {
    }

    /**
     * Will call this method when an object is unbound from a session.
     */
    public void valueUnbound(HttpSessionBindingEvent event) {
        // Sets the Intact user to an invalid stage.
        setIntactUser(null);
    }

    /**
     * Sets the Intact User.
     * @param user the Intact User.
     */
    public void setIntactUser(IntactUser user) {
        myIntactUser = user;
    }

    /**
     * Sets the topic selected by the user.
     * @param topic the selected topic.
     */
    public void setSelectedTopic(String topic) {
        mySelectedTopic = topic;
    }

    /**
     * Return the topic selected by the user.
     * @return the topic selected by the user.
     */
    public String getSelectedTopic() {
        return mySelectedTopic;
    }
}
