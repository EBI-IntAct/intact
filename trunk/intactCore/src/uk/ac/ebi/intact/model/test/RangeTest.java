/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvFuzzyType;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.Range;

/**
 * The test class for the Range class.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class RangeTest extends TestCase {

    /**
     * Constructs an instance with the specified name.
     * @param name the name of the test.
     */
    public RangeTest(String name) {
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
        return new TestSuite(RangeTest.class);
    }

    /**
     * Tests the constructor.
     */
    public void testToString() {
        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            doTestToString(helper);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
        finally {
            if (helper != null) {
                try {
                    helper.closeStore();
                }
                catch (IntactException e) {}
            }
        }
    }

    // Helper methods

    private void doTestToString(IntactHelper helper) throws IntactException {
        CvFuzzyType lessThan = (CvFuzzyType) helper.getObjectByLabel(CvFuzzyType.class,
                CvFuzzyType.LESS_THAN);
        CvFuzzyType greaterThan = (CvFuzzyType) helper.getObjectByLabel(CvFuzzyType.class,
                CvFuzzyType.GREATER_THAN);
        CvFuzzyType undetermined = (CvFuzzyType) helper.getObjectByLabel(CvFuzzyType.class,
                CvFuzzyType.UNDETERMINED);
        CvFuzzyType range = (CvFuzzyType) helper.getObjectByLabel(CvFuzzyType.class,
                CvFuzzyType.RANGE);
        CvFuzzyType ct = (CvFuzzyType) helper.getObjectByLabel(CvFuzzyType.class,
                CvFuzzyType.C_TERMINAL);
        CvFuzzyType nt = (CvFuzzyType) helper.getObjectByLabel(CvFuzzyType.class,
                CvFuzzyType.N_TERMINAL);

        Range testRange = null;

        // Cache the institution.
        Institution inst = helper.getInstitution();

        // No fuzzy type
        testRange = new Range(inst, 2, 2, 3, 3, null);
        assertEquals(testRange.toString(), "2-3");

        // No fuzzy type associated.
        assertNull(testRange.getFromCvFuzzyType());
        assertNull(testRange.getToCvFuzzyType());

        // < fuzzy type for from range
        testRange = new Range(inst, 2, 2, 3, 3, null);
        testRange.setFromCvFuzzyType(lessThan);

        assertEquals(testRange.toString(), "<2-3");
        // Fuzzy type is greater for from type.
        assertEquals(testRange.getFromCvFuzzyType(), lessThan);
        // Fuzzy type is null for to type.
        assertNull(testRange.getToCvFuzzyType());

        // > fuzzy type for from range
        testRange = new Range(inst, 2, 2, 3, 3, null);
        testRange.setFromCvFuzzyType(greaterThan);
        assertEquals(testRange.toString(), ">2-3");
        // Fuzzy type is greater for from type.
        assertEquals(testRange.getFromCvFuzzyType(), greaterThan);
        // Fuzzy type is null for to type.
        assertNull(testRange.getToCvFuzzyType());

        // < for both ranges
        testRange = new Range(inst, 2, 2, 3, 3, null);
        testRange.setFromCvFuzzyType(lessThan);
        testRange.setToCvFuzzyType(lessThan);
        assertEquals(testRange.toString(), "<2-<3");
        // Fuzzy type is greater for from type.
        assertEquals(testRange.getFromCvFuzzyType(), lessThan);
        // Fuzzy type is greater for to type.
        assertEquals(testRange.getToCvFuzzyType(), lessThan);

        // > for both ranges
        testRange = new Range(inst, 2, 2, 3, 3, null);
        testRange.setFromCvFuzzyType(greaterThan);
        testRange.setToCvFuzzyType(greaterThan);
        assertEquals(testRange.toString(), ">2->3");
        // Fuzzy type is greater for from type.
        assertEquals(testRange.getFromCvFuzzyType(), greaterThan);
        // Fuzzy type is greater for to type.
        assertEquals(testRange.getToCvFuzzyType(), greaterThan);

        // > for from and < for to ranges
        testRange = new Range(inst, 2, 2, 3, 3, null);
        testRange.setFromCvFuzzyType(greaterThan);
        testRange.setToCvFuzzyType(lessThan);
        assertEquals(testRange.toString(), ">2-<3");
        // Fuzzy type is greater for from type.
        assertEquals(testRange.getFromCvFuzzyType(), greaterThan);
        // Fuzzy type is less than for to type.
        assertEquals(testRange.getToCvFuzzyType(), lessThan);

        // < for from and > for to ranges
        testRange = new Range(inst, 2, 2, 3, 3, null);
        testRange.setFromCvFuzzyType(lessThan);
        testRange.setToCvFuzzyType(greaterThan);
        assertEquals(testRange.toString(), "<2->3");
        // Fuzzy type is less than for from type.
        assertEquals(testRange.getFromCvFuzzyType(), lessThan);
        // Fuzzy type is greater for to type.
        assertEquals(testRange.getToCvFuzzyType(), greaterThan);

        // fuzzy type is undetermined
        testRange = new Range(inst, 0, 0, 0, 0, null);
        testRange.setFromCvFuzzyType(undetermined);
        testRange.setToCvFuzzyType(undetermined);
        assertEquals(testRange.toString(), "?-?");
        // Fuzzy type is undetermined for from type.
        assertEquals(testRange.getFromCvFuzzyType(), undetermined);
        // Fuzzy type is undetermined for to type.
        assertEquals(testRange.getToCvFuzzyType(), undetermined);

        // from fuzzy type is range
        testRange = new Range(inst, 1, 2, 2, 2, null);
        testRange.setFromCvFuzzyType(range);
        assertEquals(testRange.toString(), "1..2-2");
        // Fuzzy type is range for from type.
        assertEquals(testRange.getFromCvFuzzyType(), range);
        // Fuzzy type is unknown for to type.
        assertNull(testRange.getToCvFuzzyType());

        // to fuzzy type is range
        testRange = new Range(inst, 1, 1, 2, 3, null);
        testRange.setToCvFuzzyType(range);
        assertEquals(testRange.toString(), "1-2..3");
        // Fuzzy type is unknown for from type.
        assertNull(testRange.getFromCvFuzzyType());
        // Fuzzy type is range for to type.
        assertEquals(testRange.getToCvFuzzyType(), range);

        // from and to fuzzy types are ranges
        testRange = new Range(inst, 1, 2, 2, 3, null);
        testRange.setFromCvFuzzyType(range);
        testRange.setToCvFuzzyType(range);
        assertEquals(testRange.toString(), "1..2-2..3");
        // Fuzzy type is range for from type.
        assertEquals(testRange.getFromCvFuzzyType(), range);
        // Fuzzy type is range for to type.
        assertEquals(testRange.getToCvFuzzyType(), range);

        // c for for range
        testRange = new Range(inst, 0, 0, 3, 3, null);
        testRange.setFromCvFuzzyType(ct);
        assertEquals(testRange.toString(), "c-3");
        // Fuzzy type is c-terminal for from type.
        assertEquals(testRange.getFromCvFuzzyType(), ct);
        // Fuzzy type is null for to type.
        assertNull(testRange.getToCvFuzzyType());

        // c for for both ranges
        testRange = new Range(inst, 0, 0, 0, 0, null);
        testRange.setFromCvFuzzyType(ct);
        testRange.setToCvFuzzyType(ct);
        assertEquals(testRange.toString(), "c-c");
        // Fuzzy type is c-terminal for from type.
        assertEquals(testRange.getFromCvFuzzyType(), ct);
        // Fuzzy type is c-terminal for to type.
        assertEquals(testRange.getToCvFuzzyType(), ct);

        // c for from and n for to
        testRange = new Range(inst, 0, 0, 0, 0, null);
        testRange.setFromCvFuzzyType(ct);
        testRange.setToCvFuzzyType(nt);
        assertEquals(testRange.toString(), "c-n");
        // Fuzzy type is c-terminal for from type.
        assertEquals(testRange.getFromCvFuzzyType(), ct);
        // Fuzzy type is n-terminal for to type.
        assertEquals(testRange.getToCvFuzzyType(), nt);

        // n for for from and n for to range
        testRange = new Range(inst, 0, 0, 3, 3, null);
        testRange.setFromCvFuzzyType(nt);
        assertEquals(testRange.toString(), "n-3");
        // Fuzzy type is n-terminal for from type.
        assertEquals(testRange.getFromCvFuzzyType(), nt);
        // Fuzzy type is null for to type.
        assertNull(testRange.getToCvFuzzyType());

        // n for for range
        testRange = new Range(inst, 0, 0, 0, 0, null);
        testRange.setFromCvFuzzyType(nt);
        testRange.setToCvFuzzyType(nt);
        assertEquals(testRange.toString(), "n-n");
        // Fuzzy type is n-terminal for from type.
        assertEquals(testRange.getFromCvFuzzyType(), nt);
        // Fuzzy type is n-terminal for to type.
        assertEquals(testRange.getToCvFuzzyType(), nt);
    }
}
