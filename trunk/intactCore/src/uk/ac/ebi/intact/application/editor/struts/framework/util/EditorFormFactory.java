/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework.util;

import java.util.Map;
import java.util.HashMap;

import org.apache.commons.beanutils.DynaBean;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.Globals;
import org.apache.struts.action.DynaActionFormClass;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ebi.intact.application.editor.struts.view.EditForm;

/**
 * The factory class to create edit form beans.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditorFormFactory {

    /**
     * Maps: name -> bean. Cache for dyna form beans. It is safe to reuse the
     * form beans as only one can be used at any time. We cache them to reuse
     *  rather than creating a new instance for each selection.
     */
    private Map myNameToDynaForm = new HashMap();

    /**
     * Maps: name -> Edit form. Cache for Edit forms. The purpose as same as above.
     */
    private Map myNameToEditForm = new HashMap();

    /**
     * Returns a DynaBean form constructed using given form name and Http request.
     * @param formName the name of the form.
     * @param request Http to extract the information to construct the form.
     * @return the DynaBean form; the form specification must be defined in the
     * struts configuration file under <code>name</code>. A cached form is
     * returned if a form exists in the local cache saved under
     * <code>formName</code>. A null form is retured for any errors (such
     * as unable to construct an instance).
     */
    public DynaBean getDynaBean(String formName, HttpServletRequest request) {
        DynaBean form = null;
        if (myNameToDynaForm.containsKey(formName)) {
            form = (DynaBean) myNameToDynaForm.get(formName);
        }
        else {
            // Doesn't exist; create a new form.
            try {
                form = createForm(formName, request);
            }
            catch (InstantiationException e) {
                // Null form will be returned.
            }
            catch (IllegalAccessException e) {
                // Null form will be returned.
            }
            myNameToDynaForm.put(formName, form);
        }
        return form;
    }

    /**
     * Returns an edit form from internal cache if it exists or a new form.
     * @param formName the name of the form.
     * @return A cached form is returned if a form exists in the local cache
     * saved under <code>formName</code> or else a new form is created and
     * saved in the cache before returning it.
     */
    public EditForm getEditForm(String formName) {
        EditForm form = null;
        if (myNameToEditForm.containsKey(formName)) {
            form = (EditForm) myNameToEditForm.get(formName);
        }
        else {
            form = new EditForm();
            myNameToEditForm.put(formName, form);
        }
        return form;
    }

    // Helper Methods

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
