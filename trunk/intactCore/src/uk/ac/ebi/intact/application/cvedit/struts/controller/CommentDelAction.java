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
        // The annotation on display.
        Collection viewbeans = (Collection) super.getSessionObject(request,
            WebIntactConstants.ANNOTS_TO_VIEW);

        // Find the bean for 'key'.
        CommentBean bean = findByKey(request.getParameter("key"), viewbeans);

        // We must have the bean.
        assert bean != null;

        // Remove it from the collection.
        viewbeans.remove(bean);

        // Collection of comments to delete.
        Collection delcomments = (Collection) super.getSessionObject(request,
            WebIntactConstants.ANNOTS_TO_DELETE);

        // Collections of comments to add.
        Collection addcomments = (Collection) super.getSessionObject(request,
            WebIntactConstants.ANNOTS_TO_ADD);

        // Are we trying to delete a bean that has been just added?
        if (addcomments.contains(bean)) {
            addcomments.remove(bean);
        }
        else {
            // No; we should remove it when the transaction is committed.
            delcomments.add(bean);
        }
        return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
    }

    // Returns a comment bean from a given collection for matching Ac.
    private CommentBean findByKey(String keystr, Collection collection) {
        long key = Long.parseLong(keystr);
        for (Iterator iter = collection.iterator(); iter.hasNext();) {
            CommentBean bean = (CommentBean) iter.next();
            if (bean.getKey() == key) {
                return bean;
            }
        }
        // Not found the bean; shouldn't happen.
        return null;
    }
}
