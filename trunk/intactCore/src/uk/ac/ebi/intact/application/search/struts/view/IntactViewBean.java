/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.view;

import java.io.Serializable;
import java.util.Iterator;

import org.w3c.dom.*;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;

import uk.ac.ebi.intact.application.search.struts.framework.util.TreeViewAction;

/**
 * Bean to display an Intact object. This bean is used by results.jsp to display
 * the data.
 *
 * @author Sugath Mudali, modified for basic search by Chris Lewington
 * @version $Id$
 */
public class IntactViewBean implements Serializable {

    // Instance Data.

    /**
     * The document node of the XML tree.
     */
    private Document rootNode;

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
     * Construct an instance of this class for given object.
     * @param object the object to contruct the view.
     * @param xslt the name of the stylesheet to use for transformation.
     * @exception TransformerException for errors in creating a new transformer.
     */
    public IntactViewBean(Object object, String xslt) throws TransformerException {
        this.wrappedObject = object;
        this.stylesheet = xslt;
    }

    /**
     * Marshals the wrapped object to an XML node using Castor.
     * @param mapping the mapping file for castor to do mapping.
     * @param db the document builder to create an XML node.
     * @exception MappingException for errrors with setting the map file.
     * @exception MarshalException thrown for marshalling errors.
     * @exception ValidationException thrown for XML validation errors.
     */
    public void marshall(Mapping mapping, DocumentBuilder db)
            throws MappingException, MarshalException, ValidationException {
        // Create the root node for the XML tree.
        this.rootNode = db.newDocument();
        // The output is written to the root node.
        Marshaller marshaller = new Marshaller(this.rootNode);
        marshaller.setMapping(mapping);
        // Marshall using the root node.
        marshaller.marshal(this.wrappedObject);
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
        // Perform the transform.
        transformer.transform(new DOMSource(this.rootNode), out);
    }

    /**
     * This method adds the status attribute to the root node and to all the
     * elements defined in
     * {@link uk.ac.ebi.intact.application.search.struts.framework.util.TreeViewAction#ELEMENT_TAGS}.
     * The attribute is set to false.
     */
    public void addStatusNodes() {
        // Set the status in the root element.
        this.rootNode.getDocumentElement().setAttribute(
                TreeViewAction.STATUS_ATTRIBUTE, TreeViewAction.FALSE_STR);
        // Now go through the expandable list.
        for (Iterator iter = TreeViewAction.ELEMENT_TAGS.iterator(); iter.hasNext(); ) {
            NodeList elements = this.rootNode.getElementsByTagName((String) iter.next());
            // Process each element.
            for (int length = elements.getLength(), i = 0; i < length; i++) {
                Element element = (Element) elements.item(i);
                element.setAttribute(TreeViewAction.STATUS_ATTRIBUTE,
                        TreeViewAction.FALSE_STR);
            }
        }
    }

    /**
     * Apply the action for the root and all its subnodes.
     * @param action the action to apply.
     */
//    public void setTreeStatus(TreeViewAction action) {
//        Element rootElement = this.rootNode.getDocumentElement();
//        String ac = rootElement.getAttribute("ac");
//        this.setTreeStatus(action, ac);
//    }

    /**
     * Apply the action for given ac.
     * @param action the action to apply.
     */
    public void setTreeStatus(TreeViewAction action, String ac) {
        action.apply(this.rootNode, ac);
    }
 }
