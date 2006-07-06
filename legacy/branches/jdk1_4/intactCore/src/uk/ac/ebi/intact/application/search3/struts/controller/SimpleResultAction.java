package uk.ac.ebi.intact.application.search3.struts.controller;

import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.view.beans.SimpleViewBean;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.model.*;

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
 * @author Michael Kleen
 * @version $Id$
 */
public class SimpleResultAction extends AbstractResultAction {

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
        Collection results = (Collection) request.getAttribute(SearchConstants.SEARCH_RESULTS);

        logger.info("SimpleAction: Collection contains " + results.size() + " items.");

        String contextPath = request.getContextPath();
        //build the URL for searches and pass to the view beans
        String searchURL = super.getSearchURL();

        //we can build a List of sublists, partitioned by type, here inseatd of
        //in the JSP. That way the JSP only ever gets things to display, unless
        //there are too many items to display for a particular type...

        List expList = new ArrayList(results.size());
        List interactionList = new ArrayList(results.size());
        List proteinList = new ArrayList(results.size());
        List cvObjectList = new ArrayList(results.size());
        ;
        List partitionList = new ArrayList(results.size());   //this will hold the seperate lists as items

        for (Iterator it = results.iterator(); it.hasNext();) {

            AnnotatedObject obj = (AnnotatedObject) it.next();
            //now create a relevant view bean for each type in the result set...
            if (Experiment.class.isAssignableFrom(obj.getClass())) {
                expList.add(new SimpleViewBean(obj, user.getHelpLink(), searchURL, contextPath));
            }

            if (Interaction.class.isAssignableFrom(obj.getClass())) {
                interactionList.add(new SimpleViewBean(obj, user.getHelpLink(), searchURL, contextPath));
            }

            if (Protein.class.isAssignableFrom(obj.getClass())) {
                proteinList.add(new SimpleViewBean(obj, user.getHelpLink(), searchURL, contextPath));
            }

            if (CvObject.class.isAssignableFrom(obj.getClass())) {
                cvObjectList.add(new SimpleViewBean(obj, user.getHelpLink(), searchURL, contextPath));
            }
        } // for

        //now add in the sublists to the in the partitionlist

        if (!expList.isEmpty()) {
            partitionList.add(expList);
        }
        if (!interactionList.isEmpty()) {
            partitionList.add(interactionList);
        }
        if (!proteinList.isEmpty()) {
            partitionList.add(proteinList);
        }
        if (!cvObjectList.isEmpty()) {
            partitionList.add(cvObjectList);
        }

//        //get the maximum size beans from the context for later use
//        Map sizeMap = (Map) session.getServletContext().getAttribute(SearchConstants.MAX_ITEMS_MAP);

        //put the viewbeans in the request and send on to the view...
        request.setAttribute(SearchConstants.VIEW_BEAN_LIST, partitionList);
        //TODO use a Constant here
        return "simpleResults";
    }

}
