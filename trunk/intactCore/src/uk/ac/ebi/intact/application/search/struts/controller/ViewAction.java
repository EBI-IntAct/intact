/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.controller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.w3c.dom.Document;
import uk.ac.ebi.intact.application.search.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search.struts.view.BasicObjectViewBean;
import uk.ac.ebi.intact.application.search.struts.view.ViewForm;
import uk.ac.ebi.intact.business.IntactException;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


/**
 * Process the user submitted search form.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk), Chris Lewington (clewing@ebi.ac.uk)
 * @version $Id$
 */
public class ViewAction extends IntactBaseAction {

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
                                 HttpServletResponse response) throws Exception {

        HttpSession session = getSession(request);

        // The collection of beans to process.
        Map idToView = (Map) session.getAttribute(SearchConstants.FORWARD_MATCHES);

        //first check for a CvObject - if there is one in the session
        //then the 'back' button has been pressed from a CvObject view before
        //coming to here, so we need to clear it to get the correct display
        session.setAttribute(SearchConstants.SINGLE_OBJ_VIEW_BEAN, null);

        //debug stuff...
        System.out.println("new view requested...");
        int mode = XmlBuilder.CONTRACT_NODES;
        String buttonLabel = "Detail View";
        if (((ViewForm) form).expandSelected()) {
            mode = XmlBuilder.EXPAND_NODES;
            buttonLabel = "Compact View";
        }
        try {
            //for every view bean, set the status of every Protein & Iteraction as
            // views are same.
            for (Iterator it = idToView.values().iterator(); it.hasNext();) {
                BasicObjectViewBean bean = (BasicObjectViewBean) it.next();

                System.out.println("expand/contract selected");
                System.out.println("XML before transform..");
                System.out.println();
                printBean(bean);

                //if expand is needed, we also need to do the Components
                //before setting the Protein status, because that is the only way
                //to get at them...

                if (mode == XmlBuilder.EXPAND_NODES) {

                    //calls to setStatus MUST be in the correct order here to
                    //ensure Xrefs can be expanded properly - that is why
                    //the calls cannot be done outside the conditionl..
                    setStatus(bean, "Interaction", mode);
                    Collection componentAcs = bean.getAcs("Component");
                    bean.modifyXml(XmlBuilder.EXPAND_NODES, componentAcs);
                    setStatus(bean, "Protein", mode);

                    //need to expand the Xrefs and Annotations for Interactions and Proteins also.
                    Collection expXrefs = bean.getAcs("Xref");
                    bean.modifyXml(XmlBuilder.EXPAND_NODES, expXrefs);
                    Collection annotationAcs = bean.getAcs("Annotation");
                    bean.modifyXml(XmlBuilder.EXPAND_NODES, annotationAcs);
                }
                else {

                    //compact view required - this is basically what was done
                    //for the first view, so simplest way to handle it is to forward back to
                    //the search action with the original search criteria specified in the form...
                    String searchValue = (String)session.getAttribute(SearchConstants.LAST_VALID_SEARCH);
                    DynaActionForm dyForm = (DynaActionForm)session.getAttribute(SearchConstants.SEARCH_FORM);
                    if(dyForm != null) dyForm.set("searchString", searchValue);
                    return mapping.findForward("proteinView");
                }

                System.out.println("expand/contract selected");
                System.out.println("XML after transform..");
                System.out.println();
                printBean(bean);
            }
            request.setAttribute(SearchConstants.PROTEIN_VIEW_BUTTON, buttonLabel);
        }
        catch (ParserConfigurationException e) {
            //just to pick up the transformer exceptions
        }
        // Move to the results page.
        return mapping.findForward(SearchConstants.FORWARD_RESULTS);
    }

    //--------------------------- private helper methods -------------------------------

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

    private void setStatus(BasicObjectViewBean bean, String tagName, int mode)
            throws ParserConfigurationException, IntactException {
        Collection acs = bean.getAcs(tagName);
        bean.modifyXml(mode, acs);
        bean.resetStatus(acs, mode);
    }
}
