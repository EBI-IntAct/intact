package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.struts.controller;

/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.Constants;
import uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.struts.business.CvLists;
import uk.ac.ebi.intact.application.search3.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.searchengine.SearchHelper;
import uk.ac.ebi.intact.searchengine.SearchHelperI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

/**
 * Implementation of <strong>Action</strong> that initialise the application.
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public final class InitAction extends IntactBaseAction {

    private static final Log logger = LogFactory.getLog(InitAction.class);

    /**
     * Process the specified HTTP request, and create the corresponding HTTP response (or forward to another web
     * component that will create it). Return an <code>ActionForward</code> instance describing where and how control
     * should be forwarded, or <code>null</code> if the response has already been completed.
     *
     * @param mapping  The ActionMapping used to select this instance
     * @param form     The optional ActionForm bean for this request (if any)
     * @param request  The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @return an ActionForward object
     *
     * @throws IOException      if an input/output error occurs
     * @throws ServletException if a servlet exception occurs
     */
    public ActionForward execute( ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response )
            throws IOException, ServletException {

        //// I had problems with the tomcat4 of the testserver,
        //// therefore I copied this part of the search3 WelcomeAction
        SearchHelperI helper = new SearchHelper();
        //all we need to do here is set up a valid user if possible
        if ( super.setupUser( request ) == null ) {
            // not possible to set up an user, forward to errorpage
            return mapping.findForward( SearchConstants.FORWARD_FAILURE );

        } else if ( !helper.connected() ) {
            // the database is not connected forward to errorpage
            return mapping.findForward( SearchConstants.FORWARD_NO_RESOURCE );

        } else {

            //// end of the copied part
            CvLists lists = new CvLists();
            // the following collections contain information about all cv object for the specific cv
            Collection cvDatabases = null;
            Collection cvTopics = null;
            Collection cvInteractions = null;
            Collection cvIdentifications = null;
            Collection cvInteractionTypes = null;

            try {
                // fill the collection with information about all cvDatabase objects
                cvDatabases = lists.initCVDatabaseList();
                // fill the collection with information about all cvTopic objects
                cvTopics = lists.initCVTopicList();
                // fill the collection with information about all cvInteraction objects
                cvInteractions = lists.initCVInteractionList();
                // fill the collection with information about all cvIdentification objects
                cvIdentifications = lists.initCVIdentificationList();
                // fill the collection with information about all cvInteractionType objects
                cvInteractionTypes = lists.initCVInteractionTypeList();

            } catch ( IntactException e ) {
                logger.error( "Error while loading CV lists.", e );
                e.printStackTrace();
            }

            logger.info( "got cv collections..." );

            // Session to access various session objects. This will create
            //a new session if one does not exist.
            HttpSession session = super.getSession( request );

            //clear the error message
            session.setAttribute( Constants.ERROR_MESSAGE, "" );

            // these collections are used later to be displayed in the drop down list in the JSP
            // TODO create contants for these fields !!!
            getServlet().getServletContext().setAttribute( "cvDatabases", cvDatabases );
            getServlet().getServletContext().setAttribute( "cvTopics", cvTopics );
            getServlet().getServletContext().setAttribute( "cvInteractions", cvInteractions );
            getServlet().getServletContext().setAttribute( "cvIdentifications", cvIdentifications );
            getServlet().getServletContext().setAttribute( "cvInteractionTypes", cvInteractionTypes );

            //set up a valid user if possible
            if ( super.setupUser( request ) == null ) {
                return mapping.findForward( SearchConstants.FORWARD_FAILURE );
            }

            logger.info( "init done." );
            // Forward control to the specified success URI
            return ( mapping.findForward( SearchConstants.FORWARD_SUCCESS ) );
        }
    }
}