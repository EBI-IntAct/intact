/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * <p>
 * Represents a location on a sequence.
 * </p>
 * <p>
 * Features with multiple positions on the sequence, e.g. structural
 * domains or PRINTS matches are represented by multiple range objects
 * attached to the Feature.
 * </p>
 * <p>
 * A Range may have a &quot;fuzzy&quot; start/end, e.g. 4..5 or &lt;5.
 * </p>
 * <p>
 * The table below shows the representation of both exact and &quot;fuzzy&quot;
 * features:
 * </p>
 * <p>
 * attribute 4-4 4-10 4..6-10 &lt;5-&gt;10 ?-10 undetermined
 * </p>
 * <p>
 * fromIntervalStart 4 4 4 5 null null
 * </p>
 * <p>
 * fromIntervalEnd 4 4 6 5 null null
 * </p>
 * <p>
 * toIntervalStart 4 10 10 10 10 null
 * </p>
 * <p>
 * toIntervalEnd 4 10 10 10 10 null
 * </p>
 * <p>
 * startFuzzyType exact exact interval lessThan undet. null
 * </p>
 * <p>
 * endFuzzyType exact exact exact greaterThan exact null
 * </p>
 * <p>
 * undetermined false false false false false true
 * </p>
 *
 *
 * @author Chris Lewington, hhe
 */
public class Range extends BasicObjectImpl {

    //------------ attributes ------------------------------------

    /**
     * Sequence size limit for this class
     */
    private static final int MAX_SEQ_SIZE = 100;

    /**
     * TODO Comments
     */
    private int fromIntervalStart = 0;

    /**
     * TODO Comments
     */
    private int fromIntervalEnd = 0;

    /**
     * TODO Comments
     */
    private int toIntervalStart = 0;

    /**
     * TODO Comments
     */
    private int toIntervalEnd = 0;

    /**
     * <p>
     * Contains the first 100 amino acids of the sequence in the Range. This is
     * purely used for data consistency checks. In case of sequence updates the
     * new position can be determined by sequence alignment.
     * </p>
     *
     */
    //NOTE: We will assume a maximum size of 100 characters for this
    private String sequence = "";

    /**
     * TODO Comments
     * This is really a boolean but we need to use a character for
     * it because Oracle does not support boolean types
     * NB JDBC spec has no JDBC type for char, only Strings!
     */
    private String undetermined = "N";

    /**
     * <p>
     * True if the Range describes a link between two positions in the
     * sequence, e.g. a sulfate bridge.
     * </p>
     * <p>
     * False otherwise.
     * </p>
     *
     * This is really a boolena but we need to use a character for
     * it because Oracle does not support boolean types
     * NB JDBC spec has no JDBC type for char, only Strings!
     */
    private String link = "N";

    /**
     * Only needed by OJB to get a handle to an inverse FK from Feature
     */
    private String featureAc;   //can go later by using OJB 'anonymous' field

    //------------------- cvObjects --------------------------------------

    /**
     * TODO Comments
     */
    private CvFuzzyType fromCvFuzzyType;
    private String fromCvFuzzyTypeAc;  //get rid of this later with OJB 'anonymous'
    /**
     * TODO Comments
     */
    private CvFuzzyType toCvFuzzyType;
    private String toCvFuzzyTypeAc;  //get rid of this later with OJB 'anonymous'


    //--------------------------- constructors --------------------------------------
    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    private Range() {
        super();
    }

    /**
     * Sets up a valid Range instance. Range is dependent on the feature and
     * hence it cannot exist on its own. Currently a valid Range must have at least
     * the following defined:
     *
     * @param owner the owner of this range.
     * @param fromStart The starting point of the 'from' interval for the Range.
     * @param fromEnd The end point of the 'from' interval.
     * @param toStart The starting point of the 'to' interval of the Range
     * @param toEnd The end point of the 'to' interval
     * @param seq The sequence - maximum of 100 characters (null allowed)
     *
     * <p>
     * NB ASSUMPTION: The progression of intervals is always assumed to be from 'left
     * to right' along the number line when defining intervals. Thus '-6 to -4',
     * '5 to 20' and '-7 to 15' are  all <b>valid</b> single intervals,
     * but '-3 to -8', '12 to 1' and  '5 to -7' are <b>not</b>.
     * </p>
     */
    public Range(Institution owner, int fromStart, int fromEnd, int toStart, int toEnd, String seq) {
        //NB negative intervals are allowed!! This needs more sophisticated checking..
        super(owner);

        if (fromEnd < fromStart) throw new IllegalArgumentException ("End of 'from' interval must be bigger than the start!");
        if (toEnd < toStart) throw new IllegalArgumentException ("End of 'to' interval must be bigger than the start!");
        if(fromEnd > toStart) throw new IllegalArgumentException("The 'from' and 'to' intervals cannot overlap!");
        if(fromStart > toEnd) throw new IllegalArgumentException("The 'from' interval starts beyond the 'to' interval!");
        if(fromStart > toStart) throw new IllegalArgumentException("The 'from' interval cannot begin during the 'to' interval!");
        //don't allow default empty String to be replaced by null. Check size also
        //to avoid unnecessary DB call for a seq that is too big...
        if(seq != null) {
            if(seq.length() > Range.MAX_SEQ_SIZE)
                throw new IllegalArgumentException("Sequence too big! Max allowed: "
                        + Range.MAX_SEQ_SIZE);
            sequence = seq;
        }
        this.fromIntervalStart = fromStart;
        this.fromIntervalEnd = fromEnd;
        this.toIntervalStart = toStart;
        this.toIntervalEnd = toEnd;
    }

