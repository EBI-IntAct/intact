/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.controller;

import uk.ac.ebi.intact.application.search.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search.struts.framework.util.TreeViewAction;
import uk.ac.ebi.intact.application.search.struts.view.IntactViewBean;
import uk.ac.ebi.intact.application.search.struts.view.ViewForm;
import uk.ac.ebi.intact.util.*;
import uk.ac.ebi.intact.business.*;
import uk.ac.ebi.intact.model.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForward;

import java.util.*;

//for debugging only
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import org.w3c.dom.traversal.*;


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
        // The collection of beans to process.
        Map idToView = (Map) super.getSession(request).getAttribute(
                SearchConstants.FORWARD_MATCHES);

       //local list to keep track of all bean IDs to be contracted -
       //this is needed to ensure that sub-elements of them get contracted also
       //and so keep the view modes in a consistent state
       List beanIds = new ArrayList();

       //The set of objects in this session which have recently been expanded
       Map modeMap = (Map)super.getSession(request).getAttribute(SearchConstants.VIEW_MODE_MAP);
       if (modeMap == null) {
           //should have been set up by SearchAction, but wasn't so do it here
           super.getSession(request).setAttribute(SearchConstants.VIEW_MODE_MAP, new HashMap());
       }
        // Save the parameters from the view page.
        Map map = request.getParameterMap();

       //debug stuff...
       System.out.println("new view requested...");
       System.out.println("Check box keys submitted in request:");
       for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                if (!key.startsWith("tbl_")) {
                    continue;
                }
                System.out.println("key: " + key);
       }
       System.out.println();
       System.out.println("Keys that we already have a view map for:");
       for(Iterator it = modeMap.entrySet().iterator(); it.hasNext();) {
           Map.Entry entry = (Map.Entry)it.next();
           int value = ((Integer)entry.getValue()).intValue();
           String mode = null;
           if(value == XmlBuilder.CONTRACT_NODES) mode = "contracted";
           if(value == XmlBuilder.EXPAND_NODES) mode = "expanded";
           System.out.println("key:" + entry.getKey() + ", value: " + mode);
       }
       System.out.println();


        // Only search for the check boxes if the number of parameters in a
        // request exceed 1 (one parameter is always returned for buttons).
        if (map.size() > 1) {

            // Search through the parameter lists (check boxes).
            for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();

                // Ignore the non tbl parameters.
                if (!key.startsWith("tbl_")) {
                    continue;
                }

                // Extract the bean id and the AC.
                StringTokenizer stk = new StringTokenizer(key, "_");
                String ignore = stk.nextToken();
                String beanId = stk.nextToken();
                String ac = stk.nextToken();

                // The view bean associated with the id.
                IntactViewBean bean = (IntactViewBean) idToView.get(beanId);

                //tell the bean to do whatever action was requested
                List acList = new ArrayList();
                acList.add(ac);

                if(((ViewForm)form).expandContractSelected()) {
                    try {

                        int mode = XmlBuilder.CONTRACT_NODES;    //default op

                        //have we done this key before? If not, assume expand required
                        if(!modeMap.containsKey(key)) {

                            modeMap.put(key, new Integer(XmlBuilder.EXPAND_NODES));
                            mode = XmlBuilder.EXPAND_NODES;
                            System.out.println("not yet found.. adding " + key +" to map as expanded..");
                        }
                        else {
                            //change the mode (repeated code, but easier to read this way..)
                            int currentMode = 0;
                            Integer val = (Integer)modeMap.get(key);
                            if(val != null) currentMode = val.intValue();
                            if(currentMode == XmlBuilder.EXPAND_NODES) {

                                modeMap.put(key, new Integer(XmlBuilder.CONTRACT_NODES));
                                System.out.println("changing mode for key " + key + " to contracted");
                                //don't need to set mode - already defaulted to contract
                            }
                            else {
                                //put in as expand, even if somehow they key has a null value
                                modeMap.put(key, new Integer(XmlBuilder.EXPAND_NODES));
                                System.out.println("changing mode for key " + key + " to expanded");
                                mode = XmlBuilder.EXPAND_NODES;
                            }
                        }


                        if(mode == XmlBuilder.CONTRACT_NODES) {

                            System.out.println("contract node " + ac);

                            //if the root (wrapped) object is to be contracted - all
                            //the ACs below should be too, ie those in the list,
                            //so we need to keep track of it and then when we have processed
                            //the whole request list of checkboxes we can then update the sub-items
                            //with a contracted view state
                            Object wrappedObj = bean.getWrappedObject();
                            String wrappedAc = null;
                            if(wrappedObj instanceof BasicObject) wrappedAc = ((BasicObject)wrappedObj).getAc();
                            if(wrappedObj instanceof Institution) wrappedAc = ((Institution)wrappedObj).getAc();

                            String keyToMatch = "tbl_" + beanId + "_" + wrappedAc;
                            if(key.equals(keyToMatch)) {
                                //hang on to the bean ID for later processing
                                System.out.println("AC to contract is the search object - caching it...");
                                //this.updateExpandedAcs(modeMap, beanId);
                                beanIds.add(beanId);

                            }
                        }

                        //using System.out for logging as we want the bean info, which
                        //does not have access to a logger...
                        TransformerFactory tf = TransformerFactory.newInstance();
                        Transformer transformer = tf.newTransformer();
                        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //adds whitespace
                        System.out.println("expand/contract selected");
                        System.out.println("XML before transform..");
                        System.out.println();
                        Document doc1 = bean.getAsXmlDoc();
                        transformer.transform(new DOMSource(doc1), new StreamResult(System.out));
                        System.out.println();

                        Iterator it = acList.iterator();
                        System.out.println("ACs to be expanded/contracted:");
                        while(it.hasNext()) {
                            System.out.println((String)it.next());
                        }
                        //do it
                        bean.modifyXml(mode, acList);

                        if(mode == XmlBuilder.EXPAND_NODES) {
                            //See if the AC expansion was an Interaction - if so then need to
                            //expand its Components also, but if not don't do any more
                            //NB as we only have XML available we have to traverse it to find
                            //an AC/Interaction match...
                            Document doc2 = bean.getAsXmlDoc();
                            Node root = doc2.getDocumentElement();
                            NodeIterator nodeIter = ((DocumentTraversal)doc2).createNodeIterator(root, NodeFilter.SHOW_ALL,null,true);
                            Node n = null;
                            System.out.println("Checking for an Interaction...");
                            while((n = nodeIter.nextNode()) != null) {

                                Element e = (Element)n;
                                System.out.println("element name: " + e.getTagName());
                                System.out.println("element AC: " + e.getAttribute("ac"));
                                if((ac.equals(e.getAttribute("ac"))) &
                                    (e.getTagName().equals("Interaction"))) {
                                    System.out.println("found an Interaction/AC match..");
                                    doComponents(doc2, bean, mode);
                                }
                            }
                        }
                        System.out.println();
                        System.out.println("XML after transform..");
                        Document doc3 = bean.getAsXmlDoc();
                        transformer.transform(new DOMSource(doc3), new StreamResult(System.out));

                    }
                    catch(IntactException ie) {

                        //return with errors...
                        super.log("modify XML failed" + ie.toString());
                    }
                    catch(Exception e) {
                        //just to pick up the transformer exceptions
                    }
                }
                if(((ViewForm)form).expandAllSelected()) {
                    try {

                        //due to the recursive nature of the tree here we only expand down to
                        //Xrefs, and also for CvObjects only do a compact one..
                        TransformerFactory tf = TransformerFactory.newInstance();
                        Transformer transformer = tf.newTransformer();
                        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                        //first expand the node itself
                        bean.modifyXml(XmlBuilder.EXPAND_NODES, acList);
                        System.out.println("XML before full expand..");
                        System.out.println();
                        Document doc1 = bean.getAsXmlDoc();
                        transformer.transform(new DOMSource(doc1), new StreamResult(System.out));
                        System.out.println();
                        //need to start at the tree Node that was selected for expand -
                        //otherwise the whole tree will be done!
                        //NB don't expand the siblings of the first Node to check,
                        //so pass empty Collection...
                        Element start = bean.getElement(ac);
                        if(start != null) {
                            System.out.println("found element with AC " + ac + " - expanding from there..");
                            String id = "tabl_" + beanId + "_";
                            this.expandTree(bean, start, new ArrayList(), id, modeMap);
                        }
                        //no-op if the AC doesn't exist for some reason in the tree
                        System.out.println();
                        System.out.println("XML after full expand..");
                        Document doc3 = bean.getAsXmlDoc();
                        transformer.transform(new DOMSource(doc3), new StreamResult(System.out));

                    }
                    catch(IntactException ie) {

                        //return with errors...
                        super.log("modify XML failed during full expansion" + ie.toString());
                    }
                    catch(Exception e) {
                        //transformer only
                    }

                }

            }

            //now we have processed all checkbox parameters we should make sure
            //that for a contract mode involving root items, all sub-elements
            //are marked with a contracted view state also (have to do it here, since
            //we don't know what all the key states are until they have all been processed)
            if(!beanIds.isEmpty()) {
                System.out.println("marking all sub-elements of beans to contract...");
                for(Iterator iter = beanIds.iterator(); iter.hasNext();) {
                    this.updateExpandedAcs(modeMap, (String)iter.next());
                }
            }
        }
