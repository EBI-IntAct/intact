/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import org.apache.struts.action.*;
import javax.servlet.http.*;

import uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants;
import uk.ac.ebi.intact.application.cvedit.struts.framework.CvAbstractAction;
import uk.ac.ebi.intact.application.cvedit.struts.view.EditForm;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;

/**
 * Fills the forms with data for edit.jsp to access.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */

public class CvEditPreAction extends CvAbstractAction {

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
        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);

//        ModuleConfig appConfig = (ModuleConfig) request.getAttribute(Globals.MODULE_KEY);
//        FormBeanConfig config = appConfig.findFormBeanConfig("commentEditForm");
//        DynaActionFormClass dynaClass =
//            DynaActionFormClass.createDynaActionFormClass(config);
//
//        DynaBean dynaForm = dynaClass.newInstance();
//        dynaForm.set("annotations", user.getView().getAnnotations().toArray(new CommentBean[0]));
//        super.log("In edit pre action 2");
//        Object[] annots = (Object[]) dynaForm.get("annotations");
//        for (int i = 0; i < annots.length; i++) {
//            super.log("Annot: " + i + " - " + ((CommentBean) annots[i]).getDescription());
//        }
//        super.log("In edit pre action 3 -- " + annots.length);

//        request.setAttribute("commentEditForm", dynaForm);

        // Fill data for annotations.
        EditForm ceForm = (EditForm) session.getAttribute(
                CvEditConstants.COMMENT_EDIT_FORM);
        if (ceForm == null) {
            ceForm = new EditForm();
            session.setAttribute(CvEditConstants.COMMENT_EDIT_FORM, ceForm);
        }
        ceForm.setItems(user.getView().getAnnotations());

        // Fill data for xrefs.
        EditForm xeForm = (EditForm) session.getAttribute(
                CvEditConstants.XREF_EDIT_FORM);
        if (xeForm == null) {
            xeForm = new EditForm();
            session.setAttribute(CvEditConstants.XREF_EDIT_FORM, xeForm);
        }
        xeForm.setItems(user.getView().getXrefs());

        // Move to the results page.
        return mapping.findForward(CvEditConstants.FORWARD_SUCCESS);
    }
}
