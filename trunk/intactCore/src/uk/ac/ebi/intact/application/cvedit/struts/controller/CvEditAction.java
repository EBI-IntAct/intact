/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvEditForm;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.business.IntactException;

import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.*;

/**
 * This class is called by Struts when Submit, Delete or Cancel is called from
 * edit jsp.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CvEditAction extends IntactBaseAction {

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
    public ActionForward perform (ActionMapping mapping, ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        // Determine the action taken by the user.
        CvEditForm theForm = (CvEditForm) form;

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(request);

        if (theForm.isSubmitted()) {
            // Commit all the changes.
            try {
                user.commit();
            }
            catch (IntactException ie) {
                // Unable to create an annotation.
                super.addError("error.transaction.commit", ie.getNestedMessage());
                super.saveErrors(request);
                return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
            }
            super.log("After committing the update: " + user.isActive());
        }
        else if (theForm.isDeleted()) {
            super.log("Cv Object is deleted");
            // Confirm the delete.
            return mapping.findForward(WebIntactConstants.FORWARD_DEL_CONFIRM);
        }
        else {
            // Assume Cancel is chosen.
            try {
                user.rollback();
            }
            catch (IntactException ie) {
                // Unable to rollback the transaction.
                super.addError("error.transaction.rollback", ie.getNestedMessage());
                super.saveErrors(request);
                return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
            }
        }
        return mapping.findForward(super.fwdResultsOrSearch(request));
    }
}
