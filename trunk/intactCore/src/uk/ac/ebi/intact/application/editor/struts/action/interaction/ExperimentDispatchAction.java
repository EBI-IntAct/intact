/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionActionForm;
import uk.ac.ebi.intact.model.Experiment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The action class to search an Experiment (in the context of an Interaction).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentDispatchAction extends AbstractEditorDispatchAction {

    // Implements super's abstract methods.

    /**
     * Provides the mapping from resource key to method name.
     * @return Resource key / method name map.
     */
    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("int.exp.button.recent", "recent");
        map.put("int.exp.button.search", "search");
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
        InteractionViewBean view = (InteractionViewBean) user.getView();

        Set recentExps = user.getCurrentExperiments();
        if (recentExps.isEmpty()) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.int.exp.search.recent.empty"));
            saveErrors(request, errors);
            return mapping.getInputForward();
        }
        // We have edited/added experiments in the current session.
        view.addExperimentToHold(recentExps);
        return mapping.findForward(SUCCESS);
    }

    public ActionForward search(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // The form.
        InteractionActionForm intform = (InteractionActionForm) form;

        // Search AC has high priority.
        String searchValue = intform.getExpSearchAC();

        // Assume search parameter is AC.
        String searchParam = "ac";

        // Search for short label if 'ac' is empty.
        if (searchValue.length() == 0) {
            searchValue = intform.getExpSearchLabel();
            searchParam = "shortLabel";
        }

        if (searchValue.length() == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add("int.exp.search",
                    new ActionError("error.int.exp.search.input"));
            saveErrors(request, errors);
            return mapping.getInputForward();
        }
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The collection to hold experiments.
        Collection experiments = user.search1(Experiment.class.getName(), searchParam,
                searchValue);
        // Search found any results?
        if (experiments.isEmpty()) {
            ActionErrors errors = new ActionErrors();
            errors.add("int.exp.search",
                    new ActionError("error.int.exp.search.empty", searchParam));
            saveErrors(request, errors);
            return mapping.getInputForward();
        }
        // The number of Experiments retrieved from the search.
        int size = experiments.size();

        // Just an arbitrary number for the moment.
        if (size > 10) {
            ActionErrors errors = new ActionErrors();
            errors.add("int.exp.search",
                    new ActionError("error.int.exp.search.many",
                            Integer.toString(size), searchParam, "10"));
            saveErrors(request, errors);
            return mapping.getInputForward();
        }
        // The current view of the edit session.
        InteractionViewBean view = (InteractionViewBean) user.getView();

        // Add the search result to the holder.
        view.addExperimentToHold(experiments);

        return mapping.findForward(SUCCESS);
    }
}