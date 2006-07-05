/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * The test class for the Range class.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class RangeTest extends TestCase {

    /**
     * Constructs an instance with the specified name.
     *
     * @param name the name of the test.
     */
    public RangeTest( String name ) {
        super( name );
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
        return new TestSuite( RangeTest.class );
    }
}
