/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.imex.id.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.imex.id.IMExIdTransformer;
import uk.ac.ebi.intact.application.imex.id.IMExRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * IMExIdTransformer Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05/15/2006</pre>
 */
public class IMExIdTransformerTest extends TestCase {
    public IMExIdTransformerTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( IMExIdTransformerTest.class );
    }

    /////////////////////
    // Tests

    public void testFormatIMExId() {

        assertEquals( "IM-1", IMExIdTransformer.formatIMExId( 1 ) );
        assertEquals( "IM-999", IMExIdTransformer.formatIMExId( 999 ) );

        try {
            IMExIdTransformer.formatIMExId( -1 );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testParseIMExId() {

        assertEquals( 1, IMExIdTransformer.parseIMExId( "IM-1" ) );
        assertEquals( 123, IMExIdTransformer.parseIMExId( "IM-123" ) );
        assertEquals( 1234567890L, IMExIdTransformer.parseIMExId( "IM-1234567890" ) );

        try {
            IMExIdTransformer.parseIMExId( "IM-1x3" );
            fail( "expected a format exception" );
        } catch ( Exception e ) {
            // ok
        }

    }

    public void testFormatSimpleRange() {

        assertEquals( "2..10", IMExIdTransformer.formatSimpleRange( new IMExRange( 2, 10 ) ) );
        assertEquals( "12..1234567890", IMExIdTransformer.formatSimpleRange( new IMExRange( 12, 1234567890 ) ) );

        try {
            IMExIdTransformer.formatSimpleRange( null );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testParseSimpleRange() {

        assertEquals( new IMExRange( 3, 11 ), IMExIdTransformer.parseSimpleRange( "3..11" ) );
        assertEquals( new IMExRange( 36, 1234567890 ), IMExIdTransformer.parseSimpleRange( "36..1234567890" ) );

        try {
            IMExIdTransformer.parseSimpleRange( "3.x.11" );
            fail( "expected format exception." );
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testGetFormattedIMExIds() {
        Collection<String> ids = IMExIdTransformer.getFormattedIMExIds( new IMExRange( 3, 8 ) );

        assertEquals( 6, ids.size() );

        assertTrue( ids.contains( "IM-3" ) );
        assertTrue( ids.contains( "IM-4" ) );
        assertTrue( ids.contains( "IM-5" ) );
        assertTrue( ids.contains( "IM-6" ) );
        assertTrue( ids.contains( "IM-7" ) );
        assertTrue( ids.contains( "IM-8" ) );
    }

    public void testGetUnformattedIMExIds( IMExRange range ) {

        Collection<Long> ids = IMExIdTransformer.getUnformattedIMExIds( new IMExRange( 3, 8 ) );

        assertEquals( 6, ids.size() );

        assertTrue( ids.contains( 3L ) ); // L is necessary, otherwise autoboxing make it an int ;)
        assertTrue( ids.contains( 4L ) );
        assertTrue( ids.contains( 5L ) );
        assertTrue( ids.contains( 6L ) );
        assertTrue( ids.contains( 7L ) );
        assertTrue( ids.contains( 8L ) );
    }

    public void testGetUnformattedIMExIds( Collection<String> ids ) {
        ids = new ArrayList<String>();

        ids.add( "IM-1" );
        ids.add( "IM-2" );
        ids.add( "IM-3" );
        ids.add( "IM-4" );

        Collection<Long> unformattedIMExIds = IMExIdTransformer.parseIMExIds( ids );

        assertEquals( 4, unformattedIMExIds.size() );

        assertTrue( unformattedIMExIds.contains( 1L ) );
        assertTrue( unformattedIMExIds.contains( 2L ) );
        assertTrue( unformattedIMExIds.contains( 3L ) );
        assertTrue( unformattedIMExIds.contains( 4L ) );
    }

    public void testBuildRanges_continuous() {

        Collection<Long> ids = new ArrayList<Long>();

        ids.add( 5L );
        ids.add( 2L );
        ids.add( 3L );
        ids.add( 1L );
        ids.add( 4L );

        List<IMExRange> imexRanges = IMExIdTransformer.buildRanges( ids );

        assertEquals( 1, imexRanges.size() );
        assertTrue( imexRanges.contains( new IMExRange( 1, 5 ) ) );
    }

    public void testBuildRanges_discontinuous() {

        Collection<Long> ids = new ArrayList<Long>();

        // 1..2 / 9..10 / 21..25 / 30..30
        ids.add( 2L );
        ids.add( 1L );

        ids.add( 10L );
        ids.add( 9L );

        ids.add( 21L );
        ids.add( 22L );
        ids.add( 25L );
        ids.add( 23L );
        ids.add( 24L );

        ids.add( 30L );

        List<IMExRange> imexRanges = IMExIdTransformer.buildRanges( ids );

        assertEquals( 4, imexRanges.size() );
        assertTrue( imexRanges.contains( new IMExRange( 1, 2 ) ) );
        assertTrue( imexRanges.contains( new IMExRange( 9, 10 ) ) );
        assertTrue( imexRanges.contains( new IMExRange( 21, 25 ) ) );
        assertTrue( imexRanges.contains( new IMExRange( 30, 30 ) ) );
    }

}
