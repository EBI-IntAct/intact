/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.controller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search3.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.CvObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * This class provide a bridge between the search process and the result.
 * It allows accordingly to the user request to forward to the appropriate
 * result actions.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class DispatcherAction extends IntactBaseAction {

    /**
     * Process the specified HTTP request, and create the corresponding
     * HTTP response (or forward to another web component that will create
     * it). Return an ActionForward instance describing where and how
     * control should be forwarded, or null if the response has
     * already been completed.
     *
     * @param mapping  - The <code>ActionMapping</code> used to select this instance
     * @param form     - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request  - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     * @return - represents a destination to which the controller servlet,
     *         <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     *         or HttpServletResponse.sendRedirect() to, as a result of processing
     *         activities of an <code>Action</code> class
     */
    public ActionForward execute( ActionMapping mapping, ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response ) throws Exception {

        //first check to see if we just need to forward for a tabbed page of an existing result
        String requestedPage = request.getParameter("selectedChunk");
        if((requestedPage != null) && (!requestedPage.equals("")))
            return mapping.findForward(SearchConstants.FORWARD_DETAILS_ACTION);

        //not an exisiting page request, so get the search results from the request
        Collection results = (Collection) request.getAttribute( SearchConstants.SEARCH_RESULTS );

        logger.info( "dispatcher action: analysing user's query..." );

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser( getSession( request ) );
        if(user == null) {
            //just set up a new user for the session - if it fails, need to give up!
            user = super.setupUser(request);
            if(user == null) return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }


        // The first element of the search result.
        Object resultItem = results.iterator().next();
        logger.info( "First item className: " + resultItem.getClass().getName() );

        // dispatch to the right action accordingly -
        //IMPORTANT (Aug 2004): With the new views required for search, all requests
        //that do NOT contain the searchClass should go to the 'simple' initial result
        //view UNLESS there is only a SINGLE match, in which case the 'appropriate' view should
        //be used.
        //All other requests will from now on have the searchClass specified..

        String pageSource = null;   //need this later
        String searchClass = user.getSearchClass(); //this was set in the search Action
        logger.info( "SearchClass: " + searchClass );

        //check for a searchClass(ie request from an INTERNAL LINK) and process accordingly...
        if (searchClass == null || searchClass.length() == 0) {

            //initial request - first check for single result. If YES, then forward to the
            //simple view Action; if NO then forward to the 'main' view....
            if(results.size() == 1) {
                logger.info("Dispatcher: initial search request (single result)...");
                //simplest way to do this is to set the pageSource variable and force subsequent
                //code to handle it...
                pageSource = "simple";
            }
            else {
                //handle 'normally'...
                logger.info("Dispatcher: initial search request (no search class specified) - forwarding to SimpleResultAction..");
                return mapping.findForward(SearchConstants.FORWARD_SIMPLE_ACTION);
            }
        }
        else {
            //search class defined - forward to relevant action
            logger.info("Dispatcher: Search class specified in request (so it came from a link)..");
            //Need to find out HERE what page the request came from...
            pageSource = request.getParameter(SearchConstants.PAGE_SOURCE);
        }

        //Proteins are a special case...
        if(Protein.class.isAssignableFrom(resultItem.getClass())) {

            //NB here need to distinguish between a request for Protein DETAILS
            //and a request for Protein PARTNER view (as now with the new views, BOTH may have
            //the search class specified!!)..

            if((pageSource != null) && (pageSource.equals("simple"))) {
                //need to send to partners view Action (single or multiple)...
                logger.info("Dispatcher: forwarding to Protein partners action");
                return mapping.findForward( SearchConstants.FORWARD_BINARY_ACTION );
            }

            //otherwise must be a standard 'Protein beans' view (link from another internal page)..
            logger.info("Dispatcher: forwarding to Protein beans action");
            return mapping.findForward( SearchConstants.FORWARD_SINGLE_ACTION );

        }

        //can't be a Protein if we get to here - check for Exps/Interactions..

        //new layout - Sept 2004: for the new layout, we can only get detail
        //information when a link is clicked from the 'simple' page. Currently
        //we just check for Experiments or Interactions..
        //TODO: NEEDS TO BE REFACTORED PROPERLY FROM HERE ON....
        if(((pageSource != null) && (pageSource.equals("simple"))) &
                (!CvObject.class.isAssignableFrom((resultItem.getClass())))) {

            logger.info("Dispatcher: forwarding to Details action for Experiment/Interaction..");
            return mapping.findForward( SearchConstants.FORWARD_DETAILS_ACTION );

        }

        //TODO: code below probably needs revising for the new views...

        //OLD CODE FOLLOWS (still needed for CvObject/BioSource - should be revised when new
        //view for those classes is defined)...
        //NB this code should go to a main detail unless it is a CvObject, in which
        //case it should do a single view..
        //Not a protein - deal with the others...
        if( ( results.size() == 1 ) & ( ! Interaction.class.isAssignableFrom( resultItem.getClass() ) ) ) {
            //only use the single view for Proteins (dealt with above),
            //Experiment and Controlled vocabulary - Interactions
            // still need to be displayed in the context of an Experiment
            logger.info( "Dispatcher ask forward to SingleResultAction" );
            return mapping.findForward( SearchConstants.FORWARD_SINGLE_ACTION );
        }

        logger.info( "Dispatcher ask forward to DetailsResultAction" );
        return mapping.findForward( SearchConstants.FORWARD_DETAILS_ACTION );
    }
}
