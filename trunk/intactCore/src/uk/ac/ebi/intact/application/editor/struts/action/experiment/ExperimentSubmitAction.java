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
import uk.ac.ebi.intact.application.editor.struts.action.SubmitFormAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An action to handle Experiment specific events.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentSubmitAction extends SubmitFormAction {

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
    protected ActionForward handle(ActionMapping mapping,
                                   ActionForm form,
                                   HttpServletRequest request)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        // List of buttons to check which interaction was selected.
        String[] intCmds = (String[]) dynaform.get("intCmd");
        int intIdx = getCurrentSelection(intCmds);

        System.out.println("Here: 1" + intIdx);
        if (intIdx != -1 ) {
            // Selected an interaction to delete; set the index.
            dynaform.set("idx", new Integer(intIdx));
            return mapping.findForward("intDel");
        }
        // List of buttons to check which interaction was added/hidden.
        String[] intholdCmds = (String[]) dynaform.get("intsholdCmd");
        int intholdIdx = getCurrentSelection(intholdCmds);
        System.out.println("Here: 2" + intholdIdx);

        if (intholdIdx != -1 ) {
            // Selected an interaction to add/hide.
            dynaform.set("idx", new Integer(intholdIdx));
            return mapping.findForward("intHold");
        }
        return mapping.findForward(FAILURE);
    }

    // Handle events with dispatch parameter.
    protected ActionForward handleDispatch(ActionMapping mapping,
                                           ActionForm form,
                                           HttpServletRequest request,
                                           String dispatch)
            throws Exception {
        // Message resources to access button labels.
        MessageResources msgres = getResources(request);

        if (dispatch.equals(msgres.getMessage("exp.int.button.recent"))) {
            return mapping.findForward("intSearch");
        }
        if (dispatch.equals(msgres.getMessage("exp.int.button.search"))) {
            return mapping.findForward("intSearch");
        }
        return null;
    }
}
