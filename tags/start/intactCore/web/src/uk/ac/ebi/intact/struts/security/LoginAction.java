/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.security;

import java.io.IOException;

import uk.ac.ebi.intact.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.struts.framework.IntactUserSession;
import uk.ac.ebi.intact.struts.framework.exceptions.InvalidLoginException;
import uk.ac.ebi.intact.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.struts.service.IntactService;
import uk.ac.ebi.intact.struts.service.IntactUser;

import org.apache.struts.action.*;

import javax.servlet.http.*;
import javax.servlet.ServletException;

/**
 * Implements the logic to authenticate a user for the storefront application.
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
     * @param actionForm The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward perform(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws IOException, ServletException {
        // Get the user's login name and password. They should have already
        // validated by the ActionForm.
        LoginForm theForm = (LoginForm) form;
        String username = theForm.getUsername();
        String password = theForm.getPassword();

        // Login through the security service
        IntactService serviceImpl = super.getIntactService();
        IntactUser user = null;

        try {
            user = serviceImpl.authenticate(username, password);
        }
        catch (InvalidLoginException ex) {
            super.log("invalid user " + username + " detected");
            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add("invalid.user", new ActionError("error.invalid.user"));
            super.saveErrors(request, errors);
            return (mapping.findForward(WebIntactConstants.FORWARD_FAILURE));
        }
        // Save the user session.
        //IntactUserSession userSession = super.getIntactUser(request);

        // Save the info in the user session object for us to retrieve later.
        super.log("user selected " + theForm.getTopic());
        IntactUserSession userSession = new IntactUserSession();
        userSession.setIntactUser(user);
        userSession.setSelectedTopic(theForm.getTopic());

        // Save the user. For the moment, create a new session.
        HttpSession session = request.getSession(false);
        session.setAttribute(WebIntactConstants.USER_SESSION, userSession);
//
//    UserContainer existingContainer = null;
//    HttpSession session = request.getSession(false);
//    if ( session != null ){
//      existingContainer = getUserContainer(request);
//      session.invalidate();
//    }else{
//      existingContainer = new UserContainer();
//    }

        // Create a new session for the user
//    session = request.getSession(true);
//    existingContainer.setUserView(userView);
//    session.setAttribute(IConstants.USER_CONTAINER_KEY, existingContainer);

        return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
    }
}
