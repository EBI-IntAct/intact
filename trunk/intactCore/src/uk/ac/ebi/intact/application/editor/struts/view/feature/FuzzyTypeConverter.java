/*
 Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
 All rights reserved. Please see the file LICENSE
 in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.editor.struts.view.feature;

import uk.ac.ebi.intact.model.CvFuzzyType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * This class is responsible for converting to/from CvFuzzyType to display formats.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FuzzyTypeConverter {

    // Class Data

    /**
     * Only instance of this class.
     */
    private static FuzzyTypeConverter ourInstance = new FuzzyTypeConverter();

    /**
     * Maps: CvFuzzy Labels -> Display labels.
     */
    private static Map ourNormalMap = new HashMap();

    static {
        ourNormalMap.put(CvFuzzyType.LESS_THAN, "<");
        ourNormalMap.put(CvFuzzyType.GREATER_THAN, ">");
        ourNormalMap.put(CvFuzzyType.UNDETERMINED, "?");
        ourNormalMap.put(CvFuzzyType.C_TERMINAL, "c");
        ourNormalMap.put(CvFuzzyType.N_TERMINAL, "n");
        ourNormalMap.put(CvFuzzyType.RANGE, "..");
    }

    /**
     * @return the only instance of this class.
     */
    public static FuzzyTypeConverter getInstance() {
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
        if (matcher.group(3) != null) {
            // Range type
            return CvFuzzyType.RANGE;
        }
        else if (matcher.group(1) != null) {
            if (ourNormalMap.containsValue(matcher.group(1))) {
                return (String) getKey(matcher.group(1));
            }
        }
        // None in the mapping file (normal type)
        return "";
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
