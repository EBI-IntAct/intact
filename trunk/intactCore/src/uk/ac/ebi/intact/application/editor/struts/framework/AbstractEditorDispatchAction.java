/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;
import org.apache.struts.actions.LookupDispatchAction;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.exception.SessionExpiredException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.framework.util.ForwardConstants;
import uk.ac.ebi.intact.application.editor.util.LockManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * The super class for all the dispatch actions.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $id$
 */
public abstract class AbstractEditorDispatchAction extends LookupDispatchAction
        implements ForwardConstants {

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
     * Returns the session from given request. No new session is created.
     * @param request the request to get the session from.
     * @return session associated with given request.
     * @exception uk.ac.ebi.intact.application.editor.exception.SessionExpiredException for an expired session.
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
     * @return an instance of <code>EditUser</code> stored in a session.
     * No new session is created.
     * @exception uk.ac.ebi.intact.application.editor.exception.SessionExpiredException for an expired session.
     *
     * <pre>
     * post: return <> Undefined
     * </pre>
     */
    protected EditUserI getIntactUser(HttpServletRequest request)
            throws SessionExpiredException {
        EditUserI user = (EditUserI)
                getSession(request).getAttribute(EditorConstants.INTACT_USER);
        if (user == null) {
            throw new SessionExpiredException();
        }
        return user;
    }

    /**
     * @return the lock manager stored in the application context.
     */
    protected LockManager getLockManager() {
        return (LockManager) getApplicationObject(EditorConstants.LOCK_MGR);
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
     * Sets the anchor in the form if an anchor exists.
     *
     * @param request the HTTP request to get anchor.
     * @param form the form to set the anchor and also to extract the dispath
     * event.
     *
     * @see EditorService#getAnchor(java.util.Map, HttpServletRequest, String)
     */
    protected void setAnchor(HttpServletRequest request, EditorActionForm form) {
        // The map containing anchors.
        Map map = (Map) getApplicationObject(EditorConstants.ANCHOR_MAP);

        // Any anchors to set?
        String anchor = getService().getAnchor(map, request, form.getDispatch());
        // Set the anchor only if it is set.
        if (anchor != null) {
            form.setAnchor(anchor);
        }
    }

    /**
     * Tries to acquire a lock for given id and owner.
     * @param ac the id or the accession number to lock.
     * @param owner the owner of the lock.
     * @return null if there are no errors in acquiring the lock or else
     * non null value is returned to indicate errors.
     */
    protected ActionErrors acquire(String ac, String owner) {
        // Try to acuire the lock.
        if (!getLockManager().acquire(ac, owner)) {
            ActionErrors errors = new ActionErrors();
            // The owner of the lock (not the current user).
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.lock", ac, getLockManager().getOwner(ac)));
            return errors;
        }
        return null;
    }

    /**
     * A convenient method to retrieve an application object from a session.
     * @param attrName the attribute name.
     * @return an application object stored in a session under <tt>attrName</tt>.
     */
    private Object getApplicationObject(String attrName) {
        return super.servlet.getServletContext().getAttribute(attrName);
    }
}
