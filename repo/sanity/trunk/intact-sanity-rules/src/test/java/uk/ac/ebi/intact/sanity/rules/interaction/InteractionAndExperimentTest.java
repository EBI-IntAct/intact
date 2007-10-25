package uk.ac.ebi.intact.sanity.rules.interaction;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;

import java.util.Collection;

/**
 * InteractionAndExperiment Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @since 2.0.0
 * @version $Id$
 */
public class InteractionAndExperimentTest {

    @Test
    public void check_experiment_empty() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder( );
        final Interaction interation = mockBuilder.createInteractionRandomBinary();
        interation.getExperiments().clear();

        Rule rule = new InteractionAndExperiment();
        final Collection<GeneralMessage> messages = rule.check( interation );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
        Assert.assertEquals( MessageDefinition.INTERACTION_EXPERIMENT_COUNT,
                             messages.iterator().next().getMessageDefinition() );
    }

    @Test
    public void check_experiment_count_is_1() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder( );
        final Interaction interation = mockBuilder.createInteractionRandomBinary();

        Rule rule = new InteractionAndExperiment();
        final Collection<GeneralMessage> messages = rule.check( interation );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 0, messages.size() );
    }

    @Test
    public void check_experiment_count_is_2() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder( );
        final Interaction interaction = mockBuilder.createInteractionRandomBinary();
        interaction.getExperiments().add( mockBuilder.createExperimentEmpty( ) );

        Rule rule = new InteractionAndExperiment();
        final Collection<GeneralMessage> messages = rule.check( interaction );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
        Assert.assertEquals( MessageDefinition.INTERACTION_EXPERIMENT_COUNT,
                             messages.iterator().next().getMessageDefinition() );
    }
}