/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.security;

import java.io.IOException;

import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.cvedit.exception.InvalidLoginException;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserImpl;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.model.Constants;
import uk.ac.ebi.intact.business.IntactException;

import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.*;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;

/**
 * Implements the logic to authenticate a user for the cvedit application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class LoginAction extends IntactBaseAction {

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
        // Clear any previous errors.
        super.clearErrors();

        // Get the user's login name and password. They should have already
        // validated by the ActionForm.
        LoginForm theForm = (LoginForm) form;
        String username = theForm.getUsername();
        String password = theForm.getPassword();

        // Save the context to avoid repeat calls.
        ServletContext ctx = super.getServlet().getServletContext();

        // Name of the mapping file and data source.
        String repfile = ctx.getInitParameter(Constants.MAPPING_FILE_KEY);
        String ds = ctx.getInitParameter(WebIntactConstants.DATA_SOURCE);

        // Create an instance of IntactService.
        IntactUserIF user = null;

        // Create a new session if it hasn't created before.
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // Create a new session.
        session = request.getSession(true);
//        if (session.isNew()) {
//            // New session.
//            Date createTime = new Date(session.getCreationTime());
//            super.log("Starting a new session; created at: " + createTime);
//        }
//        else {
//            // Using an existing session.
//            Date startTime = new Date(session.getCreationTime());
//            Date endTime = new Date(session.getLastAccessedTime());
//            super.log("Using a session created on time: " + startTime +
//                " and last accessed at " + endTime);
//            super.log("Starting a new session (invalidating the previous one)");
//            session.invalidate();
//            //user = super.getIntactUser(session);
//        }
        // user could be null if the user was logged off.
//        if (user == null) {
            super.log("Creating a new user");
            try {
                user = new IntactUserImpl(repfile, ds, username, password);
            }
            catch (DataSourceException de) {
                // Unable to get a data source...can't proceed
                super.log(ExceptionUtils.getStackTrace(de));
                // The errors to report back.
                super.addError("error.invalid.user", de.getMessage());
                super.saveErrors(request);
                return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
            }
            catch (IntactException ie) {
                // Unable to access the intact helper.
                super.log(ExceptionUtils.getStackTrace(ie));
                // The errors to report back.
                super.addError("error.helper", ie.getMessage());
                super.saveErrors(request);
                return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
            }
            catch (InvalidLoginException ile) {
                super.log("User " + user + " with " + password +
                    " is rejected by the user as an invalid user");
                // The errors to report back.
                super.addError("error.invalid.user", "Unauthorised user");
                super.saveErrors(request);
                return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
            }
            catch (SearchException se) {
                // Unable to construct lists such as topics, db names etc.
                super.log(ExceptionUtils.getStackTrace(se));
                // The errors to report back.
                super.addError("error.search", se.getMessage());
                super.saveErrors(request);
                return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
            }
//        }
//        else {
//            super.log("Previous sessions stats");
//            super.log("Logged in at: " + user.loginTime() + " logged off at: " +
//                user.logoffTime());
//            //super.log("Was working on: " + user.getSelectedTopic());
//        }
        // Save the info in the user session object for us to retrieve later.
        super.log("user selected " + theForm.getTopic());

//        super.log("Existing session timeout is: " + session.getMaxInactiveInterval());
//        session.setMaxInactiveInterval(90);
//        super.log("New session timeout is: " + session.getMaxInactiveInterval());

        // Need to access the user later.
        session.setAttribute(WebIntactConstants.INTACT_USER, user);
        // Have a valid user. set the topic selected.
        user.setSelectedTopic(theForm.getTopic());

        return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
    }
}
