/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.model.Experiment;

import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * The action class to search an Experiment (in the context of an Interaction).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentSearchAction extends AbstractEditorAction {

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
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        DynaActionForm theForm = (DynaActionForm) form;

        // The search parameter.
        String searchParam = getSearchParam(theForm);

        // The search value.
        String searchValue = (String) theForm.get(searchParam);
        if (searchValue.length() == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add("int.exp.search",
                    new ActionError("error.int.exp.search.input"));
            saveErrors(request, errors);
//            return new ActionForward(mapping.getInput());
            return mapping.findForward(EditorConstants.FORWARD_INPUT);
        }
        // Handler to the current user.
        EditUserI user = super.getIntactUser(request);

        // The collection to hold experiments.
        Collection experiments;
        try {
            experiments = user.search(Experiment.class.getName(), searchParam,
                    searchValue);
        }
        catch (SearchException se) {
            ActionErrors errors = new ActionErrors();
            errors.add("int.exp.search",
                    new ActionError("error.search", se.getMessage()));
            saveErrors(request, errors);
//            return new ActionForward(mapping.getInput());
            return mapping.findForward(EditorConstants.FORWARD_INPUT);
        }
        // Search found any results?
        if (experiments.isEmpty()) {
            ActionErrors errors = new ActionErrors();
            errors.add("int.exp.search",
                    new ActionError("error.int.exp.search.empty", searchParam));
            saveErrors(request, errors);
//            return new ActionForward(mapping.getInput());
            return mapping.findForward(EditorConstants.FORWARD_INPUT);
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
//            return new ActionForward(mapping.getInput());
            return mapping.findForward(EditorConstants.FORWARD_INPUT);
        }
        // Can safely cast it as we have the correct editor view bean.
        InteractionViewBean view = (InteractionViewBean) user.getView();

        // Add the search result to the holder.
        view.addExperimentToHold(experiments);

        return mapping.findForward(EditorConstants.FORWARD_SUCCESS);
    }

    /**
     * Returns the search parameter.
     * @param form the form to get search parameter values.
     * @return the search parameter; the most sepecific search
     * is preferred over the least specific one. For example, 'ac' is preferred
     * over any other value.
     */
    private String getSearchParam(DynaActionForm form) {
        if (((String) form.get("ac")).length() != 0) {
            return "ac";
        }
        if (((String) form.get("spAc")).length() != 0) {
            return "spAc";
        }
        return "shortLabel";
    }
}