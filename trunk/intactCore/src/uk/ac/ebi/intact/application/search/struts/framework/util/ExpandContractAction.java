/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.framework.util;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

import java.util.Iterator;

/**
 * Implements the action when Expand/Contract button is pressed.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExpandContractAction extends TreeViewAction {

    // Implements the abstract methods.

    public void apply(Document root, String ac) {
        // Set the status in the root element.
        Element rootElement = root.getDocumentElement();
        if (rootElement.getAttribute("ac").equals(ac)) {
            toggle(rootElement);
            // Changed the status at root; no need to check for other nodes
            // (assuming that root AC is unique).
            return;
        }
        // Now go through the expandable list.
        for (Iterator iter = ELEMENT_TAGS.iterator(); iter.hasNext(); ) {
            NodeList elements = root.getElementsByTagName((String) iter.next());
            // Search for matching 'ac'.
            int length = elements.getLength();
            for (int i = 0; i < length; i++) {
                Element element = (Element) elements.item(i);
                if (element.getAttribute("ac").equals(ac)) {
                    toggle(element);
                }
            }
        }
    }

    // Helper Methods.

    /**
     * Toggles the status of given element.
     * @param element the element to change the status.
     */
    private void toggle(Element element) {
        String attValue = element.getAttribute(STATUS_ATTRIBUTE);
        String value = attValue.equals(FALSE_STR) ? TRUE_STR : FALSE_STR;
        element.setAttribute(STATUS_ATTRIBUTE, value);
    }
}
