/*
 Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
 All rights reserved. Please see the file LICENSE
 in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.editor.struts.view.feature;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditKeyBean;
import uk.ac.ebi.intact.model.CvFuzzyType;
import uk.ac.ebi.intact.model.Range;

/**
 * Bean to store a range for a feature.
 * 
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class RangeBean extends AbstractEditKeyBean {

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

    // Class Methods

    /**
     * Parses given ranges and return the parsed values via an int array.
     * @param fromRange the from range
     * @param toRange the to range
     * @return an array of four ints. The items in the array are in the following
     * order: position 0: start for from range, pos 1: end for from range,
     * pos 2: start for to range and pos 3: end for to range.
     * @throws NumberFormatException any errors in parsing the range string.
     */
    public static int[] parseRange(String fromRange, String toRange) {
        // The array to return.
        int[] ranges = new int[4];

        // The array to hold the result.
        int[] results;

        // Copy the from range values.
        results = RangeBean.parseRange(fromRange);
        ranges[0] = results[0];
        ranges[1] = results[1];

        // Copy the to range values.
        results = RangeBean.parseRange(toRange);
        ranges[2] = results[0];
        ranges[3] = results[1];

        return ranges;
    }

    /**
     * Returns the fuzzy type for given range.
     * @param range the range as a string.
     * @param user the user search for the fuzzy type
     * @return the fuzzy type for given range. <code>null</code> is returned
     * if there is no fuzzy type associated with <code>range</code>.
     * @throws SearchException for errors in searching the persisten system
     * for the fuzzy type.
     */
    public static CvFuzzyType parseFuzzyType(String range, EditUserI user)
            throws SearchException {
        // The from fuzzy type as a string.
        String fromType = denormalizeFuzzyType(range);
        return getFuzzyType(fromType, user);
    }

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
     * Sets the From fuzzy type as undertermined.
     */
    public void setFromFuzzyTypeAsUndetermined() {
        myFromFuzzyType = CvFuzzyType.UNDETERMINED;
    }

    /**
     * Sets the To fuzzy type as undertermined.
     */
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
     * Returns true if given bean is equivalent to the current bean.
     * @param rb the bean to compare.
     * @return true if ranges, link and undetermined are equivalent
     * to corresponding value in <code>rb</code>; false is returned for all
     * other instances.
     */
    public boolean isEquivalent(RangeBean rb) {
        // Check ranges.
        if (!rb.getFromRange().equals(getFromRange())
                || !rb.getToRange().equals(getToRange())) {
            return false;
        }

        // Check link and undetermined types.
        if ((myLink != rb.myLink) || (myUndetermined != rb.myUndetermined)) {
            return false;
        }
        return true;
    }

    /**
     * Validates the vlaues for from and to ranges.
     *
     * @return null if no errors found in validating from/to ranges.
     */
    public ActionErrors validate(String prefix) {
        // The errors to return.
        ActionErrors errors = null;

        // Stores the range values.
        int[] ranges;

        // Holds values for from range.
        int fromStart = 0;
        int fromEnd = 0;

        try {
            // Parse ranges.
            ranges = parseRange(myFromRange);

            // Split ranges to start and end.
            fromStart = ranges[0];
            fromEnd = ranges[1];

            // Check the validity of ranges.
            if (fromStart > fromEnd) {
                errors = new ActionErrors();
                errors.add(prefix + ".fromRange", new ActionError(
                        "error.feature.range.interval.invalid"));
            }
        }
        catch (NumberFormatException nfe) {
            errors = new ActionErrors();
            errors.add(prefix + ".fromRange", new ActionError(
                    "error.feature.range.invalid"));
        }
        // Don't check any further if we have errors.
        if (errors != null) {
            return errors;
        }

        // Holds values for to range.
        int toStart = 0;
        int toEnd = 0;

        try {
            // Parse ranges.
            ranges = parseRange(myToRange);

            // Split ranges to start and end.
            toStart = ranges[0];
            toEnd = ranges[1];

            // Check the validity of ranges.
            if (toStart > toEnd) {
                errors = new ActionErrors();
                errors.add(prefix + ".toRange", new ActionError(
                        "error.feature.range.interval.invalid"));
            }
        }
        catch (NumberFormatException nfe) {
            errors = new ActionErrors();
            errors.add(prefix + ".toRange", new ActionError(
                    "error.feature.range.invalid"));
        }
        // Don't check any further if we have errors.
        if (errors != null) {
            return errors;
        }
        // Need to validate the from and start ranges. These validations are
        // copied from Range constructor.
        if (fromEnd < fromStart) {
            errors = new ActionErrors();
            errors.add(prefix + ".range", new ActionError(
                    "error.feature.range.fromEnd.less.fromStart"));
        }
        else if (toEnd < toStart) {
            errors = new ActionErrors();
            errors.add(prefix + ".range", new ActionError(
                    "error.feature.range.toEnd.less.toStart"));
        }
        else if (fromEnd > toStart) {
            errors = new ActionErrors();
            errors.add(prefix + ".range", new ActionError(
                    "error.feature.range.fromEnd.more.toStart"));
        }
        else if (fromStart > toEnd) {
            errors = new ActionErrors();
            errors.add(prefix + ".range", new ActionError(
                    "error.feature.range.fromStart.more.toEnd"));
        }
        else if (fromStart > toStart) {
            errors = new ActionErrors();
            errors.add(prefix + ".range", new ActionError(
                    "error.feature.range.fromStart.more.toStart"));
        }
        return errors;
    }

    /**
     * Allows access to the range object this bean is created with.
     * @return the Range instance this bean is wrapped.
     * <p>
     * <b>Notde:</b> The range is not updated. This method is used to
     * delete existing ranges.
     */
    public Range getRange() {
        return myRange;
    }

    /**
     * Construts a new Range or updates the existing range using the current
     * values in the bean. <b>Must </b> call {@link #validate(String)} method
     * prior to calling this method.
     * 
     * @param user the user object to access <code>CvFuzzyType</code>s.
     * 
     * @return a new Range using values from the bean if this is a new range or
     * else the existing range is updated.
     * @exception SearchException unable to find code>CvFuzzyType</code>s.
     * @exception IllegalArgumentException for errors in constructing a Range
     * object (ie., thrown from the constructor of the Range class).
     * 
     * <pre>
     *  pre: validate(String)
     * </pre>
     */
    public Range getRange(EditUserI user) throws SearchException,
            IllegalArgumentException {
        // The range as an int array.
        int[] ranges = RangeBean.parseRange(myFromRange, myToRange);

        // Split the ranges to parts.
        int fromStart = ranges[0];
        int fromEnd = ranges[1];
        int toStart = ranges[2];
        int toEnd = ranges[3];

        // The from/to fuzzy types.
        CvFuzzyType fromFuzzyType = parseFuzzyType(myFromRange, user);
        CvFuzzyType toFuzzyType = parseFuzzyType(myToRange, user);

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
        // Update the ranges.
        myRange.setFromIntervalStart(fromStart);
        myRange.setFromIntervalEnd(fromEnd);
        myRange.setToIntervalStart(toStart);
        myRange.setToIntervalEnd(toEnd);
        // Set the fuzzy types.
        myRange.setFromCvFuzzyType(fromFuzzyType);
        myRange.setToCvFuzzyType(toFuzzyType);

        myRange.setLink(myLink);
        myRange.setUndetermined(myUndetermined);

        return myRange;
    }

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
            myFromRange = "?";
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
            myToRange = "?";
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
     * Parses the given range.
     * @param rangeStr the range string to parse.
     * @return an array of two ints with the first element contains
     * the start and the second element contains the end value.
     */
    private static int[] parseRange(String rangeStr) {
        // To hold from start and end values.
        int[] result = new int[2];
        // The fuzzy type as a string.
        String type = denormalizeFuzzyType(rangeStr);
        // Range as a string after taking into the type.
        String range = getRange(type, rangeStr);

        // Need special parsing for fuzzy types.
        if (type.equals(CvFuzzyType.FUZZY)) {
            int pos = range.indexOf('.');
            result[0] = Integer.parseInt(range.substring(0, pos));
            // Skip two positions to be on the first character of the end level.
            pos += 2;
            result[1] = Integer.parseInt(range.substring(pos));
        }
        else {
            // Non fuzzy types.
            result[0] = Integer.parseInt(range);
            result[1] = result[0];
        }
        return result;
    }
}