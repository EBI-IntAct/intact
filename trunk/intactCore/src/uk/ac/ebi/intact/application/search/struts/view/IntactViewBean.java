/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.view;

import java.io.Serializable;
import java.util.*;

import org.w3c.dom.*;
import org.w3c.dom.traversal.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;

import uk.ac.ebi.intact.util.*;
import uk.ac.ebi.intact.business.*;

/**
 * Bean to display an Intact object. This bean is used by results.jsp to display
 * the data.
 *
 * @author Sugath Mudali, modified for XML handling by Chris Lewington
 * @version $Id$
 */
public class IntactViewBean implements Serializable {

    // Instance Data.

    /**
     * The document node of the XML tree.
     */
    private Document rootNode;

    /**
     * The object as an XML Element
     */
    private Element elem;

    /**
     * Stores the object being wrapped. Mainly used for
     * alternative display options by other views.
     */
    private Object wrappedObject;

    /**
     * The stylesheet.
     */
    private String stylesheet;

    /**
     * The Xml builder - may go into Session as a seperate object
     * later...
     */
    private XmlBuilder builder;

    /**
     * The most recent view mode for the bean data (expanded or contract)
     */
    private int currentMode;


    /**
     * Construct an instance of this class for given object. A default XML builder is also created,
     * however this means that the Bean maintains its own instance rather than reusing an
     * existing one. Further, in the case of Intact it also means that a default repository
     * configuration is used, which may not be the one you really want. To be safe you should use
     * the other constructor and pass an alreayd correctly-configured XML builder.
     * @param object the object to contruct the view.
     * @param xslt the name of the stylesheet to use for transformation.
     * @exception TransformerException for errors in creating a new transformer.
     */
    public IntactViewBean(Object object, String xslt) throws IntactException, TransformerException {

        //default builder - NB uses a default repository so may not be what is required!!
        this(object, xslt, new XmlBuilder());
    }

    /**
     * Construct an instance of this class for given object. If no XML builder is passed,
     * one will be created. However this means that the bean will maintain its own instance
     * rather than being able to reuse an existing one. Same comments apply as for the other constructor.
     * @param object the object to contruct the view.
     * @param xslt the name of the stylesheet to use for transformation.
     * @param builder The XML builder to be used
     * @exception TransformerException for errors in creating a new transformer.
     */
    public IntactViewBean(Object object, String xslt, XmlBuilder builder) throws IntactException, TransformerException {
        this.wrappedObject = object;
        this.stylesheet = xslt;

        if(builder != null) {
            this.builder = builder;
        }
        else {
            this.builder = new XmlBuilder();
        }
    }

    public Object getWrappedObject() {

        return this.wrappedObject;
    }

    /**
     * Sets the current view mode of the object wrapped by the bean. This method
     * should be used with caution as it can put the view into an inconsistent state
     * (eg when the wrapped object is currently expanded, its state can be set to
     * contracted, which is incorrect). The method is mainly here to allow the search
     * application to reset the initial state of an Experiment when it is first used to
     * display results (it gets expanded in some cases but we want it to be flagged as
     * being compact initially).
     *
     * @param mode The new view mode to be set - if an unknown mode is passed then
     * the view state of the object will be set to 'compact'
     */
    public void setViewMode(int mode) {
        if((mode == XmlBuilder.CONTRACT_NODES) || (mode == XmlBuilder.EXPAND_NODES)) {
            currentMode = mode;
        }
        else {
            currentMode = XmlBuilder.CONTRACT_NODES;
        }
    }

    /**
     * Instructs the bean to create an XML Element for the object that it wraps. Also
     * this method inserts the Element into a local Document to enable modifications
     * to it as requested.
     */
    public void createXml()
            throws ParserConfigurationException {
        this.elem = builder.buildCompactElem(this.wrappedObject);
        this.rootNode = builder.buildXml(this.wrappedObject);

    }

    /**
     * obtains the wrapped object as an XML Element
     * @return Element the Element - if currently null, it will be created
     * @exception ParserConfigurationException thrown if no Element exists and a failure
     * occurs when trying to build one
     */
    public Element getXml() throws ParserConfigurationException {

        if(this.elem == null) {

            this.createXml();
        }
        return this.elem;
    }

    /**
     * returns the object as an Element within a Document
     * @return Document The object suitably converted (created if currently null)
     * @exception ParserConfigurationException thrown if no Element exists and a failure
     * occurs when trying to build one
     */
    public Document getAsXmlDoc() throws ParserConfigurationException {

        if(this.rootNode == null) {

            this.createXml();
        }
        return this.rootNode;
    }


