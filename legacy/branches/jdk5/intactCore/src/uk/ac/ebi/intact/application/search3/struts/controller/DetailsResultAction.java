/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.controller;

import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.view.beans.MainDetailViewBean;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Performs the beans view for the Experiement View.
 * <p>
 * This Action can be called in case of a Experiement or an Interaction
 *
 * @author Michael Kleen
 * @version $Id$
 */
public class DetailsResultAction extends AbstractResultAction {

    /**
     * Overrides the parent one to process the request more effectively.
     * <p>
     * It avoids making any assumptions
     * about the beans or the size of search result list and keep all of the processing in a single place for each
     * Action type.
     *
     * @param request  The request to be processed
     * @param helpLink The contextual help link
     *
     * @return String the forward code for the parent execute method to return.
     */
    protected String processResults( HttpServletRequest request, String helpLink ) {

        // Session to access various session objects. This will create
        //a new session if one does not exist.
        HttpSession session = super.getSession( request );

        //first check for and process a tabbed page request - ROUGHLY....
        //get the ViewBean (it already should contain the Experiment)
        //set the index of the Interaction page to that given in the request
        //send back to detail.jsp.
        String selectedPage = request.getParameter( "selectedChunk" );
        if ( ( selectedPage != null ) && ( !selectedPage.equals( "" ) ) ) {

            logger.debug( "doing next page request..next page is " + selectedPage );
            //got a request for a tabbed page from a detail view -
            //so use the request info on the specified view bean to change the
            //Interaction list to be viewed, and go back to the detail JSP afterwards...

            MainDetailViewBean bean = (MainDetailViewBean) session.getAttribute( SearchConstants.LARGE_EXPERIMENT_BEAN );
            if ( bean != null ) {
                bean.setInteractionPage( Integer.parseInt( selectedPage ) );
                logger.debug( "bean index set OK" );
                return SearchConstants.FORWARD_DETAIL_PAGE;
            }
            //something wrong here - no viewbean present to modify...
            return SearchConstants.FORWARD_FAILURE;
        }

        //new info to process, so get the search results from the request
        Collection results = (Collection) request.getAttribute( SearchConstants.SEARCH_RESULTS );
        //initial sanity check - empty results should be just ignored
        if ( ( results == null ) || ( results.isEmpty() ) ) {
            return SearchConstants.FORWARD_NO_MATCHES;
        }

        logger.info( "DetailAction: result Collection contains " + results.size() + " items." );

        // String appPath = getServlet().getServletContext().getInitParameter("searchLink");
        // String searchURL = request.getContextPath().concat(appPath);
        String searchURL = super.getSearchURL();

        //regardless of the result size, just build a viewbean for each result and put into
        //a Collection for use by the JSP - but first check we have the correct type for this
        //Action to process..
        Class resultType = results.iterator().next().getClass();

        Collection experiments = null;
        Interaction interactionResult = null;   //used to tell the viewbean what needs displaying

        if ( ( Interaction.class.isAssignableFrom( resultType ) ) ) {
            interactionResult = (Interaction) results.iterator().next();
            experiments = interactionResult.getExperiments();

        } else if ( Experiment.class.isAssignableFrom( resultType ) ) {
            experiments = results;  //got Experiments in the first place
        }

        List beanList;
        if ( experiments != null ) {
            beanList = new ArrayList();
            for ( Iterator it = experiments.iterator(); it.hasNext(); ) {
                MainDetailViewBean bean = new MainDetailViewBean( (Experiment) it.next(), helpLink,
                                                                  searchURL, request.getContextPath() );
                if ( interactionResult != null ) {
                    bean.setWrappedInteraction( interactionResult );   //tell bean display info
                }
                beanList.add( bean );
            }
            logger.info( "DetailAction: Collection of " + beanList.iterator().next().getClass() +
                         " created" );

            request.setAttribute( SearchConstants.VIEW_BEAN_LIST, beanList );
            //send to the detail view JSP
            logger.info( "detailsAction: forwarding to 'details' JSP.." );
            return SearchConstants.FORWARD_DETAIL_PAGE;
        } else {
            //something is wrong here - forward to error page 
            return SearchConstants.FORWARD_FAILURE;
        }
    }
}