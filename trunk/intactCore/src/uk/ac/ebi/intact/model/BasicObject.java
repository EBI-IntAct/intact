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

import java.io.Serializable;

import uk.ac.ebi.intact.util.Utilities;

/**

 */
public abstract class BasicObject implements Serializable {

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

    /**
     * Protected constructor for use by subclasses - used to set
     * the creation data for instances
     */
    protected BasicObject() {

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

    /**
     * This method should not be used by applications, as the AC
     * is a primary key which is auto-generated. If we move
     * to an application server it may then be needed.
     * @param ac
     * @deprecated No replacement - should not be used by applications
     */
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

    /**
     * Returns true if two objects have the same content.
     */
    public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicObject)) return false;

//      By calling Object.equals, we do a reference equality !
//        if (!super.equals(o)) {
//            System.out.println("False sent back by Object");
//            return false;
//        }

        final BasicObject bo = (BasicObject) o;

        return bo.getOwner().equals(owner);
    }

    public int hashCode() {

        return (owner == null ? 0 : owner.hashCode());
    }

} // end BasicObject




