/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.util.SchemaUtils;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ExperimentWithNoInteraction Tester.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
public class ExperimentWithNoInteractionTest extends IntactBasicTestCase {

    @Before
    public void before() throws Exception {
        SchemaUtils.createSchema( true );
    }

    @After
    public void after() throws Exception {
        commitTransaction();
    }

    @Test
    public void check() throws Exception {
        ExperimentWithNoInteraction rule = new ExperimentWithNoInteraction();

        Experiment experiment = ButkevitchMock.getMock();
        Collection<GeneralMessage> messages = rule.check( experiment );
        Assert.assertEquals( 0, messages.size() );

        Collection<Interaction> interactions = new ArrayList<Interaction>();
        experiment.setInteractions( interactions );

        messages = rule.check( experiment );
        Assert.assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            Assert.assertEquals( MessageDefinition.EXPERIMENT_WITHOUT_INTERACTION, message.getMessageDefinition() );
        }
    }
}