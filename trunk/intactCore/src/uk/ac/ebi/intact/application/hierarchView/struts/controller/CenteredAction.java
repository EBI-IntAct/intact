/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.controller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUser;
import uk.ac.ebi.intact.application.hierarchView.business.graph.GraphHelper;
import uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork;
import uk.ac.ebi.intact.application.hierarchView.business.image.GraphToSVG;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean;
import uk.ac.ebi.intact.application.hierarchView.struts.StrutsConstants;
import uk.ac.ebi.intact.application.hierarchView.struts.framework.IntactBaseAction;

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
            throws IOException, ServletException {

        // Clear any previous errors.
        super.clearErrors();

        // get the current session
        HttpSession session = super.getSession(request);

        String AC = null;

        // look in the request ...
        AC = request.getParameter ("AC");

        if ((null == AC) || (AC.length() < 1)) {
            addError("error.centeredAC.required");
        }

        // Report any errors we have discovered back to the original form
        if (false == super.isErrorsEmpty()) {
            super.saveErrors(request);
            return (new ActionForward(mapping.getInput()));
        }

        String currentAC = (String)  session.getAttribute (StrutsConstants.ATTRIBUTE_AC);

        if (!AC.equals(currentAC)) {

            // Creation of the graph and the image
            InteractionNetwork in = null;

            int depthInt = 0;
            String depth = (String) session.getAttribute (StrutsConstants.ATTRIBUTE_DEPTH);

            try {
                // retreive datasource fron the session
                IntactUser user = (IntactUser) session.getAttribute (uk.ac.ebi.intact.application.hierarchView.business.Constants.USER_KEY);
                if (null == user) {
                    addError ("error.datasource.notCreated");
                    super.saveErrors(request);
                    return (mapping.findForward("error"));
                }
                GraphHelper gh = new GraphHelper ( user.getIntactHelper() );

                depthInt = Integer.parseInt (depth);
                in = gh.getInteractionNetwork (AC, depthInt);
            } catch (Exception e) {
                addError ("error.interactionNetwork.notCreated");
                super.saveErrors(request);
                return (mapping.findForward("error"));
            }

            if (null == in) {
                // warn the user that an error occur
                addError ("error.interactionNetwork.notCreated");
            } else {
                if (0 == in.sizeNodes()) {
                    addError ("error.interactionNetwork.noProteinFound");
                }
            }

            if (false == super.isErrorsEmpty()) {
                // Report any errors we have discovered back to the original form
                super.saveErrors(request);
                return (mapping.findForward("error"));
            }

            String dataTlp = in.exportTlp();
            in.importDataToImage(dataTlp);

            GraphToSVG te = new GraphToSVG(in);
            te.draw();
            ImageBean ib  = te.getImageBean();

            if (null == ib) {
                addError("error.ImageBean.build");
                super.saveErrors(request);
                return (mapping.findForward("error"));
            }

            // Save our data in the session
            session.setAttribute(StrutsConstants.ATTRIBUTE_AC, AC);

            // store the bean
            session.setAttribute (StrutsConstants.ATTRIBUTE_IMAGE_BEAN, ib);
            // store the graph
            session.setAttribute (StrutsConstants.ATTRIBUTE_GRAPH, in);
        }

        // Print debug in the log file
        super.log("CenteredAction: AC=" + AC +
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
