/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.go.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Testsuite for testing GoUtils. Use test-go-stdout target of the main build file
 * to run this test.
 *
 * @author Sugath Mudali
 * @version $Id$
 */
public class AllJUnitTests extends TestCase {

    /**
     * The constructor with the test name.
     *
     * @param name the name of the test.
     */
    public AllJUnitTests( String name ) {
        super( name );
    }

    /**
     * Returns a suite containing tests.
     *
     * @return a suite containing tests.
     *         <p/>
     *         <pre>
     *         post: return != null
     *         post: return->forall(obj : Object | obj.oclIsTypeOf(TestSuite))
     *         </pre>
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        // Need to run go_initdb script in scripts/postgres/test dir first.
        suite.addTest(GoToolsTest.suite());
        return suite;
    }
}
