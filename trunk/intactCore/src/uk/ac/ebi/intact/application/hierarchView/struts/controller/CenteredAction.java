/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.controller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserIF;
import uk.ac.ebi.intact.application.hierarchView.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.hierarchView.exception.SessionExpiredException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * Implementation of <strong>Action</strong> that validates a centered submisson (from a link).
 *
 * @author Samuel Kerrien
 * @version $Id$
 */
public final class CenteredAction extends IntactBaseAction {

    // --------------------------------------------------------- Public Methods

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
     * @exception java.io.IOException if an input/output error occurs
     * @exception javax.servlet.ServletException if a servlet exception occurs
     */
    public ActionForward execute (ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response)
            throws IOException, ServletException, SessionExpiredException {

        // Clear any previous errors.
        super.clearErrors();

        // get the current session
        HttpSession session = super.getSession(request);

        // retreive user fron the session
        IntactUserIF user = super.getIntactUser(session);
        if (null == user) {
            super.addError ("error.datasource.notCreated");
            super.saveErrors(request);
            return (mapping.findForward("error"));
        }

        String AC = null;

        // look in the request ...
        AC = request.getParameter ("AC");

        if ((null == AC) || (AC.length() < 1)) {
            addError("error.centeredAC.required");
            super.saveErrors(request);
            return (new ActionForward(mapping.getInput()));
        }

        String currentAC = user.getAC();

        /*
         * Don't create the interaction network if it is the same,
         * data are already in the session.
         */
        if (false == AC.equals(currentAC)) {

            String depth = user.getDepth();

            // Creation of the graph and the image
            super.produceInteractionNetworkImage (AC, depth, user);

            if (false == super.isErrorsEmpty()) {
                // Report any errors we have discovered back to the original form
                super.saveErrors(request);
                return (mapping.findForward("error"));
            }

            // Save our data
            user.setAC (AC);
        }

        // Print debug in the log file
        logger.info ("CenteredAction: AC=" + AC +
                     "\nlogged on in session " + session.getId());

        // Remove the obsolete form bean
        if (mapping.getAttribute() != null) {
            if ("request".equals(mapping.getScope()))
                request.removeAttribute(mapping.getAttribute());
            else
                session.removeAttribute(mapping.getAttribute());
        }

        // Forward control to the specified success URI
        return (mapping.findForward("success"));

    }

} // CenteredAction
