package uk.ac.ebi.intact.model;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;

/**
 * Range Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since 1.7.1
 */

public class RangeTest {

    @Test
    @Ignore
    // TODO fix the ranges in intact that do have their first AA missing before fixing intact-core and enabling this test  
    public void prepareSequence() throws Exception {
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String seq = "MQTIKCVVVGDGAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG";
        Range range = new Range( owner, 2, 5, seq);
        Assert.assertTrue( range.getFullSequence().startsWith( "QTIK" ));

        range.prepareSequence( "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAG" );
        Assert.assertTrue( range.getFullSequence().startsWith( "AVGK" ));

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.RANGE_MI_REF,
                CvFuzzyType.RANGE );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";

        range.prepareSequence( s );
        Assert.assertEquals( 110, s.length() );
        Assert.assertTrue( "Range's internal sequence should start with: 'AVGK' and not: " + range.getFullSequence(),
                range.getFullSequence().startsWith( "AVGK" ));
    }

    @Test
    public void prepareSequenceWithMoreThan100AA() throws Exception {
        final Institution owner = new Institution( "ebi" );
        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 1, 109, s);
        Assert.assertEquals( 110, s.length() );
        Assert.assertTrue( range.getFullSequence().startsWith( "GAV" ));
        Assert.assertEquals( s.substring(0, 109), range.getFullSequence());
    }

    @Test
    public void prepareSequenceWithCTerminal() throws Exception {
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 1, 109, s);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.C_TERMINAL_MI_REF,
                CvFuzzyType.C_TERMINAL );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType );
        range.prepareSequence(s);
        
        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(10, 110), range.getFullSequence());
    }

    @Test
    public void prepareSequenceWithNTerminal() throws Exception {
        final Institution owner = new Institution( "ebi" );
        IntactMockBuilder mockBuilder = new IntactMockBuilder( owner );

        String s = "GAVGKTCLLISYTTNKFPSEYVPTVFDNYAVTVMIGGEPYTLGLFDTAGGAVGKTCLLISYTTNKFPSEYVPTVF" +
                "DNYAVTVMIGGEPYTLGLFDTAGGALGLFDTAGGA";
        Range range = new Range( owner, 1, 109, s);

        CvFuzzyType rangeFuzzyType = mockBuilder.createCvObject( CvFuzzyType.class,
                CvFuzzyType.N_TERMINAL_MI_REF,
                CvFuzzyType.N_TERMINAL );
        range.setFromCvFuzzyType( rangeFuzzyType );
        range.setToCvFuzzyType( rangeFuzzyType );
        range.prepareSequence(s);

        Assert.assertEquals( 110, s.length() );
        Assert.assertEquals( s.substring(0, 100), range.getFullSequence());
    }
}
