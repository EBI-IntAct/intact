/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.view;

import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserImpl;

import java.io.Serializable;

/**
 * Bean to store data for x'references.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class XreferenceBean implements Serializable {

    /**
     * The unique identifier for this bean.
     */
    private long myKey;

    /**
     * Reference to the Xref object this instance is created with.
     */
    private Xref myXref;

    /**
     * The database name.
     */
    private String myDatabaseName;

    /**
     * The primary id.
     */
    private String myPrimaryId;

    /**
     * The secondary id.
     */
    private String mySecondaryId;

    /**
     * The release number.
     */
    private String myReleaseNumber;

    /**
     * The reference qualifier.
     */
    private String myReferenceQualifer;

    /**
     * Instantiate an object of this class from a Xref object.
     *
     * @param xref the <code>Xref</code> object to construct an
     * instance of this class.
     */
    public XreferenceBean(Xref xref) {
        this(IntactUserImpl.getId(), xref);
    }

    /**
     * Instantiate an object of this class from a Xref object using
     * the supplied key as the primary key (i.e., new primary key is not created).
     * <p>
     * This constructor is visible to subclasses only.
     *
     * @param key the primary key for this new instance.
     * @param xref the <code>Xref</code> object to construct an instance of
     *  this class.
     */
    protected XreferenceBean(long key, Xref xref) {
        myKey = key;
        myXref = xref;
        myDatabaseName = xref.cvDatabase.getShortLabel();
        myPrimaryId = xref.getPrimaryId();
        mySecondaryId = xref.getSecondaryId();
        myReleaseNumber = xref.getDbRelease();

        myReferenceQualifer = "";
        CvXrefQualifier qualifier = xref.getCvXrefQualifier();
        if (qualifier != null) {
            myReferenceQualifer = qualifier.getShortLabel();
        }
    }

    /**
     * Return the key for this object.
     */
    public long getKey() {
        return myKey;
    }

    /**
     * Returns the reference to the Xref object this instance is created with.
     */
    public Xref getXref() {
        return myXref;
    }

    /**
     * Return the database name.
     */
    public String getDatabase() {
        return myDatabaseName;
    }

    /**
     * Sets the database name.
     *
     * @param dbname the name of the database.
     */
    public void setDatabase(String dbname) {
        myDatabaseName = dbname;
    }

    /**
     * Return the primary id.
     */
    public String getPrimaryId() {
        return myPrimaryId;
    }

    /**
     * Return the secondary id.
     */
    public String getSecondaryId() {
        return mySecondaryId;
    }

    /**
     * Return the release number.
     */
    public String getReleaseNumber() {
        return myReleaseNumber;
    }

    /**
     * Return the reference qualifier.
     */
    public String getQualifier() {
        return myReferenceQualifer;
    }

    // Override Objects's equal method.

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract. Only returns <tt>true</tt> if the internal
     * keys for both objects match. Made it final to allow slice comparision
     * without violating transitivity law for equals() method.
     *
     * @param obj the object to compare.
     */
    public final boolean equals(Object obj) {
        // Identical to this?
        if (obj == this) {
            return true;
        }
        // Allow for slice comparision.
        if ((obj != null) && (obj instanceof XreferenceBean)) {
            return myKey == ((XreferenceBean) obj).getKey();
        }
        return false;
    }
}
