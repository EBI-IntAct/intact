/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import uk.ac.ebi.intact.application.search.business.IntactUserIF;
import uk.ac.ebi.intact.application.search.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.Protein;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class provides the actions required to carry out search operations
 * for intact via a web-based interface. The search criteria are obtained from a
 * Form object and then the search is carried out, via the IntactUser functionality,
 * which provides the business logic. Once a search has been carried out, this class will
 * forward to another Action class as appropriate to generate specific types of
 * data that will later be displayed via the View classes.
 *
 * @author Chris Lewington (clewing@ebi.ac.uk)
 * @version $Id$
 */

public class SearchAction extends IntactBaseAction {

    /**
     * The search order. The search is done in this order.
     */
    public static final String[] SEARCH_ORDER = new String[]{
        "Protein", "Interaction", "Experiment"
    };

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
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws  Exception {

        // Clear any previous errors.
        super.clearErrors();

        DynaActionForm dyForm = (DynaActionForm) form;
        String searchValue = (String) dyForm.get("searchString");
        String searchClass = (String) dyForm.get("searchClass");

        //reset the class string in the form for the next request
        dyForm.set("searchClass", "");

        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        //clean out previous single object views
        session.setAttribute(SearchConstants.SINGLE_OBJ_VIEW_BEAN, null);

        //also clean out any bean filter sets here??...

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);
        if(user == null) {
            //browser page caching screwed up the session - need to
            //get a user object created again by forwarding to welcome action...
            return mapping.findForward(SearchConstants.FORWARD_SESSION_LOST);

        }

        // Holds the result from the initial search.
        Collection results = null;

        //now need to try searches based on AC, label or name (only
        //ones we will accept for now) and return as soon as we get a result
        //NB obviously can't distinguish between a zero return and a search
        //with garbage input using this approach...
        super.log("search action: attempting to search by AC first...");
        try {

            results = doLookup(searchClass, searchValue, user);
            if (results.isEmpty()) {
                //try searching first using all uppercase, then all lower case if it returns nothing...
                //NB this would be better done at the DB level, but keep it here for now
                String upperCaseValue = searchValue.toUpperCase();
                results = doLookup(searchClass, upperCaseValue, user);
                if (results.isEmpty()) {

                    //now try all lower case....
                    String lowerCaseValue = searchValue.toLowerCase();
                    results = doLookup(searchClass, lowerCaseValue, user);
                    if (results.isEmpty()) {
                        //finished all current options, and still nothing - return a failure
                        super.log("No matches were found for the specified search criteria");
                        // Save the search parameters for results page to display.
                        session.setAttribute(SearchConstants.SEARCH_CRITERIA,
                                user.getSearchCritera() + "=" + searchValue);
                        return mapping.findForward(SearchConstants.FORWARD_NO_MATCHES);
                    }

                }
            }
            super.log("search action: search results retrieved OK");

            // ************* Search was a success. ********************************

            // Save the search parameters for results page to display.
            session.setAttribute(SearchConstants.SEARCH_CRITERIA,
                    user.getSearchCritera() + "=" + searchValue);
            super.log("found results - forwarding to relevant Action for processing...");
            request.setAttribute(SearchConstants.SEARCH_RESULTS, results);

            //Two places to go now:
            //1) do a single item view if the request was from a link rather than a search
            //   query (ie a searchClass was specified) - DOES NOT apply to Interactions,
            // because we wnat to see the Protein shortlabels in that case (ie normal view);
            //2) All other requests go to the 'detail view' for the complex processing
            if((results.size() == 1) & (!searchClass.equals("")) & (!searchClass.equals("Interaction"))) {
                System.out.println("single view requested from page link (type given)...");
                return mapping.findForward(SearchConstants.FORWARD_SINGLE_ACTION);

            }
            else {

                //set the original search criteria into the session for use by
                //the view action - needed because if the 'back' button is used from
                //single object views, the original search details are lost
                session.setAttribute(SearchConstants.LAST_VALID_SEARCH, searchValue);
                return mapping.findForward(SearchConstants.FORWARD_DETAIL_ACTION);
             }
        }
        catch (IntactException se) {
            // Something failed during search...
            super.log(ExceptionUtils.getStackTrace(se));
            super.log(se.getNestedMessage());
            super.log(se.getRootCause().toString());
            super.log(se.getLocalizedMessage());
            // The errors to report back.
            super.addError("error.search", se.getMessage());
            super.saveErrors(request);
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }
    }

    // ----------------------------- Helper methods ------------------------------

    /**
     * utility method to handle the logic for lookup, ie trying AC, label etc.
     * Isolating it here allows us to change initial strategy if we want to.
     * NB this will probably be refactored out into the IntactHelper class later on.
     *
     * @param searchClass The class to search on (only comes from a link clink) - useful for optimizing
     * search
     * @param value the user-specified value
     * @param user The object holding the IntactHelper for a given user/session
     * (passed as a parameter to avoid using an instance variable, which may
     *  cause thread problems).
     *
     * @return Collection the results of the search - an empty Collection if no results found
     *
     * @exception IntactException thrown if there were any search problems
     */
    private Collection doLookup(String searchClass, String value, IntactUserIF user)
            throws IntactException {

        Collection results = new ArrayList();
        String packageName = AnnotatedObject.class.getPackage().getName() + ".";
        if(searchClass.length() == 0) {

            //have to go through the possible classes in order...
            for (int i = 0; i < SEARCH_ORDER.length; i++) {
                // The class name associated with the search request.
                String className = packageName + SEARCH_ORDER[i];
                //try search on AC first...
                results = doSearch(className, value, user);
                if (!results.isEmpty()) {
                    super.log("found search match - class: " + className +", value: " + value);
                    break;
                }
            }
        }
        else {
            super.log("className supplied in request - going straight to search...");
            String className = packageName + searchClass;
            super.log("attempting search for " + className + " with value " + value);
            results = doSearch(className, value, user);
            if(results.isEmpty()) {
                super.log("no search results found for class: " + className +", value: " + value);
            }
            else {
                super.log("found search match - class: " + className +", value: " + value);
            }
        }
        return results;
    }

    /**
         * utility method to handle the logic for lookup, ie trying AC, label etc.
         * Isolating it here allows us to change initial strategy if we want to.
         * NB this will probably be refactored out into the IntactHelper class later on.
         *
         * @param className The class to search on (only comes from a link clink) - useful for optimizing
         * search
         * @param value the user-specified value
         * @param user The object holding the IntactHelper for a given user/session
         * (passed as a parameter to avoid using an instance variable, which may
         *  cause thread problems).
         *
         * @return Collection the results of the search - an empty Collection if no results found
         *
         * @exception IntactException thrown if there were any search problems
         */
        private Collection doSearch(String className, String value, IntactUserIF user)
                throws IntactException {

            Collection results = new ArrayList();


        //try search on AC first...
        results = user.search(className, "ac", value);
        if (results.isEmpty()) {
            // No matches found - try a search by label now...
            super.log("no match found for " + className + " with ac= " + value);
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
