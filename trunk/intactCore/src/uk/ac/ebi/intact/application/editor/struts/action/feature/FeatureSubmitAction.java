/*
 Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
 All rights reserved. Please see the file LICENSE
 in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.editor.struts.action.feature;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.action.CommonDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Interaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This action class extends from common dispatch action class to override
 * submit action (Submit button) for Feature editor. Other submit actions such
 * as Save & Continue are not affected.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureSubmitAction extends CommonDispatchAction {

    /**
     * Overrides the super's submit action to handle Feature editor specific
     * behaviour.
     *
     * @param mapping the <code>ActionMapping</code> used to select this
     * instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in updating the CV object; search
     *         mapping if the update is successful and the previous search has only one
     *         result; results mapping if the update is successful and the previous
     *         search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward submit(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Submit the form. Analyze the forward path.
        ActionForward forward = submitForm(mapping, form, request, true);

        // Return the forward for any non success.
        if (!forward.equals(mapping.findForward(SUCCESS))) {
            return forward;
        }
        // Redirection is only required if the submit form is successful
        // Handler to the user.
        EditUserI user = getIntactUser(request);

        // The current view.
        FeatureViewBean view = ((FeatureViewBean) user.getView());

        // The feature we just submitted.
        Feature feature = (Feature) view.getAnnotatedObject();

        // Set the topic.
        user.setSelectedTopic(getService().getTopic(Interaction.class));

        // The parent view of the current view.
        InteractionViewBean intView = view.getParentView();

        // The interaction we are going back to.
        user.setView(intView);

        // Update the feature in the inteaction view.
        intView.saveFeature(feature);

        // Return to the interaction editor.
        return mapping.findForward(INT);
    }
}