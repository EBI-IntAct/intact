/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import org.apache.struts.action.*;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.Globals;
import org.apache.commons.beanutils.DynaBean;
import javax.servlet.http.*;

import uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants;
import uk.ac.ebi.intact.application.cvedit.struts.framework.CvAbstractAction;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;

/**
 * Creates a result form object and initialize it with data for results.jsp
 * to display. The struts-config.xml file must have the definition for the
 * results form bean created in this class.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */

public class ResultPreAction extends CvAbstractAction {

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
     * @return success mapping (move to results page).
    */
   public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(request);

        ModuleConfig appConfig = (ModuleConfig) request.getAttribute(Globals.MODULE_KEY);
        FormBeanConfig config = appConfig.findFormBeanConfig("resultForm");
        DynaActionFormClass dynaClass =
            DynaActionFormClass.createDynaActionFormClass(config);

        DynaBean dynaForm = dynaClass.newInstance();
        dynaForm.set("items", user.getSearchCache());
        request.setAttribute("resultForm", dynaForm);
        // Move to the results page.
        return mapping.findForward(CvEditConstants.FORWARD_SUCCESS);
    }
}
