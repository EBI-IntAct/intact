package uk.ac.ebi.intact.sanity.rules.feature;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.RangeMessage;

import java.util.Collection;

/**
 * RangeAndInteractor Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since 2.0.0
 * @version $Id$
 */
public class RangeAndInteractorTest {

    @Test
    public void check_not_a_polymer() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder( );

        final SmallMolecule sm = mockBuilder.createSmallMoleculeRandom();
        final Component componentBait = mockBuilder.createComponentBait( sm );

        // add a c-terminal range on a small molecule
        final Feature feature = mockBuilder.createFeatureRandom();
        final Range range = mockBuilder.createRangeCTerminal();
        feature.addRange( range );

        componentBait.addBindingDomain( feature );

        RangeAndInteractor rule = new RangeAndInteractor();
        final Collection<GeneralMessage> messages = rule.check( feature );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );

        final GeneralMessage message = messages.iterator().next();
        Assert.assertEquals( RangeMessage.class, message.getClass() );
        RangeMessage rangeMessage = (RangeMessage) message;
        Assert.assertEquals( MessageDefinition.FEATURE_WITH_INCOMPATIBLE_INTERACTOR, message.getMessageDefinition() );
        Assert.assertEquals( range, rangeMessage.getRange() );
        Assert.assertEquals( sm, rangeMessage.getInteractor() );
    }

    @Test
    public void check_not_a_polymer2() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder( );

        final SmallMolecule sm = mockBuilder.createSmallMoleculeRandom();
        final Component componentBait = mockBuilder.createComponentBait( sm );

        // add a range on a small molecule
        final Feature feature = mockBuilder.createFeatureRandom();
        final Range range = mockBuilder.createRange( 1, 1, 5, 5);
        feature.addRange( range );

        componentBait.addBindingDomain( feature );

        RangeAndInteractor rule = new RangeAndInteractor();
        final Collection<GeneralMessage> messages = rule.check( feature );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );

        final GeneralMessage message = messages.iterator().next();
        Assert.assertEquals( RangeMessage.class, message.getClass() );
        RangeMessage rangeMessage = (RangeMessage) message;
        Assert.assertEquals( MessageDefinition.FEATURE_WITH_INCOMPATIBLE_INTERACTOR, message.getMessageDefinition() );
        Assert.assertEquals( range, rangeMessage.getRange() );
        Assert.assertEquals( sm, rangeMessage.getInteractor() );
    }

    @Test
    public void check_not_a_polymer3() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder( );

        final SmallMolecule sm = mockBuilder.createSmallMoleculeRandom();
        final Component componentBait = mockBuilder.createComponentBait( sm );

        // add a c-terminal range on a small molecule
        final Feature feature = mockBuilder.createFeatureRandom();
        final Range range = mockBuilder.createRangeUndetermined();
        feature.addRange( range );

        componentBait.addBindingDomain( feature );

        RangeAndInteractor rule = new RangeAndInteractor();
        final Collection<GeneralMessage> messages = rule.check( feature );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 0, messages.size() );
    }

    @Test
    public void check_polymer() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder( );

        final Protein protein = mockBuilder.createProteinRandom();
        protein.setSequence( "ABCABCAABCABCAABCABCAABCABCAABCABCA" ); // length = 35
        final Component componentBait = mockBuilder.createComponentBait( protein );

        // add a range of type 'range' on a small molecule
        final Feature feature = mockBuilder.createFeatureRandom();
        final Range range = mockBuilder.createRange(1, 1, 36, 36);
        feature.addRange( range );

        componentBait.addBindingDomain( feature );

        RangeAndInteractor rule = new RangeAndInteractor();
        final Collection<GeneralMessage> messages = rule.check( feature );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );

        final GeneralMessage message = messages.iterator().next();
        Assert.assertEquals( RangeMessage.class, message.getClass() );
        RangeMessage rangeMessage = (RangeMessage) message;
        Assert.assertEquals( MessageDefinition.RANGE_AND_INTERACTOR_BOUNDARIES_MISMATCH,
                             message.getMessageDefinition() );
        Assert.assertEquals( range, rangeMessage.getRange() );
        Assert.assertEquals( protein, rangeMessage.getInteractor() );
    }

    @Test
    public void check_polymer2() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder( );

        final Protein protein = mockBuilder.createProteinRandom();
        protein.setSequence( "ABCABCAABC" ); // length = 10
        final Component componentBait = mockBuilder.createComponentBait( protein );

        // add a range of type 'range' on a small molecule
        final Feature feature = mockBuilder.createFeatureRandom();
        final Range range = mockBuilder.createRange(1, 1, 10, 10);
        feature.addRange( range );

        componentBait.addBindingDomain( feature );

        RangeAndInteractor rule = new RangeAndInteractor();
        final Collection<GeneralMessage> messages = rule.check( feature );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 0, messages.size() );
    }
}
