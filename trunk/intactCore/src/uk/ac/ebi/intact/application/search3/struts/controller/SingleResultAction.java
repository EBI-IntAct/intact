/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.controller;

import uk.ac.ebi.intact.application.search3.business.Constants;
import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.struts.view.beans.AbstractViewBean;
import uk.ac.ebi.intact.application.search3.struts.view.ViewBeanFactory;
import uk.ac.ebi.intact.application.search3.struts.view.beans.ProteinViewBean;
import uk.ac.ebi.intact.application.search3.struts.view.beans.BioSourceViewBean;
import uk.ac.ebi.intact.application.search3.struts.view.beans.CvObjectViewBean;
import uk.ac.ebi.intact.application.search3.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.model.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * Performs the single view for Proteins.
 *
 * @author IntAct Team
 * @version $Id$
 */
public class SingleResultAction extends AbstractResultAction {

    ///////////////////////////////////
    // Abstract methods implementation
    ///////////////////////////////////


    protected AbstractViewBean getAbstractViewBean ( Object result, IntactUserIF user, String contextPath ) {
        

        logger.info( "single action: building view beans..." );
        //AnnotatedObject firstItem = (AnnotatedObject) results.iterator().next();
        if(!AnnotatedObject.class.isAssignableFrom(result.getClass())) {
            //We have an incorrect parameter type - return null for now, but
            //this could perhaps be handled better later on...
             logger.info( "object not assignale from annotatedobject" );
            return null;
        }

        //if( firstItem.getClass().equals( Experiment.class ) ){
        if( result.getClass().equals( Experiment.class ) ){
            // if the object is an Experiment, check the count of interactions
            //Experiment experiment = (Experiment) firstItem;
            Experiment experiment = (Experiment)result;
            int size = experiment.getInteractions().size(); // As this is proxied, it won't hang forever.
            if ( size > Constants.MAX_PAGE_SIZE ) {
                // this experiment can't be displayed as such,
                // we forward to a specialised action.
                logger.info( "Experiment's interactions count: " + size + ". Chunk its display." );

                // calculate the maximum number of chunks
                int max = size / Constants.MAX_PAGE_SIZE;
                if ( size % Constants.MAX_PAGE_SIZE > 0 ) {
                    max++;
                }

                // the the one the user want to see displayed
                int selected = user.getSelectedChunk();
                if (selected == -1) {
                    selected = 1;
                    user.setSelectedChunk( 1 );
                } else if ( selected > max ) {
                    // just in case the user fiddle around with the URL ...
                    selected = max;
                    user.setSelectedChunk( max );
                }

                return ViewBeanFactory.getInstance().getChunkedSingleViewBean(experiment, user.getHelpLink(),
                                                                               contextPath,
                                                                               max, selected  );
            }
        }

        //checking the new stuff....
        if(Protein.class.isAssignableFrom(result.getClass())) {
            // it's a single Protein
            logger.info( "Creating single Protein view" );
            String appPath = getServlet().getServletContext().getInitParameter("searchLink");
            String searchURL = contextPath.concat(appPath);
            return new ProteinViewBean((Protein)result, user.getHelpLink(), searchURL, contextPath);
        }

        //we know the result isn't an Experiment or Protein - it must be a CvObject or a BioSource Object
        // Let's see

         if(CvObject.class.isAssignableFrom(result.getClass())) {
             // it's a single CvObject
            logger.info( "Creating CvObject view" );
            logger.info( result.toString());
            String appPath = getServlet().getServletContext().getInitParameter("searchLink");
            String searchURL = contextPath.concat(appPath);
            return new CvObjectViewBean((CvObject)result, user.getHelpLink(), searchURL, contextPath);
        }

         if(BioSource.class.isAssignableFrom(result.getClass())) {
             // it's a BioSource Object
            logger.info( "Creating BioSource view" );
            String appPath = getServlet().getServletContext().getInitParameter("searchLink");
            String searchURL = contextPath.concat(appPath);
            return new BioSourceViewBean((BioSource)result, user.getHelpLink(), searchURL, contextPath);
        }


        //we know the result isn't an Experiment or Protein - handle it as a general
        //AnnotatedObject...
          /**
          logger.info( "Creating standard Annotated Object view  " );
          String appPath = getServlet().getServletContext().getInitParameter("searchLink");
          String searchURL = contextPath.concat(appPath);
          return new BioSourceViewBean((BioSource)result, user.getHelpLink(), searchURL, contextPath);
          **/
        // something was getting wrong
        // we need here a exception
         throw new RuntimeException("search result not inspectable"); 
    }

    /**
     * Used to process a 'single item' view. In general this will handle all such views,
     * but for now this just deals with single Protein views.
     * @param request The request object containing the data we want
     * @param helpLink The help link to use
     * @return String the return code for forwarding use by the execute method
     */
    protected String processResults(HttpServletRequest request, String helpLink) {

        //get the search results from the request
        Collection results = (Collection) request.getAttribute(SearchConstants.SEARCH_RESULTS);
        if (results.isEmpty()) return SearchConstants.FORWARD_FAILURE;   //something failed - no results

        Object firstItem = results.iterator().next();
        Class resultType = results.iterator().next().getClass();
        if (Protein.class.isAssignableFrom(resultType)) {
            //single Protein view - try out our new bean instead....

            //build the URL for searches and pass to the view bean
            //NB probably better to put this in the User object at some point instead
            String appPath = getServlet().getServletContext().getInitParameter("searchLink");
            String searchURL = request.getContextPath().concat(appPath);
            ProteinViewBean bean = new ProteinViewBean((Protein) firstItem, helpLink, searchURL, request.getContextPath());
            //request.getSession().setAttribute(SearchConstants.VIEW_BEAN, bean);
            request.setAttribute(SearchConstants.VIEW_BEAN, bean);

            return "singleProtein";
        }

        //TODO: need code here to handle non-Protein single views (needs specification first)...
        //something wrong here - wrong result type for this Action...
        return SearchConstants.FORWARD_FAILURE;



    }
}