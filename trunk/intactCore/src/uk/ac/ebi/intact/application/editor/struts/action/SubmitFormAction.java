/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

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
//        System.out.println("At the beginning of form dispatch");
//        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
//            String para = (String) e.nextElement();
//            System.out.println("parameters: " + para + " - " + request.getParameter(para));
//        }

        // Cast the form.
        EditorActionForm editorForm = (EditorActionForm) form;

        // Update the bean with form values.
        getIntactUser(request).getView().copyPropertiesFrom(editorForm);

        // The map containing action paths.
        Map map = (Map) getApplicationObject(EditorConstants.ACTION_MAP);

        // The dispatch value holds the button label.
        String dispatch = editorForm.getDispatch();

        // The action path from the map.
        String path = (String) map.get(dispatch);

        if (path != null) {
            return mapping.findForward(path);
        }
        LOGGER.info("Received a null mapping; check the EditorActionServlet "
                + "for setting the action map");
        return mapping.findForward(FAILURE);
    }
}
