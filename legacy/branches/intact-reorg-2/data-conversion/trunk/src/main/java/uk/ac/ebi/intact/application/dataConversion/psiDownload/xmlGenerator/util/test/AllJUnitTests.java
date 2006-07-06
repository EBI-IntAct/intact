/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.util.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Testsuite that is composed of the individual JUnit test suites. Any new test suite should be added here.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class AllJUnitTests extends TestCase {

    /**
     * The constructor with the test name.
     *
     * @param name the name of the test.
     */
    public AllJUnitTests( final String name ) {
        super( name );
    }

    /**
     * Returns a suite containing tests.
     *
     * @return a suite containing tests.
     *         <p/>
     *         <pre>
     *         <p/>
     *                               post: return != null
     *         <p/>
     *                               post: return->forall(obj : Object | obj.oclIsTypeOf(TestSuite))
     *         <p/>
     *                               </pre>
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite();

        // checking factories...
        suite.addTest( ToolBoxTest.suite() );

        return suite;
    }
}
