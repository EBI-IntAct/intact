/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.controller;

import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.collections.ListUtils;

import javax.servlet.http.*;

import uk.ac.ebi.intact.application.search.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search.business.IntactUserIF;
import uk.ac.ebi.intact.application.search.struts.view.IntactViewBean;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactException;

import uk.ac.ebi.intact.util.*;

/**
 * This class provides the actions required for the search page
 * for intact. The search criteria are obtained from a Form object
 * and then the search is carried out, via the IntactUser functionality,
 * which provides the business logic.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk), modified by Chris Lewington
 * @version $Id$
 */

public class SearchAction extends IntactBaseAction {

    /**
     * The search order. The search is done in this order.
     */
    public static final String[] SEARCH_ORDER = new String[]{
        "Protein", "Interaction", "Experiment"
    };

    /**
     * Process the specified HTTP request, and create the corresponding
     * HTTP response (or forward to another web component that will create
     * it). Return an ActionForward instance describing where and how
     * control should be forwarded, or null if the response has
     * already been completed.
     *
     * @param mapping - The <code>ActionMapping</code> used to select this instance
     * @param form - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     *
     * @return - represents a destination to which the controller servlet,
     * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     * or HttpServletResponse.sendRedirect() to, as a result of processing
     * activities of an <code>Action</code> class
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        // Clear any previous errors.
        super.clearErrors();

        // The search link for links (used by style sheet).
        String link = getServlet().getServletContext().getInitParameter("searchLink");

        DynaActionForm dyForm = (DynaActionForm) form;
        String searchValue = ((String) dyForm.get("searchString")).toUpperCase();
        String searchClass = (String) dyForm.get("searchClass");
        dyForm.set("searchClass", "");

        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);

        //now need to try searches based on AC, label or name (only
        //ones we will accept for now) and return as soon as we get a result
        //NB obviously can't distinguish between a zero return and a search
        //with garbage input using this approach...

        //get or create (if necessary) an XML builder via the Session
        XmlBuilder builder = (XmlBuilder) session.getAttribute(SearchConstants.XML_BUILDER);
        if (builder == null) {
            //create one and save in session, making sure the builder reuses
            //the current user's helper so the DB access cocnfiguration is correct
            try {
                builder = new XmlBuilder(user.getHelper());
                session.setAttribute(SearchConstants.XML_BUILDER, builder);
            }
            catch (IntactException ie) {

                //build and forward error....
                super.log(ExceptionUtils.getStackTrace(ie));
                // The errors to report back.
                super.addError("error.search", ie.getMessage());
                super.saveErrors(request);
                return mapping.findForward(SearchConstants.FORWARD_FAILURE);
            }
        }


        // The stylesheet for the transformation.
        String xslname = session.getServletContext().getInitParameter(SearchConstants.XSL_FILE);
        String xslfile = session.getServletContext().getRealPath(xslname);

        // Holds the result from the initial search.
        Collection results = null;

        //now need to try searches based on AC, label or name (only
        //ones we will accept for now) and return as soon as we get a result
        //NB obviously can't distinguish between a zero return and a search
        //with garbage input using this approach...
        super.log("search action: attempting to search by AC first...");
        try {

            results = doLookup(searchClass, searchValue, user);
            if (results.isEmpty()) {
                //try searching first using all uppercase, then all lower case if it returns nothing...
                //NB this would be better done at the DB level, but keep it here for now
                String upperCaseValue = searchValue;
                results = doLookup(searchClass, upperCaseValue, user);
                if (results.isEmpty()) {

                    //now try all lower case....
                    String lowerCaseValue = searchValue.toLowerCase();
                    results = doLookup(searchClass, lowerCaseValue, user);
                    if (results.isEmpty()) {
                        //finished all current options, and still nothing - return a failure
                        super.log("No matches were found for the specified search criteria");
                        // Save the search parameters for results page to display.
                        session.setAttribute(SearchConstants.SEARCH_CRITERIA,
                                user.getSearchCritera() + "=" + searchValue);
//                        session.setAttribute(SearchConstants.SEARCH_TYPE, searchClass);
                        return mapping.findForward(SearchConstants.FORWARD_NO_MATCHES);
                    }

                }
            }
            super.log("search action: search results retrieved OK");

            // ************* Search was a success. ********************************

            // Retrieve the map to add matching results.
            Map idToView = (Map) session.getAttribute(SearchConstants.FORWARD_MATCHES);
            // Remove any previous views.
            idToView.clear();

            // Save the search parameters for results page to display.
            session.setAttribute(SearchConstants.SEARCH_CRITERIA,
                    user.getSearchCritera() + "=" + searchValue);
            super.log("found results - performing XML conversion...");
            //assume search results are all the same type (we know it is not empty by here)...
            Object searchItem = results.iterator().next();
            int counter = 0;    // The counter for view beans.

            if (!(searchItem instanceof Experiment)) {
                // Maintains the list of protein related interactions which
                // have already processed.
                // Maps bean id -> list of interactions to be removed later.
                Map idToAcList = new HashMap();

                //Just in case we need to handle Interactions,
                //get the ACs of the result set
                List resultAcs = new ArrayList();
                if (searchItem instanceof Interaction) {
                    for (Iterator it2 = results.iterator(); it2.hasNext();) {
                        Interaction interaction = (Interaction) it2.next();
                        resultAcs.add(interaction.getAc());
                    }
                }
                //go through the search results and build beans as required...
                Iterator it = results.iterator();
                Map doneExperiments = new HashMap();
                boolean isNew = false;
                while (it.hasNext()) {

                    //need to get any related objects - eg if a Protein we want to
                    //see the experiments and interactions etc. In general we would expect
                    //the results to be all the same type, and related objects we want to be
                    //Experiments (eg when Proteins or Interactions have been searched for)
                    Object item = it.next();
                    Collection relatedObjs = user.getHelper().getRelatedObjects(item);
                    // Store the view beans in a map.
                    for (Iterator iter = relatedObjs.iterator(); iter.hasNext(); ++counter) {

                        String beanId = Integer.toString(counter);
                        IntactViewBean bean = null;

                        //handle redundancy, eg if some Proteins occur in multiple places
                        //within an Experiment we should only have one bean for the Experiment,
                        //not as many beans as there are matches..
                        Set expKeys = doneExperiments.keySet();
                        Experiment experiment = (Experiment) iter.next();
                        String experimentAc = experiment.getAc();
                        if (expKeys.contains(experimentAc)) {
                            //done this experiment before - modify it rather than create
                            //a new view bean...
                            beanId = (String) doneExperiments.get(experimentAc);
                            bean = (IntactViewBean) idToView.get(beanId);
                            isNew = false;
                        }
                        else {
                            // Construct a view bean for each search result.

                            bean = new IntactViewBean(experiment, xslfile, builder, link);
                            bean.createXml();
                            idToView.put(beanId, bean);
                            // Set the map with an empty collection.
                            idToAcList.put(beanId, new ArrayList());
                            isNew = true;
                        }
                        //know the view object is an Experiment - we only
                        //want to expand the Interactions that contain the Protein
                        //that was searched on, and leave the rest compact...
                        if (isNew) {
                            expandTree(bean, "Experiment", null);
                        }

                        //need to expand the Xrefs and Annotations for the Experiment also, so
                        //things like eg CvDatabase, CvTopic can be displayed
                        List expXrefs = bean.getAcs("Xref");
                        bean.modifyXml(XmlBuilder.EXPAND_NODES, expXrefs);
                        List expAnnots = bean.getAcs("Annotation");
                        bean.modifyXml(XmlBuilder.EXPAND_NODES, expAnnots);

                        //now we have data in a bean we need to generate the required
                        //XML, based upon the object in the bean. For example, if it is
                        //an Experiment then it needs expanding down to its Proteins, because
                        //that is going to be the new initial view. BUT, this also depends on what
                        //was searched for - if a Protein then the above holds, but if an
                        //Interaction was searched for then we only expand down to Interactions..
                        if (item instanceof Protein) {
                            //done the rest of the Experiment - now do the specific Interactions
                            //we want.......
                            Protein protein = (Protein) item;
                            System.out.println("AC of the searched-for Protein: " + protein.getAc());
                            System.out.println("AC of experiment containing this Protein: " + experimentAc);
                            System.out.println("Interactions containing this Protein:");
                            List interactionAcs = getInteractionAcs(protein, experimentAc);
                            expandTree(bean, "Interaction", interactionAcs);
                            // Interactions done so far for this bean.
                            List doneInteractions = (List) idToAcList.get(beanId);
                            doneInteractions.addAll(interactionAcs);        //record that we have done these
                        }
                        else {
                            // Must be an Interaction!!
                            //just do the Experiment (XSL should hide the ones we don't want)
                            //NB BUT to do this we probably need to keep a track here of the ACs
                            //of the search results so thatthey are the only ones to be displayed.
                            //Also they somehow need to get passed to the Stylesheet for this!!

                            //get all the Experiment's interaction ACs, then filter out the ones
                            //we are interested in and use the remaining ones to remove
                            //the XML elements in the bean...
                            Collection interactions = experiment.getInteraction();
                            List interactionAcs = new ArrayList();
                            for (Iterator iter1 = interactions.iterator(); iter1.hasNext();) {
                                interactionAcs.add(((Interaction) iter1.next()).getAc());
                            }
                            List acsToKeep = ListUtils.intersection(resultAcs, interactionAcs);
                            expandTree(bean, "Interaction", acsToKeep);
                            // Interactions done so far for this bean.
                            List doneInteractions = (List) idToAcList.get(beanId);
                            //record that we have done these
                            doneInteractions.addAll(acsToKeep);
                        }
                        // Collect the results together (if necessary)...
                        if (isNew) {
                            //record that this bean has been done...
                            doneExperiments.put(experimentAc, beanId);
                        }
                        super.log("view bean for related objects to " + searchItem.getClass().getName() + " built OK...");
                    }
                }
                // if Proteins or Interactions searched, get rid of compact elements
                //we want to leave out.
                if (!(searchItem instanceof Experiment)) {
                    //XSL is only any use for static ops - this means we can't just hide
                    //the Interactions we don't want to see, as whether or not we want
                    //them depends on user interaction, which is dynamic. So we have to
                    //be rather messy and remove the unwanted ones for this particular
                    //view from the XML managed by the bean....
                    cleanUpBeans(idToAcList, idToView);
                    // Store the state in a session, so the JSP can display the
                    // appropriate view buttons.
                    request.setAttribute(SearchConstants.PROTEIN_VIEW_BUTTON, "Detail View");
                }
            }
            else {
                //just build beans for the Experiments (no need to get the
                //related objects of them) - similar code - may refactor later when I have time!!
                for (Iterator iter = results.iterator(); iter.hasNext(); ++counter) {

                    // Construct a view bean for each search result.
                    IntactViewBean bean = new IntactViewBean(iter.next(), xslfile, builder, link);
                    bean.createXml();

                    //need to expand the Xrefs for the Experiment also, so
                    //things like eg CvDatabase can be displayed
                    expandTree(bean, "Experiment", null);
                    List expXrefs = bean.getAcs("Xref");
                    bean.modifyXml(XmlBuilder.EXPAND_NODES, expXrefs);

                    // Collect the results together...
                    idToView.put(Integer.toString(counter), bean);

                    super.log("Experiment view bean built OK...");
                }
            }
            // Move to the results page.
            return mapping.findForward(SearchConstants.FORWARD_RESULTS);
        }
        catch (IntactException se) {
            // Something failed during search...
            super.log(ExceptionUtils.getStackTrace(se));
            // The errors to report back.
            super.addError("error.search", se.getNestedMessage());
            super.saveErrors(request);
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }
        catch (TransformerException te) {
            // Unable to create a transformer for given stylesheet.
            super.log(ExceptionUtils.getStackTrace(te));
            // The errors to report back.
            super.addError("error.search", te.getMessage());
            super.saveErrors(request);
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }
        catch (ParserConfigurationException pe) {
            // Unable to create a transformer for given stylesheet.
            super.log(ExceptionUtils.getStackTrace(pe));
            // The errors to report back.
            super.addError("error.search", pe.getMessage());
            super.saveErrors(request);
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }
    }

    // Helper methods.

    /**
     * utility method to handle the logic for lookup, ie trying AC, label etc.
     * Isolating it here allows us to change initial strategy if we want to.
     * NB this will probably be refactored out into the IntactHelper class later on.
     *
     * @param searchClass The class to search on (only comes from a link clink) - useful for optimizing
     * search
     * @param value the user-specified value
     * @param user The object holding the IntactHelper for a given user/session
     * (passed as a parameter to avoid using an instance variable, which may
     *  cause thread problems).
     *
     * @return Collection the results of the search - an empty Collection if no results found
     *
     * @exception IntactException thrown if there were any search problems
     */
    private Collection doLookup(String searchClass, String value, IntactUserIF user)
            throws IntactException {

        Collection results = new ArrayList();
        String packageName = AnnotatedObject.class.getPackage().getName() + ".";
        if(searchClass.length() == 0) {

            //have to go through the possible classes in order...
            for (int i = 0; i < SEARCH_ORDER.length; i++) {
                // The class name associated with the search request.
                String className = packageName + SEARCH_ORDER[i];
                //try search on AC first...
                results = doSearch(className, value, user);
                if (!results.isEmpty()) {
                    break;
                }
            }
        }
        else {
            String className = packageName + searchClass;
            results = doSearch(className, value, user);
        }
        return results;
    }

