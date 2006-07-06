/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.framework;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionMapping;

import uk.ac.ebi.intact.struts.service.IntactService;
import uk.ac.ebi.intact.struts.service.IntactUser;
import uk.ac.ebi.intact.struts.framework.util.WebIntactConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Super class for all Intact related action classes.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public abstract class IntactBaseAction extends Action {

    /**
     * Returns the IntactService instance.
     * @return only instance of the IntactService class.
     */
    protected IntactService getIntactService() {
        IntactService service = (IntactService)
            this.getApplicationObject(WebIntactConstants.SERVICE_INTERFACE);
        return service;
    }

    /**
     * A convenient method to retrieve an application object from a session.
     * @param attrName the attribute name.
     * @return an application object stored in a session under <tt>attrName</tt>.
     */
    protected Object getApplicationObject(String attrName) {
        return super.servlet.getServletContext().getAttribute(attrName);
    }

    /**
     * Convenience method that logs for agiven message.
     * @param message string that describes the error or exception
     */
    protected void log(String message) {
       if (super.servlet.getDebug() >= 1)
           super.servlet.log(message);
    }

    /**
     * Removes the obsolete form bean.
     * @param mapping the ActionMapping used to select this instance.
     * @param request the HTTP request we are processing.
     */
    protected void removeFormBean(ActionMapping mapping,
                                  HttpServletRequest request) {
       // Remove the obsolete form bean
       if (mapping.getAttribute() != null) {
           if ("request".equals(mapping.getScope())) {
               request.removeAttribute(mapping.getAttribute());
           } else {
              HttpSession session = request.getSession();
              session.removeAttribute(mapping.getAttribute());
           }
       }
    }

    /**
     * Returns the session from given request. No new session is created.
     * @param request the request to get the session from.
     * @return session associated with given request. Null is returned if there
     * is no session associated with <code>request</code>.
     */
    protected HttpSession getSession(HttpServletRequest request) {
        // Don't create a new session.
        return request.getSession(false);
    }


    /**
     * Retrieve the user session associated with the request.
     * @param request the HTTP request to search for the session request. If
     * the user session object is not found, this request will contain a new
     * user session object.
     * @return an existing user session if it is found in the request or a
     * new session if not found.
     */
//    protected IntactUserSession getIntactUser(HttpServletRequest request) {
//        IntactUserSession userSession = (IntactUserSession) getSessionObject(
//            request, WebIntactConstants.USER_SESSION);
//        // Create a new user session object if we don't have an existing one.
//        if (userSession == null) {
//            userSession = new IntactUserSession();
//            HttpSession session = request.getSession();
//            session.setAttribute(WebIntactConstants.USER_SESSION, userSession);
//        }
//        return userSession;
//    }

    /**
     * Retrieve a session object based on the request and attribute name.
     * @param request the HTTP request to retrieve a session object stored
     * under <tt>attrName</tt>.
     * @param attrName the name of the attribute.
     * @return the session object stored in <tt>request</tt> under
     * <tt>attrName</tt>. Could be <tt>null</tt> if there is no session
     * object stored under <tt>attrName</tt> in <tt>request</tt>.
     */
    protected Object getSessionObject(HttpServletRequest request,
                                      String attrName) {
        return getSession(request).getAttribute(attrName);
    }
}
