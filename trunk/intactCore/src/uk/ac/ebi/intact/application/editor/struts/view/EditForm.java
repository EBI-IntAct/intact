/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view;

import org.apache.struts.action.ActionForm;

import java.util.List;

/**
 * Generic form to edit a collection (annotation or xref).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditForm extends ActionForm {

    /**
     * Holds a list of items.
     */
    private List myItems;

    /**
     * The index of the current annotaion.
     */
    private int myIndex;

    /**
     * The name of the current command.
     */
    private String myCommand;

    /**
     * Sets items.
     * @param items a list of <code>Object</code>s.
     */
    public void setItems(List items) {
        this.myItems = items;
    }

    /**
     * Returns an array of items.
     * @return an array of <code>Object</code>s.
     */
    public Object[] getItems() {
        return myItems.toArray();
    }

    /**
     * Sets the name of the current command and the index of the annotation.
     * @param index the position of the current annotation.
     * @param value the command value.
     */
    public void setCmd(int index, String value) {
        myIndex = index;
        myCommand = value;
    }

    /**
     * Returns the index position of the current annotation.
     * @return the index position as an <code>int</code>.
     */
    public int getIndex() {
        return myIndex;
    }

    /**
     * True if Edit button was pressed.
     * @return true if Edit button was pressed; for all other instances false
     * is returned.
     */
    public boolean editPressed() {
        return myCommand.equals("Edit");
    }

    /**
     * True if Delete button was pressed.
     * @return true if Delete button was pressed; for all other instances false
     * is returned.
     */
    public boolean deletePressed() {
        return myCommand.equals("Delete");
    }

    /**
     * True if Save button was pressed.
     * @return true if Save button was pressed; for all other instances false
     * is returned.
     */
    public boolean savePressed() {
        return myCommand.equals("Save");
    }
}
