/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.rules.experiment;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.util.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.util.sanity.rules.messages.GeneralMessage;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Annotation;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;
import uk.ac.ebi.intact.mocks.AnnotationMock;
import uk.ac.ebi.intact.mocks.cvTopics.OnHoldMock;
import uk.ac.ebi.intact.mocks.cvTopics.ToBeReviewedMock;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class ToBeReviewedExperimentTest extends TestCase {


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ToBeReviewedExperimentTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ExperimentOnHoldTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCheck() throws SanityCheckerException {
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
            assertEquals(ToBeReviewedExperiment.getDescription(), message.getDescription());
            assertEquals(ToBeReviewedExperiment.getSuggestion(), message.getProposedSolution());
        }
    }
}