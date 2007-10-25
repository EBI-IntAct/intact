package uk.ac.ebi.intact.sanity.rules.interaction;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;

import java.util.Collection;

/**
 * InteractionWithNoType Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class InteractionWithNoTypeTest {

    @Test
    public void check_positive() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final Interaction interaction = mockBuilder.createInteractionRandomBinary();
        Assert.assertNotNull( interaction.getCvInteractionType() );
        Rule rule = buildRule();
        final Collection<GeneralMessage> messages = rule.check( interaction );
        Assert.assertNotNull( messages );
        Assert.assertTrue( messages.isEmpty() );
    }

    @Test
    public void check_negative() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final Interaction interaction = mockBuilder.createInteractionRandomBinary();
        interaction.setCvInteractionType( null );
        Rule rule = buildRule();
        final Collection<GeneralMessage> messages = rule.check( interaction );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
    }

    private Rule buildRule() {
        return new InteractionWithNoType();
    }
}
