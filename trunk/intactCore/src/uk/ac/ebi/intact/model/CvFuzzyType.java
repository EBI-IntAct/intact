package uk.ac.ebi.intact.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;


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

    // -- Start of Inner class ------------------------------------------------

    /**
     * This class is responsible for converting to/from CvFuzzyType to display formats.
     *
     * @author Sugath Mudali (smudali@ebi.ac.uk)
     * @version $Id$
     */
    public static class Converter {

        // Class Data

        /**
         * Only instance of this class.
         */
        private static Converter ourInstance = new Converter();

        /**
         * Maps: CvFuzzy Labels -> Display labels.
         */
        private static Map ourNormalMap = new HashMap();

        static {
            ourNormalMap.put(LESS_THAN, "<");
            ourNormalMap.put(GREATER_THAN, ">");
            ourNormalMap.put(UNDETERMINED, "?");
            ourNormalMap.put(C_TERMINAL, "c");
            ourNormalMap.put(N_TERMINAL, "n");
            ourNormalMap.put(RANGE, "..");
        }

        /**
         * @return the only instance of this class.
         */
        public static Converter getInstance() {
            return ourInstance;
        }

        /**
         * Returns the display value for given short label value.
         * @param shortLabel the short label to return the corresponding display value.
         * @return the display value for given short label or an empty string if there
         * is no mapping for <code>label</code> (as with the case of no fuzzy type).
         */
        public String getDisplayValue(String shortLabel) {
            if (ourNormalMap.containsKey(shortLabel)) {
                return (String) ourNormalMap.get(shortLabel);
            }
            return "";
        }

        /**
         * Returns the corresponding CvFuzzy short label for given display value.
         * @param value the display value.
         * @return the fuzzy label if there is a matching found <code>matcher</code>
         * object or an empty string to denote for no fuzzy or unknown type.
         */
        public String getFuzzyShortLabel(String value) {
            if (ourNormalMap.containsValue(value)) {
                return (String) getKey(value);
            }
            return null;
        }

        /**
         * Returns the corresponding CvFuzzy short label using given matcher object.
         * @param matcher the Matcher object to analyse
         * @return the fuzzy label if there is a matching found <code>matcher</code>
         * object or else an empty string (for a normal range).
         */
        public String getFuzzyShortLabel(Matcher matcher) {
            if (!matcher.matches()) {
                throw new IllegalArgumentException("No match found");
            }
            // The short label to return.
            String shortLabel = "";

            // for ? or c or n types
            if (matcher.group(1) != null) {
                shortLabel = (String) getKey(matcher.group(1));
            }
            else if (matcher.group(4) != null) {
                if (matcher.group(2) != null) {
                    // For < or > types
                    shortLabel = (String) getKey(matcher.group(2));
                }
            }
            else {
                // Range type
                shortLabel = CvFuzzyType.RANGE;
            }
            return shortLabel;
        }

        // Helper methods

        /**
         * Returns a key for given value.
         * @param value the value to search for the key.
         * @return the corresponding key for given <code>value</code>.
         */
        private static Object getKey(Object value) {
            for (Iterator iter = ourNormalMap.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                if (entry.getValue().equals(value)) {
                    return entry.getKey();
                }
            }
            assert false;
            // Should never happen.
            return null;
        }
    }

    // --- End of Inner class -------------------------------------------------

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
