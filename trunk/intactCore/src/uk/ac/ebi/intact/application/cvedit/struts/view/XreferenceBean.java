/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.view;

import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.CvXrefQualifier;

/**
 * Bean to store data for x'references.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class XreferenceBean {

    /**
     * Reference to the Xref object this instance is created with.
     */
    private Xref myXref;

    /**
     * Stores the state of the transaction when this bean was added. This
     * state in only set during the construction of an instance of this class.
     */
    private boolean myInTransaction;

    /**
     * The accession number.
     */
    private String myAc;

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
     * Instantiate an object of this class from a Xref object. The
     * bean is not involved in a transaction.
     *
     * @param annot the <code>Xref</code> object to construct an
     * instance of this class.
     */
    public XreferenceBean(Xref xref) {
        this(xref, false);
    }

    /**
     * Instantiate an object of this class from a Xref object with
     * the transaction state when the bean was added.
     *
     * @param xref the <code>Xref</code> object to construct an
     * instance of this class.
     * @param state the state of the transaction when bean was added.
     */
    public XreferenceBean(Xref xref, boolean state) {
        myXref = xref;
        myInTransaction = state;
        myAc = xref.getAc();
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
     * Returns the reference to the Xref object this instance is created with.
     */
    public Xref getXref() {
        return myXref;
    }

    /**
     * Returns true if this object is in a transaction.
     */
    public boolean inTransaction() {
        return myInTransaction;
    }

    /**
     * Returns the accession number.
     */
    public String getAc() {
        return myAc;
    }

    /**
     * Sets the accession number.
     *
     * @param the accession number to set.
     */
    public void setAc(String ac) {
        myAc = ac;
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
     * Java's equals() contract. Only returns <tt>true</tt> if the accesssion
     * numbers match for a similar object type.
     *
     * @param obj the object to compare.
     */
    public boolean equals(Object obj) {
        // Identical to this?
        if (obj == this) {
            return true;
        }
        if ((obj != null) && (obj.getClass() == getClass())) {
            // Can safely cast it.
            XreferenceBean other = (XreferenceBean) obj;
            // Accession numbers must match.
            return myAc.equals(other.getAc());
        }
        return false;
    }
}
