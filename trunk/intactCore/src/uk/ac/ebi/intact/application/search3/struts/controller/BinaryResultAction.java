/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.controller;

import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.view.ViewBeanFactory;
import uk.ac.ebi.intact.application.search3.struts.view.beans.AbstractViewBean;
import uk.ac.ebi.intact.application.search3.struts.view.beans.PartnersViewBean;
import uk.ac.ebi.intact.model.Protein;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Performs the binary view for Proteins.
 *
 * @author IntAct Team
 * @version $Id$
 */
public class BinaryResultAction extends AbstractResultAction {

    ///////////////////////////////////
    // Abstract methods implementation
    ///////////////////////////////////


    protected String processResults(HttpServletRequest request, String helpLink) {


        //first get the search results from the request
        Collection results = (Collection) request.getAttribute(SearchConstants.SEARCH_RESULTS);

        //initial sanity check - empty results should be just ignored
        if (results.isEmpty()) return SearchConstants.FORWARD_NO_MATCHES;

        // Session to access various session objects. This will create
        //a new session if one does not exist.
        HttpSession session = super.getSession(request);

        logger.info("BinaryAction: result Collection contains " + results.size() + " items.");
        List beanList = null;
        //useful URL info
        String appPath = getServlet().getServletContext().getInitParameter("searchLink");
        String searchURL = request.getContextPath().concat(appPath);

        //regardless of the result size, just build a viewbean for each result and put into
        //a Collection for use by the JSP - but first check we have the correct type for this
        //Action to process..
        if (Protein.class.isAssignableFrom(results.iterator().next().getClass())) {

            beanList = new ArrayList();
            for (Iterator it = results.iterator(); it.hasNext();) {
                beanList.add(
                        new PartnersViewBean((Protein) it.next(), helpLink, searchURL,
                                request.getContextPath()));
            }
            logger.info(
                    "BinaryAction: Collection of " + beanList.iterator().next().getClass() +
                    " created");

            //save in the session (****REQUEST**** would be better!) and return..
            //session.setAttribute(SearchConstants.VIEW_BEAN_LIST, beanList);
            request.setAttribute(SearchConstants.VIEW_BEAN_LIST, beanList);
            //send to the protein partners view JSP
            return "partners";

        } else {
            //something wrong here - wrong result type for this Action...
            return SearchConstants.FORWARD_FAILURE;
        }

    }

    /**
     * @param result
     * @param user
     * @param contextPath
     * @return
     * @see AbstractResultAction#getAbstractViewBean
     */
    protected AbstractViewBean getAbstractViewBean(Object result, IntactUserIF user,
                                                   String contextPath) {

        //New summary view stuff....
        //build the URL for searches and pass to the view beans
        //NB probably better to put this in the User object at some point instead
        String appPath = getServlet().getServletContext().getInitParameter("searchLink");
        String searchURL = contextPath.concat(appPath);

        //just return a partners view bean - the caller should build a collection of them
        //if the search result list is bigger than one...
        if (Protein.class.isAssignableFrom(result.getClass()))
            return new PartnersViewBean((Protein) result, user.getHelpLink(), searchURL,
                    contextPath);

        //previous code (returns a bean that wraps a whole Collection).....
        logger.info("binary action: building view beans...");
        if (Collection.class.isAssignableFrom(result.getClass()))
            return ViewBeanFactory.getInstance().getBinaryViewBean((Collection) result,
                    user.getHelpLink(), contextPath);
        return null;
    }

}
