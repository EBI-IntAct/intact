/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.view;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseForm;

import javax.servlet.http.HttpServletRequest;

/**
 * The form to capture information for adding a new short label (when
 * creating a new CV object).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CvAddForm extends IntactBaseForm {

    /**
     * The short label.
     */
    private String myShortLabel;

    /**
     * Sets the short label.
     *
     * @param label the short label.
     */
    public void setShortLabel(String label) {
        myShortLabel = label;
    }

    /**
     * Returns the short label.
     */
    public String getShortLabel() {
        return myShortLabel;
    }

    /**
     * Validate the form contents entered by the user.
     *
     * @param mapping the mapping used to select this instance
     * @param request the servlet request we are processing
     * @return <tt>ActionErrors</tt> object that contains validation errors. If
     * no errors are found, <tt>null</tt> or an empty <tt>ActionErrors</tt>
     * object is returned.
     */
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = null;

        // Only verify if submitted.
        if (super.isSubmitted()) {
            if (myShortLabel == null || myShortLabel.length() < 1) {
                errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("global.required", "Short Label"));
            }
        }
        return errors;
    }
}
