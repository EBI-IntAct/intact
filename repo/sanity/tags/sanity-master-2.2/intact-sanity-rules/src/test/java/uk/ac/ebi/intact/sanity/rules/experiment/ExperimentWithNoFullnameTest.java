package uk.ac.ebi.intact.sanity.rules.experiment;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;

import java.util.Collection;

/**
 * ExperimentWithNoFullname Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
public class ExperimentWithNoFullnameTest {

    @Test
    public void execute_positive() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final Experiment experiment = mockBuilder.createExperimentEmpty();
        experiment.setFullName( "foobar" );
        Rule rule = buildRule();
        final Collection messages = rule.check( experiment );
        Assert.assertNotNull( messages );
        Assert.assertTrue( messages.isEmpty() );
    }

    @Test
    public void execute_negative_1() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final Experiment experiment = mockBuilder.createExperimentEmpty();
        experiment.setFullName( null );
        Rule rule = buildRule();
        final Collection messages = rule.check( experiment );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
    }

    @Test
    public void execute_negative_2() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final Experiment experiment = mockBuilder.createExperimentEmpty();
        experiment.setFullName( "       " );
        Rule rule = buildRule();
        final Collection messages = rule.check( experiment );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
    }

    private Rule buildRule() {
        return new ExperimentWithNoFullname();
    }
}