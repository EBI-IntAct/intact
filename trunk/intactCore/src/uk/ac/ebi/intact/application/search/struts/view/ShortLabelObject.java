/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.view;

/**
 * This class holds the data for addCV.jsp to display the existing short
 * labels for given topic. The number of columns to display is limited to
 * MAX_COLS; should you change this value to some other value, then don't
 * forget to make necessary adjustments to addCV.jsp as well.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ShortLabelObject {

    /**
     * Maximum number of columns.
     */
    public static final int MAX_COLS = 5;

    /**
     * Array to hold data for 5 columns.
     */
    private String[] myCols = new String[MAX_COLS];

    /**
     * Sets value in the internal array for given position.
     *
     * @param pos the position in the array.
     * @param value the value to set in <code>pos</code>.
     *
     * <pre>
     * pre: pos >= 0 and pos < MAX_COLS
     * post: myCols[pos] == value
     * </pre>
     */
    public void setValue(int pos, String value) {
        myCols[pos] = value;
    }

    /**
     * Returns the value at position 0.
     */
    public String getValue0() {
        return getValue(0);
    }

    /**
     * Returns the value at position 1.
     */
    public String getValue1() {
        return getValue(1);
    }

    /**
     * Returns the value at position 2.
     */
    public String getValue2() {
        return getValue(2);
    }

    /**
     * Returns the value at position 3.
     */
    public String getValue3() {
        return getValue(3);
    }

    /**
     * Returns the value at position 4.
     */
    public String getValue4() {
        return getValue(4);
    }

    /**
     * Returns the value stored in given position.
     *
     * @param pos the position in the array to get the value for.
     * @return the value stored at <code>pos</code>. An empty string is
     * returned if no value is stored in <code>pos</code>.
     *
     * <pre>
     * pre: pos >= 0 and pos < MAX_COLS
     * post: return == myCols[pos]
     * </pre>
     */
    private String getValue(int pos) {
        if (myCols[pos] == null) {
            return "";
        }
        return myCols[pos];
    }
}
