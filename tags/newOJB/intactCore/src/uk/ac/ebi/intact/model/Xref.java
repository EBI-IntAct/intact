/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * Represents a crossreference to another database.
 *
 * @author hhe
 * @version $Id$
 */
public class Xref extends BasicObjectImpl {

    ///////////////////////////////////////
    //attributes

    // TODO: find out if these two fields are used or not.
//    private String qualifierAc;
//    protected String databaseAc;

    /**
     * Ac of the object which holds that Xref
     */
    private String parentAc;

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    protected String cvXrefQualifierAc;
    protected String cvDatabaseAc;


    /**
     * Primary identifier of the database referred to.
     */
    private String primaryId;

    /**
     * Secondary identifier of the database. This will usually be
     * a meaningful name, for example a domain name.
     */
    private String secondaryId;

    /**
     * The release number of the external database from which the object
     * has been updated.
     */
    private String dbRelease;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private CvXrefQualifier cvXrefQualifier;

    /**
     * TODO comments
     */
    private CvDatabase cvDatabase;


    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    private Xref() {
        //super call sets creation time data
        super();
    }


    /**
     * Creates a valid Xref instance. Items which must be defined are:
     * <ul>
     * <li>an owner (Institution)</li>
     * <li>database details (controlled vocabulary instance)</li>
     * <li>a Primary ID</li>
     * @param anOwner Owner of the cross-reference (non-null)
     * @param aDatabase Controlled vocabulary instance defining the database details (non-null)
     * @param aPrimaryId primary identifier for the cross-reference (non-null), this should be 30 characters maximum
     * if it's more it will be truncated. if not done, Oracle would throw an error.
     * @param aSecondaryId secondary identifier (eg a domain name), this should be 30 characters maximum
     * if it's more it will be truncated. if not done, Oracle would throw an error.
     * @param aDatabaseRelease database version
     * @param aCvXrefQualifier controlled vocabulary for any qualifiers (may be null)
     * @exception NullPointerException thrown if any mandatory parameters are not specified
     */
    public Xref (Institution anOwner,
                 CvDatabase aDatabase,
                 String aPrimaryId,
                 String aSecondaryId,
                 String aDatabaseRelease,
                 CvXrefQualifier aCvXrefQualifier) {

        //super call sets creation time data
        super(anOwner);
        if(aDatabase == null) throw new NullPointerException("valid Xref must have non-null database details!");
        if(aPrimaryId == null) throw new NullPointerException("valid Xref must have a primary ID!");


        if (aPrimaryId != null && aPrimaryId.length() > 30) {
            aPrimaryId = aPrimaryId.substring(0,30);
        }

        if (aPrimaryId != null && aPrimaryId.length() > 30) {
            aPrimaryId = aPrimaryId.substring(0,30);
        }

        this.primaryId = aPrimaryId;


        if (aSecondaryId != null && aSecondaryId.length() > 30) {
            aSecondaryId = aSecondaryId.substring(0,30);
        }
        this.cvDatabase = aDatabase;

        if (aSecondaryId != null && aSecondaryId.length() > 30) {
            aSecondaryId = aSecondaryId.substring(0,30);
        }
        this.cvDatabase = aDatabase;

        this.secondaryId = aSecondaryId;
        this.dbRelease = aDatabaseRelease;
        this.cvXrefQualifier = aCvXrefQualifier;
    }


    ///////////////////////////////////////
    //access methods for attributes

    public String getPrimaryId() {
        return primaryId;
    }
    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
    }
    public String getSecondaryId() {
        return secondaryId;
    }
    public void setSecondaryId(String secondaryId) {
        this.secondaryId = secondaryId;
    }
    public String getDbRelease() {
        return dbRelease;
    }
    public void setDbRelease(String dbRelease) {
        this.dbRelease = dbRelease;
    }
    public String getParentAc() {
        return parentAc;
    }
    public void setParentAc(String parentAc) {
        this.parentAc = parentAc;
    }

    ///////////////////////////////////////
    // access methods for associations

    public CvXrefQualifier getCvXrefQualifier() {
        return cvXrefQualifier;
    }

    public void setCvXrefQualifier(CvXrefQualifier cvXrefQualifier) {
        this.cvXrefQualifier = cvXrefQualifier;
    }
    public CvDatabase getCvDatabase() {
        return cvDatabase;
    }

    public void setCvDatabase(CvDatabase cvDatabase) {
        this.cvDatabase = cvDatabase;
    }

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String getCvXrefQualifierAc() {
        return cvXrefQualifierAc;
    }
    public void setCvXrefQualifierAc(String ac) {
        this.cvXrefQualifierAc = ac;
    }

    public String getCvDatabaseAc() {
        return cvDatabaseAc;
    }
    public void setCvDatabaseAc(String ac) {
        this.cvDatabaseAc = ac;
    }


    ///////////////////////////////////////
    // instance methods

    /**
     * Equality for Xrefs is currently based on equality for
     * <code>CvDatabases</code> and primaryIds.
     * @see uk.ac.ebi.intact.model.CvDatabase
     * @param o The object to check
     * @return true if the parameter equals this object, false otherwise
     */
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Xref)) return false;

        final Xref xref = (Xref) o;

        if(cvDatabase != null) {
             if (!cvDatabase.equals(xref.cvDatabase)) return false;
        }
        else {
            if (xref.cvDatabase != null) return false;
        }

        if(primaryId != null) {
             return (primaryId.equals(xref.primaryId));
        }
        return (xref.primaryId == null);
    }

    /** This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     * @return  hash code of the object.
     */
    public int hashCode(){
        int code = 29;
        if(cvDatabase != null) code = 29*code + cvDatabase.hashCode();
        if(primaryId != null) code = 29 * code + primaryId.hashCode();

        return code;
    }

    public String toString(){
        return " Xref: " + getAc()
                + "; Owner: " + getOwner().getShortLabel()
                + "; DB: " + cvDatabase.getShortLabel()
                + "; PrimaryId: "+ primaryId;
    }
} // end Xref




