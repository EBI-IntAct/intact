/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view;

import org.apache.struts.action.*;
import org.apache.struts.validator.DynaValidatorForm;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;

/**
 * The form to add new Xref.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class XrefAddDynaForm extends DynaValidatorForm {

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
        String database = ((String) get("database")).trim();
        String qualifier = ((String) get("qualifier")).trim();

        // They must have selected a database.
        if (database.equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors.add("xref.database", new ActionError("error.dropdown.list"));
        }
        // They must have selected a qualifier.
        else if (qualifier.equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
            errors.add("xref.qualifier", new ActionError("error.dropdown.list"));
        }
        if (errors.isEmpty()) {
            // No errors; set the trimmed values (no need to trim again in the
            // action class.
            set("database", database);
            set("qualifier", qualifier);
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
        set("database", EditorMenuFactory.SELECT_LIST_ITEM);
        set("primaryId", "");
        set("secondaryId", "");
        set("releaseNumber", "");
        set("qualifier", EditorMenuFactory.SELECT_LIST_ITEM);
    }
}
