/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.framework.util;

import org.w3c.dom.Document;
import uk.ac.ebi.intact.application.search.struts.view.ViewForm;

import java.util.List;
import java.util.Arrays;

/**
 * The super class for selecting a button from the tree display.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public abstract class TreeViewAction {

    /**
     * A list of element names we want the status attributes for.
     */
    public static final List ELEMENT_TAGS = Arrays.asList(
            new String[]{"interaction", "interactor"});

    /**
     * The name of the status attribute.
     */
    public static final String STATUS_ATTRIBUTE = "status";

    /**
     * True string to set as an attribute value.
     */
    public static final String TRUE_STR = Boolean.TRUE.toString();

    /**
     * False string to set as an attribute value.
     */
    public static final String FALSE_STR = Boolean.FALSE.toString();

    /**
     * Creates an instance of User Action.
     * @param form contains the user action type.
     * @return an instance of user action depending on the user action type
     * in <code>form</code>.
     */
    public static TreeViewAction factory(ViewForm form) {
        if (form.expandContractSelected()) {
            return new ExpandContractAction();
        }
        else if (form.expandAllSelected()) {
            return new ExpandAllAction();
        }
        else if (form.contractAllSelected()) {
            return new ContractAllAction();
        }
        else if (form.graphSelected()) {
//            return new GraphAction();
        }
        // Invalid action; should never happen.
        return null;
    }

    /**
     * The action to take.
     * @param root the root node to apply the action.
     * @param ac the Accession Number.
     */
    public abstract void apply(Document root, String ac);
}
