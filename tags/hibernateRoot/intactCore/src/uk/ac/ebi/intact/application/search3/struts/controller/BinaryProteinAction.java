package uk.ac.ebi.intact.application.search3.struts.controller;

import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.util.ProteinUtils;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.view.beans.PartnersViewBean;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.Interactor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * This Action class performs the calculating and the construction of view beans that will be used for a for an url
 * based search query for 2 protein. It will calculate the interactions and give back the code to forward to the
 * coresponding JSP site for the representation of the results. This method performances the Request
 * from the url-based query with http://www.ebi.ac.uk/intact/search/do/search?binary=protein1,protein2.
 * It calculates all binary and all self interactions between these 2 proteins and returns
 * code to forward to the specific view.
 *
 * @author Michael Kleen
 * @version BinaryProteinAction.java Date: Jan 14, 2005 Time: 12:53:45 PM
 */
public class BinaryProteinAction extends AbstractResultAction {


    /**
     * This method overrides the parent one to process the request more effectively. It avoids
     * making any assumptions about the beans or the size of search result list and keep all of the
     * processing in a single place for each Action type.
     *
     * @param request  The request object containing the data we want
     * @param helpLink The help link to use
     * @return String the return code for forwarding use by the execute method
     */
    protected String processResults(HttpServletRequest request, String helpLink) {

        logger.info("binary protein action");

        final Collection someInteractors = (Collection) request.getAttribute(SearchConstants.SEARCH_RESULTS);
        Collection results = Collections.EMPTY_LIST;

        logger.info("resultset size " + someInteractors.size());
        HttpSession session = super.getSession(request);

        // first check for self interactions

        // String appPath = getServlet().getServletContext().getInitParameter("searchLink");
//        String searchURL = request.getContextPath().concat(appPath);
        String searchURL = super.getSearchURL();

        if (someInteractors.size() == 1) {
            Collection beanList = new ArrayList(1);
            logger.info("Binary Protein Action: one 1 Protein");

            final Interactor selfInteractor = (Interactor) someInteractors.iterator().next();
            results = ProteinUtils.getSelfInteractions(selfInteractor);

            boolean hasSelfInteraction = results.size() > 0;

            if (hasSelfInteraction) {
                logger.info("BinaryAction: protein has a self interaction ");
                beanList.add(new PartnersViewBean(selfInteractor, true, helpLink, searchURL,
                                                  request.getContextPath()));
                request.setAttribute(SearchConstants.VIEW_BEAN_LIST, beanList);
                return SearchConstants.FORWARD_PARTNER_VIEW;

            }

        }
        else if (someInteractors.size() == 2) {
            logger.info("binary interactions");

            // we got more than 1 protein, so check for binary Interactions between them
//            try {
            results = ProteinUtils.getBinaryInteractions(someInteractors);
            logger.info("results interactions size : " + results.size());
//            }
//            catch (IntactException e) {
//                logger.info("wrong datatype, forward to errorpage");
//                return SearchConstants.FORWARD_FAILURE;
//            }

        }
        else {

            // If we got more than 2 proteins forward to errorpage
            logger.info("more than 2 Proteins, forward to errorpage");
            return SearchConstants.FORWARD_TOO_MANY_INTERACTORS;
        }

        if (!results.isEmpty()) {
            logger.info("search sucessful");
            //TODO use session here
            request.setAttribute(SearchConstants.SEARCH_RESULTS, results);
            // the simple action handle the prasentation of the interactions
            return SearchConstants.FORWARD_SIMPLE_ACTION;

        }
        else {


            logger.info("no interactions found between these proteins resultset empty");
            // create statistic
            String info = (String) session.getAttribute("binary");
            StringTokenizer st = new StringTokenizer(info, ",");
            Collection query = new ArrayList(results.size());

            while (st.hasMoreTokens()) {
                String value = st.nextToken();
                query.add(value);
            }

            logger.info("forward to no interactions view");
            // add the statistics to the request and forward to the no interactions jsp
            request.setAttribute(SearchConstants.RESULT_INFO, query);

            return SearchConstants.FORWARD_NO_INTERACTIONS;
        }
    }
}

