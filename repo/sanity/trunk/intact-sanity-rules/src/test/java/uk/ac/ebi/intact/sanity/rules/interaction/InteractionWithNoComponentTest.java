/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.interaction;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;
import uk.ac.ebi.intact.mocks.interactions.Cja1Dbn1Mock;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class InteractionWithNoComponentTest {


    @Test
    public void check() throws Exception {

        Interaction interaction = Cja1Dbn1Mock.getMock(ButkevitchMock.getMock());
        InteractionWithNoComponent rule = new InteractionWithNoComponent();
        Collection<GeneralMessage> messages =  rule.check(interaction);
        assertEquals(0,messages.size());

        interaction = Cja1Dbn1Mock.getMock(ButkevitchMock.getMock());
        Collection<Component> components = new ArrayList();
        interaction.setComponents(components);
        messages =  rule.check(interaction);
        assertEquals(1,messages.size());
        for(GeneralMessage message : messages){
            assertEquals(MessageDefinition.INTERACTION_WITHOUT_COMPONENT, message.getMessageDefinition());
        }

    }
}