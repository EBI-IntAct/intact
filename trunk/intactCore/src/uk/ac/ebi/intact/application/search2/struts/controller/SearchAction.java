/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.struts.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.search2.business.IntactUserIF;
import uk.ac.ebi.intact.application.search2.business.Constants;
import uk.ac.ebi.intact.application.search2.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search2.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.commons.search.SearchHelper;
import uk.ac.ebi.intact.application.commons.search.CriteriaBean;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.AnnotatedObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

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

    private static final Logger logger = Logger.getLogger( Constants.LOGGER_NAME );

    /**
     * The search order. The search is done in this order.
     */
//    public static final String[] SEARCH_ORDER = new String[]{
//        "Protein", "Interaction", "Experiment"
//    };

    public static final ArrayList SEARCH_CLASSES = new ArrayList(3);
    static {
        SEARCH_CLASSES.add( "Protein" );
        SEARCH_CLASSES.add( "Interaction" );
        SEARCH_CLASSES.add( "Experiment" );
    }

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

        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);
        if(user == null) {
            //browser page caching screwed up the session - need to
            //get a user object created again by forwarding to welcome action...
            return mapping.findForward(SearchConstants.FORWARD_SESSION_LOST);
        }

        DynaActionForm dyForm = (DynaActionForm) form;
        String searchValue = (String) dyForm.get("searchString");
        String searchClass = (String) dyForm.get("searchClass");

        user.setSearchValue( searchValue );
        user.setSearchClass( searchClass );

        System.out.println("searchValue: " + searchValue);
        System.out.println("searchClass: " + searchClass);

        //reset the class string in the form for the next request
        dyForm.set("searchClass", "");

        //clean out previous single object views
        session.setAttribute(SearchConstants.VIEW_BEAN, null);

        // Holds the result from the initial search.
        Collection results = null;

        //now need to try searches based on AC, label or name (only
        //ones we will accept for now) and return as soon as we get a result
        //NB obviously can't distinguish between a zero return and a search
        //with garbage input using this approach...
        super.log("search action: attempting to search by AC first...");
        try {
            SearchHelper searchHelper = new SearchHelper( logger );
            boolean noClass = false;
            if (searchClass == null || searchClass.length() == 0) {
                results = searchHelper.doLookup( SEARCH_CLASSES, searchValue, user );
                noClass = true;
            } else {
                results = searchHelper.doLookup( searchClass, searchValue, user );
            }

            if (results.isEmpty()) {
                //now try all lower case....
                String lowerCaseValue = searchValue.toLowerCase();
                if ( noClass ) {
                    results = searchHelper.doLookup( SEARCH_CLASSES, lowerCaseValue, user );
                    noClass = true;
                } else {
                    results = searchHelper.doLookup( searchClass, lowerCaseValue, user );
                }

                if (results.isEmpty()) {
                    //finished all current options, and still nothing - return a failure
                    super.log("No matches were found for the specified search criteria");

                    // Save the search parameters for results page to display.
                    session.setAttribute( SearchConstants.SEARCH_CRITERIA,
                                          searchHelper.getSearchCritera() + "=" + searchValue);

                    return mapping.findForward( SearchConstants.FORWARD_NO_MATCHES );
                }
            }
            super.log("search action: search results retrieved OK");

            // ************* Search was a success. ********************************

            // Save the search parameters for results page to display.
            Collection criterias = searchHelper.getSearchCritera();
            StringBuffer buf = new StringBuffer( 128 );
            Iterator it = criterias.iterator ();
            CriteriaBean criteriaBean = (CriteriaBean) it.next ();
            buf.append( criteriaBean.getTarget() ).append( '=' ).append( criteriaBean.getQuery() );
            while (  it.hasNext () ) {
                criteriaBean = (CriteriaBean) it.next ();
                buf.append( ", " ).append( criteriaBean.getTarget() ).append( '=' ).append( criteriaBean.getQuery() );
            }

            session.setAttribute( SearchConstants.SEARCH_CRITERIA, buf.toString() );


            super.log("found results - forwarding to relevant Action for processing...");

            for (Iterator iterator = results.iterator(); iterator.hasNext();) {
                AnnotatedObject annotatedObject = (AnnotatedObject) iterator.next();
                System.out.println("Search result: " + annotatedObject.getShortLabel());
            }
            request.setAttribute(SearchConstants.SEARCH_RESULTS, results);

            //set the original search criteria into the session for use by
            //the view action - needed because if the 'back' button is used from
            //single object views, the original search details are lost
            session.setAttribute(SearchConstants.LAST_VALID_SEARCH, searchValue);

            // dispatch to the relevant action
            String relativeHelpLink = getServlet ().getServletContext().getInitParameter("helpLink");

            //build the help link out of the context path - strip off the 'search' bit...
            String ctxtPath = request.getContextPath();
            String relativePath = ctxtPath.substring(0, ctxtPath.lastIndexOf("search"));
            String helpLink = relativePath.concat(relativeHelpLink);
            user.setHelpLink(helpLink);
            return mapping.findForward(SearchConstants.FORWARD_DISPATCHER_ACTION);
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
}