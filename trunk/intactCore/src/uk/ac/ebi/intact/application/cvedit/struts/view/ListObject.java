/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.view;

import java.io.Serializable;
import org.apache.commons.collections.Predicate;

import uk.ac.ebi.intact.model.CvObject;

/**
 * This class returns columns of data that are useful for displaying them
 * onto a jsp form. This class forms part of the Decorator pattern required by
 * display tag library.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */

public class ListObject implements Serializable {

    // Static Inner class

    // ------------------------------------------------------------------------
    private static class ListObjectPredicate implements Predicate {

        /**
         * The ac to filter out.
         */
        private String myFilterAc;

        private ListObjectPredicate(String ac) {
            myFilterAc = ac;
        }

        /**
         * Returns true if the input object matches this predicate.
         * @param object the object to get the AC.
         * @return  true if <code>object</code>'s AC does not match with the
         * filter AC.
         */
        public boolean evaluate(Object object) {
            return !myFilterAc.equals(((ListObject) object).getAc());
        }
    }
    // ------------------------------------------------------------------------

    // End of inner classes.

    /**
     * Reference to CV object.
     */
    private CvObject myCvObject;

    /**
     * Constructs with a CvObject.
     * @param cvobj the CvObject to extract information.
     */
    public ListObject(CvObject cvobj) {
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
     * Returns the predicate for this class.
     * @param ac the AC number to filter.
     * @return the <code>Predicate</code> for list operations.
     */
    public static Predicate getPredicate(String ac) {
        return new ListObjectPredicate(ac);
    }

    // Override Objects's equal method.

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract. Only returns <tt>true</tt> if the AC for both
     * objects match.
     *
     * @param obj the object to compare.
     */
    public final boolean equals(Object obj) {
        // Identical to this?
        if (obj == this) {
            return true;
        }
        if ((obj != null) && (getClass() == obj.getClass())) {
            // Can safely cast it.
            return myCvObject.getAc().equals(((ListObject) obj).getAc());
        }
        return false;
    }

    // For debugging only.
//    public String toString() {
//        return "AC: " + myCvObject.getAc() + " SL: " + myCvObject.getShortLabel();
//    }
}
