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
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.model.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * This class provide a bridge between the search process and the result. It allows accordingly to the user request to
 * forward to the appropriate result actions.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class DispatcherAction extends IntactBaseAction {

    /**
     * Process the specified HTTP request, and create the corresponding HTTP response (or forward to another web
     * component that will create it). Return an ActionForward instance describing where and how control should be
     * forwarded, or null if the response has already been completed.
     *
     * @param mapping  - The <code>ActionMapping</code> used to select this instance
     * @param form     - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request  - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     *
     * @return - represents a destination to which the controller servlet, <code>ActionServlet</code>, might be directed
     *         to perform a RequestDispatcher.forward() or HttpServletResponse.sendRedirect() to, as a result of
     *         processing activities of an <code>Action</code> class
     *         <p/>
     *         throws Exception
     *
     * @throws Exception
     */
    public ActionForward execute( ActionMapping mapping, ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response ) throws Exception {

        logger.info( "Enter Dispatcher action" );

        //first check to see if we just need to forward for a tabbed page of an existing result
        String requestedPage = request.getParameter( "selectedChunk" );
        if ( ( requestedPage != null ) && ( !requestedPage.equals( "" ) ) ) {
            return mapping.findForward( SearchConstants.FORWARD_DETAILS_ACTION );
        }

        logger.info( "dispatcher action: analysing user's query..." );

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser( getSession( request ) );
        if ( user == null ) {
            //just set up a new user for the session - if it fails, need to give up!
            user = super.setupUser( request );
            if ( user == null ) {
                logger.info( "no user, forward failer" );
                return mapping.findForward( SearchConstants.FORWARD_FAILURE );
            }
        }

        //not an exisiting page request, so get the search results from the request
        final Collection results = (Collection) request.getAttribute( SearchConstants.SEARCH_RESULTS );

        final String binaryValue = user.getBinaryValue();
        final String viewSource = user.getView();

        logger.info( "Binary Value " + binaryValue );
        logger.info( "View Value " + viewSource );


        Object resultItem = results.iterator().next();
        logger.info( "First item className: " + resultItem.getClass().getName() );

        // now check the type, and forward to the relevant action
        if ( results.size() == 1 ) {
            // check for Experiment first
            if ( ( Experiment.class.isAssignableFrom( resultItem.getClass() ) ) ) {

                logger.info( "It's a Experiment, ask forward to SingleResultAction" );
                return mapping.findForward( SearchConstants.FORWARD_DETAILS_ACTION );
                // now check if it's an Interaction
            } else if ( ( Interaction.class.isAssignableFrom( resultItem.getClass() ) ) ) {

                logger.info( "It's a Interaction, ask forward to SingleResultAction" );
                return mapping.findForward( SearchConstants.FORWARD_DETAILS_ACTION );
                // check if it's Protein
            } else if ( ( Protein.class.isAssignableFrom( resultItem.getClass() ) ) ) {

                // now we got different choices
                if ( ( viewSource != null ) && ( viewSource.equals( "partner" ) ) ) {
                    if ( binaryValue != null && !binaryValue.equals( "" ) ) {
                        // it's a self interactions from outside
                        logger.info( "It's a Protein,  forwarding to BinaryProteinAction" );
                        return mapping.findForward( SearchConstants.FORWARD_BINARYINTERACTOR_ACTION );
                    } else {
                        // it's a request from inside the jsp
                        logger.info( "It's a Protein, forwarding to  PartnerResultAction" );
                        return mapping.findForward( SearchConstants.FORWARD_BINARY_ACTION );
                    }
                }
                // we want the single Protein View
                logger.info( "It's a Protein, ask forward to SingleResultAction" );
                return mapping.findForward( SearchConstants.FORWARD_SINGLE_ACTION );

            } // check if it's NucleicAcid
            else if ( ( NucleicAcid.class.isAssignableFrom( resultItem.getClass() ) ) ) {

                // now we got different choices
                if ( ( viewSource != null ) && ( viewSource.equals( "partner" ) ) ) {
                    if ( binaryValue != null && !binaryValue.equals( "" ) ) {
                        // it's a self interactions from outside
                        logger.info( "It's a NucleicAcid,  forwarding to BinaryProteinAction" );
                        return mapping.findForward( SearchConstants.FORWARD_BINARYINTERACTOR_ACTION );
                    } else {
                        // it's a request from inside the jsp
                        logger.info( "It's a NucleicAcid, forwarding to  PartnerResultAction" );
                        return mapping.findForward( SearchConstants.FORWARD_BINARY_ACTION );
                    }
                }
                // we want the single Protein View
                logger.info( "It's a NucleicAcid, ask forward to SingleResultAction" );
                return mapping.findForward( SearchConstants.FORWARD_SINGLE_ACTION );

            } // now it can only be a CvObject or a BioSource
            else if ( CvObject.class.isAssignableFrom( resultItem.getClass() ) ) {

                logger.info( "It's a CvObjects, ask forward to SingleResultAction" );
                return mapping.findForward( SearchConstants.FORWARD_SINGLE_ACTION );
            } else if ( BioSource.class.isAssignableFrom( resultItem.getClass() ) ) {

                logger.info( "It's a BioSource, ask forward to SingleResultAction" );
                return mapping.findForward( SearchConstants.FORWARD_SINGLE_ACTION );
            } else {
                // need to give up, we got an unknown type
                logger.info( resultItem.getClass() + "is not supported" );
                return mapping.findForward( SearchConstants.FORWARD_FAILURE );
            }
        }

        // resultsize is bigger than 1, it can only be simple request for the simple or a binary
        // request with 2 Proteins
        else if ( ( results.size() > 1 ) ) {

            if ( binaryValue != null && !binaryValue.equals( "" ) ) {
                // it's a binary interaction request
                logger.info( "Dispatcher ask forwarding to BinaryProteinAction" );
                return mapping.findForward( SearchConstants.FORWARD_BINARYINTERACTOR_ACTION );

            } else {
                // it's a  multiple requst
                logger.info( "Dispatcher ask forward to SimpleResultAction" );
                return mapping.findForward( SearchConstants.FORWARD_SIMPLE_ACTION );
            }
        }
        // something went wrong here, forward to error page
        logger.info( "Something went wrong here, forward to error page" );
        return mapping.findForward( SearchConstants.FORWARD_FAILURE );
    }
}