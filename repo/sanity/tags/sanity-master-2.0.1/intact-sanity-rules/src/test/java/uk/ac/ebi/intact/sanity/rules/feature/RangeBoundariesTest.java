package uk.ac.ebi.intact.sanity.rules.feature;

import org.junit.Test;
import org.junit.Assert;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.RangeMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;

import java.util.Collection;

/**
 * RangeBoundaries Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class RangeBoundariesTest {

    @Test
    public void undetermined_with_range() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder( );

        final Protein protein = mockBuilder.createProteinRandom();
        final Component componentBait = mockBuilder.createComponentBait( protein );

        // add a undetermined range on a small molecule
        final Feature feature = mockBuilder.createFeatureRandom();
        final Range range = mockBuilder.createRange( 2, 2, 4, 4 );
        range.setUndetermined( true );
        feature.addRange( range );

        componentBait.addBindingDomain( feature );

        final Collection<GeneralMessage> messages = buildRule().check( feature );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );

        final GeneralMessage message = messages.iterator().next();
        Assert.assertEquals( RangeMessage.class, message.getClass() );
        RangeMessage rangeMessage = (RangeMessage) message;
        Assert.assertEquals( MessageDefinition.UNDETERMINED_RANGE_WITH_BOUNDARIES, message.getMessageDefinition() );
        Assert.assertEquals( range, rangeMessage.getRange() );
        Assert.assertNull( rangeMessage.getInteractor() );
    }

    @Test
    public void range_not_undetermined_but_fuzzytypes_are() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder( );

        final Protein protein = mockBuilder.createProteinRandom();
        final Component componentBait = mockBuilder.createComponentBait( protein );

        final Feature feature = mockBuilder.createFeatureRandom();
        final Range range = mockBuilder.createRangeUndetermined();
        range.setUndetermined( false );
        feature.addRange( range );

        componentBait.addBindingDomain( feature );

        final Collection<GeneralMessage> messages = buildRule().check( feature );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );

        final GeneralMessage message = messages.iterator().next();
        Assert.assertEquals( RangeMessage.class, message.getClass() );
        RangeMessage rangeMessage = (RangeMessage) message;
        Assert.assertEquals( MessageDefinition.DETERMINED_RANGE_WITHOUT_BOUNDARIES, message.getMessageDefinition() );
        Assert.assertEquals( range, rangeMessage.getRange() );
        Assert.assertNull( rangeMessage.getInteractor() );
    }

    @Test
    public void hasFromBoudaries_and_cterminal() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder( );

        final Protein protein = mockBuilder.createProteinRandom();
        final Component componentBait = mockBuilder.createComponentBait( protein );

        final Feature feature = mockBuilder.createFeatureRandom();
        final Range range = mockBuilder.createRangeCTerminal();
        range.setUndetermined( false );
        range.setFromIntervalStart( 2 );
        range.setFromIntervalEnd( 2 );
        feature.addRange( range );

        componentBait.addBindingDomain( feature );

        final Collection<GeneralMessage> messages = buildRule().check( feature );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );

        final GeneralMessage message = messages.iterator().next();
        Assert.assertEquals( RangeMessage.class, message.getClass() );
        RangeMessage rangeMessage = (RangeMessage) message;
        Assert.assertEquals( MessageDefinition.DETERMINED_RANGE_WITHOUT_BOUNDARIES, message.getMessageDefinition() );
        Assert.assertEquals( range, rangeMessage.getRange() );
        Assert.assertNull( rangeMessage.getInteractor() );
    }

    @Test
    public void hasToBoudaries_and_nterminal() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder( );

        final Protein protein = mockBuilder.createProteinRandom();
        final Component componentBait = mockBuilder.createComponentBait( protein );

        final Feature feature = mockBuilder.createFeatureRandom();
        final Range range = mockBuilder.createRange( 2, 2, 6, 6 );
        range.setUndetermined( false );
        range.setToCvFuzzyType( mockBuilder.createCvObject( CvFuzzyType.class,
                                                            CvFuzzyType.N_TERMINAL_MI_REF,
                                                            CvFuzzyType.N_TERMINAL ) );
        range.setFromIntervalEnd( 2 );
        feature.addRange( range );

        componentBait.addBindingDomain( feature );

        final Collection<GeneralMessage> messages = buildRule().check( feature );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );

        final GeneralMessage message = messages.iterator().next();
        Assert.assertEquals( RangeMessage.class, message.getClass() );
        RangeMessage rangeMessage = (RangeMessage) message;
        Assert.assertEquals( MessageDefinition.DETERMINED_RANGE_WITHOUT_BOUNDARIES, message.getMessageDefinition() );
        Assert.assertEquals( range, rangeMessage.getRange() );
        Assert.assertNull( rangeMessage.getInteractor() );
    }

    @Test
    public void hasNoBoudaries_and_certain() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder( );

        final Protein protein = mockBuilder.createProteinRandom();
        final Component componentBait = mockBuilder.createComponentBait( protein );

        final Feature feature = mockBuilder.createFeatureRandom();
        final Range range = mockBuilder.createRangeUndetermined();
        CvFuzzyType fuzzyRange = mockBuilder.createCvObject(CvFuzzyType.class, CvFuzzyType.RANGE_MI_REF, CvFuzzyType.RANGE);
        range.setFromCvFuzzyType( fuzzyRange );
        range.setToCvFuzzyType( fuzzyRange );
        range.setUndetermined( true );
        feature.addRange( range );

        componentBait.addBindingDomain( feature );

        final Collection<GeneralMessage> messages = buildRule().check( feature );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );

        final GeneralMessage message = messages.iterator().next();
        Assert.assertEquals( RangeMessage.class, message.getClass() );
        RangeMessage rangeMessage = (RangeMessage) message;
        Assert.assertEquals( MessageDefinition.UNDETERMINED_RANGE_WITH_BOUNDARIES, message.getMessageDefinition() );
        Assert.assertEquals( range, rangeMessage.getRange() );
        Assert.assertNull( rangeMessage.getInteractor() );
    }

    private RangeBoundaries buildRule() {
        RangeBoundaries rule = new RangeBoundaries();
        return rule;
    }
}
