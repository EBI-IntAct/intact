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
import uk.ac.ebi.intact.model.Xref;
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
   public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // Clear any previous errors.
        super.clearErrors();

        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);

        // The form to access input data.
        DynaActionForm theForm = (DynaActionForm) form;

        // The topic selected by the user.
        String topic = (String) theForm.get("topic");
        user.setSelectedTopic(topic);

        String searchString = (String) theForm.get("searchString");

        super.log("search action: topic is " + topic);

        // The class name associated with the topic.
        String classname = super.getIntactService().getClassName(topic);

        // Holds the result from the search.
        Collection results = null;

        try {
            // try searching first using all uppercase, then all lower case if it returns nothing...
            // NB this would be better done at the DB level, but keep it here for now
            String upperCaseValue = searchString.toUpperCase();
            results = doLookup(classname, upperCaseValue, user);
            if (results.isEmpty()) {
                // now try all lower case....
                String lowerCaseValue = searchString.toLowerCase();
                results = doLookup(classname, lowerCaseValue, user);
            }
        }
        catch (SearchException ie) {
            // Something failed during search...
            super.log(ExceptionUtils.getStackTrace(ie));
            // The errors to report back.
            super.addError("error.search", ie.getNestedMessage());
            super.saveErrors(request);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
        if (results.isEmpty()) {
            // No matches found - forward to a suitable page
            super.log("No matches were found for the specified search criteria");
            return mapping.findForward(WebIntactConstants.FORWARD_NO_MATCHES);
        }
        // Sets the result status type.
        user.setSearchResultStatus(results.size());

        // If we retrieved one object then we can go strainght to edit page.
        if (results.size() == 1) {
            // The object to edit.
            CvObject cvobj = (CvObject) results.iterator().next();
            user.setCurrentEditObject(cvobj);

            // Straight to the edit jsp.
            return mapping.findForward(WebIntactConstants.FORWARD_EDIT);
        }
        // Cache the search results.
        user.cacheSearchResult(results);

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

    /**
     * utility method to handle the logic for lookup, ie trying AC, label etc.
     * Isolating it here allows us to change initial strategy if we want to.
     * NB this will probably be refactored out into the IntactHelper class later on.
     *
     * @param className the intact type to search on
     * @param value the user-specified value
     * @param user The object holding the IntactHelper for a given user/session
     * (passed as a parameter to avoid using an instance variable, which may
     *  cause thread problems).
     * @return Collection the results of the search - an empty Collection if no results found
     * @exception SearchException thrown if there were any search problems
     */
    private Collection doLookup(String className, String value, IntactUserIF user)
        throws SearchException {

        Collection results = new ArrayList();

        //try search on AC first...
        results = user.search(className, "ac", value);
        if (results.isEmpty()) {
            // No matches found - try a search by label now...
            super.log("now searching for class " + className + " with label " + value);
            results = user.search(className, "shortLabel", value);
            if (results.isEmpty()) {
                //no match on label - try by xref....
                super.log("no match on label - looking for: " + className + " with primary xref ID " + value);
                Collection xrefs = user.search(Xref.class.getName(), "primaryId", value);

                //could get more than one xref, eg if the primary id is a wildcard search value -
                //then need to go through each xref found and accumulate the results...
                Iterator it = xrefs.iterator();
                Collection partialResults = new ArrayList();
                while (it.hasNext()) {
                    partialResults = user.search(className, "ac", ((Xref) it.next()).getParentAc());
                    results.addAll(partialResults);
                }

                if (results.isEmpty()) {
                    //no match by xref - try finally by name....
                    super.log("no matches found using ac, shortlabel or xref - trying fullname...");
                    results = user.search(className, "fullName", value);
                }
            }
        }
        return results;
    }
}
