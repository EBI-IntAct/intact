/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.view;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.util.XmlBuilder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Bean to manage the XML and display of Interaction partners of a given
 * Protein. The Protein may occur in more than one Experiment and therefore may
 * be present in more than one <code>BasicObjectViewBean</code> - the view for
 * this information requires the redundancy to be handled (ie all Interaction
 * partners should be in a single table), and therefore the XML managed by this bean
 * is 'non-standard'.
 *
 * @see AbstractViewBean
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class ProteinPartnersViewBean extends AbstractViewBean {

    //------------- declared attributes -------------------

    /**
     * The most recent view mode for the bean data (expanded or contract)
     */
    private int currentMode;

    /**
     * The default experiment view link (defaults to localhost if not provided by subclass
     * constructors). Used by stylesheets.
     */
    private String experimentLink = "http://localhost:8080/intact/search/do/protein?viewExpLink=";



    /**
     * Construct an instance of this class for given object. If no XML builder is passed,
     * one will be created. However this means that the bean will maintain its own instance
     * rather than being able to reuse an existing one. Same comments apply as for the other constructor.
     * @param protein the Protein instance for which a 'partners' view is required.
     * @param xslt the name of the stylesheet to use for transformation - must be non-null.
     * Note: the filename is NOT checked for validity, only for being non-null.
     * @param builder The XML builder to be used (created if null)
     * @param searchLink the HTML search link used by the stylesheet; null will default to local
     * host search link.
     * @param helpLink the HTML help link used by the stylesheet; null will default to local
     * host.
     * @param experimentLink The link to pass to a stylesheet so requests to view
     * Experiment details can be sent back to the correct place. Null defaults to
     * localhost.
     * @param id The bean ID
     * @exception IntactException thrown if an XmlBuilder has to be created, but fails.
     * @exception NullPointerException thrown if the object or stylesheet filename is null.
     */
    public ProteinPartnersViewBean(Protein protein, String xslt, XmlBuilder builder,
                          String searchLink, String helpLink,
                          String experimentLink, String id) throws IntactException {

        super(id);     //set bean ID first
        if(protein == null) throw new NullPointerException("cannot create view bean with null object!");
        this.wrappedObject = protein;
        if(xslt == null) throw new NullPointerException("must have a valid stylesheet filename!");
        this.stylesheet = xslt;

        if (builder != null) {
            this.builder = builder;
        }
        else {
            this.builder = new XmlBuilder();
        }
        if (searchLink != null) this.searchLink = searchLink;
        if (helpLink != null) this.helpLink = helpLink;
        if (experimentLink != null) this.experimentLink = experimentLink;

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
     * @deprecated No alternatives - unsafe method with current design
     */
    public void setViewMode(int mode) {
        if ((mode == XmlBuilder.CONTRACT_NODES) || (mode == XmlBuilder.EXPAND_NODES)) {
            currentMode = mode;
        }
        else {
            currentMode = XmlBuilder.CONTRACT_NODES;
        }
    }

    /**
     * Sets the status attribute to <code>mode</code> for all the elements
     * identified in <code>acsToSet</code> list. The status is unchanged
     * if an AC is not found in the tree or <code>mode</code> is unknown.
     * @param acsToSet contains list of ACs to set the status for; should contain
     * strings.
     * @param mode the mode to set; possible values are:
     * {@link XmlBuilder#CONTRACT_NODES} or
     * {@link XmlBuilder#EXPAND_NODES}.
     * @exception IllegalArgumentException thrown if <code>acsToSet</code> Collection
     * is null.
     *
     * <pre>
     * pre: acsToSet != null
     * pre: return->forall(obj: Object | obj.oclIsTypeOf(String))
     * </pre>
     */
    public void resetStatus(Collection acsToSet, int mode) {

        if(acsToSet == null) throw new IllegalArgumentException("Collection parameter must be non-null");
        boolean modeOK = (mode == XmlBuilder.CONTRACT_NODES ||
                (mode == XmlBuilder.EXPAND_NODES));
        if (!modeOK) {
            // Encountered an unknown mode.
            return;
        }
        // The status to set.
        String newStatus = (mode == XmlBuilder.CONTRACT_NODES) ? "compact" : "expand";
        // The iterator to traverse the XML tree.
        NodeIterator nodeIter = ((DocumentTraversal) rootNode).createNodeIterator(rootNode.getDocumentElement(),
                NodeFilter.SHOW_ALL, null, true);
        Node n = null;
        while ((n = nodeIter.nextNode()) != null) {
            Element elem = (Element) n;
            String ac = elem.getAttribute("ac");
            if (acsToSet.contains(ac)) {
                elem.setAttribute("status", newStatus);

            }
        }
    }

    /**
     * @see AbstractViewBean
     */
    public void createXml()
            throws ParserConfigurationException {
        //do the basics first
        super.createXml();
        //add in an element to be the parent of the interaction partners
        Element partners = this.rootNode.createElement("partners");
        addElement(partners, "Protein");

    }


    /**
     * Transforms this bean using given stylesheet.
     * @param id the id for the table parameter (for XSL).
     * @param out holder for the transformation result.
     * @exception TransformerConfigurationException thrown during the parse
     *  when it is constructing the Templates object and fails.
     */
    public void transform(String id, Result out) throws TransformerException {

        //do the basics first
        Transformer transformer = setUpTransform(id);

        //now the specifics...
        if (currentMode == XmlBuilder.EXPAND_NODES) transformer.setParameter("viewMode", "expand");
        if (currentMode == XmlBuilder.CONTRACT_NODES) transformer.setParameter("viewMode", "compact");
        transformer.setParameter("experimentLink", experimentLink);

        // Perform the transform.
        transformer.transform(new DOMSource(this.rootNode), out);
    }

    /**
     * Utility method to enable a single XML node to be modified, referenced
     * by its AC.
     * @param action The action to be performed on the node (currently expand
     * or contract)
     * @param ac The AC of the node to be modified. Note that all nodes with
     * this AC will have the action applied to them. If the AC is not found then
     * the nodes are left unchanged.
     * @exception IntactException thrown if XML problems are encountered whilst
     * searching the tree.
     * @exception IllegalArgumentException thrown if the modification state is unknown
     */
    public void modifyXml(int action, String ac) throws IntactException {

        if((action != XmlBuilder.EXPAND_NODES) & (action != XmlBuilder.CONTRACT_NODES)) {
            throw new IllegalArgumentException("invalid modification state: " + action);
        }

        Collection acList = new ArrayList();
        acList.add(ac);
        modifyXml(action, acList);

    }

    /**
     * apply the mode change to the ACs within the object, as it is inside a Document
     *
     * @param action The change required - currently XmlBuilder.COMPACT_NODES or
     * XmlBuilder.EXPAND_NODES. If an unknown mode is passed then compact is the default.
     * @param acList The Collection of ACs to have the action applied
     * * @exception IntactException thrown if XML problems are encountered whilst
     * searching the tree.
     */
    public void modifyXml(int action, Collection acList) throws IntactException {

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
     * This method is used to remove particular items from the XML tree managed
     * by this bean. This method should be used with caution as it may result in
     * inconsistent view states.
     *
     * @param acList The Collection of ACs identifying the elements to be removed. If an
     * AC is not found in the XML tree then nothing is done.
     */
    public void removeElements(Collection acList) {

        if(acList == null) return;
        Iterator it = acList.iterator();
        String acToCheck = null;
        while(it.hasNext()) {
            Object obj = it.next();
            if(!(obj instanceof String)) break;  //incorrect element type
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

    /**
     * Method to insert an Element into the XML tree for this bean. The Element
     * will be placed into the tree as a direct child of the node specified by the
     * tagName parameter. Typical usage would be to add Elements to a node in the
     * tree which represents the parent of a number of Elements (ie like a Collection).
     * @param elem The tree item to insert
     * @param tagName Used to identify where to put the new Element. All matches
     * of this attribute will have the new child added to it.
     */
    public void addElement(Element elem, String tagName) {

        //first import the Element....
        Node newNode = rootNode.importNode(elem, true);

        //now go through the XML until we find the tag(s) we want...
        NodeIterator nodeIter = ((DocumentTraversal)this.rootNode).createNodeIterator(this.rootNode.getDocumentElement(),
                                                NodeFilter.SHOW_ALL, null, true);
        Node n = null;
        while((n = nodeIter.nextNode()) != null) {
            Element element = (Element)n;
            if(element.getTagName().equals(tagName)) element.appendChild(newNode);
        }

    }

    /**
     * Convenience method to check if an Element already exists. The match
     * is done on AC, as this is unique.
     * @param ac The AC to check
     * @return boolean true if the Element with this ac is already in the tree,
     * false otherwise
     */
    public boolean containsAc(String ac) {

        boolean found = false;
        NodeIterator nodeIter = ((DocumentTraversal)this.rootNode).createNodeIterator(this.rootNode.getDocumentElement(),
                                                NodeFilter.SHOW_ALL, null, true);
        Node n = null;
        while((n = nodeIter.nextNode()) != null) {
            Element element = (Element)n;
            if(element.getAttribute("ac").equals(ac)) {
                    found = true;
                    break;
            }
        }
        return found;
    }

    /**
     * Allows the insertion of an attribute into a particular tree node, as
     * identified by the 'ac' parameter which should be unique. If not then every
     * Element with that attribute value will get a new attribute added to it. A
     * <code>DOMException</code> could be thrown internally
     * to this method but nothing is done with it - it would only happen if the attribute
     * value contains any illegal characters (eg control characters).
     * @param attName name of attribute to be added
     * @param attValue  value of the attribute
     * @param ac Means of identifying a particular Element - should be unique for a single
     * attribute addition.
     */
    public void addAttribute(String attName, String attValue, String ac) {
         //go through the XML until we find the Element we want...
        NodeIterator nodeIter = ((DocumentTraversal)this.rootNode).createNodeIterator(this.rootNode.getDocumentElement(),
                                                NodeFilter.SHOW_ALL, null, true);
        Node n = null;
        while((n = nodeIter.nextNode()) != null) {
            Element element = (Element)n;
            if(element.getAttribute("ac").equals(ac)) {
                try {
                    element.setAttribute(attName, attValue);
                }
                catch(DOMException de) {
                    //do nothing - only thrown if an illegal char is passed
                }
            }
        }
    }

    /**
         * Allows the update of an attribute into a particular tree node, as
         * identified by the 'ac' parameter which should be unique. If not then every
         * Element with that attribute value will get the attribute updated. A
         * <code>DOMException</code> could be thrown internally
         * to this method but nothing is done with it - it would only happen if the attribute
         * value contains any illegal characters (eg control characters).
         * @param attName name of attribute to be updated. If not found, a new
         * one is added with the 'update' value as its data
         * @param attValue  value of the attribute. If the value already exists
         * in the attribute theen it is NOT added again
         * @param ac Means of identifying a particular Element - should be unique for a single
         * attribute addition.
         */
        public void updateAttribute(String attName, String attValue, String ac) {
             //go through the XML until we find the Element we want...
            NodeIterator nodeIter = ((DocumentTraversal)this.rootNode).createNodeIterator(this.rootNode.getDocumentElement(),
                                                    NodeFilter.SHOW_ALL, null, true);
            Node n = null;
            while((n = nodeIter.nextNode()) != null) {
                Element element = (Element)n;
                if(element.getAttribute("ac").equals(ac)) {
                    try {
                        String currentAtt = element.getAttribute(attName);
                        if(currentAtt == null) {
                            currentAtt = attValue;
                        }
                        else {
                            //only add the value if it isn't already in the String
                            if(currentAtt.indexOf(attValue) == -1)
                                currentAtt = currentAtt + "," + attValue;
                        }
                        element.setAttribute(attName, currentAtt);
                    }
                    catch(DOMException de) {
                        //do nothing - only thrown if an illegal char is passed
                    }
                }
            }
        }


}
