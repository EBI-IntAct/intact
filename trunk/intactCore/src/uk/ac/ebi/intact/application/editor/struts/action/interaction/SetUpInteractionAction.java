/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ProteinEditForm;
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
        // The view of the current object we are editing at the moment.
        InteractionViewBean view =
                (InteractionViewBean) getIntactUser(request).getView();

        // Poplulate with experiment data; first try the form first, so it
        // will save the last typed values.
        DynaActionForm dynaForm = (DynaActionForm) form;
        if (isPropertyNull(dynaForm, "kD")) {
            dynaForm.set("kD", view.getKD());
        }
        if (isPropertyNullOrEmpty(dynaForm, "organism")) {
            dynaForm.set("organism", view.getOrganism());
        }
        if (isPropertyNullOrEmpty(dynaForm, "interactionType")) {
            dynaForm.set("interactionType", view.getInteractionType());
        }
        return mapping.findForward(FORWARD_SUCCESS);
    }
}