    /**
         * utility method to handle the logic for lookup, ie trying AC, label etc.
         * Isolating it here allows us to change initial strategy if we want to.
         * NB this will probably be refactored out into the IntactHelper class later on.
         *
         * @param className The class to search on (only comes from a link clink) - useful for optimizing
         * search
         * @param value the user-specified value
         * @param user The object holding the IntactHelper for a given user/session
         * (passed as a parameter to avoid using an instance variable, which may
         *  cause thread problems).
         *
         * @return Collection the results of the search - an empty Collection if no results found
         *
         * @exception IntactException thrown if there were any search problems
         */
        private Collection doSearch(String className, String value, IntactUserIF user)
                throws IntactException {

            Collection results = new ArrayList();


        //try search on AC first...
        results = user.search(className, "ac", value);
        if (results.isEmpty()) {
            // No matches found - try a search by label now...
            super.log("now searching for class " + className + " with label " + value);
            results = user.search(className, "shortLabel", value);
            if (results.isEmpty()) {
                //no match on label - try by xref....
                super.log("no match on label - looking for: " + className + " with primary xref ID " + value);
                Collection xrefs = user.search(Xref.class.getName(), "primaryId", value);

                //could get more than one xref, eg if the primary id is a wildcard search value -
                //then need to go through each xref found and accumulate the results...
                Iterator it = xrefs.iterator();
                Collection partialResults = new ArrayList();
                while (it.hasNext()) {
                    partialResults = user.search(className, "ac", ((Xref) it.next()).getParentAc());
                    results.addAll(partialResults);
                }

                if (results.isEmpty()) {
                    //no match by xref - try finally by name....
                    super.log("no matches found using ac, shortlabel or xref - trying fullname...");
                    results = user.search(className, "fullName", value);
                }
            }
        }

        return results;
        }


