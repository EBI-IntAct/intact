/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMappings;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.ActionConfig;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.Globals;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ComponentBean;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import java.util.Map;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Dispatcher action which dispatches to various submit actions.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SubmitFormAction extends AbstractEditorAction {

    /**
     * Action for submitting the edit form.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in updating the CV object; search
     * mapping if the update is successful and the previous search has only one
     * result; results mapping if the update is successful and the previous
     * search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        LOGGER.debug("At the beginning of SubmitFormAction");
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String para = (String) e.nextElement();
            LOGGER.debug("parameter: " + para + " - " + request.getParameter(para));
        }
        // Cast the form.
        EditorActionForm editorForm = (EditorActionForm) form;

        // Update the bean with form values.
        getIntactUser(request).getView().copyPropertiesFrom(editorForm);

        // The map containing action paths.
        Map map = (Map) getApplicationObject(EditorConstants.ACTION_MAP);

        // The dispatch value holds the button label.
        String dispatch = editorForm.getDispatch();
        LOGGER.debug("Dispatch received " + dispatch);

//        if (dispatch == null) {
//            dispatch = ((InteractionActionForm) form).getDispatchFeature();
//            LOGGER.debug("Received feature dispatch: " + dispatch);
//            InteractionActionForm myform = (InteractionActionForm) form;
//            for (Iterator iter = myform.getProteins().iterator(); iter.hasNext();) {
//                ComponentBean pb = (ComponentBean) iter.next();
//                for (Iterator iter1 = pb.getFeatures().iterator(); iter1.hasNext();) {
//                    FeatureBean fb = (FeatureBean) iter1.next();
//                    LOGGER.debug("Processing feature " + fb.getAc() + " and selected status: " + fb.isSelected());
//                    if (fb.isSelected()) {
//                        LOGGER.debug("Feaure: " + fb.getAc() + " was selected");
//                    }
//                }
//            }
//        }
        // The action path from the map.
        String path = (String) map.get(dispatch);

        if (path != null) {
            return mapping.findForward(path);
        }
        LOGGER.info("Received a null mapping; check the EditorActionServlet "
                + "for setting the action map only for non Interaction form");
//        LOGGER.error("Dispatch received " + dispatch);
        return mapping.findForward(FAILURE);
    }
}
