/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import java.util.*;

import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.*;

import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.application.cvedit.struts.view.*;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.persistence.SearchException;

/**
 * This class provides the actions required for the search page
 * for intact. The search criteria are obtained from a Form object
 * and then the search is carried out, via the IntactUser functionality,
 * which provides the business logic.
 *
 * @author Chris Lewington, Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */

public class SearchAction extends IntactBaseAction {

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
        // Clear any previous errors.
        super.clearErrors();

        // Set up variables used during searching
        String searchParam = null;
        String searchValue = null;

        SearchForm theForm = (SearchForm) form;

        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);

        // The topic selected by the user.
        String topic = user.getSelectedTopic();

        // The class name associated with the topic.
        String classname = super.getIntactService().getClassName(topic);

        if (theForm.searchByAc()) {
            // Search by AC.
            super.log("search by AC requested");
            searchParam = WebIntactConstants.SEARCH_BY_AC;
            searchValue = theForm.getAcNumber();
        }
        else if (theForm.searchByLabel()) {
           // Search by short label.
           searchParam = WebIntactConstants.SEARCH_BY_LABEL;
           searchValue = theForm.getLabel();
        }
        else if (theForm.createNew()) {
            super.log("creating a new CV object");

            // Retrieve all the short labels.
            Collection labels = null;
            try {
                labels = getAllShortLabels(user, classname);
            }
            catch (SearchException se) {
                // Something failed during search...
                super.log(ExceptionUtils.getStackTrace(se));
                // The errors to report back.
                super.addError("error.search", se.getNestedMessage());
                super.saveErrors(request);
                return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
            }
            //  Store the short lables in a session for jsp.
            session.setAttribute(WebIntactConstants.SHORT_LABEL_BEAN, labels);
            return mapping.findForward(WebIntactConstants.FORWARD_CREATE);
        }
        else {
            // Unknown action.
            assert false;
        }

        //super.log("search action: action value is " + action);
        super.log("search action: search param is " + searchParam);
        super.log("search action: search value is " + searchValue);
        super.log("search action: topic is " + topic);

        // Remove the current edit object from the cache to retrieve
        // only the committed data.
        user.removeFromCache(user.getCurrentEditObject());

        // Holds the result from the search.
        Collection results = null;

        try {
            results = user.search(classname, searchParam, searchValue);
        }
        catch (SearchException se) {
            // Something failed during search...
            super.log(ExceptionUtils.getStackTrace(se));
            // The errors to report back.
            super.addError("error.search", se.getNestedMessage());
            super.saveErrors(request);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
        if (results.isEmpty()) {
            // No matches found - forward to a suitable page
            super.log("No matches were found for the specified search criteria");
            // The errors to report back.
            super.addError("error.no.match");
            super.saveErrors(request);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
        //super.log("search action: search results retrieved OK");
        // Save the serahc parameters for results page to display.
        session.setAttribute(WebIntactConstants.SEARCH_CRITERIA,
            searchParam + "=" + searchValue);

        // If we retrieved one object then we can go strainght to edit page.
        if (results.size() == 1) {
            // Found a single match only; save it to determine which page to
            // return from edit.jsp; for example, results jsp or search jsp.
            session.setAttribute(WebIntactConstants.SINGLE_MATCH, Boolean.TRUE);

            // The object to edit.
            CvObject cvobj = (CvObject) results.iterator().next();
            user.setCurrentEditObject(cvobj);

            CvViewBean viewbean = super.getViewBean(session);
            viewbean.initialise(cvobj);
            viewbean.setTopic(topic);
            session.setAttribute(WebIntactConstants.VIEW_BEAN, viewbean);
            // Straight to the edit jsp.
            return mapping.findForward(WebIntactConstants.FORWARD_EDIT);
        }
        // Found multiple results.
        session.setAttribute(WebIntactConstants.SINGLE_MATCH, Boolean.FALSE);

        // The collection to hold our List objects for display tag API.
        Collection container = new ArrayList();
        for (Iterator iter = results.iterator(); iter.hasNext();) {
            container.add(new ListObject((CvObject) iter.next()));
        }
        // Save it in a session for a JSP to display.
        session.setAttribute(WebIntactConstants.FORWARD_MATCHES, container);

        // Move to the results page.
        return mapping.findForward(WebIntactConstants.FORWARD_RESULTS);
    }

    private Collection getAllShortLabels(IntactUserIF user, String classname)
        throws SearchException {
        // The collection to return.
        Collection labels = new ArrayList();

        // Holds the result from the search.
        Collection results = user.search(classname,
            WebIntactConstants.SEARCH_BY_LABEL, "*");

        for (Iterator iter = results.iterator(); iter.hasNext();) {
            ShortLabelObject labelobj = new ShortLabelObject();

            // Only MAX_COLS at a time.
            for (int i = 0; (i < ShortLabelObject.MAX_COLS) && iter.hasNext();
                 i++) {
                labelobj.setValue(i, ((CvObject) iter.next()).getShortLabel());
            }
            labels.add(labelobj);
        }
        return labels;
    }
}
