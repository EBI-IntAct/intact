/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.security;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.business.IntactException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * The class is called by struts framework when the user logs off from editor
 * application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class LogoutAction extends AbstractEditorAction {

    /**
     * Process the specified HTTP request, and create the corresponding
     * HTTP response (or forward to another web component that will create
     * it). Return an ActionForward instance describing where and how
     * control should be forwarded, or null if the response has
     * already been completed.
     *
     * @param mapping - The <code>ActionMapping</code> used to select this instance
     * @param form - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     *
     * @return - represents a destination to which the action servlet,
     * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     * or HttpServletResponse.sendRedirect() to, as a result of processing
     * activities of an <code>Action</code> class
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        // Retrieve the user object from the session.
        EditUserI user = super.getIntactUser(session);

        // Remove the user from the session.
        session.removeAttribute(EditorConstants.INTACT_USER);

        if (user != null) {
            LOGGER.info("User " + user.getUserName() + " logged off at " +
                    user.logoffTime());
            // Close the connection to the persistent storage.
            try {
                user.logoff();
            }
            catch (IntactException ie) {
                // Problems with logging off. Just log the errors as there
                // is little point in informing the user.
                LOGGER.info(ie);
            }
        }
        // Remove any locks held by the user.
//        user.releaseLock();

        // Session is no longer valid.
        session.invalidate();

        return mapping.findForward(SUCCESS);
    }
}
