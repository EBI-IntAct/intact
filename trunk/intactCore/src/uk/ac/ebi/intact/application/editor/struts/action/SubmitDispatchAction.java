/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Dispatcher action which dispatches according to 'dispatch' parameter. This
 * class handles events for submitting the form and adding annotations/xrefs.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SubmitDispatchAction extends AbstractEditorDispatchAction {

    // Implements super's abstract methods.

    /**
     * Provides the mapping from resource key to method name.
     * @return Resource key / method name map.
     */
    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("button.submit", "submit");
        map.put("button.save.continue", "save");
        map.put("annotations.button.add", "addAnnot");
        map.put("xrefs.button.add", "addXref");
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
        return submitForm(mapping, form, request, true);
    }

    /**
     * Action for saving the edit form.
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
    public ActionForward save(ActionMapping mapping,
                              ActionForm form,
                              HttpServletRequest request,
                              HttpServletResponse response)
            throws Exception {
        ActionForward forward = submitForm(mapping, form, request, false);
        // Turn editing mode on as it was switched off upon a successfull committ.
        if (forward.getPath().equals(mapping.findForward(SUCCESS).getPath())) {
            getIntactUser(request).startEditing();
        }
        return forward;
    }

    /**
     * Action for adding a new annotation.
     *
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
    public ActionForward addAnnot(ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The bean to extract the values.
        CommentBean cb = ((EditorActionForm) form).getNewAnnotation();

        // The topic for the annotation.
        CvTopic cvtopic = (CvTopic) user.getObjectByLabel(CvTopic.class,
                cb.getTopic());
        Annotation annot = new Annotation(user.getInstitution(), cvtopic);
        annot.setAnnotationText(cb.getDescription());

        // Add the bean to the view; new bean is wrapped around the annotation.
        user.getView().addAnnotation(new CommentBean(annot));

        return mapping.getInputForward();
    }

    /**
     * Action for adding a new xref.
     *
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
    public ActionForward addXref(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Handler to the EditUserI.
        EditUserI user = getIntactUser(request);

        // The bean to extract the values.
        XreferenceBean xb = ((EditorActionForm) form).getNewXref();

        // For Go database, set values from the Go server.
        if (xb.getDatabase().equals("go")) {
            ActionErrors errors = xb.setFromGoServer(user);
            // Non null error indicates errors.
            if (errors != null) {
                saveErrors(request, errors);
                // Display the errors in the input page.
                return mapping.getInputForward();
            }
        }
        CvDatabase db = (CvDatabase) user.getObjectByLabel(
                CvDatabase.class, xb.getDatabase());
        CvXrefQualifier xqual = (CvXrefQualifier) user.getObjectByLabel(
                CvXrefQualifier.class, xb.getQualifier());
        Xref xref = new Xref(user.getInstitution(), db, xb.getPrimaryId(),
                xb.getSecondaryId(), xb.getReleaseNumber(), xqual);
        // Add the bean to the view; new bean is wrapped around the xref.
        user.getView().addXref(new XreferenceBean(xref));

        return mapping.getInputForward();
    }

    /**
     * Handles both submit/save actions.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param submit true for submit action.
     * @return failure mapping for any errors in updating the CV object; search
     * mapping if the update is successful and the previous search has only one
     * result; results mapping if the update is successful and the previous
     * search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    private ActionForward submitForm(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request,
                                     boolean submit)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The current view.
        AbstractEditViewBean view = user.getView();

        // Extract the short label for us to cheque for its uniqueness.
        String formlabel = (String) ((EditorActionForm) form).getShortLabel();

        // Validate the short label.
        if (user.shortLabelExists(formlabel)) {
            // Found more than one entry with the same short label.
            String link = "<a href=\"javascript:show('" + user.getSelectedTopic()
                    + "', '" + formlabel + "')\">here</a>";
            ActionErrors errors = new ActionErrors();
            errors.add("shortLabel",
                    new ActionError("error.cvinfo.label", formlabel, link));
            saveErrors(request, errors);
            // Display the errors in the input page.
            return mapping.getInputForward();
        }
        // Validate the data.
//        view.validate(user);
        view.sanityCheck(user);

        try {
            // Persist my current state (this takes care of updating the wrapped
            // object with values from the form).
            view.persist(user);

            // Any other objects to persist in their own transaction.
            try {
                view.persistOthers(user);
            }
            catch (IntactException ie) {
                // Delete the object we are editing at the moment (it has
                // already been made persisted by persist() method.
                user.delete();
                // Rethrow it again for outer try block to catch it and
                // log the exception.
                throw ie;
            }
        }
        catch (IntactException ie1) {
            // We may need to
            // Log the stack trace.
            LOGGER.info(ie1);
            // Error with updating.
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.update",
                    ie1.getRootCause().getMessage()));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE);
        }
        finally {
            // Release the lock only for submit.
            if (submit) {
                user.releaseLock();
            }
        }
        if (submit) {
            // Update the search cache.
            user.updateSearchCache(view.getAnnotatedObject());

            // Add the current edited object to the recent list.
            view.addToRecentList(user);

            // Only show the submitted record.
            return mapping.findForward(RESULT);
        }
        // We can't use mapping.getInputForward here as this return value
        // is used by subclasses (they need to distinguish between a success or
        // a failure such as duplicate short labels).
        return mapping.findForward(SUCCESS);
    }
}
