/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.exception.SessionExpiredException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.framework.util.ForwardConstants;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Experiment;

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
     * Returns the single instance of the Lock manager.
     * @return the lock manager stored in the application scope.
     */
//    protected LockManager getLockManager() {
//        LockManager lmr = (LockManager) getApplicationObject(
//                EditorConstants.LOCK_MGR);
//        return lmr;
//    }

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

    /**
     * Returns true if the next forward action is back to the experiment editor.
     * This is applicable to the InteractionView bean only.
     * @param request the Http request to access the user.
     * @return true if the next action path is back to the experiment editor.
     * @throws SessionExpiredException if the session is expired.
     */
    protected boolean returnToExperiment(HttpServletRequest request)
            throws SessionExpiredException {
        // The current view.
        AbstractEditViewBean view = getIntactUser(request).getView();

        // Check and see if we have to go to the experiment page (only
        // applicable for an Interaction editor.
        if (view.getClass().isAssignableFrom(InteractionViewBean.class)) {
            return ((InteractionViewBean) view).isSourceFromAnExperiment();
        }
        return false;
    }

    /**
     * Sets the destination experiment to edit. As a pre requisite
     * {@link #returnToExperiment(HttpServletRequest)} must be true.
     * @param request the Http request to access the user.
     * @throws SessionExpiredException if the session is expired.
     * @throws SearchException unable to access the experiment to go back to.
     *
     * <pre>
     * pre: returnToExperiment(request) == true
     * </pre>
     */
    protected void setDestinationExperiment(HttpServletRequest request)
            throws SessionExpiredException, SearchException {
        // Handler to the edit user.
        EditUserI user = getIntactUser(request);

        // The AC of the experiment.
        String ac = ((InteractionViewBean) user.getView()).getSourceExperimentAc();

        // The experiment we have been editing.
        AnnotatedObject annobj = (AnnotatedObject) user.getObjectByAc(
                Experiment.class, ac);

        // Set the topic.
        user.setSelectedTopic(getService().getTopic(Experiment.class));

        // The experiment we going back to.
        user.setView(annobj);

        // Update the search cache with the experiment, so the Cancel from the
        // experiment will correctly return to the result page with single
        // experiment (or else it will display the last edited Interaction).
        user.updateSearchCache(annobj);
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
