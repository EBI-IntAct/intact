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
import uk.ac.ebi.intact.application.commons.search.ResultWrapper;
import uk.ac.ebi.intact.application.commons.search.SearchHelper;
import uk.ac.ebi.intact.application.search3.business.Constants;
import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search3.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.util.SearchValidator;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.AnnotatedObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides the actions required to carry out search operations for intact via a
 * web-based interface. The search criteria are obtained from a Form object and then the search is
 * carried out, via the IntactUser functionality, which provides the business logic. Once a search
 * has been carried out, this class will forward to another Action class as appropriate to generate
 * specific types of data that will later be displayed via the View classes.
 *
 * @author Chris Lewington (clewing@ebi.ac.uk)
 * @version $Id$
 */

public class SearchAction extends IntactBaseAction {
    /**
     * this value is needed for the result wrapper
     */
    public static final int MAXIMUM_RESULT_OBJECT = 50;
    /**
     *
     */
    public static final int MAXIMUM_DISPLAYABLE_INTERACTION = 50;
    /**
     * 
     */
    public static final int MAXIMUM_DISPLAYABLE_PROTEIN = 30;


    /**
     * Process the specified HTTP request, and create the corresponding HTTP response (or forward to
     * another web component that will create it). Return an ActionForward instance describing where
     * and how control should be forwarded, or null if the response has already been completed.
     *
     * @param mapping  - The <code>ActionMapping</code> used to select this instance
     * @param form     - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request  - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     * @return - represents a destination to which the controller servlet,
     *         <code>ActionServlet</code>, might be directed to perform a
     *         RequestDispatcher.forward() or HttpServletResponse.sendRedirect() to, as a result of
     *         processing activities of an <code>Action</code> class
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {


        // Clear any previous errors.
        super.clearErrors();

        // Session to access various session objects. This will create
        //a new session if one does not exist.
        HttpSession session = super.getSession(request);

        // Handle to the Intact User.
        IntactUserIF user = super.getIntactUser(session);
        if (user == null) {
            //just set up a new user for the session - if it fails, need to give up!
            user = super.setupUser(request);
            if (user == null) return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }

        //set up a highlight list - needs to exist in ALL cases to avoid
        //JSPs having to check for 'null' all the time...
        List labelList = (List) request.getAttribute(SearchConstants.HIGHLIGHT_LABELS_LIST);
        if (labelList == null)
            labelList = new ArrayList();
        else
            labelList.clear();     //set one up or wipe out an existing one

        //first check for a tabbed page request - no need to search in this case
        String selectedPage = request.getParameter("selectedChunk");
        if ((selectedPage != null) && (!selectedPage.equals(""))) {

            //the dispatcher can forward to the detail action
            //and it can process existing view beans...
            return mapping.findForward(SearchConstants.FORWARD_DISPATCHER_ACTION);
        }

        DynaActionForm dyForm = (DynaActionForm) form;
        String searchValue = (String) dyForm.get("searchString");
        String searchClass = (String) dyForm.get("searchClass");
        String selectedChunk = (String) dyForm.get("selectedChunk");
        String binaryValue = (String) dyForm.get("binary");


        //this tabbed stuff is for handling subsequent page requests from tabbed JSPs
        int selectedChunkInt = Constants.NO_CHUNK_SELECTED;
        try {
            if (null != selectedChunk && !selectedChunk.equals("")) {
                selectedChunkInt = Integer.parseInt(selectedChunk);

            }
        } catch (NumberFormatException nfe) {
            logger.warn("The selected chunk is not an Integer value, it can't be parsed.", nfe);
        }

        //set a few useful user beans
        user.setSearchValue(searchValue);
        user.setSearchClass(searchClass);
        user.setSelectedChunk(selectedChunkInt);
        user.setBinaryValue(binaryValue);


        logger.info("searchValue: " + searchValue);
        logger.info("searchClass: " + searchClass);
        logger.info("selectedChunk: " +
                (selectedChunkInt == Constants.NO_CHUNK_SELECTED ? "none" : selectedChunk));
        logger.info("binaryValue: " + binaryValue);



        //reset the class string in the form for the next request
        dyForm.set("searchClass", "");
        dyForm.set("selectedChunk", "-1");

        //clean out previous single object views
        session.setAttribute(SearchConstants.VIEW_BEAN, null);

        // Holds the result from the initial search.
        ResultWrapper results = null;

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
        logger.info("Classname = " + searchClass);

        try {

            //first save the search parameters for results page to display.
            session.setAttribute(SearchConstants.SEARCH_CRITERIA, "'" + searchValue + "'");

            if (searchValue.equalsIgnoreCase("%") || searchValue.equalsIgnoreCase("")) {
                return mapping.findForward(SearchConstants.FORWARD_NO_MATCHES);
            }

            /**  if (!SearchValidator.isValid(searchValue)) {
             return mapping.findForward(SearchConstants.FORWARD_NO_MATCHES);
             }

             **/

            SearchHelper searchHelper = new SearchHelper(logger);

            // if it's a binary request first look for this one

            if (binaryValue != null && !binaryValue.equals("")) {
                // it's an binary request
                // convert to binary query to a normal one 
                binaryValue = binaryValue.replaceAll("\\,%20", ",");
                results = this.getResults(searchHelper, null, binaryValue, user);
                session.setAttribute(SearchConstants.SEARCH_CRITERIA, "'" + binaryValue + "'");


            } else {
                //try now the specified String case first
                results = this.getResults(searchHelper, searchClass, searchValue, user);
            }

