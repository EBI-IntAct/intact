/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.controller;

import uk.ac.ebi.intact.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.struts.view.CommentBean;
import uk.ac.ebi.intact.struts.view.XrefAddForm;
import uk.ac.ebi.intact.struts.view.XreferenceBean;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Xref;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Iterator;

/**
 * The action class is called when the user adds a new cross reference
 * (annotation).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class XrefAddAction extends IntactBaseAction {

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
                                  HttpServletResponse response ) {
        // Need the form to get data entered by the user.
        XrefAddForm theForm = (XrefAddForm) form;

        // The annotation collection.
        Collection beans = (Collection) super.getSessionObject(
            request, WebIntactConstants.XREFS);

        // Collect data from the form to construct an xreference.
        String[] data = new String[] {
            theForm.getDatabase(), theForm.getPrimaryId(), theForm.getSecondaryId(),
            theForm.getReleaseNumber(), theForm.getQualifer()
        };
        // The bean for 'topic'.
        XreferenceBean bean = findByDbPrimaryId(data[0], data[1], beans);

        // Delete the old xrference and add the new one.
        if (bean != null) {
            beans.remove(bean);
        }
        // add to the database and and set the AC.
        // bean.setAc(....);
        // Add to the collection.
        // Get the database and CvXrefQualifier records from the db
        // CvDatabase cvdb = get database;
        // CvXrefQualifier cvx = get reference qualifier object.
        // Create a new Xref object. set the database, primary and secondary ID
        // release number and the qualifier.
        // Write to the database.

        // Add to the bean collection.
        bean = new XreferenceBean(data);
        // We should get this ac from the database.
        bean.setAc("EBI-XXX");
        beans.add(bean);

        // Reset the form
        theForm.reset();

        return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
    }

    // Return a bean from the given collection for matching database and
    // primary id. Null is returned if no matching bean found in the collection.
    private XreferenceBean findByDbPrimaryId(String db, String pid, Collection collection) {
        for (Iterator iter = collection.iterator(); iter.hasNext();) {
            XreferenceBean bean = (XreferenceBean) iter.next();
            if (bean.getDatabase().equals(db) && bean.getPrimaryId().equals(pid)) {
                return bean;
            }
        }
        // New topic.
        return null;
    }
}
