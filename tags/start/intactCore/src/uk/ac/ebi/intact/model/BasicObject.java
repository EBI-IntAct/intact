/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
import java.sql.Timestamp;


/**

 */
public abstract class BasicObject {

    ///////////////////////////////////////
    //attributes


    protected String ownerAc;
    protected String ojbConcreteClass;

    /**
     * The unique accession number of an object.
     */
    private String ac;

    /**
     * Creation date of an object.
     * The type is java.sql.Date, not java.util.Data,
     * for database compatibility.
     */
    protected Timestamp created;

    /**
     * The last update of the object.
     * The type is java.sql.Date, not java.util.Data,
     * for database compatibility.
      */
    protected Timestamp updated;

    ///////////////////////////////////////
    // Constructors
    public BasicObject() {

        this.ojbConcreteClass = this.getClass().getName();
        this.created = new java.sql.Timestamp(System.currentTimeMillis());
        this.updated = new java.sql.Timestamp(System.currentTimeMillis());

    }


    ///////////////////////////////////////
    // associations

    /**
     *
     */
    public Collection evidence = new Vector();
    /**

     */
    public Institution owner;


    ///////////////////////////////////////
    //access methods for attributes

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(java.util.Date created) {

        if(created != null) {

            this.created = new Timestamp(created.getTime());
        }
        else {

            //needed for object-based search
            this.created = null;
        }
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(java.util.Date updated) {

        if(updated != null) {

            this.updated = new Timestamp(updated.getTime());
        }
        else {

            //needed for object-based search
            this.updated = null;
        }
    }

    ///////////////////////////////////////
    // access methods for associations

    public void setEvidence(Collection someEvidence) {
        this.evidence = someEvidence;
    }
    public Collection getEvidence() {
        return evidence;
    }
    public void addEvidence(Evidence evidence) {
        if (!this.evidence.contains(evidence)) this.evidence.add(evidence);
    }

    public void removeEvidence(Evidence evidence) {
        this.evidence.remove(evidence);
    }

    public Institution getOwner() {
        return owner;
    }

    public void setOwner(Institution institution) {
        this.owner = institution;
//        this.ownerAc = institution.getAc();
    }

    public String getOwnerAc() {
        return ownerAc;
    }
    public void setOwnerAc(String ac) {
        this.ownerAc = ac;
    }

    ///////////////////////////////////////
    // instance methods

    public String toString() {
        return this.ac + "; owner=" + this.ownerAc + "\n";
    }

    /** Returns true if the two object have equivalent content.
     *  The definition depends on the object.
     *  For BasicObject this method always returns true, as BasicObject
     *  contains no content attributes.
     *
     */
    public boolean isSameObject(Object comparator){

        return true;
    }

    /** Returns true if an object is in a collection.
     *  The predicate used is isSameObject, which returns true for
     * two objects which have equivalent content.
     *
     */
    public boolean isInCollection (Collection coll){
        if (null == coll){
                return false;
        }
        Iterator i = coll.iterator();
        while(i.hasNext()){
            Object element = i.next();
            if (this.isSameObject((BasicObject) element)){
                return true;
            }
        }
        // The element has not been found in the collection.
        return false;
    }

} // end BasicObject




