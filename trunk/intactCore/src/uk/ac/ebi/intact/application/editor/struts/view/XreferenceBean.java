/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view;

import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.model.Annotation;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Bean to store data for x'references.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class XreferenceBean extends AbstractEditKeyBean implements Serializable {

    /**
     * The pattern to replace the ac.
     */
    private static Pattern ourSearchUrlPat = Pattern.compile("\\$\\{ac\\}");

    /**
     * Reference to the Xref object this instance is created with.
     */
    private Xref myXref;

    /**
     * The database name.
     */
    private String myDatabaseName;

    /**
     * The primary id. Set to empty; this is a required field.
     */
    private String myPrimaryId = "";

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
     * Default constructor.
     */
    public XreferenceBean() {}

    /**
     * Instantiate an object of this class from a Xref object.
     *
     * @param xref the <code>Xref</code> object to construct an
     * instance of this class.
     */
    public XreferenceBean(Xref xref) {
        initialize(xref);
    }

    /**
     * Instantiates with given xref and key.
     * @param xref the underlying <code>Xref</code> object.
     * @param key the key to assigned to this bean.
     */
    public XreferenceBean(Xref xref, long key) {
        super(key);
        initialize(xref);
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
        // When no Xref is wrapped (for instance adding a new xref).
        if (myXref == null) {
            return myPrimaryId;
        }
        // Null to indicate that there is no search-url in the annotations.
        String searchUrl = null;

        // Loop through annotations looking for search-url.
        Collection annots = myXref.getCvDatabase().getAnnotations();
        for (Iterator iter = annots.iterator(); iter.hasNext(); ){
            Annotation annot = (Annotation) iter.next();
            if (annot.getCvTopic().getShortLabel().equals("search-url")){
                // save searchUrl for future use
                searchUrl = annot.getAnnotationText();
                break;
            }
        }
        if (searchUrl == null) {
            // No links
            return myPrimaryId;
        }
        Matcher matcher = ourSearchUrlPat.matcher(searchUrl);
        // After replacing the ac with primary id.
        searchUrl = matcher.replaceAll(myPrimaryId);
        return "<a href=\"" + "javascript:showXrefPId('" + searchUrl + "')\"" + ">"
                + myPrimaryId + "</a>";
    }

    /**
     * Sets the primary id.
     * @param primaryId the primary id as a <code>String</code>; any excess
     * blanks are trimmed as this is set from values entered into a free input
     * text box.
     */
    public void setPrimaryId(String primaryId) {
        myPrimaryId = primaryId.trim();
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
        myReferenceQualifer = refQualifier;
    }

    /**
     * Updates the internal xref with the new values from the form.
     * @param user the user instance to search for a cv database and xref qualifier.
     * @throws SearchException for errors in searching the database.
     */
    public void update(EditUserI user) throws SearchException {
        CvDatabase db = (CvDatabase) user.getObjectByLabel(
                CvDatabase.class, myDatabaseName);
        myXref.setCvDatabase(db);
        myXref.setPrimaryId(myPrimaryId);
        myXref.setSecondaryId(mySecondaryId);
        myXref.setDbRelease(myReleaseNumber);
        CvXrefQualifier xqual = (CvXrefQualifier) user.getObjectByLabel(
                CvXrefQualifier.class, myReferenceQualifer);
        myXref.setCvXrefQualifier(xqual);
    }

    /**
     * Resets fields to blanks, so the addXref form doesn't display
     * previous values. Calls the super reset to reset the internal key.
     */
    public void reset() {
        super.reset();
        myDatabaseName = "";
        myPrimaryId = "";
        mySecondaryId = "";
        myReleaseNumber = "";
        myReferenceQualifer = "";
    }

    // Helper methods

    /**
     * Intialize the member variables using the given Xref object.
     * @param xref <code>Xref</code> object to populate this bean.
     */
    private void initialize(Xref xref) {
        myXref = xref;
        myDatabaseName = xref.getCvDatabase().getShortLabel();
        myPrimaryId = xref.getPrimaryId();
        mySecondaryId = xref.getSecondaryId();
        myReleaseNumber = xref.getDbRelease();

        myReferenceQualifer = "";
        CvXrefQualifier qualifier = xref.getCvXrefQualifier();
        if (qualifier != null) {
            myReferenceQualifer = qualifier.getShortLabel();
        }
    }
}
