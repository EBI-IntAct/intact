/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.controller;

import uk.ac.ebi.intact.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.struts.view.XreferenceBean;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
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
                                  HttpServletResponse response)
        throws ServletException {
        // The ac passed via the request.
        String ac = (String) request.getParameter("ac");

        // The annotation collection.
        Collection collection = (Collection) super.getSessionObject(request,
            WebIntactConstants.XREFS);

        // The bean for 'ac'.
        XreferenceBean bean = findByAc(ac, collection);

        // null if the bean wasn't found.
        if (bean == null) {
            // This should never happen; we are going to throw an exception as
            // this is not expected to happen in normal operation.
            String msg ="Unable to find the bean for accession number " + ac;
            super.log(msg);
            throw new ServletException(msg);
        }
        else {
            // Remove it from the collection.
            collection.remove(bean);
        }
        return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
    }

    // Returns a Xreference bean from a given collection for matching Ac.
    private XreferenceBean findByAc(String ac, Collection collection) {
        for (Iterator iter = collection.iterator(); iter.hasNext();) {
            XreferenceBean bean = (XreferenceBean) iter.next();
            if (bean.getAc().equals(ac)) {
                return bean;
            }
        }
        // Not found the bean; shouldn't happen.
        return null;
    }
}
