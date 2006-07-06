/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.experiment;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.action.SubmitDispatchAction;
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
public class InteractionLinkAction extends SubmitDispatchAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        // Save the experiment first.
        ActionForward forward = save(mapping, form, request, response);

        // Check for any errors and redirect to the error page.
//        if (forward.getPath().equals(mapping.getInputForward().getPath())
        if (forward.equals(mapping.findForward(FAILURE))) {
            return forward;
        }
        // No errors. Linking starts from here.

        // Check the lock.
        LockManager lmr = LockManager.getInstance();

        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // Cache the this experiment's AC because we need to set it later.
        String expAc = user.getView().getAc();

        // The AC of the interaction we are about to edit.
        String intAc = (String) dynaform.get("intac");

        // The interaction we are about to edit.
        Interaction inter = (Interaction) user.getObjectByAc(
                Interaction.class, intAc);

        // We must have this Interaction.
        assert inter != null;

        // Try to acuire the lock.
        if (!lmr.acquire(intAc, user.getUserName())) {
            ActionErrors errors = new ActionErrors();
            // The owner of the lock (not the current user).
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.lock", intAc, lmr.getOwner(intAc)));
            saveErrors(request, errors);
            // Show the errors in the input page.
            return mapping.getInputForward();
        }
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