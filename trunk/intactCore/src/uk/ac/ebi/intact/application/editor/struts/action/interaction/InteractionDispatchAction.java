/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SessionExpiredException;
import uk.ac.ebi.intact.application.editor.struts.action.CommonDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Experiment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An action to handle when an Interaction is submitted. This action overrides
 * the submit method of the super to analyze the next cause of action
 * to take. If there are no errors, the user is returned to the experiment
 * editor only if we got to the interaction editor from an experiment.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 *
 * @struts.action
 *      path="/int/submit"
 *      name="intForm"
 *      input="edit.layout"
 *      scope="session"
 *      parameter="dispatch"
 *
 * @struts.action-forward
 *      name="success"
 *      path="/do/display"
 *
 * @struts.action-forward
 *      name="experiment"
 *      path="/do/exp/fill/form"
 *
 * @struts.action-forward
 *      name="reload"
 *      path="/do/int/fill/form"
 */
public class InteractionDispatchAction extends CommonDispatchAction {

    // Override to provide a way to get back to the experiment editor when
    // an interaction is submitted.
    public ActionForward submit(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // Submit the form. Analyze the forward path.
        ActionForward forward = submitForm(mapping, form, request, true);

        // Return the forward if it isn't a success.
        if (!forward.equals(mapping.findForward(SUCCESS))) {
            return forward;
        }
        // Handler to the user.
        EditUserI user = getIntactUser(request);

        // The current view.
        InteractionViewBean view = (InteractionViewBean) user.getView();

        // Do we have to return to the experiment editor?
        if (returnToExperiment(request)) {
            // The AC of the experiment.
            String ac = view.getSourceExperimentAc();

            // The experiment we have been editing.
            AnnotatedObject annobj = (AnnotatedObject) user.getObjectByAc(
                    Experiment.class, ac);

            // Set the topic.
            user.setSelectedTopic(getService().getTopic(Experiment.class));

            // The experiment we going back to.
            user.setView(annobj);

            // Return to the experiment editor.
            return mapping.findForward(EXP);
        }
        // Update the search cache.
        user.updateSearchCache(view.getAnnotatedObject());

        // Add the current edited object to the recent list.
        view.addToRecentList(user);

        // Only show the submitted record.
        return mapping.findForward(RESULT);
    }

    /**
     * Returns true if the next forward action is back to the experiment editor.
     * This is applicable to the InteractionView bean only.
     *
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
