/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

/**
 * Represents an object with biological annotation.
 *
 * @author hhe
 */
public abstract class AnnotatedObjectImpl extends BasicObjectImpl implements AnnotatedObject {


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
    public Collection annotations = new ArrayList();

    /**
     * The curator who has last edited the object.
     */
    public Person curator;

    /**
     *
     */
    public Collection xrefs = new ArrayList();

    /**
     * Hold aliases of an Annotated object.
     * ie. alternative name for the current object.
     */
    private Collection aliases = new ArrayList();

    /**
     *
     */
    public Collection references = new ArrayList();

    /**
     * no-arg constructor provided for compatibility with subclasses
     * that have no-arg constructors.
     */
    protected AnnotatedObjectImpl() {
        //super call sets creation time data
        super();
    }

    /**
     * Constructor for subclass use only. Ensures that AnnotatedObjects cannot be
     * created without at least a shortLabel and an owner specified.
     * @param shortLabel The memorable label to identify this AnnotatedObject
     * @param owner The Institution which owns this AnnotatedObject
     * @exception NullPointerException thrown if either parameters are not specified
     */
    protected AnnotatedObjectImpl(String shortLabel, Institution owner) {

        //super call sets creation time data
        super();
        if(shortLabel == null) throw new NullPointerException("Must define a short label for a " +getClass().getName());
        if(owner == null) throw new NullPointerException("valid " +getClass().getName()+" must have an owner (Institution)!");
        this.shortLabel = shortLabel;
        setOwner(owner);
    }
    // Class methods

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
        this.annotations = someAnnotation;
    }
    public Collection getAnnotations() {
        return annotations;
    }
    public void addAnnotation(Annotation annotation) {
        if (! this.annotations.contains(annotation)) this.annotations.add(annotation);
    }
    public void removeAnnotation(Annotation annotation) {
        this.annotations.remove(annotation);
    }
    public Person getCurator() {
        return curator;
    }
    public void setCurator(Person person) {
        this.curator = person;
    }


    ///////////////////
    // Xref related
    ///////////////////
    public void setXrefs(Collection someXrefs) {
        this.xrefs = someXrefs;
    }
    public Collection getXrefs() {
        return xrefs;
    }
    /**
     * Adds an xref to the object. The xref will only be added
     * if an equivalent xref is not yet part of the object.
     *
     */
    public void addXref(Xref aXref) {
        if (! this.xrefs.contains(aXref)) {
            this.xrefs.add(aXref);
            aXref.setParentAc(this.getAc());
        }
    }
    public void removeXref(Xref xref) {
        this.xrefs.remove(xref);
    }


    ///////////////////
    // Alias related
    ///////////////////
    public void setAliases(Collection someAliases) {
        this.aliases = someAliases;
    }
    public Collection getAliases() {
        return aliases;
    }
    /**
     * Adds an alias to the object. The alis will only be added
     * if an equivalent alias is not yet part of the object.
     *
     */
    public void addAlias( Alias alias ) {
        if (! this.aliases.contains( alias )) {
            this.aliases.add( alias );
            alias.setParentAc( this.getAc() );
        }
    }
    public void removeAlias( Alias alias ) {
        this.aliases.remove( alias );
    }



     public void setReferences(Collection someReferences) {
        this.references = someReferences;
    }
    public Collection getReferences() {
        return references;
    }
    public void addReference(Reference reference) {
        if (! this.references.contains(reference)) {
            this.references.add(reference);
            reference.addAnnotatedObject(this);
        }
    }
    public void removeReference(Reference reference) {
        boolean removed = this.references.remove(reference);
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
        for (Iterator i = xrefs.iterator(); i.hasNext();) {
            Xref xref = (Xref) i.next();

            helper.update(xref);
        }
        for (Iterator i = annotations.iterator(); i.hasNext();) {
            Annotation annotation = (Annotation) i.next();
            helper.update(annotation);
        }

        helper.update(this);

        return this;
    }

    /** Create or update an annotation. The anntation topic may only occur once in the object.
     *
     */
    public Annotation updateUniqueAnnotation(CvTopic topic, String description, Institution owner){

        Annotation annotation = null;
         for (Iterator iterator = getAnnotations().iterator(); iterator.hasNext();) {
             Annotation a = (Annotation) iterator.next();
             if (a.getCvTopic().equals(topic)){
                 annotation = a;
                 break;
             }
         }
         if (null == annotation){
             annotation = new Annotation(owner, topic);
             addAnnotation(annotation);
         }
         // Now annotation is a valid object, (re-) set the text
	 annotation.setAnnotationText(description);
         
	 return annotation;
    }

    /**
     * Equality for AnnotatedObjects is currently based on equality for
     * <code>Xrefs</code>, shortLabels and fullNames.
     * @see uk.ac.ebi.intact.model.Xref
     * @param o The object to check
     * @return true if the parameter equals this object, false otherwise
     */
    public boolean equals (Object o) {
        // TODO: the reviewed version of the intact model will provide a better implementation
        if (this == o) return true;
        if (!(o instanceof AnnotatedObject)) return false;

        //YUK! This ends up calling the java Object 'equals', which compares
        //on identity - NOT what we want....
//        if (!super.equals(o)) {
//            return false;
//        }

        final AnnotatedObject annotatedObject = (AnnotatedObject) o;

        //short label and full name (if it exists) must be equal also..
        if(shortLabel != null) {
            if (!shortLabel.equals(annotatedObject.getShortLabel())) return false;
        }
        else {
            if (annotatedObject.getShortLabel() != null) return false;
        }

        if(fullName != null) {
            if (!fullName.equals(annotatedObject.getFullName())) return false;
        }
        else {
            if(annotatedObject.getFullName() != null) return false;;
        }

        Iterator e1 = xrefs.iterator();
        Iterator e2 = annotatedObject.getXrefs().iterator();

        while(e1.hasNext() && e2.hasNext()) {
            Xref o1 = (Xref) e1.next();
            Xref o2 = (Xref) e2.next();
            if (!(o1==null ? o2==null : o1.equals(o2)))
                return false;
        }

        return true;

    }

    /** This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     * @return  hash code of the object.
     */
    public int hashCode(){

        //YUK AGAIN! ends up as a call to Object's hashcode, which is
        //based on indentity. Not require here...
        //int code = super.hashCode();
        int code = 29;

        for (Iterator iterator = xrefs.iterator(); iterator.hasNext();) {
            Xref xref = (Xref) iterator.next();
            code = 29 * code + xref.hashCode();
        }

        if (null != shortLabel) code = 29 * code + shortLabel.hashCode();
        if (null != fullName)   code = 29 * code + fullName.hashCode();

        return code;
    }

    public String toString() {
        return this.getAc() + "; owner=" + this.getOwner().getAc()
                + "; name=" + this.shortLabel + "; fullname=" + fullName;
    }

} // end AnnotatedObject




