/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */

package uk.ac.ebi.intact.application.mine.struts.controller;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import uk.ac.ebi.intact.application.mine.business.Constants;
import uk.ac.ebi.intact.application.mine.business.IntactUserI;
import uk.ac.ebi.intact.application.mine.business.MineException;
import uk.ac.ebi.intact.application.mine.business.graph.GraphHelper;
import uk.ac.ebi.intact.application.mine.business.graph.MineHelper;
import uk.ac.ebi.intact.application.mine.business.graph.model.GraphData;
import uk.ac.ebi.intact.application.mine.struts.view.ErrorForm;

/**
 * This class provides the action to start the Dijkstra algorithm to find the
 * shortest path between all search nodes.
 * 
 * @author Andreas Groscurth (groscurt@ebi.ac.uk)
 */
public class DisplayAction extends Action {
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an ActionForward instance describing where and how control should
     * be forwarded, or null if the response has already been completed.
     * 
     * @param mapping - The <code>ActionMapping</code> used to select this
     *            instance
     * @param form - The optional <code>ActionForm</code> bean for this
     *            request (if any)
     * @param request - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     * @return - represents a destination to which the controller servlet,
     *         <code>ActionServlet</code>, might be directed to perform a
     *         RequestDispatcher.forward() or HttpServletResponse.sendRedirect()
     *         to, as a result of processing activities of an
     *         <code>Action</code> class
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession( true );
        IntactUserI user = (IntactUserI) session.getAttribute( Constants.USER );
        // the accession numbers for the minimal connecting network are fetched
        Collection searchFor = (Collection) request
                .getAttribute( Constants.SEARCH );

        try {
            // if no user is in the current session an excepion is thrown
            // because up to now a user should have been created and e.g.
            // the search ac nr. should have been set.
            if ( user == null || searchFor == null ) {
                throw new NullPointerException(
                        "No user could be found in the " + "current session" );
            }
            // if less than two search Ac are given
            else if ( searchFor.size() < 2 ) {
                request.setAttribute( Constants.ERROR, new ErrorForm(
                        "At least two interactors have to be given !"
                                + " No network can be displayed !" ) );
                return mapping.findForward( Constants.ERROR );
            }

            Constants.LOGGER.info( "start minehelper" );

            MineHelper helper = new MineHelper( user );

            // the network map maps the unique graphid to the accession numbers
            // which are in the graph represented by the graphid
            // Integer -> Collection (related protein ac)
            Map networks = helper.getNetworkMap( searchFor );

            Integer graphid;
            GraphHelper graphHelper;
            Collection search = null;
            for (Iterator iter = networks.keySet().iterator(); iter.hasNext();) {
                // the key stores the taxid and graphid for the current search
                graphid = (Integer) iter.next();
                search = (Collection) networks.get( graphid );

                Constants.LOGGER.info( "start with graph nr " + graphid );

                // if the current search ac are in a graph in the database
                if ( graphid != Constants.SINGLETON_GRAPHID
                        && search.size() > 1 ) {
                    // the shortest path is computed
                    Constants.LOGGER.info( "searching for MiNe with " + search );
                    // the graphHelper is fetched from the application scope
                    graphHelper = (GraphHelper) session.getServletContext()
                            .getAttribute( Constants.GRAPH_HELPER );
                    // if the helper is not initialised yet it is
                    // initialised now and put in the application scope.
                    if ( null == graphHelper ) {
                        graphHelper = new GraphHelper( user );
                        session.getServletContext().setAttribute(
                                Constants.GRAPH_HELPER, graphHelper );
                    }
                    // the graphdata is fetched for the specific graphid
                    GraphData gd = graphHelper.getGraph( graphid );
                    // the minimal network is computed
                    // the found network is then updated in the MiNeHelper class
                    helper.computeMiNe( gd, search );
                }
                else {
                    Constants.LOGGER.info( search
                            + " is added to the singletons" );
                    // the search accession numbers are not in a graph
                    // therefore they are added to the singletons
                    user.addToSingletons( helper.getShortLabels( search ) );
                }
            }

            // if no paths could been found the application is forwarded
            // to the error page
            if ( user.getPaths().isEmpty() ) {
                Constants.LOGGER.warn( "no connecting network found" );
                String singletons = user.getSingletons().toString();
                singletons = singletons.substring( 1, singletons.length() - 1 );

                request.setAttribute( Constants.ERROR, new ErrorForm(
                        "The proteins <i>" + singletons
                                + "</i> are not in a connecting network !<br>"
                                + "Therfore no display is available !" ) );
                return mapping.findForward( Constants.ERROR );
            }
            // forward to the result page
            return mapping.findForward( Constants.SUCCESS );
        }
        //TODO: ActionErrors USE IT ?!
        catch ( MineException e ) {
            request.setAttribute( Constants.ERROR, new ErrorForm( e
                    .getLocalizedMessage() ) );
            return mapping.findForward( Constants.ERROR );
        }
        catch ( SQLException e ) {
            request.setAttribute( Constants.ERROR, new ErrorForm( e
                    .getLocalizedMessage() ) );
            return mapping.findForward( Constants.ERROR );
        }
    }
}