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

        // Save the context to avoid repeat calls.
        ServletContext ctx = super.getServlet().getServletContext();

        // Name of the mapping file and data source.
        String repfile = ctx.getInitParameter(Constants.MAPPING_FILE_KEY);
        String ds = ctx.getInitParameter(WebIntactConstants.DATA_SOURCE);

        // Create an instance of IntactService.
        IntactUserIF user = null;
        try {
            user = new IntactUserImpl(repfile, ds, username, password);
        }
        catch (DataSourceException de) {
            // Unable to get a data source...can't proceed
            super.log(ExceptionUtils.getStackTrace(de));
            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add(super.INTACT_ERROR, new ActionError("error.invalid.user"));
            super.saveErrors(request, errors);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
        catch (IntactException ie) {
            // Unable to access the intact helper.
            super.log(ExceptionUtils.getStackTrace(ie));
            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add(super.INTACT_ERROR, new ActionError("error.helper"));
            super.saveErrors(request, errors);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
        catch (SearchException se) {
            // Unable to construct lists such as topics, db names etc.
            super.log(ExceptionUtils.getStackTrace(se));
            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add(super.INTACT_ERROR, new ActionError("error.search"));
            super.saveErrors(request, errors);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
        // Have a valid user. set the topic selected.
        user.setSelectedTopic(theForm.getTopic());
        // Save the info in the user session object for us to retrieve later.
        super.log("user selected " + theForm.getTopic());

        // Save the user. For the moment, create a new session.
        HttpSession session = request.getSession(false);
        session.setAttribute(WebIntactConstants.INTACT_USER, user);
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
