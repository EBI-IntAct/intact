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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bean to store a range for a feature.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class RangeBean extends AbstractEditKeyBean {

    // Class Data

    /**
     * optional ?, <, >, c, n, -, followed by an optional number, optional ..
     * , optional -
     */
    private static final Pattern ourRangePattern =
            Pattern.compile("^(\\?|<|>|c|n|\\-)?+(\\d+)*?(\\.\\.)?+(\\-)?+(\\d+)?+$");

    // Instance Data

    /**
     * Reference to the range this instance is created with.
     */
    private Range myRange;

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
     * Handler to the fuzzy type converter.
     */
    private FuzzyTypeConverter myFTConverter = FuzzyTypeConverter.getInstance();

    // Class Methods

    /**
     * For test purposes only
     *
     * @return return the pattern to parse a range.
     */
    public static Pattern testGetRangePattern() {
        return ourRangePattern;
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
     * Instantiate an object of this class using ranges.
     *
     * @param user the user to get the CvFuzzyTypes.
     * @param fromRange the from range as a string
     * @param toRange the to range as a string
     * @throws SearchException for errors in retrieving CvFuzzyTypes
     */
    public RangeBean(EditUserI user, String fromRange, String toRange)
            throws SearchException {
        // The match result for from range
        Matcher fromMatcher = ourRangePattern.matcher(fromRange);
        if (!fromMatcher.matches()) {
            throw new IllegalArgumentException("Unable to parse the from range");
        }
        String fromFuzzyType = myFTConverter.getFuzzyShortLabel(fromMatcher);
        int[] fromRanges = getRangeValues(fromFuzzyType, fromMatcher);

        // The match result for to range.
        Matcher toMatcher = ourRangePattern.matcher(toRange);
        if (!toMatcher.matches()) {
            throw new IllegalArgumentException("Unable to parse the to range");
        }
        String toFuzzyType = myFTConverter.getFuzzyShortLabel(toMatcher);
        int[] toRanges = getRangeValues(toFuzzyType, toMatcher);

        // Construct a range and set fuzzy types.
        Range range = new Range(user.getInstitution(), fromRanges[0], fromRanges[1],
                toRanges[0], toRanges[1], null);

        // Set the from and to fuzzy types.
        range.setFromCvFuzzyType(getFuzzyType(user, fromFuzzyType));
        range.setToCvFuzzyType(getFuzzyType(user, toFuzzyType));
        initialize(range);
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
     *
     * @param rb the bean to compare.
     * @return true if ranges, link and undetermined are equivalent
     *         to corresponding value in <code>rb</code>; false is returned for all
     *         other instances.
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
            ranges = getFromRangeValues();

            // Split ranges to start and end.
            fromStart = ranges[0];
            fromEnd = ranges[1];

            // Check the validity of ranges.
            if (fromStart > fromEnd) {
                errors = new ActionErrors();
                errors.add(prefix + ".fromRange", new ActionError("error.feature.range.interval.invalid"));
            }
        }
        catch (IllegalArgumentException iae) {
            errors = new ActionErrors();
            errors.add(prefix + ".fromRange", new ActionError("error.feature.range.invalid"));
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
            ranges = getToRangeValues();

            // Split ranges to start and end.
            toStart = ranges[0];
            toEnd = ranges[1];

            // Check the validity of ranges.
            if (toStart > toEnd) {
                errors = new ActionErrors();
                errors.add(prefix + ".toRange", new ActionError("error.feature.range.interval.invalid"));
            }
        }
        catch (IllegalArgumentException iae) {
            errors = new ActionErrors();
            errors.add(prefix + ".toRange", new ActionError("error.feature.range.invalid"));
        }
        // Don't check any further if we have errors.
        if (errors != null) {
            return errors;
        }
        // Need to validate the from and start ranges. These validations are
        // copied from Range constructor.
        if (fromEnd < fromStart) {
            errors = new ActionErrors();
            errors.add(prefix + ".range", new ActionError("error.feature.range.fromEnd.less.fromStart"));
        }
        else if (toEnd < toStart) {
            errors = new ActionErrors();
            errors.add(prefix + ".range", new ActionError("error.feature.range.toEnd.less.toStart"));
        }
        else if (fromEnd > toStart) {
            errors = new ActionErrors();
            errors.add(prefix + ".range", new ActionError("error.feature.range.fromEnd.more.toStart"));
        }
        else if (fromStart > toEnd) {
            errors = new ActionErrors();
            errors.add(prefix + ".range", new ActionError("error.feature.range.fromStart.more.toEnd"));
        }
        else if (fromStart > toStart) {
            errors = new ActionErrors();
            errors.add(prefix + ".range", new ActionError("error.feature.range.fromStart.more.toStart"));
        }
        return errors;
    }

    /**
     * Allows access to the range object this bean is created with.
     *
     * @return the Range instance this bean is wrapped.
     *         <p/>
     *         <b>Notde:</b> The range is not updated. This method is used to
     *         delete existing ranges.
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
     * @return a new Range using values from the bean if this is a new range or
     *         else the existing range is updated.
     * @throws SearchException unable to find code>CvFuzzyType</code>s.
     * @throws IllegalArgumentException for errors in constructing a Range
     * object (ie., thrown from the constructor of the Range class).
     * <p/>
     * <pre>
     *  pre: validate(String)
     * </pre>
     */
    public Range getRange(EditUserI user) throws SearchException,
            IllegalArgumentException {
        // The range as an int array.
        int[] ranges = getRangeValues();

        // Split the ranges to parts.
        int fromStart = ranges[0];
        int fromEnd = ranges[1];
        int toStart = ranges[2];
        int toEnd = ranges[3];

        // Need to validate the from and start ranges. These validations are
        // copied from Range constructor. Alternative is to create a dummy
        // range object to validate inputs.
        if (fromEnd < fromStart) {
            throw new IllegalArgumentException("End of 'from' interval must be bigger than the start!");
        }
        if (toEnd < toStart) {
            throw new IllegalArgumentException("End of 'to' interval must be bigger than the start!");
        }
        if (fromEnd > toStart) {
            throw new IllegalArgumentException("The 'from' and 'to' intervals cannot overlap!");
        }
        if (fromStart > toEnd) {
            throw new IllegalArgumentException("The 'from' interval starts beyond the 'to' interval!");
        }
        if (fromStart > toStart) {
            throw new IllegalArgumentException("The 'from' interval cannot begin during the 'to' interval!");
        }
        // Update the ranges.
        myRange.setFromIntervalStart(fromStart);
        myRange.setFromIntervalEnd(fromEnd);
        myRange.setToIntervalStart(toStart);
        myRange.setToIntervalEnd(toEnd);

        // The from/to fuzzy types.
        Matcher matcher = ourRangePattern.matcher(myFromRange);
        String type = myFTConverter.getFuzzyShortLabel(matcher);
        myRange.setFromCvFuzzyType(getFuzzyType(user, type));

        matcher = ourRangePattern.matcher(myToRange);
        type = myFTConverter.getFuzzyShortLabel(matcher);
        myRange.setToCvFuzzyType(getFuzzyType(user, type));

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

        // Saves the from type as a short label
        String fromType = "";

        // Set the fuzzy type first as they are used in set range methods.
        if (range.getFromCvFuzzyType() != null) {
            fromType = range.getFromCvFuzzyType().getShortLabel();
        }
        setFromRange(fromType, range);

        // Saves the to type as a short label
        String toType = "";

        if (range.getToCvFuzzyType() != null) {
            toType = range.getToCvFuzzyType().getShortLabel();
        }
        setToRange(toType, range);

        myLink = range.isLinked();
        myUndetermined = range.isUndertermined();
    }

    /**
     * Sets the bean's from range
     *
     * @param range the Range to get the from range.
     */
    private void setFromRange(String type, Range range) {
        // The value for display (fuzzy).
        String dispLabel = myFTConverter.getDisplayValue(type);

        // Undetermined?
        if (type.equals(CvFuzzyType.UNDETERMINED)) {
            myFromRange = dispLabel;
        }
        // Range type?
        else if (type.equals(CvFuzzyType.RANGE)) {
            myFromRange = range.getFromIntervalStart() + dispLabel
                    + range.getFromIntervalEnd();
        }
        // No fuzzy type?
        else if (type.length() == 0) {
            myFromRange = dispLabel + range.getFromIntervalStart();
        }
        else {
            // >, <, c or n
            myFromRange = dispLabel + range.getFromIntervalStart();
        }
    }

    /**
     * Returns an array of ints.
     *
     * @return an array of ints. int[0] = fromStart, int[1] = fromEnd, int[2] =
     *         toStart and int[3] toEnd.
     */
    private int[] getRangeValues() {
        // The array to return.
        int[] ranges = new int[4];

        // The array to hold the result.
        int[] results;
        results = getFromRangeValues();

        // Copy the from range values.
        ranges[0] = results[0];
        ranges[1] = results[1];

        // The match result for to range.
        results = getToRangeValues();

        // Copy the to range values.
        ranges[2] = results[0];
        ranges[3] = results[1];
        return ranges;
    }

    /**
     * A convenient method to get from range values in an int array.
     *
     * @return an arry of ints. int[0] = fromStart and int[1] = fromEnd
     */
    private int[] getFromRangeValues() {
        Matcher matcher = ourRangePattern.matcher(myFromRange);
        String fuzzyType = myFTConverter.getFuzzyShortLabel(matcher);
        return getRangeValues(fuzzyType, matcher);
    }

    /**
     * A convenient method to get to range values in an int array.
     *
     * @return an arry of ints. int[0] = toStart and int[1] = toEnd
     */
    private int[] getToRangeValues() {
        Matcher matcher = ourRangePattern.matcher(myToRange);
        String fuzzyType = myFTConverter.getFuzzyShortLabel(matcher);
        return getRangeValues(fuzzyType, matcher);
    }

    /**
     * Returns an array of ints.
     *
     * @param fuzzyType the fuzzy type as a string
     * @param matcher the matcher object to extract int values to return.
     * @return an array of ints. int[0] = fromStart, int[1] = fromEnd, int[2] =
     *         toStart and int[3] toEnd.
     */
    private int[] getRangeValues(String fuzzyType, Matcher matcher) {
        int[] ranges = new int[2];
        if (!fuzzyType.equals(CvFuzzyType.UNDETERMINED)) {
            if (fuzzyType.equals(CvFuzzyType.RANGE)) {
                // Range, 1..2 type
                // From value.
                ranges[0] = Integer.parseInt(matcher.group(2));
                // Check for negative values.
                if (matcher.group(1) != null && matcher.group(1).equals("-")) {
                    ranges[0] *= -1;
                }
                // End value
                ranges[1] = Integer.parseInt(matcher.group(5));
                // Check for negative values.
                if (matcher.group(4) != null && matcher.group(4).equals("-")) {
                    ranges[1] *= -1;
                }
            }
            else {
                // Other type, <2 or >2, c2 etc.
                ranges[0] = Integer.parseInt(matcher.group(5));
                if (matcher.group(1) != null && matcher.group(1).equals("-")) {
                    ranges[0] *= -1;
                }
                ranges[1] = ranges[0];
            }
        }
        return ranges;
    }

    /**
     * Sets the bean's to range
     *
     * @param range the Range to get the to range.
     */
    private void setToRange(String type, Range range) {
        // The value for display (fuzzy).
        String dispLabel = myFTConverter.getDisplayValue(type);

        // Undetermined?
        if (type.equals(CvFuzzyType.UNDETERMINED)) {
            myToRange = dispLabel;
        }
        // Range type?
        else if (type.equals(CvFuzzyType.RANGE)) {
            myToRange = range.getToIntervalStart() + dispLabel + range.getToIntervalEnd();
        }
        // No fuzzy type?
        else if (type.length() == 0) {
            myToRange = dispLabel + range.getToIntervalStart();
        }
        else {
            // >, <, c or n
            myToRange = dispLabel + range.getToIntervalEnd();
        }
    }

    private CvFuzzyType getFuzzyType(EditUserI user, String type) throws SearchException {
        // Set the from and to fuzzy types.
        CvFuzzyType fuzzyType = null;
        if (type.length() != 0) {
            fuzzyType = (CvFuzzyType) user.getObjectByLabel(CvFuzzyType.class, type);
        }
        return fuzzyType;
    }
}