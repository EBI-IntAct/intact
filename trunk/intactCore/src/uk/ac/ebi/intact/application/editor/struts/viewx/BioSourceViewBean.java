/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.viewx;

import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.business.IntactException;
import org.apache.commons.beanutils.DynaBean;
import org.apache.struts.tiles.ComponentContext;

/**
 * BioSource edit view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class BioSourceViewBean extends AbstractEditViewBean {

    /**
     * The name of the BioSource form.
     */
//    public static final String BIOSOURCE_FORM = "bioSourceForm";

    /**
     * The tax id.
     */
    private String myTaxId;

    // Override the super method to set the tax id.
    public void setAnnotatedObject(BioSource biosrc) {
        super.setAnnotatedObject(biosrc);
        setTaxId(biosrc.getTaxId());
    }

    // Override the super method to persist tax id.
    public void persist(EditUserI user) throws IntactException {
        // The order is important! update super last as it does
        // the update of the object.
        ((BioSource) getAnnotatedObject()).setTaxId(getTaxId());
        super.persist(user);
    }

    // Ovverride to provide BioSource layout.
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.biosrc.layout");
    }

    // Implement the abstract methods.

    public void fillEditorSpecificInfo(DynaBean form) {
        // Fill the form with current values.
        form.set("taxId", getTaxId());
    }

    // Getter/Setter methods for tax id.
    public String getTaxId() {
        return myTaxId;
    }

    public void setTaxId(String taxid) {
        myTaxId = taxid;
    }
}
