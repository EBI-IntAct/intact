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
import uk.ac.ebi.intact.mocks.proteins.P08050Mock;
import uk.ac.ebi.intact.mocks.components.P08050ComponentMock;
import uk.ac.ebi.intact.mocks.components.Q9QXS6ComponentMock;
import uk.ac.ebi.intact.mocks.cvComponentRoles.BaitMock;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class InteractionAndComponentRoleTest extends TestCase {


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public InteractionAndComponentRoleTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( InteractionAndComponentRoleTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCheck() throws SanityCheckerException {

        Interaction interaction = Cja1Dbn1Mock.getMock(ButkevitchMock.getMock());

        InteractionAndComponentRole rule = new InteractionAndComponentRole();
        Collection<GeneralMessage> messages =  rule.check(interaction);
        assertEquals(0,messages.size());

        interaction.setComponents(new ArrayList<Component>());
        Collection<Component> components = new ArrayList<Component>();
        Component componentA = P08050ComponentMock.getMock(interaction);
        componentA.setCvComponentRole(BaitMock.getMock());
        components.add(componentA);
        Component componentB = Q9QXS6ComponentMock.getMock(interaction);
        componentB.setCvComponentRole(BaitMock.getMock());
        components.add(componentB);
        interaction.setComponents(components);
        messages =  rule.check(interaction);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(InteractionAndComponentRole.getNoPreyDescription(),message.getDescription());
            assertEquals(InteractionAndComponentRole.getSuggestion(),message.getProposedSolution());
        }




    }
}