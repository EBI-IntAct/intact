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
