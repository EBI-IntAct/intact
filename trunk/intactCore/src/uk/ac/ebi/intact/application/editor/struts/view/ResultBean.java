/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view;

import java.io.Serializable;

import org.apache.commons.collections.Predicate;

import uk.ac.ebi.intact.model.AnnotatedObject;

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
     * Reference to CV object.
     */
    private AnnotatedObject myAnnotObject;

    /**
     * Returns the predicate for this class.
     * @param ac the AC number to filter.
     * @return the <code>Predicate</code> for list operations.
     */
    public static Predicate getPredicate(String ac) {
        return new ResultBeanPredicate(ac);
    }

    /**
     * Constructs with an Annotated object.
     * @param anobj the <code>AnnotatedObject</code> to extract information.
     */
    public ResultBean(AnnotatedObject anobj) {
        myAnnotObject = anobj;
    }

    /**
     * Returns the AC as a string.
     * @return accession number of the CvObject.
     */
    public String getAc() {
        return myAnnotObject.getAc();
    }

    /**
     * Returns the short label.
     * @return short label.
     */
    public String getShortLabel() {
        return myAnnotObject.getShortLabel();
    }

    /**
     * Returns the full name.
     * @return the full name as a <code>String</code> for the wrapped object.
     */
    public String getFullName() {
        return myAnnotObject.getFullName();
    }

    /**
     * True if the given object and this object's annotated object are of safe type.
     * @param obj the object to compare for type.
     * @return true if <code>obj.getClass()</code> equals the class of the annotated
     * object.
     */
    public boolean isSameType(Object obj) {
        // Same instance?
        if (obj == this) {
            return true;
        }
        return myAnnotObject.getClass() == obj.getClass();
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
            return myAnnotObject.getAc().equals(((ResultBean) obj).getAc());
        }
        return false;
    }
}
