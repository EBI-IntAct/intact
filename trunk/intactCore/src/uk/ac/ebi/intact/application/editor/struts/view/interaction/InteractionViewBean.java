/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.struts.view.EditForm;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.validation.ValidationException;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactException;
import org.apache.struts.tiles.ComponentContext;

import java.util.*;

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
     * Holds Proteins to add. This collection is cleared once the user
     * commits the transaction.
     */
    private transient List myProteinsToAdd = new ArrayList();

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


    // Override the super method to set the tax id.
    public void setAnnotatedObject(Interaction intact) {
        super.setAnnotatedObject(intact);
        myKD = intact.getKD();
        // Only set the short labels if the interaction has non null values.
        BioSource biosrc = intact.getBioSource();
        if (biosrc != null) {
            setOrganism(biosrc.getShortLabel());
        }
        CvInteractionType inter = intact.getCvInteractionType();
        if (inter != null) {
            setInteractionType(intact.getCvInteractionType().getShortLabel());
        }
        // Clear any left overs from previous transaction.
        clearTransactions();

        // Prepare for Proteins and Experiments for display.
        makeExperimentBeans(intact.getExperiment());
        makeProteinBeans(intact.getComponent());
    }

    // Override the super method to this bean's info.
    public void persist(EditUserI user) throws IntactException, SearchException {
        // The order is important! update super last as it does
        // the update of the object.
        Interaction intact = (Interaction) getAnnotatedObject();

        // Get the objects using their short label.
        BioSource biosource = (BioSource) user.getObjectByLabel(
                BioSource.class, myOrganism);
        CvInteractionType type = (CvInteractionType) user.getObjectByLabel(
                CvInteractionType.class, myInteractionType);

        intact.setBioSource(biosource);
        intact.setBioSourceAc(biosource.getAc());
        intact.setCvInteractionType(type);
        intact.setCvInteractionTypeAc(type.getAc());

        // Need to update Proteins here.

        super.persist(user);
    }

    // Ovverride to provide Experiment layout.
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.interaction.layout");
    }

    // Null for any of these values will throw an exception.
    public void validate() throws ValidationException {
        if ((myOrganism == null) || (myInteractionType == null)) {
//            throw new ExperimentException();
        }
    }

    // Override to provde menus needed for this editor.
    public Map getEditorMenus() throws SearchException {
        // The object we are editing at the moment.
        Interaction exp = (Interaction) getAnnotatedObject();
        Map map = (exp.getBioSource() == null)
                ? getMenuFactory().getInteractionMenus(1)
                : getMenuFactory().getInteractionMenus(0);
        return map;
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
        return getMenuFactory().getMenu(EditorMenuFactory.ROLES, 0);
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
        for (Iterator iter = exps.iterator(); iter.hasNext(); ) {
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
     * post: myProteinsToAdd = myProteinsToAdd@pre + 1
     * post: myProteins = myProteins@pre + 1
     * </pre>
     */
    public void addProtein(Protein protein) {
        ProteinBean pb = new ProteinBean(protein);
        // Protein to add.
        myProteinsToAdd.add(pb);
        // Add to the view as well.
        myProteins.add(pb);
    }

    /**
     * Removes a Protein.
     * @param pb the Protein bean to remove.
     *
     * <pre>
     * post: myProteinsToDel = myProteinsToDel@pre - 1
     * post: myProteins = myProteins@pre - 1
     * </pre>
     */
    public void delProtein(ProteinBean pb) {
        // Add to the container to delete proteins.
        myProteinsToDel.add(pb);
        // Remove from the view as well.
        myProteins.remove(pb);
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
        myProteinsToAdd.clear();
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
}
