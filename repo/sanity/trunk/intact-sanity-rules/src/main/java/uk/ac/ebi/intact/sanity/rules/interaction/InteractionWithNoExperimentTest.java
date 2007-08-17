/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.interaction;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.sanity.rules.messages.GeneralMessage;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.mocks.interactions.Cja1Dbn1Mock;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;

import java.util.Collection;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class InteractionWithNoExperimentTest extends TestCase {


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public InteractionWithNoExperimentTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( InteractionWithNoExperimentTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCheck() throws SanityCheckerException {
        Interaction interaction = Cja1Dbn1Mock.getMock(ButkevitchMock.getMock());
        InteractionWithNoExperiment rule = new InteractionWithNoExperiment();
        Collection<GeneralMessage> messages =  rule.check(interaction);
        assertEquals(0,messages.size());

        interaction = Cja1Dbn1Mock.getMock(null);
        rule = new InteractionWithNoExperiment();
        messages =  rule.check(interaction);
        assertEquals(1,messages.size());
        for(GeneralMessage message : messages){
            assertEquals(InteractionWithNoExperiment.getDescription(), message.getDescription());
            assertEquals(InteractionWithNoExperiment.getSuggestion(), message.getProposedSolution());
        }
        
    }

}