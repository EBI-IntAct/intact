/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.feature;

import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.model.CvFeatureIdentification;
import uk.ac.ebi.intact.model.CvFeatureType;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Range;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Feature bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureBean implements Serializable, Cloneable {

    // Instance Data

    /**
     * Reference to the range this instance is created with.
     */
    private Feature myFeature;

    /**
     * The short label
     */
    private String myShortLabel;

    /**
     * The full name of this feature.
     */
    private String myFullName;

    /**
     * The feature type.
     */
    private String myType;

    /**
     * The feature detection.
     */
    private String myDetection;

    /**
     * A list of range beans.
     */
    private List myRanges = new ArrayList();

    /**
     * The short label of feature this feature inteacts with
     */
    private String myBoundDomain;

    /**
     * True if this feature is linked. Default is not linked.
     */
    private boolean myLinked;

    /**
     * True if this bein selected. Default is not selected.
     */
    private boolean mySelected;
    
    /**
     * Default constructor. Only visible to subclasses.
     */
    protected FeatureBean() {}

    /**
     * Instantiate an object of this class from a Feature instance.
     *
     * @param feature the <code>Feature</code> object.
     */
    public FeatureBean(Feature feature) {
        myFeature = feature;
        myShortLabel = feature.getShortLabel();
        myFullName = feature.getFullName();
        myType = feature.getCvFeatureType().getShortLabel();
        myDetection = feature.getCvFeatureIdentification() == null ? ""
                : feature.getCvFeatureIdentification().getShortLabel();

        // Loop through the ranges.
        for (Iterator iter = feature.getRanges().iterator(); iter.hasNext();) {
            Range range = (Range) iter.next();
            myRanges.add(new RangeBean(range));
        }

        myBoundDomain = feature.getBoundDomain() == null ? ""
                : feature.getBoundDomain().getShortLabel();
    }

    // Read only properties.

    public String getAc() {
        return myFeature.getAc();
    }

    public String getShortLabel() {
        return myShortLabel;
    }

    public String getFullName() {
        return myFullName;
    }

    public String getRanges() {
        // The range to return.
        String range = "";
        boolean first = true;
        for (Iterator iterator = myRanges.iterator(); iterator.hasNext();) {
            RangeBean rangeBean = (RangeBean) iterator.next();
            if (first) {
                first = false;
            }
            else {
                range += ", ";
            }
            range += rangeBean.toString();

        }
        return range;
    }

    public String getType() {
        return myType;
    }

    public String getDetection() {
        return myDetection;
    }

    public String getComponentAc() {
        return myFeature.getComponent().getAc();
    }
    
    // Read/Write methods for JSPs

    public String getBoundDomain() {
        return myBoundDomain;
    }

    public void setBoundDomain(String label) {
        myBoundDomain = label;
    }

    public boolean isLinked() {
        return myLinked;
    }

    public void setLinked(boolean linked) {
        myLinked = linked;
    }

    public void setFeatureCmd(String value) {
        mySelected = true;
    }

    // Other methods

    public void unselect() {
        mySelected = false;
    }
    
    public boolean isSelected() {
        return mySelected;
    }

    public Feature getFeature() {
        return myFeature;
    }

    /**
     * Updates the internal Feature with the new values from the form.
     *
     * @param user the user instance to search database.
     * @throws IllegalArgumentException
     * @throws SearchException for errors in searching the database.
     * @throws IllegalArgumentException for errors in creating ranges.
     */
    public Feature getFeature(EditUserI user) throws SearchException,
        IllegalArgumentException {
        myFeature.setShortLabel(getShortLabel());
        myFeature.setFullName(getFullName());
        CvFeatureType type = (CvFeatureType) user.getObjectByLabel(
                CvFeatureType.class, getType());
        myFeature.setCvFeatureType(type);
        
        // Set the cv ident.
        CvFeatureIdentification ident = null;
        if (getDetection().length() != 0) {
            ident = (CvFeatureIdentification) user.getObjectByLabel(
                    CvFeatureIdentification.class, getDetection());
        }
        myFeature.setCvFeatureIdentification(ident);

        // Clear previous reanges and update with new ranges.
        myFeature.getRanges().clear();
        
        // Update the ranges.
        for (Iterator iterator = myRanges.iterator(); iterator.hasNext();) {
            RangeBean rangeBean = (RangeBean) iterator.next();
            myFeature.addRange(rangeBean.getRange(user));
        }
        // Set the bound domain if it isn't empty.
        if (getBoundDomain().length() != 0) {
            Feature boumdDomain = (Feature) user.getObjectByLabel(
                    Feature.class, getBoundDomain());
            myFeature.setBoundDomain(boumdDomain);
        }
        return myFeature;
    }
    
    // Override Objects's hashCode and equals method.

    /**
     * Overrides the hashcode method.
     * @return the hascode of the AC is returned.
     */
    public int hashCode() {
        return myFeature.getAc().hashCode();
    }

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract. Only returns <tt>true</tt> if the ac
     * for both objects match.
     * @param obj the object to compare.
     */
    public boolean equals(Object obj) {
        // Identical to this?
        if (obj == this) {
            return true;
        }
        if ((obj != null) && (getClass() == obj.getClass())) {
            // Can safely cast it.
            FeatureBean other = (FeatureBean) obj;
            return myFeature.getAc().equals(other.myFeature.getAc());
        }
        return false;
    }

    /**
     * Makes a clone of this object apart for the Feature instance this object
     * is wrapped around.
     *
     * @return a cloned version of the current instance. A null
     */
    public Object clone() throws CloneNotSupportedException {
        FeatureBean copy = (FeatureBean) super.clone();

        // Clone range beans.
        copy.myRanges = new ArrayList(myRanges.size());
        for (Iterator iter = myRanges.iterator(); iter.hasNext();) {
            copy.myRanges.add(((RangeBean) iter.next()).clone());
        }
        return copy;
    }

    // Write methods.

    protected void setShortLabel(String label) {
        myShortLabel = label;
    }

    protected void setFullName(String fullname) {
        myFullName = fullname;
    }

    protected void addRange(RangeBean rb) {
        myRanges.add(rb);
    }

    protected void resetRanges() {
        myRanges.clear();
    }
}