    /**
     * Method to recursively expand the Elements of a tree.
     * Note: this is intact-specific, so for example all expansions stop
     * at an Xref element, and also any CvObjects are only generated to a compact level
     * due to the recursive constraints they impose on the XML tree. For each new AC within
     * a bean's tree that is expanded, the AC+bean id key information is also updated to expand mode
     * to ensure cache and view consistency.
     *
     * @param bean The bean whose Document is to be expanded
     * @param tagName The name of the type of tags which are to be expanded
     * @param acList A list of specific ACs of this tag type to be expanded. If null, then
     * by default all elements with the specified taqg name will be expanded.
     *
     * @exception IntactException thrown if there was a modify problem
     * @exception ParserConfigurationException if there was a problem obtaining the XML from the bean
     */
    private void expandTree(IntactViewBean bean, String tagName, List acList) throws ParserConfigurationException, IntactException {

        //Basic algorithm:
        //depending on the tag name, either simply expand in full (Protein)
        //or expand in full then call expandTree again with the next tag type down.
        //For example, an Experiment should then move to Interactions. the recursion will
        //take care of the rest if each tag type is handled correctly.
        if (acList == null) {
            //find all the ACs for this tag type in the bean, then expand them
            List localAcList = new ArrayList();
            localAcList = bean.getAcs(tagName);
            bean.modifyXml(XmlBuilder.EXPAND_NODES, localAcList);
        }
        else {
            bean.modifyXml(XmlBuilder.EXPAND_NODES, acList);
        }



        //update the state, just to be sure
        //this.updateViewState(modes, id, XmlBuilder.EXPAND_NODES);

        //now consider each tag type in turn we are interested in...
        if (tagName.equals("Protein")) {
            //base case.....do nothing more
            return;
        }
        if (tagName.equals("Interaction")) {
            //do the Components, but leave the Proteins as compact for now..
            expandTree(bean, "Component", null);
            //expandTree(bean, "Protein", null);
        }
        //if(tagName.equals("Experiment")) {
        //for an Experiment we want to expand the Interactions
        //containing the the Protein ONLY
        //expandTree(bean, "Interaction");
        //}

    }

