/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A template for AllJUnitTests class.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class AllJUnitTests extends TestCase {

    /**
     * Constructs an AllJUnitTests instance with the specified name.
     *
     * @param name the name of the test.
     */
    public AllJUnitTests( String name ) {
        super( name );
    }

    /**
     * Returns a suite containing tests.
     * <p/>
     * </br><b>OCL:</b>
     * <pre>
     * post: return != null
     * post: return->forall(obj : Object | obj.oclIsTypeOf(Test))
     * </pre>
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();

        // Add tests one by one.
        suite.addTest( InstitutionTest.suite() );
        suite.addTest( XrefTest.suite() );
        suite.addTest( AliasTest.suite() );
        suite.addTest( AnnotationTest.suite() );
        suite.addTest( CvFuzzyTypeTest.suite() );
        suite.addTest( AnnotatedObjectTest.suite() );

        suite.addTest(ProteinTest.suite());
        suite.addTest(NucleicAcidTest.suite());

        // will be replaced by smaller test in dedicated files.
//        suite.addTest( EqualityTests.suite() );

        // not implemented yet, those tests are empty.
//        suite.addTest(RangeTest.suite());
//        suite.addTest(ExperimentTest.suite());

        return suite;
    }
}
