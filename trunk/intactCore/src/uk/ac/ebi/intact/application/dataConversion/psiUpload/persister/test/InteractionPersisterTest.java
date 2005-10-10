/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.persister.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.Constants;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.*;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.persister.InteractionPersister;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.CommandLineOptions;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * That class .
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionPersisterTest extends TestCase {

    static {
        CommandLineOptions.getInstance().setDebugEnabled( true );
    }

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public InteractionPersisterTest( final String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( InteractionPersisterTest.class );
    }

    ///////////////////////////////
    // private attributes

    private Institution owner = new Institution( "EBI" );
    private BioSource human = new BioSource( owner, "human", "9606" );
    private CvXrefQualifier identity = new CvXrefQualifier( owner, "identity" );
    private CvDatabase psi = new CvDatabase( owner, Constants.PSI_DB_SHORTLABEL );
    private CvDatabase uniprot = new CvDatabase( owner, Constants.UNIPROT_DB_SHORTLABEL );
    private CvComponentRole bait = new CvComponentRole( owner, "bait" );
    private CvComponentRole prey = new CvComponentRole( owner, "prey" );
    private CvInteractorType proteinType = new CvInteractorType( owner, "protein" );
    private CvInteractorType interactionType = new CvInteractorType( owner, "interaction" );


    ///////////////////////////////
    // Utility methods

    private Component createComponent( Institution owner, Protein protein, CvComponentRole role ) {

        Experiment experiment = new Experiment( owner, "exp1", human );
        Collection experiments = new ArrayList();
        experiments.add( experiment );

        CvInteractionType cvInteractionType = new CvInteractionType( owner, "interactionType" );
        Interaction interaction = new InteractionImpl( experiments, cvInteractionType, interactionType, "interaction 1", owner );

        return new Component( owner, interaction, protein, role );
    }

    private Feature createFeature( Component component, CvFeatureType type, CvFeatureIdentification identification ) {

        Feature feature = new Feature( owner, "feature", component, type );
        feature.setCvFeatureIdentification( identification );

        return feature;
    }

    private CvFeatureType createFeatureType( String name, String psiID ) {

        if ( name == null ) {
            throw new IllegalArgumentException( "You must give a non null name to create a CvFeatureType" );
        }

        if ( psiID == null ) {
            throw new IllegalArgumentException( "You must give a non null PSI ID to create a CvFeatureType" );
        }

        CvFeatureType featureType = new CvFeatureType( owner, name );
        featureType.addXref( new Xref( owner, psi, psiID, null, null, identity ) );

        return featureType;
    }

    private CvFeatureIdentification createFeatureIdentification( String name, String psiID ) {

        if ( name == null ) {
            throw new IllegalArgumentException( "You must give a non null name to create a CvFeatureIdentification" );
        }

        if ( psiID == null ) {
            throw new IllegalArgumentException( "You must give a non null PSI ID to create a CvFeatureIdentification" );
        }

        CvFeatureIdentification featureIdentification = new CvFeatureIdentification( owner, name );
        featureIdentification.addXref( new Xref( owner, psi, psiID, null, null, identity ) );

        return featureIdentification;
    }

    private CvInteractionType createInteractionType( String name, String psiID ) {

        if ( name == null ) {
            throw new IllegalArgumentException( "You must give a non null name to create a CvFeatureIdentification" );
        }

        if ( psiID == null ) {
            throw new IllegalArgumentException( "You must give a non null PSI ID to create a CvFeatureIdentification" );
        }

        CvInteractionType cvInteractionType = new CvInteractionType( owner, name );
        cvInteractionType.addXref( new Xref( owner, psi, psiID, null, null, identity ) );

        return cvInteractionType;
    }


    ///////////////////////////////
    // Tests creation


    ////////////////////////////
    // 1. Comparison or Ranges
    public void testCompareRange_ok1() {

        Range range = new Range( owner, 1, 5, null );
        LocationTag location = new LocationTag( new LocationIntervalTag( 1, 1 ),
                                                new LocationIntervalTag( 5, 5 ) );

        assertTrue( InteractionPersister.areRangeEquals( range, location ) );
    }

    public void testCompareRange_ok2() {

        Range range = new Range( owner, 1, 1, 5, 5, null );
        LocationTag location = new LocationTag( new LocationIntervalTag( 1, 1 ),
                                                new LocationIntervalTag( 5, 5 ) );

        assertTrue( InteractionPersister.areRangeEquals( range, location ) );
    }

    public void testCompareRange_ok3() {

        Range range = new Range( owner, 1, 3, 5, 8, null );
        LocationTag location = new LocationTag( new LocationIntervalTag( 1, 3 ),
                                                new LocationIntervalTag( 5, 8 ) );

        assertTrue( InteractionPersister.areRangeEquals( range, location ) );
    }

    public void testCompareRange_ok4() {

        Range range = new Range( owner, 1, 3, 5, 9, null );
        LocationTag location = new LocationTag( new LocationIntervalTag( 1, 3 ),
                                                new LocationIntervalTag( 5, 8 ) );

        assertFalse( InteractionPersister.areRangeEquals( range, location ) );
    }

    public void testCompareRange_error1() {

        Range range = new Range( owner, 1, 6, null );
        LocationTag location = new LocationTag( new LocationIntervalTag( 1, 1 ),
                                                new LocationIntervalTag( 5, 5 ) );

        assertFalse( InteractionPersister.areRangeEquals( range, location ) );
    }

    public void testCompareRange_error2() {

        Range range = new Range( owner, 2, 5, null );
        LocationTag location = new LocationTag( new LocationIntervalTag( 1, 1 ),
                                                new LocationIntervalTag( 5, 5 ) );

        assertFalse( InteractionPersister.areRangeEquals( range, location ) );
    }

    public void testCompareRange_error3() {

        Range range = new Range( owner, 2, 6, null );
        LocationTag location = new LocationTag( new LocationIntervalTag( 1, 1 ),
                                                new LocationIntervalTag( 5, 5 ) );

        assertFalse( InteractionPersister.areRangeEquals( range, location ) );
    }

    public void testCompareRange_error4() {

        Range range = new Range( owner, 1, 3, 5, 8, null );
        LocationTag location = new LocationTag( new LocationIntervalTag( 1, 3 ),
                                                new LocationIntervalTag( 6, 8 ) );

        assertFalse( InteractionPersister.areRangeEquals( range, location ) );
    }


    /////////////////////////////////////////////
    // 2. Retreival of a PSI ID from a CvObject
    public void testGetPsiID_ok() {

        // create IntAct object
        CvFeatureType featureType = new CvFeatureType( owner, "type" );
        featureType.addXref( new Xref( owner, psi, "MI:type", null, null, null ) );

        // Check that they are equals
        assertEquals( "MI:type", InteractionPersister.getPsiID( featureType ) );
    }

    public void testGetPsiID_error() {

        // create IntAct object
        CvFeatureType featureType = new CvFeatureType( owner, "type" );

        // Check that they are equals
        assertNull( InteractionPersister.getPsiID( featureType ) );
    }


    //////////////////////////////
    // 3. Comparison of features
    public void testCheckFeature_compareFeatureSingleRange_ok() {

        /////////////////////////
        // create IntAct object
        Protein protein = new ProteinImpl( owner, human, "P12345", proteinType );
        protein.addXref( new Xref( owner, uniprot, "P12345", null, null, identity ) );

        Component component = createComponent( owner, protein, bait );

        CvFeatureType featureType = createFeatureType( "type", "MI:type" );
        CvFeatureIdentification featureIdentification = createFeatureIdentification( "detect", "MI:detect" );

        Feature feature = createFeature( component, featureType, featureIdentification );

        feature.addRange( new Range( owner, 1, 1, 3, 3, null ) );

        component.addBindingDomain( feature );


        //////////////////////
        // create PSI object
        XrefTag xrefTag = new XrefTag( XrefTag.PRIMARY_REF, "P12345", "uniprot" );
        OrganismTag organismTag = new OrganismTag( "9606" );
        ProteinInteractorTag proteinInteractorTag = new ProteinInteractorTag( xrefTag, organismTag );

        Collection features = new ArrayList();
        FeatureTypeTag featureTypeTag = new FeatureTypeTag( new XrefTag( XrefTag.PRIMARY_REF, "MI:type", "psi-mi" ) );
        FeatureDetectionTag featureDetectionTag = new FeatureDetectionTag( new XrefTag( XrefTag.PRIMARY_REF, "MI:detect", "psi-mi" ) );
        LocationTag locationTag = new LocationTag( new LocationIntervalTag( 1, 1 ), new LocationIntervalTag( 3, 3 ) );
        features.add( new FeatureTag( "feature1", "", featureTypeTag, locationTag, featureDetectionTag, null ) );

        ProteinParticipantTag proteinParticipantTag = new ProteinParticipantTag( proteinInteractorTag, "bait", null, features, null, null );


        ////////////////////////////////
        // Check that they are equals
        assertTrue( InteractionPersister.featureAreEquals( component, proteinParticipantTag ) );
    }

    public void testCheckFeature_compareFeatureMultiRange_ok() {

        /////////////////////////
        // create IntAct object
        Protein protein = new ProteinImpl( owner, human, "P12345", proteinType );
        protein.addXref( new Xref( owner, uniprot, "P12345", null, null, identity ) );

        Component component = createComponent( owner, protein, bait );

        CvFeatureType featureType = createFeatureType( "type", "MI:type" );
        CvFeatureIdentification featureIdentification = createFeatureIdentification( "detect", "MI:detect" );

        Feature feature = createFeature( component, featureType, featureIdentification );

        feature.addRange( new Range( owner, 1, 1, 3, 3, null ) );
        feature.addRange( new Range( owner, 4, 6, 10, 10, null ) );
        feature.addRange( new Range( owner, 15, 20, null ) );

        component.addBindingDomain( feature );


        //////////////////////
        // create PSI object
        // -------------------
        // Note: to represent that IntAct structure in PSI, we have to create 3 FeatureTag,
        // ----  each of them having a single LocationTag but we add an Xref at the Feature
        //       level to allow their clustering.

        XrefTag xrefTag = new XrefTag( XrefTag.PRIMARY_REF, "P12345", "uniprot" );
        OrganismTag organismTag = new OrganismTag( "9606" );
        ProteinInteractorTag proteinInteractorTag = new ProteinInteractorTag( xrefTag, organismTag );

        Collection features = new ArrayList( 1 );
        FeatureTypeTag featureTypeTag = new FeatureTypeTag( new XrefTag( XrefTag.PRIMARY_REF, "MI:type", "psi-mi" ) );
        FeatureDetectionTag featureDetectionTag = new FeatureDetectionTag( new XrefTag( XrefTag.PRIMARY_REF, "MI:detect", "psi-mi" ) );

        Collection xrefs = new ArrayList();
        xrefs.add( new XrefTag( XrefTag.SECONDARY_REF, "ClusterID=1", FeatureTag.FEATURE_CLUSTER_ID_XREF ) );

        LocationTag location1 = new LocationTag( new LocationIntervalTag( 1, 1 ), new LocationIntervalTag( 3, 3 ) );
        FeatureTag feature1 = new FeatureTag( "feature1", "", featureTypeTag, location1, featureDetectionTag, xrefs );
        features.add( feature1 );

        LocationTag location2 = new LocationTag( new LocationIntervalTag( 4, 6 ), new LocationIntervalTag( 10, 10 ) );
        FeatureTag feature2 = new FeatureTag( "feature2", "", featureTypeTag, location2, featureDetectionTag, xrefs );
        features.add( feature2 );

        LocationTag location3 = new LocationTag( new LocationIntervalTag( 15, 15 ), new LocationIntervalTag( 20, 20 ) );
        FeatureTag feature3 = new FeatureTag( "feature3", "", featureTypeTag, location3, featureDetectionTag, xrefs );
        features.add( feature3 );

        ProteinParticipantTag proteinParticipantTag = new ProteinParticipantTag( proteinInteractorTag, "bait", null, features, null, null );


        ////////////////////////////////
        // Check that they are equals
        assertTrue( InteractionPersister.featureAreEquals( component, proteinParticipantTag ) );
    }


    //////////////////////////////////
    // 4. Comparison of Interactions
    public void _testCheckFeature_compareInteraction_ok() {

        // TODO That test will only work if the checker has been running beforehand
        //      We have to get the IntAct Protein from a ProteinParticipantTag.

        /////////////////////////
        // create IntAct object
        Protein protein = new ProteinImpl( owner, human, "P12345", proteinType );
        protein.addXref( new Xref( owner, uniprot, "P12345", null, null, identity ) );
        Component component1 = createComponent( owner, protein, bait );

        Protein protein2 = new ProteinImpl( owner, human, "Q98765", proteinType );
        protein.addXref( new Xref( owner, uniprot, "Q98765", null, null, identity ) );
        Component component2 = createComponent( owner, protein2, prey );

        Collection components = new ArrayList();
        components.add( component1 );
        components.add( component2 );

        Experiment experiment = new Experiment( owner, "test-2004-1", human );
        Collection experiments = new ArrayList();
        experiments.add( experiment );

        CvInteractionType anInteractionType = createInteractionType( "intType", "MI:intTyp" );

        Interaction intactInteraction = new InteractionImpl( experiments, components, anInteractionType, interactionType, "p12345-q98765", owner );
        intactInteraction.setBioSource( human );
        

        //////////////////////
        // create PSI object
        OrganismTag organismTag = new OrganismTag( "9606" );

        XrefTag xrefTag1 = new XrefTag( XrefTag.PRIMARY_REF, "P12345", "uniprot" );
        ProteinInteractorTag proteinInteractorTag1 = new ProteinInteractorTag( xrefTag1, organismTag );

        XrefTag xrefTag2 = new XrefTag( XrefTag.PRIMARY_REF, "Q98765", "uniprot" );
        ProteinInteractorTag proteinInteractorTag2 = new ProteinInteractorTag( xrefTag2, organismTag );

        ProteinParticipantTag proteinParticipantTag1 = new ProteinParticipantTag( proteinInteractorTag1, "bait", null, null, null, null );
        ProteinParticipantTag proteinParticipantTag2 = new ProteinParticipantTag( proteinInteractorTag2, "prey", null, null, null, null );
        Collection psiParticipants = new ArrayList();
        psiParticipants.add( proteinParticipantTag1 );
        psiParticipants.add( proteinParticipantTag2 );


        XrefTag publi = new XrefTag( XrefTag.PRIMARY_REF, "12345678", Constants.PUBMED_DB_SHORTLABEL );
        HostOrganismTag hostOrganismTag = new HostOrganismTag( "9606" );
        InteractionDetectionTag detectionTag = new InteractionDetectionTag( new XrefTag( XrefTag.PRIMARY_REF, "MI:intDetect", Constants.PSI_DB_SHORTLABEL ) );
        ParticipantDetectionTag participantDetectionTag = new ParticipantDetectionTag( new XrefTag( XrefTag.PRIMARY_REF, "MI:parDetect", Constants.PSI_DB_SHORTLABEL ) );

        ExperimentDescriptionTag experimentTag = new ExperimentDescriptionTag( "test-2004-1",
                                                                               "test-2004-1 fullname",
                                                                               publi,
                                                                               null, null, null,
                                                                               hostOrganismTag,
                                                                               detectionTag,
                                                                               participantDetectionTag );
        Collection psiExperiments = new ArrayList();
        psiExperiments.add( experimentTag );

        InteractionTypeTag interactionTypeTag = new InteractionTypeTag( new XrefTag( XrefTag.PRIMARY_REF, "MI:intType", Constants.PSI_DB_SHORTLABEL ) );

        InteractionTag psiInteraction = new InteractionTag( "", "",
                                                            psiExperiments,
                                                            psiParticipants,
                                                            interactionTypeTag,
                                                            null,
                                                            null,
                                                            null );

        Collection psiExperimentWrappers = new ArrayList();
        psiExperimentWrappers.add( new InteractionPersister.ExperimentWrapper( experimentTag ) );

        ////////////////////////////////
        // Check that they are equals
        InteractionPersister.alreadyExistsInIntact( psiInteraction,
                                                    psiExperimentWrappers,
                                                    intactInteraction );

        assertEquals( 1, psiExperimentWrappers.size() );
    }

}