    /**
     * obtains the ACs of Interactions for a particular Protein.
     * @param protein The Protein we are interested in
     * @param experimentAc The AC of the experiment that contains the Protein
     * @return List The list of ACs of the Interactions that refer to the Protein (empty if none)
     */
    private List getInteractionAcs(Protein protein, String experimentAc) {

        List resultList = new ArrayList();

        //the active instance refers to the Components - this isn't documented
        //though, but by looking at the code activeInstance should contain Components...
        Collection components = protein.getActiveInstance();
        //get the Interactions containing this Protein, for the Experiment
        //we want...
        Iterator it = components.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (!(obj instanceof Component)) {
                //something very odd - didn't get Components back from the Protein!!
                System.out.println("no valid Components for this Protein!");
                break;
            }
            Component component = (Component) obj;
            Interaction interaction = component.getInteraction();
            Collection experiments = interaction.getExperiment();
            Iterator iter = experiments.iterator();
            while (iter.hasNext()) {
                Object item = iter.next();
                if (!(item instanceof Experiment)) {
                    //something very odd - didn't get Experiments back from the Interaction!!
                    System.out.println("no valid Experiments for this Interaction!");
                    break;
                }
                Experiment experiment = (Experiment) item;
                String expAc = experiment.getAc();
                if (expAc.equals(experimentAc)) {
                    resultList.add(interaction.getAc());
                    System.out.println(interaction.getAc());
                }
            }
        }

