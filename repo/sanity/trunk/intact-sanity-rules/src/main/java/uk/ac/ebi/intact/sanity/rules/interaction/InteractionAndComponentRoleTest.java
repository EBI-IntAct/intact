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
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.mocks.interactions.Cja1Dbn1Mock;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;
import uk.ac.ebi.intact.mocks.proteins.P08050Mock;
import uk.ac.ebi.intact.mocks.components.P08050ComponentMock;
import uk.ac.ebi.intact.mocks.components.Q9QXS6ComponentMock;

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
        /***********************************************
        Give a right interaction with 1 bait, 1 prey
        ************************************************/
        Interaction interaction = Cja1Dbn1Mock.getMock(ButkevitchMock.getMock());
        InteractionAndComponentRole rule = new InteractionAndComponentRole();
        Collection<GeneralMessage> messages =  rule.check(interaction);
        assertEquals(0,messages.size());

        /***********************************************
        Give a wrong interaction with 2 baits
        ************************************************/
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

        /***********************************************
        Give a wrong interaction with 2 preys
        ************************************************/
        componentA.setCvComponentRole(PreyMock.getMock());
        componentB.setCvComponentRole(PreyMock.getMock());
        messages =  rule.check(interaction);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(InteractionAndComponentRole.getNoBaitDescription(),message.getDescription());
            assertEquals(InteractionAndComponentRole.getSuggestion(),message.getProposedSolution());
        }

        /*****************************************************************************
        Give a right interaction with 1 fluorophore donor and one fluorophore acceptor
        ******************************************************************************/
        componentA.setCvComponentRole(FluorophoreDonorMock.getMock());
        componentB.setCvComponentRole(FluorophoreAcceptorMock.getMock());
        messages =  rule.check(interaction);
        assertEquals(0, messages.size());

        /*****************************************************************************
        Give a right interaction with 2 fluorophore donors
        ******************************************************************************/
        componentA.setCvComponentRole(FluorophoreDonorMock.getMock());
        componentB.setCvComponentRole(FluorophoreDonorMock.getMock());
        messages =  rule.check(interaction);
        assertEquals(0, messages.size());

        /******************************************************************
        Give a wrong interaction with 2 fluorophore accepetors and no donor
        *******************************************************************/
        componentA.setCvComponentRole(FluorophoreAcceptorMock.getMock());
        componentB.setCvComponentRole(FluorophoreAcceptorMock.getMock());
        messages =  rule.check(interaction);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(InteractionAndComponentRole.getNoFluorophoreDonorDescription(),message.getDescription());
            assertEquals(InteractionAndComponentRole.getSuggestion(),message.getProposedSolution());
        }

        /******************************************************************
        Give a wrong interaction with 2 electron accepetors and no donor
        *******************************************************************/
        componentA.setCvComponentRole(ElectronAcceptorMock.getMock());
        componentB.setCvComponentRole(ElectronAcceptorMock.getMock());
        messages =  rule.check(interaction);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(InteractionAndComponentRole.getNoElectronDonorDescription(),message.getDescription());
            assertEquals(InteractionAndComponentRole.getSuggestion(),message.getProposedSolution());
        }

        /******************************************************************
        Give a wrong interaction with 2 electron donors and no acceptor
        *******************************************************************/
        componentA.setCvComponentRole(ElectronDonorMock.getMock());
        componentB.setCvComponentRole(ElectronDonorMock.getMock());
        messages =  rule.check(interaction);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(InteractionAndComponentRole.getNoElectronAcceptorDescription(),message.getDescription());
            assertEquals(InteractionAndComponentRole.getSuggestion(),message.getProposedSolution());
        }

        /******************************************************************
        Give a wrong interaction with 2 enzymes and no enzyme target
        *******************************************************************/
        componentA.setCvComponentRole(EnzymeMock.getMock());
        componentB.setCvComponentRole(EnzymeMock.getMock());
        messages =  rule.check(interaction);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(InteractionAndComponentRole.getNoEnzymeTargetDescription(),message.getDescription());
            assertEquals(InteractionAndComponentRole.getSuggestion(),message.getProposedSolution());
        }

        /******************************************************************
         Give a wrong interaction with 2 enzyme targets and no enzyme
        *******************************************************************/
        componentA.setCvComponentRole(EnzymeTargetMock.getMock());
        componentB.setCvComponentRole(EnzymeTargetMock.getMock());
        messages =  rule.check(interaction);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(InteractionAndComponentRole.getNoEnzymeDescription(),message.getDescription());
            assertEquals(InteractionAndComponentRole.getSuggestion(),message.getProposedSolution());
        }

        /******************************************************************
         Give a wrong interaction with 2 self components and no enzyme
        *******************************************************************/
        componentA.setCvComponentRole(SelfMock.getMock());
        componentB.setCvComponentRole(SelfMock.getMock());
        messages =  rule.check(interaction);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(InteractionAndComponentRole.getMoreThan2SelfProteinDescription(),message.getDescription());
            assertEquals(InteractionAndComponentRole.getSuggestion(),message.getProposedSolution());
        }

        /******************************************************************
         Give a wrong interaction with only 1 component and stoechiometry 1
        *******************************************************************/
        componentA.setCvComponentRole(NeutralMock.getMock());
        componentA.setStoichiometry(Float.parseFloat("1"));
        components = new ArrayList<Component>();
        components.add(componentA);
        interaction.setComponents(components);
        messages =  rule.check(interaction);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(InteractionAndComponentRole.getOnly1NeutralDescription(),message.getDescription());
            assertEquals(InteractionAndComponentRole.getSuggestion(),message.getProposedSolution());
        }

        /**************************************************************************
         Give a right interaction with only 1 neutral component but stoechiometry 2
        ***************************************************************************/
        componentA.setStoichiometry(Float.parseFloat("2"));
        messages =  rule.check(interaction);
        assertEquals(0, messages.size());

        /******************************************************************************************************
         Give a right interaction with 1 neutral component stoechiometry 1 but as well one inhibited component
        *******************************************************************************************************/
        componentA.setStoichiometry(Float.parseFloat("1"));
        componentB.setCvComponentRole(InhibitedMock.getMock());
        interaction.addComponent(componentB);
        messages =  rule.check(interaction);
        assertEquals(0, messages.size());

        /******************************************************************************************************
         Give a wrong interaction with 1 fluorophore, 1 bait donor
        *******************************************************************************************************/
        componentA.setCvComponentRole(FluorophoreDonorMock.getMock());
        componentB.setCvComponentRole(BaitMock.getMock());
        messages =  rule.check(interaction);
        assertEquals(1, messages.size());
        for(GeneralMessage message : messages){
            assertEquals(InteractionAndComponentRole.getMixedCategoriesDescription(),message.getDescription());
            assertEquals(InteractionAndComponentRole.getSuggestion(),message.getProposedSolution());
        }


    }
}