/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.security;

import java.io.IOException;

import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.view.EditForm;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.business.EditUser;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.model.Constants;
import uk.ac.ebi.intact.business.IntactException;

import org.apache.struts.action.*;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.Globals;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.beanutils.DynaBean;

import javax.servlet.http.*;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;

/**
 * Implements the logic to authenticate a user for the editor application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class LoginAction extends AbstractEditorAction {

    /**
     * Used as a key to identify a datasource class - its value
     * is defined in the web.xml file as a servlet context parameter
     */
    private static final String theirDSKey = "datasource";

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // Get the user's login name and password. They should have already
        // validated by the ActionForm.
        DynaActionForm theForm = (DynaActionForm) form;
        String username = (String) theForm.get("username");
        String password = (String) theForm.get("password");

        // Validate the form parameters as we are using dynamic form.
        if (username == null || username.length() < 1) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("global.required", "username"));
            super.saveErrors(request, errors);
            return mapping.findForward(EditorConstants.FORWARD_FAILURE);
        }

        // Save the context to avoid repeat calls.
        ServletContext ctx = super.getServlet().getServletContext();

        // Name of the mapping file and data source.
        String repfile = ctx.getInitParameter(Constants.MAPPING_FILE_KEY);
        String ds = ctx.getInitParameter(theirDSKey);

        // Create an instance of EditorService.
        EditUserI user = null;

        // Invalidate any previous sessions.
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // Create a new session.
        session = request.getSession(true);
        super.log("Created a new session");
        try {
            user = new EditUser(repfile, ds, username, password);
        }
        catch (DataSourceException de) {
            // Unable to get a data source...can't proceed
            super.log(ExceptionUtils.getStackTrace(de));
            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add(AbstractEditorAction.EDITOR_ERROR,
                    new ActionError("error.datasource", de.getMessage()));
            saveErrors(request, errors);
            return mapping.findForward(EditorConstants.FORWARD_FAILURE);
        }
        catch (IntactException ie) {
            // Unable to access the intact helper.
            super.log(ExceptionUtils.getStackTrace(ie));
            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add(AbstractEditorAction.EDITOR_ERROR,
                    new ActionError("error.invalid.user"));
            saveErrors(request, errors);
            return mapping.findForward(EditorConstants.FORWARD_FAILURE);
        }
        catch (SearchException se) {
            // Unable to construct lists such as topics, db names etc.
            super.log(ExceptionUtils.getStackTrace(se));
            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add(AbstractEditorAction.EDITOR_ERROR,
                    new ActionError("error.invalid.user"));
            saveErrors(request, errors);
            return mapping.findForward(EditorConstants.FORWARD_FAILURE);
        }
        // Need to access the user later.
        session.setAttribute(EditorConstants.INTACT_USER, user);

        // Set the forms for the session (basicially recyling the forms here).
        // All these forms are declared in session scope in the struts
        // config file.
        session.setAttribute(EditorConstants.FORM_RESULTS,
                createForm(EditorConstants.FORM_RESULTS, request));
        session.setAttribute(EditorConstants.FORM_INFO,
                createForm(EditorConstants.FORM_INFO, request));
        session.setAttribute(EditorConstants.FORM_BIOSOURCE,
                createForm(EditorConstants.FORM_BIOSOURCE, request));

        session.setAttribute(EditorConstants.FORM_COMMENT_EDIT, new EditForm());
        session.setAttribute(EditorConstants.FORM_XREF_EDIT, new EditForm());
        return mapping.findForward(EditorConstants.FORWARD_SUCCESS);
    }


    /**
     * Creates a new DynaBean.
     * @param formName the name of the form configured in the struts
     * configuration file.
     * @param request the HTTP request to get the application configuration.
     * @return new <code>DynaBean</code> instance.
     * @throws InstantiationException errors in creating the bean
     * @throws IllegalAccessException errors in creating the bean
     */
    private DynaBean createForm(String formName, HttpServletRequest request)
            throws InstantiationException, IllegalAccessException {
        // Fill the form to edit short label and full name.
        ModuleConfig appConfig = (ModuleConfig) request.getAttribute(
            Globals.MODULE_KEY);
        FormBeanConfig config = appConfig.findFormBeanConfig(formName);
        DynaActionFormClass dynaClass =
                DynaActionFormClass.createDynaActionFormClass(config);
        return dynaClass.newInstance();
    }
}
