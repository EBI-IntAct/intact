/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.controller;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import uk.ac.ebi.intact.application.search.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search.business.IntactUserIF;
import uk.ac.ebi.intact.application.search.business.IntactUserImpl;
import uk.ac.ebi.intact.application.search.business.IntactServiceIF;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.model.Constants;
import uk.ac.ebi.intact.business.*;

import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.*;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;

/**
 * Performs the required initialisations for a user search session when
 * they have been through the welcome page.
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class WelcomeAction extends IntactBaseAction {

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
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws IOException, ServletException {
        // Don't need to process the form as it is a dummy one, only used
        //to integrate things smoothly with struts

        // Save the context to avoid repeat calls.
        ServletContext ctx = super.getServlet().getServletContext();

        // Name of the mapping file and data source.
        String repfile = ctx.getInitParameter(Constants.MAPPING_FILE_KEY);
        String ds = ctx.getInitParameter(SearchConstants.DATA_SOURCE);

        // Create an instance of IntactService.
        IntactUserIF user = null;
        try {
            user = new IntactUserImpl(repfile, ds);
        }
        catch (DataSourceException de) {
            // Unable to get a data source...can't proceed
            super.log(ExceptionUtils.getStackTrace(de));
            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add(super.INTACT_ERROR, new ActionError("error.invalid.user"));
            super.saveErrors(request, errors);
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }
        catch (IntactException se) {
            // Unable to construct lists such as topics, db names etc.
            super.log(ExceptionUtils.getStackTrace(se));
            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add(super.INTACT_ERROR, new ActionError("error.search"));
            super.saveErrors(request, errors);
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }

        // Save the user. For the moment, create a new session.
        HttpSession session = request.getSession();
        session.setAttribute(SearchConstants.INTACT_USER, user);

        // The map to hold intact view beans; this will be filled later on.
        Map idToView = new HashMap();
        session.setAttribute(SearchConstants.FORWARD_MATCHES, idToView);

        return mapping.findForward(SearchConstants.FORWARD_SUCCESS);
    }
}
