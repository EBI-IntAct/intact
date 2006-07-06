/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.biosrc;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.struts.view.cv.CvDynaForm;

import javax.servlet.http.HttpServletRequest;

/**
 * The form to validate biosource data.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class BioSourceDynaForm extends CvDynaForm {

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
        ActionErrors errors = super.validate(mapping, request);

        // Only proceed if super method does not find any errors.
        if ((errors != null) && !errors.isEmpty()) {
            return errors;
        }
        // Validate biosource specific fields.
        String taxid = (String) get("taxId");

        // Must be an integer.
        try {
            Integer.parseInt(taxid);
        }
        catch (NumberFormatException nfe) {
            errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.taxid.mask", taxid));
        }
        return errors;
    }
}
