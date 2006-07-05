/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.uniprotExport.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Testsuite that is composed of the individual JUnit test suites. Any new test
 * suite should be added here.
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
     *                                                 post: return != null
     *                                                 post: return->forall(obj : Object | obj.oclIsTypeOf(TestSuite))
     *                                                 </pre>
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();

        // Add your test suite here.
        suite.addTest( DRLineExportTest.suite() );
        suite.addTest( CcLineTest.suite() );

        return suite;
    }
}
