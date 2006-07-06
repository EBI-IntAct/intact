// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.model.CvTopic;
import uk.ac.ebi.intact.model.Institution;

/**
 * Test the basic methods of an Annotation.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotationTest extends TestCase {

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( AnnotationTest.class );
    }

    private Institution owner;
    private CvTopic topic1;
    private CvTopic topic2;

    protected void setUp() throws Exception {
        super.setUp();

        // initialize required objects

        owner = new Institution( "owner" );
        assertNotNull( owner );

        topic1 = new CvTopic( owner, "topic1" );
        assertNotNull( topic1 );

        topic2 = new CvTopic( owner, "topic2" );
        assertNotNull( topic2 );
    }

    public void testConstructor_ok() {

        try {
            Annotation annotation = new Annotation( owner, topic1, "my comment" );
            assertNotNull( annotation );

        } catch ( Exception e ) {

            e.printStackTrace();
            fail( "That constructor call should have succeeded." );
        }
    }

    public void testConstructor2_ok() {

        try {
            Annotation annotation = new Annotation( owner, topic1 );
            assertNotNull( annotation );

        } catch ( Exception e ) {

            e.printStackTrace();
            fail( "That constructor call should have succeeded." );
        }
    }

    public void testConstructor3_missingInstitution() {

        try {
            new Annotation( null, topic1, "my comment" );
            fail( "Null Institution should not be allowed." );

        } catch ( Exception e ) {

            // ok.
        }
    }

    public void testConstructor3_missingCvTopic() {

        try {
            new Annotation( owner, null, "my comment" );
            fail( "Null CvTopic should not be allowed." );

        } catch ( Exception e ) {

            // ok.
        }

        try {
            new Annotation( owner, null );
            fail( "Null CvTopic should not be allowed." );

        } catch ( Exception e ) {

            // ok.
        }
    }

    public void testSetCvTopic() {

        // set a invalid CvTopic
        try {
            Annotation annotation = new Annotation( owner, topic1, "my comment" );
            annotation.setCvTopic( null );

            fail( "Null CvTopic should not be allowed." );

        } catch ( Exception e ) {

            // ok.
        }

        // set a valid CvTopic
        try {
            Annotation annotation1 = new Annotation( owner, topic1, "my comment" );
            annotation1.setCvTopic( topic2 );

            Annotation annotation2 = new Annotation( owner, topic2, "my comment" );
            assertEquals( annotation1, annotation2 );

        } catch ( Exception e ) {

            fail();
        }
    }

    public void testSetAnnotationText() {

        String text = " my comment ";
        Annotation annotation = new Annotation( owner, topic1, text );
        assertEquals( text.trim(), annotation.getAnnotationText() );

        text = null;
        annotation = new Annotation( owner, topic1, text );
        assertNull( annotation.getAnnotationText() );
    }

    public void testEquals() {

        Annotation annotation1 = new Annotation( owner, topic1, "my comment" );
        Annotation annotation2 = new Annotation( owner, topic1, "my comment" );
        assertEquals( annotation1, annotation2 );

        Annotation annotation3 = new Annotation( owner, topic2, "my comment" );
        assertNotSame( annotation1, annotation3 );
        assertNotSame( annotation2, annotation3 );
    }
}