    //------------------------- public methods --------------------------------------

    public int getFromIntervalStart() {
        return fromIntervalStart;
    }
    public void setFromIntervalStart(int posFrom) {
        fromIntervalStart = posFrom;
    }
    public int getFromIntervalEnd() {
        return fromIntervalEnd;
    }
    public void setFromIntervalEnd(int posTo) {
        fromIntervalEnd = posTo;
    }

    public int getToIntervalStart() {
        return toIntervalStart;
    }
    public void setToIntervalStart(int posFrom) {
        toIntervalStart = posFrom;
    }
    public int getToIntervalEnd() {
        return toIntervalEnd;
    }
    public void setToIntervalEnd(int posTo) {
        toIntervalEnd = posTo;
    }

    public boolean isUndertermined() {
        return charToBoolean(undetermined);
    }

    public void setUndetermined(boolean val) {
        undetermined = booleanToChar(val);
    }

    public boolean isLinked() {
        return charToBoolean(link);
    }

    public void setLink(boolean isLinked) {
        link = booleanToChar(isLinked);
    }

    public CvFuzzyType getFromCvFuzzyType() {
        return fromCvFuzzyType;
    }


    public void setFromCvFuzzyType(CvFuzzyType type) {
        fromCvFuzzyType = type;
    }

    public CvFuzzyType getToCvFuzzyType() {
        return toCvFuzzyType;
    }

    public void setToCvFuzzyType(CvFuzzyType type) {
        toCvFuzzyType = type;
    }

    public void setParentAc(String parentAc) {
        this.featureAc = parentAc;
    }

    /**
     * Equality for Ranges is currently based on equality for
     * <code>Modification</code>, position from and position to (ints).
     * @param o The object to check
     * @return true if the parameter equlas this object, false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Range)) return false;

        final Range range = (Range) o;

        //check the intervals are the same
        if (fromIntervalStart != range.fromIntervalStart) return false;
        if (fromIntervalEnd != range.fromIntervalEnd) return false;
        if (toIntervalStart != range.toIntervalStart) return false;
        if (toIntervalEnd != range.toIntervalEnd) return false;

        //check the booleans
        if(link != range.link) return false;
        if(undetermined != range.undetermined) return false;

        //check the from fuzzy types
        if (fromCvFuzzyType != null) {
            if (!fromCvFuzzyType.equals(range.fromCvFuzzyType)) return false;
        } else {
            if (range.fromCvFuzzyType != null) return false;
        }

        //check the to fuzzy types
        if (toCvFuzzyType != null) {
            if (!toCvFuzzyType.equals(range.toCvFuzzyType)) return false;
        } else {
            if (range.toCvFuzzyType != null) return false;
        }

        //check the sequence
        if (sequence != null) {
            if (!sequence.equals(range.sequence)) return false;
        } else {
            if (range.sequence != null) return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = fromIntervalStart;

        result = 29 * result + fromIntervalEnd;
        result = 29 * result + toIntervalStart;
        result = 29 * result + toIntervalEnd;

        //add in the sequence hashcode
        result = 29 * result + sequence.hashCode();

        return result;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        // Saves the from type as a short label
        String fromType = "";

        // Set the fuzzy type first as they are used in set range methods.
        if (fromCvFuzzyType != null) {
            fromType = fromCvFuzzyType.getShortLabel();
        }
        sb.append(getRange(fromType, fromIntervalStart, fromIntervalEnd));

        sb.append("-");

        // Saves the to type as a short label
        String toType = "";

        if (toCvFuzzyType != null) {
            toType = toCvFuzzyType.getShortLabel();
        }
        sb.append(getRange(toType, toIntervalStart, toIntervalEnd));
        return sb.toString();
    }


    /**
     * Returns a cloned version of the current object.
     * @return a cloned version of the current Range. The fuzzy types
     * are not cloned (shared).
     * @throws CloneNotSupportedException for errors in cloning this object.
     */
    public Object clone() throws CloneNotSupportedException {
        Range copy = (Range) super.clone();
        // Reset the parent ac.
        copy.featureAc = null;
        return copy;
    }

    //---------------- private utility methods -----------------------


    /**
     * Simple converter.
     * @param val boolean
     * @return "Y" if the boolean is true, "N" otherwise
     */
    private String booleanToChar(boolean val) {
          if(val) return "Y";
        return "N";
    }

    /**
     * Simple converter
     * @param st The String to convert
     * @return true if the String is "Y", false otherwise
     */
    private boolean charToBoolean(String st) {
        if(st.equals("N")) return false;
        return true;
    }

    /**
     * Sets the bean's from range
     */
    private String getRange(String type, int start, int end) {
        // The rage to return.
        String result;

        // The value for display (fuzzy).
        String dispLabel = CvFuzzyType.Converter.getInstance().getDisplayValue(type);

        // Single type?
        if (isSingleType(type)) {
            result = dispLabel;
        }
        // Range type?
        else if (type.equals(CvFuzzyType.RANGE)) {
            result = start + dispLabel + end;
        }
        // No fuzzy type?
        else if (type.length() == 0) {
            result = dispLabel + start;
        }
        else {
            // >, <, c or n
            result = dispLabel + start;
        }
        return result;
    }

    /**
     * @param type the CvFuzzy label to compare
     * @return true if <code>type</code> is of Untermined or C or N terminal types.
     * False is returned for all other instances.
     */
    private boolean isSingleType(String type) {
        return type.equals(CvFuzzyType.UNDETERMINED)
                || type.equals(CvFuzzyType.C_TERMINAL)
                || type.equals(CvFuzzyType.N_TERMINAL);
    }
} // end Range




