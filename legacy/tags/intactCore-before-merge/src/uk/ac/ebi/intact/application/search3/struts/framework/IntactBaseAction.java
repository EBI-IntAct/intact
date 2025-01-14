/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.framework;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.commons.lang.exception.ExceptionUtils;
import uk.ac.ebi.intact.application.search3.business.Constants;
import uk.ac.ebi.intact.application.search3.business.IntactServiceIF;
import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.business.IntactUserImpl;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.business.IntactException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import java.util.Map;
import java.util.HashMap;

/**
 * Super class for all Intact related action classes.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public abstract class IntactBaseAction extends Action {

    /**
     * Logger for that class.
     */
    protected transient static final Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

    /**
     * The global Intact error key.
     */
    public static final String INTACT_ERROR = "IntactError";

    /**
     * Error container
     */
    private ActionErrors myErrors = new ActionErrors();

    /**
     * Returns the only instance of Intact Service instance.
     *
     * @return only instance of the <code>IntactServiceImpl</code> class.
     */
    protected IntactServiceIF getIntactService() {
        IntactServiceIF service = (IntactServiceIF)
                getApplicationObject(SearchConstants.INTACT_SERVICE);
        return service;
    }

    /**
     * Returns the Intact User instance saved in a session.
     *
     * @param session the session to access the Intact user object.
     * @return an instance of <code>IntactUserImpl</code> stored in
     *         <code>session</code>
     */
    protected IntactUserIF getIntactUser(HttpSession session) {
        IntactUserIF service = (IntactUserIF)
                session.getAttribute(SearchConstants.INTACT_USER);
        return service;
    }

    /**
     * Returns the session from given request. If there is no current session
     * then a new one will be created - as there is no login for search it is
     * not necessary to prevent new session creation.
     *
     * @param request the request to get the session from.
     * @return session associated with given request - either the current session
     *         or a new one.
     */
    protected HttpSession getSession(HttpServletRequest request) {
        return request.getSession();
    }

    /**
     * Clear error container.
     */
    protected void clearErrors() {
        if (!myErrors.isEmpty()) {
            myErrors.clear();
        }
    }

    /**
     * Adds an error with given key.
     *
     * @param key the error key. This value is looked up in the
     *            IntactResources.properties bundle.
     */
    protected void addError(String key) {
        myErrors.add(INTACT_ERROR, new ActionError(key));
    }

    /**
     * Adds an error with given key and value.
     *
     * @param key   the error key. This value is looked up in the
     *              IntactResources.properties bundle.
     * @param value the value to substitute for the first place holder in the
     *              IntactResources.properties bundle.
     */
    protected void addError(String key, String value) {
        myErrors.add(INTACT_ERROR, new ActionError(key, value));
    }

    /**
     * Saves the errors in given request for &lt;struts:errors&gt; tag.
     *
     * @param request the request to save errors.
     */
    protected void saveErrors(HttpServletRequest request) {
        super.saveErrors(request, myErrors);
    }

    ///////////////////////
    // Helper methods.

    /**
     * A convenient method to retrieve an application object from a session.
     *
     * @param attrName the attribute name.
     * @return an application object stored in a session under <tt>attrName</tt>.
     */
    private Object getApplicationObject(String attrName) {
        return super.servlet.getServletContext().getAttribute(attrName);
    }

    /**
     * Useful method for subclasses to use when for some reason they have lost a user
     * object (eg when a session has died or been lost in some other way). This method
     * will create a new session if necessary, create a new User object and then add
     * it to the session. If the process fails then struts errors will be logged and
     * null will be returned to the caller. Typically a subclass would call
     * this method upon finding that the request session has no User object.
     *
     * @param request The HTTP request object from which the session should be obtained
     * @return IntactUserIF the new User object, or null if the creation failed.
     */
    protected IntactUserIF setupUser(HttpServletRequest request) {

        // The servlet context contains the OJB setup info.
        ServletContext ctx = super.getServlet().getServletContext();
        logger.info("IN WELCOME ACTION");

        // Name of the mapping file and data source.
        String repfile = ctx.getInitParameter(uk.ac.ebi.intact.model.Constants.MAPPING_FILE_KEY);
        String ds = ctx.getInitParameter(SearchConstants.DATA_SOURCE);

        // Create an instance of IntactService.
        IntactUserIF user = null;
        try {
            user = new IntactUserImpl(repfile, ds);
            logger.info("new user created..");
        }
        catch (DataSourceException de) {
            // Unable to get a data source...can't proceed
            logger.info(ExceptionUtils.getStackTrace(de));
            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add(INTACT_ERROR, new ActionError("error.invalid.user"));
            super.saveErrors(request, errors);
            return null;
        }
        catch (IntactException se) {
            // Unable to construct lists such as topics, db names etc.
            logger.info(ExceptionUtils.getStackTrace(se));
            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add(INTACT_ERROR, new ActionError("error.search"));
            super.saveErrors(request, errors);
            return null;
        }
        catch (Exception e) {
            logger.info("failed to create user - unexpected error!!");
            logger.info(ExceptionUtils.getStackTrace(e));
        }

        // Save the user. For the moment, create a new session.
        HttpSession session = request.getSession();
        session.setAttribute(SearchConstants.INTACT_USER, user);

        // The map to hold intact view beans; this will be filled later on.
        Map idToView = new HashMap();
        session.setAttribute(SearchConstants.FORWARD_MATCHES, idToView);

        //creation succeeded - return the new user object
        return user;
    }
}