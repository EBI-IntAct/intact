/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvAddForm;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.application.cvedit.exception.SessionExpiredException;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.persistence.SearchException;
import uk.ac.ebi.intact.business.IntactException;
import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.*;
import java.util.Collection;

/**
 * This action is invoked when the user adds a new short label in
 * response to adding a new CV object.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CvAddAction extends IntactBaseAction {

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
        // Need the form to get data entered by the user.
        CvAddForm theForm = (CvAddForm) form;

        if (theForm.isSubmitted()) {
            // The form is submitted.
            formSubmitted(request, theForm.getShortLabel());

            // Any errors in creating a new CV object?
            if (super.hasErrors()) {
                return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
            }
            // Set it single match; once the editing is over, it will return
            // back to the search page.
            IntactUserIF user = super.getIntactUser(request);
            user.setSearchResultStatus(1);
            return mapping.findForward(WebIntactConstants.FORWARD_EDIT);
        }
        // Cancel assumed.
        super.log("Form is cancelled");
        // Back to the search page.
        return mapping.findForward(WebIntactConstants.FORWARD_SEARCH);
    }

    /**
     * This method is responsible for handling the sequence of events when the
     * user presses Submit button to create a new CV object. Errors are added
     * to super classes's error container as they occur.
     *
     * @param request the request to access various objects saved under a
     * session.
     * @param label the short label to create new CV object.
     * @exception SessionExpiredException for an expired session.
     */
    private void formSubmitted(HttpServletRequest request, String label)
            throws SessionExpiredException {
        super.log("Form is submitted");

        // Clear any previous errors.
        super.clearErrors();

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(request);

        // The topic selected by the user.
        String topic = user.getSelectedTopic();

        // The class name associated with the topic.
        String classname = super.getIntactService().getClassName(topic);

        // The owner of the object we are editing.
        Institution owner = null;

        // Holds the result from the search.
        Collection result = null;

        try {
            owner = user.getInstitution();
            result = user.search(classname,
                WebIntactConstants.SEARCH_BY_LABEL, label);
        }
        catch (SearchException se) {
            // Can't query the database.
            super.log(ExceptionUtils.getStackTrace(se));
            super.addError("error.search", se.getMessage());
            super.saveErrors(request);
        }
        // result is not empty if we have this label on the database.
        if (!result.isEmpty()) {
            super.addError("error.create",
                label + " you entered is not unique! Label must be unique.");
            super.saveErrors(request);
        }
        // Quit if errors ocurred.
        if (super.hasErrors()) {
            return;
        }
        // Found a unique short label and topic combination; create a new CV.
        CvObject cvobj = createNew(classname);
        cvobj.setShortLabel(label);
        cvobj.setOwner(owner);

        try {
            // Begin the transaction.
            user.begin();
            // Create the new object on the persistence system.
            user.create(cvobj);
            // Commit all the changes.
            user.commit();
            // Set the new object as the current edit object.
            user.setCurrentEditObject(cvobj);
            // Added a new CV object; update the drop down list.
            user.refreshList();
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
            // Error with creating the new CV object changes.
            super.addError("error.create", ie1.getMessage());
            super.saveErrors(request);
        }
        catch (SearchException se) {
            // Log the stack trace.
            super.log(ExceptionUtils.getStackTrace(se));
            // Error with updating the drop down lists.
            super.addError("error.search.list", se.getMessage());
            super.saveErrors(request);
        }
    }

    /**
     * Construct a new instance for given class name using Java reflection.
     */
    private CvObject createNew(String classname) {
        // Create an instance of given classname by useing reflection.
        CvObject cvobj = null;
        try {
            Class clazz = Class.forName(classname);
            cvobj = (CvObject) clazz.newInstance();
        }
        catch (IllegalAccessException iae) {
            // Shouldn't happen.
            assert false;
        }
        catch (ClassNotFoundException cnfe) {
            // Shouldn't happen.
            assert false;
        }
        catch (InstantiationException ie) {
            // Shouldn't happen.
            assert false;
        }
        return cvobj;
    }
}
