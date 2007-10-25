package uk.ac.ebi.intact.sanity.rules.feature;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;
import uk.ac.ebi.intact.sanity.commons.rules.Rule;

import java.util.Collection;

/**
 * FeatureWithoutType Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class FeatureWithoutTypeTest {

    @Test
    public void check_feature_with_type() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final Feature feature = mockBuilder.createFeatureRandom();
        Assert.assertNotNull( feature );
        Assert.assertNotNull( feature.getCvFeatureType() );

        Rule rule = new FeatureWithoutType();
        final Collection<GeneralMessage> messages = rule.check( feature );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 0, messages.size() );
    }

    @Test
    public void check_feature_without_type() throws Exception {
        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        final Feature feature = mockBuilder.createFeatureRandom();
        Assert.assertNotNull( feature );
        feature.setCvFeatureType( null );

        Rule rule = new FeatureWithoutType();
        final Collection<GeneralMessage> messages = rule.check( feature );
        Assert.assertNotNull( messages );
        Assert.assertEquals( 1, messages.size() );
        Assert.assertEquals( MessageDefinition.FEATURE_WITHOUT_TYPE,
                             messages.iterator().next().getMessageDefinition() );

    }
}
