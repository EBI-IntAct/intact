/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.ResultBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Xref;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
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
        return submitForm(mapping, form, request, false);
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
        // Need the form to get data entered by the user.
        DynaActionForm dynaform = (DynaActionForm) form;

        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The bean to extract the values.
        CommentBean cb = (CommentBean) dynaform.get("annotation");

        // Bean is wrapped around this annotation.
        Annotation annot = cb.getAnnotation(user);

        // Add the bean to the view; need to create a new bean.
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
        // Need the form to get data entered by the user.
        DynaActionForm dynaform = (DynaActionForm) form;

        // Handler to the EditUserI.
        EditUserI user = getIntactUser(request);

        // The bean to extract the values.
        XreferenceBean xb = (XreferenceBean) dynaform.get("xref");

        // Try to to set the secondary id for a go primary id.
        ActionErrors errors = xb.setSecondaryIdFromGo(user);

        // Non null error indicates errors.
        if (errors != null) {
            saveErrors(request, errors);
            // Display the errors in the input page.
            return mapping.getInputForward();
        }
        // Bean is wrapped around this xref
        Xref xref = xb.getXref(user);

        // Add the bean to the view; need to create a new bean.
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

        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        // Extract the short label for us to cheque for its uniqueness.
        String formlabel = (String) dynaform.get("shortLabel");

        // Validate the short label.
        if (user.duplicateShortLabel(formlabel)) {
            // Found more than one entry with the same short label.
            ActionErrors errors = new ActionErrors();
            errors.add("shortLabel",
                    new ActionError("error.cvinfo.label", formlabel));
            saveErrors(request, errors);

            ActionMessages messages = new ActionMessages();
            messages.add(ActionMessages.GLOBAL_MESSAGE,
                    new ActionMessage("message.existing.labels", getExistingLabels(user)));
            saveMessages(request, messages);
            // Display the errors in the input page.
            return mapping.getInputForward();
        }
        String newlabel = user.getUniqueShortLabel(formlabel);
        dynaform.set("shortLabel", newlabel);

        // Validate the data.
        view.validate(user);

        // Update the annotated object with current values.
        view.update(user);

        try {
            // Persist my current state
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
            errors.add(AbstractEditorAction.EDITOR_ERROR,
                    new ActionError("error.update",
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
            // Need to rebuild the menu again as the short label may have been
            // changed. Remove it from cache.
            view.removeMenu();

            // Update the search cache.
            ResultBean rb = new ResultBean(view.getAnnotatedObject());
            user.updateSearchCache(rb);

            // Add the current edited object to the recent list.
            view.addToRecentList(user);

            // Only show the submitted record.
            return mapping.findForward(RESULT);
        }
        return mapping.getInputForward();
    }

    /**
     * Returns a list of existing short labels.
     * @param user to search the database.
     * @return a String object consisting of short labels of current object type
     * minus the short label of the current edit object.
     * @throws SearchException for errors in search the database for short labels.
     */
    private String getExistingLabels(EditUserI user) throws SearchException {
        // The current view topic.
        String topic = user.getSelectedTopic();

        // The buffer to construct existing labels.
        StringBuffer sb = new StringBuffer();

        // The counter to count line length.
        int lineLength = 0;
        // Flag to indicate processing of the first item.
        boolean first = true;
        // Search the database.
        for (Iterator iter = user.getExistingShortLabels().iterator();
             iter.hasNext();) {
            String label = (String) iter.next();
            if (first) {
                first = false;
            }
            else {
                sb.append(", ");
            }
            sb.append("<a href=\"" + "javascript:show('" + topic + "', '"
                    + label + "')\"" + ">" + label + "</a>");
            lineLength += label.length();
            if (lineLength > 80) {
                sb.append("<br/>");
                first = true;
                lineLength = label.length();
            }
        }
        return sb.toString();
    }
}
