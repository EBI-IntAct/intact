/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ebi.intact.application.editor.struts.view.EditForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;

/**
 * The form to edit Proteins. Overrides the validate method to provide Protein
 * specific validation.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinEditForm extends EditForm {

    /**
     * Validate the properties that have been set from the HTTP request.
     *
     * @param mapping the mapping used to select this instance
     * @param request the servlet request we are processing
     * @return <tt>ActionErrors</tt> object that contains validation errors. If
     * no errors are found, <tt>null</tt> or an empty <tt>ActionErrors</tt>
     * object is returned.
     */
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        // No need for validation if delete button is pressed.
        if (deletePressed()) {
            return errors;
        }
        // The bean associated with the current action.
        int index = getIndex();
        ProteinBean pb = (ProteinBean) getItems()[index];

        // Must select from the drop down list.
        if (pb.getRole().equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors.add("protein.role", new ActionError("error.dropdown.list"));
        }
        return errors;
    }
}