package uk.ac.ebi.intact.model;


/**
 * <p>
 * Represents types of &quot;fuzzy&quot; Range start and end positions. Examples are
 * &quot;less than&quot;, &quot;greater than&quot;, &quot;undetermined&quot;.
 * </p>
 *
 * @author Chris Lewington
 * $Id$
 */
public class CvFuzzyType extends CvObject implements Editable {

    /**
     * The constant name for <.
     */
    public static final String LESS_THAN = "less-than";

    /**
     * The constant for >.
     */
    public static final String GREATER_THAN = "greater-than";

    /**
     * The constant for range.
     */
    public static final String RANGE = "range";

    /**
     * The constant for undetermined.
     */
    public static final String UNDETERMINED = "undetermined";

    /**
     * The constant for c-terminal.
     */
    public static final String C_TERMINAL = "c-terminal";

    /**
     * The constant for n-terminal.
     */
    public static final String N_TERMINAL = "n-terminal";

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    private CvFuzzyType() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvFuzzyType instance. Requires at least a shortLabel and an
     * owner to be specified.
     * @param shortLabel The memorable label to identify this CvFuzzyType
     * @param owner The Institution which owns this CvFuzzyType
     * @exception NullPointerException thrown if either parameters are not specified
     */
    public CvFuzzyType(Institution owner, String shortLabel) {

        //super call sets up a valid CvObject
        super(owner, shortLabel);
    }

}
