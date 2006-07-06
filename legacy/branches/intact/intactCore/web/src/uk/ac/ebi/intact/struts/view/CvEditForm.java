/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.view;

import org.apache.struts.action.ActionForm;

/**
 * The form which captures user action (Submit, Cancel or Delete).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CvEditForm extends ActionForm {

    /**
     * The user choice.
     */
    private String myAction;

    /**
     * True if the form is submitted.
     *
     * <pre>
     * post: return = true if myAction = 'Submit'
     * </pre>
     */
    public boolean isSubmitted() {
        return myAction.equals("Submit");
    }

    /**
     * True if the form is cancelled.
     *
     * <pre>
     * post: return = true if myAction = 'Cancel'
     * </pre>
     */
    public boolean isCancelled() {
        return myAction.equals("Cancel");
    }

    /**
     * True if the delete option is selected.
     *
     * <pre>
     * post: return = true if myAction = 'Delete'
     * </pre>
     */
    public boolean isDeleted() {
        return myAction.equals("Delete");
    }

//    /**
//     * Returns the user action.
//     */
//    public String getAction() {
//        return myAction;
//    }

    /**
     * Sets the action taken by the user.
     *
     * @param action the user action.
     */
    public void setAction(String action) {
        myAction = action;
    }
}
