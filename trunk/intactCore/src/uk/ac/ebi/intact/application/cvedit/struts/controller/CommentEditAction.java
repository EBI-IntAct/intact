/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import uk.ac.ebi.intact.application.cvedit.struts.framework.CvAbstractAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvViewBean;
import uk.ac.ebi.intact.application.cvedit.struts.view.CommentBean;
import uk.ac.ebi.intact.application.cvedit.struts.view.EditForm;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.SearchException;

import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The action class is called when the user adds a new comment (annotation) to
 * the edit.jsp.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CommentEditAction extends CvAbstractAction {

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
     * @return - represents a destination to which the controller servlet,
     * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     * or HttpServletResponse.sendRedirect() to, as a result of processing
     * activities of an <code>Action</code> class
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        EditForm theForm = (EditForm) form;

        // The current view of the edit session.
        IntactUserIF user = super.getIntactUser(request);
        CvViewBean viewbean = user.getView();

        // The bean associated with the current action.
        int index = theForm.getIndex();
        CommentBean cb = viewbean.getAnnotation(index);

        // We must have the annotation bean.
        assert cb != null;

        if (theForm.editPressed()) {
            // Must save this bean.
            cb.setEditState(false);
        }
        else if (theForm.savePressed()) {
            // Save button pressed.
            Annotation annot = null;
            try {
                annot = doSaving(user, cb);
            }
            catch (SearchException se) {
                // Can't query the database.
                super.log(ExceptionUtils.getStackTrace(se));
                // Clear any previous errors.
                super.clearErrors();
                super.addError("error.search", se.getMessage());
                super.saveErrors(request);
                return mapping.findForward(CvEditConstants.FORWARD_FAILURE);
            }
            // The annotation to update.
            viewbean.addAnnotationToUpdate(annot);
        }
        else if (theForm.deletePressed()) {
            // Delete is pressed.
            viewbean.delAnnotation(cb);
        }
        else {
            // Unknown operation; should never get here.
            assert false;
        }
        return mapping.findForward(CvEditConstants.FORWARD_SUCCESS);
    }

    // Helper methods

    private Annotation doSaving(IntactUserIF user, CommentBean cb)
            throws SearchException {
        // Update with the new description.
        Annotation annot = cb.getAnnotation();
        annot.setAnnotationText(cb.getDescription());

        // Only update the topic if they differ.
        String topic = cb.getTopic();
        if (!topic.equals(annot.getCvTopic().getShortLabel())) {
            // Get the topic object for the new annotation.
            CvTopic cvtopic = (CvTopic) user.getObjectByLabel(CvTopic.class, topic);
            annot.setCvTopic(cvtopic);
        }
        // Back to edit
        cb.setEditState(true);
        return annot;
    }
}