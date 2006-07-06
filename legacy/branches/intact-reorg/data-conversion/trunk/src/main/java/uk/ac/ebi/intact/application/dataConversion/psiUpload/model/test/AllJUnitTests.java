/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.dataConversion.psiUpload.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Testsuite that is composed of the individual JUnit test suites. Any new test suite should be added here.
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
    public AllJUnitTests( final String name ) {
        super( name );
    }

    /**
     * Returns a suite containing tests.
     *
     * @return a suite containing tests.
     *         <p/>
     *         <pre>
     *
     *                       post: return != null
     *
     *                       post: return->forall(obj : Object | obj.oclIsTypeOf(TestSuite))
     *
     *                       </pre>
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite();

        suite.addTest( uk.ac.ebi.intact.application.dataConversion.psiUpload.model.util.test.AllJUnitTests.suite() );

        // Add your test suite here.
        suite.addTest( XrefTest.suite() );
        suite.addTest( AnnotationTest.suite() );
        suite.addTest( TissueTest.suite() );
        suite.addTest( CellTypeTest.suite() );
        suite.addTest( OrganismTest.suite() );
        suite.addTest( HostOrganismTest.suite() );
        suite.addTest( ConfidenceTest.suite() );
        suite.addTest( ExpressedInTest.suite() );
        suite.addTest( InteractionDetectionTest.suite() );
        suite.addTest( InteractionTypeTest.suite() );
        suite.addTest( ParticipantDetectionTest.suite() );
        suite.addTest( ProteinInteractorTest.suite() );
        suite.addTest( ProteinParticipantTest.suite() );
        suite.addTest( FeatureTest.suite() );
        suite.addTest( ExperimentDescriptionTest.suite() );
        suite.addTest( InteractionTest.suite() );
        suite.addTest( EntryTest.suite() );
        return suite;
    }
}
