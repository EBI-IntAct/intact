/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvViewBean;
import uk.ac.ebi.intact.application.cvedit.struts.view.XreferenceBean;
import org.apache.struts.action.*;

import javax.servlet.http.*;

/**
 * The action class is called when the user deletes a cross reference (xref).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class XrefDelAction extends IntactBaseAction {

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
        // The key to search as a long.
        long key = Long.parseLong(request.getParameter("key"));

        // Save the current view.
        CvViewBean viewbean = super.getIntactUser(request).getView();

        // The xref we want to delete.
        XreferenceBean delxref = viewbean.findXref(key);

        // We must have the xref.
        assert delxref != null;
        viewbean.delXref(delxref);

        return mapping.findForward(CvEditConstants.FORWARD_SUCCESS);
    }
}
