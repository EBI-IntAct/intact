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
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditKeyBean;
import uk.ac.ebi.intact.model.CvFuzzyType;
import uk.ac.ebi.intact.model.Range;

/**
 * Bean to store a range for a feature.
 * 
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class RangeBean extends AbstractEditKeyBean implements Cloneable {

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

        // Extract the fuzzy type for the from range.
        String fromType = denormalizeFuzzyType(myFromRange);
        // From range as a string.
        String fromRange = getRange(fromType, myFromRange);

        try {
            // Parse the from range.
            if (!isRangeValid(fromType, fromRange)) {
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

        // Extract the fuzzy type for the to range.
        String toType = denormalizeFuzzyType(myToRange);
        // To Range as a string.
        String toRange = getRange(toType, myToRange);

        try {
            // Parse the to range.
            if (!isRangeValid(toType, toRange)) {
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
        // Do the parsing to get the values for 'from'.
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
        // Do the parsing to get the values for 'to'.
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
        // The range to rturn.
        Range range = myRange;

        // Create a new range object if the current range is null (for new range)
        if (range == null) {
            // Construct a range object to return.
            range = new Range(user.getInstitution(), fromStart, fromEnd, toStart,
                    toEnd, null);
        }
        else {
            // Update the ranges for the existing range.
            range.setFromIntervalStart(fromStart);
            range.setFromIntervalEnd(fromEnd);
            range.setToIntervalStart(toStart);
            range.setToIntervalEnd(toEnd);
        }
        // Set the fuzzy types.
        range.setFromCvFuzzyType(fromFuzzyType);
        range.setToCvFuzzyType(toFuzzyType);

        range.setLink(myLink);
        range.setUndetermined(myUndetermined);

        return range;
    }

    /**
     * Makes a clone of this object.
     * 
     * @return a cloned version of the current instance.
     * @throws CloneNotSupportedException for errors in cloning.
     */
    public Object clone() throws CloneNotSupportedException {
        RangeBean copy = (RangeBean) super.clone();
        // Reset to view mode.
        copy.setEditState(AbstractEditBean.VIEW);
        return copy;
    }

    /**
     * Make a copy of the current object. The key is set to a different key. Apart
     * from that this method is similar to the {@link #clone()} method.
     * @return a copy of the current object with key reset to a different key.
     */
    public RangeBean copy() {
        RangeBean copy = null;
        try {
            copy = (RangeBean) clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
        // Reset the key to a different key.
        System.out.println("Before reset: " + copy.getFromRange() + " key: " + copy.getKey());
        copy.reset();
        System.out.println("After reset: " + copy.getFromRange() + " key: " + copy.getKey());
        return copy;
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