/*
 Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
 All rights reserved. Please see the file LICENSE
 in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.editor.struts.view.feature;

import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditKeyBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ComponentBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactHelper;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;

/**
 * Bean to store a range for a feature.
 * 
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class RangeBean extends AbstractEditKeyBean implements Cloneable {
    // Class Data

    private static final String ourUndeterminedFuzzyType = "undetermined";

    // Instance Data

    /**
     * Reference to the range this instance is created with.
     */
    private Range myRange;

    /**
     * The from fuzzy type. Default is none.
     */
    private String myFromFuzzyType = "";

    /**
     * The to fuzzy type. Default is none.
     */
    private String myToFuzzyType = "";

    /**
     * The from range as a string.
     */
    private String myFromRange;

    /**
     * The to range as a string.
     */
    private String myToRange;

    /**
     * The link.
     */
    private boolean myLink;

    /**
     * Undetermined or not.
     */
    private boolean myUndetermined;

    /**
     * Default constructor. Needs for adding new ranges to a feature.
     */
    public RangeBean() {
    }

    /**
     * Instantiate an object of this class from a Range instance. The key is set
     * to a default value (unique).
     * 
     * @param range the <code>Range</code> object.
     */
    public RangeBean(Range range) {
        initialize(range);
    }

    /**
     * Instantiate an object of this class from a Range instance and a key.
     * 
     * @param range the <code>Range</code> object.
     * @param key the key to assigned to this bean.
     */
    public RangeBean(Range range, long key) {
        super(key);
        initialize(range);
    }

    /**
     * Sets the from fuzzy type as undertermined.
     * 
     * @param type the from fuzzy type. Must be of valid type. No exceptions or
     * errors thrown for an invalid/unknown type.
     */
    //    public void setFromFuzzyType(String type) {
    //        myFromFuzzyType = getFuzzyType(type);
    //    }
    public void setFromFuzzyTypeAsUndetermined() {
        myFromFuzzyType = CvFuzzyType.UNDETERMINED;
    }

    /**
     * Sets the to fuzzy type as undertermined.
     * 
     * @param type the from fuzzy type. Must be of valid type. No exceptions or
     * errors thrown for an invalid/unknown type.
     */
    //    public void setToFuzzyType(String type) {
    //        myToFuzzyType = getFuzzyType(type);
    //    }
    public void setToFuzzyTypeAsUndetermined() {
        myToFuzzyType = CvFuzzyType.UNDETERMINED;
    }

    // Read/Write only properties.

    /**
     * Returns the from range.
     * 
     * @return the from range as a string for display purposes.
     */
    public String getFromRange() {
        return myFromRange;
    }

    public void setFromRange(String fromRange) {
        myFromRange = fromRange.trim();
    }

    /**
     * Returns the to range.
     * 
     * @return the to range as a string for display purposes.
     */
    public String getToRange() {
        return myToRange;
    }

    public void setToRange(String toRange) {
        myToRange = toRange.trim();
    }

    /**
     * Returns the link as a string.
     * 
     * @return the link as a string (true or false).
     */
    public String getLink() {
        return Boolean.toString(myLink);
    }

    /**
     * Sets the link.
     * 
     * @param link the link to set as a string object.
     */
    public void setLink(String link) {
        myLink = Boolean.valueOf(link).booleanValue();
    }

    /**
     * Returns the undetermined as a string.
     * 
     * @return the undetermined as a string (true or false).
     */
    public String getUndetermined() {
        return Boolean.toString(myUndetermined);
    }

    /**
     * Sets the undetermined.
     * 
     * @param undetermined the undetermined to set as a string object.
     */
    public void setUndetermined(String undetermined) {
        myUndetermined = Boolean.valueOf(undetermined).booleanValue();
    }

    // Override Object's toString() method to display the range.

    public String toString() {
        return myFromRange + "-" + myToRange;
    }

    /**
     * Resets fields to blanks, so the addXref form doesn't display previous
     * values.
     */
    public void reset() {
        super.reset();
        myFromRange = "";
        myToRange = "";
        myLink = false;
        myUndetermined = false;
    }

    /**
     * Validates the vlaues for from and to ranges.
     * 
     * @return null if no errors found in validating from/to ranges.
     */
    public ActionErrors validate() {
        // The errors to return.
        ActionErrors errors = null;

        // Extract the fuzzy type for the from range.
        String fromType = denormalizeFuzzyType(myFromRange);
        // From range as a string.
        String fromRange = getRange(fromType, myFromRange);

        try {
            // Parse the from range.
            if (!isRangeValid(fromType, fromRange)) {
                errors = new ActionErrors();
                errors.add("new.fromRange", new ActionError(
                        "error.feature.range.interval.invalid"));
            }
        }
        catch (NumberFormatException nfe) {
            errors = new ActionErrors();
            errors.add("new.fromRange", new ActionError(
                    "error.feature.range.invalid"));
        }
        // Don't check any further if we have errors.
        if (errors != null) {
            return errors;
        }

        // Extract the fuzzy type for the to range.
        String toType = denormalizeFuzzyType(myToRange);
        // To Range as a string.
        String toRange = getRange(toType, myToRange);

        try {
            // Parse the to range.
            if (!isRangeValid(toType, toRange)) {
                errors = new ActionErrors();
                errors.add("new.toRange", new ActionError(
                        "error.feature.range.interval.invalid"));
            }
        }
        catch (NumberFormatException nfe) {
            errors = new ActionErrors();
            errors.add("new.toRange", new ActionError(
                    "error.feature.range.invalid"));
        }
        return errors;
    }

    /**
     * Construts a new or updates an existing Range using the current values in
     * the bean. <b>Must </b> call {@link #validate()}method prior to calling
     * this method.
     * 
     * @param parent the parent of the range to return. Only required for a new
     * range. For all other instances, the parent of the Range this bean
     * constructed is used.
     * @param user the user object to access <code>CvFuzzyType</code>s.
     * 
     * @return a new or an updated Range using values from the bean.
     * @exception SearchException unable to find code>CvFuzzyType</code>s.
     * @exception IllegalArgumentException for errors in constructing a Range
     * object (ie., thrown from the constructor of the Range class).
     * 
     * <pre>
     *  pre: validate()
     * </pre>
     */
    public Range getRange(Feature parent, EditUserI user)
            throws SearchException, IllegalArgumentException {
        // The from fuzzy type as a string.
        String fromType = denormalizeFuzzyType(myFromRange);
        // From range as a string.
        String fromRange = getRange(fromType, myFromRange);

        int fromStart = 0;
        int fromEnd = 0;
        // Need special parsing for fuzzy types.
        if (fromType.equals(CvFuzzyType.FUZZY)) {
            int pos = fromRange.indexOf('.');
            fromStart = Integer.parseInt(fromRange.substring(0, pos));
            // Skip two positions to be on the first character of the end level.
            pos += 2;
            fromEnd = Integer.parseInt(fromRange.substring(pos));
        }
        else {
            // Non fuzzy types.
            fromStart = Integer.parseInt(fromRange);
            fromEnd = fromStart;
        }

        // The to fuzzy type as a string.
        String toType = denormalizeFuzzyType(myToRange);
        // To range as a string.
        String toRange = getRange(toType, myToRange);

        int toStart = 0;
        int toEnd = 0;
        // Need special parsing for fuzzy types.
        if (toType.equals(CvFuzzyType.FUZZY)) {
            int pos = toRange.indexOf('.');
            toStart = Integer.parseInt(toRange.substring(0, pos));
            // Skip two positions to be on the first character of the end level.
            pos += 2;
            toEnd = Integer.parseInt(toRange.substring(pos));
        }
        else {
            // Non fuzzy types.
            toStart = Integer.parseInt(toRange);
            toEnd = toStart;
        }
        // The from/to fuzzy types.
        CvFuzzyType fromFuzzyType = getFuzzyType(fromType, user);
        CvFuzzyType toFuzzyType = getFuzzyType(toType, user);

        // myRange is null for a new range.
        if (myRange == null) {
            // Construct a range object to return.
            myRange = new Range(user.getInstitution(), parent, fromStart,
                    fromEnd, toStart, toEnd, null);
        }
        else {
            // Need to validate the from and start ranges. These validations are
            // copied from Range constructor. Alternative is to create a dummy
            // range object to validate inputs.
            if (fromEnd < fromStart) {
                throw new IllegalArgumentException(
                        "End of 'from' interval must be bigger than the start!");
            }
            if (toEnd < toStart) {
                throw new IllegalArgumentException(
                        "End of 'to' interval must be bigger than the start!");
            }
            if (fromEnd > toStart) {
                throw new IllegalArgumentException(
                        "The 'from' and 'to' intervals cannot overlap!");
            }
            if (fromStart > toEnd) {
                throw new IllegalArgumentException(
                        "The 'from' interval starts beyond the 'to' interval!");
            }
            if (fromStart > toStart) {
                throw new IllegalArgumentException(
                        "The 'from' interval cannot begin during the 'to' interval!");
            }
            // Update the ranges for the existing range.
            myRange.setFromIntervalStart(fromStart);
            myRange.setFromIntervalEnd(fromEnd);
            myRange.setToIntervalStart(toStart);
            myRange.setToIntervalEnd(toEnd);
        }
        //
        //        // Important: Set the owner (or persistent will fail)
        //        range.setOwner(user.getInstitution());

        // Set the fuzzy types.
        myRange.setFromCvFuzzyType(fromFuzzyType);
        myRange.setToCvFuzzyType(toFuzzyType);

        myRange.setLink(myLink);
        myRange.setUndetermined(myUndetermined);

        return myRange;
    }

    /**
     * Makes a clone of this object.
     * 
     * @return a cloned version of the current instance. A null
     */
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    //    // Override Objects's equal method.
    //
    //    /**
    //     * Compares <code>obj</code> with this object according to
    //     * Java's equals() contract. Only returns <tt>true</tt> if ranges, link
    // and
    //     * undetermined al match.
    //     * @param obj the object to compare.
    //     */
    //    public boolean equals(Object obj) {
    //        // Identical to this?
    //        if (obj == this) {
    //            return true;
    //        }
    //        if ((obj != null) && (getClass() == obj.getClass())) {
    //            // Can safely cast it.
    //            RangeBean other = (RangeBean) obj;
    //            // Match the from ranges.
    //            if (getFromRange().equals(other.getFromRange())) {
    //                // From ranges match. Match to the to range.
    //                if (getToRange().equals(other.getToRange())) {
    //                    // To ranges match; match boolean attributes.
    //                    return myLink == other.myLink && myUndetermined == other.myUndetermined;
    //                }
    //            }
    //        }
    //        return false;
    //    }

    // Helper methods

    /**
     * Intialize the member variables using the given Range object.
     * 
     * @param range <code>Range</code> object to populate this bean.
     */
    private void initialize(Range range) {
        myRange = range;

        // Set the fuzzy type first as they are used in set range methods.
        setFromFuzzyType(range);
        setToFuzzyType(range);

        // Set the from/to ranges.
        setFromRange(range);
        setToRange(range);

        myLink = range.isLinked();
        myUndetermined = range.isUndertermined();
    }

    private void setFromFuzzyType(Range range) {
        if (range.getFromCvFuzzyType() != null) {
            myFromFuzzyType = normalizeFuzzyType(range.getFromCvFuzzyType()
                    .getShortLabel());
        }
    }

    private static CvFuzzyType getFuzzyType(String fuzzyType, EditUserI user)
            throws SearchException {
        // The fuzzy type to return.
        CvFuzzyType type = null;

        if (fuzzyType.equals(CvFuzzyType.LESS_THAN)) {
            // Return the less than fuzzy type.
            type = (CvFuzzyType) user.getObjectByLabel(CvFuzzyType.class,
                    CvFuzzyType.LESS_THAN);
        }
        else if (fuzzyType.equals(CvFuzzyType.GREATER_THAN)) {
            // Return the greater than fuzzy type.
            type = (CvFuzzyType) user.getObjectByLabel(CvFuzzyType.class,
                    CvFuzzyType.GREATER_THAN);
        }
        else if (fuzzyType.equals(CvFuzzyType.FUZZY)) {
            // Return the fuzzy type.
            type = (CvFuzzyType) user.getObjectByLabel(CvFuzzyType.class,
                    CvFuzzyType.FUZZY);
        }
        else if (fuzzyType.equals(CvFuzzyType.UNDETERMINED)) {
            // Return the undetermined type.
            type = (CvFuzzyType) user.getObjectByLabel(CvFuzzyType.class,
                    CvFuzzyType.UNDETERMINED);
        }
        return type;
    }

    private void setToFuzzyType(Range range) {
        if (range.getToCvFuzzyType() != null) {
            myToFuzzyType = normalizeFuzzyType(range.getToCvFuzzyType()
                    .getShortLabel());
        }
    }

    private String normalizeFuzzyType(String label) {
        String result;
        if (label.equals(CvFuzzyType.LESS_THAN)) {
            result = "<";
        }
        else if (label.equals(CvFuzzyType.GREATER_THAN)) {
            result = ">";
        }
        else if (label.equals(CvFuzzyType.UNDETERMINED)) {
            result = "?";
        }
        else {
            result = label;
        }
        return result;
    }

    private static String denormalizeFuzzyType(String input) {
        String result = "";
        if (input.startsWith("<")) {
            result = CvFuzzyType.LESS_THAN;
        }
        else if (input.startsWith(">")) {
            result = CvFuzzyType.GREATER_THAN;
        }
        else if (input.startsWith("?")) {
            result = CvFuzzyType.UNDETERMINED;
        }
        else if (input.indexOf("..") != -1) {
            result = CvFuzzyType.FUZZY;
        }
        return result;
    }

    private void setFromRange(Range range) {
        // Undetermined?
        if (myFromFuzzyType.equals("?")) {
            myFromRange = "0";
        }
        // Fuzzy type?
        else if (myFromFuzzyType.equals(CvFuzzyType.FUZZY)) {
            myFromRange = range.getFromIntervalStart() + ".."
                    + range.getFromIntervalEnd();
        }
        // No fuzzy type?
        else if (myFromFuzzyType.length() == 0) {
            myFromRange = "" + range.getFromIntervalStart();
        }
        else {
            // > or <
            myFromRange = myFromFuzzyType + range.getFromIntervalStart();
        }
    }

    private static String getRange(String fuzzyType, String input) {
        String range;
        if (fuzzyType.equals(CvFuzzyType.UNDETERMINED)) {
            // Undetermined fuzzy.
            range = "0";
        }
        else if (fuzzyType.equals(CvFuzzyType.FUZZY)
                || (fuzzyType.length() == 0)) {
            // 1..3 situation or no fuzzy specified. Return the whole range
            range = input;
        }
        else {
            // < or > situation; skip the leading char.
            range = input.substring(1);
        }
        return range;
    }

    private void setToRange(Range range) {
        // Undetermined?
        if (myToFuzzyType.equals("?")) {
            myToRange = "0";
        }
        // Fuzzy type?
        else if (myToFuzzyType.equals(CvFuzzyType.FUZZY)) {
            myToRange = range.getToIntervalStart() + ".."
                    + range.getToIntervalEnd();
        }
        // No fuzzy type?
        else if (myToFuzzyType.length() == 0) {
            myToRange = "" + range.getToIntervalStart();
        }
        else {
            // > or <
            myToRange = myToFuzzyType + range.getToIntervalEnd();
        }
    }

    /**
     * True if given range is valid.
     * 
     * @param fuzzyType the type consider when checking for range. If the type
     * is fuzzy, intervals are considered for validation purposes.
     * @param range the range to check as a string object.
     * @return true only if range consists of numbers and start is <= end range.
     * @throws NumberFormatException any errors in parsing the range string.
     */
    private static boolean isRangeValid(String fuzzyType, String range)
            throws NumberFormatException {
        int start = 0;
        int end = 0;
        // Special processing for fuzzy type.
        if (fuzzyType.equals(CvFuzzyType.FUZZY)) {
            int pos = range.indexOf('.');
            start = Integer.parseInt(range.substring(0, pos));
            // Skip two positions to be on the first character of the end level.
            pos += 2;
            end = Integer.parseInt(range.substring(pos));
        }
        else {
            start = Integer.parseInt(range);
            end = start;
        }
        return start <= end;
    }
}