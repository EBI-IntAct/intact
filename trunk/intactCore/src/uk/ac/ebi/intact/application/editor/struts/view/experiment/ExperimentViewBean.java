/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import org.apache.struts.action.DynaActionForm;
import org.apache.struts.tiles.ComponentContext;
import org.apache.commons.collections.CollectionUtils;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.exception.validation.ExperimentException;
import uk.ac.ebi.intact.application.editor.exception.validation.ValidationException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
     * Holds Interactions to add. This collection is cleared once the user
     * commits the transaction.
     */
    private transient List myInteractionsToAdd = new ArrayList();

    /**
     * Holds Interactions to del. This collection is cleared once the user
     * commits the transaction.
     */
    private transient List myInteractionsToDel = new ArrayList();

    /**
     * Holds Interaction to not yet added. Only valid for the current session.
     */
    private transient List myInteractionsToHold = new ArrayList();

    /**
     * Constructs with the Intact helper.
     * @param helper the Intact helper.
     */
//    public ExperimentViewBean(IntactHelper helper) {
//        super(helper);
//    }

    // Reset the fields to null if we don't have values to set. Failure
    // to do so will display the previous edit object's values as current.
    public void setAnnotatedObject(AnnotatedObject annobj) {
        super.setAnnotatedObject(annobj);
        Experiment exp = (Experiment) annobj;
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
        // Clear any left overs from previous transaction.
        clearTransactions();

        // Prepare for Interactions for display.
        makeInteractionBeans(exp.getInteractions());
    }

    // Implements abstract methods

    protected void updateAnnotatedObject(EditUserI user) throws SearchException {
        // Get the objects using their short label.
        BioSource biosource = (BioSource) user.getObjectByLabel(
                BioSource.class, myOrganism);
        CvInteraction interaction = (CvInteraction) user.getObjectByLabel(
                CvInteraction.class, myInter);
        CvIdentification ident = (CvIdentification) user.getObjectByLabel(
                CvIdentification.class, myIdent);

        // The current experiment.
        Experiment exp = (Experiment) getAnnotatedObject();

        // Have we set the annotated object for the view?
        if (exp == null) {
            if (getAc() == null) {
                // Can't read from the persistent system. Create a new Experiment.
                exp = new Experiment(user.getInstitution(), getShortLabel(), biosource);
            }
            else {
                // Read it from the peristent system first and then update it.
                exp = (Experiment) user.getObjectByAc(getEditClass(), getAc());
                exp.setShortLabel(getShortLabel());
                exp.setBioSource(biosource);
            }
            // Set the current experiment as the annotated object.
            setAnnotatedObject(exp);
        }
        exp.setCvInteraction(interaction);
        exp.setCvIdentification(ident);
        exp.setFullName(getFullName());

        // Add interaction to the experiment.
        for (Iterator iter = getInteractionsToAdd().iterator(); iter.hasNext();) {
            Interaction intact = ((InteractionBean) iter.next()).getInteraction();
            intact.addExperiment(exp);
        }
        // Delete interactions from the experiment.
        for (Iterator iter = getInteractionsToDel().iterator(); iter.hasNext();) {
            Interaction intact = ((InteractionBean) iter.next()).getInteraction();
            intact.removeExperiment(exp);
        }
    }

    // Override the super method to update the current Experiment.
//    public void updateXXX(EditUserI user) throws SearchException {
//        super.updateXXX(user);
//        // The current Experiment object we want to update
//        Experiment exp = (Experiment) getAnnotatedObject();
//
//        // Get the objects using their short label.
//        BioSource biosource = (BioSource) user.getObjectByLabel(
//                BioSource.class, myOrganism);
//        CvInteraction interaction = (CvInteraction) user.getObjectByLabel(
//                CvInteraction.class, myInter);
//        CvIdentification ident = (CvIdentification) user.getObjectByLabel(
//                CvIdentification.class, myIdent);
//
//        // Update the experiment object.
//        exp.setBioSource(biosource);
//        exp.setCvInteraction(interaction);
//        exp.setCvIdentification(ident);
//
//        // Add interaction to the experiment.
//        for (Iterator iter = getInteractionsToAdd().iterator(); iter.hasNext();) {
//            Interaction intact = ((InteractionBean) iter.next()).getInteraction();
//            intact.addExperiment(exp);
//        }
//        // Delete interactions from the experiment.
//        for (Iterator iter = getInteractionsToDel().iterator(); iter.hasNext();) {
//            Interaction intact = ((InteractionBean) iter.next()).getInteraction();
//            intact.removeExperiment(exp);
//        }
//    }

    // Override the super method to this bean's info.
