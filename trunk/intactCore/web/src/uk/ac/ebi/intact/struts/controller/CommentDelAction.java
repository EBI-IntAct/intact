/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.controller;

import uk.ac.ebi.intact.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.struts.view.CommentBean;
import uk.ac.ebi.intact.struts.service.IntactService;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.util.Assert;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Iterator;

/**
 * The class is called by struts framework when the user deletes a comment.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CommentDelAction extends IntactBaseAction {

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
     * @return - represents a destination to which the controller servlet,
     * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     * or HttpServletResponse.sendRedirect() to, as a result of processing
     * activities of an <code>Action</code> class
     */
    public ActionForward perform (ActionMapping mapping, ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        // The ac passed via the request.
        String ac = (String) request.getParameter("ac");

        // Handler to the IntactService.
        IntactService service = super.getIntactService();

        try {
            // The Intact Helper to search the database.
            IntactHelper helper = service.getIntactHelper();

            // Find the annotation object to delete.
            Collection results = (Collection) helper.search(
                Annotation.class.getName(), "ac", ac);

            // We should at least have one annotation.
            Assert.assert(!results.isEmpty(),
                "Unable to find the annotation for " + ac);

            // The annotation to delete.
            Iterator iter = results.iterator();
            Annotation annot = (Annotation) iter.next();

            // We should only have one annotation.
            Assert.assert(!iter.hasNext(),
                "Expected one annotation but received multiple annotations!");

            // Delete the annotation.
            helper.delete(annot);
        }
        catch (IntactException ie) {
            // Can't create a helper class.
            ActionErrors errors = new ActionErrors();
            errors.add(super.INTACT_ERROR, new ActionError("error.datasource"));
            super.saveErrors(request, errors);
            return (mapping.findForward(WebIntactConstants.FORWARD_FAILURE));
        }
        // The annotation collection.
        Collection collection = (Collection) super.getSessionObject(request,
            WebIntactConstants.ANNOTATIONS);

        // The bean for 'ac'.
        CommentBean bean = findByAc(ac, collection);

        // We must have the bean.
        Assert.assert(bean != null,
            "Unable to find the bean for accession number " + ac);

        // Remove it from the collection.
        collection.remove(bean);
        return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
    }

    // Returns a comment bean from a given collection for matching Ac.
    private CommentBean findByAc(String ac, Collection collection) {
        for (Iterator iter = collection.iterator(); iter.hasNext();) {
            CommentBean bean = (CommentBean) iter.next();
            if (bean.getAc().equals(ac)) {
                return bean;
            }
        }
        // Not found the bean; shouldn't happen.
        return null;
    }
}
