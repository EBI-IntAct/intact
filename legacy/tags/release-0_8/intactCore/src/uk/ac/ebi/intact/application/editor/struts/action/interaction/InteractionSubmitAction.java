/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.util.MessageResources;
import uk.ac.ebi.intact.application.editor.struts.action.SubmitFormAction;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An action to handle Interaction specific events.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionSubmitAction extends SubmitFormAction {

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

        // List of buttons to check which experiment was selected.
        String[] expCmds = (String[]) dynaform.get("expCmd");
        int expIdx = getCurrentSelection(expCmds);

        if (expIdx != -1 ) {
            // Selected an experiment to delete; set the index.
            dynaform.set("idx", new Integer(expIdx));
            return mapping.findForward("expDel");
        }
        // List of buttons to check which experiment was added/hidden.
        String[] expholdCmds = (String[]) dynaform.get("expsholdCmd");
        int expholdIdx = getCurrentSelection(expholdCmds);

        if (expholdIdx != -1 ) {
            // Selected an experiment to add/hide.
            dynaform.set("idx", new Integer(expholdIdx));
            return mapping.findForward("expHold");
        }
        // List of buttons to check which proten was edited/saved/deleted.
        String[] protCmds = (String[]) dynaform.get("protCmd");
        int protIdx = getCurrentSelection(protCmds);

        if (protIdx != -1 ) {
            // Selected a Protein.
            dynaform.set("idx", new Integer(protIdx));
            return mapping.findForward("prot");
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

        if (dispatch.equals(msgres.getMessage("int.exp.button.recent"))) {
            return mapping.findForward("expSearch");
        }
        if (dispatch.equals(msgres.getMessage("int.exp.button.search"))) {
            return mapping.findForward("expSearch");
        }
        if (dispatch.equals(msgres.getMessage("int.proteins.button.search"))) {
            return mapping.findForward("protSearch");
        }
        return null;
    }
}
