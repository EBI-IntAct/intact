/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.biosrc;

import org.apache.struts.tiles.ComponentContext;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.exception.validation.BioSourceException;
import uk.ac.ebi.intact.application.editor.exception.validation.ValidationException;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.CvCellType;
import uk.ac.ebi.intact.model.CvTissue;

import java.util.List;

/**
 * BioSource edit view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class BioSourceViewBean extends AbstractEditViewBean {

    /**
     * The tax id.
     */
    private String myTaxId;

    private String myCellType;
    private String myTissue;

    // Override the super method to initialize this class specific resetting.
    protected void reset(Class clazz) {
        super.reset(clazz);
        // Set fields to null.
        setTaxId(null);
        setCellType(null);
        setTissue(null);
    }

    // Override the super method to set the tax id.
    protected void reset(AnnotatedObject annobj) {
        super.reset(annobj);

        // Must be a BioSource.
        BioSource bio = (BioSource) annobj;

        // Must have a tax id.
        setTaxId(bio.getTaxId());

        CvCellType cellType = bio.getCvCellType();
        setCellType(cellType != null ? cellType.getShortLabel() : null);

        CvTissue tissue = bio.getCvTissue();
        setTissue(tissue != null ? tissue.getShortLabel() : null);
    }

    // Implements abstract methods

    protected void updateAnnotatedObject(EditUserI user) throws SearchException {
        // The current biosource.
        BioSource bs = (BioSource) getAnnotatedObject();

        // Have we set the annotated object for the view?
        if (bs == null) {
            // Not persisted; create a new biosource object.
            bs = new BioSource(user.getInstitution(), getShortLabel(), getTaxId());
            setAnnotatedObject(bs);
        }
        else {
            bs.setTaxId(getTaxId());
        }
        // Set tissue and cell objects.
        bs.setCvTissue(getTissue(user));
        bs.setCvCellType(getCellType(user));
    }

    // Override to copy biosource from the form to the bean.
    public void copyPropertiesFrom(EditorActionForm editorForm) {
        // Set the common values by calling super first.
        super.copyPropertiesFrom(editorForm);

        // Cast to the biosource form to get biosource data.
        BioSourceActionForm bsform = (BioSourceActionForm) editorForm;

        setTaxId(bsform.getTaxId());
        setTissue(bsform.getTissue());
        setCellType(bsform.getCellType());
    }

    // Override to copy BS data to given form.
    public void copyPropertiesTo(EditorActionForm form) {
        super.copyPropertiesTo(form);

        // Cast to the biosource form to copy biosource data.
        BioSourceActionForm bsform = (BioSourceActionForm) form;

        bsform.setTaxId(getTaxId());
        bsform.setTissue(getTissue());
        bsform.setCellType(getCellType());
    }

    // Override to provide BioSource layout.
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.biosrc.layout");
    }

    // Override to provide BioSource help tag.
    public String getHelpTag() {
        return "editor.biosource";
    }

    // Override to provide biosource specific sanity checking.
    public void sanityCheck(EditUserI user) throws ValidationException,
            SearchException {
        // There should be one unique bisosurce.
        if ((getCellType() == null) && (getTissue() == null)) {
            BioSource bs = user.getBioSourceByTaxId(myTaxId);
            if (bs !=null) {
                // A BioSource found.
                if (!bs.getAc().equals(getAc())) {
                    // Different biosources.
                    throw new BioSourceException("bs.sanity.taxid.dup",
                         "error.bs.sanity.taxid.dup");
                }
            }
        }
    }

    // Getter/Setter methods for attributes.

    public String getTaxId() {
        return myTaxId;
    }

    public void setTaxId(String taxid) {
        myTaxId = taxid;
    }

    public String getCellType() {
        return myCellType;
    }

    public void setCellType(String cellType) {
        myCellType = EditorMenuFactory.normalizeMenuItem(cellType);
    }

    public String getTissue() {
        return myTissue;
    }

    public void setTissue(String tissue) {
        myTissue = EditorMenuFactory.normalizeMenuItem(tissue);
    }

    /**
     * The cell type menu list.
     * @return the cell type menu consisting of CvCellType short labels.
     * The first item in the menu contains the '---Select---' item.
     * @throws SearchException for errors in generating menus.
     */
    public List getCellTypeMenu() throws SearchException {
        return getMenuFactory().getMenu(EditorMenuFactory.CELLS, 1);
    }

    /**
     * The tissue menu list.
     * @return the tissue menu consisting of CvTissue short labels.
     * The first item in the menu contains the '---Select---' item.
     * @throws SearchException for errors in generating menus.
     */
    public List getTissueMenu() throws SearchException {
        return getMenuFactory().getMenu(EditorMenuFactory.TISSUES, 1);
    }

    /**
     * Returns the CvTissue object using the current tissue.
     * @param user the user to get the CvTissue object from a persistent system.
     * @return CvTissue object or null if the current tissue is null.
     * @throws SearchException for errors in retrieving the CvTisue object.
     */
    private CvTissue getTissue(EditUserI user) throws SearchException {
        if (myTissue == null) {
            return null;
        }
        return (CvTissue) user.getObjectByLabel(CvTissue.class, myTissue);
    }

    /**
     * Returns the CvCellType object using the current cell type.
     * @param user the user to get the CvCellType object from a persistent system.
     * @return CvCellType object or null if the current cell type is null.
     * @throws SearchException for errors in retrieving the CvCellType object.
     */
    private CvCellType getCellType(EditUserI user) throws SearchException {
        if (myCellType == null) {
            return null;
        }
        return (CvCellType) user.getObjectByLabel(CvCellType.class, myCellType);
    }
}
