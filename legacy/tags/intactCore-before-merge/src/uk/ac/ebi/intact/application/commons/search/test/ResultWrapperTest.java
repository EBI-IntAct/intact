/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.commons.search.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.ebi.intact.application.commons.search.ResultWrapper;

/**
 * The test class for ResultWrapper class.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ResultWrapperTest extends TestCase {

    /**
     * Constructs an instance with the specified name.
     * @param name the name of the test.
     */
    public ResultWrapperTest(String name) {
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
        return new TestSuite(ResultWrapperTest.class);
    }

    /**
     * Tests the no result constructor.
     */
    public void testNoResultConstructor() {
        ResultWrapper rw = new ResultWrapper(10);
        assertTrue(rw.getResult().isEmpty());
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), 0);
    }

    /**
     * Tests the non empty constructor.
     */
    public void testNonEmptyConstructor() {
        List list = Arrays.asList(new String[]{"a", "b"});
        ResultWrapper rw = new ResultWrapper(list, 10);
        assertEquals(rw.getResult(), list);
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), list.size());
    }

    /**
     * Tests the too large constructor.
     */
    public void testTooLargeConstructor() {
        List list = Arrays.asList(new String[]{"a", "b", "c"});

        // max size < result size
        ResultWrapper rw = new ResultWrapper(list.size(), 2);
        assertTrue(rw.getResult().isEmpty());
        assertTrue(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), list.size());
    }

    /**
     * Tests the size acceptable constructor.
     */
    public void testSizeOKConstructor() {
        List list = Arrays.asList(new String[]{"a", "b", "c"});

        // max size == result size
        ResultWrapper rw = new ResultWrapper(list, 3);
        assertEquals(rw.getResult().size(), list.size());
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), list.size());

        // max size > result size
        rw = new ResultWrapper(list, 4);
        assertEquals(rw.getResult().size(), list.size());
        assertFalse(rw.isTooLarge());
        assertEquals(rw.getPossibleResultSize(), list.size());
    }
}
