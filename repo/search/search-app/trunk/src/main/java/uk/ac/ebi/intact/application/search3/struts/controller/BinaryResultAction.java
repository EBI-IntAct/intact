/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.view.beans.PartnersView;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Interactor;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * Performs the construction of view beans that will be used for a the partner view. It will calculate the interactions
 * and give back the code to forward to the coresponding JSP site for the representation of the results.
 *
 * @author Michael Kleen
 * @version $Id$
 */
public class BinaryResultAction extends AbstractResultAction {

    private static final Log logger = LogFactory.getLog(BinaryResultAction.class);

    /**
     * Implements abstract method AbstractResultAction.processResults.
     *
     * @param request  The request object containing the data we want
     * @param helpLink The help link to use
     *
     * @return String the return code for forwarding use by the execute method
     */
    protected String processResults( HttpServletRequest request ) {

        logger.info( "binary result  action" );

        Collection<AnnotatedObject> results = (Collection<AnnotatedObject>) request.getAttribute( SearchConstants.SEARCH_RESULTS );

        //initial sanity check - empty results should be just ignored
        if ( results.isEmpty() ) {
            return SearchConstants.FORWARD_NO_MATCHES;
        }

        logger.info( "BinaryAction: result Collection contains " + results.size() + " items." );

        //useful URL info
        String searchURL = super.getSearchURL();

        // check first for if we got the correct type and then build a collection of resulttypes
        if ( Interactor.class.isAssignableFrom( results.iterator().next().getClass() ) ) {

            PartnersView view = null;

            for (AnnotatedObject interactor : results)
            {
                view = new PartnersView(request,(Interactor)interactor);
            }

            // We store the PartnersView in the request, and it will be accessed from the jsp page
            request.setAttribute( SearchConstants.VIEW_BEAN, view );
            //send to the protein partners view JSP
            return SearchConstants.FORWARD_PARTNER_VIEW;

        } else {
            //something is wrong here forward to error page
            return SearchConstants.FORWARD_FAILURE;
        }
    }
}