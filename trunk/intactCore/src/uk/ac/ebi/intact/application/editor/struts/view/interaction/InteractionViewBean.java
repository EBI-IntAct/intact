/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.tiles.ComponentContext;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.exception.validation.InteractionException;
import uk.ac.ebi.intact.application.editor.exception.validation.ValidationException;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
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
        makeExperimentBeans(intact.getExperiments());
        makeProteinBeans(intact.getComponents());
    }

    // Override the super method to this bean's info.
    public void persist(EditUserI user) throws IntactException, SearchException {
        // Get the objects using their short label.
        BioSource biosource = (BioSource) user.getObjectByLabel(
                BioSource.class, myOrganism);
        CvInteractionType type = (CvInteractionType) user.getObjectByLabel(
                CvInteractionType.class, myInteractionType);

        Interaction intact = (Interaction) getAnnotatedObject();
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
        super.persist(user);
    }

    /**
     * Persists proteins.
     * @param user handler to the user to persist proteins.
     * @throws IntactException for errors in persisting.
     * @throws SearchException for errors in searching for objects in the
     * persistent system.
     */
    public void persistProteins(EditUserI user) throws IntactException, SearchException {
        Interaction intact = (Interaction) getAnnotatedObject();
        // Delete proteins and remove it from the interaction.
        for (Iterator iter = myProteinsToDel.iterator(); iter.hasNext();) {
            Component comp = ((ProteinBean) iter.next()).getComponent(user);
            // No need to delete from persistent storage if the link to this
            // Protein is not persisted.
            if ((comp == null) || (comp.getAc() == null)) {
                continue;
            }
            user.delete(comp);
            intact.removeComponent(comp);
        }
        // Update proteins.
        for (Iterator iter = myProteinsToUpdate.iterator(); iter.hasNext();) {
            ProteinBean pb = (ProteinBean) iter.next();
            Component comp = pb.getComponent(user);
            intact.addComponent(comp);
            if (user.isPersistent(comp)) {
                user.update(comp);
            }
            else {
                user.create(comp);
            }
        }
        // No need to test whether this 'intact' persistent or not because we
        // know it has been already persisted by persist() call.
        user.update(intact);
    }

    // Override super method to clear experiments and componets.
    public void clear() {
        super.clear();

        Interaction intact = (Interaction) getAnnotatedObject();
        // Experiments involved in this interaction.
        intact.getExperiments().clear();
        // Components.
        intact.getComponents().clear();
    }

    // Override to provide Experiment layout.
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.int.layout");
    }

    // Override to provide Interaction help tag.
    public String getHelpTag() {
        return "editor.interaction";
    }

    // Override to provide set experiment from the bean.
    public void updateFromForm(DynaActionForm dynaform) {
        // Set the common values by calling super first.
        super.updateFromForm(dynaform);

        String inttype = (String) dynaform.get("interactionType");
        if (!EditorMenuFactory.SELECT_LIST_ITEM.equals(inttype)) {
            setInteractionType(inttype);
        }
        String organism = (String) dynaform.get("organism");
        if (!EditorMenuFactory.SELECT_LIST_ITEM.equals(organism)) {
            // Set the view bean with the new values.
            setOrganism(organism);
        }
        setKD((Float) dynaform.get("kD"));
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
            if (!pb.getEditState().equals(AbstractEditBean.VIEW)) {
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
        return getMenuFactory().getDagMenu(EditorMenuFactory.INTERACTION_TYPES, mode);
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
     * Adds an Experiment bean to hold if the new experiment doesn't
     * already exists in the experiment hold collection and in the
     * current experiment collection for this interaction.
     * @param exps a collection of <code>Experiment</code> to add.
     *
     * <pre>
     * pre:  forall(obj : Object | obj.oclIsTypeOf(Experiment))
     * </pre>
     */
    public void addExperimentToHold(Collection exps) {
        for (Iterator iter = exps.iterator(); iter.hasNext();) {
            ExperimentBean expbean = new ExperimentBean((Experiment) iter.next());
            // Avoid duplicates.
            if (!myExperimentsToHold.contains(expbean)
                    && !myExperiments.contains(expbean)) {
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
     * Clears all the experiments on hold.
     */
    public void clearExperimentToHold() {
        myExperimentsToHold.clear();
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
     * Adds an Protein.
     * @param protein the Protein to add.
     *
     * <pre>
     * post: myProteins = myProteins@pre + 1
     * </pre>
     */
    public void addProtein(Protein protein) {
        // Add to the view.
        myProteins.add(new ProteinBean(protein, (Interaction) getAnnotatedObject()));
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
        // Remove from the update list if it has already been added.
        myProteinsToUpdate.remove(pb);
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

    // Override super to add extra.
    public void clearTransactions() {
        super.clearTransactions();

        // Clear experiments.
        myExperimentsToAdd.clear();
        myExperimentsToDel.clear();
        myExperimentsToHold.clear();

        // Clear proteins.
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
}
