/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.biosrc;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;

/**
 * The form to validate biosource data.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class BioSourceActionForm extends EditorActionForm {

    /**
     * The tax id.
     */
    private String myTaxId;

    /**
     * The CV cell type.
     */
    private String myCellType;

    /**
     * The tissue
     */
    private String myTissue;

    // Getter / Setter methods

    public String getTaxId() {
        return myTaxId;
    }

    public void setTaxId(String taxId) {
        myTaxId = taxId;
    }

    public String getCellType() {
        return myCellType;
    }

    public void setCellType(String cellType) {
        myCellType = cellType;
    }

    public String getTissue() {
        return myTissue;
    }

    public void setTissue(String tissue) {
        myTissue = tissue;
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
        ActionErrors errors = super.validate(mapping, request);

        // Only proceed if super method does not find any errors.
        if ((errors != null) && !errors.isEmpty()) {
            return errors;
        }
       // The tax id must be an integer.
        try {
            Integer.parseInt(getTaxId());
        }
        catch (NumberFormatException nfe) {
            errors = new ActionErrors();
            errors.add("bs.taxid", new ActionError("error.taxid.mask", getTaxId()));
        }
        return errors;
    }
}
