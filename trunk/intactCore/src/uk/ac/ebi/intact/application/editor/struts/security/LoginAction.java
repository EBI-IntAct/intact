/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.security;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import uk.ac.ebi.intact.application.editor.business.EditUser;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.event.EventListener;
import uk.ac.ebi.intact.application.editor.event.LoginEvent;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Implements the logic to authenticate a user for the editor application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class LoginAction extends AbstractEditorAction {

    /**
     * Used as a key to identify a datasource class - its value
     * is defined in the web.xml file as a servlet context parameter
     */
    private static final String theirDSKey = "datasource";

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Get the user's login name and password. They should have already
        // validated by the ActionForm.
        DynaActionForm theForm = (DynaActionForm) form;
        String username = (String) theForm.get("username");
        String password = (String) theForm.get("password");

        // Save the context to avoid repeat calls.
        ServletContext ctx = super.getServlet().getServletContext();

        // Name of the data source.
        String ds = ctx.getInitParameter(theirDSKey);

        // Create an instance of EditorService.
        EditUserI user = new EditUser(ds, username, password);

        // Invalidate any previous sessions.
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // Create a new session.
        session = request.getSession(true);

        LOGGER.info("Created a new session");

        // Set the status for the filter to let logged in users to get through.
        session.setAttribute(EditorConstants.LOGGED_IN, Boolean.TRUE);

        // Need to access the user later.
        session.setAttribute(EditorConstants.INTACT_USER, user);

        // Notify the event listener.
        EventListener listener = (EventListener) ctx.getAttribute(
                EditorConstants.EVENT_LISTENER);
        listener.notifyObservers(new LoginEvent(username));

        // Store the server path.
        ctx.setAttribute(EditorConstants.SERVER_PATH, request.getContextPath());

        String ac = (String) theForm.get("ac");
        String type = (String) theForm.get("type");

        // Accessing an editor page directly?
        if ((ac.length() != 0) && (type.length() != 0)) {
            // Set the topic for editor to load the correct page.
            return mapping.findForward("redirect");
        }
        return mapping.findForward(SUCCESS);
    }
}
