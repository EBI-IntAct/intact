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
import uk.ac.ebi.intact.application.editor.struts.action.DeleteFormAction;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Experiment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This action handles the event when the user clicks on the Delete button to
 * delete the current edit record.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 *
 * @struts.action
 *      path="/int/delete"
 *      validate="false"
 *
 * @struts.action-forward
 *      name="experiment"
 *      path="/do/exp/fill/form"
 */
public class InteractionDeleteAction extends DeleteFormAction {

    /**
     * Action for cancelling changes to the current edit.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in cancelling the CV object; search
     * mapping if the cancel is successful and the previous search has only one
     * result; results mapping if the cancel is successful and the previous
     * search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward execute(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // Delete the interaction first.
        ActionForward forward = super.execute(mapping, form, request, response);

        // Return the forward for any failures.
        if (forward.getPath().equals(mapping.findForward(FAILURE).getPath())) {
            return forward;
        }
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The current view of the edit session.
        InteractionViewBean view = (InteractionViewBean) user.getView();

        // No further processing if not returning back to exp.
        if (!view.isSourceFromAnExperiment()) {
            return forward;
        }
        // The AC of the experiment.
        String ac = view.getSourceExperimentAc();

        // The intact helper to access the persistent layer.
        IntactHelper helper = user.getIntactHelper();
        try {
            // Remove it from the cache first.
            helper.removeFromCache(helper.getObjectByAc(Experiment.class, ac));
            // Sets the destination experiment to return to.
            setDestinationExperiment(request, helper);
            // Back to the experiment editor.
            forward = mapping.findForward(EXP);
        }
        finally {
            // Close the helper.
            helper.closeStore();
        }
        return forward;
    }
}
