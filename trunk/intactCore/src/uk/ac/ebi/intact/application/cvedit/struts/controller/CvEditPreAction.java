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
import uk.ac.ebi.intact.application.cvedit.struts.view.CvViewBean;
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
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request (if any)
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return always returns successful mapping as this action class sets data for
     * a JSP to display.
    */
   public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);

        // The current user view.
        CvViewBean viewbean = user.getView();

        // Fill data for annotations.
        EditForm ceForm = this.getEditForm(session,
                CvEditConstants.COMMENT_EDIT_FORM);
        ceForm.setItems(viewbean.getAnnotations());

        // Fill data for xrefs.
        EditForm xeForm = this.getEditForm(session, CvEditConstants.XREF_EDIT_FORM);
        xeForm.setItems(viewbean.getXrefs());

        // Move to the results page.
        return mapping.findForward(CvEditConstants.FORWARD_SUCCESS);
    }

    /**
     * Returns the edit form for given type.
     * @param session the HTTP session object to access and store the bean.
     * @param type the form type; CvEditConstants.COMMENT_EDIT_FORM or
     * CvEditConstants.XREF_EDIT_FORM
     * @return the form created for given <code>type</code>.
     *
     * <pre>
     * post: session.getAttribute(type) != Null
     * </pre>
     */
    private EditForm getEditForm(HttpSession session, String type) {
        EditForm ceForm = (EditForm) session.getAttribute(type);
        if (ceForm == null) {
            ceForm = new EditForm();
            session.setAttribute(type, ceForm);
        }
        return ceForm;
    }
}
