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
     *
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
     *
     * @param anOwner          Owner of the cross-reference (non-null)
     * @param aDatabase        Controlled vocabulary instance defining the database details (non-null)
     * @param aPrimaryId       primary identifier for the cross-reference (non-null), this should be 30 characters maximum
     *                         if it's more it will be truncated. if not done, Oracle would throw an error.
     * @param aSecondaryId     secondary identifier (eg a domain name), this should be 30 characters maximum
     *                         if it's more it will be truncated. if not done, Oracle would throw an error.
     * @param aDatabaseRelease database version
     * @param aCvXrefQualifier controlled vocabulary for any qualifiers (may be null)
     * @throws NullPointerException thrown if any mandatory parameters are not specified
     */
    public Xref( Institution anOwner,
                 CvDatabase aDatabase,
                 String aPrimaryId,
                 String aSecondaryId,
                 String aDatabaseRelease,
                 CvXrefQualifier aCvXrefQualifier ) {

        //super call sets creation time data
        super( anOwner );

        setPrimaryId( aPrimaryId );
        setSecondaryId( aSecondaryId );
        setCvDatabase( aDatabase );

        setDbRelease( aDatabaseRelease );
        setCvXrefQualifier( aCvXrefQualifier );
    }

    
    ///////////////////////////////////////
    //access methods for attributes

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId( String aPrimaryId ) {
        if( aPrimaryId == null ) throw new NullPointerException( "valid Xref must have a primary ID!" );


        if( aPrimaryId != null && aPrimaryId.length() > 30 ) {
            aPrimaryId = aPrimaryId.substring( 0, 30 );
        }
        this.primaryId = aPrimaryId;
    }

    public String getSecondaryId() {
        return secondaryId;
    }

    public void setSecondaryId( String aSecondaryId ) {
        if( aSecondaryId != null && aSecondaryId.length() > 30 ) {
            aSecondaryId = aSecondaryId.substring( 0, 30 );
        }
        this.secondaryId = aSecondaryId;
    }

    public String getDbRelease() {
        return dbRelease;
    }

    public void setDbRelease( String aDbRelease ) {
        this.dbRelease = aDbRelease;
    }

    public String getParentAc() {
        return parentAc;
    }

    public void setParentAc( String parentAc ) {
        this.parentAc = parentAc;
    }

    ///////////////////////////////////////
    // access methods for associations

    public CvXrefQualifier getCvXrefQualifier() {
        return cvXrefQualifier;
    }

    public void setCvXrefQualifier( CvXrefQualifier cvXrefQualifier ) {
        this.cvXrefQualifier = cvXrefQualifier;
    }

    public CvDatabase getCvDatabase() {
        return cvDatabase;
    }

    public void setCvDatabase( CvDatabase cvDatabase ) {
        if( cvDatabase == null ) throw new NullPointerException( "valid Xref must have non-null database details!" );
        this.cvDatabase = cvDatabase;
    }

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String getCvXrefQualifierAc() {
        return cvXrefQualifierAc;
    }

    public void setCvXrefQualifierAc( String ac ) {
        this.cvXrefQualifierAc = ac;
    }

    public String getCvDatabaseAc() {
        return cvDatabaseAc;
    }

    public void setCvDatabaseAc( String ac ) {
        this.cvDatabaseAc = ac;
    }


    ///////////////////////////////////////
    // instance methods

    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( !( o instanceof Xref ) ) return false;

        final Xref xref = (Xref) o;

        if( !primaryId.equals( xref.primaryId ) ) return false;
        if( !cvDatabase.equals( xref.cvDatabase ) ) return false;

        if( cvXrefQualifier != null ? !cvXrefQualifier.equals( xref.cvXrefQualifier ) : xref.cvXrefQualifier != null ) return false;
        if( dbRelease != null ? !dbRelease.equals( xref.dbRelease ) : xref.dbRelease != null ) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = primaryId.hashCode();
        result = 29 * result + cvDatabase.hashCode();
        result = 29 * result + ( cvXrefQualifier != null ? cvXrefQualifier.hashCode() : 0 );
        result = 29 * result + ( dbRelease != null ? dbRelease.hashCode() : 0 );
        return result;
    }

    public String toString() {
        return " Xref: " + getAc()
               + "; Owner: " + getOwner().getShortLabel()
               + "; DB: " + cvDatabase.getShortLabel()
               + "; PrimaryId: " + primaryId;
    }

    /**
     * Returns a cloned version of the current object.
     *
     * @return a cloned version of the current Range. The Cv database and
     *         xref qualifier are not cloned (shared). The parent AC is set to null.
     * @throws CloneNotSupportedException for errors in cloning this object.
     */
    public Object clone() throws CloneNotSupportedException {
        Xref copy = (Xref) super.clone();
        // Reset the parent ac.
        copy.parentAc = null;
        return copy;
    }

} // end Xref




