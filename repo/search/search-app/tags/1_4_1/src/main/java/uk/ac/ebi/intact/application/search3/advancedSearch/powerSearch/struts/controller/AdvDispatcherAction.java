/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.struts.controller;

import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.context.IntactContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class provide a bridge between the search process and the result. It allows accordingly to the user request to
 * forward to the appropriate result actions.
 * <p>
 * copied and modified from the DispatcherAction in search3
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk), Anja Friedrichsen
 * @version $Id$
 */
public class AdvDispatcherAction extends IntactBaseAction {

    private static final Log logger = LogFactory.getLog(AdvDispatcherAction.class);

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
     *
     * @throws Exception ...
     */
    public ActionForward execute( ActionMapping mapping, ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response ) throws Exception {

        logger.info( "Enter Dispatcher action" );

        //first check to see if we just need to forward for a tabbed page of an existing result
        String requestedPage = request.getParameter( "page" );
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
        final IterableMap resultMap = (IterableMap) request.getAttribute( SearchConstants.SEARCH_RESULTS_MAP );

        final String binaryValue = user.getBinaryValue();
        final String viewSource = user.getView();

        logger.info( "Binary Value " + binaryValue );
        logger.info( "View Value " + viewSource );

        Collection results = null;
        // now check the type, and forward to the relevant action
        // the resultMap has the structure that the keys are the names of the search objects
        // (protein, experiment, interaction, cv or biosource) and the corresponding value is a collection containing all
        // intact objects of the type of the key
        if ( resultMap.size() == 1 ) {
            MapIterator it = resultMap.mapIterator();
            // the key is the name of one search object
            String key = (String) it.next();
            // the value is a collection holding all objects, which have the type of the key
            results = (ArrayList) it.getValue();
            request.setAttribute( SearchConstants.SEARCH_RESULTS, results );

            // check the number of results and forward to the corresponding page
            if ( results.size() == 1 ) {

                String ac = ((AnnotatedObject) results.iterator().next()).getAc();
                request.getParameterMap().put("searchString", ac);

                logger.info( "First item className: " + key );
                // check for Experiment or Interaction first
                if ( key.equalsIgnoreCase( "experiment" ) || key.equalsIgnoreCase( "interaction" ) ) {

                    logger.info( "It's a Experiment or Interaction, ask forward to SingleResultAction" );
                    return mapping.findForward( SearchConstants.FORWARD_DETAILS_ACTION );
                    // now check if it's an Interaction
                } else if ( key.equalsIgnoreCase( "protein" ) ) {
                    // now we got different choices whether the protein has interaction partners or not
                    Collection proteinInteractionPartner = IntactContext.getCurrentInstance()
                            .getDataContext().getDaoFactory().getProteinDao().getPartnersCountingInteractionsByProteinAc(ac).keySet();
                    //logger.info( "Partner Collection: " + proteinInteractionPartner );
                    if ( proteinInteractionPartner.isEmpty() ) {
                        //the protein has no interaction partners so we want the single Protein View
                        logger.info( "It's a Protein, ask forward to SingleResultAction" );
                        return mapping.findForward( SearchConstants.FORWARD_SINGLE_ACTION );
                    } else {
                        //the protein has interaction partners so we want the partner view
                        logger.info( "It's a Protein, forwarding to  PartnerResultAction" );

                        return mapping.findForward( SearchConstants.FORWARD_BINARY_ACTION );
                    }
                } else if ( key.equalsIgnoreCase( "biosource" ) || key.equalsIgnoreCase( "cvobject" ) ) {
                    // we want the single Protein View
                    logger.info( "It's a Biosource or CV, ask forward to SingleResultAction" );
                    return mapping.findForward( SearchConstants.FORWARD_SINGLE_ACTION );
                } else {
                    // need to give up, we got an unknown type
                    logger.info( key + "is not supported" );
                    return mapping.findForward( SearchConstants.FORWARD_FAILURE );
                }
            } else if ( results.size() > 1 ) {
                // it's a  multiple requst
                logger.info( "Dispatcher ask forward to AdvSimpleResultAction" );
                return mapping.findForward( SearchConstants.FORWARD_SIMPLE_ACTION );
            }
        }
        // result size is bigger than 1, it can only be simple request for the simple
        else if ( ( resultMap.size() > 1 ) ) {
            // it's a  multiple requst
            logger.info( "Dispatcher ask forward to AdvSimpleResultAction" );
            return mapping.findForward( SearchConstants.FORWARD_SIMPLE_ACTION );
        }

        // something went wrong here, forward to error page
        logger.info( "Something went wrong here, forward to error page" );
        return mapping.findForward( SearchConstants.FORWARD_FAILURE );
    }
}