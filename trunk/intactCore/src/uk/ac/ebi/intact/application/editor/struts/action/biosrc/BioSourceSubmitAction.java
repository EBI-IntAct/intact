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
import uk.ac.ebi.intact.application.editor.struts.action.SubmitFormAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles when the user enters a value for tax id. The short label and the full
 * name of the BioSource object is replaced with the values retrieved from the
 * Newt server using the tax id.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class BioSourceSubmitAction extends SubmitFormAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Update the bean with form values.
        getIntactUser(request).getView().updateFromForm((DynaActionForm) form);
        return super.execute(mapping, form, request, response);
    }

    // Override the super method to handle the event for pressing the tax id.
    protected ActionForward handleDispatch(ActionMapping mapping,
                                           ActionForm form,
                                           HttpServletRequest request,
                                           String dispatch)
            throws Exception {
        return mapping.findForward("taxid");
    }
}
