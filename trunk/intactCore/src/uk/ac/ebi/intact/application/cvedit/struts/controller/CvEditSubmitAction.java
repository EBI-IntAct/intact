/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvViewBean;
import uk.ac.ebi.intact.application.cvedit.struts.view.CommentBean;
import uk.ac.ebi.intact.application.cvedit.struts.view.XreferenceBean;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Xref;

import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.*;
import java.util.Collection;
import java.util.Iterator;

/**
 * Called when the user clicks on the submit button to commit changes.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CvEditSubmitAction extends IntactBaseAction {

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
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(request);

        // The object we are editing at the moment.
        CvObject cvobj = user.getCurrentEditObject();

        // Holds the annotations to add.
        CvViewBean viewbean = user.getView();

        // The annotations/xrefs to add and delete to/from persistent system.
        Collection addcomments = viewbean.getAnnotationsToAdd();
        Collection delcomments = viewbean.getAnnotationsToDel();
        Collection addxrefs = viewbean.getXrefsToAdd();
        Collection delxrefs = viewbean.getXrefsToDel();

        // Clear any previous errors.
        super.clearErrors();

        try {
            // Begin the transaction.
            user.begin();

            // Create annotations and add them to CV object.
            for (Iterator iter = addcomments.iterator(); iter.hasNext();) {
                Annotation annot = ((CommentBean) iter.next()).getAnnotation();
                user.create(annot);
                cvobj.addAnnotation(annot);
            }
            // Delete annotations and remove them from CV object.
            for (Iterator iter = delcomments.iterator(); iter.hasNext();) {
                Annotation annot = ((CommentBean) iter.next()).getAnnotation();
                user.delete(annot);
                cvobj.removeAnnotation(annot);
            }
            // Create xrefs and add them to CV object.
            for (Iterator iter = addxrefs.iterator(); iter.hasNext();) {
                Xref xref = ((XreferenceBean) iter.next()).getXref();
                user.create(xref);
                cvobj.addXref(xref);
            }
            // Delete xrefs and remove them from CV object.
            for (Iterator iter = delxrefs.iterator(); iter.hasNext();) {
                Xref xref = ((XreferenceBean) iter.next()).getXref();
                user.delete(xref);
                cvobj.removeXref(xref);
            }
            user.update(cvobj);

            // Commit all the changes.
            user.commit();

            // Update the drop down lists for certain types.
//            user.refreshList();
        }
        catch (IntactException ie1) {
            try {
                user.rollback();
            }
            catch (IntactException ie2) {
                // Oops! Problems with rollback; ignore this as this
                // error is reported via the main exception (ie1).
            }
            // Log the stack trace.
            super.log(ExceptionUtils.getStackTrace(ie1));
            // Error with updating.
            super.addError("error.update", ie1.getNestedMessage());
            super.saveErrors(request);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
        finally {
            // Clear containers; regradless of the outcome.
            viewbean.clearTransAnnotations();
            viewbean.clearTransXrefs();
        }
        // All changes are committed successfully; either search or results.
        return mapping.findForward(super.getForwardAction(request));
    }
}
