/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * Represents an object with biological annotation.
 *
 * @author hhe
 */
public abstract class AnnotatedObject extends BasicObject {

  ///////////////////////////////////////
  //attributes


/**
 * Short name for the object, not necessarily unique. To be used e.g.
 * in minimised displays of the object.
 */
    protected String shortLabel;

/**
 * The full name or a minimal description of the object.
 */
    protected String fullName;

   ///////////////////////////////////////
   // associations

/**
 *
 */
    public Collection annotation = new Vector();
/**
 * The curator who has last edited the object.
 */
    public Person curator;
/**
 *
 */
    public Collection xref = new Vector();
/**
 *
 */
    public Collection referenceQualifier = new Vector();
/**
 *
 */
    public Collection reference = new Vector();


  ///////////////////////////////////////
  //access methods for attributes

    public String getShortLabel() {
        return shortLabel;
    }
    public void setShortLabel(String shortLabel) {
        this.shortLabel = shortLabel;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

   ///////////////////////////////////////
   // access methods for associations

    public Collection getAnnotation() {
        return annotation;
    }
    public void addAnnotation(Annotation annotation) {
        if (! this.annotation.contains(annotation)) this.annotation.add(annotation);
    }
    public void removeAnnotation(Annotation annotation) {
        this.annotation.remove(annotation);
    }
    public Person getCurator() {
        return curator;
    }

    public void setCurator(Person person) {
        this.curator = person;
    }
    public Collection getXref() {
        return xref;
    }

    /**
     * Adds an xref to the object. The xref will only be added
     * if an equivalent xref is not yet part of the object.
     */
    public void addXref(Xref aXref) {
        if (! aXref.isInCollection(this.xref)) {
            this.xref.add(aXref);
            aXref.parentAc = this.getAc();
        }
    }
    public void removeXref(Xref xref) {
        this.xref.remove(xref);
    }
    public Collection getReferenceQualifier() {
        return referenceQualifier;
    }
    public void addReferenceQualifier(ReferenceQualifier referenceQualifier) {
        if (! this.referenceQualifier.contains(referenceQualifier)) this.referenceQualifier.add(referenceQualifier);
    }
    public void removeReferenceQualifier(ReferenceQualifier referenceQualifier) {
        this.referenceQualifier.remove(referenceQualifier);
    }
    public Collection getReference() {
        return reference;
    }
    public void addReference(Reference reference) {
        if (! this.reference.contains(reference)) {
            this.reference.add(reference);
            reference.addAnnotatedObject(this);
        }
    }
    public void removeReference(Reference reference) {
        boolean removed = this.reference.remove(reference);
        if (removed) reference.removeAnnotatedObject(this);
    }

    ///////////////////////////////////////
    // instance methods

    public String toString() {
        return this.getAc() + "; owner=" + this.ownerAc 
	    + "; name=" + this.shortLabel + "\n";
    }



} // end AnnotatedObject




