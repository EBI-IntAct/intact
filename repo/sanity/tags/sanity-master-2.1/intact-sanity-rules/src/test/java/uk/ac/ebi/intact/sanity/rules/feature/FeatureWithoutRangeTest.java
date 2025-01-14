/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.feature;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.ac.ebi.intact.mocks.InstitutionMock;
import uk.ac.ebi.intact.mocks.components.Q9QXS6ComponentMock;
import uk.ac.ebi.intact.mocks.cvFeatureType.MutationDecreasingMock;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;
import uk.ac.ebi.intact.mocks.interactions.Cja1Dbn1Mock;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Range;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;

import java.util.Collection;

/**
 * FeatureWithoutRange Tester.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
public class FeatureWithoutRangeTest {

    @Test
    public void check() throws Exception {
        Interaction interaction = Cja1Dbn1Mock.getMock( ButkevitchMock.getMock() );
        FeatureWithoutRange rule = new FeatureWithoutRange();

        Component component = Q9QXS6ComponentMock.getMock( interaction );
        Feature feature = new Feature( InstitutionMock.getMock(), "feature", component, MutationDecreasingMock.getMock() );
        Range range = new Range( InstitutionMock.getMock(), 1, 1, 1, 1, "" );
        feature.addRange( range );
        Collection<GeneralMessage> messages = rule.check( feature );
        assertEquals( 0, messages.size() );

        component = Q9QXS6ComponentMock.getMock( interaction );
        feature = new Feature( InstitutionMock.getMock(), "feature", component, MutationDecreasingMock.getMock() );
        messages = rule.check( feature );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            assertEquals( MessageDefinition.FEATURE_WITHOUT_RANGE, message.getMessageDefinition() );
        }
    }
}