/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.experiment;

import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentViewBean;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Populates experiment form for display.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SetUpExperimentAction  extends AbstractEditorAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The view of the current object we are editing at the moment.
        ExperimentViewBean view =
                (ExperimentViewBean) getIntactUser(request).getView();

        // Poplulate with experiment data.
        DynaActionForm dynaForm = (DynaActionForm) form;
        if (dynaForm.get("organism") == null) {
            dynaForm.set("organism", view.getOrganism());
        }
        if (dynaForm.get("interaction") == null) {
            dynaForm.set("interaction", view.getInteraction());
        }
        if (dynaForm.get("identification") == null) {
            dynaForm.set("identification", view.getIdentification());
        }
        return mapping.findForward(EditorConstants.FORWARD_EDITOR);
    }
}
