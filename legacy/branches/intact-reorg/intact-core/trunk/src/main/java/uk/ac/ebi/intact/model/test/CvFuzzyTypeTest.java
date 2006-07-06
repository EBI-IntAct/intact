/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.CvFuzzyType;

import java.util.regex.Pattern;

/**
 * Test class for CvFuzzyType
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CvFuzzyTypeTest extends TestCase {

    /**
     * Constructs an instance with the specified name.
     * @param name the name of the test.
     */
    public CvFuzzyTypeTest(String name) {
        super(name);
    }

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() {
        // Write setting up code for each test.
    }

    /**
     * Tears down the test fixture. Called after every test case method.
     */
    protected void tearDown() {
        // Release resources for after running a test.
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(CvFuzzyTypeTest.class);
    }

    public void testNormalize() {
        CvFuzzyType.Converter FTC = CvFuzzyType.Converter.getInstance();
        assertEquals(FTC.getDisplayValue(CvFuzzyType.LESS_THAN), "<");
        assertEquals(FTC.getDisplayValue(CvFuzzyType.GREATER_THAN), ">");
        assertEquals(FTC.getDisplayValue(CvFuzzyType.UNDETERMINED), "?");
        assertEquals(FTC.getDisplayValue(CvFuzzyType.C_TERMINAL), "c");
        assertEquals(FTC.getDisplayValue(CvFuzzyType.N_TERMINAL), "n");
        assertEquals(FTC.getDisplayValue(CvFuzzyType.RANGE), "..");
    }

    public void testGetFuzzyShortLabel() {
        CvFuzzyType.Converter FTC = CvFuzzyType.Converter.getInstance();

        // The pattern for matching the range (COPIED from RangeBean in the editor)
        Pattern pattern = Pattern.compile(
                "^(\\?|c|n)$|^(<|>)?+(-)*?(\\d+)$|^(-)*?(\\d+)\\.\\.(-)*?(\\d+)$");

        assertEquals(FTC.getFuzzyShortLabel(pattern.matcher("<1")), CvFuzzyType.LESS_THAN);
        assertEquals(FTC.getFuzzyShortLabel(pattern.matcher(">1")), CvFuzzyType.GREATER_THAN);
        assertEquals(FTC.getFuzzyShortLabel(pattern.matcher("?")), CvFuzzyType.UNDETERMINED);
        assertEquals(FTC.getFuzzyShortLabel(pattern.matcher("c")), CvFuzzyType.C_TERMINAL);
        assertEquals(FTC.getFuzzyShortLabel(pattern.matcher("n")), CvFuzzyType.N_TERMINAL);

        // Normal range.
        assertEquals(FTC.getFuzzyShortLabel(pattern.matcher("3")), "");
        // Range
        assertEquals(FTC.getFuzzyShortLabel(pattern.matcher("1..2")), CvFuzzyType.RANGE);

        // Two mixed types. error
        try {
            FTC.getFuzzyShortLabel(pattern.matcher("<2..3"));
        }
        catch (IllegalArgumentException iae) {
            assertTrue(true);
        }
    }
}
