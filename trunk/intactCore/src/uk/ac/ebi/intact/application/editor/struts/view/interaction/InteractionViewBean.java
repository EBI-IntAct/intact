/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts.tiles.ComponentContext;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.exception.validation.InteractionException;
import uk.ac.ebi.intact.application.editor.exception.validation.ValidationException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.struts.view.EditBean;
import uk.ac.ebi.intact.application.editor.struts.view.EditForm;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Interaction edit view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionViewBean extends AbstractEditViewBean {

    /**
     * The KD.
     */
    private Float myKD;

    /**
     * The host organism.
     */
    private String myOrganism;

    /**
     * The interaction with the experiment.
     */
    private String myInteractionType;

    /**
     * The collection of Experiments. Transient as it is only valid for the
     * current display.
     */
    private transient List myExperiments = new ArrayList();

    /**
     * Holds Experiments to add. This collection is cleared once the user
     * commits the transaction.
     */
    private transient List myExperimentsToAdd = new ArrayList();

    /**
     * Holds Experiments to del. This collection is cleared once the user
     * commits the transaction.
     */
    private transient List myExperimentsToDel = new ArrayList();

    /**
     * Holds Experiments to not yet added. Only valid for the current session.
     */
    private transient List myExperimentsToHold = new ArrayList();

    /**
     * The collection of Proteins. Transient as it is only valid for the
     * current display.
     */
    private transient List myProteins = new ArrayList();

    /**
     * Holds Proteins to del. This collection is cleared once the user
     * commits the transaction.
     */
    private transient List myProteinsToDel = new ArrayList();

    /**
     * Holds Proteins to update. This collection is cleared once the user
     * commits the transaction.
     */
    private transient Collection myProteinsToUpdate = new ArrayList();

    public void setAnnotatedObject(Interaction intact) {
        super.setAnnotatedObject(intact);

        myKD = intact.getKD();

        // Only set the short labels if the interaction has non null values.
        BioSource biosrc = intact.getBioSource();
        if (biosrc != null) {
            myOrganism = biosrc.getShortLabel();
        }
        else {
            myOrganism = null;
        }
        CvInteractionType inter = intact.getCvInteractionType();
        if (inter != null) {
            myInteractionType = intact.getCvInteractionType().getShortLabel();
        }
        else {
            myInteractionType = null;
        }
        // Clear any left overs from previous transaction.
        clearTransactions();

        // Prepare for Proteins and Experiments for display.
        makeExperimentBeans(intact.getExperiment());
        makeProteinBeans(intact.getComponent());
    }

    // Override the super method to this bean's info.
    public void persist(EditUserI user) throws IntactException, SearchException {
        // Persists the annotations and xrefs.
        super.persist(user);

        // The order is important! update super last as it does
        // the update of the object.
        Interaction intact = (Interaction) getAnnotatedObject();

        // Get the objects using their short label.
        BioSource biosource = (BioSource) user.getObjectByLabel(
                BioSource.class, myOrganism);
        CvInteractionType type = (CvInteractionType) user.getObjectByLabel(
                CvInteractionType.class, myInteractionType);

        intact.setBioSource(biosource);
        intact.setCvInteractionType(type);
        intact.setKD(myKD);

        // Create experiments and add them to CV object.
        for (Iterator iter = getExperimentsToAdd().iterator(); iter.hasNext();) {
            Experiment exp = ((ExperimentBean) iter.next()).getExperiment();
            intact.addExperiment(exp);
        }
        // Delete experiments and remove them from CV object.
        for (Iterator iter = getExperimentsToDel().iterator(); iter.hasNext();) {
            Experiment exp = ((ExperimentBean) iter.next()).getExperiment();
            intact.removeExperiment(exp);
        }

        // Delete proteins and remove it from the interaction.
        for (Iterator iter = myProteinsToDel.iterator(); iter.hasNext();) {
            Component comp = ((ProteinBean) iter.next()).getComponent();
            // No need to delete from persistent storage if the link to this
            // Protein is not persisted.
            if (comp.getAc() == null) {
                continue;
            }
            user.delete(comp);
            intact.removeComponent(comp);
        }
        // Update proteins.
        for (Iterator iter = getProteinsToUpdateIter(); iter.hasNext();) {
            ProteinBean pb = (ProteinBean) iter.next();
            pb.update(user);
            Component comp = pb.getComponent(user);
            intact.addComponent(comp);
            if (user.isPersistent(comp)) {
                user.update(comp);
            }
            else {
                user.create(comp);
            }
        }
    }

    // Override to provide Experiment layout.
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.interaction.layout");
    }

    // Override to provide Interaction help tag.
    public String getHelpTag() {
        return "editor.interaction";
    }

    // Null for any of these values will throw an exception.
    public void validate(EditUserI user) throws ValidationException,
            SearchException {
        super.validate(user);

        if (myInteractionType == null) {
            throw new InteractionException("error.int.validation.type");
        }
        if (myOrganism == null) {
            throw new InteractionException("error.int.validation.biosrc");
        }
        // Look for any unsaved or error proteins.
        for (Iterator iter = myProteins.iterator(); iter.hasNext();) {
            ProteinBean pb = (ProteinBean) iter.next();
            if (!pb.getEditState().equals(EditBean.VIEW)) {
                throw new InteractionException();
            }
        }
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
     * The interaction type menu list.
     * @return the interaction type menu consisting of interaction type short
     * labels. The first item in the menu may contain '---Select---' if the
     * current interaction type is not set.
     * @throws SearchException for errors in generating menus.
     */
    public List getInteractionTypeMenu() throws SearchException {
        int mode = (myInteractionType == null) ? 1 : 0;
        return getMenuFactory().getMenu(EditorMenuFactory.INTERACTION_TYPES, mode);
    }

    /**
     * Returns the selected interaction type. It is necessary to match the
     * current interaction to what is given in the drop down list. For example,
     * the match for current interaction type 'xyz' could be '...xyz'. If we don't
     * peform this mapping, the hightlighted menu always defaults to the first
     * item in the list.
     * @return the mapped menu item as it appears in the drop down list. If there
     * is no Interaction Type for this experiment (i.e, null) or the current interaction
     * is not found (highly unlikely), the selected menu defaults to the first
     * item in the list (doesn't mean that the first item is the selected one).
     * @throws SearchException for errors in constructing the menu.
     */
    public String getSelectedInterationType() throws SearchException {
        return getSelectedMenuItem(myInteractionType,
                EditorMenuFactory.INTERACTION_TYPES);
    }

    /**
     * Returns the normalized interaction. This is the opposite of
     * {@link #getSelectedInterationType()} method. Given an item from the drop
     * sown list, this method returns the normalized version of it. For example,
     * the match for current interaction '..xyz' this method returns 'xyz'.
     * @param item the menu item to normalize.
     * @return the normalized menu item as without menu level indicator
     * characters.
     * @throws SearchException for errors in constructing the menu.
     */
    public String getNormalizedInterationType(String item) throws SearchException {
        return getNormalizedMenuItem(item,
                EditorMenuFactory.INTERACTION_TYPES);
    }

    /**
     * Returns the edit menu for a Protein role.
     * @return the Protein role menu.
     * @throws SearchException for errors in constructing the menu.
     */
    public List getEditProteinRoleMenu() throws SearchException {
        return getMenuFactory().getMenu(EditorMenuFactory.ROLES, 0);
    }

    /**
     * Returns the add menu for a Protein role.
     * @return the Protein role menu.
     * @throws SearchException for errors in constructing the menu.
     */
    public List getAddProteinRoleMenu() throws SearchException {
        return getMenuFactory().getMenu(EditorMenuFactory.ROLES, 1);
    }

    public void setKD(Float kd) {
        myKD = kd;
    }

    public Float getKD() {
        return myKD;
    }

    public void setOrganism(String organism) {
        myOrganism = organism;
    }

    public String getOrganism() {
        return myOrganism;
    }

    public void setInteractionType(String interaction) {
        myInteractionType = interaction;
    }

    public String getInteractionType() {
        return myInteractionType;
    }

    /**
     * Adds an Experiment.
     * @param expbean the Experiment bean to add.
     *
     * <pre>
     * post: myExperimentsToAdd = myExperimentsToAdd@pre + 1
     * post: myExperiments = myExperiments@pre + 1
     * </pre>
     */
    public void addExperiment(ExperimentBean expbean) {
        // Experiment to add.
        myExperimentsToAdd.add(expbean);
        // Add to the view as well.
        myExperiments.add(expbean);
    }

    /**
     * True if given experiment exists in this object's experiment collection.
     * @param expbean the bean to compare.
     * @return true <code>expbean</code> exists in this object's experiment
     * collection. The comparision uses the equals method of
     * <code>ExperimentBean</code> class.
     *
     * <pre>
     * post: return->true implies myExperimentsToAdd.exists(exbean)
     * </pre>
     */
    public boolean experimentExists(ExperimentBean expbean) {
        return myExperiments.contains(expbean);
    }

    /**
     * Removes an Experiment
     * @param expbean the Experiment bean to remove.
     *
     * <pre>
     * post: myExperimentsToDel = myExperimentsToDel@pre - 1
     * post: myExperiments = myExperiments@pre - 1
     * </pre>
     */
    public void delExperiment(ExperimentBean expbean) {
        // Add to the container to delete experiments.
        myExperimentsToDel.add(expbean);
        // Remove from the view as well.
        myExperiments.remove(expbean);
    }

    /**
     * Adds an Experiment bean to hold.
     * @param exps a collection of <code>Experiment</code> to add.
     * <pre>
     * pre:  forall(obj : Object | obj.oclIsTypeOf(Experiment))
     * post: myExperimentsToHold = myExperimentsToHold@pre + 1
     * post: myExperimentsToHold = myExperimentsToHold@pre + exps->size
     * </pre>
     */
    public void addExperimentToHold(Collection exps) {
        for (Iterator iter = exps.iterator(); iter.hasNext();) {
            ExperimentBean expbean = new ExperimentBean((Experiment) iter.next());
            // Avoid duplicates.
            if (!myExperimentsToHold.contains(expbean)) {
                myExperimentsToHold.add(expbean);
            }
        }
    }

    /**
     * Hides an Experiment bean from hold.
     * @param expbean an <code>ExperimentBean</code> to hide.
     * <pre>
     * pre: myExperimentsToHold->includes(expbean)
     * post: myExperimentsToHold = myExperimentsToHold@pre - 1
     * </pre>
     */
    public void hideExperimentToHold(ExperimentBean expbean) {
        myExperimentsToHold.remove(expbean);
    }

    /**
     * Returns a collection of <code>ExperimentBean</code> objects.
     *
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(ExperimentBean))
     * </pre>
     */
    public List getExperiments() {
        return myExperiments;
    }

    /**
     * Returns a collection of <code>ExperimentBean</code> objects on hold.
     *
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(ExperimentBean))
     * </pre>
     */
    public List getHoldExperiments() {
        return myExperimentsToHold;
    }

    /**
     * Returns an <code>ExperimentBean</code> at given location.
     * @param index the position to return <code>ExperimentBean</code>.
     * @return <code>ExperimentBean</code> at <code>index</code>.
     *
     * <pre>
     * pre: index >=0 and index < myExperiments->size
     * post: return != null
     * post: return = myExperiments->at(index)
     * </pre>
     */
    public ExperimentBean getExperiment(int index) {
        return (ExperimentBean) myExperiments.get(index);
    }

    /**
     * Returns an <code>ExperimentBean</code> from a collection of
     * 'hold' experiments at given location.
     * @param index the position to return <code>ExperimentBean</code>.
     * @return <code>ExperimentBean</code> at <code>index</code> from 'hold'
     * (or experiment not yet added) collection.
     *
     * <pre>
     * pre: index >=0 and index < myExperimentsToHold->size
     * post: return != null
     * post: return = myExperimentsToHold->at(index)
     * </pre>
     */
    public ExperimentBean getHoldExperiment(int index) {
        return (ExperimentBean) myExperimentsToHold.get(index);
    }

    /**
     * Fills the given form with Experiment data.
     * @param form the form to fill with.
     */
    public void fillExperiments(EditForm form) {
        form.setItems(myExperiments);
    }

    /**
     * Fills the given form with 'hold' Experiments.
     * @param form the form to fill with.
     */
    public void fillHoldExperiments(EditForm form) {
        form.setItems(myExperimentsToHold);
    }

    /**
     * Adds an Protein.
     * @param protein the Protein to add.
     *
     * <pre>
     * post: myProteins = myProteins@pre + 1
     * </pre>
     */
    public void addProtein(Protein protein) {
        // Add to the view.
        myProteins.add(new ProteinBean(protein));
    }

    /**
     * Removes a Protein from given position.
     * @param pos the position in the current Protein collection.
     *
     * <pre>
     * post: myProteinsToDel = myProteinsToDel@pre + 1
     * post: myProteins = myProteins@pre - 1
     * </pre>
     */
    public void delProtein(int pos) {
        // Remove from the view as well; need the index because we need to
        // remove a specific bean (not just any bean which returns true for
        // equals method).
        ProteinBean pb = (ProteinBean) myProteins.remove(pos);
        // Add to the container to delete proteins.
        myProteinsToDel.add(pb);
    }

    /**
     * Adds a Protein bean to update.
     * @param pb a <code>ProteinBean</code> object to update.
     *
     * <pre>
     * post: myProteinsToUpdate = myProteinsToUpdate@pre + 1
     * post: myProteins = myProteins@pre
     * </pre>
     */
    public void addProteinToUpdate(ProteinBean pb) {
        myProteinsToUpdate.add(pb);
    }

    /**
     * True if given protein bean already exists among current saved proteins.
     * @param pb the bean to compare.
     * @return true if another 'saved' already exists.
     */
    public boolean hasDuplicates(ProteinBean pb) {
        for (Iterator iter = myProteins.iterator(); iter.hasNext();) {
            ProteinBean bean = (ProteinBean) iter.next();
            // Only consider committed proteins.
            if (!bean.getEditState().equals(ProteinBean.VIEW)) {
                continue;
            }
            if (bean.equals(pb)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a collection of <code>ProteinBean</code> objects.
     *
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(ProteinBean))
     * </pre>
     */
    public List getProteins() {
        return myProteins;
    }

    /**
     * Returns a <code>ProteinBean</code> at given location.
     * @param index the position to return <code>ProteinBean</code>.
     * @return <code>ProteinBean</code> at <code>index</code>.
     *
     * <pre>
     * pre: index >=0 and index < myProteins->size
     * post: return != null
     * post: return = myProteins->at(index)
     * </pre>
     */
    public ProteinBean getProtein(int index) {
        return (ProteinBean) myProteins.get(index);
    }

    /**
     * Fills the given form with Protein data.
     * @param form the form to fill with.
     */
    public void fillProteins(EditForm form) {
        form.setItems(myProteins);
    }

    // Override super to add extra.
    public void clearTransactions() {
        super.clearTransactions();

        // Clear experiments.
        myExperimentsToAdd.clear();
        myExperimentsToDel.clear();
        myExperimentsToHold.clear();

        // Clear Proteins
        myProteinsToDel.clear();
        myProteinsToUpdate.clear();
    }

    // Helper methods

    private void makeProteinBeans(Collection components) {
        myProteins.clear();
        for (Iterator iter = components.iterator(); iter.hasNext();) {
            Component comp = (Component) iter.next();
            myProteins.add(new ProteinBean(comp));
        }
    }

    private void makeExperimentBeans(Collection exps) {
        myExperiments.clear();
        for (Iterator iter = exps.iterator(); iter.hasNext();) {
            Experiment exp = (Experiment) iter.next();
            myExperiments.add(new ExperimentBean(exp));
        }
    }

    /**
     * Returns a collection of experiments to add.
     * @return the collection of experiments to add to the current Interaction.
     * Empty if there are no experiments to add.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(ExperimentBean)
     * </pre>
     */
    private Collection getExperimentsToAdd() {
        // Experiments common to both add and delete.
        Collection common = CollectionUtils.intersection(
                myExperimentsToAdd, myExperimentsToDel);
        // All the experiments only found in experiments to add collection.
        return CollectionUtils.subtract(myExperimentsToAdd, common);
    }

    /**
     * Returns a collection of experiments to remove.
     * @return the collection of experiments to remove from the current Interaction.
     * Could be empty if there are no experiments to delete.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(ExperimentBean)
     * </pre>
     */
    private Collection getExperimentsToDel() {
        // Experiments common to both add and delete.
        Collection common = CollectionUtils.intersection(
                myExperimentsToAdd, myExperimentsToDel);
        // All the experiments only found in experiments to delete collection.
        return CollectionUtils.subtract(myExperimentsToDel, common);
    }

    /**
     * Returns an Iterator of proteins to update. This excludes any Proteins
     * already marked for deletions.
     * @return the collection of proteins to update for the current Interaction.
     * Could be empty if there are no proteins to update.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(ProteinBean)
     * </pre>
     */
    private Iterator getProteinsToUpdateIter() {
        // Holds Proteins to update.
        Collection result = new ArrayList();
        // Remove any proteins marked for deletions.
        for (Iterator iter = myProteinsToUpdate.iterator(); iter.hasNext();) {
            ProteinBean pb = (ProteinBean) iter.next();
//            System.out.println("Going though with " + pb.getShortLabel());
            if (pb.isMarkedForDelete()) {
                continue;
            }
//            System.out.println("Adding to the list to update: " + pb.getShortLabel());
            result.add(pb);
        }
        return result.iterator();
    }
}
