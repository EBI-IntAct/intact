/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import uk.ac.ebi.intact.application.cvedit.struts.framework.CvAbstractAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvViewBean;
import uk.ac.ebi.intact.application.cvedit.struts.view.EditForm;
import uk.ac.ebi.intact.application.cvedit.struts.view.XreferenceBean;
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
public class XrefEditAction extends CvAbstractAction {

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
        XreferenceBean xb = viewbean.getXref(index);

        // We must have the bean.
        assert xb != null;

        if (theForm.editPressed()) {
            // Must save this bean.
            xb.setEditState(false);
        }
        else if (theForm.savePressed()) {
            // Save button pressed.
            Xref xref = null;
            try {
                xref = doSaving(user, xb);
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
            // The xref to update.
            viewbean.addXrefToUpdate(xref);
        }
        else if (theForm.deletePressed()) {
            // Delete is pressed.
            viewbean.delXref(xb);
        }
        else {
            // Unknown operation; should never get here.
            assert false;
        }
        return mapping.findForward(CvEditConstants.FORWARD_SUCCESS);
    }

    // Helper methods

    private Xref doSaving(IntactUserIF user, XreferenceBean xb)
            throws SearchException {
        // The xref object to update
        Xref xref = xb.getXref();

        // Only update the database if it has been changed.
        String database = xb.getDatabase();
        if (!database.equals(xref.getCvDatabase().getShortLabel())) {
            // The database the new xref belong to.
            CvDatabase db = (CvDatabase) user.getObjectByLabel(
                CvDatabase.class, database);
            xref.setCvDatabase(db);
        }
        xref.setPrimaryId(xb.getPrimaryId());
        xref.setSecondaryId(xb.getSecondaryId());
        xref.setDbRelease(xb.getReleaseNumber());

        String qualifier = xb.getQualifier();
        // Check for null pointer.
        if (xref.getCvXrefQualifier() != null) {
            // Only update the quailier if they differ.
            if (!qualifier.equals(xref.getCvXrefQualifier().getShortLabel())) {
                CvXrefQualifier xqual = (CvXrefQualifier) user.getObjectByLabel(
                    CvXrefQualifier.class, qualifier);
                xref.setCvXrefQualifier(xqual);
            }
        }
        // Back to edit
        xb.setEditState(true);
        return xref;
    }
}