            if (results.isTooLarge()) {

                logger.info("Results set is too Large for the specified search criteeria");
                request.setAttribute(SearchConstants.SEARCH_CRITERIA, "'" + searchValue + "'");
                request.setAttribute(SearchConstants.RESULT_INFO, results.getInfo());
                return mapping.findForward(SearchConstants.FORWARD_TOO_LARGE);

            }
            if (results.isEmpty()) {
                //finished all current options, and still nothing - return a failure
                logger.info("No matches were found for the specified search criteria");
                return mapping.findForward(SearchConstants.FORWARD_NO_MATCHES);
            }

            logger.info("search action: search results retrieved OK");

            // ************* Search was a success. ********************************

            logger.info("found results - forwarding to relevant Action for processing...");

            //determine the shortlabel highlighting list for display...
            AnnotatedObject obj = null;
            logger.info("building highlight list...");
            for (Iterator iterator = results.getResult().iterator(); iterator.hasNext();) {
                obj = (AnnotatedObject) iterator.next();
                logger.info("Search result: " + obj.getShortLabel());
                labelList.add(obj.getShortLabel());
                logger.info("Search result: " + obj.getShortLabel());
            }

            //put both the results and also a list of the shortlabels for highlighting into the request
            // TODO CHANGE THIS .GETLIST !!!!
            request.setAttribute(SearchConstants.SEARCH_RESULTS, results.getResult());
            request.setAttribute(SearchConstants.HIGHLIGHT_LABELS_LIST, labelList);

            //set the original search criteria into the session for use by
            //the view action - needed because if the 'back' button is used from
            //single object views, the original search beans are lost
            session.setAttribute(SearchConstants.LAST_VALID_SEARCH, searchValue);

            // dispatch to the relevant action
            String relativeHelpLink = getServlet().getServletContext().getInitParameter("helpLink");

            //build the help link out of the context path - strip off the 'search' bit...
            String ctxtPath = request.getContextPath();
            String relativePath = ctxtPath.substring(0, ctxtPath.lastIndexOf("search"));
            String helpLink = relativePath.concat(relativeHelpLink);
            user.setHelpLink(helpLink);
            return mapping.findForward(SearchConstants.FORWARD_DISPATCHER_ACTION);
        } catch (IntactException se) {
            logger.info("something went wrong ...");
            // Something failed during search...
            logger.info(se);
            logger.info(se.getNestedMessage());
            logger.info(se.getRootCause().toString());
            logger.info(se.getLocalizedMessage());


            // clear in case there is some old errors in there.
            super.clearErrors();

            // The errors to report back.
            super.addError("error.search", se.getMessage());
            super.saveErrors(request);
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);

        }
    }

    //---------------------- helper methods ------------------------------------

    /**
     * Decides how to perform the search, based upon whether or not the intact type has been
     * specified. If not then all the currently defined search types will be iterated through to
     * collect all matches. If the type is specified then the search request must have come from a
     * JSP and so only single-type results are possible. NB this is different to before: Protein 'no
     * type' requests used to go to a binary view, but now this view results from a request from the
     * main JSP result page.
     *
     * @param helper      The search helper instance that will do the searching for us
     * @param searchClass The class to search on - may be null or empty
     * @param searchValue The value to search on
     * @param user        The Intact user object (needed by the search helper)
     * @return Collection A Collection of the results, or empty if none found.
     * @throws IntactException Thrown if there was a searching problem
     **/
    /**
     * private Collection getResults(SearchHelper helper, String searchClass, String searchValue,
     * IntactUserIF user) throws IntactException, TooLargeDataException {
     * <p/>
     * Collection result = null; if (searchClass == null || searchClass.length() == 0) {
     * <p/>
     * logger.info("we got no class type so let's look for all types of classes");
     * <p/>
     * //must be an initial search request, with possible multiple type results. //In this case we
     * should NOT finish after the first match, but continue //searching across ALL SPECIFIED
     * TYPES... // use for this search a value result = new ArrayList(); for (Iterator it =
     * SEARCH_CLASSES.iterator(); it.hasNext();) { result.addAll(helper.doLookup((String) it.next(),
     * searchValue, 50)); }
     * <p/>
     * } else { // this is a normal request from the servlet, we know the class, we know the value.
     * // nothing can go wrong here result = helper.doLookup(searchClass, searchValue, user); }
     * <p/>
     * return result; }
     */
    private ResultWrapper getResults(SearchHelper helper, String searchClass,
                                     String searchValue, IntactUserIF user) throws IntactException {

        ResultWrapper result = null;
        if (SearchValidator.isSearchable(searchClass)) {
            result = helper.searchFast(searchValue, searchClass);
        } else if (searchClass == null || searchClass.length() == 0) {
            // this is a initial request, so we don?t know how big is the result set
            // in that case searchFast, get the result from searchFast
            result = helper.searchFast(searchValue);

        } else {
            // this is a normal request from the servlet, we know the class, we know the value.
            Collection temp = new ArrayList();
            temp.addAll(helper.doLookup(searchClass, searchValue, user));
            result = new ResultWrapper(temp, SearchConstants.MAXIMUM_RESULT_SIZE);
        }


        return result;
    }

}



