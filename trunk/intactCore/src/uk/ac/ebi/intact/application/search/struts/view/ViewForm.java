/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.view;

import org.apache.struts.action.ActionForm;

/**
 * This form captues the user action of the view.jsp.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ViewForm extends ActionForm {

    // Class Data

    /**
     * Identifier for when Expand/Contract button is pressed.
     */
    private static final int EXPAND_CONTRACT = 0;

    /**
     * Identifier for when Expand All button is pressed.
     */
    private static final int EXPAND_ALL = 1;

    /**
     * Identifier for when Contract All button is pressed.
     */
    private static final int CONTRACT_ALL = 2;

    /**
     * Identifier for when Graph button is pressed.
     */
    public static final int GRAPH = 3;

    // Instance Data

    /**
     * Saves the user action.
     */
    private int myAction;

    /**
     * Sets the action.
     * @param action the action for the form. If this contains the word
     * 'AC' then the search is by AC otherwise the search is by label.
     */
    public void setAction(String action) {
        if (action.equals("View Detail")) {
            myAction = EXPAND_CONTRACT;
        }
        else if (action.equals("Full Expand")) {
            myAction = EXPAND_ALL;
        }
        else if (action.equals("Contract All")) {
            myAction = CONTRACT_ALL;
        }
        else {
            // Assume graph button is pressed.
            myAction = GRAPH;
        }
    }

    /**
     * True if Expand/Contract button is pressed.
     */
    public boolean expandContractSelected() {
        return myAction == EXPAND_CONTRACT;
    }

    /**
     * True if Expand All button is pressed.
     */
    public boolean expandAllSelected() {
        return myAction == EXPAND_ALL;
    }

    /**
     * True if Contract All button is pressed.
     */
    public boolean contractAllSelected() {
        return myAction == CONTRACT_ALL;
    }


    /**
     * True if Graph button is pressed.
     */
    public boolean graphSelected() {
        return myAction == GRAPH;
    }
}