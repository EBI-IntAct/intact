/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.controller;

import uk.ac.ebi.intact.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.struts.view.CvEditForm;

import org.apache.struts.action.*;

import javax.servlet.http.*;

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

        if (theForm.isSubmitted()) {
            super.log("Form is submitted");
            // We should commit the transation now.
        }
        else if (theForm.isDeleted()) {
            super.log("Cv Object is deleted");
            // Rollback any changes and delete this CvObject from the datatabase.
        }
        else if (theForm.isCancelled()) {
            super.log("Form is cancelled");
        }
        else {
            super.log("Unknown action selected - should never happen");
        }
        return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
    }
}
