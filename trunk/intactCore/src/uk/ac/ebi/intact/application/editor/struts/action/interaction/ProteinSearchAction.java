/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.model.Protein;

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

        // Handler to the current user.
        EditUserI user = super.getIntactUser(request);

        String searchParam = (String) theForm.get("param");
        String searchValue = (String) theForm.get("value");

        // The collection to hold proteins.
        Collection proteins;

        if (searchParam.equals("spAc")) {
            proteins = user.getProteinsByXref(searchValue);
            // Try importing Proteins via SRS
            if (proteins.isEmpty()) {
                proteins = user.getSPTRProteins(searchValue);
            }
        }
        else {
            proteins = user.search(Protein.class.getName(), searchParam,
                    searchValue);
        }
        // Search found any results?
        if (proteins.isEmpty()) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.int.protein.search.empty", searchParam));
            saveErrors(request, errors);
            return mapping.getInputForward();
        }
        // The number of Proteins retrieved from the search.
        int psize = proteins.size();

        // Just an arbitrary number for the moment.
        if (psize > 10) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.int.protein.search.many",
                            Integer.toString(psize), searchParam, "10"));
            saveErrors(request, errors);
            return mapping.getInputForward();
        }
        // Can safely cast it as we have the correct editor view bean.
        InteractionViewBean view = (InteractionViewBean) user.getView();

        for (Iterator iter = proteins.iterator(); iter.hasNext();) {
            view.addProtein((Protein) iter.next());
        }
        return mapping.findForward(FORWARD_SUCCESS);
    }
}