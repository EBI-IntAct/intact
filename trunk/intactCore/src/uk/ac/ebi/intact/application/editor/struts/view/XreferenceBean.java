/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view;

import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.application.editor.business.EditUser;

import java.io.Serializable;

/**
 * Bean to store data for x'references.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class XreferenceBean extends EditBean implements Serializable {

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
        myKey = EditUser.getId();
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
     * Returns the database with a link to show its contents in a window.
     * @return the database as a browsable link.
     */
    public String getDatabaseLink() {
        return getLink("CvDatabase", myDatabaseName);
    }

    /**
     * Sets the database name.
     *
     * @param dbname the name of the database.
     */
    public void setDatabase(String dbname) {
        myDatabaseName = dbname.trim();
    }

    /**
     * Return the primary id.
     */
    public String getPrimaryId() {
        return myPrimaryId;
    }

    /**
     * Sets the primary id.
     * @param primaryId the primary id as a <code>String</code>.
     */
    public void setPrimaryId(String primaryId) {
        myPrimaryId = primaryId;
    }

    /**
     * Return the secondary id.
     */
    public String getSecondaryId() {
        return mySecondaryId;
    }

    /**
     * Sets the secondary id.
     * @param secondaryId the primary id as a <code>String</code>.
     */
    public void setSecondaryId(String secondaryId) {
        mySecondaryId = secondaryId;
    }

    /**
     * Return the release number.
     */
    public String getReleaseNumber() {
        return myReleaseNumber;
    }

    /**
     * Sets the release number.
     * @param releaseNumber the release number as a <code>String</code>.
     */
    public void setReleaseNumber(String releaseNumber) {
        myReleaseNumber = releaseNumber;
    }

    /**
     * Return the reference qualifier.
     */
    public String getQualifier() {
        return myReferenceQualifer;
    }

    /**
     * Returns the qualifier with a link to show its contents in a window.
     * @return the qualifier as a browsable link.
     */
    public String getQualifierLink() {
        return getLink("CvXrefQualifier", myReferenceQualifer);
    }

    /**
     * Sets the reference qualifier.
     * @param refQualifier the reference qaulifier as a <code>String</code>.
     */
    public void setQualifier(String refQualifier) {
        String qualifier = refQualifier.trim();
        // Avoid empty topic list indicator
//        if (!qualifier.equals(EditUserI.EMPTY_LIST_ITEM)) {
//            myReferenceQualifer = qualifier;
//        }
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
