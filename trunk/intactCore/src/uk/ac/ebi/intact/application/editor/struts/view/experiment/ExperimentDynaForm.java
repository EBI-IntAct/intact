/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import org.apache.struts.action.*;
import org.apache.struts.validator.DynaValidatorForm;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;

/**
 * The form to edit bio experiment data.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentDynaForm extends DynaValidatorForm {

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
        String organism = removeDots((String) get("organism"));
        String interaction = removeDots((String) get("interaction"));
        String identification = removeDots((String) get("identification"));

        // Must select from the drop down list.
        if (organism.equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors.add("exp.organism", new ActionError("error.dropdown.list"));
        }
        else if (interaction.equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors.add("exp.interaction", new ActionError("error.dropdown.list"));
        }
        else if (identification.equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors.add("exp.identification", new ActionError("error.dropdown.list"));
        }
        if (errors.isEmpty()) {
            // No errors; set the trimmed values (no need to trim again in the
            // action class.
            set("organism", organism);
            set("interaction", interaction);
            set("identification", identification);
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
        set("interaction", EditorMenuFactory.SELECT_LIST_ITEM);
        set("identification", EditorMenuFactory.SELECT_LIST_ITEM);
    }

    /**
     * Removes leading '.' characters from given string.
     * @param s the string to remove leading '.' characters.
     * @return a string without leading '.' characters. This my equivalent to
     * the given string if it has no leading '.' characters.
     */
    private String removeDots(String s) {
        if (s.charAt(0) != '.') {
            return s;
        }
        // At least the first psotion contains '.'

        // Holds the first non dot position.
        int pos = 0;
        int size = s.length();
        // Search for non dot position, if found exit the loop.
        for (int i = 1; i < size; i++) {
            if (s.charAt(i) != '.') {
                pos = i;
                break;
            }
        }
        return s.substring(pos);
    }
}
