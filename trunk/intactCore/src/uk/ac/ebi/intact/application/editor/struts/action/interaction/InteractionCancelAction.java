/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.action.CancelFormAction;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This action handles the event when the user cancels an Interaction.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionCancelAction extends CancelFormAction {

    /**
     * Action for cancelling changes to the current Interaction.
     *
     * @param mapping  the <code>ActionMapping</code> used to select this instance
     * @param form     the optional <code>ActionForm</code> bean for this request
     *                 (if any).
     * @param request  the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in cancelling the CV object; search
     *         mapping if the cancel is successful and the previous search has only one
     *         result; results mapping if the cancel is successful and the previous
     *         search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        EditUserI user = getIntactUser(request);

        // The current view of the edit session.
        InteractionViewBean view = (InteractionViewBean) user.getView();

        // Delete any features that have been added.
        view.delFeaturesAdded(user);

        // Check and see if we have to go to the experiment page.
        if (view.isSourceFromAnExperiment()) {
            // Release the lock before going back to the experiment.
            getLockManager().release(view.getAc());
            // Set the experiment to go back.
            setDestinationExperiment(request);
            // Back to the experiment editor.
            return mapping.findForward(EXP);
        }
        // As a result of accessing an Interaction through the search.
        return super.execute(mapping, form, request, response);
    }
}