    /**
     * Transforms this bean using given stylesheet.
     * @param id the id for the table parameter (for XSL).
     * @param out holder for the transformation result.
     * @exception TransformerConfigurationException thrown during the parse
     *  when it is constructing the Templates object and fails.
     */
    public void transform(String id, Result out) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer =
                factory.newTransformer(new StreamSource(this.stylesheet));
        // Set the global parameters.
        transformer.setParameter("tableName", "tbl_" + id);
        if(currentMode == XmlBuilder.EXPAND_NODES) transformer.setParameter("viewMode", "expand");
        if(currentMode == XmlBuilder.CONTRACT_NODES) transformer.setParameter("viewMode", "compact");

        // Perform the transform.
        transformer.transform(new DOMSource(this.rootNode), out);
    }

    /**
     * apply the mode change to the ACs within the object, as it is inside a Document
     *
     * @param action The change required - currently XmlBuilder.COMPACT_NODES or
     * XmlBuilder.EXPAND_NODES. If an unknown mode is passed then compact is the default.
     * @param acList The list of ACs to have the action applied
     */
    public void modifyXml(int action, List acList) throws IntactException {

        System.out.println("modifyXml called on bean...");
        System.out.println("mode value used: " + action);
        currentMode = action;
//        if(currentMode == action) {
//            //do nothing - requested mode is the same as the current one
//            System.out.println("mode unchanged! returning..");
//            return;
//        }
        try {
            if((action != XmlBuilder.EXPAND_NODES) & (action != XmlBuilder.CONTRACT_NODES)) {

                System.out.println("unknown mode - default to compact..");
                //default to compact as the requested mode is unknown
                this.rootNode = builder.modifyDoc(this.rootNode, acList, XmlBuilder.CONTRACT_NODES);
                //currentMode = XmlBuilder.CONTRACT_NODES;
            }
            else {
                System.out.println("mode OK - doing modify..");
                this.rootNode = builder.modifyDoc(this.rootNode, acList, action);
                //reset the current mode
                //currentMode = action;
            }
        }
        catch(ParserConfigurationException pe) {
            throw new IntactException(pe.getMessage(), pe);
        }
    }

    /**
     * Given an AC, returns the Element of the DOM tree for it.
     *
     * @param ac The AC to check for
     *
     * @return Element the DOm Element with that AC, or null if not found
     */
    public Element getElement(String ac) {

        NodeIterator nodeIter = ((DocumentTraversal)this.rootNode).createNodeIterator(this.rootNode.getDocumentElement(),
                                                NodeFilter.SHOW_ALL, null, true);
        Node n = null;
        while((n = nodeIter.nextNode()) != null) {
            Element elem = (Element)n;
            if(elem.getAttribute("ac").equals(ac)) return elem;
        }
        return null;
    }

    /**
     * Gets all of the ACs for a given tag name in an XML document
     * @tag The element type we want the ACs for
     *
     * @return List the list of ACs for that tag type (empty if none found)
     * @exception ParserConfigurationException if there was a problem getting the XML from the bean
     */
    public List getAcs(String tag) throws ParserConfigurationException {

        List result = new ArrayList();
        //set up a NodeIterator for the bean
        Node root = this.rootNode.getDocumentElement();
        NodeIterator nodeIter = ((DocumentTraversal)this.rootNode).createNodeIterator(root, NodeFilter.SHOW_ALL,null,true);
        Node n = null;
        System.out.println("collecting " + tag + " ACs in bean...");
        while((n = nodeIter.nextNode()) != null) {
            Element e = (Element)n;
            if((e.getTagName().equals(tag))) {
                System.out.println(tag + " AC found:" + e.getAttribute("ac"));
                result.add(e.getAttribute("ac"));
            }
        }
        return result;
    }

    /**
     * This method is used to remove particular items from the XML tree managed
     * by this bean. This method should be used with caution as it may result in
     * inconsistent view states.
     *
     * @param acList The list of ACs identifying the elements to be removed. If an
     * AC is not found in the XML tree then nothing is done.
     */
    public void removeElements(List acList) {

        if(acList == null) return;
        Iterator it = acList.iterator();
        String acToCheck = null;
        while(it.hasNext()) {
            Object obj = it.next();
            if(!(obj instanceof String)) break;  //incorrect List element type
            acToCheck = (String)obj;

            NodeIterator nodeIter = ((DocumentTraversal)this.rootNode).createNodeIterator(this.rootNode.getDocumentElement(),
                                                    NodeFilter.SHOW_ALL, null, true);
            Node n = null;
            while((n = nodeIter.nextNode()) != null) {
                Element elem = (Element)n;
                if(elem.getAttribute("ac").equals(acToCheck)) {
                    //remove it from the tree
                    Node parent = elem.getParentNode();
                    parent.removeChild(elem);
                    break;      //AC is unique - don't waste time searching anymore
                }
            }
        }

    }

 }
