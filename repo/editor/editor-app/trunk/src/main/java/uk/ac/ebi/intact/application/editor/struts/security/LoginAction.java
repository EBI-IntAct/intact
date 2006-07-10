/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.security;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.business.EditUser;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.event.EventListener;
import uk.ac.ebi.intact.application.editor.event.LoginEvent;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.business.IntactException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Implements the logic to authenticate a user for the editor application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 *
 * @struts.action
 *      path="/login"
 *      name="loginForm"
 *      input="login.error.layout"
 *
 * @struts.action-exception
 *      type="uk.ac.ebi.intact.application.editor.exception.AuthenticateException"
 *      key="error.invalid.user"
 *      path="login.error.layout"
 *
 * @struts.action-forward
 *      name="success"
 *      path="search.layout"
 *
 * @struts.action-forward
 *      name="redirect"
 *      path="/do/secure/edit"
 */
public class LoginAction extends AbstractEditorAction {

    private Logger loginLogger = Logger.getLogger("uk.co.intact.LoginEditor");

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

        long timeOnStartLogin = System.currentTimeMillis();

        // Get the user's login name and password. They should have already
        // validated by the ActionForm.
        LoginForm theForm = (LoginForm) form;
        String username = (String) theForm.getUsername();
        String password = (String) theForm.getPassword();

        // Validate the user.
        EditUserI user = UserAuthenticator.authenticate(username, password);

        // Must have a valid user.
        assert user != null: "User must exist!";

//        // Create an instance of EditorService.
//        EditUserI user = new EditUser(username, password);

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

        // Save the context to avoid repeat calls.
        ServletContext ctx = super.getServlet().getServletContext();

        // Notify the event listener.
        EventListener listener = (EventListener) ctx.getAttribute(
                EditorConstants.EVENT_LISTENER);
        listener.notifyObservers(new LoginEvent(username));

        // Store the server path.
        ctx.setAttribute(EditorConstants.SERVER_PATH, request.getContextPath());

        String ac = (String) theForm.getAc();
        String type = (String) theForm.getType();

        // Accessing an editor page directly?
        if (!isPropertyNullOrEmpty(ac) && !isPropertyNullOrEmpty(type)) {
            // Set the topic for editor to load the correct page.
            return mapping.findForward("redirect");
        }

        // log the time needed to login
        long loginTime = System.currentTimeMillis()-timeOnStartLogin;

        String warning = "";
        if (loginTime >= 30000)
        {
            warning = " - /!\\";
        }

        loginLogger.info("Login time: "+username+", "+loginTime+" ms, "+request.getRemoteAddr()+warning);

        return mapping.findForward(SUCCESS);
    }
}
