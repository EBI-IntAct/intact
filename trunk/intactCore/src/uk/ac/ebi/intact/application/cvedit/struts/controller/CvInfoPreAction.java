/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import org.apache.struts.action.*;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.Globals;
import org.apache.commons.beanutils.DynaBean;

import javax.servlet.http.*;

import uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants;
import uk.ac.ebi.intact.application.cvedit.struts.framework.CvAbstractAction;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvViewBean;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;

/**
 * Fills the forms with CV info data (short label & full name) for edit.jsp to
 *  access.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */

public class CvInfoPreAction extends CvAbstractAction {

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

        // Fill the form to edit short label and full name.
        DynaBean dynaForm = this.getCvInfoForm(request, session);
        super.log("Short Label: " + viewbean.getShortLabel());
        super.log("Full Name: " + viewbean.getFullName());
        dynaForm.set("ac", viewbean.getAc());
        dynaForm.set("shortLabel", viewbean.getShortLabel());
        dynaForm.set("fullName", viewbean.getFullName());

        // Move to the results page.
        return mapping.findForward(CvEditConstants.FORWARD_SUCCESS);
    }

    /**
     * Returns a DynaBean from the session; new bean is created if the bean
     * doesn't exist in the session (the new bean is stored in a session).
     * @param request the HTTP request to get the application configuration.
     * @param session the HTTP session object to access and store the bean.
     * @return <code>DynaBean</code> instance either retrieved from
     * <code>session</code> or a new object.
     * @throws InstantiationException errors in creating the bean
     * @throws IllegalAccessException errors in creating the bean
     *
     * <pre>
     * post: session.getAttribute(CvEditConstants.CV_INFO_FORM) != Null
     * </pre>
     */
    private DynaBean getCvInfoForm(HttpServletRequest request, HttpSession session)
            throws InstantiationException, IllegalAccessException {
        // Fill the form to edit short label and full name.
        DynaBean dynaForm = (DynaBean) session.getAttribute(
                CvEditConstants.CV_INFO_FORM);
        if (dynaForm == null) {
            ModuleConfig appConfig =
                    (ModuleConfig) request.getAttribute(Globals.MODULE_KEY);
            FormBeanConfig config = appConfig.findFormBeanConfig(
                    CvEditConstants.CV_INFO_FORM);
            DynaActionFormClass dynaClass =
                DynaActionFormClass.createDynaActionFormClass(config);
            dynaForm = dynaClass.newInstance();
            session.setAttribute(CvEditConstants.CV_INFO_FORM, dynaForm);
            super.log("Created cvinfo instance ");
        }
        return dynaForm;
    }
}
