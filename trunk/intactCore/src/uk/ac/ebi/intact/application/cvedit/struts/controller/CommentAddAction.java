/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import uk.ac.ebi.intact.application.cvedit.struts.framework.CvAbstractAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvViewBean;
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
public class CommentAddAction extends CvAbstractAction {

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
        // Need the form to get data entered by the user.
        DynaActionForm theForm = (DynaActionForm) form;

        // The topic for the annotation.
        CvTopic cvtopic = null;

        // The owner of the object we are editing.
        Institution owner = null;

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(request);

        try {
            // Get the topic object for the new annotation.
            cvtopic = (CvTopic) user.getObjectByLabel(
                CvTopic.class, (String) theForm.get("topic"));

            // The owner of the object we are editing.
            owner = user.getInstitution();
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
        // The new annotation to add to database.
        Annotation annot = new Annotation();
        annot.setAnnotationText((String) theForm.get("description"));
        annot.setCvTopic(cvtopic);
        annot.setOwner(owner);

        // Add the annotation to the view.
        CvViewBean viewbean = user.getView();
        viewbean.addAnnotation(annot);

        // Clear previous entries.
        theForm.reset(mapping, request);

        return mapping.findForward(CvEditConstants.FORWARD_SUCCESS);
    }
}