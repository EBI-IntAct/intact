/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import uk.ac.ebi.intact.application.editor.struts.action.SubmitFormAction;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * This class extends from the common Submit action to handle submit events
 * (Edit/Delete) related to a Feature.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SubmitIntFormAction extends SubmitFormAction {

    /**
     * Action for submitting the Interaction form.
     *
     * @param mapping  the <code>ActionMapping</code> used to select this instance
     * @param form     the optional <code>ActionForm</code> bean for this request
     *                 (if any).
     * @param request  the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in updating the CV object; search
     *         mapping if the update is successful and the previous search has only one
     *         result; results mapping if the update is successful and the previous
     *         search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Ask super to handle it first.
        ActionForward forward = super.execute(mapping, form, request, response);

        // Only process if forward is not a success.
        if (!forward.getPath().equals(mapping.findForward(FAILURE).getPath())) {
            // non failure; simply return the returned action path.
            return forward;
        }
        // Super can't handle the submit action.
        InteractionActionForm intform = (InteractionActionForm) form;

        // The dispatch related to feature.
        String dispatch = intform.getDispatchFeature();

        LOGGER.debug("Dispatch is SubmitInt action: " + dispatch);
        // The map containing action paths.
        Map map = (Map) getApplicationObject(EditorConstants.ACTION_MAP);

        // The action path from the map.
        String path = (String) map.get(dispatch);

        if (path != null) {
            return mapping.findForward(path);
        }
        LOGGER.error("Unknown mapping; check the EditorActionServlet "
                + " for setting the action map");
        return mapping.findForward(FAILURE);
    }
}
