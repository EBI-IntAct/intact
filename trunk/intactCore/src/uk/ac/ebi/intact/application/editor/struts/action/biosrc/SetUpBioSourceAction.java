/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.biosrc;

import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.biosrc.BioSourceViewBean;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Populates BioSource form for display.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SetUpBioSourceAction  extends AbstractEditorAction {

    // Override Actions's execure method.
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The view of the current object we are editing at the moment.
        BioSourceViewBean view =
                (BioSourceViewBean) getIntactUser(request).getView();

        // Poplulate with experiment data.
        DynaActionForm dynaForm = (DynaActionForm) form;
        dynaForm.set("taxId", view.getTaxId());

        return mapping.findForward(EditorConstants.FORWARD_EDITOR);
    }
}
