/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.sequence;

import org.apache.struts.tiles.ComponentContext;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorFormI;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.PolymerFactory;
import uk.ac.ebi.intact.util.Crc64;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sequence edit view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public abstract class SequenceViewBean extends AbstractEditViewBean {

    /**
     * The sequence
     */
    private String mySequence;

    /**
     * The interactor type
     */
    private String myInteractorType;

    /**
     * The organism for the sequence.
     */
    private String myOrganism;

    // Override the super method to initialize this class specific resetting.
    public void reset() {
        super.reset();
        // Set fields to null.
        myInteractorType = null;
        myOrganism = null;
        mySequence = null;
    }

    // Override the super method to set the tax id.
    public void reset(AnnotatedObject annobj) {
        super.reset(annobj);

        // Cast to the new abstract super.
        Polymer polymer = (Polymer) annobj;

        // Set the bean data
        myInteractorType = polymer.getCvInteractorType().getShortLabel();
        myOrganism = polymer.getBioSource().getShortLabel();
        mySequence = polymer.getSequence();
    }

    // Override to copy sequence data from the form to the bean.
    public void copyPropertiesFrom(EditorFormI editorForm) {
        // Set the common values by calling super first.
        super.copyPropertiesFrom(editorForm);

        // Cast to the sequence form to get sequence data.
        SequenceActionForm seqform = (SequenceActionForm) editorForm;

        myInteractorType = seqform.getInteractorType();
        myOrganism = seqform.getOrganism();
        mySequence = seqform.getSequence();
    }

    // Override to copy sequence data to given form.
    public void copyPropertiesTo(EditorFormI form) {
        super.copyPropertiesTo(form);

        // Cast to the sequence form to copy sequence data.
        SequenceActionForm seqform = (SequenceActionForm) form;

        seqform.setInteractorType(myInteractorType);
        seqform.setOrganism(myOrganism);
        seqform.setSequence(mySequence);
    }

    // Override to provide Sequence layout.
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.sequence.layout");
    }

    // Override to provide Sequence help tag.
    public String getHelpTag() {
        return "editor.sequence";
    }

    // Getter/Setter methods for attributes.

    public String getSequence() {
        return mySequence;
    }

    public void persistOthers(EditUserI user) throws IntactException {
        // Set the sequence here, so it will create sequence records.
        if (getSequence().length() > 0) {
            // The current protein.
            Polymer polymer = (Polymer) getAnnotatedObject();
            // Only set the sequence for when we have a seq.
            List emptyChunks = polymer.setSequence(getSequence());
            if (!emptyChunks.isEmpty()) {
                user.getIntactHelper().deleteAllElements(emptyChunks);
            }
        }
    }

    // --------------------- Protected Methods ---------------------------------

    /**
     * Override to provide the menus for this view.
     * @return a map of menus for this view. It consists of common menus for
     * annotation/xref and organism (add or edit).
     */
    protected Map getMenus() throws IntactException {
        // The map containing the menus.
        Map map = new HashMap();

        map.putAll(super.getMenus());
//        map.putAll(super.getMenus(Protein.class.getName()));

        String name = EditorMenuFactory.ORGANISM;
        int mode = (myOrganism == null) ? 1 : 0;
        map.put(name, EditorMenuFactory.getInstance().getMenu(name, mode));
        return map;
    }

    // Implements abstract methods

    protected void updateAnnotatedObject(IntactHelper helper) throws IntactException {
        // Get the objects using their short label.
        BioSource biosrc = (BioSource) helper.getObjectByLabel(BioSource.class,
                myOrganism);
        CvInteractorType intType = (CvInteractorType) helper.getObjectByLabel(
                CvInteractorType.class, myInteractorType);
        // The current polymer
        Polymer polymer = (Polymer) getAnnotatedObject();

        // Have we set the annotated object for the view?
        if (polymer == null) {
            // Not persisted; create a new Polymer using the factory
            polymer = PolymerFactory.factory(getService().getOwner(), biosrc,
                    getShortLabel(), intType);
            setAnnotatedObject(polymer);
        }
        else {
            polymer.setCvInteractorType(intType);
            polymer.setBioSource(biosrc);
        }
        // Set the sequence in the persistOthers method we can safely delete
        // unused sequences.
        if (getSequence().length() > 0) {
            polymer.setSequence(getSequence());
            polymer.setCrc64(Crc64.getCrc64(getSequence()));
        }
    }

    protected String getInteractorType() {
        return myInteractorType;
    }
}


