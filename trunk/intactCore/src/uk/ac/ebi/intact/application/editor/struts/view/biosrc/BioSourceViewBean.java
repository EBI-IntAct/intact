/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.biosrc;

import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.action.DynaActionForm;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.AnnotatedObject;
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
        // Set tissue and cell objects if they aren't null.
        if (getTissue() != null) {
            CvTissue tissue = (CvTissue) user.getObjectByLabel(
                    CvTissue.class, getTissue());
            bs.setCvTissue(tissue);
        }
        if (getCellType() != null) {
            CvCellType cell = (CvCellType) user.getObjectByLabel(
                    CvCellType.class, getCellType());
            bs.setCvCellType(cell);
        }
    }

    // Override to provide set experiment from the bean.
    public void updateFromForm(DynaActionForm dynaform) {
        // Set the common values by calling super first.
        super.updateFromForm(dynaform);

        // These two items need to be normalized.
        String tissue = (String) dynaform.get("tissue");
        if (!EditorMenuFactory.SELECT_LIST_ITEM.equals(tissue)) {
            setTissue(tissue);
        }

        String cell = (String) dynaform.get("cellType");
        if (!EditorMenuFactory.SELECT_LIST_ITEM.equals(cell)) {
            setCellType(cell);
        }
    }

    // Override to provide BioSource layout.
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.biosrc.layout");
    }

    // Override to provide BioSource help tag.
    public String getHelpTag() {
        return "editor.biosource";
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
        myCellType = cellType;
    }

    public String getTissue() {
        return myTissue;
    }

    public void setTissue(String tissue) {
        myTissue = tissue;
    }

    /**
     * The cell type menu list.
     * @return the cell type menu consisting of CvCellType short labels.
     * The first item in the menu may contain '---Select---' if the cell type
     * is not set.
     * @throws SearchException for errors in generating menus.
     */
    public List getCellTypeMenu() throws SearchException {
        int mode = (myCellType == null) ? 1 : 0;
        return getMenuFactory().getMenu(EditorMenuFactory.CELLS, mode);
    }

    /**
     * The tissue menu list.
     * @return the tissue menu consisting of CvTissue short labels.
     * The first item in the menu may contain '---Select---' if the tissue is
     * not set.
     * @throws SearchException for errors in generating menus.
     */
    public List getTissueMenu() throws SearchException {
        int mode = (myTissue == null) ? 1 : 0;
        return getMenuFactory().getMenu(EditorMenuFactory.TISSUES, mode);
    }
}
