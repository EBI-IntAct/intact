/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.application.cvedit.struts.view.XreferenceBean;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.model.Xref;
import org.apache.struts.action.*;

import javax.servlet.http.*;
import java.util.Collection;
import java.util.Iterator;

/**
 * The action class is called when the user deletes a cross reference (xref).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class XrefDelAction extends IntactBaseAction {

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
        Collection viewbeans = (Collection) super.getSessionObject(request,
            WebIntactConstants.XREFS_TO_VIEW);

        // The bean for 'key'.
        XreferenceBean bean = findByKey(request.getParameter("key"), viewbeans);

        // null if the bean wasn't found. this is not expected to happen.
        assert bean != null;

        // Remove it from the view.
        viewbeans.remove(bean);

        // Collection of xrefs to delete.
        Collection delxrefs = (Collection) super.getSessionObject(request,
            WebIntactConstants.XREFS_TO_DELETE);

        // Collections of xrefs to add.
        Collection addxrefs = (Collection) super.getSessionObject(request,
            WebIntactConstants.XREFS_TO_ADD);

        // Are we trying to delete a bean that has been just added?
        if (addxrefs.contains(bean)) {
            addxrefs.remove(bean);
        }
        else {
            // No; we should remove it when the transaction is committed.
            delxrefs.add(bean);
        }
        // The annotation for the selected object.
        return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
    }

    // Returns a Xreference bean from a given collection for matching Ac.
    private XreferenceBean findByKey(String keystr, Collection collection) {
        long key = Long.parseLong(keystr);
        for (Iterator iter = collection.iterator(); iter.hasNext();) {
            XreferenceBean bean = (XreferenceBean) iter.next();
            if (bean.getKey() == key) {
                return bean;
            }
        }
        // Not found the bean; shouldn't happen.
        return null;
    }
}
