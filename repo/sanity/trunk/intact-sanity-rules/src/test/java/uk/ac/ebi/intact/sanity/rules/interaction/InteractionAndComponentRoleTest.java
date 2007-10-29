/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.sanity.rules.interaction;

import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.mocks.components.P08050ComponentMock;
import uk.ac.ebi.intact.mocks.components.Q9QXS6ComponentMock;
import uk.ac.ebi.intact.mocks.cvexperimentalroles.*;
import uk.ac.ebi.intact.mocks.experiments.ButkevitchMock;
import uk.ac.ebi.intact.mocks.interactions.Cja1Dbn1Mock;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.sanity.commons.rules.GeneralMessage;
import uk.ac.ebi.intact.sanity.commons.rules.MessageDefinition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * InteractionAndComponentRole Tester.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since 2.0
 */
@Ignore
public class InteractionAndComponentRoleTest {

    @Test
    public void check() throws Exception {
        /***********************************************
         Give a right interaction with 1 bait, 1 prey
         ************************************************/
        Interaction interaction = Cja1Dbn1Mock.getMock( ButkevitchMock.getMock() );
        InteractionAndComponentRole rule = new InteractionAndComponentRole();
        Collection<GeneralMessage> messages = rule.check( interaction );
        assertEquals( 0, messages.size() );

        /***********************************************
         Give a wrong interaction with 2 baits
         ************************************************/
        interaction.setComponents( new ArrayList<Component>() );
        Collection<Component> components = new ArrayList<Component>();
        Component componentA = P08050ComponentMock.getMock( interaction );
        componentA.setCvExperimentalRole( BaitMock.getMock() );
        components.add( componentA );
        Component componentB = Q9QXS6ComponentMock.getMock( interaction );
        componentB.setCvExperimentalRole( BaitMock.getMock() );
        components.add( componentB );
        interaction.setComponents( components );
        messages = rule.check( interaction );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            //assertEquals(InteractionAndComponentRole.getNoPreyDescription(),message.getDescription());
            //TODO: wrong assertion due to missing MessageDefinition
            assertEquals( MessageDefinition.INTERACTION_ROLES_MIXED_CATEGORIES, message.getMessageDefinition() );
        }

