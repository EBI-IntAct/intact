/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class ExperimentWithNoCvIdentificationTest  {


    @Test
    public void check() throws Exception {
        ExperimentWithNoCvIdentification rule = new ExperimentWithNoCvIdentification();

        Experiment experiment = ButkevitchMock.getMock();
        Collection<GeneralMessage> messages = rule.check(experiment);
        assertEquals(0, messages.size());

        experiment.setCvIdentification(null);
        messages = rule.check(experiment);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(MessageDefinition.EXPERIMENT_WITHOUT_PARTICIPANT_DETECT, message.getMessageDefinition());
        }
    }
}