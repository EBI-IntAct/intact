/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.experiment;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.util.MessageResources;
import uk.ac.ebi.intact.application.editor.struts.action.SubmitDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.InteractionBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Invoked when Delete/Edit Interaction button is pressed to delete/edit
 * an Interaction.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionAction extends SubmitDispatchAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        // The index position of the annotation.
        int idx = ((Integer) dynaform.get("idx")).intValue();

        // The protein we are editing at the moment.
        InteractionBean ib = ((InteractionBean[]) dynaform.get("ints"))[idx];

        // We must have this Interaction bean.
        assert ib != null;

        // The command associated with the index.
        String cmd = ((String[]) dynaform.get("intCmd"))[idx];

        // Message resources to access button labels.
        MessageResources msgres = getResources(request);

        // Check for edit Interaction.
        if (cmd.equals(msgres.getMessage("exp.int.button.edit"))) {
            // Pass the control to another action to edit an interaction.
            return mapping.findForward("editInt");
        }
        // default is to delete an interaction from the experiment.

        // The current view of the edit session.
        ExperimentViewBean view =
                (ExperimentViewBean) getIntactUser(request).getView();

        // Delete the selected interaction.
        view.delInteraction(ib);

        // Back to the input page.
        return mapping.getInputForward();
    }
}