        /***********************************************
         Give a wrong interaction with 2 preys
         ************************************************/
        componentA.setCvExperimentalRole( PreyMock.getMock() );
        componentB.setCvExperimentalRole( PreyMock.getMock() );
        messages = rule.check( interaction );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            //assertEquals(InteractionAndComponentRole.getNoBaitDescription(),message.getDescription());
            //TODO: wrong assertion due to missing MessageDefinition
            assertEquals( MessageDefinition.INTERACTION_ROLES_MIXED_CATEGORIES, message.getMessageDefinition() );
        }

        /*****************************************************************************
         Give a right interaction with 1 fluorophore donor and one fluorophore acceptor
         ******************************************************************************/
        componentA.setCvExperimentalRole( FluorophoreDonorMock.getMock() );
        componentB.setCvExperimentalRole( FluorophoreAcceptorMock.getMock() );
        messages = rule.check( interaction );
        assertEquals( 0, messages.size() );

        /*****************************************************************************
         Give a right interaction with 2 fluorophore donors
         ******************************************************************************/
        componentA.setCvExperimentalRole( FluorophoreDonorMock.getMock() );
        componentB.setCvExperimentalRole( FluorophoreDonorMock.getMock() );
        messages = rule.check( interaction );
        assertEquals( 0, messages.size() );

        /******************************************************************
         Give a wrong interaction with 2 fluorophore accepetors and no donor
         *******************************************************************/
        componentA.setCvExperimentalRole( FluorophoreAcceptorMock.getMock() );
        componentB.setCvExperimentalRole( FluorophoreAcceptorMock.getMock() );
        messages = rule.check( interaction );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            //assertEquals(InteractionAndComponentRole.getNoFluorophoreDonorDescription(),message.getDescription());
            //TODO: wrong assertion due to missing MessageDefinition
            assertEquals( MessageDefinition.INTERACTION_ROLES_MIXED_CATEGORIES, message.getMessageDefinition() );
        }

        /******************************************************************
         Give a wrong interaction with 2 electron accepetors and no donor
         *******************************************************************/
        componentA.setCvExperimentalRole( ElectronAcceptorMock.getMock() );
        componentB.setCvExperimentalRole( ElectronAcceptorMock.getMock() );
        messages = rule.check( interaction );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            //assertEquals(InteractionAndComponentRole.getNoElectronDonorDescription(),message.getDescription());
            //TODO: wrong assertion due to missing MessageDefinition
            assertEquals( MessageDefinition.INTERACTION_ROLES_MIXED_CATEGORIES, message.getMessageDefinition() );
        }

        /******************************************************************
         Give a wrong interaction with 2 electron donors and no acceptor
         *******************************************************************/
        componentA.setCvExperimentalRole( ElectronDonorMock.getMock() );
        componentB.setCvExperimentalRole( ElectronDonorMock.getMock() );
        messages = rule.check( interaction );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            //assertEquals(InteractionAndComponentRole.getNoElectronAcceptorDescription(),message.getDescription());
            //TODO: wrong assertion due to missing MessageDefinition
            assertEquals( MessageDefinition.INTERACTION_ROLES_MIXED_CATEGORIES, message.getMessageDefinition() );
        }

        /******************************************************************
         Give a wrong interaction with 2 enzymes and no enzyme target
         *******************************************************************/
        componentA.setCvExperimentalRole( EnzymeMock.getMock() );
        componentB.setCvExperimentalRole( EnzymeMock.getMock() );
        messages = rule.check( interaction );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            //assertEquals(InteractionAndComponentRole.getNoEnzymeTargetDescription(),message.getDescription());
            //TODO: wrong assertion due to missing MessageDefinition
            assertEquals( MessageDefinition.INTERACTION_ROLES_MIXED_CATEGORIES, message.getMessageDefinition() );
        }

        /******************************************************************
         Give a wrong interaction with 2 enzyme targets and no enzyme
         *******************************************************************/
        componentA.setCvExperimentalRole( EnzymeTargetMock.getMock() );
        componentB.setCvExperimentalRole( EnzymeTargetMock.getMock() );
        messages = rule.check( interaction );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            //assertEquals(InteractionAndComponentRole.getNoEnzymeDescription(),message.getDescription());
            //TODO: wrong assertion due to missing MessageDefinition
            assertEquals( MessageDefinition.INTERACTION_ROLES_MIXED_CATEGORIES, message.getMessageDefinition() );
        }

        /******************************************************************
         Give a wrong interaction with 2 self components and no enzyme
         *******************************************************************/
        componentA.setCvExperimentalRole( SelfMock.getMock() );
        componentB.setCvExperimentalRole( SelfMock.getMock() );
        messages = rule.check( interaction );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            //assertEquals(InteractionAndComponentRole.getMoreThan2SelfProteinDescription(),message.getDescription());
            //TODO: wrong assertion due to missing MessageDefinition
            assertEquals( MessageDefinition.INTERACTION_ROLES_MIXED_CATEGORIES, message.getMessageDefinition() );
        }

        /******************************************************************
         Give a wrong interaction with only 1 component and stoechiometry 1
         *******************************************************************/
        componentA.setCvExperimentalRole( NeutralMock.getMock() );
        componentA.setStoichiometry( Float.parseFloat( "1" ) );
        components = new ArrayList<Component>();
        components.add( componentA );
        interaction.setComponents( components );
        messages = rule.check( interaction );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            //assertEquals(InteractionAndComponentRole.getOnly1NeutralDescription(),message.getDescription());
            //TODO: wrong assertion due to missing MessageDefinition
            assertEquals( MessageDefinition.INTERACTION_ROLES_MIXED_CATEGORIES, message.getMessageDefinition() );
        }

        /**************************************************************************
         Give a right interaction with only 1 neutral component but stoechiometry 2
         ***************************************************************************/
        componentA.setStoichiometry( Float.parseFloat( "2" ) );
        messages = rule.check( interaction );
        assertEquals( 0, messages.size() );

        /******************************************************************************************************
         Give a right interaction with 1 neutral component stoechiometry 1 but as well one inhibited component
         *******************************************************************************************************/
        componentA.setStoichiometry( Float.parseFloat( "1" ) );
        componentB.setCvExperimentalRole( InhibitedMock.getMock() );
        interaction.addComponent( componentB );
        messages = rule.check( interaction );
        assertEquals( 0, messages.size() );

        /******************************************************************************************************
         Give a wrong interaction with 1 fluorophore, 1 bait donor
         *******************************************************************************************************/
        componentA.setCvExperimentalRole( FluorophoreDonorMock.getMock() );
        componentB.setCvExperimentalRole( BaitMock.getMock() );
        messages = rule.check( interaction );
        assertEquals( 1, messages.size() );
        for ( GeneralMessage message : messages ) {
            assertEquals( MessageDefinition.INTERACTION_ROLES_MIXED_CATEGORIES, message.getMessageDefinition() );
        }


    }
}