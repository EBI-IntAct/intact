/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.framework;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;

import uk.ac.ebi.intact.application.search.business.IntactUserIF;
import uk.ac.ebi.intact.application.search.business.IntactServiceIF;
import uk.ac.ebi.intact.application.search.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.application.search.struts.view.IntactViewBean;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.persistence.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Super class for all Intact related action classes.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public abstract class IntactBaseAction extends Action {

    /** The global Intact error key. */
    public static final String INTACT_ERROR = "IntactError";

    /** Error container */
    private ActionErrors myErrors = new ActionErrors();

    /**
     * Returns the only instance of Intact Service instance.
     * @return only instance of the <code>IntactServiceImpl</code> class.
     */
    protected IntactServiceIF getIntactService() {
        IntactServiceIF service = (IntactServiceIF)
            getApplicationObject(WebIntactConstants.INTACT_SERVICE);
        return service;
    }

    /**
     * Returns the Intact User instance saved in a session for given
     * Http request.
     *
     * @param request the Http request to access the Intact user object.
     * @return an instance of <code>IntactUserImpl</code> stored in a session.
     * No new session is created.
     */
    protected IntactUserIF getIntactUser(HttpServletRequest request) {
        IntactUserIF service = (IntactUserIF)
            getSessionObject(request,WebIntactConstants.INTACT_USER);
        return service;
    }

    /**
     * Returns the Intact User instance saved in a session.
     *
     * @param session the session to access the Intact user object.
     * @return an instance of <code>IntactUserImpl</code> stored in
     * <code>session</code>
     */
    protected IntactUserIF getIntactUser(HttpSession session) {
        IntactUserIF service = (IntactUserIF)
            session.getAttribute(WebIntactConstants.INTACT_USER);
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
     *
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

    /**
     * Returns a view bean for JSP to display. This method tries to
     * reuse an existing bean without creating a new bean for each call.
     * If there is no existing bean found in a session object, then
     * a new bean is created.
     *
     * @param session the session object to retrieve an instance of
     * the view bean.
     *
     * @return an Intact view object; a new object if there is no existing
     * bean found in <code>session</code>.
     *
     * <pre>
     * post: return != null
     * </pre>
     */
    protected IntactViewBean getViewBean(HttpSession session) {
        // Reuse an existing bean without creating a new bean for each call.
        IntactViewBean viewbean = (IntactViewBean) session.getAttribute(
            WebIntactConstants.VIEW_BEAN);
        // Create a new bean if it doesn't exist.
        if (viewbean == null) {
            viewbean = new IntactViewBean();
        }
        return viewbean;
    }

    protected ActionErrors getErrors() {
        return myErrors;
    }

//    protected ActionErrors getErrorBean(HttpSession session) {
//        // Reuse an existing bean without creating a new bean for each call.
//        ActionErrors errorbean = (ActionErrors) session.getAttribute(
//            WebIntactConstants.ERROR_BEAN);
//        if (errorbean == null) {
//            log("Creating a new error bean");
//            errorbean = new ActionErrors();
//            session.setAttribute(WebIntactConstants.ERROR_BEAN, errorbean);
//        }
//        return errorbean;
//    }

    /**
     * Returns the current CvObject we are editing at the moment.
     * <code>null</code> object is returned if it hasn't been saved under a
     * session object.
     *
     * @param request the HTTP request to access the session object to
     * retrieve current Cv object.
     */
//    public CvObject getEditCvObject(HttpServletRequest request) {
//        // Handler to the IntactService.
//        IntactService service = getIntactService();
//
//        // The current CV object we are editing.
//        CvViewBean viewbean = (CvViewBean) getSessionObject(
//            request, WebIntactConstants.VIEW_BEAN);
//        return viewbean.getCvObject();
//    }

    /**
     * Wrapper to create an object via OJB layer. All the errors are saved
     * in internal error container.
     *
     * @param object the object to create.
     */
//    public void create(Object object) {
//        // Handler to the IntactService.
//        IntactService service = getIntactService();
//
//        try {
//            service.getDAO().create(object);
//        }
//        catch (CreateException ce) {
//            // Unable to create an annotation; no nested message provided.
//            addError("error.create", ce.getMessage());
//        }
//    }

//    public void update(Object object) {
//        // Handler to the IntactService.
//        IntactService service = getIntactService();
//
//        try {
//            //this.log("BEFORE update - COMMIT FLAG: " + service.getDAO().getConnection().getAutoCommit());
//            service.getDAO().update(object);
//            //this.log("After update - COMMIT FLAG: " + service.getDAO().getConnection().getAutoCommit());
//        }
//        catch (CreateException ce) {
//            // Unable to create an annotation; no nested message provided.
//            addError("error.create", ce.getMessage());
//        }
//    }

//    public void delete(Object object) {
//        // Handler to the IntactService.
//        IntactService service = getIntactService();
//
//        try {
//            log("Before delete: Transaction flag=" + service.getDAO().isActive());
//            service.getDAO().remove(object);
//            log("After delete: Transaction flag=" + service.getDAO().isActive());
//        }
//        catch (TransactionException te) {
//            // Unable to create an annotation; no nested message provided.
//            addError("error.create", te.getMessage());
//        }
//        catch (DataSourceException dse) {
//            log(dse.getMessage());
//        }
//    }

//    public Object find(String objectType, String searchParam, String searchValue) {
//        // Handler to the IntactService.
//        IntactService service = getIntactService();
//
//        try {
//            service.getDAO().find(objectType, searchParam, searchValue);
//        }
//        catch (DataSourceException dse) {
//            // Can't acquire the DAO instance to create.
//            addError("error.dao", dse.getNestedMessage());
//        }
//        catch (SearchException se) {
//            // Unable to create an annotation; no nested message provided.
//            addError("error.create", se.getMessage());
//        }
////        catch (SQLException sqlex) {
////            log("SQL Error: " + sqlex.getMessage());
////        }
//    }

    /**
     * Wrapper to begin a transaction via OJB layer. All the errors are saved
     * in internal error container.
     */
//    public void begin() {
//        // Handler to the IntactService.
//        IntactService service = getIntactService();
//
//        try {
//            //this.log("BEFORE BEGIN - COMMIT FLAG: " + service.getDAO().getConnection().getAutoCommit());
//            service.begin();
//            //service.getDAO().getConnection().setAutoCommit(false);
//            //this.log("After Begin - COMMIT FLAG: " + service.getDAO().getConnection().getAutoCommit());
//        }
//        catch (TransactionException ce) {
//            // Unable to create an annotation.
//            addError("error.transaction", ce.getNestedMessage());
//        }
//        catch (SQLException sqlex) {
//            log("SQL Error: " + sqlex.getMessage());
//        }
//    }

    /**
     * Wrapper to roll back a transaction via OJB layer. All the errors are saved
     * in internal error container.
     */
//    public void rollback() {
//        // Handler to the IntactService.
//        IntactService service = getIntactService();
//
//        try {
//            DAO dao = service.getDAO();
//            // Only roll back if we had started the transaction.
//            if (dao.isActive()) {
//                dao.rollback();
//            }
//        }
//        catch (DataSourceException dse) {
//            // Can't acquire the DAO instance to create.
//            addError("error.dao", dse.getNestedMessage());
//        }
//        catch (TransactionException ce) {
//            // Unable to create an annotation.
//            addError("error.transaction", ce.getNestedMessage());
//        }
//    }

    /**
     * Clear error container.
     */
    protected void clearErrors() {
        if (!myErrors.empty()) {
            myErrors.clear();
        }
    }

    /**
     * Adds an error with given key.
     *
     * @param key the error key. This value is looked up in the
     * IntactResources.properties bundle.
     */
    protected void addError(String key) {
        myErrors.add(INTACT_ERROR, new ActionError(key));
    }

    /**
     * Adds an error with given key and value.
     *
     * @param key the error key. This value is looked up in the
     * IntactResources.properties bundle.
     * @param value the value to substitute for the first place holder in the
     * IntactResources.properties bundle.
     */
    protected void addError(String key, String value) {
        myErrors.add(INTACT_ERROR, new ActionError(key, value));
    }

    /**
     * Return <code>true</code> if error container is not empty.
     */
//    protected boolean hasErrors() {
//        return !myErrors.empty();
//    }

    /**
     * Saves the errors in given request for <struts:errors> tag.
     *
     * @param request the request to save errors.
     */
    protected void saveErrors(HttpServletRequest request) {
        super.saveErrors(request, myErrors);
    }

    /**
     * Returns the course of action depending on the value in
     * <code>WebIntactConstants.SINGLE_MATCH</code>. The default action
     * is to forward to search page.
     *
     * @param request the HTTP request to access the
     * <code>WebIntactConstants.SINGLE_MATCH</code>.
     *
     * <pre>
     * post: return = WebIntactConstants.SINGLE_MATCH or
     *                WebIntactConstants.FORWARD_RESULTS
     * </pre>
     */
    protected String fwdResultsOrSearch(HttpServletRequest request) {
        // The default is to search again.
        String retval = WebIntactConstants.FORWARD_SEARCH;

        // If we have multiple search results then we go to the results page
        // otherwise go to search page.
        Boolean singleCase = (Boolean) getSessionObject(request,
            WebIntactConstants.SINGLE_MATCH);

        // Ensure that we have this attribute or else NullPointerException.
        if ((singleCase != null) && !singleCase.booleanValue()) {
            // Multiple objects; go to results page.
            retval = WebIntactConstants.FORWARD_RESULTS;
        }
        return retval;
    }
}
