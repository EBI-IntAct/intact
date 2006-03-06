package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.struts.controller;

import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;
import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.struts.controller.AbstractResultAction;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.view.beans.SimpleViewBean;
import uk.ac.ebi.intact.model.AnnotatedObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This Action class performs the construction of view beans that will be used by the simple.jsp
 * page to display initial search results. Note that this Action is different to all others in that
 * it may have to construct more than a single view bean since the results to be displayed may be of
 * multiple types. Therefore it may not be appropriate to extend from AbstractResultAction as in
 * other cases.
 *
 * copied and modified from the SimpleResultAction in search3
 *
 * @author Michael Kleen, Anja Friedrichsen
 * @version $Id$
 */
public class AdvSimpleResultAction extends AbstractResultAction {

    /**
     * This method overrides the parent one to process the request more effectively. It avoids
     * making any assumptions about the beans or the size of search result list and keep all of the
     * processing in a single place for each Action type.  This method creates WrapperObjects for the
     * Objects Interactions, Proteins,  Experiments, CvObjects and BioSource and returns the the code which
     * is needed to forward to view.
     *
     * @param request  The request object containing the data we want
     * @param helpLink The help link to use
     * @return String the return code for forwarding use by the execute method
     */
    protected String processResults(HttpServletRequest request, String helpLink) {

        logger.info("enter simple action");

        HttpSession session = super.getSession(request);

        // Handle to the Intact User.
        IntactUserIF user = super.getIntactUser(session);

        //get the search results from the request
        final IterableMap resultMap = (IterableMap) request.getAttribute(SearchConstants.SEARCH_RESULTS_MAP);

        logger.info("SimpleAction: Map contains " + resultMap.size() + " keys.");

        String contextPath = request.getContextPath();
        //build the URL for searches and pass to the view beans
        String searchURL = super.getSearchURL();

        logger.info("SearchLink: " + searchURL);

        //we can build a List, partitioned by type, here inseatd of
        //in the JSP. That way the JSP only ever gets things to display, unless
        //there are too many items to display for a particular type...
        List partitionList = new ArrayList();   //this will hold the seperate lists as items

        // the resultMap has the structure that the keys are the names of the search objects
        // (protein, experiment, interaction, cv or biosource) and the corresponding value is a collection containing all
        // intact objects of the type of the key
        MapIterator it = resultMap.mapIterator();
        Collection temp;
        while (it.hasNext()) {
            // go to the next key->value pair.
            it.next();

            Collection results = (ArrayList) it.getValue();
            temp = new ArrayList(results.size());
            //now add in the sublists to the in the partitionlist
            for (Iterator iterator = results.iterator(); iterator.hasNext();) {
                AnnotatedObject obj = (AnnotatedObject) iterator.next();
                temp.add(new SimpleViewBean(obj, user.getHelpLink(), searchURL, contextPath));
                logger.info("add " + obj.getShortLabel());
            }

            logger.info("List size: " + partitionList.size());
            partitionList.add(temp);
        }
        //put the viewbeans in the request and send on to the view...
        request.setAttribute(SearchConstants.VIEW_BEAN_LIST, partitionList);
        return SearchConstants.FORWARD_SIMPLE_RESULTS;
    }
}