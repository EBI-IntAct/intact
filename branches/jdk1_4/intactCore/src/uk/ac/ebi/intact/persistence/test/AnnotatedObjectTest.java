// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.persistence.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.util.TestCaseHelper;

/**
 * Test the persistence of an AnnotatedObject
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotatedObjectTest extends TestCase {

    /**
     *
     */
    IntactHelper helper;
    TestCaseHelper testHelper;
    Institution owner;

    /**
     * Constructor
     *
     * @param name the name of the test.
     */
    public AnnotatedObjectTest( String name ) throws Exception {
        super( name );
        testHelper = new TestCaseHelper();
        helper = testHelper.getHelper();
        owner = new Institution( "EBI-TEST-OWNER" );
    }

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws Exception {
        super.setUp();
        testHelper.setUp();

    }

    /**
     * Tears down the test fixture. Called after every test case method.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        testHelper.tearDown();
    }

    /**
     * Test the addition and removal of Xref objects.
     *
     * @throws IntactException
     */
    public void testXref() throws IntactException {
        // Set up required objects
        System.out.println( "Doing Xref test (AnnotatedObject Test Case)..." );
        Protein p1 = (Protein) helper.getObjectByLabel( Protein.class, "prot1" );

        CvDatabase db1 = (CvDatabase) helper.getObjectByLabel( CvDatabase.class, "testCvDb" );
        Xref x1 = new Xref( owner, db1, "xx1", null, null, null );
        Xref x2 = new Xref( owner, db1, "xx1", null, null, null );
        System.out.println( "example Xrefs created..." );
        System.out.println( "Doing add test..." );

        // get the initial state
        int xrefCount = p1.getXrefs().size();

        // check addition of xref
        p1.addXref( x1 );
        assertEquals( xrefCount + 1, p1.getXrefs().size() );

        // x2 should not be added, it has the same content as x1
        p1.addXref( x2 );
        assertEquals( xrefCount + 1, p1.getXrefs().size() );

        // change x2, now it should be added
        x2.setPrimaryId( "xx2" );
        p1.addXref( x2 );
        assertEquals( xrefCount + 2, p1.getXrefs().size() );

        // Test removal
        p1.removeXref( x2 );
        p1.removeXref( x1 );
        assertEquals( xrefCount, p1.getXrefs().size() );

        System.out.println( "AnnotatedObject tests done." );
        System.out.println();
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( AnnotatedObjectTest.class );
    }
}