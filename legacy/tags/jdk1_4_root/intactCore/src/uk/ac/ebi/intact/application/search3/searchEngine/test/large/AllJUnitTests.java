/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search3.searchEngine.test.large;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TODO comment that ...
 *
 * @author Anja Friedrichsen
 * @version $Id$
 */
public class AllJUnitTests extends TestCase {

    /**
     * The constructor with the test name.
     *
     * @param name the name of the test.
     */
    public AllJUnitTests(final String name) {
        super(name);
    }

    /**
     * Returns a suite containing tests.
     *
     * @return a suite containing tests.
     *         <p/>
     *         <pre>
     *                                            post: return != null
     *                                            post: return->forall(obj : Object | obj.oclIsTypeOf(TestSuite))
     *                                         </pre>
     *         <p/>
     *         The CreateLargeIndexTest must be called first, otherwise there is no index available
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite();
//        suite.addTest(CreateLargeIndexTest.suite());
        suite.addTest(SearchProteinTest.suite());
        suite.addTest(SearchExperimentsTest.suite());
        suite.addTest(SearchInteractionsTest.suite());
        suite.addTest(SearchCvObjectTest.suite());
        suite.addTest(SearchXrefTest.suite());
        suite.addTest(SearchCvsTest.suite());
        suite.addTest(SearchCvDatabaseTest.suite());
        suite.addTest(SearchBioSourceTest.suite());
        suite.addTest(SearchAnnotationTest.suite());
        return suite;
    }

}
