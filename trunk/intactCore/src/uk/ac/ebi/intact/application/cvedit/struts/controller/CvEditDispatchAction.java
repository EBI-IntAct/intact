/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvViewBean;
import uk.ac.ebi.intact.application.cvedit.struts.view.CommentBean;
import uk.ac.ebi.intact.application.cvedit.struts.view.XreferenceBean;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants;
import uk.ac.ebi.intact.application.cvedit.struts.framework.CvAbstractDispatchAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.CvAbstractAction;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.business.IntactException;

/**
 * Dispatches an action according to 'dispatch' parameter.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CvEditDispatchAction extends CvAbstractDispatchAction {

    // Implements super's abstract methods.

    /**
     * Provides the mapping from resource key to method name.
     * @return Resource key / method name map.
     */
    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("button.submit", "submit");
        map.put("button.delete", "delete");
        map.put("button.cancel", "cancel");
        return map;
    }

    /**
     * Action for submitting the CV edit form.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in updating the CV object; search
     * mapping if the update is successful and the previous search has only one
     * result; results mapping if the update is successful and the previous
     * search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward submit(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response)
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
        Collection updatecomments = viewbean.getAnnotationsToUpdate();
        Collection addxrefs = viewbean.getXrefsToAdd();
        Collection delxrefs = viewbean.getXrefsToDel();
        Collection updatexrefs = viewbean.getXrefsToUpdate();

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
            // Update annotations.
            for (Iterator iter = updatecomments.iterator(); iter.hasNext();) {
                user.update(iter.next());
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
            // Update xrefs.
            for (Iterator iter = updatexrefs.iterator(); iter.hasNext();) {
                user.update(iter.next());
            }
            // Update the cv object.
            user.update(cvobj);

            // Commit all the changes.
            user.commit();
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
            this.log(ExceptionUtils.getStackTrace(ie1));
            // Error with updating.
            ActionErrors errors = new ActionErrors();
            errors.add(CvAbstractAction.INTACT_ERROR,
                    new ActionError("error.update", ie1.getNestedMessage()));
            super.saveErrors(request, errors);
            return mapping.findForward(CvEditConstants.FORWARD_FAILURE);
        }
        finally {
            // Clear containers; regradless of the outcome.
            viewbean.clearTransAnnotations();
            viewbean.clearTransXrefs();
        }
        // All changes are committed successfully; either search or results.
        return mapping.findForward(super.getForwardAction(user));
    }

    /**
     * Action for deleting a CV object.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in deleting the CV object; search
     * mapping if the delete is successful and the previous search has only one
     * result; results mapping if the delete is successful and the previous
     * search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward delete(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(request);

        // The object to delete.
        CvObject cvobj = user.getCurrentEditObject();

        try {
            // Begin the transaction.
            user.begin();
            // Delete the object we are editing at the moment.
            user.delete(cvobj);
            // Commit all the changes.
            user.commit();
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
            // Error with deleting the object.
            ActionErrors errors = new ActionErrors();
            errors.add(CvAbstractAction.INTACT_ERROR,
                    new ActionError("error.delete", ie1.getNestedMessage()));
            super.saveErrors(request, errors);
            return mapping.findForward(CvEditConstants.FORWARD_FAILURE);
        }
        user.removeFromSearchCache(cvobj.getAc());
        // Deleted successfully; either search or results.
        return mapping.findForward(super.getForwardAction(user));
    }

    /**
     * Action for cancelling a CV object.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in cancelling the CV object; search
     * mapping if the cancel is successful and the previous search has only one
     * result; results mapping if the cancel is successful and the previous
     * search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward cancel(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
            // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(request);

        // Cancel the current update.
        user.cancelUpdate();

        // Cancel any pending annotations/xrefs.
        CvViewBean viewbean = user.getView();
        viewbean.clearTransAnnotations();
        viewbean.clearTransXrefs();

        // Either search or results.
        return mapping.findForward(super.getForwardAction(user));
    }
}
