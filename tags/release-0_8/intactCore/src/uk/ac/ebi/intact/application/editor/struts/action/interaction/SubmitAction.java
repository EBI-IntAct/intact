/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SessionExpiredException;
import uk.ac.ebi.intact.application.editor.struts.action.SubmitDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Experiment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An action to handle when an Interaction is submitted. This action overrides
 * the submit method of the super class to analyze the the next cause of action
 * to take. If there are no errors, the user is returned to the experiment
 * editor only if we got to the interaction editor came from an experiment.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SubmitAction extends SubmitDispatchAction {

    // Override to provide a way to get back to the experiment editor when
    // an interaction is submitted.
    public ActionForward submit(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // Submit the form. Analyze the forward path.
        ActionForward forward = super.submit(mapping, form, request, response);

        // Handler to the user.
        EditUserI user = getIntactUser(request);

        // Update the bean with form values.
//        user.getView().updateFromForm((DynaActionForm) form);

        // Intercept before returning back to the result page.
//        System.out.println("forward: " + forward.getPath());
//        System.out.println("forward 1: " + mapping.findForward(RESULT).getPath());

        if (forward.getPath().equals(mapping.findForward(RESULT).getPath())) {
            // Check and see if we have to go to the experiment page (only
            // applicable for an Interaction editor.
            if (returnToExperiment(request)) {
                // The AC of the experiment.
                String ac = ((InteractionViewBean)
                        user.getView()).getSourceExperimentAc();
                // The experiment we have been editing.
                AnnotatedObject annobj = (AnnotatedObject) user.getObjectByAc(
                        Experiment.class, ac);
                // Set the topic.
                user.setSelectedTopic(getService().getTopic(Experiment.class));
                // The experiment we going back to.
                user.setView(annobj);

                // Update the search cache with the experiment, so the Cancel from the
                // experiment will correctly return to the result page with single
                // experiment (or else it will display the last edited Interaction).
                user.updateSearchCache(annobj);

                // Return to the experiment editor.
                return mapping.findForward(EXP);
            }
        }
        // Normal forward.
        return forward;
    }

    /**
     * Returns true if the next forward action is back to the experiment editor.
     * This is applicable to the InteractionView bean only.
     * @param request the Http request to access the user.
     * @return true if the next action path is back to the experiment editor.
     * @throws SessionExpiredException if the session is expired.
     */
    private boolean returnToExperiment(HttpServletRequest request)
            throws SessionExpiredException {
        // The current view.
        AbstractEditViewBean view = getIntactUser(request).getView();

        // Check and see if we have to go to the experiment page (only
        // applicable for an Interaction editor.
        if (view.getClass().isAssignableFrom(InteractionViewBean.class)) {
            return ((InteractionViewBean) view).isSourceFromAnExperiment();
        }
        return false;
    }
}
