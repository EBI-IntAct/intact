/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.view;

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
     * Construts an object of this class with data from given
     * <code>Xref</code>.
     *
     * @param xref the object to construct this class.
     */
    public XreferenceBean(Xref xref) {
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
     * Constructs an instance of this class with values provided in an array.
     * This mainly used by forms to add a new xreference object.
     *
     * @param array list of values to construct an instance of this class. The
     * array must conform to the following format. The index 0 holds the database
     * name, index 1 is the primary id, index 2 is the secondary id, index 3
     * is the release number and the final position contains reference qualifier.
     *
     * <pre>
     * pre: array.length = 5
     * </pre>
     */
    public XreferenceBean(String[] array) {
        myDatabaseName = array[0];
        myPrimaryId = array[1];
        mySecondaryId = array[2];
        myReleaseNumber = array[3];
        myReferenceQualifer = array[4];
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
