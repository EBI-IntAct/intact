/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.*;
import org.apache.struts.util.MessageResources;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ExperimentBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Invoked when either Add/Hide button is pressed in the form for holding
 * experiments yet to add to the Interaction.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentHoldAction extends AbstractEditorAction {

    /**
     * Process the specified HTTP request, and create the corresponding
     * HTTP response (or forward to another web component that will create
     * it). Return an ActionForward instance describing where and how
     * control should be forwarded, or null if the response has
     * already been completed.
     *
     * @param mapping - The <code>ActionMapping</code> used to select this instance
     * @param form - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     *
     * @return - represents a destination to which the action servlet,
     * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     * or HttpServletResponse.sendRedirect() to, as a result of processing
     * activities of an <code>Action</code> class
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        // The index position of the annotation.
        int idx = ((Integer) dynaform.get("idx")).intValue();

        // The bean associated with the current action.
        ExperimentBean eb = ((ExperimentBean[]) dynaform.get("expshold"))[idx];

        // We must have the experiment bean.
        assert eb != null;

        // The current view of the edit session.
        InteractionViewBean view =
                (InteractionViewBean) getIntactUser(request).getView();

        // Message resources to access button labels.
        MessageResources msgres = getResources(request);

        // The command associated with the index.
        String cmd = ((String[]) dynaform.get("expsholdCmd"))[idx];

        if (cmd.equals(msgres.getMessage("int.exp.button.add"))) {
            // Avoid duplicates.
            if (view.experimentExists(eb)) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR,
                        new ActionError("error.int.exp.hold.add", eb.getShortLabel()));
                saveErrors(request, errors);
                return mapping.getInputForward();
            }
            else {
                // Wants to add the selected experiment to the Interaction.
                view.addExperiment(eb);
                // Clear all the experiments in the hold section.
                view.clearExperimentToHold();
            }
        }
        else {
            // Must have pressed 'Hide'.
            view.hideExperimentToHold(eb);
        }
        return mapping.findForward(SUCCESS);
    }
}