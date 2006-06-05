package uk.ac.ebi.intact.application.search3.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Michael Kleen
 * @version HttpUnitTests.java Date: Jan 26, 2005 Time: 11:47:39 PM
 */
public class HttpUnitTests extends TestCase {

    /**
     * The constructor with the test name.
     *
     * @param name the name of the test.
     */
    public HttpUnitTests(String name) {
        super(name);
    }

    /**
     * Returns a suite containing tests.
     *
     * @return a suite containing tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(TooLargeTest.suite());
        suite.addTest(BinarTest.suite());
        return suite;
    }
}


