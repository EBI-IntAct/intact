/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.struts.controller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.search2.business.IntactUserIF;
import uk.ac.ebi.intact.application.search2.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search2.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.ProteinImpl;

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

        //get the search results from the request
        Collection results = (Collection) request.getAttribute( SearchConstants.SEARCH_RESULTS );

        logger.info( "dispatcher action: analysing user's query..." );

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser( getSession( request ) );
        if( user == null ) {
            //browser page caching screwed up the session - need to
            //get a user object created again by forwarding to welcome action...
            return mapping.findForward( SearchConstants.FORWARD_SESSION_LOST );
        }

        // The first element of the search result.
        Object resultItem = results.iterator().next();

        // dispatch to the right action accordingly
        logger.info( "First item className: " + resultItem.getClass().getName() );
        if(Protein.class.isAssignableFrom(resultItem.getClass())) {

            if( ( results.size() == 1 ) ) {
                Class searchClass = user.getSearchClass();
                logger.info( "SearchClass: " + searchClass );

                //check to see if the request came from a link on a page
                //(ie searchClass is non-empty) - if so, do a single view
                if( searchClass.isAssignableFrom(ProteinImpl.class) ) {
                    logger.info( "Dispatcher ask forward to SingleResultAction" );
                    return mapping.findForward( SearchConstants.FORWARD_SINGLE_ACTION );
                }

                logger.info( "Dispatcher ask forward to BinaryResultAction" );
                return mapping.findForward( SearchConstants.FORWARD_BINARY_ACTION );
            }

            logger.info( "Dispatcher ask forward to DetailsResultAction" );
            return mapping.findForward( SearchConstants.FORWARD_DETAILS_ACTION );
        }

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
