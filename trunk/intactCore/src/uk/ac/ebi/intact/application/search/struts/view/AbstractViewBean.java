/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.view;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import uk.ac.ebi.intact.util.XmlBuilder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract class containing some basic operations useful to display beans for Intact.
 * Subclasses might for example be based around requirements for particular
 * Intact types (eg BasicObjects) or perhaps concrete type requiring specific functionality
 * (eg Proteins). Typically subclasses will contain XML, and may or may not require
 * methods to modify it.
 *
 * @author Chris Lewington
 * @version $Id$
 */
public abstract class AbstractViewBean implements Serializable {

    /**
     * Convenient way of referring to a bean instance
     */
    private String beanId;
    //---- Attributes needed by all subclasses - protected to allow easy access -----

    /**
     * The document node of the XML tree managed by this bean.
     */
    protected Document rootNode;

    /**
     * The object as an XML Element - useful for applications requiring different format.
     */
    protected Element elem;

    /**
     * Stores the object being wrapped. Mainly used for
     * alternative display options by other views. This should be overriden
     * by subclasses wishing to use alternative objects (eg BasicObject).
     */
    protected Object wrappedObject;

    /**
     * The stylesheet managed by this bean. It is used by subclasses
     * to format the XML for display. (expected as a URL string), and should be set
     * via a constructor. It may later be changed if required, but the bean MUST have
     * a stylesheet to work with.
     */
    protected String stylesheet;

    /**
     * The object used by subclasses to create/manipulate the XML representation
     * of the wrapped data. usually supplied in subclass constructors.
     */
    protected XmlBuilder builder;

    /**
     * Details of the search parameters, so the web pages can highlight them.
     */
    protected String searchInfo;


    /**
     * The default search link (defaults to localhost if not provided by subclass
     * constructors). Typically used by stylesheets.
     */
    protected String searchLink = "http://localhost:8080/intact/search/do/search?searchString=";

    /**
     * The default link to help pages (to localhost). Typically used in stylesheets.
     */
    protected String helpLink = "http://localhost:8080/intact/search/search.html#search.";

    //------------------- sensible constructor --------------------------

    /**
     * Constructor for use by subclasses to set a bean ID.
     * @param id The ID to set for the bean.
     */
    protected AbstractViewBean(String id) {
        this.beanId = id;
    }
    //------------ methhods useful to all subclasses --------------------------

    /**
     * There is no mutator method for the beanId - it should be set
     * by subclass constructors.
     * @return String the ID of the bean, or null if it has not been set properly
     */
    public String getBeanId() {
        return beanId;
    }
    /**
     * Typically subclasses will return a more 'interesting' instance than Object
     * @return Object the instance warpped by the bean
     */
    public Object getWrappedObject() {
        return this.wrappedObject;
    }

    /**
     * Convenience method to change the stylesheet used by this view bean.
     * @param stylesheet String identifying the stylesheet location. If null, stylesheet
     * in the bean will remain unchanged.
     */
    public void setStylesheet(String stylesheet){

        if(stylesheet != null) this.stylesheet = stylesheet;
    }

    /**
     * method to set the search details. Typically used by applications to
     * pass search results into a bean for later display.
     * @param info Comma-seperated list of search result identifiers (eg shortLabels). If
     * garbage is passed, that is what will be displayed!
     */
    public void setSearchInfo(String info){
        this.searchInfo = info;
    }

    /**
     * Retrieval of the search match details.
     * @return String The comma-seperated list of search match identifiers.
     */
    public String getSearchInfo() {
        return searchInfo;
    }

    /**
     * Instructs the bean to create an XML Element for the object that it wraps. Also
     * this method inserts the Element into a local Document to enable modifications
     * to it as requested. This method should have a public version in subclasses and
     * firstly call this one, to allow for the option of extra processing during or
     * after initial XML creation.
     * @exception ParserConfigurationException thrown if an error occurred whilst
     * building the XML.
     */
    protected void createXml()
            throws ParserConfigurationException {
        this.elem = builder.buildCompactElem(this.wrappedObject);
        this.rootNode = builder.buildXml(this.wrappedObject);

    }

    /**
     * obtains the wrapped object as an XML Element. Note that this method returns
     * a copy of the Element, not the Element itself, and it is a deep copy. Further
     * note that it has no parent (ie parentNode is <code>null</code>).
     * @return Element the Element - if currently null, it will be created
     * @exception ParserConfigurationException thrown if no Element exists and a failure
     * occurs when trying to build one
     */
    public Element getXml() throws ParserConfigurationException {

        if (this.elem == null) {

            this.createXml();
        }
        //return a copy - don't want this tree messed up!
        return (Element)this.elem.cloneNode(true);
    }

    /**
     * returns the object as an Element within a Document
     * @return Document The object suitably converted (created if currently null)
     * @exception ParserConfigurationException thrown if no Element exists and a failure
     * occurs when trying to build one
     */
    public Document getAsXmlDoc() throws ParserConfigurationException {

        if (this.rootNode == null) {

            this.createXml();
        }
        return this.rootNode;
    }


    /**
     * Sets up the XSL transformer machinery. This method provides
     * the basic transformation functionality - subclasses should use this method to set
     * up transformation processing before actually doing a transformation.
     * @param id the id for the table parameter (for XSL).
     * @return TransFormer The Transformer to use.
     * @exception TransformerConfigurationException thrown during the parse
     *  when it is constructing the Templates object and fails.
     */
    protected Transformer setUpTransform(String id) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer =
                factory.newTransformer(new StreamSource(this.stylesheet));
        // Set the global parameters.
        transformer.setParameter("tableName", "tbl_" + id);
        transformer.setParameter("helpLink", helpLink);
        transformer.setParameter("searchLink", searchLink);
        transformer.setParameter("searchParams", searchInfo);

        return transformer;
    }

    /**
     * Transforms a bean using a given stylesheet. The stylesheet should
     * be passed into a subclass constructor.
     * @param id the id for the table parameter (for XSL).
     * @param out holder for the transformation result.
     * @exception TransformerConfigurationException thrown during the parse
     *  when it is constructing the Templates object and fails.
     */
    public abstract void transform(String id, Result out) throws TransformerException;


    /**
     * Given an AC, returns the Element of the DOM tree for it. This method
     * assumes that XML elements contain AC attributes, of course - if there are none
     * then nothing is returned.
     *
     * @param ac The AC to check for
     *
     * @return Element the DOM Element with that AC, or null if not found
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
     * Gets all of the ACs for a given tag name in an XML document. This assumes that Elements
     * have an attribute called 'ac' - if not then no results can be generated.
     * @param tag The element type we want the ACs for
     *
     * @return Collection the list of ACs for that tag type (empty if none found)
     * @exception ParserConfigurationException if there was a problem getting the XML from the bean
     */
    public Collection getAcs(String tag) throws ParserConfigurationException {

        Collection result = new ArrayList();
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

}
