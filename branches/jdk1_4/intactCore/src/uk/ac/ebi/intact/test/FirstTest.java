/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.test;

import junit.framework.*;

/**
 * A template for a test class.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FirstTest extends TestCase {

    /**
     * Constructs a FirstTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public FirstTest(String name) {
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
        return new TestSuite(FirstTest.class);
    }

    /**
     * In this method, you should use various assert
     * methods of the super class to test conditions. For example,
     * <pre>
     * assertTrue("Didn't generate IntactTypes resource files correctly",
     *    compareResources(newResources, expectedResources));
     * </pre>
     * You can also use fail method to indicate that the test is a failure.
     * This is normally used in a catch block if a method is not expected to
     * throw an exception as shown below:
     * <pre>
     *   try {
     *      // Run some method and catch the exception.
     *      objtotest.someMethod();
     *   }
     *   catch (AssertFailureException ex) {
     *      // Fatal error; we shouldn't get this.
     *      fail(ex.getMessage());
     *   }
     * </pre>
     */
    public void testXXX() {
        // You do the testing here for someMethod() here.
    }
}
