package uk.ac.ebi.intact.application.search3.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Michael Kleen
 * @version AllJunitTests.java Date: Jan 20, 2005 Time: 1:56:47 PM
 */
public class AllJUnitTests extends TestCase {

    /**
     * The constructor with the test name.
     *
     * @param name the name of the test.
     */
    public AllJUnitTests(String name) {
        super(name);
    }

    /**
     * Returns a suite containing tests.
     *
     * @return a suite containing tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(SearchValidatorTest.suite());
        return suite;
    }
}
