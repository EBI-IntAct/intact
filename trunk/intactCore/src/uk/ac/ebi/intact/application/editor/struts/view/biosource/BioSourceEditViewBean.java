/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.biosource;

import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditViewBeanFactory;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.business.IntactException;

/**
 * BioSource edit view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class BioSourceEditViewBean extends AbstractEditViewBean {

    /**
     * The tax id.
     */
    private String myTaxId;

    // Override the super method to set the tax id.
    public void setAnnotatedObject(BioSource biosrc) {
        System.out.println("In biosource editor " + biosrc.getShortLabel());
        super.setAnnotatedObject(biosrc);
        setTaxId(biosrc.getTaxId());
    }

    // Override the super method to persist tax id.
    public void persist(EditUserI user) throws IntactException {
        // The order is important as super's persist calls update to
        // update the object as the last persistence call.
        ((BioSource) getAnnotatedObject()).setTaxId(getTaxId());
        super.persist(user);
    }

    // Override to provide the editory type.
    public String getEditorType() {
        return EditViewBeanFactory.BIOSRC_EDITOR;
    }

    // Getter/Setter methods for tax id.
    public String getTaxId() {
        return myTaxId;
    }
    public void setTaxId(String taxid) {
        myTaxId = taxid;
    }
}
