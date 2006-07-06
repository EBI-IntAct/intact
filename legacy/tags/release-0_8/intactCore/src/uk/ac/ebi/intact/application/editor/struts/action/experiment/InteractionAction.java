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
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentViewBean;
import uk.ac.ebi.intact.model.Interaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Invoked when Delete/Edit Interaction button is pressed to delete/edit
 * an Interaction.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionAction extends AbstractEditorAction {

    // Override the execute method to
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        // Are we editing an Interaction?
        if (dynaform.get("dispatch").equals(
                getResources(request).getMessage("exp.int.button.edit"))) {
            // Pass the control to another action to edit an interaction.
            return mapping.findForward("editInt");
        }
        // default is to delete an interaction from the experiment.

        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The interaction to delete.
        Interaction inter = (Interaction) user.getObjectByAc(
                Interaction.class, (String) dynaform.get("intac"));

        // We must have the interaction bean.
        assert inter != null;

        // The current view of the edit session.
        ExperimentViewBean view = (ExperimentViewBean) user.getView();

        // Delete the selected interaction.
        view.delInteraction(inter);

        // Back to the input page.
        return mapping.getInputForward();
    }
}