/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
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

import java.util.List;

/**
 * The form to edit Proteins. Overrides the validate method to provide Protein
 * specific validation. No validation is performed if 'Delete' button was pressed.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinEditForm extends EditForm {

    /**
     * Sets the proteins on hold for the form to retrieve.
     * @param exps list of ProteinBean objects.
     *
     * <pre>
     * pre:  exps->forAll(obj : Object | obj.oclIsTypeOf(ProteinBean))
     * </pre>
     */
    public void setProteins(List exps) {
        setItems(exps);
    }

    /**
     * Returns a list of proteins for a JSP to display. The var component
     * of c:forAll part must be named as 'proteins'.
     * @return a list of ProteinBean onjects.
     *
     * <pre>
     * post:  return->forAll(obj : Object | obj.oclIsTypeOf(ProteinBean))
     * </pre>
     */
    public List getProteins() {
        return getItems();
    }

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

        // No need for validation if delete button is pressed.
        if (deletePressed()) {
            return null;
        }
        // The bean associated with the current action.
        ProteinBean pb = (ProteinBean) getSelectedBean();

        // Must select from the drop down list.
        if (pb.getRole().equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.int.protein.edit.role"));
            // This is an error protein.
            pb.setEditState(ProteinBean.ERROR);
            return errors;
        }
        return null;
    }
}