/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.view;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.BasicObject;
import uk.ac.ebi.intact.util.XmlBuilder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import java.util.*;

/**
 * Bean to display an Intact BasicObject. This bean is used by results.jsp to display
 * the data, and also by applications which may wish to manipulate the XML tree that
 * is managed by isnatcnes of this bean class. For example, as well as the basic XML creation
 * and view methods of the parent <code>AbstractViewBean</code> class there are methods
 * to expand, contract and remove elements from the XML tree.
 *
 * @see uk.ac.ebi.intact.application.search.struts.view.AbstractViewBean
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class BasicObjectViewBean extends AbstractViewBean {

    //------------- declared attributes -------------------

    /**
     * The most recent view mode for the bean data (expanded or contract)
     */
    private int currentMode;

    /**
     * The set of ACs of XML tree objects which in the current state of the bean
     * are in an expanded mode. Useful for external applications.
     */
    private Set currentlyExpanded;

    /**
     * The set of ACs of XML tree objects which in the current state of the bean
     * are in a contracted mode. Useful for external applications.
     */
    private Set currentlyContracted;


    /**
     * Construct an instance of this class for given object. If no XML builder is passed,
     * one will be created. However this means that the bean will maintain its own instance
     * rather than being able to reuse an existing one. Same comments apply as for the other constructor.
     * @param object the object to construct the view for.
     * @param xslt the name of the stylesheet to use for transformation.
     * @param builder The XML builder to be used (created if none passed)
     * @param searchLink the HTML search link used by the stylesheet; null will default to local
     * host search link.
     * @param helpLink the HTML help link used by the stylesheet; null will default to local
     * host.
     * @param id The ID to use for identifying the bean later
     * @exception IntactException thrown if an XmlBuilder has to be created, but fails.
     * @exception NullPointerException thrown if the object or stylesheet filename is null.
     */
    public BasicObjectViewBean(BasicObject object, String xslt, XmlBuilder builder,
                          String searchLink, String helpLink, String id)
            throws IntactException {

        super(id); //set the bean's ID
        if(object == null) throw new NullPointerException("cannot create view bean with null object!");
        this.wrappedObject = object;

        if(xslt == null) throw new NullPointerException("must have a valid stylesheet filename!");
        this.stylesheet = xslt;

        if((searchLink == null) || (helpLink == null)) {
            throw new NullPointerException("need search and help links specified for ViewBean!");
        }
        if (builder != null) {
            this.builder = builder;
        }
        else {
            this.builder = new XmlBuilder();
        }
        this.searchLink = searchLink;
        this.helpLink = helpLink;

        //make the AC lists synchronized to avoid possible threading problems in web apps
        currentlyExpanded = Collections.synchronizedSet(new HashSet());
        currentlyContracted = Collections.synchronizedSet(new HashSet());
    }

    public Set getExpanded() {
        return currentlyExpanded;
    }

    public Set getContracted() {
          return currentlyContracted;
    }

    /**
     * Tests current state of the bean to determine whether the item with
     * the specified AC is in an expanded mode or not.
     * @param acToCheck The AC to test
     * @return true if the item is currently expanded, false otherwise.
     */
    public boolean isExpanded(String acToCheck) {
        return currentlyExpanded.contains(acToCheck);
    }

    /**
     * Convenience method to check status of more than one AC.
     * @param acList The Collection of ACs to check
     * @return true if the item is currently contracted, false otherwise.
     */
    public boolean areCompact(Collection acList) {
        return currentlyContracted.containsAll(acList);
    }

    /**
     * Convenience method to check status of more than one AC.
     * @param acList The Collection of ACs to check
     * @return true if the item is currently expanded, false otherwise.
     */
    public boolean areExpanded(Collection acList) {
        return currentlyExpanded.containsAll(acList);
    }

    /**
     * Tests current state of the bean to determine whether the item with
     * the specified AC is in a compact mode or not.
     * @param acToCheck The AC to test
     * @return true if the item is currently contracted, false otherwise.
     */
    public boolean isCompact(String acToCheck) {
        return currentlyContracted.contains(acToCheck);
    }

    /**
     * For a given Collection of ACs, keeps only those which are not
     * expanded in the current bean state.
     * @param acList Collection of ACs to be modified
     * @return Collection the original Collection with only ACs marked
     * as 'contracted' remaining.
     * @exception NullPointerException thrown if the parameter is null
     */
    public Collection retainCompact(Collection acList) {

        if(acList == null) throw new NullPointerException("Need a Collection of ACs to check!!");
        acList.removeAll(currentlyExpanded);
        return acList;

    }

    /**
     * For a given Collection of ACs, keeps only those which are not
     * contracted in the current bean state.
     * @param acList Collection of ACs to be modified
     * @return Collection the original Collection with only ACs marked
     * as 'expanded' remaining.
     * @exception NullPointerException thrown if the parameter is null
     */
    public Collection retainExpanded(Collection acList) {

        if(acList == null) throw new NullPointerException("Need a Collection of ACs to check!!");
        acList.removeAll(currentlyContracted);
        return acList;

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

                //update current bean state info on expansion/contraction
                if(newStatus.equals("expand")) {
                currentlyExpanded.add(ac);
                currentlyContracted.remove(ac);
                }
                else {
                currentlyContracted.add(ac);
                currentlyExpanded.remove(ac);
                }
            }
        }
    }

    /**
     * @see uk.ac.ebi.intact.application.search.struts.view.AbstractViewBean
     */
    public void createXml()
            throws ParserConfigurationException {
        //do the basics first
        super.createXml();
        //wrapped item is a BasicObject
        currentlyContracted.add(((BasicObject)this.wrappedObject).getAc());

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

        //update state info on ACs
        if(action == XmlBuilder.EXPAND_NODES) {
                currentlyExpanded.add(ac);
                currentlyContracted.remove(ac);
        }
        else {
            currentlyContracted.add(ac);
            currentlyExpanded.remove(ac);
        }
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
            //update state info on ACs
            if(action == XmlBuilder.EXPAND_NODES) {
                    currentlyExpanded.addAll(acList);
                    currentlyContracted.removeAll(acList);
            }
            else {
                currentlyContracted.addAll(acList);
                currentlyExpanded.removeAll(acList);
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
        //make sure the removed ones are no longer part of the state info
        currentlyExpanded.removeAll(acList);
        currentlyContracted.removeAll(acList);

    }

}
