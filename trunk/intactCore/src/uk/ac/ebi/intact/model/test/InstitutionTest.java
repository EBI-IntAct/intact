// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.test.util.StringUtils;

/**
 * Test the basic methods of an Annotation.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class InstitutionTest extends TestCase {

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( InstitutionTest.class );
    }

    public void testConstructor_ok() {

        try {
            Institution institution = new Institution( "owner" );
            assertNotNull( institution );

        } catch ( Exception e ) {

            e.printStackTrace();
            fail( "That constructor call should have succeeded." );
        }
    }

    public void testConstructor_noShortlabel() {

        try {
            new Institution( null );
            fail( "That constructor should have thrown an exception." );

        } catch ( Exception e ) {

            // ok
        }

        try {
            // check that we don't allow empty shortlabel.
            new Institution( "  " );
            fail( "That constructor should have thrown an exception." );

        } catch ( Exception e ) {

            // ok
        }
    }

    public void testSetShortLabel() {

        // set a invalid CvTopic
        String name = "owner";
        Institution institution = new Institution( name );
        assertEquals( name.trim(), institution.getShortLabel() );

        // check trimming
        name = "  owner  ";
        institution = new Institution( name );
        assertEquals( name.trim(), institution.getShortLabel() );

        // check truncation
        name = StringUtils.generateStringOfLength( AnnotatedObject.MAX_SHORT_LABEL_LEN + 2 );
        institution = new Institution( name );
        assertEquals( AnnotatedObject.MAX_SHORT_LABEL_LEN,
                      institution.getShortLabel().length() );
    }
}