        return resultList;

    }

    /**
     * For Protein searches, this method tidies up view beans wrapping
     * experiments. It removes compact elements and leaves expanded ones.
     * @param map contains bean id to ACs to keep.
     * @param idToView to access the bean.
     */
    private void cleanUpBeans(Map map, Map idToView)
            throws ParserConfigurationException {
        for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            String beanId = (String) entry.getKey();
            Collection acsToKeep = (Collection) entry.getValue();
            IntactViewBean bean = (IntactViewBean) idToView.get(beanId);

            Experiment exp = (Experiment) bean.getWrappedObject();
            List removeAcs = new ArrayList();
            for (Iterator iter1 = exp.getInteraction().iterator(); iter1.hasNext();) {
                Object obj = iter1.next();
                if (obj instanceof Interaction) {
                    removeAcs.add(((Interaction) obj).getAc());
                }
            }
            //now clear the Experiment's interaction AC list of the ones
            //we have already done, and leave the ones to be removed in it
            removeAcs.removeAll(acsToKeep);
            bean.removeElements(removeAcs);

            //now reset the view state to the Protein view.
            // The AC for Proteins need to set compact for this bean.
            List proteinAcs = bean.getAcs("Protein");
            bean.resetStatus(proteinAcs, XmlBuilder.CONTRACT_NODES);
        }
    }
}
