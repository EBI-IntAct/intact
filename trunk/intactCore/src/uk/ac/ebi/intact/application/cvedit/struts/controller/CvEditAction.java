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
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.persistence.TransactionException;
import uk.ac.ebi.intact.persistence.CreateException;

import org.apache.struts.action.*;

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
            // The object we are editing at the moment.
            CvObject cvobj = user.getCurrentEditObject();

            try {
                // Commit the changes.
                //super.log("About to commit");
                super.log("before the commit Transaction flag=" + user.isActive());
                user.commit();
                super.log("After the commit");

                // Update the current edit object using the helper.
                super.log("Before the update");
                super.log("Transaction flag=" + user.isActive());
                try {
                    user.begin();
                }
                catch (TransactionException te) {
                    // Unable to create an annotation.
                    super.addError("error.transaction.begin", te.getNestedMessage());
                    super.saveErrors(request);
                    return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
                }
                super.log("After transaction begun: " + user.isActive());

                try {
                    user.update(cvobj);
                }
                catch (CreateException ce) {
                    // Error in committing our changes. Force a rollback.
                    try {
                        user.rollback();
                    }
                    catch (TransactionException te1) {
                        // Just ignore it.
                    }
                    super.log("Unable to update the current CV object: " +
                        ce.getMessage());

                    // The errors to report back.
                    super.addError("error.transaction", ce.getNestedMessage());
                    super.saveErrors(request);
                    return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
                }
                super.log("After the update: " + user.isActive());
                try {
                    user.commit();
                }
                catch (TransactionException te) {
                    // Unable to create an annotation.
                    super.addError("error.transaction.commit", te.getNestedMessage());
                    super.saveErrors(request);
                    return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
                }
                super.log("After committing the update: " + user.isActive());
            }
            catch (TransactionException te) {
                // Error in committing our changes. Force a rollback.
                try {
                    user.rollback();
                }
                catch (TransactionException te1) {
                    // Just ignore it.
                }
                super.log("Unable to commit the current transaction: " +
                    te.getMessage());

                // The errors to report back.
                super.addError("error.transaction", te.getNestedMessage());
                super.saveErrors(request);
                return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
            }
        }
        else if (theForm.isDeleted()) {
            super.log("Cv Object is deleted");
            // Confirm the delete.
            return mapping.findForward(WebIntactConstants.FORWARD_DEL_CONFIRM);
        }
        // To get this far,we must have selected either Submit or Cancel option.
        super.log("Form is cancelled or submitted");

        // If we have multiple search results then we go to the results page
        // otherwise go to search page.
//        Boolean singleCase = (Boolean) super.getSessionObject(request,
//            WebIntactConstants.SINGLE_MATCH);
//        // Ensure that we have this attribute or else NullPointerException.
//        if ((singleCase != null) && !singleCase.booleanValue()) {
//            // Multiple objects; go to results page.
//            return mapping.findForward(WebIntactConstants.FORWARD_RESULTS);
//        }
//        // The default is to search again.
//        return mapping.findForward(WebIntactConstants.FORWARD_SEARCH);
        return mapping.findForward(super.fwdResultsOrSearch(request));

    }
}
