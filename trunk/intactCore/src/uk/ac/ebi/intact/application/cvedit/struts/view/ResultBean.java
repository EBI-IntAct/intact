/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.view;

import java.io.Serializable;
import java.text.DateFormat;

import org.apache.commons.collections.Predicate;

import uk.ac.ebi.intact.model.CvObject;

/**
 * This class contains information for a single search result.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */

public class ResultBean implements Serializable {

    // Static Inner class

    // ------------------------------------------------------------------------
    private static class ResultBeanPredicate implements Predicate {

        /**
         * The ac to filter out.
         */
        private String myFilterAc;

        private ResultBeanPredicate(String ac) {
            myFilterAc = ac;
        }

        /**
         * Returns true if the input object matches this predicate.
         * @param object the object to get the AC.
         * @return  true if <code>object</code>'s AC does not match with the
         * filter AC.
         */
        public boolean evaluate(Object object) {
            return !myFilterAc.equals(((ResultBean) object).getAc());
        }
    }
    // ------------------------------------------------------------------------

    // End of inner classes.

    /**
     * The date formatter instance to format dates.
     */
    private static DateFormat DATE_FORMATTER = DateFormat.getDateInstance();

    /**
     * Reference to CV object.
     */
    private CvObject myCvObject;

    /**
     * Returns the predicate for this class.
     * @param ac the AC number to filter.
     * @return the <code>Predicate</code> for list operations.
     */
    public static Predicate getPredicate(String ac) {
        return new ResultBeanPredicate(ac);
    }

    /**
     * Constructs with a CvObject.
     * @param cvobj the CvObject to extract information.
     */
    public ResultBean(CvObject cvobj) {
        myCvObject = cvobj;
    }

    /**
     * Returns the AC as a string.
     * @return accession number of the CvObject.
     */
    public String getAc() {
        return myCvObject.getAc();
    }

    /**
     * Returns the short label.
     * @return short label.
     */
    public String getShortLabel() {
        return myCvObject.getShortLabel();
    }

    /**
     * Returns the full name.
     * @return the full name as a <code>String</code> for the wrapped object.
     */
    public String getFullName() {
        return myCvObject.getFullName();
    }

    /**
     * Returns the last updated date.
     * @return the last updated date as a formatted <code>String</code>.
     */
    public String getUpdated() {
        return DATE_FORMATTER.format(myCvObject.getUpdated());
    }

    /**
     * Returns the created date.
     * @return the created date as a formatted <code>String</code>.
     */
    public String getCreated() {
        return DATE_FORMATTER.format(myCvObject.getCreated());
    }

    /**
     * Returns the short label of the owner.
     * @return short label of the owner as a <code>String</code>.
     */
    public String getOwner() {
        return myCvObject.getOwner().getShortLabel();
    }

    // Override Objects's equal method.

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract. Only returns <tt>true</tt> if the AC for both
     * objects match.
     *
     * @param obj the object to compare.
     */
    public boolean equals(Object obj) {
        // Identical to this?
        if (obj == this) {
            return true;
        }
        if ((obj != null) && (getClass() == obj.getClass())) {
            // Can safely cast it.
            return myCvObject.getAc().equals(((ResultBean) obj).getAc());
        }
        return false;
    }

    // For debugging only.
//    public String toString() {
//        return "AC: " + myCvObject.getAc() + " SL: " + myCvObject.getShortLabel();
//    }
}