//        else {
//            // No check boxes were selected. Assume all is wanted.
//            for (Iterator iter = idToView.values().iterator(); iter.hasNext(); ) {
//                IntactViewBean bean = (IntactViewBean) iter.next();
//                bean.setTreeStatus(action);
//            }
//        }
        // Move to the results page.
        return mapping.findForward(SearchConstants.FORWARD_RESULTS);
    }


    //--------------------------- private helper methods -------------------------------

    /**
     * expands/contracts Component Elements when an Interaction has been requested
     * to be modifed. This has to be done because we are interested in the Proteins
     * that a Component holds for a given Interaction, and this would not appear
     * with a first expansion.
     *
     * @param doc The XML Document to modify
     * @param bean The bean to apply the changes to
     * @param mode the mode (expand/contract)
     *
     * @exception IntactException thrown if the modification failed
     */
    private void doComponents(Document doc, IntactViewBean bean, int mode) throws IntactException {

        List componentList = new ArrayList();
        System.out.println("Interaction expansion - checking for Component elements...");
        NodeList nodes = doc.getElementsByTagName("Component");
        if((nodes != null) & (nodes.getLength() != 0)) {

            System.out.println("found Components - expanding...");
            for(int i=0; i < nodes.getLength(); i++) {
                Node compNode = nodes.item(i);
                if(compNode.hasAttributes()) {
                    //it should do - something very odd if not!!
                    Attr acAttr = (Attr)compNode.getAttributes().getNamedItem("ac"); //know ac is an attribute
                    componentList.add(acAttr.getValue());
                    System.out.println("Component AC: " + acAttr.getValue());
                }
            }
            System.out.println("calling expand for the Components...");
            bean.modifyXml(mode, componentList);

            //don't need to mark the view modes of Components as expanded, because
            //they are not displayed in any case (only their Proteins are)

        }
    }

    /**
     * Updates the mode Map for the items related to the root object
     * when it needs contracting. In other words, sub-items need to be marked as contracted
     * because otherwise the next time the root object is expanded, the sub-items will have
     * the wrong mode associated with them.
     *
     * @param modes The Map of view modes for bean keys known so far
     * @param id The id of the Bean we are interested in
     */
    private void updateExpandedAcs (Map modes, String id) {

        //make sure all items associated with this bean are set to contracted
        System.out.println("setting items associated with bean " + id + " to contracted...");
        for (Iterator iter = modes.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                System.out.println("known key: " + key);
                if (key.indexOf("tbl_" + id +"_") != -1) {
                    entry.setValue(new Integer(XmlBuilder.CONTRACT_NODES));
                    System.out.println("marked as contracted: " + key);

                }
       }

        /*Iterator it = expandedAcs.iterator();
        System.out.println("removing expansion keys associated with bean ID = " + id +":-");
        Set toRemove = new HashSet();
        //have to copy them, *then* remove them as the Iterator
        //will get confused if some get deleted along the way
        //NB probably a List would have been better..
        while(it.hasNext()) {
                String acKey = (String)it.next();
                if(acKey.indexOf("tbl_" + id +"_") != -1) toRemove.add(acKey);
        }
        Iterator toRemoveIter = toRemove.iterator();
        System.out.println("sub-elements to be removed from expansion set:");
        while(toRemoveIter.hasNext()) {
            Object obj = toRemoveIter.next();
            System.out.println(obj);
            expandedAcs.remove(obj);
        }*/
    }

    /**
     * Method to recursively expand the Elements of a tree until it can't be done
     * anymore. NOte: this is intact-specific, so for example all expansions stop
     * at an Xref element, and also any CvObjects are only generated to a compact level
     * due to the recursive constraints they impose on the XML tree. For each new AC within
     * a bean's tree that is expanded, the AC+bean id key information is also updated to expand mode
     * to ensure cache and view consistency.
     *
     * @param bean The bean whose Document is to be expanded
     * @param currentNode The first Node of the tree identifying the tree level and hence
     * @param siblings The siblings of the current Node
     * @param id The id of the bean (used to identify ACs in the xapnded set)
     * @param modes The current Map of view modes for the known keys
     * where to start expanding from.
     *
     * @exception IntactException thrown if there was a modify problem
     */
    private void expandTree(IntactViewBean bean, Node currentNode, List siblings, String id, Map modes) throws IntactException {

        //Basic algorithm:
        //for each Node in a list of siblings:
        //get the ACs of the Node's children, then expand the bean for those ACs;
        //when the siblings are all done, move down to the next level of the DOM tree;
        //repeat the above until all Nodes at one level are either Xrefs or compact CvObjects

        System.out.println("current Node AC: " + ((Element)currentNode).getAttribute("ac"));
        System.out.println("current Node Tag: " + ((Element)currentNode).getTagName());
        Node nextSibling = null;

        //local copy of siblings
        List remainingSibs = new ArrayList();
        if(!siblings.isEmpty()) {
            Node sib = (Node)siblings.get(0);
            if(sib != null) {
                System.out.println("current Node first sibling: " + sib.getNodeType() + sib.getNodeName());
            }
            else { System.out.println("no next sibling!!"); }
            Iterator siblingIter = siblings.iterator();
            while(siblingIter.hasNext()) {
                Object obj = siblingIter.next();
                if(obj instanceof Element) {
                    Element e = (Element)obj;
                    System.out.println("sibling AC: " + e.getAttribute("ac"));
                    System.out.println("sibling Tag: " + e.getTagName());
                    remainingSibs.add(e);
                }
                else { System.out.println("sibling type wrong! Type is " + ((Node)obj).getNodeType()); }
            }
        }
        else {System.out.println("current Node has no siblings - continuing..."); }

        //base case - all Nodes at this level are Xrefs, or CvObjects
        if((((Element)currentNode).getTagName().equals("Xref")) ||
            (((Element)currentNode).getTagName().startsWith("Cv"))) {
            boolean done = true;
            if(!siblings.isEmpty()) {
                Iterator it = siblings.iterator();
                while(it.hasNext()) {
                    Node checkNode = (Node)it.next();
                    if(!((Element)checkNode).getTagName().equals("Xref") &
                        !(((Element)currentNode).getTagName().startsWith("Cv"))) {
                        //found one - better start here instead
                        System.out.println("found a non-Xref/CvObject sibling - changing target node to it..");
                        currentNode = checkNode;
                        done = false;
                        break;
                    }
                }
            }
            System.out.println("done expanding for node " + ((Element)currentNode).getAttribute("ac"));
            if(done) return;
        }
        List childAcs = new ArrayList();
        NodeList children = currentNode.getChildNodes();
        Element nextLevelChild = (Element)children.item(0);
        System.out.println("next level first Node: " + nextLevelChild.getAttribute("ac"));
        System.out.println("Children of current node " + ((Element)currentNode).getAttribute("ac") + ":");
        for(int i=0; i<children.getLength(); i++) {
            Element childElem = (Element)children.item(i);
            String childAc = childElem.getAttribute("ac");
            if(childElem.hasAttribute("ac")) {
                System.out.println("child: " + childElem.getAttribute("ac"));
            }
            else {System.out.println("child has no AC - tag name: " + childElem.getTagName()); }
            childAcs.add(childAc);

            //update it in the mode Map (even though it is not yet expanded for
            //this bean, but it will be..)
            String key = id + childAc;
            Integer val = (Integer)modes.get(key);
            if(val != null) {
                modes.remove(key);
                modes.put(key, new Integer(XmlBuilder.EXPAND_NODES));
            }

            //add the children into the sibling list for the next level down
            remainingSibs.add(childElem);
        }
        //got the current Node's child ACs, so now modify the bean data
        System.out.println("modifying bean with child ACs..");
        bean.modifyXml(XmlBuilder.EXPAND_NODES, childAcs);

        //now do the siblings and move to the next level
        if(!siblings.isEmpty()) {
            System.out.println("now doing siblings...");
            Iterator sibIterator = siblings.iterator();

            while(sibIterator.hasNext()) {
                this.expandTree(bean, (Node)sibIterator.next(), siblings, id, modes);
            }
        }
        else {System.out.println("no siblings to check"); }
        System.out.println("now moving down to next level...");
        //take out the next level "first" child as it is the first node to check at the
        //next level
        remainingSibs.remove(nextLevelChild);
        this.expandTree(bean, nextLevelChild, remainingSibs, id, modes);
    }
}
