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
import uk.ac.ebi.intact.util.Assert;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.persistence.TransactionException;
import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.*;
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
                                  HttpServletResponse response) {
        // The annotation collection.
        Collection collection = (Collection) super.getSessionObject(request,
            WebIntactConstants.XREFS);

        // The bean for 'ac'.
        XreferenceBean bean = findByAc(request.getParameter("ac"), collection);

        // null if the bean wasn't found. this is not expected to happen.
        Assert.assert(bean != null, "Unable to find the xref bean to delete");

        // Remove it from the collection.
        collection.remove(bean);

        // There is no need to delete from the persistence system if this x'ref
        // is in a transaction.
        if (bean.inTransaction()) {
            super.log("Deleting an uncommitted xref");
            return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
        }
        // Not in a transaction; we must delete it from the persistence storage.
        IntactUserIF user = super.getIntactUser(request);
        try {
            super.log("Transaction before xref delete: " + user.isActive());
            user.delete(bean.getXref());
            super.log("Transaction after xref delete: " + user.isActive());
        }
        catch (TransactionException te) {
            // Log the stack trace.
            super.log(ExceptionUtils.getStackTrace(te));
            // Clear any previous errors.
            super.clearErrors();
            super.addError("error.transaction", te.getMessage());
            super.saveErrors(request);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
        // The annotation for the selected object.
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
