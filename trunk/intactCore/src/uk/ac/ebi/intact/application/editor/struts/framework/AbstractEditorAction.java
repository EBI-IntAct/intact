/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.*;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.util.RequestUtils;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.exception.SessionExpiredException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.framework.util.ForwardConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Super class for all Editor related action classes.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractEditorAction extends Action implements ForwardConstants {

    /** The global Intact error key. */
    public static final String EDITOR_ERROR = "EditError";

    /**
     * The logger for Editor. Allow access from the subclasses.
     */
    protected static final Logger LOGGER = Logger.getLogger(EditorConstants.LOGGER);

    /**
     * The key to success action.
     */
//    protected static final String FORWARD_SUCCESS = "success";

    /**
     * The key to failure action.
     */
//    protected static final String FORWARD_FAILURE = "failure";

    /**
     * Forward to the search page.
     */
//    protected static final String FORWARD_SEARCH = "search";

    /**
     * Forward to the results page.
     */
//    protected static final String FORWARD_RESULTS = "results";

    // Class Methods

    /**
     * Returns true if given value is empty.
     * @param value the value to check for empty.
     * @return true if <code>value</code> is empty; any excess spaces
     * are removed before checking for empty.
     */
    public static boolean isPropertyEmpty(String value) {
        return value.trim().length() == 0;
    }

    // Protected methods

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
        return user.hasSingleSearchResult() ? SEARCH : RESULT;
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
//    protected DynaBean getDynaBean(HttpServletRequest request, String formName)
//            throws InstantiationException, IllegalAccessException {
//        ModuleConfig appConfig = (ModuleConfig) request.getAttribute(
//                Globals.MODULE_KEY);
//        FormBeanConfig config = appConfig.findFormBeanConfig(formName);
//        DynaActionFormClass dynaClass =
//                DynaActionFormClass.createDynaActionFormClass(config);
//        return dynaClass.newInstance();
//    }

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
//    protected ActionForward inputForward(ActionMapping mapping) {
//        return mapping.findForward("input");
//    }

    /**
     * Returns an action form.
     * @param request the Http request to search for the form; the form is
     * created and saved in given scope.
     * @param mapping the mapping to get the scope for the form.
     * @param name the name of the form.
     * @return an ActionForm instance; this will be stored under given scope.
     * @exception SessionExpiredException when a session expires.
     */
    protected ActionForm createActionForm(HttpServletRequest request,
                                          ActionMapping mapping,
                                          String name)
            throws SessionExpiredException {
        FormBeanConfig config = mapping.getModuleConfig().findFormBeanConfig(name);
        if (config == null) {
            return null;
        }
        ActionForm instance = null;

        if ("request".equals(mapping.getScope())) {
            instance = (ActionForm) request.getAttribute(name);
        }
        else {
            instance = (ActionForm) getSession(request).getAttribute(name);
        }
        // Create and return a new form bean instance
        if (instance == null) {
            if (config.getDynamic()) {
                try {
                    DynaActionFormClass dynaClass =
                            DynaActionFormClass.createDynaActionFormClass(config);
                    instance = (ActionForm) dynaClass.newInstance();
                }
                catch (Throwable t) {
                    return null;
                }
            }
            else {
                try {
                    instance = (ActionForm) RequestUtils.applicationInstance(
                            config.getType());
                }
                catch (Throwable t) {
                    return null;
                }
            }
        }
        if ("request".equals(mapping.getScope())) {
            request.setAttribute(name, instance);
        }
        else {
            getSession(request).setAttribute(name, instance);
        }
        return instance;
    }

    /**
     * Returns true if the property for given name is empty. This assumes the
     * property value for given name is a String object.
     * @param form the form to check.
     * @param name the name of the property to check.
     * @return true if <code>name</code> is empty in <code>form</code>.
     *
     * <pre>
     * pre: form.get(name).class.getName() == java.lang.String
     * </pre>
     */
    protected boolean isPropertyNullOrEmpty(DynaActionForm form, String name) {
        return isPropertyNull(form, name) || isPropertyEmpty((String) form.get(name));
    }

    /**
     * Returns true if the property for given name is null.
     * @param form the form to check.
     * @param name the name of the property to check.
     * @return true if <code>name</code> is null in <code>form</code>.
     */
    protected boolean isPropertyNull(DynaActionForm form, String name) {
        return form.get(name) == null;
    }

    /**
     * Returns true if given property is empty.
     * @param form the form to check.
     * @param name the name of the property; the value stored under this property
     * must be a String type.
     * @return true if property <code>name</code> is empty; any excess spaces
     * are removed before checking for empty.
     */
    protected boolean isPropertyEmpty(DynaActionForm form, String name) {
        return ((String) form.get(name)).trim().length() == 0;
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
