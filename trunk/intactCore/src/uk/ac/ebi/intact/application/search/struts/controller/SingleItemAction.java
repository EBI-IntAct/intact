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
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.BasicObject;
import uk.ac.ebi.intact.model.CvDagObject;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class provides the actions required to generate information to be displayed in
 * a 'single object view'. Examples are single Protein views, CvObject views, etc. Typically this class
 * will not be accessed directly - after a search the <code>SearchAction</code>
 * class will forward to this Action class if appropriate.
 *
 * @author Chris Lewington (clewing@ebi.ac.uk)
 * @version $Id$
 */

public class SingleItemAction extends IntactBaseAction {


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

        // The search and help links for links (used by style sheet).
        String relativeSearchLink = getServlet().getServletContext().getInitParameter("searchLink");
        String relativeHelpLink = getServlet().getServletContext().getInitParameter("helpLink");
        String helpLink = request.getContextPath() + relativeHelpLink;
        String searchLink = request.getContextPath() + relativeSearchLink;

        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        //clean out previous single object views
        session.setAttribute(SearchConstants.SINGLE_OBJ_VIEW_BEAN, null);

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);
        if(user == null) {
            //browser page caching screwed up the session - need to
            //get a user object created again by forwarding to welcome action...
            return mapping.findForward(SearchConstants.FORWARD_SESSION_LOST);

        }

        //get an XML builder from the user (always non-null, otherwise an excpetion
        //would have been thrown on user creation)
        XmlBuilder builder = user.getXmlBuilder();

        // The stylesheet for the transformation.
        String xslname = session.getServletContext().getInitParameter(SearchConstants.XSL_FILE);

        //for war files....
        //String xslfile = session.getServletContext().getRealPath(xslname);
        //String xslfile = request.getContextPath() + (xslname);

        String xslfile = session.getServletContext().getResource(xslname).toExternalForm();

        //get the search results from the request
        Collection results = (Collection)request.getAttribute(SearchConstants.SEARCH_RESULTS);
        if(results == null) {
            super.log("Can't display search details - no results saved!");
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }

        super.log("single item action: building view beans...");
        try {

            //get all the shortLabels (for AnnotatedObjects!) from the result set so we
            //can pass them on to view beans for highlighting in web pages...
            String shortLabels = buildShortLabelList(results);

            //should only be one, as this is a single object view, but you never know...
            //Must all be BasicObjects (Institution search???) so cast is OK
            BasicObject searchItem = (BasicObject)results.iterator().next();

            //in this case only want a viewBean wrapping a single (non-experiment)
            //object, expanded as necessary (NB don't care about this bean's ID).....
            BasicObjectViewBean bean = new BasicObjectViewBean(searchItem, xslfile, builder,
                    searchLink, helpLink, null);
            bean.createXml();

            //now expand it...
            String objAc = searchItem.getAc();
            bean.modifyXml(XmlBuilder.EXPAND_NODES, objAc);

            System.out.println("Single Object View: XML after initial expansion:");
            printBean(bean);

            Collection objXrefs = bean.getAcs("Xref");
            bean.modifyXml(XmlBuilder.EXPAND_NODES, objXrefs);
            System.out.println("Single Object View: XML after Xref expansion:");
            printBean(bean);

            Collection objAnnots = bean.getAcs("Annotation");
            bean.modifyXml(XmlBuilder.EXPAND_NODES, objAnnots);
            System.out.println("Single Object View: XML after Annotation expansion:");
            printBean(bean);

            //for DAG objects, need also to expand details of parent and
            //child terms...
            if (CvDagObject.class.isAssignableFrom(searchItem.getClass())) {

                //get the parent and child ACs from the object itself, because
                //CvDagObject is abstract and therefore not represented in any
                //XML representations....
                Collection parentAcs = new ArrayList();
                Collection childAcs = new ArrayList();
                CvDagObject cvObj = (CvDagObject) searchItem;

                Collection parents = cvObj.getParents();
                Collection children = cvObj.getChilds();
                if (!parents.isEmpty()) {
                    for (Iterator it = parents.iterator(); it.hasNext();) {
                        parentAcs.add(((BasicObject) it.next()).getAc());
                    }
                }
                if (!children.isEmpty()) {
                    for (Iterator it = parents.iterator(); it.hasNext();) {
                        parentAcs.add(((BasicObject) it.next()).getAc());
                    }
                }
                //now do it (no ACs, no change :-))
                bean.modifyXml(XmlBuilder.EXPAND_NODES, childAcs);
                bean.modifyXml(XmlBuilder.EXPAND_NODES, parentAcs);

            }
            //set the search details, add the bean to the session for later display
            //and then continue
            //bean.setSearchInfo(buf.toString());
            bean.setSearchInfo(shortLabels);
            session.setAttribute(SearchConstants.SINGLE_OBJ_VIEW_BEAN, bean);
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
