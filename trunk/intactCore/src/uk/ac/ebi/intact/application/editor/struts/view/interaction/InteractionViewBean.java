/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rigits reserved. Please see the file LICENSE
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
    private List myLinkFeatures = new ArrayList();

    /**
     * Keeps a track of features to unlink
     */
    private List myUnlinkFeatures = new ArrayList();

    /**
     * Handler to sort link features.
     */
    private transient ItemLinkSorter myFeatureSorter;

    // Override the super method to initialize this class specific resetting.
    protected void reset(Class clazz) {
        super.reset(clazz);
        // Set fields to null.
        setKD(Float.valueOf("1.0"));
        setOrganism(null);
        setInteractionType(null);
        setSourceExperimentAc(null);

        // Reset components and experiments
        myExperiments.clear();
        myComponents.clear();
    }

    protected void reset(AnnotatedObject annobj) {
        super.reset(annobj);

        // Must be an Interaction; can cast it safely.
        Interaction intact = (Interaction) annobj;

        // Reset the current interaction with the argument interaction.
        resetInteraction(intact);

        // Set the source experiment to null to indicate that this bean
        // is not constructed within an experiment.
        setSourceExperimentAc(null);

        // Prepare for Proteins and Experiments for display.
        makeExperimentBeans(intact.getExperiments());
        makeProteinBeans(intact.getComponents());
    }

    // Reset the fields to null if we don't have values to set. Failure
    // to do so will display the previous edit object's values as current.
    public void resetClonedObject(AnnotatedObject copy) {
        super.resetClonedObject(copy);

        Interaction interaction = (Interaction) copy;

        // Clear existing exps and comps.
        myExperiments.clear();
        myComponents.clear();
        
        // Reset the interaction view.
        resetInteraction(interaction);

        // Add cloned proteins as new proteins.
        for (Iterator iter = interaction.getComponents().iterator(); iter.hasNext();) {
            ComponentBean cb = new ComponentBean();
            cb.setFromClonedObject((Component) iter.next());
            // Add to the view.
            myComponents.add(cb);
            // The componen needs to be updated as well.
            myComponentsToUpdate.add(cb);
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
        BioSource biosource = (BioSource) user.getObjectByLabel(BioSource.class, myOrganism);
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
        // First transaction for
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
        // Need another transaction to delete features.
        try {
            // Begin the transaction.
            user.begin();

            // persist the view in a second transaction
            persistCurrentView2(user);

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

        // Look for any unsaved or error features.
        if (hasErrorFeatures()) {
            throw new InteractionException("int.error.feature",
                    "error.int.sanity.error.feature");
        }

        // Any missing experiments (check 7).
        if (myExperiments.isEmpty()) {
            throw new InteractionException("int.sanity.exp",
                    "error.int.sanity.exp");
        }

        // Check to see that a cloned feature already exists.
        if (hasDuplicateFeatures(user)) {
            markDuplicateFeatures(user);
            // The errors to display.
            throw new InteractionException("int.duplicate.feature",
                    "error.int.sanity.duplicate.feature");
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
     *
     * @return the organism menu consisting of organism short labels. The first
     *         item in the menu may contain '---Select---' if the current organism is
     *         not set.
     * @throws SearchException for errors in generating menus.
     */
    public List getOrganismMenu() throws SearchException {
        int mode = (myOrganism == null) ? 1 : 0;
        return getMenuFactory().getMenu(EditorMenuFactory.ORGANISM, mode);
    }

    /**
     * The interaction type menu list.
     *
     * @return the interaction type menu consisting of interaction type short
     *         labels. The first item in the menu may contain '---Select---' if the
     *         current interaction type is not set.
     * @throws SearchException for errors in generating menus.
     */
    public List getInteractionTypeMenu() throws SearchException {
        int mode = (myInteractionType == null) ? 1 : 0;
        return getMenuFactory().getDagMenu(EditorMenuFactory.INTERACTION_TYPE, mode);
    }

    /**
     * Returns the edit menu for a Protein role.
     *
     * @return the Protein role menu.
     * @throws SearchException for errors in constructing the menu.
     */
    public List getEditProteinRoleMenu() throws SearchException {
        return getMenuFactory().getMenu(EditorMenuFactory.ROLE, 0);
    }

    /**
     * Returns the add menu for a Protein role.
     *
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
     *
     * @param expbean the Experiment bean to add.
     * <p/>
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
     *
     * @param expbean the bean to compare.
     * @return true <code>expbean</code> exists in this object's experiment
     * collection. The comparision uses the equals method of
     * <code>ExperimentBean</code> class.
     * <p/>
     * <pre>
     * post: return->true implies myExperimentsToAdd.exists(exbean)
     * </pre>
     */
    public boolean experimentExists(ExperimentBean expbean) {
        return myExperiments.contains(expbean);
    }

    /**
     * Removes an Experiment
     *
     * @param expbean the Experiment bean to remove.
     * <p/>
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
     *
     * @param exps a collection of <code>Experiment</code> to add.
     * <p/>
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
     *
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
     * <p/>
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
     * <p/>
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
     *
     * @param index the position to return <code>ExperimentBean</code>.
     * @return <code>ExperimentBean</code> at <code>index</code>.
     * <p/>
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
     *
     * @param index the position to return <code>ExperimentBean</code>.
     * @return <code>ExperimentBean</code> at <code>index</code> from 'hold'
     * (or experiment not yet added) collection.
     * <p/>
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
     *
     * @param protein the Protein to add.
     * <p/>
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
     *
     * @param pos the position in the current Protein collection.
     * <p/>
     * <pre>
     * post: myComponentsToDel = myComponentsToDel@pre + 1
     * post: myComponents = myComponents@pre - 1
     * </pre>
     */
    public void delProtein(int pos) {
        // The component bean at position 'pos'.
        ComponentBean cb = (ComponentBean) myComponents.get(pos);

        // Avoid creating an empty list if the comp has no features.
        if (!cb.getFeatures().isEmpty()) {
            // Collects features to delete (to get around concurrent modification prob)
            List featuresToDel = new ArrayList(cb.getFeatures());
            // Delete all the Features belonging to this component.
            for (Iterator iter = featuresToDel.iterator(); iter.hasNext();) {
                delFeature((FeatureBean) iter.next());
            }
        }
        // Remove it from the view.
        myComponents.remove(pos);
        // Add to the container to delete it.
        myComponentsToDel.add(cb);
        // Remove from the update list if it has already been added.
        myComponentsToUpdate.remove(cb);
    }

    /**
     * Adds a Component bean to update.
     *
     * @param cb a <code>ComponentBean</code> object to update.
     * <p/>
     * <pre>
     * post: myComponentsToUpdate = myComponentsToUpdate@pre + 1
     * post: myComponents = myComponents@pre
     * </pre>
     */
    public void addProteinToUpdate(ComponentBean cb) {
        myComponentsToUpdate.add(cb);
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
     * <p/>
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
        myExperimentsToAdd.clear();
        myExperimentsToDel.clear();
        myExperimentsToHold.clear();

        // Clear components.
        myComponentsToDel.clear();
        myComponentsToUpdate.clear();

        // Clear links
        myLinkFeatures.clear();
        myUnlinkFeatures.clear();

        // Clear any transactions associated with component beans.
        for (Iterator iter = myComponents.iterator(); iter.hasNext();) {
            ((ComponentBean) iter.next()).clearTransactions();
        }
    }

    /**
     * Returns the AC of the source experiment.
     *
     * @return the AC of the source experiment if it is set; othewise
     *         null is returned.
     */
    public String getSourceExperimentAc() {
        return mySourceExperimentAc;
    }

    /**
     * Set the AC of the source experiment.
     *
     * @param ac the AC of the source experiment.
     */
    public void setSourceExperimentAc(String ac) {
        mySourceExperimentAc = ac;
    }

    /**
     * True when this bean is constructed from an experiment.
     *
     * @return true if this bean is constructed from an experiment. For all
     *         otheer instances, false is returned.
     */
    public boolean isSourceFromAnExperiment() {
        return mySourceExperimentAc != null;
    }

    /**
     * Returns the state for this editor to clone.
     *
     * @return true for all the persistent interactions (i.e., false for a
     *         new interaction not yet persisted).
     */
    public boolean getCloneState() {
        return getAc() != null;
    }

    /**
     * Updates the Feature bean within a Component. If this is a new Feature,
     * it will add as a bean to the appropriate component. Any existing matching
     * bean is removed before adding the updated feature (at the same location).
     *
     * @param feature the feature to update.
     */
    public void saveFeature(Feature feature) {
        // The component bean the feature belongs to.
        ComponentBean compBean = null;

        // The ac to match to retrieve the component.
        String compAc = feature.getComponent().getAc();

        // Find the component bean this feature bean belongs to.
        for (Iterator iter = myComponents.iterator(); iter.hasNext();) {
            ComponentBean cb = (ComponentBean) iter.next();
            if (cb.getAc().equals(compAc)) {
                compBean = cb;
                break;
            }
        }
        // We should have this component.
        assert compBean != null;

        // The feature to AC to compare with.
        String featureAc = feature.getAc();

        // Get corresponding feature bean among this component bean.
        FeatureBean featureBean = null;
        for (Iterator iter = compBean.getFeatures().iterator(); iter.hasNext();) {
            FeatureBean fb = (FeatureBean) iter.next();
            if (fb.getAc().equals(featureAc)) {
                featureBean = fb;
                break;
            }
        }
        // Assume that short label has been changed.
        boolean labelChanged = true;

        // Feature bean can be null for a new feature.
        if (featureBean == null) {
            featureBean = new FeatureBean(feature);
            // Update this component for it to persist correctly.
            addProteinToUpdate(compBean);
        }
        else {
            // The flag is based on the short label of the bean and updated Feature
            labelChanged = !featureBean.getShortLabel().equals(feature.getShortLabel());
            // New bean based on the same key as the existing one.
            featureBean = new FeatureBean(feature, featureBean.getKey());
        }
        // Should have this feature.
        assert featureBean != null;

        // Save the 'updated' feature.
        compBean.saveFeature(featureBean);

        // This feature may be linked to another feature. If the short label
        // has been changed, we need to update the linked Feature as well.
        if (labelChanged && featureBean.hasBoundDomain()) {
            FeatureBean destFb = getFeatureBean(featureBean.getBoundDomain());
            destFb.setBoundDomain(featureBean.getShortLabel());
        }
    }

    /**
     * Deletes the given feature from the current view.
     * @param fb the Feature bean to delete. This feature must exist
     * in the current view.
     */
    public void delFeature(FeatureBean fb) {
        // Extract the corresponding feature bean.
        FeatureBean bean = getFeatureBean(fb);

        // Has this feature linked to another feature?
        if (bean.hasBoundDomain()) {
            addFeatureToUnlink(bean);
            // The linked feature bean.
        }
        // The component bean the feature belongs to.
        ComponentBean comp = getComponentBean(bean);

        // We should have this component.
        assert comp != null;

        // Remove it from the component beans.
        comp.delFeature(bean);

        // Update this component for it to persist correctly.
        addProteinToUpdate(comp);
    }

    /**
     * Adds to the Set that maintains which Features to be linked.
     *
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
        fb1.setChecked(false);
        fb2.setChecked(false);

        // Add to the list of Features to link.
        myLinkFeatures.add(fb1);
        myLinkFeatures.add(fb2);
    }

    /**
     * Adds to the Set that maintains which Features to be unlinked.
     *
     * @param fb the Feature bean to remove the link. This bean
     * replaces any previous similar feature (i.e, no duplicates).
     */
    public void addFeatureToUnlink(FeatureBean fb) {
        // The destination feature as a bean.
        FeatureBean toFb = getFeatureBean(fb.getBoundDomain());
        // This bean must exist.
        assert toFb != null;

        // Update the screen beans.
        fb.setBoundDomain("");
        toFb.setBoundDomain("");

        // Make sure to set the linked beans to false (or else they will
        // display as checked).
        fb.setChecked(false);
        toFb.setChecked(false);

        // Add to the set to update the database later.
        myUnlinkFeatures.add(fb);
        myUnlinkFeatures.add(toFb);
    }

    /**
     * Deletes features that have been added. This is required when an Interaction
     * is cancelled after adding features (these features are submitted via the
     * Feature editor).
     *
     * @param user to delete feature.
     * @throws IntactException for errors in deleting features.
     */
    public void delFeaturesAdded(EditUserI user) throws IntactException {
        // Search among the updated and deleted components.
        deleteFeaturesAdded(myComponentsToUpdate, user);
        // Could be that newly added Feature was deleted.
        deleteFeaturesAdded(myComponentsToDel, user);
    }

    /**
     * Returns a list of selected (checkboxes) Feature beans.
     * @return a list of selected features beans. The list is empty if no items
     * were selected.
     */
    public List getFeaturesToDelete() {
        // The array to collect features to delete.
        List fbs = new ArrayList();

        // Loop through components collecting checked features.
        for (Iterator iter0 = myComponents.iterator(); iter0.hasNext();) {
            ComponentBean compBean = (ComponentBean) iter0.next();
            for (Iterator iter1 = compBean.getFeatures().iterator();
                 iter1.hasNext();) {
                FeatureBean featureBean = (FeatureBean) iter1.next();
                if (featureBean.isChecked()) {
                    fbs.add(featureBean);
                }
            }
        }
        return fbs;
    }

    /**
     * Returns an array that contains two selected (checkboxes) Feature beans.
     * This method assumes that the form has been validated.
     * @return an array containing two selected features beans. The array contains
     * null items if none were selected.
     */
    public FeatureBean[] getFeaturesForLink() {
        // The two feature beans to return.
        FeatureBean[] fbs = new FeatureBean[2];

        // For array indexing.
        int idx = 0;

        // Loop through components until we found two items.
        for (Iterator iter0 = myComponents.iterator(); iter0.hasNext()
                && fbs[1] == null;) {
            ComponentBean compBean = (ComponentBean) iter0.next();
            for (Iterator iter1 = compBean.getFeatures().iterator(); iter1
                    .hasNext()
                    && fbs[1] == null;) {
                FeatureBean featureBean = (FeatureBean) iter1.next();
                if (featureBean.isChecked()) {
                    fbs[idx] = featureBean;
                    ++idx;
                }
            }
        }
        return fbs;
    }

    /**
     * Returns the selected feature for selecting a checkbox. This method
     * @return the selected feature or null if none was selected.
     */
    public FeatureBean getFeatureForUnlink() {
        // Loop till we found the selected feature.
        for (Iterator iter0 = myComponents.iterator(); iter0.hasNext();) {
            ComponentBean compBean = (ComponentBean) iter0.next();
            for (Iterator iter1 = compBean.getFeatures().iterator();
                 iter1.hasNext();) {
                FeatureBean fb = (FeatureBean) iter1.next();
                if (fb.isChecked()) {
                    return fb;
                }
            }
        }
        // None found so far, return null.
        return null;
    }

    /**
     * Returns the selected feature bean by way of selecting edit/delete feature
     * buttons. A selected bean returns true for
     * {@link uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureBean#isSelected()}
     * method. If a bean was found, it will be 'unselected' before returning it.
     * @return the selected feature bean. <code>null</code> is returned
     * if a Feature wasn't selected.
     *
     * <pre>
     * post: return isSelected() == FALSE iff return != null
     * </pre>
     */
    public FeatureBean getSelectedFeature() {
        for (Iterator iter1 = myComponents.iterator(); iter1.hasNext();) {
            ComponentBean cb = (ComponentBean) iter1.next();
            for (Iterator iter2 = cb.getFeatures().iterator(); iter2.hasNext();) {
                FeatureBean fb = (FeatureBean) iter2.next();
                if (fb.isSelected()) {
                    fb.unselect();
                    return fb;
                }
            }
        }
        return null;
    }

    /**
     * Returns the Feature bean for given short label.
     *
     * @param label the short label to get the feature for.
     * @return the matching Feature bean for <code>label</code> or null
     *         if none found.
     */
    public FeatureBean getFeatureBean(String label) {
        // Look in the componets.
        for (Iterator iter0 = myComponents.iterator(); iter0.hasNext();) {
            List features = ((ComponentBean) iter0.next()).getFeatures();
            for (Iterator iter1 = features.iterator(); iter1.hasNext();) {
                FeatureBean fb = (FeatureBean) iter1.next();
                if (fb.getShortLabel().equals(label)) {
                    return fb;
                }
            }
        }
        // Not found the bean, return null.
        return null;
    }

//    private boolean hasFeatureWithClonedSuffix() {
//        for (Iterator iter1 = myComponents.iterator(); iter1.hasNext();) {
//            ComponentBean cb = (ComponentBean) iter1.next();
//            for (Iterator iter2 = cb.getFeatures().iterator(); iter2.hasNext();) {
//                FeatureBean fb = (FeatureBean) iter2.next();
//                if (fb.getShortLabel().endsWith("-x")) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    // Helper methods

    /**
     * Returns true if a Feature in the components already exists (comparision is
     * made on a Feature's short label).
     * @param user the user to do the searching the persistent system.
     * @return true if a Feature in the current Interaction already exists. True
     * is also returned for errors in searching the persistent system.
     */
    private boolean hasDuplicateFeatures(EditUserI user) {
        try {
            for (Iterator iter1 = myComponents.iterator(); iter1.hasNext();) {
                ComponentBean cb = (ComponentBean) iter1.next();
                for (Iterator iter2 = cb.getFeatures().iterator(); iter2.hasNext();) {
                    FeatureBean fb = (FeatureBean) iter2.next();
                    if (user.shortLabelExists(Feature.class, fb.getShortLabel(), fb.getAc())) {
                        return true;
                    }
                }
            }
        }
        catch (SearchException se) {
            // Assume the worst (feature exists).
            return true;
        }
        return false;
    }

    /**
     * Marks a Feature as duplicate if its short label already exists in the
     * database.
     * @param user the user to check the existence of the short label.
     * @throws SearchException error is searching the persistent system.
     */
    private void markDuplicateFeatures(EditUserI user) throws SearchException {
        for (Iterator iter1 = myComponents.iterator(); iter1.hasNext();) {
            ComponentBean cb = (ComponentBean) iter1.next();
            for (Iterator iter2 = cb.getFeatures().iterator(); iter2.hasNext();) {
                FeatureBean fb = (FeatureBean) iter2.next();
                if (user.shortLabelExists(Feature.class, fb.getShortLabel(), fb.getAc())) {
                    fb.setEditState(AbstractEditBean.ERROR);
                }
            }
        }
    }

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
     *
     * @return the collection of experiments to add to the current Interaction.
     * Empty if there are no experiments to add.
     * <p/>
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(ExperimentBean)
     * </pre>
     */
    private Collection getExperimentsToAdd() {
        // Experiments common to both add and delete.
        Collection common = CollectionUtils.intersection(myExperimentsToAdd,
                myExperimentsToDel);
        // All the experiments only found in experiments to add collection.
        return CollectionUtils.subtract(myExperimentsToAdd, common);
    }

    /**
     * Returns a collection of experiments to remove.
     *
     * @return the collection of experiments to remove from the current Interaction.
     * Could be empty if there are no experiments to delete.
     * <p/>
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(ExperimentBean)
     * </pre>
     */
    private Collection getExperimentsToDel() {
        // Experiments common to both add and delete.
        Collection common = CollectionUtils.intersection(myExperimentsToAdd,
                myExperimentsToDel);
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
        deleteComponents(intact, user);

        // Update components.
        updateComponents(intact, user);

        // No need to test whether this 'intact' persistent or not because we
        // know it has been already persisted by persist() call.
        user.update(intact);
    }

    private void persistCurrentView2(EditUserI user) throws SearchException,
            IntactException {
        // Keeps a track of Features to update. This avoids multiple updates to the
        // same feature.
        Set featuresToUpdate = new HashSet();

        // Handler to the link sorter and sort links.
        ItemLinkSorter sorter = getFeatureSorter();
        sorter.doIt(myLinkFeatures, myUnlinkFeatures);

        // Links features.
        linkFeatures(featuresToUpdate, sorter.getItemsToLink().iterator(), user);

        // Unlinks features.
        unlinkFeatures(featuresToUpdate, sorter.getItemsToUnLink().iterator());

        // Update the feature in the Set.
        for (Iterator iter = featuresToUpdate.iterator(); iter.hasNext();) {
            user.update(iter.next());
        }

        for (Iterator iter1 = myComponentsToUpdate.iterator(); iter1.hasNext();) {
            ComponentBean cb = (ComponentBean) iter1.next();
            Component comp = cb.getComponent(user);

            // Process features deleted from the current component.
            for (Iterator iter2 = cb.getFeaturesToDelete().iterator(); iter2.hasNext();) {
                FeatureBean fb1 = (FeatureBean) iter2.next();
                Feature featureToDel = fb1.getFeature(user);
                // Remove from the component.
                comp.removeBindingDomain(featureToDel);

                // No further action if this feature is not linked.
                if (!fb1.hasBoundDomain()) {
                    continue;
                }
                // The linked feature bean.
                FeatureBean fb2 = getFeatureBean(fb1.getBoundDomain());

                // Linked feature may have already been deleted.
                if (fb2 == null) {
                    continue;
                }
                // The linked feature.
                Feature linkedFeature = fb2.getFeature(user);
                linkedFeature.setBoundDomain(null);
                user.update(linkedFeature);
            }
            user.update(comp);
        }
    }

    /**
     * Resets this interaction with give interaction object.
     *
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
    }

    /**
     * Returns the Component bean for given Feature bean.
     * @param fb the Feature bean to search for.
     * @return the Component bean for <code>fb</code>; null is returned if there
     * is no matching component exists for <code>fb</code>.
     */
    private ComponentBean getComponentBean(FeatureBean fb) {
        for (Iterator iterator = myComponents.iterator(); iterator.hasNext();) {
            ComponentBean cb = (ComponentBean) iterator.next();
            if (cb.getFeatures().contains(fb)) {
                return cb;
            }
        }
        // None found.
        return null;
    }

    private FeatureBean getFeatureBean(FeatureBean fb) {
        // Look in the componets.
        for (Iterator iter0 = myComponents.iterator(); iter0.hasNext();) {
            List features = ((ComponentBean) iter0.next()).getFeatures();
            if (features.contains(fb)) {
                return (FeatureBean) features.get(features.indexOf(fb));
            }
        }
        // Not found the bean, return null.
        return null;
    }

    /**
     * Deletes added featues from given collection.
     * @param componets the components to search for Features
     * @param user the user to delete added features.
     * @throws IntactException for errors in deleting a Feature.
     */
    private void deleteFeaturesAdded(Collection componets, EditUserI user)
            throws IntactException {
        for (Iterator iter0 = componets.iterator(); iter0.hasNext();) {
            ComponentBean cb = (ComponentBean) iter0.next();
            for (Iterator iter1 = cb.getFeaturesAdded().iterator(); iter1.hasNext();) {
                user.delete(((FeatureBean) iter1.next()).getFeature());
            }
        }
    }

    /**
     * Deletes all the components in the components to delete collection.
     * @param intact the current Interaction
     * @param user the user to delete a Component.
     * @throws SearchException for retrieving a Component or a Feature.
     * @throws IntactException for errors in creating a Component.
     */
    private void deleteComponents(Interaction intact, EditUserI user)
            throws SearchException, IntactException {
        for (Iterator iter = myComponentsToDel.iterator(); iter.hasNext();) {
            ComponentBean cb = (ComponentBean) iter.next();
            Component comp = cb.getComponent(user);
            // No need to delete from persistent storage if the link to this
            // Protein is not persisted.
            if ((comp == null) || (comp.getAc() == null)) {
                continue;
            }
            // Disconnect any links between features in the component.
            disconnectLinkedFeatures(cb, user);

            user.delete(comp);
            intact.removeComponent(comp);
        }
    }

    /**
     * Updates Components.
     * @param intact the current Interaction
     * @param user the user to create objects if they don't exist.
     * @throws IntactException for errors in creating a Feature or a Range
     * @throws SearchException for errors in retrieving a Feature.
     */
    private void updateComponents(Interaction intact, EditUserI user)
            throws IntactException, SearchException {
        // Update components.
        for (Iterator iter1 = myComponentsToUpdate.iterator(); iter1.hasNext();) {
            ComponentBean cb = (ComponentBean) iter1.next();
            cb.setInteraction((Interaction) getAnnotatedObject());

            // Disconnect any links between features in the component which are
            disconnectLinkedFeatures(cb, user);

            Component comp = cb.getComponent(user);

            // Add features
            for (Iterator iter2 = cb.getFeaturesToAdd().iterator(); iter2.hasNext();) {
                Feature feature = ((FeatureBean) iter2.next()).getFeature(user);
                // Feature AC is null for a cloned interaction.
                if (feature.getAc() == null) {
                    // Create a new Feature.
                    user.create(feature);

                    // Create ranges for the feature.
                    for (Iterator iter3 = feature.getRanges().iterator(); iter3.hasNext();) {
                        user.create((Range) iter3.next());
                    }
                }
                // Add to the component.
                comp.addBindingDomain(feature);
            }
            intact.addComponent(comp);

            if (user.isPersistent(comp)) {
                user.update(comp);
            }
            else {
                user.create(comp);
            }
        }
    }

    /**
     * Links features.
     * @param set collects Features to update.
     * @param user the user to get a Feature.
     * @throws SearchException error in accessing a Feature.
     */
    private void linkFeatures(Set set, Iterator iter, EditUserI user)
            throws SearchException {
        while (iter.hasNext()) {
            // The feature bean to link.
            FeatureBean fb = (FeatureBean) iter.next();

            // The bound domain can be empty if this link was later unlinked
            // before it was persisted.
            if (!fb.hasBoundDomain()) {
                // Already unliked, carry on with the next.
                continue;
            }
            // The Feature objets to link together. Use 'user' to get changes
            // to the bound domain.
            Feature srcFeature = fb.getFeature(user);
            Feature toFeature = getFeatureBean(fb.getBoundDomain()).getFeature(user);

            // Sets the links.
            srcFeature.setBoundDomain(toFeature);
            toFeature.setBoundDomain(srcFeature);

            // Update features.
            set.add(srcFeature);
            set.add(toFeature);
        }
    }

    /**
     * Unlinks features.
     * @param set collects Features to update.
     */
    private void unlinkFeatures(Set set, Iterator iter) {
        while (iter.hasNext()) {
            // The Feature to unlink.
            Feature feature = ((FeatureBean) iter.next()).getFeature();

            // Set the bound domain to null.
            feature.setBoundDomain(null);

            // Update features.
            set.add(feature);
        }
    }

    /**
     * Disconnects the link between two Features.
     * @param cb the bean to search among the Features to delete
     * @param user the user to update a Feature
     * @throws SearchException for errors in accessing a Feature.
     * @throws IntactException for update errors.
     */
    private void disconnectLinkedFeatures(ComponentBean cb, EditUserI user)
            throws SearchException, IntactException {
        // Delete any links among features to delete. This should be done
        // first before deleting a feature. Actual deleting a feature is
        // done in a separate transaction.
        for (Iterator iter = cb.getFeaturesToDelete().iterator(); iter.hasNext();) {
            Feature feature = ((FeatureBean) iter.next()).getFeature(user);
            // Remove any links if this feature is linked to another feature.
            if (feature.getBoundDomain() != null) {
                Feature toFeature = feature.getBoundDomain();
                if (toFeature.getBoundDomain() == null) {
                    continue;
                }
                // Disconnect the links between two features.
                toFeature.setBoundDomain(null);
                user.update(toFeature);
            }
        }
    }

    private boolean hasErrorFeatures() {
        for (Iterator iter1 = myComponents.iterator(); iter1.hasNext();) {
            ComponentBean cb = (ComponentBean) iter1.next();
            for (Iterator iter2 = cb.getFeatures().iterator(); iter2.hasNext();) {
                FeatureBean fb = (FeatureBean) iter2.next();
                if (fb.getEditState().equals(AbstractEditBean.ERROR)) {
                    return true;
                }
            }
        }
        return false;
    }

    private ItemLinkSorter getFeatureSorter() {
        if (myFeatureSorter == null) {
            myFeatureSorter = new ItemLinkSorter();
        }
        return myFeatureSorter;
    }

    // Static Inner Class -----------------------------------------------------

    private static class ProteinBeanPredicate implements Predicate {

        private static ProteinBeanPredicate ourInstance = new ProteinBeanPredicate();

        public boolean evaluate(Object object) {
            return !((ComponentBean) object).getEditState().equals(ComponentBean.SAVE_NEW);
        }
    }

    // End of Inner class -----------------------------------------------------

    // Sanity checking routines
}
