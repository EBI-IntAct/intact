/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvAddForm;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvViewBean;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
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
    public ActionForward perform (ActionMapping mapping, ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response ) {
        // Need the form to get data entered by the user.
        CvAddForm theForm = (CvAddForm) form;

        // Clear any previous errors.
        super.clearErrors();

        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        if (theForm.isSubmitted()) {
            super.log("Form is submitted");
            // Check for the short label; it must be unique.
            String label = theForm.getShortLabel();

            // Handler to the Intact User.
            IntactUserIF user = super.getIntactUser(session);

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
                return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
            }
            // result is not empty if we have this label on the database.
            if (!result.isEmpty()) {
                super.addError("error.create",
                    label + " you entered is not unique! Label must be unique.");
                super.saveErrors(request);
                return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
            }
            // Found a unique short label and topic combination; create a new CV.
            CvObject cvobj = createNew(classname);
            cvobj.setShortLabel(label);
            cvobj.setOwner(owner);

            try {
                user.create(cvobj);
            }
            catch (IntactException ce) {
                super.log(ExceptionUtils.getStackTrace(ce));
                super.addError("error.create", ce.getMessage());
                super.saveErrors(request);
                return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
            }
            // Need to set the new CV object in a view bean to display on the
            // screen for it to display.
            CvViewBean viewbean = super.getViewBean(session);
            viewbean.initialise(cvobj);
            viewbean.setTopic(topic);
            session.setAttribute(WebIntactConstants.VIEW_BEAN, viewbean);
            // Straight to the edit jsp.
            return mapping.findForward(WebIntactConstants.FORWARD_EDIT);
        }
        // Cancel assumed.
        super.log("Form is cancelled");
        // Back to the search page.
        return mapping.findForward(WebIntactConstants.FORWARD_SEARCH);
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
