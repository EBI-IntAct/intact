/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.feature;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts.tiles.ComponentContext;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;

import java.util.*;

/**
 * Feature view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureViewBean extends AbstractEditViewBean {

    // Class Data

    /**
     * The menu items for the boolean list.
     */
    private static final List ourBoolenMenus = Arrays.asList(
            new String[]{Boolean.TRUE.toString(), Boolean.FALSE.toString()});


    private Interaction myParent;
    /**
     * The parent of this view bean (Feature is part of an Interaction)
     */
    private InteractionViewBean myParentViewBean;
    
    /**
     * The component this feature belongs to.
     */
    private Component myComponent;

    /**
     * The CvFeature type; null for a feature not yet persisted.
     */
    private String myCvFeatureType;

    /**
     * The CvFeatureIdentification type; null for a feature not yet persisted.
     */
    private String myCvFeatureIdent;

    /**
     * List of ranges for the feature. This collection is cleared once the user
     * commits the transaction.This represents the current view of ranges for
     * the feature.
     */
    private List myRanges = new ArrayList();

    /**
     * List of new ranges to add to the feature. This collection is cleared
     * once the user commits the transaction.
     */
    private Set myRangesToAdd = new HashSet();

    /**
     * List of ranges to delete from the feature. This collection is cleared
     * once the user commits the transaction.
     */
    private Set myRangesToDel = new HashSet();

    /**
     * List of ranges to update for the current feature. This collection is
     * cleared once the user commits the transaction.
     */
    private Set myRangesToUpdate = new HashSet();

    /**
     * True if this is a new feature (as a result of selecting Add Feature). Once
     * this is set it will remain till the current Feature is ended.
     */
    private boolean myNewFeature;

    // Override to provide the Feature layout.
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.feature.layout");
    }

    // Override to provide Experiment help tag.
    public String getHelpTag() {
        return "editor.experiment";
    }

    // Override to provide set feature from the form.
    public void copyPropertiesFrom(EditorActionForm editorForm) {
        // Set the common values by calling super first.
        super.copyPropertiesFrom(editorForm);

        // Cast to the feature form to get feature data.
        FeatureActionForm featureForm = (FeatureActionForm) editorForm;
        setCvFeatureType(featureForm.getFeatureType());
        setCvFeatureIdentification(featureForm.getFeatureIdent());
    }

    // Override to copy Feature data.
    public void copyPropertiesTo(EditorActionForm form) {
        super.copyPropertiesTo(form);

        // Cast to the feature form to copy feature data.
        FeatureActionForm featureForm = (FeatureActionForm) form;

        // Properties related to the parent protein.
        featureForm.setParentAc(getParentAc());
        featureForm.setParentShortLabel(getParentShortLabel());
        featureForm.setParentFullName(getParentFullName());

        // Properties related to feature.
        featureForm.setShortLabel(getShortLabel());
        featureForm.setAc(getAc());
        featureForm.setFeatureType(getCvFeatureType());
        featureForm.setFeatureIdent(getCvFeatureIdentification());

        // Properties related to the ranges.
        featureForm.setRanges(myRanges);
    }

    // Override to not to display the Delete button.
    public boolean getDeleteState() {
        return false;
    }

    /**
     * Sets the parent view of this feature.
     * @param parent the parent of this view.
     */
    public void setParentView(InteractionViewBean parent) {
        myParentViewBean = parent;
    }

    public Interaction getParent() {
        return myParent;
    }

    public void setParent(Interaction interaction) {
        myParent = interaction;
    }

    /**
     * Returns the handler to the parent view.
     * @return the Interaction view as the parent of this view.
     */
    public InteractionViewBean getParentView() {
        return myParentViewBean;
    }
    
    /**
     * Sets the component for this feature.
     * @param component the component this feature belongs to.
     */
    public void setComponent(Component component) {
        myComponent = component;
    }

    public Component getComponent() {
        return myComponent;
    }

    /**
     * Returns a defined feature instance. Needed by JSPs.
     * @return the defined feature instance.
     */
    public DefinedFeatureBean getDefinedFeature() {
        return DefinedFeatureBean.getInstance();
    }

    public String getCvFeatureType() {
        return myCvFeatureType;
    }

    public void setCvFeatureType(String featureType) {
        myCvFeatureType = featureType;
    }

    public String getCvFeatureIdentification() {
        return myCvFeatureIdent;
    }

    public void setCvFeatureIdentification(String featureIdent) {
        myCvFeatureIdent = EditorMenuFactory.normalizeMenuItem(featureIdent);
    }

    // Override super to add extra.
    public void clearTransactions() {
        super.clearTransactions();

        // Clear transaction ranges.
        myRangesToAdd.clear();
        myRangesToDel.clear();
        myRangesToUpdate.clear();
    }

    /**
     * The CvFeatureType menu list.
     * @return the CvFeatureType menu consisting of CvFeatureType short labels.
     * The first item in the menu may contain '---Select---' if the current
     * CvFeatureType is not set (a Feature not yet persisted).
     * @throws SearchException for errors in generating menus.
     */
    public List getCvFeatureTypeMenu() throws SearchException {
        int mode = (getCvFeatureType() == null) ? 1 : 0;
        return getMenuFactory().getMenu(EditorMenuFactory.FEATURE_TYPE, mode);
    }

    /**
     * The CvFeatureIdentification menu list.
     * @return the CvFeatureIdentification menu consisting of
     * CvFeatureIdentification short labels. The first item in the menu may
     * contain '---Select---' if the current CvFeatureIdentification is
     * not set (a Feature not yet persisted).
     * @throws SearchException for errors in generating menus.
     */
    public List getCvFeatureIdentificationMenu() throws SearchException {
        int mode = (getCvFeatureIdentification() == null) ? 1 : 0;
        return getMenuFactory().getMenu(EditorMenuFactory.FEATURE_IDENTIFICATION, mode);
    }

    /**
     * Returns the boolean menu. Warning this method exposes the internal list
     * to outside (this avoids the overhead of creating an array for each method
     * call).
     * @return the boolean menu.
     *
     * <pre>
     * post: return->size() == 2
     * post: return->forall(obj : Object | obj.oclIsTypeOf(String))
     * </pre>
     */
    public List getBooleanMenu() {
        return ourBoolenMenus;
    }

    /**
     * Adds a range.
     * @param rb the range bean to add.
     *
     * <pre>
     * post: myRangesToAdd = myRangesToAdd@pre + 1
     * post: myRanges = myRanges@pre + 1
     * </pre>
     */
    public void addRange(RangeBean rb) {
        // Add to the container to add a range.
        myRangesToAdd.add(rb);
        // Add to the view as well.
        myRanges.add(rb);
    }

    /**
     * Removes a range for given bean.
     * @param rb the bean to remove the range for. The key in the bean identifies
     * the range to delete.
     */
    public void delRange(RangeBean rb) {
        // Add to the container to delete the range.
        myRangesToDel.add(rb);

        // Remove from the view as well.
        myRanges.remove(rb);
    }

    /**
     * Replaces an existing range bean with a new bean. This method takes
     * care of refreshing relevant lists. For example, if the existing bean is in
     * the new collection, it will be removed from the new collection before the
     * new bean is added.
     * @param rb the new bean to replace existing bean. The existing bean has
     * the same key as the new bean.
     */
    public void saveRange(RangeBean rb) {
//        // Remove the existing bean which has the same key as the new bean.
//        myRanges.remove(rb);
//        // This will add the new bean.
//        myRanges.add(rb);

        // Do the same check for beans to add collection as well.
        if (!myRangesToAdd.contains(rb)) {
             myRangesToUpdate.add(rb);
//            // Remove the existing bean with the same key.
//            myRangesToAdd.remove(rb);
//            // Add the new bean.
//            myRangesToAdd.add(rb);
//        }
//        else {
//            // Remove the old bean from the 'update' list.
//            myRangesToUpdate.remove(rb);
//            // Add the new bean to the update list.
//            myRangesToUpdate.add(rb);
        }
    }

    /**
     * True if given range exists in the current display.
     * @param bean the Range bean to compare.
     * @return true if <code>bean</code> exists in the current display.
     *
     * @see RangeBean#isEquivalent(RangeBean)
     */
    public boolean rangeExists(RangeBean bean) {
        for (Iterator iter = myRanges.iterator(); iter.hasNext();) {
            RangeBean rb = (RangeBean) iter.next();
            // Avoid comparing to itself.
            if (rb.getKey() == bean.getKey()) {
                continue;
            }
            if (rb.isEquivalent(bean)) {
                return true;
            }
        }
        // Not equivalent; false is returned.
        return false;
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

    /**
     * Return the status (new or old) of the current feature.
     * @return true if this is a new feature (Add Feature); for all other
     * instances false is returned.
     */
    public boolean isNewFeature() {
        return myNewFeature;
    }

    // Override the super method to initialize this class specific resetting.
    protected void reset(Class clazz) {
        super.reset(clazz);

        setCvFeatureType(null);
        setCvFeatureIdentification(null);

        // Clear ranges
        myRanges.clear();

        // Mark it as a new feature.
        myNewFeature = true;
    }

    // Reset the fields to null if we don't have values to set. Failure
    // to do so will display the previous edit object's values as current.
    protected void reset(AnnotatedObject annobj) {
        super.reset(annobj);

        // Must be a feature.
        Feature feature = (Feature) annobj;

        setComponent(feature.getComponent());

        // CVFeatureType is compulsory.
        setCvFeatureType(feature.getCvFeatureType().getShortLabel());

        // CvFeatureIdent is not compulsory.
        CvFeatureIdentification ident = feature.getCvFeatureIdentification();
        setCvFeatureIdentification(ident != null
                ? feature.getCvFeatureIdentification().getShortLabel() : null);

        // Set the ranges for the form to update.
        setRanges(feature.getRanges());

        // Not a new feature.
        myNewFeature = false;
    }

    // Implements abstract methods

    protected void updateAnnotatedObject(EditUserI user) throws SearchException {
        // The feature type for the current feature.
        CvFeatureType featureType = (CvFeatureType) user.getObjectByLabel(
                CvFeatureType.class, getCvFeatureType());

        // The current feature.
        Feature feature = (Feature) getAnnotatedObject();

        // null if creating a new Feature.
        if (feature == null) {
            // Not persisted; create a new feature object.
            feature = new Feature(user.getInstitution(), getShortLabel(),
                    myComponent, featureType);
            setAnnotatedObject(feature);
        }
        else {
            // Update the existing feature.
            feature.setCvFeatureType(featureType);
        }
        feature.setCvFeatureIdentification(getCvFeatureIndent(user));
    }

    // Helper methods.

    private String getParentAc() {
        return myComponent.getInteractor().getAc();
    }

    private String getParentShortLabel() {
        return myComponent.getInteractor().getShortLabel();
    }

    private String getParentFullName() {
        return myComponent.getInteractor().getFullName();
    }

    private void setRanges(Collection ranges) {
        // Clear any previous ranges.
        myRanges.clear();
        for (Iterator iterator = ranges.iterator(); iterator.hasNext();) {
            Range range = (Range) iterator.next();
            myRanges.add(new RangeBean(range));
        }
    }

    private CvFeatureIdentification getCvFeatureIndent(EditUserI user) throws SearchException {
        if (myCvFeatureIdent == null) {
            return null;
        }
        return (CvFeatureIdentification) user.getObjectByLabel(
                CvFeatureIdentification.class, myCvFeatureIdent);
    }

    /**
     * Returns a collection of ranges to add.
     * @return the collection of ranges to add to the current Feature.
     * Could be empty if there are no ranges to add.
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(RangeBean)
     * </pre>
     */
    private Collection getRangesToAdd() {
        // Ranges common to both add and delete.
        Collection common = CollectionUtils.intersection(myRangesToAdd, myRangesToDel);
        // All the ranges only found in 'ranges to add' collection.
        return CollectionUtils.subtract(myRangesToAdd, common);
    }

    /**
     * Returns a collection of ranges to delete.
     * @return the collection of ranges to delete from the current Feature.
     * Could be empty if there are no ranges to delete..
     *
     * <pre>
     * post: return->forall(obj: Object | obj.oclIsTypeOf(RangeBean)
     * </pre>
     */
    private Collection getRangesToDel() {
        // Ranges common to both add and delete.
        Collection common = CollectionUtils.intersection(myRangesToAdd, myRangesToDel);
        // All the ranges only found in 'ranges to delete' collection.
        return CollectionUtils.subtract(myRangesToDel, common);
    }
    
    private void persistCurrentView(EditUserI user) throws IntactException, SearchException {
        // The current feature.
        Feature feature = (Feature) getAnnotatedObject();
        
        // Add new ranges.
        for (Iterator iter = getRangesToAdd().iterator(); iter.hasNext();) {
            // Create the updated range.
            Range range = ((RangeBean) iter.next()).getRange(user);
            // Avoid creating duplicate Ranges.
            if (feature.getRanges().contains(range)) {
                continue;
            }
            user.create(range);
            feature.addRange(range);
        }
        
        // Delete ranges.
        for (Iterator iter = getRangesToDel().iterator(); iter.hasNext();) {
            Range range = ((RangeBean) iter.next()).getRange();
            user.delete(range);
            feature.removeRange(range);
        }

        // Update existing ranges.
        for (Iterator iter = myRangesToUpdate.iterator(); iter.hasNext();) {
            // Update the 'updated' range.
            Range range = ((RangeBean) iter.next()).getRange(user);
            user.update(range);
        }
        // No need to test whether this 'feature' persistent or not because we
        // know it has been already persisted by persist() call.
        user.update(feature);
    }
}
