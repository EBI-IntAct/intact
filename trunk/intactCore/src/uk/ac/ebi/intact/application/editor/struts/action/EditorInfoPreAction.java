/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.*;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.Globals;
import org.apache.commons.beanutils.DynaBean;

import javax.servlet.http.*;

import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditViewBeanFactory;
import uk.ac.ebi.intact.application.editor.struts.view.biosource.BioSourceEditViewBean;

/**
 * Fills the forms with info data (short label & full name) for edit.jsp to
 *  access.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */

public class EditorInfoPreAction extends AbstractEditorAction {

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
     * @return 'biosource' forward if the current edit object is a BioSource
     * instance. For other object types, 'successful' forward is returned.
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Session to access various session objects.
        HttpSession session = getSession(request);

        // The current user view.
        AbstractEditViewBean viewbean = getIntactUser(session).getView();

        // Fill the form to edit short label and full name.
        DynaBean infoForm = getDynaForm("cvinfoForm", request, session);
        infoForm.set("ac", viewbean.getAc());
        infoForm.set("shortLabel", viewbean.getShortLabel());
        infoForm.set("fullName", viewbean.getFullName());

        if (viewbean.getEditorType().equals(EditViewBeanFactory.BIOSRC_EDITOR)) {
            DynaBean bioForm = getDynaForm("bioSourceForm", request, session);
            bioForm.set("taxId", ((BioSourceEditViewBean) viewbean).getTaxId());
        }
        // Move to the results page.
        return mapping.findForward(EditorConstants.FORWARD_SUCCESS);
    }

    /**
     * Returns a DynaBean from the session; new bean is created if the bean
     * doesn't exist in the session (the new bean is stored in a session).
     * @param formName the name of the form configured in the struts
     * configuration file.
     * @param request the HTTP request to get the application configuration.
     * @param session the HTTP session object to access and store the bean.
     * @return <code>DynaBean</code> instance either retrieved from
     * <code>session</code> or a new object.
     * @throws InstantiationException errors in creating the bean
     * @throws IllegalAccessException errors in creating the bean
     *
     * <pre>
     * post: session.getAttribute(EditorConstants.CV_INFO_FORM) != Null
     * </pre>
     */
    private DynaBean getDynaForm(String formName, HttpServletRequest request,
                                   HttpSession session)
            throws InstantiationException, IllegalAccessException {
        // Fill the form to edit short label and full name.
        DynaBean dynaForm = (DynaBean) session.getAttribute(formName);
        if (dynaForm == null) {
            ModuleConfig appConfig =
                    (ModuleConfig) request.getAttribute(Globals.MODULE_KEY);
            FormBeanConfig config = appConfig.findFormBeanConfig(formName);
            DynaActionFormClass dynaClass =
                    DynaActionFormClass.createDynaActionFormClass(config);
            dynaForm = dynaClass.newInstance();
            session.setAttribute(formName, dynaForm);
            super.log("Created " + formName + " instance ");
        }
        return dynaForm;
    }
}
