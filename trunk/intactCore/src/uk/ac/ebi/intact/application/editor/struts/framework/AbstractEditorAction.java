/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework;

import org.apache.struts.action.*;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.Globals;
import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Logger;

import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.view.EditForm;
import uk.ac.ebi.intact.application.editor.struts.view.XrefEditForm;
import uk.ac.ebi.intact.application.editor.exception.SessionExpiredException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Super class for all Editor related action classes.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractEditorAction extends Action {

    /** The global Intact error key. */
    public static final String EDITOR_ERROR = "EditError";

    /**
     * The logger for Editor. Allow access from the subclasses.
     */
    protected static final Logger LOGGER = Logger.getLogger(EditorConstants.LOGGER);

    /**
     * Returns the only instance of Intact Service instance.
     * @return only instance of the <code>EditorService</code> class.
     */
    protected EditorService getService() {
        EditorService service = (EditorService)
                getApplicationObject(EditorConstants.EDITOR_SERVICE);
        return service;
    }

    /**
     * Returns the Intact User instance saved in a session for given
     * Http request.
     *
     * @param request the Http request to access the Intact user object.
     * @return an instance of <code>EditUser</code> stored in a session.
     * No new session is created.
     * @exception SessionExpiredException for an expired session.
     *
     * <pre>
     * post: return <> Undefined
     * </pre>
     */
    protected EditUserI getIntactUser(HttpServletRequest request)
            throws SessionExpiredException {
        EditUserI user = (EditUserI)
                getSessionObject(request, EditorConstants.INTACT_USER);
        if (user == null) {
            throw new SessionExpiredException();
        }
        return user;
    }

    /**
     * Returns the Intact User instance saved in a session.
     *
     * @param session the session to access the Intact user object.
     * @return an instance of <code>EditUser</code> stored in
     * <code>session</code>
     * @exception SessionExpiredException for an expired session.
     *
     * <pre>
     * post: return <> Undefined
     * </pre>
     */
    protected EditUserI getIntactUser(HttpSession session)
            throws SessionExpiredException {
        EditUserI user = (EditUserI)
                session.getAttribute(EditorConstants.INTACT_USER);
        if (user == null) {
            throw new SessionExpiredException();
        }
        return user;
    }

    /**
     * Returns the session from given request. No new session is created.
     * @param request the request to get the session from.
     * @return session associated with given request.
     * @exception SessionExpiredException for an expired session.
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
            }
            else {
                HttpSession session = request.getSession();
                session.removeAttribute(mapping.getAttribute());
            }
        }
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
     * post: return = EditorConstants.FORWARD_SEARCH or
     *                EditorConstants.FORWARD_RESULTS
     * post: return <> Undefined
     * </pre>
     */
    protected String getForwardAction(EditUserI user) {
        return user.hasSingleSearchResult() ? EditorConstants.FORWARD_SEARCH :
                EditorConstants.FORWARD_RESULTS;
    }

    /**
     * Identicial to {@link #getForwardAction(EditUserI)} with user
     * retrieved from <code>request</code>.
     *
     * @param request the HTTP request to extract the user.
     * @exception SessionExpiredException if the session has expired.
     * @see #getForwardAction(EditUserI)
     *
     * <pre>
     * post: return = EditorConstants.FORWARD_SEARCH or
     *                EditorConstants.FORWARD_RESULTS
     * post: return <> Undefined
     * </pre>
     */
    protected String getForwardAction(HttpServletRequest request)
            throws SessionExpiredException {
        return this.getForwardAction(this.getIntactUser(request));
    }

    /**
     * Returns a new DynaBean instance for given form name.
     * @param formName the name of the form configured in the struts
     * configuration file.
     * @param request the HTTP request to get the application configuration.
     * @return a <code>DynaBean</code> instance.
     * @throws InstantiationException errors in creating the bean
     * @throws IllegalAccessException errors in creating the bean
     */
    protected DynaBean getDynaBean(HttpServletRequest request, String formName)
            throws InstantiationException, IllegalAccessException {
        ModuleConfig appConfig = (ModuleConfig) request.getAttribute(
                Globals.MODULE_KEY);
        FormBeanConfig config = appConfig.findFormBeanConfig(formName);
        DynaActionFormClass dynaClass =
                DynaActionFormClass.createDynaActionFormClass(config);
        return dynaClass.newInstance();
    }

    /**
     * Returns an edit form from a session object. The forms which require
     * editing (e.g., annotations, xref and proteins in an interaction) are
     * stored in a session.
     * @param request the Http request to retrieve the session.
     * @param formName the name of the form stored in <code>session</code>.
     * @return an <code>EditForm</code> stored in <code>session</code>. A
     * new form is created and stored in the session before returning it.
     * @exception SessionExpiredException for an expired session.
     */
    protected EditForm getEditForm(HttpServletRequest request, String formName)
            throws SessionExpiredException {
        HttpSession session = getSession(request);
        if (session.getAttribute(formName) == null) {
            // The new form.
            EditForm form = null;
            // Special form form xref editing.
            if (formName.equals(EditorConstants.FORM_XREF_EDIT)) {
                form = new XrefEditForm();
            }
            else {
                form = new EditForm();
            }
            session.setAttribute(formName, form);
            return form;
        }
        return (EditForm) session.getAttribute(formName);
    }

    /**
     * Returns true if errors in stored in the request
     * @param request Http request to search errors for.
     * @return true if strut's error is found in <code>request</code> and
     * it is not null. For all instances, false is returned.
     */
    protected boolean hasErrors(HttpServletRequest request) {
        ActionErrors errors =
                (ActionErrors) request.getAttribute(Globals.ERROR_KEY);
        if (errors != null) {
            // Empty menas no errors.
            return !errors.isEmpty();
        }
        // No errors stored in the request.
        return false;
    }

    /**
     * Returns the forward for input.
     * @param mapping the mapping to get forward action.
     * @return forward action stored in <code>mapping</code> under "input".
     */
    protected ActionForward inputForward(ActionMapping mapping) {
        return mapping.findForward("input");
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

    /**
     * Retrieve a session object based on the request and attribute name.
     *
     * @param request the HTTP request to retrieve a session object stored
     * under <tt>attrName</tt>.
     * @param attrName the name of the attribute.
     * @return the session object stored in <tt>request</tt> under
     * <tt>attrName</tt>.
     * @exception SessionExpiredException for an expired session.
     *
     * <pre>
     * post: return <> Undefined
     * </pre>
     */
    private Object getSessionObject(HttpServletRequest request,
                                    String attrName) throws SessionExpiredException {
        return getSession(request).getAttribute(attrName);
    }
}
