/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.*;
import org.apache.struts.util.MessageResources;
import uk.ac.ebi.intact.application.editor.struts.action.FillFormAction;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The action class to handle events related to link/unlink of a feature from
 * the Interaction editor.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureLinkAction extends FillFormAction {

    /**
     * This method handles events related to link/unlink of a feature.
     *
     * @param mapping  - The <code>ActionMapping</code> used to select this instance
     * @param form     - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request  - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     * @return - represents a destination to which the action servlet,
     *         <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     *         or HttpServletResponse.sendRedirect() to, as a result of processing
     *         activities of an <code>Action</code> class
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The form.
        InteractionActionForm intform = (InteractionActionForm) form;

        // The dispatch command
        String cmd = intform.getDispatch();

        // Message resources to access button labels.
        MessageResources msgres = getResources(request);

        // The current view to link/unlink features.
        InteractionViewBean view =
                (InteractionViewBean) getIntactUser(request).getView();

        if (cmd.equals(msgres.getMessage("int.proteins.button.feature.link"))) {
            link(intform, request, view);
        }
        else {
            // default is unlink.
            unlink(intform, request, view);
        }
        // Set Any anchors.
        setAnchor(request, intform);

        // Reset the dispatch action.
        intform.resetDispatch();

        // Back to the edit screen.
        return mapping.getInputForward();
    }

    private void link(InteractionActionForm form,
                      HttpServletRequest request,
                      InteractionViewBean view)
            throws Exception {
        // The two Features to link.
        FeatureBean[] fbs = form.getFeaturesForLink();

        // Check if they already have bound domains.
        if ((fbs[0].getBoundDomain().length() != 0)
                || (fbs[1].getBoundDomain().length() != 0)) {
            ActionErrors errors = new ActionErrors();
            errors.add("feature.link",
                    new ActionError("error.int.feature.link.error"));
            saveErrors(request, errors);
            return;
        }
        // The feature to link.
        view.addFeatureLink(fbs[0], fbs[1]);
    }

    private void unlink(InteractionActionForm form,
                       HttpServletRequest request,
                       InteractionViewBean view)
            throws Exception {
        // The Feature to unlink.
        FeatureBean fb = form.getFeatureForUnlink();

        // Check if they already have bound domains.
        if (fb.getBoundDomain().length() == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add("feature.link",
                    new ActionError("error.int.feature.unlink.error"));
            saveErrors(request, errors);
            return;
        }
        // This feature is linked.
        view.addFeatureToUnlink(fb);
    }
}