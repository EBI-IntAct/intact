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
    public List getItems() {
//        System.out.println("IN getItems method of EditForm");
        return myItems;
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
     * Returns the selected bean.
     * @return the selected bean. This can result in a null pointer exception
     * if the form is not in a session scope. So, only use this method for a form
     * stored in a session. A null object is returned if this method is called
     * when the form has no items.
     *
     * <pre>
     * post: return != Null iff items.size() > 0
     * post: return == Null iff items-size() == 0
     * </pre>
     */
    public Object getSelectedBean() {
        if (myItems.isEmpty()) {
            return null;
        }
        return myItems.get(myIndex);
    }

    /**
     * Returns the index position of the selected bean.
     * @return an index psoition as an int.
     *
     * <pre>
     * post: return >= 0 and <= myItems.length
     * </pre>
     */
    public int getIndex() {
        return myIndex;
    }

    /**
     * True if Edit button was pressed.
     * @return true if Edit button was pressed; for all other instances false
     * is returned as long as some command is selected (ie., not null).
     */
    public boolean editPressed() {
        return myCommand.equals("Edit");
    }

    /**
     * True if Delete button was pressed.
     * @return true if Delete button was pressed; for all other instances false
     * is returned as long as some command is selected (ie., not null).
     */
    public boolean deletePressed() {
        return myCommand.equals("Delete");
    }

    /**
     * True if Save button was pressed.
     * @return true if Save button was pressed; for all other instances false
     * is returned as long as some command is selected (ie., not null).
     */
    public boolean savePressed() {
        return myCommand.equals("Save");
    }

    /**
     * True if a button with given title is pressed.
     * @param title the title of the button to compare.
     * @return <code>true</code> if <code>title</code> is pressed.
     */
    public boolean buttonPressed(String title) {
        return myCommand.equals(title);
    }
}
