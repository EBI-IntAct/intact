/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.framework.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Iterator;

/**
 * Implements the action when Expand All button is pressed.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExpandAllAction extends TreeViewAction {

    // Implements the abstract methods.

    public void apply(Document root, String ac) {
        // Expand the root.
        Element rootElement = root.getDocumentElement();
        if (rootElement.getAttribute("ac").equals(ac)) {
            expandAll(rootElement);
            // Root is expanded; all other nodes are expanded as well.
            return;
        }
        // Now go through the expandable list.
        for (Iterator iter = ELEMENT_TAGS.iterator(); iter.hasNext(); ) {
            NodeList elements = root.getElementsByTagName((String) iter.next());
            // Search for matching 'ac' and expand it.
            for (int length = elements.getLength(), i = 0; i < length; i++) {
                Element element = (Element) elements.item(i);
                if (element.getAttribute("ac").equals(ac)) {
                    expandAll(element);
                }
            }
        }
    }

    // Helper Methods.

    /**
     * Expands all the children nodes for given root node.
     * @param root the root node.
     */
    private void expandAll(Element root) {
        // Expand the root.
        root.setAttribute(STATUS_ATTRIBUTE, TRUE_STR);

        // Now go through the expandable list.
        for (Iterator iter = ELEMENT_TAGS.iterator(); iter.hasNext(); ) {
            NodeList elements = root.getElementsByTagName((String) iter.next());
            // Expand each element.
            for (int length = elements.getLength(), i = 0; i < length; i++) {
                Element element = (Element) elements.item(i);
                element.setAttribute(STATUS_ATTRIBUTE, TRUE_STR);
            }
        }
    }
}
