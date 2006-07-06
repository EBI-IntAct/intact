/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.controller;

import uk.ac.ebi.intact.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.struts.view.CommentBean;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Iterator;

/**
 * The class is called when the user deletes a comment.
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

        // The annotation collection.
        Collection collection = (Collection) super.getSessionObject(request,
            WebIntactConstants.ANNOTATIONS);

        // The bean for 'ac'.
        CommentBean bean = findByAc(ac, collection);

        // null if the bean wasn't found.
        if (bean == null) {
            super.log("Unable to find the bean for accession number " + ac);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
        else {
            // Remove it from the collection.
            collection.remove(bean);
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
