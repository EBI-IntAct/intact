/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.controller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import uk.ac.ebi.intact.application.commons.search.SearchHelper;
import uk.ac.ebi.intact.application.search3.business.Constants;
import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search3.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;

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

    //TODO: do we need a limit for CvObjects also here?
    public static final int MAXIMUM_DISPLAYABLE_INTERACTION = 50;
    public static final int MAXIMUM_DISPLAYABLE_PROTEIN     = 30;

    /**
     * The search order. The search is done in this order.
     * NB (Aug 2004): With the new front page these will be searched
     * individually to get ALL matches, not just the first one.
     */
    public static final ArrayList SEARCH_CLASSES = new ArrayList(4);
    static {
        SEARCH_CLASSES.add( "Protein" );
        SEARCH_CLASSES.add( "Interaction" );
        SEARCH_CLASSES.add( "Experiment" );
        SEARCH_CLASSES.add( "CvObject" );   //Note that this is ABSTRACT....
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

        // Session to access various session objects. This will create
        //a new session if one does not exist.
        HttpSession session = super.getSession(request);

        // Handle to the Intact User.
        IntactUserIF user = super.getIntactUser(session);
        if(user == null) {
            //just set up a new user for the session - if it fails, need to give up!
            user = super.setupUser(request);
            if(user == null) return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }

        //set up a highlight list - needs to exist in ALL cases to avoid
        //JSPs having to check for 'null' all the time...
        List labelList = (List)request.getAttribute(SearchConstants.HIGHLIGHT_LABELS_LIST);
        if(labelList == null) labelList = new ArrayList();
        else labelList.clear();     //set one up or wipe out an existing one

        //first check for a tabbed page request - no need to search in this case
        String selectedPage = request.getParameter("selectedChunk");
        if((selectedPage != null) && (!selectedPage.equals(""))) {

            //the dispatcher can forward to the detail action
            //and it can process existing view beans...
            return mapping.findForward(SearchConstants.FORWARD_DISPATCHER_ACTION);
        }

        DynaActionForm dyForm = (DynaActionForm) form;
        String searchValue   = (String) dyForm.get( "searchString") ;
        String searchClass   = (String) dyForm.get( "searchClass" );
        String selectedChunk = (String) dyForm.get( "selectedChunk" );

        //this tabbed stuff is for handling subsequent page requests from tabbed JSPs
        int selectedChunkInt = Constants.NO_CHUNK_SELECTED;
        try {
            if ( null != selectedChunk && !selectedChunk.equals("") ) {
                selectedChunkInt = Integer.parseInt( selectedChunk );

            }
        } catch( NumberFormatException nfe ) {
            logger.warn( "The selected chunk is not an Integer value, it can't be parsed.", nfe );
        }

        //set a few useful user beans
        user.setSearchValue( searchValue );
        user.setSearchClass( searchClass );
        user.setSelectedChunk( selectedChunkInt );

        logger.info( "searchValue: " + searchValue);
        logger.info( "searchClass: " + searchClass);
        logger.info( "selectedChunk: " + (selectedChunkInt == Constants.NO_CHUNK_SELECTED ? "none" : selectedChunk));

        //reset the class string in the form for the next request
        dyForm.set("searchClass", "");
        dyForm.set("selectedChunk", "-1");

        //clean out previous single object views
        session.setAttribute( SearchConstants.VIEW_BEAN, null );

        // Holds the result from the initial search.
        Collection results = null;

        //now need to try searches based on AC, label, name or xref (only
        //ones we will accept for now)...

        //IMPORTANT (Aug 2004):
        //The new search interface defines a new front page - this implies that
        //all requests with NO TYPE SPECIFIED should have results sent to the new page as this
        //is the only request type that can have an empty type now.
        //CONSEQUENCE:
        //The new page can have multiple result types to display, but others cannot.
        //Main impact of change is on Protein searches - previously a 'no type' request with
        //a Protein result was sent to the binary view. Now this should come from the front
        //page, OR a single Protein detail view request from a DIFFERENT page.

        //NB obviously can't distinguish between a zero return and a search
        //with garbage input using this approach.
        logger.info( "search action: attempting to search by AC first..." );
        try {

            //first save the search parameters for results page to display.
            session.setAttribute( SearchConstants.SEARCH_CRITERIA, "'" + searchValue + "'");

            //try the specified String case first
            SearchHelper searchHelper = new SearchHelper( logger );
            results = this.getResults(searchHelper, searchClass, searchValue, user );

            if (results.isEmpty()) {
                //now try all lower case....
                String lowerCaseValue = searchValue.toLowerCase();
                results = this.getResults(searchHelper, searchClass, lowerCaseValue, user );

                if (results.isEmpty()) {
                    //finished all current options, and still nothing - return a failure
                    logger.info( "No matches were found for the specified search criteria" );
                    return mapping.findForward( SearchConstants.FORWARD_NO_MATCHES );
                }
            }
            logger.info("search action: search results retrieved OK");

            // ************* Search was a success. ********************************

            logger.info("found results - forwarding to relevant Action for processing...");

            //determine the shortlabel highlighting list for display...
            AnnotatedObject obj = null;
            logger.info("building highlight list...");
            for (Iterator iterator = results.iterator(); iterator.hasNext();) {
                obj = (AnnotatedObject)iterator.next();
                labelList.add(obj.getShortLabel());
                logger.info("Search result: " + obj.getShortLabel());
            }

            //put both the results and also a list of the shortlabels for highlighting into the request
            request.setAttribute(SearchConstants.SEARCH_RESULTS, results);
            request.setAttribute(SearchConstants.HIGHLIGHT_LABELS_LIST, labelList);

            //set the original search criteria into the session for use by
            //the view action - needed because if the 'back' button is used from
            //single object views, the original search beans are lost
            session.setAttribute(SearchConstants.LAST_VALID_SEARCH, searchValue);

            // dispatch to the relevant action
            String relativeHelpLink = getServlet().getServletContext().getInitParameter( "helpLink" );

            //build the help link out of the context path - strip off the 'search' bit...
            String ctxtPath = request.getContextPath();
            String relativePath = ctxtPath.substring( 0, ctxtPath.lastIndexOf( "search" ) );
            String helpLink = relativePath.concat( relativeHelpLink );
            user.setHelpLink( helpLink );
            return mapping.findForward( SearchConstants.FORWARD_DISPATCHER_ACTION );
        }
        catch ( IntactException se ) {
            // Something failed during search...
            logger.info( se );
            logger.info(se.getNestedMessage());
            logger.info(se.getRootCause().toString());
            logger.info(se.getLocalizedMessage());

            // clear in case there is some old errors in there.
            super.clearErrors();

            // The errors to report back.
            super.addError( "error.search", se.getMessage() );
            super.saveErrors( request );
            return mapping.findForward( SearchConstants.FORWARD_FAILURE  );
        }
    }

    //---------------------- helper methods ------------------------------------

    /**
     * Decides how to perform the search, based upon whether or not the intact type
     * has been specified. If not then all the currently defined search types will be
     * iterated through to collect all matches. If the type is specified then the search request
     * must have come from a JSP and so only single-type results are possible.
     * NB this is different to before: Protein 'no type' requests used to go to a binary
     * view, but now this view results from a request from the main JSP result page.
     * @param helper The search helper instance that will do the searching for us
     * @param searchClass The class to search on - may be null or empty
     * @param searchValue The value to search on
     * @param user The Intact user object (needed by the search helper)
     * @return Collection A Collection of the results, or empty if none found.
     * @throws IntactException Thrown if there was a searching problem
     */
    private Collection getResults(SearchHelper helper, String searchClass,
                                  String searchValue, IntactUserIF user) throws IntactException {

        Collection result = null;
        if (searchClass == null || searchClass.length() == 0) {

            //must be an initial search request, with possible multiple type results.
            //In this case we should NOT finish after the first match, but continue
            //searching across ALL SPECIFIED TYPES...
            result = new ArrayList();
            for (Iterator it = SEARCH_CLASSES.iterator(); it.hasNext();) {
                result.addAll(helper.doLookup((String) it.next(), searchValue, user));
            }

        }
        else {
            //request must have come from a JSP link
            result = helper.doLookup(searchClass, searchValue, user);
        }

        return result;
    }
}