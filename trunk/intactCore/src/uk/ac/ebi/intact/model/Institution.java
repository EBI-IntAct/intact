/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.util.Utilities;

import java.util.Date;
import java.sql.Timestamp;
import java.io.Serializable;

/**
 * Represents the contact details for an institution.
 * @author Henning Hermjakob
 */
public class Institution implements Serializable {

    ///////////////////////////////////////
    //attributes

    /**
     * The unique accession number of an object.
     */
    protected String ac;

    /**
     * Creation date of an object.
     */
    protected Timestamp created;

    /**
     * The last update of the object.
     */
    protected Timestamp updated;


    /**
     * The name of the institution.
     */
    protected String shortLabel;

    /**
     * Postal address.
     * Format: One string with line breaks.
     */
    protected String postalAddress;

    /**

     */
    protected String fullName;

    /**

     */
    protected String url;


    ///////////////////////////////////////
    // Constructors

    /**
     * no-arg constructor. Hope to replace with a private one as it should
     * not be used by applications because it will result in objects with invalid
     * states.
     */
    public Institution (){
        //this.created = new java.util.Date(System.currentTimeMillis());
        //this.updated = new java.util.Date(System.currentTimeMillis());

        //this to be moved to the proper constructor - onley here for code compatibility
        //in the short term...
        this.created = new java.sql.Timestamp(System.currentTimeMillis());
        this.updated = new java.sql.Timestamp(System.currentTimeMillis());
    }

    /**
     * This constructor ensures creation of a valid Institution. Specifically
     * it must have at least a shortLabel defined since this is indexed in persistent store.
     * Note that a side-effect of this constructor is to set the <code>created</code> and
     * <code>updated</code> fields of the instance to the current time.
     * @param shortLabel The short label used to refer to this Institution.
     * @exception NullPointerException if an attempt is made to create an Instiution without
     * defining a shortLabel.
     */
    public Institution (String shortLabel){
        this();
        if(shortLabel == null) throw new NullPointerException("Must define a short label to create an Institution!");
        this.shortLabel = shortLabel;
    }


    ///////////////////////////////////////
    // access methods for attributes

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    /*
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    */

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


    public String getShortLabel() {
        return shortLabel;
    }

    public void setShortLabel(String shortLabel) {
        this.shortLabel = shortLabel;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    ///////////////////////////////////////
    // instance methods

    /** Returns true if the "important" attributes are equal.
     */
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Institution)) return false;
        if (!super.equals(o)) return false;

        final Institution institution = (Institution) o;

        return (Utilities.equals(this.shortLabel, institution.getShortLabel()) &&
                Utilities.equals(this.fullName,   institution.getFullName()));
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
        return this.ac + "\n";
    }
} // end Institution




