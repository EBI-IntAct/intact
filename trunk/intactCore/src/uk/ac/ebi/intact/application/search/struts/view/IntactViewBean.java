/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.view;

import java.io.Serializable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

/**
 * Bean to display an Intact object. This bean is used by results.jsp to display
 * the data.
 *
 * @author Sugath Mudali, modified for basic search by Chris Lewington
 * @version $Id$
 */
public class IntactViewBean implements Serializable {

    // Class Data.

    /**
     * A list of element names we want the status attributes for.
     */
    private static List expElementTags = Arrays.asList(
            new String[]{"experiment", "interaction", "interactor"});

    /**
     * The name of the status attribute.
     */
    private static String STATUS_ATTRIBUTE = "status";

    /**
     * True string.
     */
    private static String TRUE_STR = Boolean.TRUE.toString();

    /**
     * False string.
     */
    private static String FALSE_STR = Boolean.FALSE.toString();

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
     * The name of the stylesheet to use to transform this object.
     */
    private String stylesheet;

    /**
     * Construct an instance of this class for given object.
     * @param object the object to contruct the view.
     */
    public IntactViewBean(Object object) {
        this.wrappedObject = object;
    }

    /**
     * Sets the stylesheet.
     * @param xslt the name of the stylesheet to use for transformation.
     */
    public void setStylesheet(String xslt) {
        this.stylesheet = xslt;
    }

    /**
     * Marshsasls the wrapped object to an XML node.
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
     * @param factory the factory to create a transformer.
     * @param id the id for the table parameter (for XSL).
     * @param out holder for the transformation result.
     * @exception TransformerConfigurationException thrown during the parse
     *  when it is constructing the Templates object and fails.
     */
    public void transform(TransformerFactory factory, int id, Result out)
            throws TransformerException {
        Transformer transformer =
                factory.newTransformer(new StreamSource(this.stylesheet));
        // Set the global parameters.
        transformer.setParameter("tableName", "tbl_" + id);
        // Perform the transform.
        transformer.transform(new DOMSource(this.rootNode), out);
    }

    /**
     * This method adds the status attribute to all the elements with AC as an
     * attribute except for Xref elements.
     */
    public void addStatusNodes() {
        // Set the status in the root element.
        this.rootNode.getDocumentElement().setAttribute(STATUS_ATTRIBUTE, FALSE_STR);
        // Now go through the expandable list.
        for (Iterator iter = expElementTags.iterator(); iter.hasNext(); ) {
            NodeList elements = this.rootNode.getElementsByTagName((String) iter.next());
            // Process each element.
            //int length = elements.getLength();
            for (int length = elements.getLength(), i = 0; i < length; i++) {
                Element element = (Element) elements.item(i);
                element.setAttribute(STATUS_ATTRIBUTE, FALSE_STR);
            }
        }
    }

    /**
     * Changes the status for given AC.
     * @param ac the AC to search for the element.
     */
    public void changeElementStatus(String ac) {
        // Set the status in the root element.
        Element rootElement = this.rootNode.getDocumentElement();
        if (rootElement.getAttribute("ac").equals(ac)) {
            toggle(rootElement);
            // Changed the status at root; no need to check for other nodes
            // (assuming that root AC is unique).
            return;
        }
        // Now go through the expandable list.
        for (Iterator iter = expElementTags.iterator(); iter.hasNext(); ) {
            NodeList elements = this.rootNode.getElementsByTagName((String) iter.next());
            // Process each element.
            int length = elements.getLength();
            for (int i = 0; i < length; i++) {
                Element element = (Element) elements.item(i);
                if (element.getAttribute("ac").equals(ac)) {
                    toggle(element);
                }
            }
        }
    }

    // Helper methods.

    /**
     * Changes the status of given element.
     * @param element the element to change the status.
     */
    private void toggle(Element element) {
        String attValue = element.getAttribute(STATUS_ATTRIBUTE);
        String value = attValue.equals(FALSE_STR) ? TRUE_STR : FALSE_STR;
        element.setAttribute(STATUS_ATTRIBUTE, value);
    }

    // All the following methods for debugging purposes only.
    /**
     * Helper method to dump the XML tree.
     * @param filename the name of the file to dump the XML tree.
     */
//    public void dumpXML(String filename) {
//        StreamResult sr = new StreamResult(new File(filename));
//        DOMSource dom = new DOMSource(this.rootNode);
//        TransformerFactory tFactory = TransformerFactory.newInstance();
//        Transformer transformer = null;
//        try {
//            transformer = tFactory.newTransformer();
//            transformer.transform(dom, sr);
//        }
//        catch (TransformerConfigurationException e) {
//            e.printStackTrace();
//        }
//        catch (TransformerException e) {
//            e.printStackTrace();
//        }
//    }

//    public Document root() {
//        return this.rootNode;
//    }
//
//    public List getList() {
//        return IntactViewBean.expElementTags;
//    }
}
