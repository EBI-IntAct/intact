/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.controller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search3.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.view.beans.*;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Perform the common behaviour of the result Action.
 * <p/>
 * TODO: This class needs refactoring/removing when all new views are in place - it may be possible
 * to integrate the new 'simple' view into here, but currently it needs to generate a LIST of
 * viewbeans, not a SINGLE one (but maybe see partners view as this is similar but is included here
 * in the 'hacked' code below!)
 *
 * @author IntAct Team
 * @version $Id$
 */
public abstract class AbstractResultAction extends IntactBaseAction {

    /**
     * Process the specified HTTP request, and create the corresponding HTTP response (or forward to
     * another web component that will create it). Return an <code>ActionForward</code> instance
     * describing where and how control should be forwarded, or <code>null</code> if the response
     * has already been completed.
     *
     * @param mapping  The ActionMapping used to select this instance
     * @param form     The optional ActionForm bean for this request (if any)
     * @param request  The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @throws IOException      if an input/output error occurs
     * @throws ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws IOException, ServletException {

        //New view - Sept 2004:
        //To get to here we must have something to display, and that we have come to
        //this Action via a link click from the 'simple' JSP (that display is handled by
        //a completely seperate Action not in the AbstractResultAction hierarchy). So
        //we can process the request and send to either a single, partner or detail view..

        // Session to access various session objects. This will create
        //a new session if one does not exist.
        HttpSession session = super.getSession(request);

        // Handle to the Intact User.
        IntactUserIF user = super.getIntactUser(session);
        if (user == null) {
            //just set up a new user for the session - if it fails, need to give up!
            user = super.setupUser(request);
            if (user == null) return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }

        //first check for a tabbed page request on existing data - in this
        //case there are no search results in the request.
        //TODO: Complete the refactoring so all subclasses just implement 'processResults'
        //TODO: This will remove the need for the check here..
        
        String selectedPage = request.getParameter("selectedChunk");
        if ((selectedPage != null) && (!selectedPage.equals(""))) {
            return mapping.findForward(processResults(request, user.getHelpLink()));
        }

        //get the search results from the request
        Collection results = (Collection) request.getAttribute(SearchConstants.SEARCH_RESULTS);

        logger.info("Collection contains " + results.size() + " items.");

        AbstractViewBean bean = null; //used for single results
        List beanList = null;         //used for multiple results

        //**** TESTS ****
        //- checking some refactoring of this classes' code - hopefully we can remove
        //most of the code in this class as it will be handled in the processResults
        //method of subclasses...
        Class resultType = results.iterator().next().getClass();
        //checking refactoring for Protein
        if (Protein.class.isAssignableFrom(resultType)) {
            //use the procesRequest method instead....
            return mapping.findForward(processResults(request, user.getHelpLink()));
        }

        //checking refactoring for Exp/Interaction
        if ((Experiment.class.isAssignableFrom(resultType)) ||
                (Interaction.class.isAssignableFrom(resultType))) {
            //use the procesRequest method instead....
            return mapping.findForward(processResults(request, user.getHelpLink()));
        }

        //**** END OF REFACTORING ***

        //handle a single result first (simplest)..
        if (results.size() == 1) {
            // look here !!

            bean = getAbstractViewBean(results.iterator().next(), user, request.getContextPath());

            if (bean == null) {
                logger.warn("something went wrong - no viewBean created  ");
                super.clearErrors();
                super.addError("error.search", "No data collected in the database.");
                super.saveErrors(request);
                return mapping.findForward(SearchConstants.FORWARD_FAILURE);
            }

            logger.info(bean.getClass() + " created");

            //TODO - Forwards: This can be handled by subclasses which can add view items
            //to a session and return the appropriate forwaard code to here. The
            //explicit bits below should go when the refactoring is done...

            //if we need to display a Protein detail view, go there now
            //TODO this should be removed when the other views are finished
            if (bean instanceof ProteinViewBean) {
                //testing stuff - save the bean and send to the protein view JSP
                logger.info("AbstractResult: going to single Protein view");
                session.setAttribute(SearchConstants.VIEW_BEAN, bean);
                return mapping.findForward("singleProtein");
            }

            //if we need to display a Protein partner view,
            //put the single view bean inside a Collection and go there now
            //(it's too messy to have the JSP check for single objects or Collections
            //and then do basically the same thing in each case)....
            //TODO This should go when the other views are finished
            if (bean instanceof PartnersViewBean) {
                //testing stuff - send to the protein partners view JSP
                beanList = new ArrayList();
                beanList.add(bean);
                session.setAttribute(SearchConstants.VIEW_BEAN_LIST, beanList);
                logger.info("AbstractResult: going to partners view");
                return mapping.findForward("partners");
            }

            //TODO this is not a nice to do that but at the moemnt there's no better way
            //sould remove after the complete refactoring
            if (bean instanceof CvObjectViewBean) {
                //testing stuff - save the bean and send to the protein view JSP
                logger.info(
                        "Content of the Bean " + ((CvObjectViewBean) bean).getObject().toString());

                logger.info("AbstractResult: going to single Object view");
                session.setAttribute(SearchConstants.VIEW_BEAN, bean);
                return mapping.findForward("cvObject");

            }

            //TODO needs reforcatoring
            //sould remove after the complete refactoring
            if (bean instanceof BioSourceViewBean) {
                //testing stuff - save the bean and send to the protein view JSP
                logger.info(
                        "Content of the Bean " + ((BioSourceViewBean) bean).getObject().toString());

                logger.info("AbstractResult: going to single Object view");
                session.setAttribute(SearchConstants.VIEW_BEAN, bean);
                return mapping.findForward("bioSource");

            }

            //the view bean is from old code - just do as before....
            session.setAttribute(SearchConstants.VIEW_BEAN, bean);
        } else {

            //got a multiple result list - if they are Proteins build views for
            //interaction partner summary, but just do as above if not
            //(for now, because the beans views are not yet ready - this will be
            //changed later...)
            if (Protein.class.isAssignableFrom(results.iterator().next().getClass())) {
                beanList = new ArrayList();
                for (Iterator it = results.iterator(); it.hasNext();) {
                    beanList.add(getAbstractViewBean(it.next(), user, request.getContextPath()));
                }
                logger.info("Collection of " + beanList.iterator().next().getClass() + " created");
                session.setAttribute(SearchConstants.VIEW_BEAN_LIST, beanList);

                //send to the protein partners view JSP
                return mapping.findForward("partners");
            } else {
                //multiple result list of something not yet reengineered - proceed
                //as before ....
                //TODO get rid of this code when the other views are done...
                bean = getAbstractViewBean(results, user, request.getContextPath());
                if (bean == null) {
                    logger.warn("something went wrong - no viewBean created");
                    super.clearErrors();
                    super.addError("error.search", "No data collected in the database.");
                    super.saveErrors(request);
                    return mapping.findForward(SearchConstants.FORWARD_FAILURE);
                }

                logger.info(bean.getClass() + " created");
                session.setAttribute(SearchConstants.VIEW_BEAN, bean);

            }

        }

        return mapping.findForward(SearchConstants.FORWARD_RESULTS);
    }

    //default implementation for now to use in testing - avoids subclass rewrites..
    protected String processResults(HttpServletRequest request, String helpLink) {
        return null;
    }

    //////////////////////
    // Abstract methods
    //////////////////////

    /**
     * Builds a view bean depending upon the search result data (eg Experiment, single Protein,
     * etc). Specific subclasses should have a means of creating the appropriate view bean to be
     * forwarded.
     *
     * @param result      The search result - left as type Object to allow subclasses to handle
     *                    either Collections or single items as they require.
     * @param user        The User object, used to obtain various beans.
     * @param contextPath The context path of the servlet - used to build machine-independent
     *                    links.
     * @return AbstractViewBean a view bean appropriate for the display item(s), or null if there
     *         was a problem with either the parameters or in building a bean
     */
    protected abstract AbstractViewBean getAbstractViewBean(Object result, IntactUserIF user,
                                                            String contextPath);


}
