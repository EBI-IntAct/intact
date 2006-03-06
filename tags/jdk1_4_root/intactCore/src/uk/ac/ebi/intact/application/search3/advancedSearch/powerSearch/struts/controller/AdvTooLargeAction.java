/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.struts.controller;

import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.search3.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.view.beans.SingleResultViewBean;
import uk.ac.ebi.intact.application.search3.struts.view.beans.TooLargeViewBean;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This class provides the actions to handle the case if the retriving resultset from the
 * searchaction is too big. The Action creates a basic statistic which gives an overview over the
 * resultsset. this statistics will be shown in the web-interface.
 *
 * copied and configured from the TooLargeAction in search3
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class AdvTooLargeAction extends IntactBaseAction {

    /**
     * counts the complete result information in 4 different categories this is necessary because
     * all controlled vocabulary terms should count in the same category.
     *
     * @param mapping  The ActionMapping used to select this instance
     * @param form     The optional ActionForm bean for this request (if any)
     * @param request  The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @return an ActionForward object
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        logger.info("tooLarge action: the resultset contains to many objects");


        // get the resultinfo from the initial request from the search action
        final IterableMap resultInfo = ((IterableMap) request.getAttribute(SearchConstants.RESULT_INFO));
        logger.info(resultInfo);

        int cvCount = 0;
        int proteinCount = 0;
        int experimentCount = 0;
        int interactionCount = 0;

        // count for any type of searchable objects in the resultset to generate the statistic
        // this is done by creating from the classname a class and check then for classtype
        MapIterator it = resultInfo.mapIterator();
        String key = null;
        String className = null;
        while (it.hasNext()) {
            it.next();
            className = (String) it.getValue();
            className = className.trim();
            logger.info("tooLarge action: searching for class" + className);
            Class clazz = null;
            try {
                clazz = Class.forName(className);
                if (Protein.class.isAssignableFrom(clazz)) {
                    proteinCount++;
                } else if (Experiment.class.isAssignableFrom(clazz)) {
                    experimentCount++;
                } else if (Interaction.class.isAssignableFrom(clazz)) {
                    interactionCount++;
                } else if (CvObject.class.isAssignableFrom(clazz)) {
                    cvCount++;
                } else {
                    logger.error("in tooLarge: unknown key");
                    return mapping.findForward(SearchConstants.FORWARD_FAILURE);
                }

            } catch (ClassNotFoundException e) {
                logger.info("Class: " + clazz);
                // we got a class which is not part of the the searchable classes.
                logger.info("tooLarge action: the resultset contains to an object which is no " +
                        "assignable from an intactType");
                return mapping.findForward(SearchConstants.FORWARD_FAILURE);
            }
        }


        // get the helplink count the results and create with them  a couple of viewbeans for the jsp

        final String relativeHelpLink = getServlet().getServletContext().getInitParameter("helpLink");
        final String ctxtPath = request.getContextPath();
        final String relativePath = ctxtPath.substring(0, ctxtPath.lastIndexOf("search"));
        final String helpLink = relativePath.concat(relativeHelpLink);

        final String appPath = getServlet().getServletContext().getInitParameter("searchLink");
        final String searchURL = ctxtPath.concat(appPath);
        HttpSession session = super.getSession(request);
        String query = (String) session.getAttribute(SearchConstants.SEARCH_CRITERIA);


        TooLargeViewBean tooLargeViewBean = new TooLargeViewBean();

        if (experimentCount > 0) {
            tooLargeViewBean.add(new SingleResultViewBean("Experiment", experimentCount,
                    helpLink, searchURL, query));
        }

        if (interactionCount > 0) {
            tooLargeViewBean.add(new SingleResultViewBean("Interaction", interactionCount,
                    helpLink, searchURL, query));
        }

        if (proteinCount > 0) {
            tooLargeViewBean.add(new SingleResultViewBean("Protein", proteinCount,
                    helpLink, searchURL, query));
        }

        if (cvCount > 0) {
            tooLargeViewBean.add(new SingleResultViewBean("Controlled vocabulary term", cvCount,
                    helpLink, searchURL, query));
        }

        // add the viewbean to the request and forward to the jsp
        request.setAttribute(SearchConstants.VIEW_BEAN, tooLargeViewBean);
        return mapping.findForward(SearchConstants.FORWARD_RESULTS);
    }
}