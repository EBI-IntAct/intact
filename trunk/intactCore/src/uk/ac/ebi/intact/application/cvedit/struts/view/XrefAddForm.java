/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.view;

import org.apache.struts.action.ActionForm;

import java.util.Collection;

import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseForm;

/**
 * The form for adding a cross reference.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class XrefAddForm extends IntactBaseForm {

    /**
     * The database name.
     */
    private String myDatabase;

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
    private String myRefQualifier;

    /**
     * Returns the database name.
     */
    public String getDatabase() {
        return myDatabase;
    }

    /**
     * Sets the database name
     *
     * @param db the database name.
     */
    public void setDatabase(String db) {
        myDatabase = db;
    }

    /**
     * Return a collection of database names.
     *
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(String))
     * </pre>
     */
//    public Collection getDbNames() {
//        return super.getList(IntactUserIF.DB_NAMES);
//    }

    /**
     * Return the primary id.
     */
    public String getPrimaryId() {
        return myPrimaryId;
    }

    /**
     * Sets the primary id.
     *
     * @param pid sets the primary id.
     */
    public void setPrimaryId(String pid) {
        myPrimaryId = pid;
    }

    /**
     * Returns the secondary id.
     */
    public String getSecondaryId() {
        return mySecondaryId;
    }

    /**
     * Sets the secondary id.
     *
     * @param sid the secondary id to set.
     */
    public void setSecondaryId(String sid) {
        mySecondaryId = sid;
    }

    /**
     * Returns the release number.
     */
    public String getReleaseNumber() {
        return myReleaseNumber;
    }

    /**
     * Sets the release number.
     *
     * @param relnum the release number to set.
     */
    public void setReleaseNumber(String relnum) {
        myReleaseNumber = relnum;
    }

    /**
     * Returns the reference qualifer.
     */
    public String getQualifer() {
        return myRefQualifier;
    }

    /**
     * Sets the reference qualifier.
     *
     * @param qualifier the reference qualifier to set.
     */
    public void setQualifier(String qualifier) {
        myRefQualifier = qualifier;
    }

    /**
     * Return a collection of reference qualifier names. Just a wrapper
     * to <code>getRefQualifierNames</code> method of <code>IntactService</code>.
     *
     * @see uk.ac.ebi.intact.application.cvedit.business.IntactService#getRefQualifierNames()
     */
//    public Collection getRefQualifierNames() {
//        return super.getList(IntactUserIF.QUALIFIER_NAMES);
//    }

    /**
     * Resets the form to their defualt values. Called from action classes
     * to reset the forms or else it will display the user entered values.
     */
    public void reset() {
        myDatabase = "";
        myPrimaryId = "";
        mySecondaryId = "";
        myReleaseNumber = "";
        myRefQualifier = "";
    }
}