/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditKeyBean;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactHelper;

import java.io.Serializable;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.commons.collections.CollectionUtils;

/**
 * Bean to hold data for a component in an Interaction.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ComponentBean extends AbstractEditKeyBean implements Serializable {

    // Class Data

    /**
     * The saving new state; this is different to saving state as this state
     * indicates saving a new item. This state is only used by proteins.jsp
     * to save new componets as as result of a search.
     */
    static final String SAVE_NEW = "saveNew";

    // Instance Data

    /**
     * The interaction this protein belongs to.
     */
    private Interaction myInteraction;

    /**
     * The interactor of the bean.
     */
    private Interactor myInteractor;

    /**
     * The object this instance is created with.
     */
    private Component myComponent;

    /**
     * Swiss-Prot AC.
     */
    private String mySPAc;

    /**
     * The role of this Protein.
     */
    private String myRole;

    /**
     * The stoichiometry.
     */
    private float myStoichiometry = 1.0f;

    /**
     * The organism.
     */
    private String myOrganism;

    /**
     * Expressed in as a biosource short label.
     */
    private String myExpressedIn;

    /**
     * Contains features associated with this component.
     */
    private List myFeatures = new ArrayList();

    /**
     * A list of features to add.
     */
    private transient List myFeaturesToAdd = new ArrayList();

    /**
     * A list of features to delete.
     */
    private transient List myFeaturesToDel = new ArrayList();

    /**
     * Instantiate an object of this class from a Protein instance.
     * @param protein the <code>Protein</code> object.
     */
    public ComponentBean(Protein protein) {
        myInteractor = (Interactor) IntactHelper.getRealIntactObject(protein);
        mySPAc = getSPAc();
        setOrganism();
        setEditState(SAVE_NEW);
    }

    /**
     * Instantiate an object of this class from a Component instance.
     * @param component the <code>Component</code> object.
     */
    public ComponentBean(Component component) {
        myComponent = component;
        myInteraction = (Interaction) IntactHelper.getRealIntactObject(
                component.getInteraction());
        myInteractor = (Interactor) IntactHelper.getRealIntactObject(
                component.getInteractor());
        mySPAc = getSPAc();
        myRole = component.getCvComponentRole().getShortLabel();
        myStoichiometry = component.getStoichiometry();
        setOrganism();
        setExpressedIn();

        // Set the feature for this bean.
        for (Iterator iter = myComponent.getBindingDomains().iterator(); iter.hasNext();) {
            Feature feature = (Feature) iter.next();
            myFeatures.add(new FeatureBean(feature));
        }
    }

    // Read only properties.

    public Component getComponent(EditUserI user) throws SearchException {
        CvComponentRole newrole = getRole(user);
        // Must have a non null role and interaction for a valid component
        if ((newrole == null) || (myInteraction == null)) {
            return null;
        }
        // Component is null if this bean constructed from a Protein.
        if (myComponent == null) {
            myComponent = new Component(user.getInstitution(), myInteraction,
                    myInteractor, newrole);
        }
        else {
            myComponent.setCvComponentRole(newrole);
        }
        myComponent.setStoichiometry(getStoichiometry());

        // The expressed in to set in the component.
        BioSource expressedIn = null;
        if (myExpressedIn != null) {
            expressedIn = (BioSource) user.getObjectByLabel(
                    BioSource.class, myExpressedIn);
        }
        myComponent.setExpressedIn(expressedIn);

        return myComponent;
    }

    public String getAc() {
        return myComponent == null ? null : myComponent.getAc();
    }

    public String getInteractorAc() {
        return myInteractor.getAc();
    }

    public String getShortLabel() {
        return myInteractor.getShortLabel();
    }

    public String getShortLabelLink() {
        return getLink("Protein", getShortLabel());
    }

    public String getSpAc() {
        return mySPAc;
    }

    public String getFullName() {
        return myInteractor.getFullName();
    }

    // Read/Write properties.

    public String getRole() {
        return myRole;
    }

    public void setRole(String role) {
        myRole = EditorMenuFactory.normalizeMenuItem(role);
    }

    public float getStoichiometry() {
        return myStoichiometry;
    }

    public void setStoichiometry(float stoichiometry) {
        myStoichiometry = stoichiometry;
    }

    public String getExpressedIn() {
        return myExpressedIn;
    }

    public void setExpressedIn(String expressedIn) {
        myExpressedIn = EditorMenuFactory.normalizeMenuItem(expressedIn);
    }

    public String getOrganism() {
        return myOrganism;
    }

    /**
     * Sets the interaction for this bean. This is necessary for a newly created
     * Interaction as it doesn't exist until it is ready to persist.
     * @param interaction the interaction to set.
     */
    public void setInteraction(Interaction interaction) {
        myInteraction = interaction;
    }

    // Override Objects's hashCode and equal methods.

