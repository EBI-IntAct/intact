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

import org.apache.struts.action.*;

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
            // Save button pressed. The xref to update.
            viewbean.addXrefToUpdate(xb);
            // Back to edit
            xb.setEditState(true);
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
}