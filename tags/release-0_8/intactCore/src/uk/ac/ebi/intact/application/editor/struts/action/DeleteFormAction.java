/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Experiment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This action handles the event when the user clicks on the Delete button to
 * delete the current edit record.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class DeleteFormAction extends AbstractEditorAction {

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
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // No need to delete if the object is not yet persisted.
        if (!user.isPersistent()) {
            // Back to the search page.
            return mapping.findForward(SEARCH);
        }

        try {
            // Begin the transaction.
            user.begin();
            // Delete the object we are editing at the moment.
            user.delete();
            // Commit all the changes.
            user.commit();
        }
        catch (IntactException ie1) {
            try {
                user.rollback();
            }
            catch (IntactException ie2) {
                // Oops! Problems with rollback; ignore this as this
                // error is reported via the main exception (ie1).
            }
            // Log the stack trace.
            LOGGER.info(ie1);
            // Error with deleting the object.
            ActionErrors errors = new ActionErrors();
            errors.add(EDITOR_ERROR, new ActionError(
                    "error.delete", ie1.getNestedMessage()));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE);
        }
        finally {
            // Release the lock.
            user.releaseLock();
        }
        // Remove this current bean from the recent lists.
        user.getView().removeFromRecentList(user);

        // Check and see if we have to go to the experiment page (only
        // applicable for an Interaction editor).
        if (returnToExperiment(request)) {
            // Sets the destination experiment to return to.
            setDestinationExperiment(request);
            // Back to the experiment editor.
            return mapping.findForward(EXP);
        }
        // Back to the search page.
        return mapping.findForward(SEARCH);
    }
}
