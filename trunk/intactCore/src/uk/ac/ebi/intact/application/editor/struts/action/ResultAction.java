/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.util.LockManager;
import uk.ac.ebi.intact.model.AnnotatedObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The action when the the user selects an entry from a list to edit an object.
 * Short label is passed as a request paramter.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ResultAction extends AbstractEditorAction {

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
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // This check is for display tag library when a click on a link
        // takes to the next page. Without this check, it will reset the view
        // bean to the original state.
        String page = request.getParameter("page");
        if (page != null) {
            // As a result of clicking on the page tag link.
            return mapping.findForward(SUCCESS);
        }
        // The ac to search
        String ac = request.getParameter("ac");
        // The class name to search.
        String className = request.getParameter("searchClass");

        LOGGER.info("AC: " + ac + " class: " + className);

        // Check the lock.
        LockManager lmr = LockManager.getInstance();

        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // Try to acuire the lock.
        if (!lmr.acquire(ac, user.getUserName())) {
            ActionErrors errors = new ActionErrors();
            // The owner of the lock (not the current user).
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.lock", ac, lmr.getOwner(ac)));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE);
        }
        // The selected Annotated object.
        AnnotatedObject annobj = (AnnotatedObject) user.getObjectByAc(
                        Class.forName("uk.ac.ebi.intact.model." + className), ac);
        // The object we are editing presently.
        user.setView(annobj);

        LOGGER.info("Numner of annotations: " + annobj.getAnnotations().size());
        LOGGER.info("Number of xrefs: " + annobj.getXrefs().size());

        return mapping.findForward(SUCCESS);
    }
}
