package uk.ac.ebi.intact.application.search.struts.controller;

/**
 * Created by IntelliJ IDEA.
 * User: clewington
 * Date: 30-Jun-2003
 * Time: 14:50:51
 * To change this template use Options | File Templates.
 */

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import uk.ac.ebi.intact.application.search.business.IntactUserIF;
import uk.ac.ebi.intact.application.search.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search.struts.view.BasicObjectViewBean;
import uk.ac.ebi.intact.application.search.struts.view.ProteinPartnersViewBean;
import uk.ac.ebi.intact.application.search.struts.view.AbstractViewBean;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.XmlBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;

/**
 * This class generates the information required and processes specific link
 * requests for the intact Protein view of a search. In general it will be called
 * from both the <code>DetailsAction</code> and also via links presented on the
 * Protein view JSP (which is different to other views and requires different
 * subsequent processing).
 *
 * @author Chris Lewington (clewing@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinAction extends IntactBaseAction {

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
        //---------------- context data --------------------------
        String relativeSearchLink = getServlet().getServletContext().getInitParameter("searchLink");
        String relativeHelpLink = getServlet().getServletContext().getInitParameter("helpLink");
        String relativeExpLink = getServlet().getServletContext().getInitParameter("experimentLink");
        //build the help link out of the context path - strip off the 'search' bit...
        String ctxtPath = (request.getContextPath());
        String relativePath = ctxtPath.substring(0, ctxtPath.lastIndexOf("search"));
        String helpLink = relativePath.concat(relativeHelpLink);

        //String helpLink = request.getContextPath() + relativeHelpLink;
        String searchLink = request.getContextPath() + relativeSearchLink;
        String experimentLink = request.getContextPath() + relativeExpLink;

        //----------- request data ----------------------------
        Map resultToExpSet = (Map)request.getAttribute(SearchConstants.RESULT_EXP_MAP);
        Map expToBean = (Map)request.getAttribute(SearchConstants.EXP_BEAN_MAP);
        Collection results = (Collection)request.getAttribute(SearchConstants.SEARCH_RESULTS);

        //--------------- session data -------------------------
        HttpSession session = super.getSession(request);
        //The Set used to record only the Experiment bean IDs we will be interested
        //in for a partner view. If we got here from DetailAction we know it is
        //non-null, but in case not make sure we have something to work with.
        Set beanFilter = (Set)session.getAttribute(SearchConstants.BEAN_FILTER);
        if(beanFilter == null) {
            beanFilter = new HashSet();    //need to set up a new filter
            session.setAttribute(SearchConstants.BEAN_FILTER, beanFilter);
        }
        beanFilter.clear();
        //request may come from a browser, not just forwarded - so need
        //to clear out any old single object views for consistency
        session.setAttribute(SearchConstants.SINGLE_OBJ_VIEW_BEAN, null);

        //will hold the partner beans we wish to be displayed - seperate from the Experiment
        //beans because we need both around in case a link is clicked. Previously initialised or cleared in DetailAction so
        //we know it is non-null and empty
        Map partnerBeanMap = (Map)session.getAttribute(SearchConstants.PARTNER_BEAN_MAP);

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);
        XmlBuilder builder = user.getXmlBuilder();

        // The stylesheet for the transformation.
        String xslName = session.getServletContext().getInitParameter(SearchConstants.PROTEIN_XSL_FILE);
        String xslFile = session.getServletContext().getResource(xslName).toExternalForm();

        //get the Experiment beans previously generated by the DetailsAction - expected to
        //be non-null and non-empty
        Map idToView = (Map) session.getAttribute(SearchConstants.FORWARD_MATCHES);

        //now do some work..............

        /* ------------------- For 'view related experiments' request: ------------
        *  - get the list of experiment view beans we want from the request
        *  - put them into a filter set for the JSP to check
        *  - forward to results.jsp for display
        */
        String expsToView = request.getParameter(SearchConstants.VIEW_EXPS_LINK);
        if(expsToView != null) {
            System.out.println("partner action: experiment view requested as a link");
            StringTokenizer st = new StringTokenizer(expsToView, ",");
            while(st.hasMoreTokens()) {
                //know we have a beanFilter...
                beanFilter.add(st.nextToken());
            }
            if(beanFilter != null) {
                for(Iterator it = beanFilter.iterator(); it.hasNext();) {
                    System.out.println("partner action: Bean filter value: " + it.next());
                }
            }
            else System.out.println("BEAN FILTER IS NULL EVEN THOUGH SET!!");
            return mapping.findForward(SearchConstants.FORWARD_RESULTS);
        }
        System.out.println("partner action: interaction partner view requested...");


        /*
        * -------------------- For  Protein Partner view: --------------------------
        * - get the result items (Proteins)
        * - get the maps of result AC -> Exp ACs and Exp AC -> Exp beans
        * - for each result item, create a ProteinPartner view bean
        * - set its list of experiment bean IDs
        * - for each Experiment bean containing this Protein, get the Interaction partners
        * - ACs and store them
        * - use the ACs to get the relevant partner Elements from the Experiment bean
        * - add the Elements to the protein view bean
        * - add the pairwise Experiment info to each related protein in the XML tree
        * - finally add the Elements into the protein partner bean
        * NB need a map of IDs to beans again as may have more than one Protein table
        * to display....

        */
        int counter = 0; //used for bean IDs
        System.out.println("partners action: number of results to process: " + results.size());
        for(Iterator it = results.iterator(); it.hasNext(); ++counter) {
            Protein protein = (Protein)it.next();
            //Set of experiment ACs containing this Protein
            Set expSet = (Set)resultToExpSet.get(protein.getAc());
            System.out.println("partners action: number of experiments relevant to protein "
                    + protein.getShortLabel() +": " + expSet.size());

            //set up the partner view bean
            String beanId = Integer.toString(counter);
            ProteinPartnersViewBean bean = new ProteinPartnersViewBean(protein,
                                            xslFile, builder, searchLink, helpLink,
                                            experimentLink, beanId);
            bean.createXml();
            System.out.println("partners action: bean built - XML before processing:");
            printBean(bean);
            //set the top Protein attribute with the IDs of Experiment beans
            String beanIds = getBeanIds(expSet, idToView);
            bean.addAttribute(SearchConstants.BEAN_LIST, beanIds, protein.getAc());
            System.out.println("partners action: bean IDs of related experiments: " + beanIds);

            //now process the partners, Experiment by Experiment....
            System.out.println("partners action: partner Proteins being processed...");
            for(Iterator iter = expSet.iterator(); iter.hasNext();) {
                BasicObjectViewBean expBean = (BasicObjectViewBean)expToBean.get(iter.next());
                System.out.println("XML from Experiment bean:");
                printBean(expBean);
                //now the Experiment bean needs its Proteins expanded.....
                //TBD
                //Protein protein = (Protein)obj;
//                        Collection interactionAcs = getInteractionAcs(protein, experimentAc);
//                        acsToExpand = interactionAcs;
                Collection partnerAcs = getPartnerAcs((Experiment)expBean.getWrappedObject(), protein);
                //get the Elements for each partner, then add the bean ID attribute,
                //allowing for partners occurring in more than one Experiment...
                for(Iterator it1 = partnerAcs.iterator(); it1.hasNext();) {
                    String partnerAc = (String)it1.next();
                    System.out.println("partner AC: " + partnerAc);
                    if(bean.containsAc(partnerAc)) {
                        //found this one before - just update its Experiment bean ID list, but
                        //only if the beanID isn't already in it (handled by the update
                        //method...
                        bean.updateAttribute(SearchConstants.BEAN_LIST, expBean.getBeanId(), partnerAc);

                    }
                    else {
                        Element partnerElem = expBean.getElement(partnerAc);  //this should be a copy!!
                        //need the current Experiment beanID here....
                        partnerElem.setAttribute(SearchConstants.BEAN_LIST, expBean.getBeanId());
                        bean.addElement(partnerElem, "partners");
                    }
                }
            }
            //save in the session for later processing
            partnerBeanMap.put(beanId, bean);
            System.out.println("partners Action: XML after bean processing:");
            printBean(bean);
        }

        //now display
        return mapping.findForward(SearchConstants.FORWARD_RESULTS);
    }

    //----------------------- helper methods -------------------------------------

    /**
     * Builds a comma-separated String of bean IDs that relate to a set
     * of Experiment ACs. This is useful for JSPs to later filter the beans
     * they wish to display.
     * @param expSet Set of Experiment ACs we are interested in
     * @param IdToBean Map of view beans - assumed to be BasicObject beans wrapping
     * Experiments
     * @return String comma-separated String of the bean IDs, or empty String
     * if no matches found.
     */
    private String getBeanIds(Set expSet, Map IdToBean) {

        System.out.println("partners action: generating String of bean IDs for an experiment list..");
        StringBuffer buf = new StringBuffer();
        for(Iterator iter = IdToBean.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry)iter.next();
            BasicObjectViewBean bean = (BasicObjectViewBean)entry.getValue();
            String expAc = ((BasicObject)bean.getWrappedObject()).getAc();
            if(expSet.contains(expAc)) {
                buf.append((String)entry.getKey());
                System.out.println("partners action: Experiment AC: " + expAc);
                System.out.println("partners action: related bean ID: " + entry.getKey());
                //NB just checking for more beans isn't enough - in some cases
                //of a * search, there may be more (but irrelevant) beans, which
                //then ad a ',' and make links incorrect. See below
                if(iter.hasNext()) buf.append(",");
            }
        }
        if(buf.lastIndexOf(",") == buf.length() - 1) {
            //shouldn't have a ',' at the end (may happen for the above
            //reasons) - get rid of it
            buf.deleteCharAt(buf.lastIndexOf(","));
        }
        System.out.println("partners action: bean IDs relevant to experiments: " + buf.toString());
        return buf.toString();
    }

    /**
     * Obtains the ACs of Interaction partners for a given protein, in a given
     * Experiment.
     * @param exp The Experiment we wish to check
     * @param protein The Protein we want the partners of
     * @return  Collection a Collection of the partner ACs, empty if none
     */
    private Collection getPartnerAcs(Experiment exp, Protein protein) {

        //NB We have to be careful here not to modify the Collections of
        //the model objects because the changes are only relevant to the current
        //view and so removals etc would mess up other applications...
        Collection result = new ArrayList();
        Collection expInteractions = exp.getInteraction();
        //get the protein's Interactions, then check for this Experiment and then
        //if it belongs to it, collect the other interaction partners
        Collection proteinComponents = protein.getActiveInstance();
        for(Iterator it = proteinComponents.iterator(); it.hasNext();) {
            Interaction interaction = ((Component)it.next()).getInteraction();
            if(expInteractions.contains(interaction)) {

                //found a relevant Interaction - get the other partners
                for(Iterator iter = interaction.getComponent().iterator(); iter.hasNext();) {
                    Component comp = (Component)iter.next();

                    //ignore the Components that hold the Protein we know about..
                    if(proteinComponents.contains(comp)) continue;
                    Interactor partner = comp.getInteractor();
                    result.add(partner.getAc());
                }
            }
        }
        return result;
    }

    //might be better to put this in utils somewhere...
    private void printBean(AbstractViewBean bean) throws TransformerException,
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
