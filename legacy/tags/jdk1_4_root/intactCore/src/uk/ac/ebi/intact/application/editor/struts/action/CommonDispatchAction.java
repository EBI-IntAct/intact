/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.*;
import org.apache.ojb.broker.query.Criteria;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorFormI;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentActionForm;
import uk.ac.ebi.intact.application.editor.exception.SessionExpiredException;
import uk.ac.ebi.intact.application.editor.util.IntactHelperUtil;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * This dispatcher class contains common dispath events for all the forms.
 * The common dispatch events are the actions related to action buttons plus
 * adding new annotations and xrefs.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CommonDispatchAction extends AbstractEditorDispatchAction {

    // Implements super's abstract methods.

    /**
     * Provides the mapping from resource key to method name.
     *
     * @return Resource key / method name map.
     */
    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("button.submit", "submit");
        map.put("button.save.continue", "save");
        map.put("button.clone", "clone");
        map.put("annotations.button.add", "addAnnot");

        map.put("exp.button.review","addAnnot");
        map.put("exp.button.accept","addAnnot");

        map.put("xrefs.button.add", "addXref");
        return map;
    }

    /**
     * Action for submitting the edit form.
     *
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in updating the CV object; search
     *         mapping if the update is successful and the previous search has only one
     *         result; results mapping if the update is successful and the previous
     *         search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward submit(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        ActionForward forward = submitForm(mapping, form, request, true);

        // Only return to the result page for a successful submission.
        if (forward.getPath().equals(mapping.findForward(SUCCESS).getPath())) {
            // Handler to the Intact User.
            EditUserI user = getIntactUser(request);

            // The current view.
            AbstractEditViewBean view = user.getView();

            // Update the search cache.
            user.updateSearchCache(view.getAnnotatedObject());

            // Add the current edited object to the recent list.
            view.addToRecentList(user);

            // Only show the submitted record.
            forward = mapping.findForward(RESULT);
        }
        return forward;
    }

    /**
     * Action for saving the edit form.
     *
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in updating the CV object; search
     *         mapping if the update is successful and the previous search has only one
     *         result; results mapping if the update is successful and the previous
     *         search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward save(ActionMapping mapping,
                              ActionForm form,
                              HttpServletRequest request,
                              HttpServletResponse response)
            throws Exception {
        ActionForward forward = submitForm(mapping, form, request, false);
        // Turn editing mode on as it was switched off upon a successfull comitt
        if (forward.equals(mapping.findForward(SUCCESS))) {
            getIntactUser(request).startEditing();
        }
        return forward;
    }

    /**
     * Action for cloning the edit form. The current object is saved before
     * cloning it.
     *
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in updating the CV object; search
     *         mapping if the update is successful and the previous search has only one
     *         result; results mapping if the update is successful and the previous
     *         search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward clone(ActionMapping mapping,
                               ActionForm form,
                               HttpServletRequest request,
                               HttpServletResponse response)
            throws Exception {
        // Save the form first. Analyze the forward path.
        ActionForward forward = save(mapping, form, request, response);

        // Return the forward if it isn't a success.
        if (!forward.equals(mapping.findForward(SUCCESS))) {
            return forward;
        }
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The current view.
        AbstractEditViewBean view = user.getView();

        // Get the original object for clone.
        AnnotatedObjectImpl orig = (AnnotatedObjectImpl) view.getAnnotatedObject();
        // Clone it.
        AnnotatedObjectImpl copy = (AnnotatedObjectImpl) orig.clone();

        // Release the lock first.
        getLockManager().release(view.getAc());

        // Now, set the view as the cloned object.
        user.setClonedView(copy);

        // Redisplay the cloned object.
        return mapping.findForward(RELOAD);
    }

    /**
     * Action for adding a new annotation.
     *
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in cancelling the CV object; search
     *         mapping if the cancel is successful and the previous search has only one
     *         result; results mapping if the cancel is successful and the previous
     *         search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward addAnnot(ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);
        String userName = user.getUserName();

        ResourceBundle rb = ResourceBundle.getBundle("uk.ac.ebi.intact.application.editor.MessageResources");

        // The current form.
        EditorFormI editorForm = (EditorFormI) form;

        // The dispatch value holds the button label.
        String dispatch = editorForm.getDispatch();
        String acceptButtonLabel = rb.getString("exp.button.accept");
        String reviewButtonLabel = rb.getString("exp.button.review");
        if(dispatch.equals(reviewButtonLabel) || dispatch.equals(acceptButtonLabel)) {
            acceptOrReview(mapping, form, request, response, dispatch, acceptButtonLabel);
        } else{

            // The bean to extract the values.
            CommentBean cb = editorForm.getNewAnnotation();

            // The current view.
            AbstractEditViewBean view = user.getView();

            // Does this bean exist in the current view?
            if (view.annotationExists(cb)) {
                // The errors to display.
                ActionErrors errors = new ActionErrors();
                errors.add("new.annotation", new ActionError("error.annotation.exists"));
                saveErrors(request, errors);

                // Set the anchor
                setAnchor(request, editorForm);
                // Display the error in the edit page.
                return mapping.getInputForward();
            }
            // Add the bean to the view.
            view.addAnnotation((CommentBean) cb.clone());

            // Set anchor if necessary.
            setAnchor(request, editorForm);

            return mapping.getInputForward();
        }
        //setAnchor(request, editorForm);
        return mapping.getInputForward();
    }

    /**
     * Action for adding a new xref.
     *
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in cancelling the CV object; search
     *         mapping if the cancel is successful and the previous search has only one
     *         result; results mapping if the cancel is successful and the previous
     *         search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward addXref(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Handler to the EditUserI.
        EditUserI user = getIntactUser(request);

        // The current form.
        EditorFormI editorForm = (EditorFormI) form;

        // The bean to extract the values.
        XreferenceBean xb = editorForm.getNewXref();

        // The current view.
        AbstractEditViewBean view = user.getView();

        // Does this bean exist in the current view?
        if (view.xrefExists(xb)) {
            ActionErrors errors = new ActionErrors();
            errors.add("new.xref", new ActionError("error.xref.exists"));
            saveErrors(request, errors);

            // Set the anchor
            setAnchor(request, editorForm);
            // Display the error in the edit page.
            return mapping.getInputForward();
        }
        // For Go database, set values from the Go server.
        if (xb.getDatabase().equals("go")) {
            ActionErrors errors = xb.setFromGoServer(user.getGoProxy());
            // Non null error indicates errors.
            if (errors != null) {
                saveErrors(request, errors);
                // Set the anchor
                setAnchor(request, editorForm);
                // Display the errors in the input page.
                return mapping.getInputForward();
            }
        }

        // We test that the xref has a valid primaryId, has the hasValidPrimaryId is already implemented in
        // uk.ac.ebi.intact.model.Xref, out of the XreferenceBean we create an xref and use its method hasValidPrimaryId
        // If if return false we display the error.
        Xref xref = createXref(xb);
        if(!xref.hasValidPrimaryId()){
            ActionErrors errors = new ActionErrors();
            errors.add("new.xref", new ActionError("error.xref.pid.not.valid"));
            saveErrors(request, errors);

            // Set the anchor
            setAnchor(request, editorForm);
            // Display the error in the edit page.
            return mapping.getInputForward();
        }

        // Add the bean to the view.
        view.addXref((XreferenceBean) xb.clone());

        // Set anchor if necessary.
        setAnchor(request, editorForm);

        return mapping.getInputForward();
    }

    /**
     * Given a XreferenceBean it creates a Xref and return it
     * @param xb
     * @return
     * @throws IntactException
     */

    public Xref createXref(XreferenceBean xb) throws IntactException {
        Institution institution = new Institution("ebi");
        IntactHelper helper = IntactHelperUtil.getDefaultIntactHelper();
        CvDatabase cvDatabase = (CvDatabase) helper.getObjectByLabel(CvDatabase.class , xb.getDatabase());
        CvXrefQualifier cvXrefQualifier = new CvXrefQualifier(institution, xb.getQualifier());
        Xref xref = new Xref(institution,cvDatabase,xb.getPrimaryId(),xb.getSecondaryId(),xb.getReleaseNumber(),cvXrefQualifier);
        return xref;
    }

    /**
     * Handles both submit/save actions.
     *
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param submit true for submit action.
     * @return failure mapping for any errors in updating the CV object; search
     *         mapping if the update is successful and the previous search has only one
     *         result; results mapping if the update is successful and the previous
     *         search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    protected ActionForward submitForm(ActionMapping mapping,
                                       ActionForm form,
                                       HttpServletRequest request,
                                       boolean submit)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The current view.
        AbstractEditViewBean view = user.getView();

        // The current form.
        EditorFormI editForm = (EditorFormI) form;

        // The new short label.
        String newFormLabel = getShortLabel(user, view.getEditClass(),
                editForm.getShortLabel());

        // Update the view and the form.
        if (!view.getShortLabel().equals(newFormLabel)) {
            view.setShortLabel(newFormLabel);
            editForm.setShortLabel(newFormLabel);
        }
        // Runs the editor sanity checking
        view.sanityCheck();

        try {
            // Persist my current state (this takes care of updating the wrapped
            // object with values from the form).
            view.persist(user);

            // Any other objects to persist in their own transaction.
            view.persistOthers(user);
        }
        catch (IntactException ie) {
            // Log the stack trace.
            LOGGER.error("Exception trying to persist the view ", ie);
            // Error with updating.
            ActionErrors errors = new ActionErrors();
            // The error message.
            String msg = ie.getRootCause() != null ? ie.getRootCause().getMessage()
                    : "Update failure, root cause is not availabe";
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.update", msg));
            saveErrors(request, errors);
            return mapping.findForward(FAILURE);
        }
        finally {
            // Release the lock only for submit.
            if (submit) {
                getLockManager().release(view.getAc());
            }
            // Clear any left overs from previous transaction.
            view.clearTransactions();
        }
        // All are up todate except for AC which still can be null for a new object.
        editForm.setAc(view.getAcLink());

        // We can't use mapping.getInputForward here as this return value
        // is used by subclasses (they need to distinguish between a success or
        // a failure such as duplicate short labels).
        return mapping.findForward(SUCCESS);
    }

    /**
     * Returns a unique short label.
     * @param user to get the next shortlabel available and to check whether given
     * short label is unique or not.
     * @param editClass the current editing class.
     * @param formlabel the short label from the form.
     * @return the new short label if <code>formlabel</code> is not unique or else
     * it is as same as <code>formlabel</code>
     * @throws IntactException for errors in searching the database.
     */
    protected String getShortLabel(EditUserI user, Class editClass,
                                   String formlabel) throws IntactException {
        // No need to get the next available short label if it is unique.
        if (!user.shortLabelExists(formlabel)) {
            return formlabel;
        }
        // Try to get the next available short label by adding -x if it doesn't
        // end with -
        formlabel += formlabel.endsWith("-") ? "x" : "-x";
        return user.getNextAvailableShortLabel(editClass, formlabel);
    }

    /**
     * Returns the query to get gene names for a Protein
     * @param shortlabel the Shortlabel of the Experiment.
     * @return the query to extract the gene name for given protein AC
     */
    public Query getCuratorNameQuery(String shortlabel) {
        Criteria crit = new Criteria();
        // Need all records for given alias AC.
        crit.addEqualTo("shortlabel", shortlabel);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(Experiment.class, crit);

        // Limit to userstamp
        query.setAttributes(new String[] {"userstamp"});
        return query;
    }

    public ActionForward acceptOrReview(ActionMapping mapping,
                                        ActionForm form,
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        String dispatch,
                                        String acceptButtonLabel) throws SessionExpiredException, IntactException {

        EditUserI user = getIntactUser(request);
        EditorFormI editorForm = (EditorFormI)form;
        ExperimentActionForm expForm=(ExperimentActionForm)form;
        IntactHelper helper=user.getIntactHelper();
        String userName = user.getUserName();

        // Search for the userstamp corresponding to the experiment (name of the curator who has
        // curated the experiment)
        String shortlabel = expForm.getShortLabel();
        String userstamp="";
        Query query = getCuratorNameQuery(shortlabel);
        Iterator userstamps = helper.getIteratorByReportQuery(query);
        Object[] row = (Object[])userstamps.next();
        userstamp=(String)row[0];


        // If the user who is trying to Accept or Review the experiment is the user who has curated the experiment
        // display the error : "You can not Accept or Review your own curated experiment"d
        if(userName.toUpperCase().trim().equals(userstamp)){
            ActionErrors errors = new ActionErrors();
            errors.add("new.annotation", new ActionError("error.curator.accepter"));
            saveErrors(request, errors);
            // Set the anchor
            setAnchor(request, editorForm);
            // Display the error in the edit page.
            return mapping.getInputForward();
        }else{
            Calendar cal = new GregorianCalendar();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH)+1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            CvTopic cvTopic;

            if(dispatch.equals(acceptButtonLabel)){ // if the button press is "Accept"
                // The topic for new annotation.
                cvTopic = (CvTopic) helper.getObjectByLabel(CvTopic.class, "accepted");
            }else{ // if the button press is "Review"
                // The topic for new annotation.
                cvTopic = (CvTopic) helper.getObjectByLabel(CvTopic.class, "to-be-reviewed");
            }
            Annotation annotation=new Annotation(getService().getOwner(), cvTopic,cvTopic.getFullName());
            CommentBean cb = new CommentBean(annotation);
            AbstractEditViewBean view = getIntactUser(request).getView();
            view.addAnnotation(cb);
            view.copyPropertiesTo(editorForm);
            expForm.clearNewBeans();
            expForm.resetDispatch();
        }
        setAnchor(request, editorForm);

        return mapping.getInputForward();
    }
}