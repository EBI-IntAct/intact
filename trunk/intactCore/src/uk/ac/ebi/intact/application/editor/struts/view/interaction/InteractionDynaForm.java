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
 * The form to validate interaction info data.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionDynaForm extends DynaValidatorForm {

    /**
     * Validates Interaction info page.
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
        String organism = (String) get("organism");
        String interaction = (String) get("interactionType");

        // Must select from the drop down list.
        if (interaction.equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.int.biosrc"));
        }
        else if (organism.equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.int.cvtype"));
        }
        return errors;
    }
}
