/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

import uk.ac.ebi.intact.util.Utilities;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

/**
 * Represents an object with biological annotation.
 *
 * @author hhe
 */
public abstract class AnnotatedObject extends BasicObject {

    /**
     * Cache a Vector of all shortLabels of the class, e.g. for menus.
     *
     */
    protected static Vector menuList = null;

    /////////////////////////////////
    //attributes


    //attributes used for mapping BasicObjects - project synchron
    protected String curatorAc;

    /**
     * Short name for the object, not necessarily unique. To be used for example
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
    public Collection reference = new Vector();

    // Class methods

    /** Return a Vector of all shortLabels of the class, e.g. for menus.
     *
     * @param helper Database access object
     * @param forceUpdate If true, an update of the list is forced.
     *
     * @return Vector of Strings. Each string one shortlabel.
     */
    public static Vector getMenuList(Class targetClass, IntactHelper helper, boolean forceUpdate)
            throws IntactException {
        if ((menuList == null) || forceUpdate){
            menuList = new Vector();
            // get all elements of the class
            Collection allElements = helper.search(targetClass.getName(),"ac","*");
            // save all shortLabels
            for (Iterator i = allElements.iterator(); i.hasNext();) {
                menuList.add(((AnnotatedObject) i.next()).getShortLabel());
            }
        }
        return menuList;
    }

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
    public void setAnnotation(Collection someAnnotation) {
        this.annotation = someAnnotation;
    }
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
    public void setXref(Collection someXref) {
        this.xref = someXref;
    }
    public Collection getXref() {
        return xref;
    }

    /**
     * Adds an xref to the object. The xref will only be added
     * if an equivalent xref is not yet part of the object.
     *
     */
    public void addXref(Xref aXref) {
        if (! this.xref.contains(aXref)) {
            this.xref.add(aXref);
            aXref.parentAc = this.getAc();
        }
    }
    public void removeXref(Xref xref) {
        this.xref.remove(xref);
    }
     public void setReference(Collection someReference) {
        this.reference = someReference;
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

    //attributes used for mapping BasicObjects - project synchron
    public String getCuratorAc() {
        return this.curatorAc;
    }
    public void setCuratorAc(String ac) {
        this.curatorAc = ac;
    }


    ///////////////////////////////////////
    // instance methods

    /** Update an annotated object in the database.
     * Ensure subobjects are updated appropriately.
     */
    public AnnotatedObject update(IntactHelper helper) throws IntactException {

        // Update dependent objects
        for (Iterator i = xref.iterator(); i.hasNext();) {
            Xref xref = (Xref) i.next();
            helper.update(xref);
        }
        for (Iterator i = annotation.iterator(); i.hasNext();) {
            Annotation annotation = (Annotation) i.next();
            helper.update(annotation);
        }

        helper.update(this);

        return this;
    }

    /** Create or update an annotation. The anntation topic may only occur once in the object.
     *
     */
    public void updateUniqueAnnotation(CvTopic topic, String description, Institution owner){

        Annotation annotation = null;
         for (Iterator iterator = getAnnotation().iterator(); iterator.hasNext();) {
             Annotation a = (Annotation) iterator.next();
             if (a.getCvTopic() == topic){
                 annotation = a;
                 break;
             }
         }
         if (null == annotation){
             annotation = new Annotation();
             annotation.setOwner(owner);
             annotation.setCvTopic(topic);
             addAnnotation(annotation);
         }
         // Now annotation is a valid object, (re-) set the text
         if (annotation.getAnnotationText() != description){
             annotation.setAnnotationText(description);
         }
    }

    /** Returns true if the "important" attributes are equal.
     */
    public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof AnnotatedObject)) return false;
        if (!super.equals(o)) return false;

        final AnnotatedObject annotatedObject = (AnnotatedObject) o;

        return (Utilities.equals (this.shortLabel, annotatedObject.getShortLabel()) &&
                Utilities.equals (this.fullName, annotatedObject.getFullName()));
    }

    /** This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     * @return  hash code of the object.
     */
    public int hashCode(){

        int code = super.hashCode();

        if (null != shortLabel) code = 29 * code + shortLabel.hashCode();
        if (null != fullName)   code = 29 * code + fullName.hashCode();

        return code;
    }

    public String toString() {
        return this.getAc() + "; owner=" + this.ownerAc
                + "; name=" + this.shortLabel + "\n";
    }

} // end AnnotatedObject




