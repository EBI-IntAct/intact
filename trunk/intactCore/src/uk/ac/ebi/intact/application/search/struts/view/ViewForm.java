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
     * Identifier for when Expand Protein button is pressed.
     */
    private static final int EXPAND = 0;

    /**
     * Identifier for when Contract Protein button is pressed.
     */
    private static final int CONTRACT = 1;

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
     * AC - stored when a link is clicked to have data expanded
     */
    private String ac;

    public void setAc(String ac) {
        this.ac = ac;
    }
    public String getAc() {
        return this.ac;
    }

    /**
     * Sets the action.
     * @param action the action for the form. If this contains the word
     * 'AC' then the search is by AC otherwise the search is by label.
     */
    public void setAction(String action) {
        if (action.equals("Detail View")) {
            myAction = EXPAND;
        }
        else if (action.equals("Compact View")) {
            myAction = CONTRACT;
        }
        else {
            // Assume graph button is pressed.
            myAction = GRAPH;
        }
    }

    /**
     * True if Expand button is pressed.
     */
    public boolean expandSelected() {
        return myAction == EXPAND;
    }

    /**
     * True if Contract button is pressed.
     */
    public boolean contractSelected() {
        return myAction == CONTRACT;
    }

    /**
     * True if Graph button is pressed.
     */
    public boolean graphSelected() {
        return myAction == GRAPH;
    }
}