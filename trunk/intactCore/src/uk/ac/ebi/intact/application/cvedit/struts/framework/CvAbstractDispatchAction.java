/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.framework;

import org.apache.struts.actions.LookupDispatchAction;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import uk.ac.ebi.intact.application.cvedit.exception.SessionExpiredException;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.application.cvedit.business.IntactServiceIF;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants;

/**
 * The super class for all the CV edit actions.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $id$$
 */
public abstract class CvAbstractDispatchAction extends LookupDispatchAction  {

    /**
     * Returns the only instance of Intact Service instance.
     * @return only instance of the <code>IntactServiceImpl</code> class.
     */
    protected IntactServiceIF getIntactService() {
        IntactServiceIF service = (IntactServiceIF)
            getApplicationObject(CvEditConstants.INTACT_SERVICE);
        return service;
    }

    /**
     * Returns the session from given request. No new session is created.
     * @param request the request to get the session from.
     * @return session associated with given request.
     * @exception uk.ac.ebi.intact.application.cvedit.exception.SessionExpiredException for an expired session.
     *
     * <pre>
     * post: return <> Undefined
     * </pre>
     */
    protected HttpSession getSession(HttpServletRequest request)
            throws SessionExpiredException {
        // Don't create a new session.
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new SessionExpiredException();
        }
        return session;
    }

    /**
     * Returns the Intact User instance saved in a session for given
     * Http request.
     *
     * @param request the Http request to access the Intact user object.
     * @return an instance of <code>IntactUserImpl</code> stored in a session.
     * No new session is created.
     * @exception uk.ac.ebi.intact.application.cvedit.exception.SessionExpiredException for an expired session.
     *
     * <pre>
     * post: return <> Undefined
     * </pre>
     */
    protected IntactUserIF getIntactUser(HttpServletRequest request)
            throws SessionExpiredException {
        IntactUserIF user = (IntactUserIF)
            getSession(request).getAttribute(CvEditConstants.INTACT_USER);
        if (user == null) {
            throw new SessionExpiredException();
        }
        return user;
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
     * Returns the course of action based on the last search result. If the
     * last search produced multiple entries then this method returns the
     * path to multiple results page. For a single result, the method
     * returns the path to the search page.
     *
     * @param user the user to determine where to go.
     *
     * <pre>
     * post: return = CvEditConstants.FORWARD_SEARCH or
     *                CvEditConstants.FORWARD_RESULTS
     * post: return <> Undefined
     * </pre>
     */
    protected String getForwardAction(IntactUserIF user) {
        return user.hasSingleSearchResult() ? CvEditConstants.FORWARD_SEARCH :
                CvEditConstants.FORWARD_RESULTS;
    }

    // Helper Methods

    /**
     * A convenient method to retrieve an application object from a session.
     * @param attrName the attribute name.
     * @return an application object stored in a session under <tt>attrName</tt>.
     */
    private Object getApplicationObject(String attrName) {
        return super.servlet.getServletContext().getAttribute(attrName);
    }
}