//    public int hashCode() {
//        // The result to return.
//        int result = 0;
//
//        // The prime number to calculate the hashcode.
//        int prime = 17;
//
//        result = prime * result + getShortLabel().hashCode();
//        if (myRole != null) {
//            result = prime * result + myRole.hashCode();
//        }
////        result = prime * result + myFeatures.size();
//        System.out.println("At result1: " + result);
//        for (int i = 0; i < myFeatures.size(); i++) {
//            result = prime * result + myFeatures.get(i).hashCode();
//        }
//        System.out.println("Resul2: " + result);
//        return result;
//    }
//    /**
//     * Compares <code>obj</code> with this object according to
//     * Java's equals() contract. Only returns <tt>true</tt> if the short label
//     * role and features for both objects match.
//     * @param obj the object to compare.
//     */
//    public boolean equals(Object obj) {
//        System.out.println("AT the top of equals");
//        // Identical to this?
//        if (obj == this) {
//            System.out.println("Same bean");
//            return true;
//        }
//        if ((obj != null) && (getClass() == obj.getClass())) {
//            // Can safely cast it.
//            ComponentBean other = (ComponentBean) obj;
//
//            // Need to compare object attributes. Assume they are not equal.
//            boolean result = false;
//            if (getShortLabel().equals(other.getShortLabel())) {
//                System.out.println("short label match");
//                if (myRole == null) {
//                    System.out.println("Roles are null");
//                    // Other's role must be null as well.
//                    result = other.myRole == null;
//                }
//                System.out.println("Role: " + myRole + " - " + other.myRole);
//                result = myRole.equals(other.myRole);
//            }
//            // No need to check further if the result is false.
//            if (!result) {
//                return result;
//            }
//            // Check the feature beans.
//            System.out.println("About to check the features");
//            return CollectionUtils.isEqualCollection(myFeatures, other.myFeatures);
//        }
//        return false;
//    }

    /**
     * List of features if the component is set. An empty list is returned if
     * this bean is constructed without a component.
     * @return a list of feature beans.
     *
     * <pre>
     * post: return->forall(obj : Object | obj.oclIsTypeOf(FeatureBean))
     * </pre>
     */
    public List getFeatures() {
//        Logger.getLogger(EditorConstants.LOGGER).debug("Getting features");
//        for (Iterator iter = myFeatures.iterator(); iter.hasNext();) {
//            FeatureBean element = (FeatureBean) iter.next();
//            System.out.println("Getting the feature: " + element.getAc() + " - " + element.getRanges());
//        }
        return myFeatures;
    }

    /**
     * Removes the feature from view and add it to the collection of features
     * to delete
     * @param bean the Feature bean to delete.
     */
    public void delFeature(FeatureBean bean) {
        // Remove it from the view (it was added as a bean).
        myFeatures.remove(bean);
        
        // Add the feature to delete.
        myFeaturesToDel.add(bean);
    }

    /**
     * Returns a collection of Feature beans to delete
     * @return a collection of Feature beans to delete
     *
     * <pre>
     * post: return->forall(obj : Object | obj.oclIsTypeOf(FeatureBean))
     * </pre>
     */
    public Collection getFeaturesToDelete() {
//        // Features common to both add and delete.
//        Collection common = CollectionUtils.intersection(myFeaturesToDel, myFeaturesToAdd);
//        // All the features only found in 'features to delete' collection.
//        return CollectionUtils.subtract(myFeaturesToDel, common);
        return myFeaturesToDel;
    }

    /**
     * Returns a collection of Feature to add
     * @return a collection of Feature to add
     *
     * <pre>
     * post: return->forall(obj : Object | obj.oclIsTypeOf(FeatureBean))
     * </pre>
     */
    public Collection getFeaturesToAdd() {
        // Features common to both add and delete.
        Collection common = CollectionUtils.intersection(myFeaturesToAdd, myFeaturesToDel);
        // All the features only found in 'features to add' collection.
        return CollectionUtils.subtract(myFeaturesToAdd, common);
    }

    public void saveFeature(Feature feature) {
        // Create the feature bean to compare.
        FeatureBean fb = new FeatureBean(feature);
//        System.out.println("In save feature - component bean - " + fb.getAc() + ", " + fb.getBoundDomain());

        // Find the feature bean within the component bean.
        if (myFeatures.contains(fb)) {
            // Update an existing feature; remove it and add the new bean.
            int idx = myFeatures.indexOf(fb);
            myFeatures.remove(idx);
            myFeatures.add(idx, fb);

            // Update the new feature list if it exist in there. This is important
            // or else getFeatue() wouldn't return an up todate Feature object.
            if (myFeaturesToAdd.contains(fb)) {
//                System.out.println("Updating existing new feature bean");
                // Update an existing feature; remove it and add the new bean.
                myFeaturesToAdd.remove(fb);
                myFeaturesToAdd.add(fb);
            }
        }
        else {
//            System.out.println("Adding a new feature as: " + fb.getAc());
            // New feature; just add it.
            myFeatures.add(fb);
            myFeaturesToAdd.add(fb);
        }
    }

    public void clearTransactions() {
        myFeaturesToAdd.clear();
        myFeaturesToDel.clear();
    }

    // Helper methods

    private void setOrganism() {
        BioSource biosource = myInteractor.getBioSource();
        if (biosource != null) {
            myOrganism = biosource.getShortLabel();
        }
    }

    private void setExpressedIn() {
        BioSource bs = myComponent.getExpressedIn();
        if (bs != null) {
            myExpressedIn = bs.getShortLabel();
        }
    }

    private String getSPAc() {
        for (Iterator iter = myInteractor.getXrefs().iterator(); iter.hasNext();) {
            Xref xref = (Xref) iter.next();
            // Only consider SwissProt database entries.
            if (xref.getCvDatabase().getShortLabel().equals("uniprot")) {
                return xref.getPrimaryId();
            }
        }
        return "";
    }

    private CvComponentRole getRole(EditUserI user) throws SearchException  {
        if (myRole != null) {
            return (CvComponentRole) user.getObjectByLabel(
                    CvComponentRole.class, myRole);
        }
        return null;
    }
}
