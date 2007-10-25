/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.ExperimentXref;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ExperimentWithNoPubmedXref Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk), Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class ExperimentWithNoPubmedXrefTest {

    @Test
    public void check() throws Exception {
        ExperimentWithNoPubmedXref rule = new ExperimentWithNoPubmedXref();

        Experiment experiment = ButkevitchMock.getMock();
        Collection<GeneralMessage> messages = rule.check( experiment );
        assertEquals( 0, messages.size() );

        Collection<ExperimentXref> xrefs = new ArrayList<ExperimentXref>();
        experiment.setXrefs( xrefs );
        messages = rule.check( experiment );
        assertEquals( 1, messages.size() );
        assertEquals( MessageDefinition.EXPERIMENT_WITHOUT_PRIMARY_REF,
                      messages.iterator().next().getMessageDefinition() );
    }

    @Test
    public void check_multiple_primary_ref() throws Exception {

        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        ExperimentWithNoPubmedXref rule = new ExperimentWithNoPubmedXref();

        Experiment experiment = mockBuilder.createExperimentRandom( 1 );
        experiment.addXref( mockBuilder.createPrimaryReferenceXref( experiment, "1234566789" ) );
        Assert.assertEquals( 2, experiment.getXrefs().size() );

        Collection<GeneralMessage> messages = rule.check( experiment );
        assertEquals( 1, messages.size() );
        assertEquals( MessageDefinition.EXPERIMENT_WITH_MULTIPLE_PRIMARY_REF,
                      messages.iterator().next().getMessageDefinition() );
    }

    @Test
    public void check_to_be_assigned() throws Exception {

        IntactMockBuilder mockBuilder = new IntactMockBuilder();
        ExperimentWithNoPubmedXref rule = new ExperimentWithNoPubmedXref();

        Experiment experiment = mockBuilder.createExperimentRandom( 1 );
        experiment.getXrefs().clear();
        experiment.addXref( mockBuilder.createPrimaryReferenceXref( experiment, "to_be_assigned_1" ) );
        Assert.assertEquals( 1, experiment.getXrefs().size() );

        Collection<GeneralMessage> messages = rule.check( experiment );
        assertEquals( 1, messages.size() );
        assertEquals( MessageDefinition.EXPERIMENT_WITH_PUBMED_TO_BE_ASSIGNED,
                      messages.iterator().next().getMessageDefinition() );
    }
}