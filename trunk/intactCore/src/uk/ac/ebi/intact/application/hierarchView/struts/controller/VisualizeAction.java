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
import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.business.graph.GraphHelper;
import uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork;
import uk.ac.ebi.intact.application.hierarchView.business.image.GraphToSVG;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageBean;
import uk.ac.ebi.intact.application.hierarchView.struts.StrutsConstants;
import uk.ac.ebi.intact.application.hierarchView.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.hierarchView.struts.view.VisualizeForm;
import uk.ac.ebi.intact.business.IntactHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Properties;

/**
 * Implementation of <strong>Action</strong> that validates a visualize submisson.
 *
 * @author Samuel Kerrien
 * @version $Id$
 */
public final class VisualizeAction extends IntactBaseAction {

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

        String AC    = null;
        String depth = null;
        boolean hasNoDepthLimit = false; // the default value.
        String methodLabel = null;
        String methodClass = null;
        String behaviourDefault = null;

        if (null != form) {
            // read form values from the bean
            AC              = ((VisualizeForm) form).getAC ();
            depth           = ((VisualizeForm) form).getDepth ();
            hasNoDepthLimit = ((VisualizeForm) form).getHasNoDepthLimit ();
            methodLabel     = ((VisualizeForm) form).getMethod ();

            // read the ApplicationResource.proterties file
            Properties properties = PropertyLoader.load (StrutsConstants.PROPERTY_FILE_HIGHLIGHTING);

            if (null != properties) {
                methodClass = properties.getProperty ("highlightment.source." + methodLabel + ".class");
                behaviourDefault = properties.getProperty ("highlighting.behaviour.default.class");
            }
        }

        // creating the bean to allow a good init of the form (checkbox)
        form = new VisualizeForm();
        VisualizeForm visualizeForm = (VisualizeForm) form;

        // populate the form
        boolean value = (new Boolean(hasNoDepthLimit)).booleanValue();
        visualizeForm.setHasNoDepthLimit (value);

        // store the bean (by taking care of the scope)
        if ("request".equals(mapping.getScope()))
            request.setAttribute(mapping.getAttribute(), form);
        else
            session.setAttribute(mapping.getAttribute(), form);

        if (false == super.isErrorsEmpty()) {
            // Report any errors we have discovered back to the original form
            super.saveErrors(request);
            return (mapping.findForward("error"));
        } else {

            if (hasNoDepthLimit == true) {
                Properties properties = PropertyLoader.load (StrutsConstants.PROPERTY_FILE);
                if (null != properties) {
                    depth = properties.getProperty ("hierarchView.view.form.default.value.nodepthlimit");
                }
            }

            // Save our data in the session
            session.setAttribute (StrutsConstants.ATTRIBUTE_AC, AC);
            session.setAttribute (StrutsConstants.ATTRIBUTE_DEPTH, depth);
            session.setAttribute (StrutsConstants.ATTRIBUTE_NO_DEPTH_LIMIT, new Boolean (hasNoDepthLimit));
            session.setAttribute (StrutsConstants.ATTRIBUTE_METHOD_LABEL, methodLabel);
            session.setAttribute (StrutsConstants.ATTRIBUTE_METHOD_CLASS, methodClass);
            session.setAttribute (StrutsConstants.ATTRIBUTE_BEHAVIOUR, behaviourDefault);


            // Creation of the graph and the image
            int depthInt = 0;
            InteractionNetwork in = null;

            // retreive user fron the session
            IntactUser user = super.getIntactUser(session);
            if (null == user) {
                super.addError ("error.datasource.notCreated");
                super.saveErrors(request);
                return (mapping.findForward("error"));
            }
            IntactHelper intactHelper = user.getIntactHelper ();

            try {
                GraphHelper gh = new GraphHelper ( intactHelper );
                depthInt = Integer.parseInt(depth);
                in = gh.getInteractionNetwork (AC, depthInt);
            } catch (Exception e) {
                super.addError ("error.interactionNetwork.notCreated");
                super.saveErrors(request);
                return (mapping.findForward("error"));
            }

            if (null == in) {
                // warn the user that an error occur
                super.addError ("error.interactionNetwork.notCreated");
            } else {
                if (0 == in.sizeNodes()) {
                    super.addError ("error.interactionNetwork.noProteinFound");
                }
            }

            if (false == super.isErrorsEmpty()) {
                // Report any errors we have discovered back to the original form
                super.saveErrors(request);
                return (mapping.findForward("error"));
            }

            // If depth desacrease we don't have to access IntAct, we have to reduce the current graph.

            String dataTlp  = in.exportTlp();
            super.log ("\n" + dataTlp + "\n\n");
            in.importDataToImage (dataTlp);

            // GraphToImage te = new GraphToImage (in);
            GraphToSVG te = new GraphToSVG (in);

            te.draw ();
            ImageBean ib = te.getImageBean ();

            if (null == ib) {
                super.addError ("error.ImageBean.build");
                super.saveErrors(request);
                return (mapping.findForward("error"));
            }

            // store the bean
            session.setAttribute (StrutsConstants.ATTRIBUTE_IMAGE_BEAN, ib);
            // store the graph
            session.setAttribute (StrutsConstants.ATTRIBUTE_GRAPH, in);
        }

        super.log(" Populating form from " + visualizeForm);

        // Print debug in the log file
        super.log("VisualizeAction: AC=" + AC +
                " depth=" + depth +
                " noDepthLimit=" + hasNoDepthLimit +
                " methodLabel=" + methodLabel +
                " methodClass=" + methodClass +
                "\nlogged on in session " + session.getId());

        // Forward control to the specified success URI
        return (mapping.findForward("success"));

    } // perform

} // VisualizeAction




