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
import uk.ac.ebi.intact.application.cvedit.struts.view.ResultBean;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;

/**
 * This class provides the actions required for the search page
 * for intact. The search criteria are obtained from a Form object
 * and then the search is carried out, via the IntactUser functionality,
 * which provides the business logic.
 *
 * @author Chris Lewington, Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */

public class ResultPreAction extends CvAbstractAction {

    /**
     * A single element array for toArray() method.
     */
    private static final ResultBean[] RESULT_BEAN_ARRAY = new ResultBean[0];

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
        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);

        ModuleConfig appConfig = (ModuleConfig) request.getAttribute(Globals.MODULE_KEY);
        FormBeanConfig config = appConfig.findFormBeanConfig("resultForm");
        DynaActionFormClass dynaClass =
            DynaActionFormClass.createDynaActionFormClass(config);

        DynaBean dynaForm = dynaClass.newInstance();
        dynaForm.set("items", user.getCacheSearchResult().toArray(RESULT_BEAN_ARRAY));
        request.setAttribute("resultForm", dynaForm);
        // Move to the results page.
        return mapping.findForward(CvEditConstants.FORWARD_SUCCESS);
    }
}
