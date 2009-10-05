package uk.ac.ebi.intact.sanity.check.range;

import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.util.List;

/**
 * SequenceAlignementUtils Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
public class SequenceAlignementUtilsTest {

    // 2 matches in this one
    public static final String QUERY_1 = "SSWWAHVEMGPPDPILGVTEAYKRDTNSKKSSWWAHVEMGPPDPILGVTEAYKRDTNSKK";
    public static final String TARGET_1 = "PPDPILGVTE";

    // no matches
    public static final String QUERY_2 = "SSWWAHVEMGPPDSSWWAHVEMGPPDAYKRDTNSKK";
    public static final String TARGET_2 = "AAAAAA";

    // single matche
    public static final String QUERY_3 = "SSWWAHVEMGPPDPILGVTEAY";
    public static final String TARGET_3 = "SWWAHVE";

    // multiple overlappin matches
    public static final String QUERY_4 = "ABBBBBA";
    public static final String TARGET_4 = "BBB";

    @Test
    public void needlemanWunch() {
        try {
            String out = SequenceAlignementUtils.alignNeedlemanWunsch( QUERY_1, "query", TARGET_1, "target" );
            Assert.assertNotNull( out );
            Assert.assertTrue( out.length() > 0 );
        } catch ( AlignementException e ) {
            e.printStackTrace( );
            fail( e.getMessage() );
        }
        
        try {
            String out = SequenceAlignementUtils.alignNeedlemanWunsch( QUERY_2, "query", TARGET_2, "target" );
            Assert.assertNotNull( out );
            Assert.assertTrue( out.length() > 0 );
        } catch ( AlignementException e ) {
            fail(e.getMessage());
        }

        try {
            String out = SequenceAlignementUtils.alignNeedlemanWunsch( QUERY_3, "query", TARGET_3, "target" );
            Assert.assertNotNull( out );
            Assert.assertTrue( out.length() > 0 );
        } catch ( AlignementException e ) {
            fail(e.getMessage());
        }
    }

    @Test
    public void smithWaterman() {
        try {
            String out = SequenceAlignementUtils.alignSmithWaterman( QUERY_1, "query", TARGET_1, "target" );
            Assert.assertNotNull( out );
            Assert.assertTrue( out.length() > 0 );
        } catch ( AlignementException e ) {
            fail(e.getMessage());
        }

        try {
            String out = SequenceAlignementUtils.alignSmithWaterman( QUERY_2, "query", TARGET_2, "target" );
            Assert.assertNotNull( out );
            Assert.assertTrue( out.length() > 0 );
        } catch ( AlignementException e ) {
            fail(e.getMessage());
        }

        try {
            String out = SequenceAlignementUtils.alignSmithWaterman( QUERY_3, "query", TARGET_3, "target" );
            Assert.assertNotNull( out );
            Assert.assertTrue( out.length() > 0 );
        } catch ( AlignementException e ) {
            fail(e.getMessage());
        }
    }

    @Test
    public void exactMatching() {
        List<Integer> matches;

        matches = SequenceAlignementUtils.findExactMatches( QUERY_1, TARGET_1 );
        Assert.assertNotNull( matches );
        Assert.assertEquals( 2, matches.size() );
        Assert.assertTrue( matches.toString(), matches.contains( 11 ) );
        Assert.assertTrue( matches.toString(), matches.contains( 41 ) );

        matches = SequenceAlignementUtils.findExactMatches( QUERY_2, TARGET_2 );
        Assert.assertNotNull( matches );
        Assert.assertEquals( 0, matches.size() );

        matches = SequenceAlignementUtils.findExactMatches( QUERY_3, TARGET_3 );
        Assert.assertNotNull( matches );
        Assert.assertEquals( 1, matches.size() );
        Assert.assertTrue( matches.toString(), matches.contains( 2 ) );

        matches = SequenceAlignementUtils.findExactMatches( QUERY_4, TARGET_4 );
        Assert.assertNotNull( matches );
        Assert.assertEquals( 3, matches.size() );
        Assert.assertTrue( matches.toString(), matches.contains( 2 ) );
        Assert.assertTrue( matches.toString(), matches.contains( 3 ) );
        Assert.assertTrue( matches.toString(), matches.contains( 4 ) );
    }
}