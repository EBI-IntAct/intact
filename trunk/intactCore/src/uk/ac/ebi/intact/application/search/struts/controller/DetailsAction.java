/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.w3c.dom.Document;
import uk.ac.ebi.intact.application.search.business.IntactUserIF;
import uk.ac.ebi.intact.application.search.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search.struts.view.BasicObjectViewBean;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.XmlBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.*;

/**
 * This class provides the actions required to generate the information needed
 * when detailed amounts of data are to be produced. Typically this class
 * will not be accessed directly - after a search the <code>SearchAction</code>
 * class will forward to this Action class if appropriate.
 *
 * @author Chris Lewington (clewing@ebi.ac.uk)
 * @version $Id$
 */

public class DetailsAction extends IntactBaseAction {


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
                                 HttpServletResponse response) throws  Exception {

        // Clear any previous errors.
        super.clearErrors();

        //------------------------ context data ----------------------------------
        // The search and help links for links (used by style sheet).
        String relativeSearchLink = getServlet().getServletContext().getInitParameter("searchLink");
        String relativeHelpLink = getServlet().getServletContext().getInitParameter("helpLink");
        String helpLink = request.getContextPath() + relativeHelpLink;
        String searchLink = request.getContextPath() + relativeSearchLink;


        //------------------- session data ----------------------------------------
        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        //clean out previous single object views and maps - need to do this
        //regardless of which view is required, to ensure consistency
        session.setAttribute(SearchConstants.SINGLE_OBJ_VIEW_BEAN, null);
        Map idToView = (Map) session.getAttribute(SearchConstants.FORWARD_MATCHES);
        idToView.clear();
        //The Set used to record only the Experiment bean IDs we will be interested
        Set beanFilter = (Set)session.getAttribute(SearchConstants.BEAN_FILTER);
        if(beanFilter == null) {
            beanFilter = new HashSet();    //need to set up a new filter
            session.setAttribute(SearchConstants.BEAN_FILTER, beanFilter);
        }
        beanFilter.clear();
        //will hold the partner beans we wish to be displayed - seperate from the Experiment
        //beans because we need both around in case a link is clicked.
        Map partnerBeanMap = (Map)session.getAttribute(SearchConstants.PARTNER_BEAN_MAP);
        if(partnerBeanMap == null) {
            partnerBeanMap = new HashMap();  //holds IDs to partner bean map
            session.setAttribute(SearchConstants.PARTNER_BEAN_MAP,partnerBeanMap);
        }
        partnerBeanMap.clear();

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);
        if(user == null) {
            //browser page caching screwed up the session - need to
            //get a user object created again by forwarding to welcome action...
            return mapping.findForward(SearchConstants.FORWARD_SESSION_LOST);

        }

        //get an XML builder via the user (non-null, or user creation would have failed)
        XmlBuilder builder = user.getXmlBuilder();

        // The stylesheet for the transformation.
        String xslname = session.getServletContext().getInitParameter(SearchConstants.XSL_FILE);

        //for war files....
        //String xslfile = session.getServletContext().getRealPath(xslname);
        //String xslfile = request.getContextPath() + (xslname);

        String xslfile = session.getServletContext().getResource(xslname).toExternalForm();

       //------------------------ request data -------------------------------------

        //get the search results from the request
        Collection results = (Collection)request.getAttribute(SearchConstants.SEARCH_RESULTS);
        if(results == null) {
            super.log("Can't display search details - no results saved!");
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }

        //now do some work...
        super.log("details action: building view beans...");
        try {

            //get all the shortLabels (for AnnotatedObjects!) from the result set so we
            //can pass them on to view beans for highlighting in web pages...
            String shortLabels = buildShortLabelList(results);
            super.log("short labels of results: " + shortLabels);
            super.log("details action - performing XML conversion...");

            int counter = 0;    // The counter for view beans.

            //go through the results and collect all the related Experiments
            //together, then build a view bean for each Experiment and save in
            //the idToView map stored in the session...
            //ALSO maintain a couple of local work Maps as follows:
            // 1) result item AC -> Experiment ACs
            // 2) Experiment AC -> view bean
            // Then to do the next bit:
            // - go through the result, and for each item, use its AC plus the
            //   above maps to get at the relevant bean(s);
            // then expand the beans where required, depending on whether the result
            // item is either a Protein or an Interaction:
            // - if an Interaction just expand them;
            // - if a Protein, get its related interactions and only expand those...
            // - if an Experiment, just expand as per Interaction, but for each
            //Interaction of the Experiment
            Map resultToExpSet = new HashMap();
            Map expToBean = new HashMap();

            //Collect also the result item ACs in case we need them
            Collection resultAcs = new ArrayList();

            //NB Again - this looks like a BeanBuilder class would help here....
            Set experimentSet = new HashSet();
            boolean resultsAreExperiments = false;
            if(results.iterator().next() instanceof Experiment) {
                //just record the result set details for later...
                experimentSet.addAll(results);
                resultsAreExperiments = true;  //saves lots of instanceof checks later
            }
            else {
                Collection relatedExps = new ArrayList();
                for(Iterator it = results.iterator(); it.hasNext();) {
                    Set expAcSet = new HashSet(); //may be a set of Exps per item
                    BasicObject obj = (BasicObject)it.next();
                    resultAcs.add(obj.getAc());  //may need this later - saves doing another iteration
                    relatedExps = user.getHelper().getRelatedExperiments(obj);

                    //record info about item AC to Exp ACs in a Map for later..
                    for(Iterator it1 = relatedExps.iterator(); it1.hasNext();) {
                        String expAc = ((Experiment)it1.next()).getAc();
                        expAcSet.add(expAc);
                    }
                    resultToExpSet.put(obj.getAc(), expAcSet);
                    //NB using a Set handles multiple occurences of the same Experiment
                    experimentSet.addAll(relatedExps);
                }
            }

            //build the beans...
            BasicObjectViewBean bean = null;
            for(Iterator iter = experimentSet.iterator(); iter.hasNext(); ++counter) {

                Experiment exp = (Experiment)iter.next();
                String beanId = Integer.toString(counter);
                bean = new BasicObjectViewBean(exp, xslfile, builder, searchLink,
                        helpLink, beanId);
                bean.createXml();

                //always want to expand Experiment details, but not necessarily
                //Interaction and Protein info..
                expandTree(bean, "Experiment", null);
                //need to expand the Xrefs and Annotations for the Experiment also, so
                //things like eg CvDatabase, CvTopic can be displayed
                Collection expXrefs = bean.getAcs("Xref");
                bean.modifyXml(XmlBuilder.EXPAND_NODES, expXrefs);
                Collection expAnnots = bean.getAcs("Annotation");
                bean.modifyXml(XmlBuilder.EXPAND_NODES, expAnnots);

                if(resultsAreExperiments) {
                    //basically done - just expand all the Interactions per Experiment
                    expandTree(bean, "Interaction", null);
                }

                //tell the bean about the search details....
                bean.setSearchInfo(shortLabels);

                //also record the Exp AC -> bean Map details
                expToBean.put(exp.getAc(), bean);

                //put the bean in the Session map for later processing...
                idToView.put(beanId, bean);
                // Set the map with an empty collection.
                //idToAcList.put(beanId, new ArrayList());
                System.out.println("detail Action: XML for bean wrapping " + exp.getAc() + " :");
                printBean(bean);
            }

            //next, go through the result list and for each item:
            //- get the beans related to the item, then for each one:
            //  -if Protein, pass on to another Action (need different view beans);
            //  - if Interaction, get those ACs from the result set and then expand them,
            //    then mark them as done (ie track them)
            // We need to keep track of which Interactions have been expanded in which
            // beans (Experiments) to avoid them being processed multiple times (there may
            // be more than one reference from a result item to a bean)
            //
            if(results.iterator().next() instanceof Protein) {
                //pass to another Action to handle 'protein partners' view.
                System.out.println("Detail Action: protein partners view requested");

                //expand the relevant Interactions in the Experiment bean...
                //NB may be better off in the ProteinAction?? Also this is
                //done for both Protein and Interaction!! Only difference is the
                //bit in the middle about getting the interaction ACs relevant to
                //the Protein..
                for(Iterator it = results.iterator(); it.hasNext();) {
                    BasicObject obj = (BasicObject)it.next();
                    Set experimentAcSet = (Set)resultToExpSet.get(obj.getAc());

                    //modify the (Experiment) beans relevant to this search item...
                    for(Iterator iter = experimentAcSet.iterator(); iter.hasNext();) {
                        String experimentAc = (String)iter.next();
                        BasicObjectViewBean viewBean = (BasicObjectViewBean)expToBean.get(experimentAc);
                        Protein protein = (Protein)obj;
                        Collection interactionAcs = getInteractionAcs(protein, experimentAc);
                        expandTree(viewBean, "Interaction", interactionAcs);
                    }
                }
                cleanUpBeans(idToView);  //get rid of unwanted compact elements
                request.setAttribute(SearchConstants.EXP_BEAN_MAP,expToBean); //useful
                request.setAttribute(SearchConstants.RESULT_EXP_MAP, resultToExpSet); //useful
                return mapping.findForward(SearchConstants.FORWARD_PROTEIN_ACTION);
            }
            else {

                //query result must be an Experiment or Interaction -
                //if Experiment then we are already done; if Interaction, proceed..
                if(results.iterator().next() instanceof Interaction) {

                    System.out.println("Detail Action: Interaction expansion requested");
                    for(Iterator it = results.iterator(); it.hasNext();) {

                        //first get the list of  Experiment ACs for this item
                        BasicObject obj = (BasicObject)it.next();
                        Set experimentAcSet = (Set)resultToExpSet.get(obj.getAc());

                        //modify the (Experiment) beans relevant to this search item...
                        for(Iterator iter = experimentAcSet.iterator(); iter.hasNext();) {
                            String experimentAc = (String)iter.next();
                            BasicObjectViewBean viewBean = (BasicObjectViewBean)expToBean.get(experimentAc);

                            //now do the expansion (we only expand Interactions currently)
                            expandTree(viewBean, "Interaction", resultAcs);
                            System.out.println("detail Action: bean for " + experimentAc + " after expansion:");
                            printBean(bean);
                        }
                    }

                    //now get rid of compact elements we want to leave out.
                    //XSL is only any use for static ops - this means we can't just hide
                    //the Interactions we don't want to see, as whether or not we want
                    //them depends on user interaction, which is dynamic. So we have to
                    //be rather messy and remove the unwanted ones for this particular
                    //view from the XML managed by the bean....
                    //NB would still be better not to expand unwanted elements in the first place!
                    //Q: do we need this for Interaction expansions?
                    cleanUpBeans(idToView);
                }
            }

            //make sure the JSP can display the appropriate view buttons.
            //NB does this need to be done in all cases?
            request.setAttribute(SearchConstants.PROTEIN_VIEW_BUTTON, "Detail View");

            // Move to the results page.
            return mapping.findForward(SearchConstants.FORWARD_RESULTS);
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

//-------------------------- private helper methods -----------------------------------

    /**
     * Create a comma-seperated String listing all of the shortLabels in
     * a search result Collection. NB this only applies to AnnotatedObjects as
     * others do not have shortLabel attributes.
     * @param items The Collection of objects to check
     * @return String comma-seperated list of shortLabels, or String of length 0 if something
     * is not as it should be.
     */
    private String buildShortLabelList(Collection items) {

        StringBuffer buf = new StringBuffer();
        //better to do only a single type check - assumes all Collection
        //objects are the same type....
        if(items.isEmpty()) return buf.toString();
        if(!((items.iterator().next()) instanceof AnnotatedObject)) return buf.toString();
        for(Iterator it = items.iterator(); it.hasNext();) {
            buf.append(((AnnotatedObject)it.next()).getShortLabel());
            if(it.hasNext()) buf.append(",");
        }
        return buf.toString();
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
     * @param acList A Collection of specific ACs of this tag type to be expanded. If null, then
     * by default all elements with the specified taqg name will be expanded.
     *
     * @exception IntactException thrown if there was a modify problem
     * @exception ParserConfigurationException if there was a problem obtaining the XML from the bean
     */
    private void expandTree(BasicObjectViewBean bean, String tagName, Collection acList) throws ParserConfigurationException, IntactException {

        //Basic algorithm:
        //depending on the tag name, either simply expand in full (Protein)
        //or expand in full then call expandTree again with the next tag type down.
        //For example, an Experiment should then move to Interactions. the recursion will
        //take care of the rest if each tag type is handled correctly.
        if (acList == null) {
            //find all the ACs for this tag type in the bean, then expand them
            Collection localAcList = new ArrayList();
            localAcList = bean.getAcs(tagName);
            //only expand if they are not done already...
            //Done most easily by retaining only those ACs from the list
            //which we know are currently compact for this bean..
            bean.modifyXml(XmlBuilder.EXPAND_NODES, bean.retainCompact(localAcList));
        }
        else {
            //only expand if they are not done already...
            bean.modifyXml(XmlBuilder.EXPAND_NODES, bean.retainCompact(acList));
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
    }

    /**
     * obtains the ACs of Interactions for a particular Protein.
     * @param protein The Protein we are interested in
     * @param experimentAc The AC of the experiment that contains the Protein
     * @return Collection The Collection of ACs of the Interactions that refer to the Protein (empty if none)
     */
    private Collection getInteractionAcs(Protein protein, String experimentAc) {

        Collection resultList = new ArrayList();

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
                    System.out.println("Interaction in Experiment " + expAc
                            + " containing Protein " + protein.getAc()
                            + " : " + interaction.getAc());
                }
            }
        }

        return resultList;

    }

    /**
     * For Protein searches, this method tidies up view beans wrapping
     * experiments. It removes redundant compact elements and leaves expanded ones.
     * The method assumes that beans wrap experiments, as these are the only ones
     * that need cleaning up currently.
     * @param idToView to access the bean.
     */
    private void cleanUpBeans(Map idToView)
            throws ParserConfigurationException {
        for (Iterator iter = idToView.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            BasicObjectViewBean bean = (BasicObjectViewBean)entry.getValue();
            Experiment exp = (Experiment) bean.getWrappedObject();

            //gather the Experiment's Interaction ACs
            Collection removeAcs = new ArrayList();
            for (Iterator iter1 = exp.getInteraction().iterator(); iter1.hasNext();) {
                Object obj = iter1.next();
                if (obj instanceof Interaction) {
                    removeAcs.add(((Interaction) obj).getAc());
                }
            }
            //now find the Experiment's Interactions that are compact and
            //remove the elements in the bean for them
            removeAcs.removeAll(bean.getExpanded());
            bean.removeElements(removeAcs);

            //now reset the view state to the Protein view.
            // The AC for Proteins need to set compact for this bean.
            Collection proteinAcs = bean.getAcs("Protein");
            bean.resetStatus(proteinAcs, XmlBuilder.CONTRACT_NODES);
        }
    }

    //might be better to put this in utils somewhere...
    private void printBean(BasicObjectViewBean bean) throws TransformerException,
            ParserConfigurationException {
        //using System.out for logging as we want the bean info, which
        //does not have access to a logger...
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //adds whitespace
        Document doc1 = bean.getAsXmlDoc();
        transformer.transform(new DOMSource(doc1), new StreamResult(System.out));
        System.out.println();
    }
}
