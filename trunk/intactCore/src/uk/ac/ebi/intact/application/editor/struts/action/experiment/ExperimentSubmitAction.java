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
import uk.ac.ebi.intact.application.editor.struts.framework.util.PageValueBean;

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

        // PV bean to extract values from the cmd string.
        PageValueBean pvb = new PageValueBean((String) dynaform.get("intCmd"));

        // Is to do with Edit/Delete existing ints?
        if (pvb.isMajor("interaction")) {
            return mapping.findForward("interaction");
        }
        // Assume the request is with hold interactions.
        return mapping.findForward("intHold");
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
