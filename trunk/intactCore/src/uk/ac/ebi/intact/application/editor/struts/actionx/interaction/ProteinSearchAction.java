/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.actionx.interaction;

import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.viewx.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.persistence.SearchException;

import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Iterator;

/**
 * The action class to search a Protein.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinSearchAction extends AbstractEditorAction {

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
            errors.add("protein.search",
                    new ActionError("error.protein.search.input"));
            saveErrors(request, errors);
            return new ActionForward(mapping.getInput());
        }
        // Handler to the current user.
        EditUserI user = super.getIntactUser(request);
//        log("Searching for: " + searchParam + " and value: " + searchValue);

        // The collection to hold proteins.
        Collection proteins;
        try {
            proteins = user.search(Protein.class.getName(), searchParam,
                    searchValue);
        }
        catch (SearchException se) {
            ActionErrors errors = new ActionErrors();
            errors.add("protein.search",
                    new ActionError("error.search", se.getMessage()));
            saveErrors(request, errors);
            return new ActionForward(mapping.getInput());
        }
        // Search found any results?
        if (proteins.isEmpty()) {
            ActionErrors errors = new ActionErrors();
            errors.add("protein.search",
                    new ActionError("error.protein.search.empty", searchParam));
            saveErrors(request, errors);
            return new ActionForward(mapping.getInput());
        }
        // The number of Prteins retrieved from the search.
        int psize = proteins.size();

        // Just an arbitrary number for the moment.
        if (psize > 10) {
            ActionErrors errors = new ActionErrors();
            errors.add("protein.search",
                    new ActionError("error.protein.search.many",
                            Integer.toString(psize), searchParam, "10"));
            saveErrors(request, errors);
            return new ActionForward(mapping.getInput());
        }
        // Can safely cast it as we have the correct editor view bean.
        InteractionViewBean view = (InteractionViewBean) user.getView();

        for (Iterator iter = proteins.iterator(); iter.hasNext();) {
            view.addProtein((Protein) iter.next());
        }

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
        if (((String) form.get("shortLabel")).length() != 0) {
            return "shortLabel";
        }
        return "fullName";
    }
}