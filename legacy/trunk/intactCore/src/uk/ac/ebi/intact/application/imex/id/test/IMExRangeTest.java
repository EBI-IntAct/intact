/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.imex.id.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.imex.id.IMExRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * IMExRange Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05/15/2006</pre>
 */
public class IMExRangeTest extends TestCase {
    public IMExRangeTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( IMExRangeTest.class );
    }

    /////////////////////
    // Tests

    public void testImexRangeFromTo() {
        try {
            new IMExRange( 1, 14, 13, "IntAct" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            new IMExRange( 1, 13, 13, "IntAct" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            new IMExRange( 13, 13 );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testGetTimestamp() {
        assertNotNull( new IMExRange( 1, 10, 13, "IntAct" ).getTimestamp() );
        assertNull( new IMExRange( 10, 13 ).getTimestamp() );
    }

    public void testGetSubmissionId() {
        assertEquals( 1, new IMExRange( 1, 10, 13, "IntAct" ).getSubmissionId() );

        assertEquals( -1, new IMExRange( 10, 13 ).getSubmissionId() );
    }

    public void testGetPartner() {
        assertEquals( "IntAct", new IMExRange( 1, 10, 13, "IntAct" ).getPartner() );
        assertNull( new IMExRange( 10, 13 ).getPartner() );
    }

    public void testGetFrom() {
        assertEquals( 10, new IMExRange( 1, 10, 13, "IntAct" ).getFrom() );
        assertEquals( 10, new IMExRange( 10, 13 ).getFrom() );
    }

    public void testGetTo() {
        assertEquals( 13, new IMExRange( 1, 10, 13, "IntAct" ).getTo() );
        assertEquals( 13, new IMExRange( 10, 13 ).getTo() );
    }

    public void testIterator() {
        IMExRange range = new IMExRange( 1, 10, 13, "IntAct" );

        Collection<Long> values = new ArrayList<Long>();
        Iterator<Long> iterator = range.iterator();

        while ( iterator.hasNext() ) {
            values.add( iterator.next() );
        }

        assertEquals( 4, values.size() );
        values.contains( 10 );
        values.contains( 11 );
        values.contains( 12 );
        values.contains( 13 );
    }
}
