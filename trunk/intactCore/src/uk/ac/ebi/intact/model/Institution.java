/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.io.Serializable;

/**
 * Represents the contact details for an institution.
 * @author Henning Hermjakob
 * @version $Id$
 */
// TODO cf. note
public class Institution extends IntactObject implements Serializable {

    ///////////////////////////////////////
    //attributes

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
     * TODO comments
     */
    protected String fullName;

    /**
     * TODO comments
     */
    protected String url;


    ///////////////////////////////////////
    // Constructors

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    public Institution (){
        super();
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

    /**
     * Equality for Institutions is currently based on equal shortLabels and fullNames.
     * @param o The object to check
     * @return true if the parameter equlas this object, false otherwise
     */
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Institution)) return false;

        final Institution institution = (Institution) o;

        //need checks as we currently still have a no-arg constructor...
        if(shortLabel != null) {
            if (!shortLabel.equals(institution.shortLabel)) return false;
        }
        else {
           if (institution.shortLabel != null) return false;
        }
        if(fullName != null) {
            return (fullName.equals(institution.fullName));
        }

        return institution.fullName == null;
    }

    /** This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     * @return  hash code of the object.
     */
    public int hashCode(){

        int code = 29;

        //still need shortLabel check as we still have no-arg constructor..
        if(shortLabel != null) code = 29*code + shortLabel.hashCode();
        if (null != fullName)   code = 29 * code + fullName.hashCode();

        return code;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer (64);

        sb.append ("ShortLabel:").append(shortLabel);
        sb.append (" Fullname:").append(fullName);

        return sb.toString();
    }
} // end Institution




