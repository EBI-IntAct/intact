/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.sanity.commons.SanityRuleException;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class ExperimentWithNoInteractionTest extends IntactBasicTestCase {


    @Test
    public void testCheck() throws SanityRuleException {
        ExperimentWithNoInteraction rule = new ExperimentWithNoInteraction();

        Experiment experiment = ButkevitchMock.getMock();
        beginTransaction();
        Collection<GeneralMessage> messages = rule.check(experiment);
        commitTransaction();
        Assert.assertEquals(0, messages.size());

        Collection<Interaction> interactions = new ArrayList<Interaction>();
        experiment.setInteractions(interactions);

        beginTransaction();
        messages = rule.check(experiment);
        commitTransaction();
        Assert.assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            Assert.assertEquals(ExperimentWithNoInteraction.getDescription(), message.getDescription());
            Assert.assertEquals(ExperimentWithNoInteraction.getSuggestion(), message.getProposedSolution());
        }
    }

}