package uk.ac.ebi.intact.sanity.rules.interactor;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.commons.rules.InteractorMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;

import java.util.Collection;

/**
 * PolymerWithoutSequence Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class PolymerWithoutSequenceTest extends IntactBasicTestCase {

    @Test
    public void error_null_seq() throws Exception {
        final Protein protein = getMockBuilder().createProteinRandom();
        protein.setSequence( null );
        Rule rule = new PolymerWithoutSequence();
        final Collection<InteractorMessage> messages = rule.check( protein );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
        Assert.assertEquals( MessageDefinition.POLYMER_WITHOUT_SEQUENCE,
                             messages.iterator().next().getMessageDefinition() );
    }

    @Test
    public void error_blank_seq() throws Exception {
        final Protein protein = getMockBuilder().createProteinRandom();
        protein.setSequence( "   " );
        Rule rule = new PolymerWithoutSequence();
        final Collection<InteractorMessage> messages = rule.check( protein );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
        Assert.assertEquals( MessageDefinition.POLYMER_WITHOUT_SEQUENCE,
                             messages.iterator().next().getMessageDefinition() );
    }

    @Test
    public void polymer_with_sequence() throws Exception {
        final Protein protein = getMockBuilder().createProteinRandom();
        protein.setSequence( null );
        Rule rule = new PolymerWithoutSequence();
        final Collection<InteractorMessage> messages = rule.check( protein );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
        Assert.assertEquals( MessageDefinition.POLYMER_WITHOUT_SEQUENCE,
                             messages.iterator().next().getMessageDefinition() );
    }
}
