/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.biosrc;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import uk.ac.ebi.intact.application.editor.struts.action.FillCvFormAction;
import uk.ac.ebi.intact.application.editor.struts.view.biosrc.BioSourceViewBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Populates BioSource form for display.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FillBioSrcFormAction extends FillCvFormAction {

    // Override Actions's execure method.
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Fill the form with common info.
        super.execute(mapping, form, request, response);

        // The view of the current object we are editing at the moment.
        BioSourceViewBean view =
                (BioSourceViewBean) getIntactUser(request).getView();

        // Poplulate with experiment data.
        DynaActionForm dynaForm = (DynaActionForm) form;

        // Assumption: We are creating a new record if the current view has no
        // short label. Reset the previous values.
        if (view.getShortLabel() == null) {
            dynaForm.set("taxId", null);
            dynaForm.set("tissue", null);
            dynaForm.set("cellType", null);
        }
        else {
            // Preserver existing values for editing.
            dynaForm.set("taxId", view.getTaxId());
            dynaForm.set("tissue", view.getTissue());
            dynaForm.set("cellType", view.getCellType());
        }
        return mapping.findForward(SUCCESS);
    }
}
