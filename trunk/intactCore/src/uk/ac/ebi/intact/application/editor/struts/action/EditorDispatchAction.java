/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.exception.ValidationException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactException;

/**
 * Dispatcher action which dispatches according to 'dispatch' parameter. This
 * class handles events for submit, cancel or delete when the user presses
 * these buttons on the edit page.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditorDispatchAction extends AbstractEditorDispatchAction {

    // Implements super's abstract methods.

    /**
     * Provides the mapping from resource key to method name.
     * @return Resource key / method name map.
     */
    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("button.submit", "submit");
        map.put("button.delete", "delete");
        map.put("button.cancel", "cancel");
        return map;
    }

    /**
     * Action for submitting the edit form.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in updating the CV object; search
     * mapping if the update is successful and the previous search has only one
     * result; results mapping if the update is successful and the previous
     * search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward submit(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The current view.
        AbstractEditViewBean view = user.getView();

        // Validate the data.
        validate(user, view, request);

        // Any validation errors?
        if (hasErrors(request)) {
            return inputForward(mapping);
        }
        try {
            // Begin the transaction.
            user.begin();

            // Need to create a new record first if we are editing a new record.
//            if (user.isEditingNew()) {
//                // Create the new object on the persistence system.
//                user.create(view.getAnnotatedObject());
//            }
            // Persist my current state
            view.persist(user);

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
            // Error with updating.
            ActionErrors errors = new ActionErrors();
            errors.add(AbstractEditorAction.EDITOR_ERROR,
                    new ActionError("error.update", ie1.getNestedMessage()));
            saveErrors(request, errors);
            return mapping.findForward(EditorConstants.FORWARD_FAILURE);
        }
        finally {
            // Clear containers; regardless of the outcome.
            view.clearTransactions();
            // Not editing a new record any more.
//            user.setEditNew(false);
        }
        // Update the search cache.
        user.updateSearchCache();
        // Need to rebuild the menu again. Remove it from cache.
        view.removeMenu();
        // All changes are committed successfully; either search or results.
        return mapping.findForward(getForwardAction(user));
    }

    /**
     * Action for deleting the current edit object.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in deleting the CV object; search
     * mapping if the delete is successful and the previous search has only one
     * result; results mapping if the delete is successful and the previous
     * search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward delete(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The object to delete.
        AnnotatedObject annot = user.getView().getAnnotatedObject();

        try {
            // Begin the transaction.
            user.begin();
            // Delete the object we are editing at the moment.
            user.delete(annot);
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
            errors.add(AbstractEditorAction.EDITOR_ERROR,
                    new ActionError("error.delete", ie1.getNestedMessage()));
            saveErrors(request, errors);
            return mapping.findForward(EditorConstants.FORWARD_FAILURE);
        }
        finally {
            // Clear containers; regardless of the outcome.
            user.getView().clearTransactions();
        }
        // Remove the current edit object from the cache.
        user.removeFromSearchCache();
        // Need to rebuild the menu again. Remove it from cache.
        user.getView().removeMenu();
        // Deleted successfully; either search or results.
        return mapping.findForward(getForwardAction(user));
    }

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
    public ActionForward cancel(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // Cancel the current edit session.
        user.cancelEdit();

        // Either search or results.
        return mapping.findForward(getForwardAction(user));
    }

    // Helper methods

    /**
     * Validates the current view.
     * @param user handler to the user to access database for validation.
     * @param view the view to validate.
     * @param request the Http request to store validation errors.
     * @throws SearchException thrown by validate method of the view.
     */
    private void validate(EditUserI user, AbstractEditViewBean view,
                          HttpServletRequest request) throws SearchException {
        try {
            view.validate(user);
        }
        catch (ValidationException ex) {
            ActionErrors errors = new ActionErrors();
            errors.add(ex.getFilterKey(), new ActionError(ex.getMessageKey()));
            saveErrors(request, errors);
        }
    }
}
