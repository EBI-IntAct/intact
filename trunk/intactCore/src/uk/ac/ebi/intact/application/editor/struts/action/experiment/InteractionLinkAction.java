/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.experiment;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.action.CommonDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.util.LockManager;
import uk.ac.ebi.intact.model.Interaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Links to an interaction button from an experiment.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionLinkAction extends CommonDispatchAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Save the experiment first.
        ActionForward forward = save(mapping, form, request, response);

        // Return the forward for any non success.
        if (!forward.equals(mapping.findForward(SUCCESS))) {
            return forward;
        }
        // No errors. Linking starts from here.

        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // Cache the this experiment's AC because we need to set it later.
        String expAc = user.getView().getAc();

        // The AC of the interaction we are about to edit.
        String intAc = (String) ((ExperimentActionForm) form).getIntac();

        // The interaction we are about to edit.
        Interaction inter = (Interaction) user.getObjectByAc(
                Interaction.class, intAc);

        // We must have this Interaction.
        assert inter != null;

        // Try to acuire the lock.
        ActionErrors errors = acquire(intAc, user.getUserName());
        if (errors != null) {
            saveErrors(request, errors);
            return mapping.findForward(FAILURE);
        }
        // Try to acuire the lock.
        // Set the topic.
        user.setSelectedTopic(getService().getTopic(Interaction.class));

        // Set the interaction as the new view.
        user.setView(inter);

        // Set the experiment AC, so we can come back to this experiment again.
        ((InteractionViewBean) user.getView()).setSourceExperimentAc(expAc);

        // Update the form.
        return mapping.findForward(SUCCESS);
    }
}