//    public void persist(EditUserI user) throws IntactException, SearchException {
//        // The order is important! update super last as it does
//        // the update of the object.
//        Experiment exp = (Experiment) getAnnotatedObject();
//
//        // Get the objects using their short label.
//        BioSource biosource = (BioSource) user.getObjectByLabel(
//                BioSource.class, myOrganism);
//        CvInteraction interaction = (CvInteraction) user.getObjectByLabel(
//                CvInteraction.class, myInter);
//        CvIdentification ident = (CvIdentification) user.getObjectByLabel(
//                CvIdentification.class, myIdent);
//
//        // Update the experiment object.
//        exp.setBioSource(biosource);
//        exp.setCvInteraction(interaction);
//        exp.setCvIdentification(ident);
//
//        // Add interaction to the experiment.
//        for (Iterator iter = getInteractionsToAdd().iterator(); iter.hasNext();) {
//            Interaction intact = ((InteractionBean) iter.next()).getInteraction();
//            intact.addExperiment(exp);
//        }
//        // Delete interactions from the experiment.
//        for (Iterator iter = getInteractionsToDel().iterator(); iter.hasNext();) {
//            Interaction intact = ((InteractionBean) iter.next()).getInteraction();
//            intact.removeExperiment(exp);
//        }
//        super.persist(user);
//
//        // The experiment is sucessfully persisted; add this to the
//        // current edit/add experiment list.
//        user.addToCurrentExperiment(exp);
//    }

    // Override the super method as the current experiment is added to the
    // recent experiment list.
    public void addToRecentList(EditUserI user) {
        user.addToCurrentExperiment((Experiment) getAnnotatedObject());
    }

    // Override to remove the current experiment from the recent list.
    public void removeFromRecentList(EditUserI user) {
        user.removeFromCurrentExperiment((Experiment) getAnnotatedObject());
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
     * Adds an Interaction.
     * @param intbean the Interaction bean to add.
     *
     * <pre>
     * post: myInteractionsToAdd = myInteractionsToAdd@pre + 1
     * post: myInteractions = myInteractions@pre + 1
     * </pre>
     */
    public void addInteraction(InteractionBean intbean) {
        // Interaction to add.
        myInteractionsToAdd.add(intbean);
        // Add to the view as well.
        myInteractions.add(intbean);
    }

    /**
     * True if given interaction exists in this object's interaction collection.
     * @param intbean the bean to compare.
     * @return true <code>intbean</code> exists in this object's interaction
     * collection. The comparision uses the equals method of
     * <code>InteractionBean</code> class.
     *
     * <pre>
     * post: return->true implies myInteractionsToAdd.exists(intbean)
     * </pre>
     */
    public boolean interactionExists(InteractionBean intbean) {
        return myInteractions.contains(intbean);
    }

    /**
     * Removes an Interaction
     * @param intbean the Interaction bean to remove.
     *
     * <pre>
     * post: myInteractionsToDel = myInteractionsToDel@pre - 1
     * post: myInteractions = myInteractions@pre - 1
     * </pre>
     */
    public void delInteraction(InteractionBean intbean) {
        // Add to the container to delete interactions.
        myInteractionsToDel.add(intbean);
        // Remove from the view as well.
        myInteractions.remove(intbean);
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

    /**
     * Returns a collection of <code>InteractionBean</code> objects.
     *
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(InteractionBean))
     * </pre>
     */
    public List getInteractions() {
        return myInteractions;
    }

    /**
     * Returns a collection of <code>InteractionBean</code> objects on hold.
     *
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(InteractionBean))
     * </pre>
     */
    public List getHoldInteractions() {
        return myInteractionsToHold;
    }

    /**
     * Hides an Interaction bean from hold.
     * @param intbean an <code>InteractionBean</code> to hide.
     * <pre>
     * pre: myInteractionsToHold->includes(intbean)
     * post: myInteractionsToHold = myInteractionsToHold@pre - 1
     * </pre>
     */
    public void hideInteractionToHold(InteractionBean intbean) {
        myInteractionsToHold.remove(intbean);
    }

    /**
     * Clears all the interactions on hold.
     */
    public void clearInteractionToHold() {
        myInteractionsToHold.clear();
    }

    // Override super to add extra.
    public void clearTransactions() {
        super.clearTransactions();

        // Clear experiments.
//        myInteractionsToAdd.clear();
//        myInteractionsToDel.clear();
        myInteractionsToHold.clear();
    }

    // Helper methods

    private void makeInteractionBeans(Collection ints) {
        myInteractions.clear();
        for (Iterator iter = ints.iterator(); iter.hasNext();) {
            Interaction interaction = (Interaction) iter.next();
            myInteractions.add(new InteractionBean(interaction));
        }
    }

    /**
     * Returns a collection of interactions to add.
     * @return the collection of interactions to add to the current Experiment.
     * Empty if there are no interactions to add.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(InteractionBean)
     * </pre>
     */
    private Collection getInteractionsToAdd() {
        // Interaction common to both add and delete.
        Collection common = CollectionUtils.intersection(
                myInteractionsToAdd, myInteractionsToDel);
        // All the interactions only found in interaction to add collection.
        return CollectionUtils.subtract(myInteractionsToAdd, common);
    }

    /**
     * Returns a collection of interactions to remove.
     * @return the collection of interactions to remove from the current Experiment.
     * Could be empty if there are no interactions to delete.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(InteractionBean)
     * </pre>
     */
    private Collection getInteractionsToDel() {
        // Interactions common to both add and delete.
        Collection common = CollectionUtils.intersection(
                myInteractionsToAdd, myInteractionsToDel);
        // All the interactions only found in interaction to delete collection.
        return CollectionUtils.subtract(myInteractionsToDel, common);
    }
}
