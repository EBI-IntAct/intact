/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.struts.tiles.ComponentContext;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.exception.validation.InteractionException;
import uk.ac.ebi.intact.application.editor.exception.validation.ValidationException;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureBean;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;

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
     * The collection of Components. Transient as it is only valid for the
     * current display.
     */
    private transient List myComponents = new ArrayList();

    /**
     * Holds Components to del. This collection is cleared once the user
     * commits the transaction.
     */
    private transient List myComponentsToDel = new ArrayList();

    /**
     * Holds Components to update. This collection is cleared once the user
     * commits the transaction.
     */
    private transient Set myComponentsToUpdate = new HashSet();

    /**
     * This holds the AC of the experiment from this bean is construected.
     */
    private String mySourceExperimentAc;

    /**
     * Keeps a track of features to link
     */
    private Set myLinkFeatures = new HashSet();

    /**
     * Keeps a track of features to unlink
     */
    private Set myUnlinkFeatures = new HashSet();

    // Override the super method to initialize this class specific resetting.
    protected void reset(Class clazz) {
        super.reset(clazz);
        // Set fields to null.
        setKD(Float.valueOf("1.0"));
        setOrganism(null);
        setInteractionType(null);
        setSourceExperimentAc(null);
    }

    protected void reset(AnnotatedObject annobj) {
        super.reset(annobj);

        // Must be an Interaction; can cast it safely.
        Interaction intact = (Interaction) annobj;

        // Reset the current interaction with the argument interaction.
        resetInteraction(intact);

        // Prepare for Proteins and Experiments for display.
        makeExperimentBeans(intact.getExperiments());
        makeProteinBeans(intact.getComponents());
    }


    // Reset the fields to null if we don't have values to set. Failure
    // to do so will display the previous edit object's values as current.
    public void resetClonedObject(AnnotatedObject copy) {
        super.resetClonedObject(copy);

        Interaction interaction = (Interaction) copy;

        // Reset the interaction view.
        resetInteraction(interaction);

        // Add cloned proteins as new proteins.
        for (Iterator iter = interaction.getComponents().iterator(); iter.hasNext();) {
            ComponentBean cb = new ComponentBean((Component) iter.next());
            myComponents.add(cb);
            addProteinToUpdate(cb);
        }
    }

    // Implements abstract methods

    protected void updateAnnotatedObject(EditUserI user) throws SearchException {
        // The cv interaction type for the interaction.
        CvInteractionType type = (CvInteractionType) user.getObjectByLabel(
                CvInteractionType.class, myInteractionType);

        // The current Interaction.
        Interaction intact = (Interaction) getAnnotatedObject();

        // Have we set the annotated object for the view?
        if (intact == null) {
            // Collect experiments from beans.
            List exps = new ArrayList();
            for (Iterator iter = getExperimentsToAdd().iterator(); iter.hasNext();) {
                exps.add(((ExperimentBean) iter.next()).getExperiment());
            }
            // Not persisted. Create a new Interaction.
            intact = new InteractionImpl(exps, new ArrayList(),
                                         type, getShortLabel(), user.getInstitution());
            // Set this interaction as the annotated object.
            setAnnotatedObject(intact);
        }
        else {
            // Update the existing interaction.
            intact.setCvInteractionType(type);
        }
        // Get the objects using their short label.
        BioSource biosource = (BioSource) user.getObjectByLabel(
                BioSource.class, myOrganism);
        intact.setBioSource(biosource);
        intact.setKD(myKD);

        // Delete experiments.
        for (Iterator iter = getExperimentsToDel().iterator(); iter.hasNext();) {
            Experiment exp = ((ExperimentBean) iter.next()).getExperiment();
            intact.removeExperiment(exp);
        }
    }

    // Override the super to persist others.
    public void persistOthers(EditUserI user) throws IntactException,
            SearchException {
        try {
            // Begin the transaction.
            user.begin();

            // persist the view.
            persistCurrentView(user);

            // Commit the transaction.
            user.commit();
        }
        catch (IntactException ie1) {
            ie1.printStackTrace();
            try {
                user.rollback();
            }
            catch (IntactException ie2) {
                // Oops! Problems with rollback; ignore this as this
                // error is reported via the main exception (ie1).
            }
            // Rethrow the exception to be logged.
            throw ie1;
        }
    }

    // Override the super method as the current interaction is added to the
    // recent interaction list.
    public void addToRecentList(EditUserI user) {
        user.addToCurrentInteraction((Interaction) getAnnotatedObject());
    }

    // Override to remove the current interaction from the recent list.
    public void removeFromRecentList(EditUserI user) {
        user.removeFromCurrentInteraction((Interaction) getAnnotatedObject());
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

    // Override to copy data from the form.
    public void copyPropertiesFrom(EditorActionForm editorForm) {
        // Set the common values by calling super first.
        super.copyPropertiesFrom(editorForm);

        // Cast to the interaction to get interaction specific data.
        InteractionActionForm intform = (InteractionActionForm) editorForm;
        setInteractionType(intform.getInteractionType());
        setOrganism(intform.getOrganism());
        setKD(intform.getKd());
    }

    // Override to copy Interaction data.
    public void copyPropertiesTo(EditorActionForm form) {
        super.copyPropertiesTo(form);

        // Cast to the interaction form to copy interaction data.
        InteractionActionForm intform = (InteractionActionForm) form;

        intform.setInteractionType(getInteractionType());
        intform.setOrganism(getOrganism());
        intform.setKd(getKD());

        intform.setExperiments(getExperiments());
        intform.setExpsOnHold(getHoldExperiments());
        intform.setComponents(getComponents());
    }

    public void sanityCheck(EditUserI user) throws ValidationException,
            SearchException {
        // Look for any unsaved or error proteins.
        for (Iterator iter = myComponents.iterator(); iter.hasNext();) {
            ComponentBean pb = (ComponentBean) iter.next();
            if (!pb.getEditState().equals(AbstractEditBean.VIEW)) {
                throw new InteractionException("int.unsaved.prot",
                        "error.int.sanity.unsaved.prot");
            }
        }
        // Any missing experiments (check 7).
        if (myExperiments.isEmpty()) {
            throw new InteractionException("int.sanity.exp",
                    "error.int.sanity.exp");
        }
    }

    // TODO Remove this later
    // Null for any of these values will throw an exception.
//    public void validate(EditUserI user) throws ValidationException,
//            SearchException {
//        super.validate(user);
//
//        if (myInteractionType == null) {
//            throw new InteractionException("error.int.validation.type");
//        }
//        if (myOrganism == null) {
//            throw new InteractionException("error.int.validation.biosrc");
//        }
//        // Look for any unsaved or error proteins.
//        for (Iterator iter = myComponents.iterator(); iter.hasNext();) {
//            ComponentBean pb = (ComponentBean) iter.next();
//            if (!pb.getEditState().equals(AbstractEditBean.VIEW)) {
//                throw new InteractionException();
//            }
//        }
//    }

    /**
     * The organism menu list.
     * @return the organism menu consisting of organism short labels. The first
     * item in the menu may contain '---Select---' if the current organism is
     * not set.
     * @throws SearchException for errors in generating menus.
     */
    public List getOrganismMenu() throws SearchException {
        int mode = (myOrganism == null) ? 1 : 0;
        return getMenuFactory().getMenu(EditorMenuFactory.ORGANISM, mode);
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
        return getMenuFactory().getDagMenu(EditorMenuFactory.INTERACTION_TYPE, mode);
    }

    /**
     * Returns the edit menu for a Protein role.
     * @return the Protein role menu.
     * @throws SearchException for errors in constructing the menu.
     */
    public List getEditProteinRoleMenu() throws SearchException {
        return getMenuFactory().getMenu(EditorMenuFactory.ROLE, 0);
    }

    /**
     * Returns the add menu for a Protein role.
     * @return the Protein role menu.
     * @throws SearchException for errors in constructing the menu.
     */
    public List getAddProteinRoleMenu() throws SearchException {
        return getMenuFactory().getMenu(EditorMenuFactory.ROLE, 1);
    }

    public List getAddBioSourceMenu() throws SearchException {
        return getMenuFactory().getMenu(EditorMenuFactory.ORGANISM, 1);
    }

    public void setKD(Float kd) {
        myKD = kd;
    }

    public Float getKD() {
        return myKD;
    }

    public void setOrganism(String organism) {
        myOrganism = EditorMenuFactory.normalizeMenuItem(organism);
    }

    public String getOrganism() {
        return myOrganism;
    }

    public void setInteractionType(String interaction) {
        myInteractionType = EditorMenuFactory.normalizeMenuItem(interaction);
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
     * post: myComponents = myComponents@pre + 1
     * </pre>
     */
    public void addProtein(Protein protein) {
        // Add to the view.
        myComponents.add(new ComponentBean(protein));
    }

    /**
     * Removes a Protein from given position.
     * @param pos the position in the current Protein collection.
     *
     * <pre>
     * post: myComponentsToDel = myComponentsToDel@pre + 1
     * post: myComponents = myComponents@pre - 1
     * </pre>
     */
    public void delProtein(int pos) {
        // Remove from the view as well; need the index because we need to
        // remove a specific bean (not just any bean which returns true for
        // equals method).
        ComponentBean cb = (ComponentBean) myComponents.remove(pos);
        // Add to the container to delete proteins.
        myComponentsToDel.add(cb);
        // Remove from the update list if it has already been added.
        myComponentsToUpdate.remove(cb);
    }

    /**
     * Adds a Component bean to update.
     * @param cb a <code>ComponentBean</code> object to update.
     *
     * <pre>
     * post: myComponentsToUpdate = myComponentsToUpdate@pre + 1
     * post: myComponents = myComponents@pre
     * </pre>
     */
    public void addProteinToUpdate(ComponentBean cb) {
        myComponentsToUpdate.add(cb);

        // Need to update the update component bean collection for it to
        // persist properly.
        for (Iterator iterator = myComponentsToUpdate.iterator(); iterator.hasNext();) {
            ComponentBean bean = (ComponentBean) iterator.next();
            System.out.println("Found in the update components: " + bean.getAc());
//            if (bean.getAc() != null && bean.getAc().equals(comp.getAc())) {
//                int idx = myComponentsToUpdate.indexOf(comp);
//                if (idx != -1) {
//                    // Remove the existing component to update.
//                    myComponentsToUpdate.remove(idx);
//                    // Add
//                }
//            }
        }
    }

    /**
     * Removes all the unsaved proteins for the current protein collection. A
     * protein bean whose state equivalent to {@link ComponentBean.SAVE_NEW} is
     * considered as unsaved.
     */
    public void removeUnsavedProteins() {
        CollectionUtils.filter(myComponents, ProteinBeanPredicate.ourInstance);
    }

    /**
     * Returns a collection of <code>ComponentBean</code> objects.
     *
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(ComponentBean))
     * </pre>
     */
    public List getComponents() {
        return myComponents;
    }

    // Override super to add extra.
    public void clearTransactions() {
        super.clearTransactions();

        // Clear experiments.
        myExperiments.clear();
        myExperimentsToAdd.clear();
        myExperimentsToDel.clear();
        myExperimentsToHold.clear();

        // Clear proteins.
        myComponents.clear();
        myComponentsToDel.clear();
        myComponentsToUpdate.clear();
    }

    /**
     * Returns the AC of the source experiment.
     * @return the AC of the source experiment if it is set; othewise
     * null is returned.
     */
    public String getSourceExperimentAc() {
        return mySourceExperimentAc;
    }

    /**
     * Set the AC of the source experiment.
     * @param ac the AC of the source experiment.
     */
    public void setSourceExperimentAc(String ac) {
        mySourceExperimentAc = ac;
    }

    /**
     * True when this bean is constructed from an experiment.
     * @return true if this bean is constructed from an experiment. For all
     * otheer instances, false is returned.
     */
    public boolean isSourceFromAnExperiment() {
        return mySourceExperimentAc != null;
    }

    /**
     * Returns the state for this editor to clone.
     * @return true for all the persistent interactions (i.e., false for a
     * new interaction not yet persisted).
     */
    public boolean getCloneState() {
        return getAc() != null;
    }

    /**
     * Updates the Feature bean within a Component. If this is a new Feature,
     * it will add as a bean to the appropriate component. Any existing matching
     * bean is removed before adding the updated feature (at the same location).
     * @param feature the feature to update.
     */
    public void saveFeature(Feature feature) {
        // The component bean the feature belongs to.
        ComponentBean comp = null;

        // The ac to match to retrieve the component.
        String compAc = feature.getComponent().getAc();

        // Find the component bean this feature bean belongs to.
        for (Iterator iterator = myComponents.iterator(); iterator.hasNext();) {
            ComponentBean cb = (ComponentBean) iterator.next();
            if (cb.getAc().equals(compAc)) {
                comp = cb;
                break;
            }
        }
        // We should have this component.
        assert comp != null;

        comp.saveFeature(feature);

        // Update this component for it to persist correctly.
        addProteinToUpdate(comp);
    }

    public void deleteFeature(Feature feature) {
        // The component bean the feature belongs to.
        ComponentBean comp = null;

        // The ac to match to retrieve the component.
        String compAc = feature.getComponent().getAc();

        // Find the component bean this feature bean belongs to.
        for (Iterator iterator = myComponents.iterator(); iterator.hasNext();) {
            ComponentBean cb = (ComponentBean) iterator.next();
            if (cb.getAc().equals(compAc)) {
                comp = cb;
                break;
            }
        }
        // We should have this component.
        assert comp != null;

        // Remove from the component beans.
        comp.delFeature(feature);

        // Update this component for it to persist correctly.
        addProteinToUpdate(comp);
    }

    /**
     * Adds to the Set that maintains which Features to be linked.
     * @param fb1 the Feature bean to add the link to. This bean
     * replaces any previous similar feature (i.e, no duplicates).
     * @param fb2 other Feature bean to link.
     */
    public void addFeatureLink(FeatureBean fb1, FeatureBean fb2) {
        // Update the screen beans.
        fb1.setBoundDomain(fb2.getShortLabel());
        fb2.setBoundDomain(fb1.getShortLabel());

        // Make sure to set the linked beans to false (or else they will
        // display as checked).
        fb1.setLinked(false);
        fb2.setLinked(false);

        // We only add one bean because its bound domain has the short label
        // of the other bean (ie., short label of fb2).
        myLinkFeatures.add(fb1);
    }

    /**
     * Adds to the Set that maintains which Features to be unlinked.
     * @param fb the Feature bean to remove the link. This bean
     * replaces any previous similar feature (i.e, no duplicates).
     * @throws SearchException for errors in accessing Feature instance
     * wrapped around the bean.
     */
    public void addFeatureToUnlink(FeatureBean fb, EditUserI user)
            throws SearchException {
        // The Features to unlink. Need to access the updated feature.
        // This is important when a feature is linked but not yet persisted.
        Feature srcFeature = fb.getFeature(user);
        Feature toFeature = srcFeature.getBoundDomain();

        // The destination feature as a bean.
        FeatureBean toFb = getFeatureBean(toFeature);
        // This bean must exist.
        assert toFb != null;

        // Update the screen beans.
        fb.setBoundDomain("");
        toFb.setBoundDomain("");

        // Make sure to set the linked beans to false (or else they will
        // display as checked).
        fb.setLinked(false);
        toFb.setLinked(false);

        // Add to the set to update the database later.
        myUnlinkFeatures.add(fb);
        myUnlinkFeatures.add(toFb);
    }

    // Helper methods

    private void makeProteinBeans(Collection components) {
        myComponents.clear();
        for (Iterator iter = components.iterator(); iter.hasNext();) {
            Component comp = (Component) iter.next();
            myComponents.add(new ComponentBean(comp));
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

    private void persistCurrentView(EditUserI user) throws IntactException,
            SearchException {
        // The current Interaction.
        Interaction intact = (Interaction) getAnnotatedObject();

        // Add experiments here. Make sure this is done after persisting the
        // Interaction first. - IMPORTANT. don't change the order.
        for (Iterator iter = getExperimentsToAdd().iterator(); iter.hasNext();) {
            Experiment exp = ((ExperimentBean) iter.next()).getExperiment();
            intact.addExperiment(exp);
        }

        // Delete components and remove it from the interaction.
        for (Iterator iter = myComponentsToDel.iterator(); iter.hasNext();) {
            Component comp = ((ComponentBean) iter.next()).getComponent(user);
            // No need to delete from persistent storage if the link to this
            // Protein is not persisted.
            if ((comp == null) || (comp.getAc() == null)) {
                continue;
            }
            user.delete(comp);
            intact.removeComponent(comp);
        }

        // Update components.
        for (Iterator iter1 = myComponentsToUpdate.iterator(); iter1.hasNext();) {
            ComponentBean cb = (ComponentBean) iter1.next();
            cb.setInteraction((Interaction) getAnnotatedObject());
            Component comp = cb.getComponent(user);
            // Delete features if there are any features to delete.
            for (Iterator iter2 = cb.getFeaturesToDelete().iterator(); iter2.hasNext();) {
                Feature feature = ((FeatureBean) iter2.next()).getFeature(user);
                // Remove from the component.
                comp.removeBindingDomain(feature);

                // Remove any links if this feature is linked to another feature.
                if (feature.getBoundDomain() != null) {
                    Feature toFeature = feature.getBoundDomain();
                    // Disconnect the links between two features.
                    System.out.println("Disconnecting bound domains");
                    feature.setBoundDomain(null);
                    toFeature.setBoundDomain(null);
                }
                user.delete(feature);
            }
            // Add features
            for (Iterator iter2 = cb.getFeaturesToAdd().iterator(); iter2.hasNext();) {
                Feature feature = ((FeatureBean) iter2.next()).getFeature(user);
                // Add to the component.
                comp.addBindingDomain(feature);
                // No need to create the Feature because it has already been persisted.
            }
            intact.addComponent(comp);
            if (user.isPersistent(comp)) {
                user.update(comp);
            }
            else {
                user.create(comp);
            }
        }
        // Keeps a track of Features to update. This avoids many updates to the
        // same feature.
        Set featuresToUpdate = new HashSet();

        // Update any features to link.
        for (Iterator iter = myLinkFeatures.iterator(); iter.hasNext();) {
            // The feature bean to link.
            FeatureBean fb = (FeatureBean) iter.next();

            // The bound domain can be empty if this link was later unlinked
            // before it was persisted.
            if (fb.getBoundDomain().length() == 0) {
                // Already unliked, carry on with the next.
                continue;
            }

            // The Feature objets to link together. Use 'user' to get changes
            // to bound domain.
            Feature srcFeature = fb.getFeature(user);
            Feature toFeature = (Feature) user.getObjectByLabel(
                    Feature.class, fb.getBoundDomain());

            // Sets the links.
            srcFeature.setBoundDomain(toFeature);
            toFeature.setBoundDomain(srcFeature);

            // Update features.
            featuresToUpdate.add(srcFeature);
            featuresToUpdate.add(toFeature);
//            user.update(srcFeature);
//            user.update(toFeature);
        }

        // Update any features to unlink.
        for (Iterator iter = myUnlinkFeatures.iterator(); iter.hasNext();) {
            // The Feature to unlink.
            Feature feature = ((FeatureBean) iter.next()).getFeature();

            // Set the bound domian to null.
            feature.setBoundDomain(null);

            // Update features.
            featuresToUpdate.add(feature);
//            user.update(feature);
        }
        // Do the real updates to the features which require update.
        for (Iterator iter = featuresToUpdate.iterator(); iter.hasNext();) {
            Feature feature = (Feature) iter.next();
            System.out.println("Updating feature: " + feature.getAc());
            user.update(feature);
//            user.update(iter.next());
        }
        // No need to test whether this 'intact' persistent or not because we
        // know it has been already persisted by persist() call.
        user.update(intact);
    }

    /**
     * Resets this interaction with give interaction object.
     * @param interaction the Interaction to set the current interaction.
     */
    private void resetInteraction(Interaction interaction) {
        setKD(interaction.getKD());

        // Only set the short labels if the interaction has non null values.
        BioSource biosrc = interaction.getBioSource();
        setOrganism(biosrc != null ? biosrc.getShortLabel() : null);

        CvInteractionType inter = interaction.getCvInteractionType();
        setInteractionType(inter != null
                ? interaction.getCvInteractionType().getShortLabel() : null);

        // Set the source experiment to null to indicate that this bean
        // is not constructed within an experiment.
        setSourceExperimentAc(null);
    }

    /**
     * Returns the Feature bean for given Feature.
     * @param feature the Feature to get the matching bean.
     * @return the matching Feature bean for <code>feature</code> or null
     * if none found.
     */
    private FeatureBean getFeatureBean(Feature feature) {
        // Wrap it around a bean, so we can do the search containers.
        FeatureBean fb = new FeatureBean(feature);

        // Look in the componets.
        for (Iterator iter = myComponents.iterator(); iter.hasNext();) {
            List features = ((ComponentBean) iter.next()).getFeatures();
            if (features.contains(fb)) {
                return (FeatureBean) features.get(features.indexOf(fb));
            }
        }
        // Not found the bean, return null.
        return null;
    }

    // Static Inner Class -----------------------------------------------------

    private static class ProteinBeanPredicate implements Predicate {

        private static ProteinBeanPredicate ourInstance = new ProteinBeanPredicate();

        public boolean evaluate(Object object) {
            return !((ComponentBean) object).getEditState().equals(
                    ComponentBean.SAVE_NEW);
        }
    }

    // End of Inner class -----------------------------------------------------

    // Sanity checking routines
}
