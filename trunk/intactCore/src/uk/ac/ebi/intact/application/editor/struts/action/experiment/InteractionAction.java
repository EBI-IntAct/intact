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
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.InteractionBean;
import uk.ac.ebi.intact.application.editor.struts.action.SubmitDispatchAction;

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

        // The command associated with the index.
        String cmd = ((String[]) dynaform.get("intCmd"))[idx];

        // Message resources to access button labels.
        MessageResources msgres = getResources(request);

        if (cmd.equals(msgres.getMessage("exp.int.button.edit"))) {
            return save(mapping, form, request, response);
//            return mapping.findForward("expSave");
        }
        // default is delete.
        return delete(mapping, form, request, response);
    }

    private ActionForward delete(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        // The index position of the annotation.
        int idx = ((Integer) dynaform.get("idx")).intValue();

        // The current view of the edit session.
        ExperimentViewBean view =
                (ExperimentViewBean) getIntactUser(request).getView();

        // The bean associated with the current action.
        InteractionBean ib = ((InteractionBean[]) dynaform.get("ints"))[idx];

        // We must have the bean.
        assert ib != null;

        // Wants to delete the selected interaction.
        view.delInteraction(ib);

        return mapping.getInputForward();
    }
}