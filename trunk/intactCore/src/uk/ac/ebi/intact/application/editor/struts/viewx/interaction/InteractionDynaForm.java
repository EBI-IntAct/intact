/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import org.apache.struts.action.*;
import org.apache.struts.validator.DynaValidatorForm;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;

/**
 * The form to edit interaction data.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionDynaForm extends DynaValidatorForm {

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
        String organism = ((String) get("organism")).trim();
        String interaction = ((String) get("interactionType")).trim();
        String experiment = ((String) get("experiment")).trim();

        // Must select from the drop down list.
        if (organism.equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors.add("int.organism", new ActionError("error.dropdown.list"));
        }
        else if (interaction.equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors.add("int.interaction", new ActionError("error.dropdown.list"));
        }
        else if (experiment.equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors.add("int.experiment", new ActionError("error.dropdown.list"));
        }
        if (errors.isEmpty()) {
            // No errors; set the trimmed values (no need to trim again in the
            // action class.
            set("organism", organism);
            set("interactionType", interaction);
            set("experiment", experiment);
        }
        return errors;
    }

    /**
     * Reset all properties to their default values.
     *
     * @param mapping the mapping used to select this instance
     * @param request the servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        set("organism", EditorMenuFactory.SELECT_LIST_ITEM);
        set("interactionType", EditorMenuFactory.SELECT_LIST_ITEM);
        set("experiment", EditorMenuFactory.SELECT_LIST_ITEM);
    }
}
