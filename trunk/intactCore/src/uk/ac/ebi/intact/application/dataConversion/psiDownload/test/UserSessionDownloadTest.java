// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.application.dataConversion.psiDownload.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.dataConversion.PsiVersion;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.UserSessionDownload;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.test.util.TestableProtein;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO document this ;o)
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class UserSessionDownloadTest extends PsiDownloadTest {

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( UserSessionDownloadTest.class );
    }

    ////////////////////////
    // Tests

    public void testIsExportable() {

        UserSessionDownload session = new UserSessionDownload( PsiVersion.VERSION_1 );
        session.addAnnotationFilter( remark );

        Annotation annotation = new Annotation( owner, remark );
        assertFalse( session.isExportable( annotation ) );

        annotation = new Annotation( owner, comment );
        assertTrue( session.isExportable( annotation ) );
    }

    public void testIsProteinAlreadyDefined() {

        //Protein protein = new TestableProtein( "EBI-111", owner, yeast, "prot", proteinType, null );
        Protein protein = new TestableProtein( "EBI-111", owner, yeast, "prot", null );
        UserSessionDownload session = new UserSessionDownload( PsiVersion.VERSION_1 );

        assertFalse( session.isAlreadyDefined( protein ) );
        session.declareAlreadyDefined( protein );
        assertTrue( session.isAlreadyDefined( protein ) );
    }

    public void testIsExperimentAlreadyDefined() {

        Experiment experiment = new Experiment( owner, "prot", yeast );
        UserSessionDownload session = new UserSessionDownload( PsiVersion.VERSION_1 );

        assertFalse( session.isAlreadyDefined( experiment ) );
        session.declareAlreadyDefined( experiment );
        assertTrue( session.isAlreadyDefined( experiment ) );
    }

    public void testGetNextExperimentIdentifier() {

        UserSessionDownload session = new UserSessionDownload( PsiVersion.VERSION_1 );

        try {
            session.getNextExperimentIdentifier( null );
            fail( "null is not allowed." );
        } catch ( Exception e ) {
            // ok
        }

        Experiment experiment = new Experiment( owner, "exp", human );
        Experiment experiment2 = new Experiment( owner, "exp2", human );
        Experiment experiment3 = new Experiment( owner, "exp3", yeast );

        long id = session.getNextExperimentIdentifier( experiment );
        assertEquals( id, session.getExperimentIdentifier( experiment ) );

        long id2 = session.getNextExperimentIdentifier( experiment2 );
        assertEquals( id2, session.getExperimentIdentifier( experiment2 ) );

        long id3 = session.getNextExperimentIdentifier( experiment3 );
        assertEquals( id3, session.getExperimentIdentifier( experiment3 ) );

        assertFalse( id == id2 );
        assertFalse( id == id3 );
        assertFalse( id2 == id3 );

        try {
            id = session.getNextExperimentIdentifier( experiment ); // already generated
        } catch ( Exception e ) {
            //ok
        }
    }

    public void testGetNextParticipantIdentifier() {

        UserSessionDownload session = new UserSessionDownload( PsiVersion.VERSION_1 );

        try {
            session.getNextParticipantIdentifier( null );
            fail( "null is not allowed." );
        } catch ( Exception e ) {
            // ok
        }

        Experiment experiment = new Experiment( owner, "exp", human );
        Collection experiments = new ArrayList( 1 );
        experiments.add( experiment );
        Interaction interaction = new InteractionImpl( experiments, aggregation, interactionType, "interaction", owner );
        Protein protein = new ProteinImpl( owner, yeast, "bbc1_yeast", proteinType );
        Protein protein2 = new ProteinImpl( owner, yeast, "cdc42_yeast", proteinType );

        Component component = new Component( owner, interaction, protein, bait );
        Component component2 = new Component( owner, interaction, protein, prey );
        Component component3 = new Component( owner, interaction, protein2, prey );

        long id = session.getNextParticipantIdentifier( component );
        assertEquals( id, session.getParticipantIdentifier( component ) );

        long id2 = session.getNextParticipantIdentifier( component2 );
        assertEquals( id2, session.getParticipantIdentifier( component2 ) );

        long id3 = session.getNextParticipantIdentifier( component3 );
        assertEquals( id3, session.getParticipantIdentifier( component3 ) );

        assertFalse( id == id2 );
        assertFalse( id == id3 );
        assertFalse( id2 == id3 );

        try {
            id = session.getNextParticipantIdentifier( component ); // already generated
        } catch ( Exception e ) {
            //ok
        }
    }

    public void testResetParticipantIdentifier() {

        UserSessionDownload session = new UserSessionDownload( PsiVersion.VERSION_1 );

        Experiment experiment = new Experiment( owner, "exp", human );
        Collection experiments = new ArrayList( 1 );
        experiments.add( experiment );
        Interaction interaction = new InteractionImpl( experiments, aggregation, interactionType, "interaction", owner );
        Protein protein = new ProteinImpl( owner, yeast, "bbc1_yeast", proteinType );
        Protein protein2 = new ProteinImpl( owner, yeast, "cdc42_yeast", proteinType );

        Component component = new Component( owner, interaction, protein, bait );
        Component component2 = new Component( owner, interaction, protein, prey );
        Component component3 = new Component( owner, interaction, protein2, prey );

        long id = session.getNextParticipantIdentifier( component );
        assertEquals( id, session.getParticipantIdentifier( component ) );

        long id2 = session.getNextParticipantIdentifier( component2 );
        assertEquals( id2, session.getParticipantIdentifier( component2 ) );

        session.resetParticipantIdentifier();

        // start from the beginning again
        assertEquals( id, session.getNextParticipantIdentifier( component3 ) );
    }

    public void testGetNextFeatureIdentifier() {

        UserSessionDownload session = new UserSessionDownload( PsiVersion.VERSION_1 );

        try {
            session.getNextFeatureIdentifier( null );
            fail( "null is not allowed." );
        } catch ( Exception e ) {
            // ok
        }

        Experiment experiment = new Experiment( owner, "exp", human );
        Collection experiments = new ArrayList( 1 );
        experiments.add( experiment );
        Interaction interaction = new InteractionImpl( experiments, aggregation, interactionType, "interaction", owner );
        Protein protein = new ProteinImpl( owner, yeast, "bbc1_yeast", proteinType );
        Protein protein2 = new ProteinImpl( owner, yeast, "cdc42_yeast", proteinType );

        Component component = new Component( owner, interaction, protein, bait );
        Component component2 = new Component( owner, interaction, protein, prey );
        Component component3 = new Component( owner, interaction, protein2, prey );

        Feature feature = new Feature( owner, "region", component, formylation );
        Feature feature2 = new Feature( owner, "region", component2, formylation );
        Feature feature3 = new Feature( owner, "region", component3, hydroxylation );

        long id = session.getNextFeatureIdentifier( feature );
        assertEquals( id, session.getFeatureIdentifier( feature ) );

        long id2 = session.getNextFeatureIdentifier( feature2 );
        assertEquals( id2, session.getFeatureIdentifier( feature2 ) );

        long id3 = session.getNextFeatureIdentifier( feature3 );
        assertEquals( id3, session.getFeatureIdentifier( feature3 ) );

        assertFalse( id == id2 );
        assertFalse( id == id3 );
        assertFalse( id2 == id3 );

        try {
            id = session.getNextFeatureIdentifier( feature ); // already generated
        } catch ( Exception e ) {
            //ok
        }
    }

    public void testResetFeatureIdentifier() {

        UserSessionDownload session = new UserSessionDownload( PsiVersion.VERSION_1 );

        Experiment experiment = new Experiment( owner, "exp", human );
        Collection experiments = new ArrayList( 1 );
        experiments.add( experiment );
        Interaction interaction = new InteractionImpl( experiments, aggregation, interactionType, "interaction", owner );
        Protein protein = new ProteinImpl( owner, yeast, "bbc1_yeast", proteinType );

        Component component = new Component( owner, interaction, protein, bait );
        Component component2 = new Component( owner, interaction, protein, prey );

        Feature feature = new Feature( owner, "region", component, formylation );
        Feature feature2 = new Feature( owner, "region", component2, formylation );

        long id = session.getNextFeatureIdentifier( feature );
        assertEquals( id, session.getFeatureIdentifier( feature ) );

        long id2 = session.getNextFeatureIdentifier( feature2 );
        assertEquals( id2, session.getFeatureIdentifier( feature2 ) );

        session.resetFeatureIdentifier();

        // start from the beginning again
        assertEquals( id, session.getNextFeatureIdentifier( feature2 ) );
    }

    public void testGetNextInteractorIdentifier() {

        UserSessionDownload session = new UserSessionDownload( PsiVersion.VERSION_1 );

        try {
            session.getNextInteractorIdentifier( null );
            fail( "null is not allowed." );
        } catch ( Exception e ) {
            // ok
        }

        Protein protein = new ProteinImpl( owner, yeast, "bbc1_yeast", proteinType );
        Protein protein2 = new ProteinImpl( owner, yeast, "cdc42_yeast", proteinType );
        Experiment experiment = new Experiment( owner, "exp", human );
        Collection experiments = new ArrayList( 1 );
        experiments.add( experiment );
        Interaction interaction = new InteractionImpl( experiments, aggregation, interactionType, "interaction", owner );


        long id = session.getNextInteractorIdentifier( protein );
        assertEquals( id, session.getInteractorIdentifier( protein ) );

        long id2 = session.getNextInteractorIdentifier( protein2 );
        assertEquals( id2, session.getInteractorIdentifier( protein2 ) );

        long id3 = session.getNextInteractorIdentifier( interaction );
        assertEquals( id3, session.getInteractorIdentifier( interaction ) );

        assertFalse( id == id2 );
        assertFalse( id == id3 );
        assertFalse( id2 == id3 );

        try {
            id = session.getNextExperimentIdentifier( experiment ); // already generated
        } catch ( Exception e ) {
            //ok
        }
    }

    public void testGetNextInteractionIdentifier() {

        UserSessionDownload session = new UserSessionDownload( PsiVersion.VERSION_1 );

        try {
            session.getNextInteractionIdentifier( null );
            fail( "null is not allowed." );
        } catch ( Exception e ) {
            // ok
        }

        Experiment experiment = new Experiment( owner, "exp", human );
        Collection experiments = new ArrayList( 1 );
        experiments.add( experiment );
        Interaction interaction = new InteractionImpl( experiments, aggregation, interactionType, "interaction", owner );
        Interaction interaction2 = new InteractionImpl( experiments, cleavage, interactionType, "interaction2", owner );
        Interaction interaction3 = new InteractionImpl( experiments, aggregation, interactionType, "interaction3", owner );


        long id = session.getNextInteractionIdentifier( interaction );
        assertEquals( id, session.getInteractionIdentifier( interaction ) );

        long id2 = session.getNextInteractionIdentifier( interaction2 );
        assertEquals( id2, session.getInteractionIdentifier( interaction2 ) );

        long id3 = session.getNextInteractionIdentifier( interaction3 );
        assertEquals( id3, session.getInteractionIdentifier( interaction3 ) );

        assertFalse( id == id2 );
        assertFalse( id == id3 );
        assertFalse( id2 == id3 );

        try {
            id = session.getNextInteractorIdentifier( interaction3 ); // already generated
        } catch ( Exception e ) {
            //ok
        }
    }
}