/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.EditForm;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Populates interaction form for display.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SetUpInteractionAction  extends AbstractEditorAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The view of the current object we are editing at the moment.
        InteractionViewBean view = (InteractionViewBean) user.getView();

        // Poplulate with experiment data.
        DynaActionForm dynaForm = (DynaActionForm) form;
        dynaForm.set("kD", view.getKD());
        dynaForm.set("organism", view.getOrganism());
        dynaForm.set("interactionType", view.getInteractionType());

        // The session to retrieve forms.
        HttpSession session = getSession(request);

        // The experiment form.
        String expFormName = EditorConstants.FORM_INTERACTION_EXP;
        EditForm expForm = user.getEditForm(expFormName);
        expForm.setItems(view.getExperiments());
        request.setAttribute(expFormName, expForm);

        // The experiment hold form.
        String expHoldFormName = EditorConstants.FORM_INTERACTION_EXP_HOLD;
        EditForm expHoldForm = user.getEditForm(expHoldFormName);
        expHoldForm.setItems(view.getHoldExperiments());
        request.setAttribute(expHoldFormName, expHoldForm);

        // The proteins edit form.
        EditForm protForm =
                getEditForm(session, EditorConstants.FORM_INTERACTION_PROT);
        // Populate with proteins.
        protForm.setItems(view.getProteins());

        return mapping.findForward(EditorConstants.FORWARD_EDITOR);
    }
}
