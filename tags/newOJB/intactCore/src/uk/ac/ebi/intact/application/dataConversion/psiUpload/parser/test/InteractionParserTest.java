/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.*;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.InteractionParser;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockDocumentBuilder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockXmlContent;
import uk.ac.ebi.intact.util.test.mocks.MockInputStream;

import java.util.Collection;
import java.util.Iterator;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionParserTest extends TestCase {

    private class MultipleParticipantFound extends Exception {

        public MultipleParticipantFound( String role ) {
            super( "Multiple ProteinParticipantTag found for the role: " + role );
        }
    }

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public InteractionParserTest( final String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( InteractionParserTest.class );
    }

    private ProteinParticipantTag getParticipantByRole( Collection participants, String role )
            throws MultipleParticipantFound {

        if( role == null || "".equals( role.trim() ) ) {
            return null;
        }
        ProteinParticipantTag found = null;

        for ( Iterator iterator = participants.iterator(); iterator.hasNext(); ) {
            ProteinParticipantTag proteinParticipant = (ProteinParticipantTag) iterator.next();
            if( role.equals( proteinParticipant.getRole() ) ) {
                if( found != null ) {
                    throw new MultipleParticipantFound( role );
                } else {
                    found = proteinParticipant;
                }
            }
        }

        return found;
    }

    public void testProcess() {

        final MockInputStream is = new MockInputStream();
        is.setBuffer( MockXmlContent.INTERACTION_1 );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        InteractionTag interaction = null;
        final InteractionParser psiInteraction = new InteractionParser( null, null, element );
        interaction = psiInteraction.process();

        assertNotNull( interaction );

        assertEquals( "intShortlabel", interaction.getShortlabel() );
        assertEquals( "intFullname", interaction.getFullname() );


        // Xrefs
        final Collection xrefs = interaction.getXrefs();
        assertNotNull( xrefs );
        assertEquals( 2, xrefs.size() );

        XrefTag xref = Utilities.getXrefByCvDatabase( xrefs, "pubmed" );
        assertNotNull( xref );
        assertEquals( "pubmed", xref.getDb() );
        assertEquals( "11805826", xref.getId() );
        assertEquals( "mySecondaryId", xref.getSecondary() );
        assertEquals( "version1", xref.getVersion() );

        xref = Utilities.getXrefByCvDatabase( xrefs, "sgd" );
        assertNotNull( xref );
        assertEquals( "sgd", xref.getDb() );
        assertEquals( "S0006220", xref.getId() );
        assertEquals( "TIF6", xref.getSecondary() );
        assertEquals( "", xref.getVersion() );


        // Annotations
        final Collection annotations = interaction.getAnnotations();
        assertNotNull( annotations );
        assertEquals( 3, annotations.size() );

        AnnotationTag annotation;
        annotation = Utilities.getAnnotationByType( annotations, "comment" );
        assertNotNull( annotation );
        assertEquals( "comment", annotation.getType() );
        assertEquals( "a first comment.", annotation.getText() );

        annotation = Utilities.getAnnotationByType( annotations, "test" );
        assertNotNull( annotation );
        assertEquals( "test", annotation.getType() );
        assertEquals( "A first test", annotation.getText() );

        annotation = Utilities.getAnnotationByType( annotations, "remark" );
        assertNotNull( annotation );
        assertEquals( "remark", annotation.getType() );
        assertEquals( "Oh! a remark", annotation.getText() );


        // experiments
        Collection experiments = interaction.getExperiments();
        assertNotNull( experiments );
        assertEquals( 1, experiments.size() );
        ExperimentDescriptionTag experimentDescription = (ExperimentDescriptionTag) experiments.iterator().next();
        // This is already tested in the ExperimentDescriptionTest, let reuse it !
        ExperimentDescriptionParserTest.checkExperimentContent_gavin_2002( experimentDescription );


        // CvInteractionType
        final InteractionTypeTag cvInteractionType = interaction.getInteractionType();
        assertNotNull( cvInteractionType );
        xref = cvInteractionType.getPsiDefinition();
        assertNotNull( xref );
        assertEquals( "psi-mi", xref.getDb() );
        assertEquals( "MI:0109", xref.getId() );
        assertEquals( "", xref.getSecondary() );
        assertEquals( "", xref.getVersion() );


        // participants
        final Collection participants = interaction.getParticipants();
        assertNotNull( participants );
        assertEquals( 2, participants.size() );
        ProteinParticipantTag proteinParticipant = null;
        try {
            proteinParticipant = getParticipantByRole( participants, "bait" );
            assertNotNull( proteinParticipant );
        } catch ( MultipleParticipantFound multipleParticipantFound ) {
            fail( "multiple bait found where only one should exist." );
        }

        ProteinInteractorTag protein = proteinParticipant.getProteinInteractor();
        assertNotNull( protein );

        OrganismTag organism = protein.getOrganism();
        assertNotNull( organism );
        assertEquals( "4932", organism.getTaxId() );

        xref = protein.getUniprotXref();
        assertNotNull( xref );
        assertEquals( "uniprot", xref.getDb() );
        assertEquals( "P12345", xref.getId() );
        assertEquals( "blablabla", xref.getSecondary() );
        assertEquals( "2.46", xref.getVersion() );


        try {
            proteinParticipant = getParticipantByRole( participants, "prey" );
            assertNotNull( proteinParticipant );
        } catch ( MultipleParticipantFound multipleParticipantFound ) {
            fail( "multiple prey found where only one should exist." );
        }

        protein = proteinParticipant.getProteinInteractor();
        assertNotNull( protein );

        organism = protein.getOrganism();
        assertNotNull( organism );
        assertEquals( "4932", organism.getTaxId() );

        xref = protein.getUniprotXref();
        assertNotNull( xref );
        assertEquals( "uniprot", xref.getDb() );
        assertEquals( "Q12522", xref.getId() );
        assertEquals( "if6_yeast", xref.getSecondary() );
        assertEquals( "", xref.getVersion() );


        ConfidenceTag confidence = interaction.getConfidence();
        assertNotNull( confidence );
        assertEquals( "arbitrary", confidence.getUnit() );
        assertEquals( "high", confidence.getValue() );
    }
}
