/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.application.cvedit.struts.view.CommentBean;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.business.IntactException;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        // The annotation collection.
        Collection collection = (Collection) super.getSessionObject(request,
            WebIntactConstants.ANNOTATIONS);

        // The bean for 'ac'.
        CommentBean bean = findByAc(request.getParameter("ac"), collection);

        // We must have the bean.
        assert bean != null;

        // Remove it from the collection.
        collection.remove(bean);

        // There is no need to delete from the persistence system if this comment
        // is in a transaction.
//        if (bean.inTransaction()) {
//            super.log("Deleting an uncommitted annotation");
//            return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
//        }
        // Not in a transaction; we must delete it from the persistence storage.

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(request);

        try {
            super.log("Transaction before annt delete: " + user.isActive());
            user.delete(bean.getAnnotation());
            super.log("Transaction after annt delete: " + user.isActive());
        }
        catch (IntactException ie) {
            // Clear any previous errors.
            super.clearErrors();
            super.addError("error.transaction", ie.getMessage());
            super.saveErrors(request);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
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
