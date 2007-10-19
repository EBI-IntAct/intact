/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.experiment;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import uk.ac.ebi.intact.mocks.AnnotationMock;
import uk.ac.ebi.intact.mocks.cvTopics.ToBeReviewedMock;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;
import uk.ac.ebi.intact.model.Annotation;
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
public class ToBeReviewedExperimentTest {


    @Test
    public void check() throws Exception {
        ToBeReviewedExperiment rule = new ToBeReviewedExperiment();

        // Give the check method an experiment without on-hold annotation and make sure that it returns no message.
        Experiment experiment = ButkevitchMock.getMock();
        Collection<GeneralMessage> messages =  rule.check(experiment);
        assertEquals(0,messages.size());

        // Give the check method an experiment with 1 on-hold annotation and make sure that it returns 1 message
        Annotation annotation = AnnotationMock.getMock(ToBeReviewedMock.getMock(),"waiting for data" );
        experiment.addAnnotation(annotation);
        messages =  rule.check(experiment);
        assertEquals(1,messages.size());
        for(GeneralMessage message : messages){
            assertEquals(MessageDefinition.EXPERIMENT_TO_BE_REVIEWED, message.getMessageDefinition());
        }
    }
    
}