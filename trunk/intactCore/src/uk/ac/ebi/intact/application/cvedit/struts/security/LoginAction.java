/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.security;

import java.io.IOException;

import uk.ac.ebi.intact.application.cvedit.struts.framework.CvAbstractAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants;
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
public class LoginAction extends CvAbstractAction {

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
        DynaActionForm theForm = (DynaActionForm) form;
        String username = (String) theForm.get("username");
        String password = (String) theForm.get("password");

        // Validate the form parameters as we are using dynamic form.
        if (username == null || username.length() < 1) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                new ActionError("global.required", "username"));
            super.saveErrors(request, errors);
            return mapping.findForward(CvEditConstants.FORWARD_FAILURE);
        }

        // Save the context to avoid repeat calls.
        ServletContext ctx = super.getServlet().getServletContext();

        // Name of the mapping file and data source.
        String repfile = ctx.getInitParameter(Constants.MAPPING_FILE_KEY);
        String ds = ctx.getInitParameter(CvEditConstants.DATA_SOURCE);

        // Create an instance of IntactService.
        IntactUserIF user = null;

        // Invalidate any previous sessions.
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // Create a new session.
        session = request.getSession(true);
        super.log("Created a new session");
        try {
            user = new IntactUserImpl(repfile, ds, username, password);
        }
        catch (DataSourceException de) {
            // Unable to get a data source...can't proceed
            super.log(ExceptionUtils.getStackTrace(de));
            // The errors to report back.
            super.addError("error.datasource", de.getMessage());
            super.saveErrors(request);
            return mapping.findForward(CvEditConstants.FORWARD_FAILURE);
        }
        catch (IntactException ie) {
            // Unable to access the intact helper.
            super.log(ExceptionUtils.getStackTrace(ie));
            // The errors to report back.
            super.addError("error.invalid.user");
            super.saveErrors(request);
            return mapping.findForward(CvEditConstants.FORWARD_FAILURE);
        }
        catch (SearchException se) {
            // Unable to construct lists such as topics, db names etc.
            super.log(ExceptionUtils.getStackTrace(se));
            // The errors to report back.
            super.addError("error.invalid.user");
//            super.addError("error.search", se.getMessage());
            super.saveErrors(request);
            return mapping.findForward(CvEditConstants.FORWARD_FAILURE);
        }
//        super.log("Existing session timeout is: " + session.getMaxInactiveInterval());
//        session.setMaxInactiveInterval(90);
//        super.log("New session timeout is: " + session.getMaxInactiveInterval());

        // Need to access the user later.
        session.setAttribute(CvEditConstants.INTACT_USER, user);

        return mapping.findForward(CvEditConstants.FORWARD_SUCCESS);
    }
}
