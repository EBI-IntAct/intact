/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.experiment;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.commons.search.ResultWrapper;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentViewBean;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Interaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The action class to search an Interaction (in the context of an Experiment).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionDispatchAction extends AbstractEditorDispatchAction {

    // Implements super's abstract methods.

    /**
     * Provides the mapping from resource key to method name.
     * @return Resource key / method name map.
     */
    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("exp.int.button.recent", "recent");
        map.put("exp.int.button.search", "search");
        return map;
    }

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
     *
     * @return - represents a destination to which the action servlet,
     * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     * or HttpServletResponse.sendRedirect() to, as a result of processing
     * activities of an <code>Action</code> class
     */
    public ActionForward recent(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The current view of the edit session.
        ExperimentViewBean view = (ExperimentViewBean) user.getView();

        Set recentInts = user.getCurrentInteractions();
        if (recentInts.isEmpty()) {
            ActionErrors errors = new ActionErrors();
            errors.add("exp.int.search",
                    new ActionError("error.exp.int.search.recent.empty"));
            saveErrors(request, errors);
        }
        else {
            // We have edited/added experiments in the current session.
            view.addInteractionToHold(recentInts);
        }
        return mapping.getInputForward();
    }

    public ActionForward search(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // The form.
        ExperimentActionForm expform = (ExperimentActionForm) form;

        // Search AC has high priority.
        String searchValue = expform.getIntSearchAC();

        // Assume search parameter is AC.
        String searchParam = "ac";

        // Search for short label if 'ac' is empty.
        if (searchValue.length() == 0) {
            searchValue = expform.getIntSearchLabel();
            searchParam = "shortLabel";
        }
        // The search value.
        if (searchValue.length() == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add("exp.int.search",
                    new ActionError("error.exp.int.search.input"));
            saveErrors(request, errors);
            return mapping.getInputForward();
        }
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The maximum interactions allowed.
        int max = getService().getInteger("int.search.limit");

        // The wrapper to hold lookup result.
        ResultWrapper rw = null;
        try {
            rw = user.lookup(Interaction.class, searchParam, searchValue, max);
        }
        catch (IntactException ie) {
            // This can only happen when problems with creating an internal helper
            // This error is already logged from the User class.
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.intact"));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE);
        }

        // Check the size
        if (rw.isTooLarge()) {
            ActionErrors errors = new ActionErrors();
            errors.add("exp.int.search", new ActionError("error.exp.int.search.many",
                    Integer.toString(rw.getPossibleResultSize()), searchParam, Integer.toString(max)));
            saveErrors(request, errors);
            // Report back to the form.
            return mapping.getInputForward();
        }

        // Search found any results?
        if (rw.isEmpty()) {
            ActionErrors errors = new ActionErrors();
            errors.add("exp.int.search",
                    new ActionError("error.exp.int.search.empty", searchParam));
            saveErrors(request, errors);
            // Report back to the form.
            return mapping.getInputForward();
        }
        // The current view of the edit session.
        ExperimentViewBean view = (ExperimentViewBean) user.getView();

        // Add the search result to the holder.
        view.addInteractionToHold(rw.getResult());

        return mapping.getInputForward();
    }
}