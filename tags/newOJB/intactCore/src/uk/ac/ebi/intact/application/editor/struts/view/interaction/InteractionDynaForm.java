/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.struts.view.cv.CvDynaForm;

import javax.servlet.http.HttpServletRequest;

/**
 * The form to validate interaction info data.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionDynaForm extends CvDynaForm {

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
        ActionErrors errors = super.validate(mapping, request);

        // Only proceed if super method does not find any errors.
        if ((errors != null) && !errors.isEmpty()) {
            return errors;
        }
        // Validate required fields.
        String interaction = (String) get("interactionType");
        String organism = (String) get("organism");

        // Must select from the drop down list.
        if (interaction.equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.int.cvtype"));
            return errors;
        }
        if (organism.equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.int.biosrc"));
            return errors;
        }
        // Must have at least one experiment.
        ExperimentBean[] exps = (ExperimentBean[]) get("exps");
        if (exps.length == 0) {
            errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.int.validation.exp"));
        }
        return errors;
    }
}
