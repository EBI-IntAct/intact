/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import org.apache.struts.action.DynaActionForm;
import org.apache.struts.tiles.ComponentContext;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.exception.validation.ExperimentException;
import uk.ac.ebi.intact.application.editor.exception.validation.ValidationException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ExperimentBean;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Experiment edit view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentViewBean extends AbstractEditViewBean {

    /**
     * The host organism.
     */
    private String myOrganism;

    /**
     * The interaction with the experiment.
     */
    private String myInter;

    /**
     * The experiment identification.
     */
    private String myIdent;

    /**
     * The collection of Interactions. Transient as it is only valid for the
     * current display.
     */
    private transient List myInteractions = new ArrayList();

    /**
     * Holds Interaction to not yet added. Only valid for the current session.
     */
    private transient List myInteractionsToHold = new ArrayList();

    // Reset the fields to null if we don't have values to set. Failure
    // to do so will display the previous edit object's values as current.
    public void setAnnotatedObject(Experiment exp) {
        super.setAnnotatedObject(exp);
        // Only set the short labels if the experiment has the objects.
        BioSource biosrc = exp.getBioSource();
        if (biosrc != null) {
            myOrganism = biosrc.getShortLabel();
        }
        else {
            myOrganism = null;
        }
        CvInteraction inter = exp.getCvInteraction();
        if (inter != null) {
            myInter = inter.getShortLabel();
        }
        else {
            myInter = null;
        }
        CvIdentification ident = exp.getCvIdentification();
        if (ident != null) {
            myIdent = ident.getShortLabel();
        }
        else {
            myIdent = null;
        }
    }

    // Override the super method to this bean's info.
    public void persist(EditUserI user) throws IntactException, SearchException {
        // The order is important! update super last as it does
        // the update of the object.
        Experiment exp = (Experiment) getAnnotatedObject();

        // Get the objects using their short label.
        BioSource biosource = (BioSource) user.getObjectByLabel(
                BioSource.class, myOrganism);
        CvInteraction interaction = (CvInteraction) user.getObjectByLabel(
                CvInteraction.class, myInter);
        CvIdentification ident = (CvIdentification) user.getObjectByLabel(
                CvIdentification.class, myIdent);

        // Update the experiment object.
        exp.setBioSource(biosource);
        exp.setCvInteraction(interaction);
        exp.setCvIdentification(ident);
        super.persist(user);

        // The experiment is sucessfully persisted; add this to the
        // current edit/add experiment list.
        user.addToCurrentExperiment(exp);
    }

    // Ovverride to provide Experiment layout.
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.exp.layout");
    }

    // Override to provide Experiment help tag.
    public String getHelpTag() {
        return "editor.experiment";
    }

    // Override to provide set experiment from the bean.
    public void updateFromForm(DynaActionForm dynaform) {
        // Set the common values by calling super first.
        super.updateFromForm(dynaform);
        String organism = (String) dynaform.get("organism");
        if (!EditorMenuFactory.SELECT_LIST_ITEM.equals(organism)) {
            // Set the view bean with the new values.
            setOrganism(organism);
        }

        // These two items need to be normalized.
        String interaction = (String) dynaform.get("inter");
        if (!EditorMenuFactory.SELECT_LIST_ITEM.equals(interaction)) {
            setInter(interaction);
        }
        String identification = (String) dynaform.get("ident");
        if (!EditorMenuFactory.SELECT_LIST_ITEM.equals(identification)) {
            setIdent(identification);
        }
    }

    // Null for any of these values will throw an exception.
    public void validate(EditUserI user) throws ValidationException,
            SearchException {
        super.validate(user);
        if (myOrganism == null) {
            throw new ExperimentException("error.exp.biosrc");
        }
        else if (myInter == null) {
            throw new ExperimentException("error.exp.inter");
        }
        else if (myIdent == null) {
            throw new ExperimentException("error.exp.ident");
        }
    }

    // Override the super method to clear this object.
    public void clear() {
        super.clear();
        // Clear of any references to this object.
        myOrganism = null;
        myInter = null;
        myIdent = null;
    }

    /**
     * The organism menu list.
     * @return the organism menu consisting of organism short labels. The first
     * item in the menu may contain '---Select---' if the current organism is
     * not set.
     * @throws SearchException for errors in generating menus.
     */
    public List getOrganismMenu() throws SearchException {
        int mode = (myOrganism == null) ? 1 : 0;
        return getMenuFactory().getMenu(EditorMenuFactory.ORGANISMS, mode);
    }

    /**
     * The interaction menu list.
     * @return the interaction menu consisting of interaction short labels.
     * The first item in the menu may contain '---Select---' if the current
     * interaction is not set.
     * @throws SearchException for errors in generating menus.
     */
    public List getInterMenu() throws SearchException {
        int mode = (myInter == null) ? 1 : 0;
        return getMenuFactory().getDagMenu(EditorMenuFactory.INTERACTIONS, mode);
    }

    /**
     * The idetification menu list.
     * @return the idetification menu consisting of idetification short labels.
     * The first item in the menu may contain '---Select---' if the current
     * idetification is not set.
     * @throws SearchException for errors in generating menus.
     */
    public List getIdentMenu() throws SearchException {
        int mode = (myIdent == null) ? 1 : 0;
        return getMenuFactory().getDagMenu(EditorMenuFactory.IDENTIFICATIONS, mode);
    }

    // Getter/Setter methods for Organism.
    public String getOrganism() {
        return myOrganism;
    }

    public void setOrganism(String organism) {
        myOrganism = organism;
    }

    // Getter/Setter methods for Interaction.
    public String getInter() {
        return myInter;
    }

    public void setInter(String interaction) {
        myInter = interaction;
    }

    // Getter/Setter methods for Identification.
    public String getIdent() {
        return myIdent;
    }

    public void setIdent(String identification) {
        myIdent = identification;
    }

    /**
     * Adds an Interaction bean to hold if the new interaction doesn't
     * already exists in the interaction hold collection and in the
     * current interaction collection for this interaction.
     * @param ints a collection of <code>Interaction</code> to add.
     *
     * <pre>
     * pre:  forall(obj : Object | obj.oclIsTypeOf(Interaction))
     * </pre>
     */
    public void addInteractionToHold(Collection ints) {
        for (Iterator iter = ints.iterator(); iter.hasNext();) {
            InteractionBean expbean = new InteractionBean((Interaction) iter.next());
            // Avoid duplicates.
            if (!myInteractionsToHold.contains(expbean)
                    && !myInteractions.contains(expbean)) {
                myInteractionsToHold.add(expbean);
            }
        }
    }
}
