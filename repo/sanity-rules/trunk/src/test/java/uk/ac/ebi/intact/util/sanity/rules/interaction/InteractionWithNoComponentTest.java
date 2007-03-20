/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.sanity.rules.interaction;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.util.sanity.exception.SanityCheckerException;
import uk.ac.ebi.intact.util.sanity.rules.messages.GeneralMessage;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.mocks.interactions.Cja1Dbn1Mock;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class InteractionWithNoComponentTest extends TestCase {


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public InteractionWithNoComponentTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( InteractionWithNoComponentTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCheck() throws SanityCheckerException {

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
            assertEquals(InteractionWithNoComponent.getDescription(), message.getDescription());
            assertEquals(InteractionWithNoComponent.getSuggestion(), message.getProposedSolution());
        }

    }
}