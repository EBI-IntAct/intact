/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**

 */
public class Feature extends BasicObject {

  ///////////////////////////////////////
  //attributes


/**
 * References a description of a domain in an external database.
 */
    protected Xref xref;

   ///////////////////////////////////////
   // associations

/**
 * 
 */
    public CvFeatureType cvFeatureType;
/**
 * 
 */
    public Protein protein;
/**
 * The Substrate a domain belongs to.
 */
    public Component component;
/**
 * The domain the current domain binds to.
 */
    public Feature boundDomain;
/**
 * 
 */
    public Collection range = new Vector();
/**
 * 
 */
    public CvFeatureIdentification cvFeatureIdentification;


  ///////////////////////////////////////
  //access methods for attributes

    public Xref getXref() {
        return xref;
    }
    public void setXref(Xref xref) {
        this.xref = xref;
    }

   ///////////////////////////////////////
   // access methods for associations

    public CvFeatureType getCvFeatureType() {
        return cvFeatureType;
    }

    public void setCvFeatureType(CvFeatureType cvFeatureType) {
        this.cvFeatureType = cvFeatureType;
    } 
    public Protein getProtein() {
        return protein;
    }

    public void setProtein(Protein protein) {
        if (this.protein != protein) {
            if (this.protein != null) this.protein.removeFeature(this);     
            this.protein = protein;
            if (protein != null) protein.addFeature(this);  
        }
    } 
    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        if (this.component != component) {
            if (this.component != null) this.component.removeBindingDomain(this);     
            this.component = component;
            if (component != null) component.addBindingDomain(this);  
        }
    } 
    public Feature getBoundDomain() {
        return boundDomain;
    }

    public void setBoundDomain(Feature feature) {
        this.boundDomain = feature;
    } 
    public Collection getRange() {
        return range;
    }
    public void addRange(Range range) {
        if (! this.range.contains(range)) this.range.add(range);  
    }
    public void removeRange(Range range) {    
        this.range.remove(range);        
    }
    public CvFeatureIdentification getCvFeatureIdentification() {
        return cvFeatureIdentification;
    }

    public void setCvFeatureIdentification(CvFeatureIdentification cvFeatureIdentification) {
        this.cvFeatureIdentification = cvFeatureIdentification;
    } 

} // end Feature




