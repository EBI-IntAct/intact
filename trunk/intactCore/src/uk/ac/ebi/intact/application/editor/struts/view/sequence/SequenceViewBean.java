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
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.util.Crc64;

import java.util.HashMap;
import java.util.Map;

/**
 * Sequence edit view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SequenceViewBean extends AbstractEditViewBean {

    /**
     * The sequence
     */
    private String mySequence;

    /**
     * The organism for the sequence.
     */
    private String myOrganism;

    /**
     * The map of menus for this view.
     */
    private transient Map myMenus = new HashMap();

    // Override the super method to initialize this class specific resetting.
    protected void reset(Class clazz) {
        super.reset(clazz);
        // Set fields to null.
        mySequence = null;
        myOrganism = null;
    }

    // Override the super method to set the tax id.
    protected void reset(AnnotatedObject annobj) {
        super.reset(annobj);

        // Must be a protein (for the moment, this could be the new abstract super).
        Protein prot = (Protein) annobj;

        // Set the bean data
        myOrganism = prot.getBioSource().getShortLabel();
        mySequence = prot.getSequence();
    }

    // Implements abstract methods

    protected void updateAnnotatedObject(IntactHelper helper) throws IntactException {
        // Get the objects using their short label.
        BioSource biosrc = (BioSource) helper.getObjectByLabel(BioSource.class,
                myOrganism);

        // The current protein
        Protein prot = (Protein) getAnnotatedObject();

        // Have we set the annotated object for the view?
        if (prot == null) {
            // Not persisted; create a new Protein
            prot = new ProteinImpl(getService().getOwner(), biosrc, getShortLabel());
            setAnnotatedObject(prot);
        }
        else {
            prot.setBioSource(biosrc);
        }
        // Set the sequence should have done here but we can't do it because it
        // will go ahead and start persisting sequences on the DB!!! This behaviour
        // is TOTALLY inconsistent with other editors (they all persist records only
        // upon submitting the form (or save & continue).
        // prot.setSequence(user.getIntactHelper(), getSequence());
        prot.setCrc64(Crc64.getCrc64(mySequence));
    }

    protected void clearMenus() {
        myMenus.clear();
    }

    // Override to copy sequence data from the form to the bean.
    public void copyPropertiesFrom(EditorFormI editorForm) {
        // Set the common values by calling super first.
        super.copyPropertiesFrom(editorForm);

        // Cast to the sequence form to get sequence data.
        SequenceActionForm seqform = (SequenceActionForm) editorForm;

        myOrganism = seqform.getOrganism();
        mySequence = seqform.getSequence();
    }

    // Override to copy sequence data to given form.
    public void copyPropertiesTo(EditorFormI form) {
        super.copyPropertiesTo(form);

        // Cast to the sequence form to copy sequence data.
        SequenceActionForm seqform = (SequenceActionForm) form;

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
            Protein prot = (Protein) getAnnotatedObject();
            IntactHelper helper = user.getIntactHelper();
            try {
                // Only set the sequence for when we have a seq.
                prot.setSequence(helper, getSequence());
            }
            finally {
                helper.closeStore();
            }
        }
    }

    /**
     * Override to provide the menus for this view.
     * @return a map of menus for this view. It consists of common menus for
     * annotation/xref and organism (add or edit).
     * @throws IntactException for errors in accessing the persistent system.
     */
    public Map getMenus() throws IntactException {
        if (!myMenus.isEmpty()) {
            return myMenus;
        }
        // The Intact helper to construct menus.
        IntactHelper helper = new IntactHelper();

        try {
            myMenus.putAll(super.getMenus(helper));

            String name = EditorMenuFactory.ORGANISM;
            int mode = (myOrganism == null) ? 1 : 0;
            myMenus.put(name, EditorMenuFactory.getInstance().getMenu(name, mode, helper));
        }
        finally {
            helper.closeStore();
        }
        return myMenus;
    }
}
