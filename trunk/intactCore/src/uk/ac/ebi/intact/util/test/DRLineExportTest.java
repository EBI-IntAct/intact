/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.util.DRLineExport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class DRLineExportTest extends TestCase {

    // needed controlled vocabulary (ie. the DRLineExport relies on it)
    private CvDatabase uniprot;
    private CvTopic uniprotDrExport;
    private CvTopic authorConfidence;
    private CvXrefQualifier identityCvXrefQualifier;

    private Institution institution;

    private Interaction interaction1a;
    private Interaction interaction2a;
    private Interaction interaction3a;

    private Interaction interaction1b;
    private Interaction interaction2b;

    private Protein protein1;
    private Protein protein2;
    private Protein protein3;
    private Protein protein4;


    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( DRLineExportTest.class );
    }


    public void setUp() {

        institution = new Institution( "MyInstitution" );

        // create the needed vocabulary
        uniprotDrExport = new CvTopic( institution, "uniprot-dr-export" );
        authorConfidence = new CvTopic( institution, "author-confidence" );
        uniprot = new CvDatabase( institution, "uniprot" );
        identityCvXrefQualifier = new CvXrefQualifier( institution, "identity" );


        // Create proteins
        BioSource bioSource = new BioSource( institution, "bio1", "1" );
        CvDatabase cvDatabase = new CvDatabase( institution, "oneOfMine" );

        protein1 = new ProteinImpl( institution, bioSource, "PROT-1" );
        protein1.addXref( new Xref( institution, uniprot, "PROT-1", null, null, identityCvXrefQualifier ) );
        protein1.addXref( new Xref( institution, cvDatabase, "1laigvh", null, null, null ) );
        protein1.addXref( new Xref( institution, cvDatabase, "1slgn", null, null, null ) );

        protein2 = new ProteinImpl( institution, bioSource, "PROT-2" );
        protein2.addXref( new Xref( institution, cvDatabase, "2qwerty", null, null, null ) );
        protein2.addXref( new Xref( institution, uniprot, "PROT-2", null, null, identityCvXrefQualifier ) );
        protein2.addXref( new Xref( institution, cvDatabase, "2zxcvb", null, null, null ) );

        protein3 = new ProteinImpl( institution, bioSource, "PROT-3" );
        protein3.addXref( new Xref( institution, cvDatabase, "3asfdg", null, null, null ) );
        protein3.addXref( new Xref( institution, cvDatabase, "3ryuk", null, null, null ) );
        protein3.addXref( new Xref( institution, uniprot, "PROT-3", null, null, identityCvXrefQualifier ) );
        protein3.addXref( new Xref( institution, cvDatabase, "3lkjhgf", null, null, null ) );

        protein4 = new ProteinImpl( institution, bioSource, "PROT-4" );
        protein4.addXref( new Xref( institution, cvDatabase, "4alklk", null, null, null ) );
        protein4.addXref( new Xref( institution, cvDatabase, "4pppp", null, null, null ) );
        protein4.addXref( new Xref( institution, uniprot, "PROT-4", null, null, identityCvXrefQualifier ) );
    }


    //////////////////////
    // UTILITY METHOD

    private CvInteraction initCvInteraction() {

        CvTopic topic1 = new CvTopic( institution, "foo" );
        CvTopic topic2 = new CvTopic( institution, "foobar" );

        CvInteraction cvInteraction = new CvInteraction( institution, "experimentalMethod" );
        cvInteraction.addAnnotation( new Annotation( institution, topic1 ) );
        cvInteraction.addAnnotation( new Annotation( institution, topic2 ) );
        cvInteraction.addAnnotation( new Annotation( institution, topic1 ) );

        return cvInteraction;
    }

    /**
     * Gives back an experiment ahving the following interactions
     * 1a (P1 P2)
     * 2a (P2 P3)
     * 3a (P3 P3)
     *
     * @return
     */
    private Experiment initExperimentA() {

        Experiment experiment;

        BioSource bioSource;
        CvComponentRole componentRole;

        bioSource = new BioSource( institution, "bio1", "1" );

        CvTopic topic1 = new CvTopic( institution, "foo" );
        CvTopic topic2 = new CvTopic( institution, "foobar" );

        experiment = new Experiment( institution, "experimentA", bioSource );
        experiment.setFullName( "test experiment A" );
        experiment.addAnnotation( new Annotation( institution, topic1 ) );
        experiment.addAnnotation( new Annotation( institution, topic1 ) );
        experiment.addAnnotation( new Annotation( institution, topic2 ) );

        CvInteraction cvInteraction = initCvInteraction();
        experiment.setCvInteraction( cvInteraction );

        //set up some collections to be added to later - needed for
        //some of the constructors..
        Collection experiments = new ArrayList();

        experiments.add( experiment );

        //needs exps, components (empty in this case), type, shortlabel, owner...
        //No need to set BioSource - taken from the Experiment...
        interaction1a = new InteractionImpl( experiments, new ArrayList(), null, "int1a", institution );
        interaction1a.addAnnotation( new Annotation( institution, topic1 ) );
        interaction1a.addAnnotation( new Annotation( institution, topic2 ) );

        interaction2a = new InteractionImpl( experiments, new ArrayList(), null, "int2a", institution );
        interaction2a.addAnnotation( new Annotation( institution, topic2 ) );
        interaction2a.addAnnotation( new Annotation( institution, topic1 ) );

        interaction3a = new InteractionImpl( experiments, new ArrayList(), null, "int3a", institution );
        interaction3a.addAnnotation( new Annotation( institution, topic1 ) );
        interaction3a.addAnnotation( new Annotation( institution, topic1 ) );

        //now link up interactions and proteins via some components..
        componentRole = new CvComponentRole( institution, "role" );

        // Creating the Conponent (it updates the Interaction and Protein).
        new Component( institution, interaction1a, protein1, componentRole );
        new Component( institution, interaction1a, protein2, componentRole );
        new Component( institution, interaction2a, protein2, componentRole );
        new Component( institution, interaction2a, protein3, componentRole );
        new Component( institution, interaction3a, protein3, componentRole );
        new Component( institution, interaction3a, protein3, componentRole );

        // link up experiment and interactions
        experiment.addInteraction( interaction1a );
        experiment.addInteraction( interaction2a );
        experiment.addInteraction( interaction3a );

        return experiment;
    }

    /**
     * Gives back an experiment ahving the following interactions
     * 1b (P1 P2)
     * 2b (P4 P4)
     *
     * @return
     */
    private Experiment initExperimentB() {

        Experiment experiment;

        BioSource bioSource;
        CvComponentRole componentRole;

        bioSource = new BioSource( institution, "bio1", "1" );

        CvTopic topic1 = new CvTopic( institution, "foo" );
        CvTopic topic2 = new CvTopic( institution, "foobar" );

        experiment = new Experiment( institution, "experimentB", bioSource );
        experiment.setFullName( "test experiment B" );
        experiment.addAnnotation( new Annotation( institution, topic1 ) );
        experiment.addAnnotation( new Annotation( institution, topic1 ) );
        experiment.addAnnotation( new Annotation( institution, topic2 ) );

        CvInteraction cvInteraction = initCvInteraction();
        experiment.setCvInteraction( cvInteraction );

        //set up some collections to be added to later - needed for
        //some of the constructors..
        Collection experiments = new ArrayList();

        experiments.add( experiment );

        //needs exps, components (empty in this case), type, shortlabel, owner...
        //No need to set BioSource - taken from the Experiment...
        interaction1b = new InteractionImpl( experiments, new ArrayList(), null, "int1b", institution );
        interaction1b.addAnnotation( new Annotation( institution, topic1 ) );
        interaction1b.addAnnotation( new Annotation( institution, topic2 ) );

        interaction2b = new InteractionImpl( experiments, new ArrayList(), null, "int2b", institution );
        interaction2b.addAnnotation( new Annotation( institution, topic2 ) );
        interaction2b.addAnnotation( new Annotation( institution, topic1 ) );

        //now link up interactions and proteins via some components..
        componentRole = new CvComponentRole( institution, "role" );

        // Creating the Conponent (it updates the Interaction and Protein).
        new Component( institution, interaction1b, protein1, componentRole );
        new Component( institution, interaction1b, protein2, componentRole );
        new Component( institution, interaction2b, protein4, componentRole );
        new Component( institution, interaction2b, protein4, componentRole );

        // link up experiment and interactions
        experiment.addInteraction( interaction1b );
        experiment.addInteraction( interaction2b );

        return experiment;
    }

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public DRLineExportTest( String name ) {
        super( name );
    }

    private DRLineExport getDrLineExporter() {

        DRLineExport drLineExporter = new DRLineExport() {

            // Override that method and give some mock object for init !
            public void init( IntactHelper helper ) {
                // provide the content of what should have been picked up from a Database.
                this.uniprotDatabase = uniprot;
                this.identityXrefQualifier = identityCvXrefQualifier;
                this.uniprotDR_Export = uniprotDrExport;
                this.authorConfidenceTopic = authorConfidence;
            }
        };

        try {
            // we have overriden the method but now we have to call it to initialise our object.
            drLineExporter.init( null );
        } catch ( Exception e ) {
            // should never happen !!
            e.printStackTrace();
        }

        return drLineExporter;
    }



    //////////////////////
    //   T E S T S

    public void testGetExperimentExportStatus_Export() {

        Experiment experiment = initExperimentA();

        Annotation annotation = new Annotation( institution, uniprotDrExport );
        annotation.setAnnotationText( "yes" );
        experiment.addAnnotation( annotation );

        DRLineExport exporter = getDrLineExporter();
        DRLineExport.ExperimentStatus status = exporter.getExperimentExportStatus( experiment );

        assertNotNull( status );
        assertEquals( true, status.doExport() );
        assertEquals( false, status.doNotExport() );
        assertEquals( false, status.isLargeScale() );
        assertEquals( false, status.isNotSpecified() );

        assertNull( status.getKeywords() );
    }

    public void testGetExperimentExportStatus_DoNotExport() {

        Experiment experiment = initExperimentA();

        Annotation annotation = new Annotation( institution, uniprotDrExport );
        annotation.setAnnotationText( "no" );
        experiment.addAnnotation( annotation );

        DRLineExport exporter = getDrLineExporter();
        DRLineExport.ExperimentStatus status = exporter.getExperimentExportStatus( experiment );

        assertNotNull( status );
        assertEquals( false, status.doExport() );
        assertEquals( true, status.doNotExport() );
        assertEquals( false, status.isLargeScale() );
        assertEquals( false, status.isNotSpecified() );

        assertNull( status.getKeywords() );
    }

    public void testGetExperimentExportStatus_NotSpecified() {

        Experiment experiment = initExperimentA();

        DRLineExport exporter = getDrLineExporter();
        DRLineExport.ExperimentStatus status = exporter.getExperimentExportStatus( experiment );

        assertNotNull( status );
        assertEquals( false, status.doExport() );
        assertEquals( false, status.doNotExport() );
        assertEquals( false, status.isLargeScale() );
        assertEquals( true, status.isNotSpecified() );

        assertNull( status.getKeywords() );
    }

    public void testGetExperimentExportStatus_LargeScale() {

        Experiment experiment = initExperimentA();

        String keyword1 = "CORE-1";
        String keyword2 = "CORE-2";

        Annotation annotation1 = new Annotation( institution, uniprotDrExport );
        annotation1.setAnnotationText( keyword1 );
        experiment.addAnnotation( annotation1 );

        Annotation annotation2 = new Annotation( institution, uniprotDrExport );
        annotation2.setAnnotationText( keyword2 );
        experiment.addAnnotation( annotation2 );

        DRLineExport exporter = getDrLineExporter();
        DRLineExport.ExperimentStatus status = exporter.getExperimentExportStatus( experiment );

        assertNotNull( status );
        assertEquals( false, status.doExport() );
        assertEquals( false, status.doNotExport() );
        assertEquals( true, status.isLargeScale() );
        assertEquals( false, status.isNotSpecified() );

        Collection keywords = status.getKeywords();
        assertNotNull( keywords );
        assertEquals( 2, keywords.size() );
        assertTrue( keywords.contains( keyword1.toLowerCase() ) );
        assertTrue( keywords.contains( keyword2.toLowerCase() ) );
    }


    public void testGetCvInteractionExportStatus_Export() {

        DRLineExport exporter = getDrLineExporter();

        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation = new Annotation( institution, uniprotDrExport );
        annotation.setAnnotationText( "yes" );
        cvInteraction.addAnnotation( annotation );

        DRLineExport.CvInteractionStatus status = exporter.getMethodExportStatus( cvInteraction );

        assertNotNull( status );
        assertEquals( true, status.doExport() );
        assertEquals( false, status.doNotExport() );
        assertEquals( false, status.isNotSpecified() );
        assertEquals( false, status.isConditionalExport() );
    }

    public void testGetCvInteractionExportStatus_DoNotExport() {

        DRLineExport exporter = getDrLineExporter();

        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation = new Annotation( institution, uniprotDrExport );
        annotation.setAnnotationText( "no" );
        cvInteraction.addAnnotation( annotation );

        DRLineExport.CvInteractionStatus status = exporter.getMethodExportStatus( cvInteraction );

        assertNotNull( status );
        assertEquals( false, status.doExport() );
        assertEquals( true, status.doNotExport() );
        assertEquals( false, status.isNotSpecified() );
        assertEquals( false, status.isConditionalExport() );
    }

    public void testGetCvInteractionExportStatus_NotSpecified() {

        DRLineExport exporter = getDrLineExporter();

        CvInteraction cvInteraction = initCvInteraction();

        DRLineExport.CvInteractionStatus status = exporter.getMethodExportStatus( cvInteraction );

        assertNotNull( status );
        assertEquals( false, status.doExport() );
        assertEquals( true, status.doNotExport() );
        assertEquals( false, status.isNotSpecified() );
        assertEquals( false, status.isConditionalExport() );
    }

    public void testGetCvInteractionExportStatus_ConditionalExport() {

        DRLineExport exporter = getDrLineExporter();

        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation = new Annotation( institution, uniprotDrExport );
        annotation.setAnnotationText( "3" );
        cvInteraction.addAnnotation( annotation );

        DRLineExport.CvInteractionStatus status = exporter.getMethodExportStatus( cvInteraction );

        assertNotNull( status );
        assertEquals( false, status.doExport() );
        assertEquals( false, status.doNotExport() );
        assertEquals( false, status.isNotSpecified() );
        assertEquals( true, status.isConditionalExport() );

        assertEquals( 3, status.getMinimumOccurence() );
    }

    public void testGetCvInteractionExportStatus_JunkValue() {

        DRLineExport exporter = getDrLineExporter();

        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation = new Annotation( institution, uniprotDrExport );
        annotation.setAnnotationText( "junk" );
        cvInteraction.addAnnotation( annotation );

        DRLineExport.CvInteractionStatus status = exporter.getMethodExportStatus( cvInteraction );

        assertNotNull( status );
        assertEquals( false, status.doExport() );
        assertEquals( true, status.doNotExport() );
        assertEquals( false, status.isNotSpecified() );
        assertEquals( false, status.isConditionalExport() );
    }

    public void testGetCvInteractionExportStatus_MultipleValues() {

        DRLineExport exporter = getDrLineExporter();

        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation1 = new Annotation( institution, uniprotDrExport );
        annotation1.setAnnotationText( "yes" );
        cvInteraction.addAnnotation( annotation1 );

        Annotation annotation2 = new Annotation( institution, uniprotDrExport );
        annotation2.setAnnotationText( "no" );
        cvInteraction.addAnnotation( annotation2 );

        DRLineExport.CvInteractionStatus status = exporter.getMethodExportStatus( cvInteraction );

        assertNotNull( status );
        assertEquals( false, status.doExport() );
        assertEquals( true, status.doNotExport() );
        assertEquals( false, status.isNotSpecified() );
        assertEquals( false, status.isConditionalExport() );
    }


    public void testGetUniprotID() {

        DRLineExport exporter = getDrLineExporter();

        String id = exporter.getUniprotID( protein1 );
        assertNotNull( id );
        assertEquals( "PROT-1", id );
    }


    public void testGetProteinEligibleForExport_NothingSpecified() {

        DRLineExport exporter = getDrLineExporter();

        Collection proteins = new ArrayList();
        proteins.add( protein1 );
        proteins.add( protein2 );
        proteins.add( protein3 );

        Set eligibleProteins = exporter.getProteinEligibleForExport( proteins );

        assertNotNull( eligibleProteins );
        assertEquals( 0, eligibleProteins.size() );
    }

    public void testGetProteinEligibleForExport_ExperimentExportable() {

        DRLineExport exporter = getDrLineExporter();

        Experiment experiment = initExperimentA();

        Annotation annotation = new Annotation( institution, uniprotDrExport );
        annotation.setAnnotationText( "yes" );
        experiment.addAnnotation( annotation );

        Collection proteins = new ArrayList();
        proteins.add( protein1 );
        proteins.add( protein2 );
        proteins.add( protein3 );

        Set eligibleProteins = exporter.getProteinEligibleForExport( proteins );

        assertNotNull( eligibleProteins );
        assertEquals( 3, eligibleProteins.size() );
        assertTrue( eligibleProteins.contains( "PROT-1" ) );
        assertTrue( eligibleProteins.contains( "PROT-2" ) );
        assertTrue( eligibleProteins.contains( "PROT-3" ) );
    }

    public void testGetProteinEligibleForExport_ExperimentNotExportable() {

        DRLineExport exporter = getDrLineExporter();

        Experiment experiment = initExperimentA();

        Annotation annotation = new Annotation( institution, uniprotDrExport );
        annotation.setAnnotationText( "no" );
        experiment.addAnnotation( annotation );

        Collection proteins = new ArrayList();
        proteins.add( protein1 );
        proteins.add( protein2 );
        proteins.add( protein3 );

        Set eligibleProteins = exporter.getProteinEligibleForExport( proteins );

        assertNotNull( eligibleProteins );
        assertEquals( 0, eligibleProteins.size() );
    }

    public void testGetProteinEligibleForExport_ExperimentLargeScale() {

        DRLineExport exporter = getDrLineExporter();

        Experiment experiment = initExperimentA();

        Annotation annotation = new Annotation( institution, uniprotDrExport );
        annotation.setAnnotationText( "CORE-1" );
        experiment.addAnnotation( annotation );

        Annotation annotation2 = new Annotation( institution, uniprotDrExport );
        annotation2.setAnnotationText( "CORE-2" );
        experiment.addAnnotation( annotation2 );

        // remember that the comparison is case not sensitive.
        Annotation conf1 = new Annotation( institution, authorConfidence );
        conf1.setAnnotationText( "CoRE-1" );
        interaction2a.addAnnotation( conf1 );

        Annotation conf2 = new Annotation( institution, authorConfidence );
        conf2.setAnnotationText( "CORe-2" );
        interaction3a.addAnnotation( conf2 );

        Collection proteins = new ArrayList();
        proteins.add( protein1 );
        proteins.add( protein2 );
        proteins.add( protein3 );

        Set eligibleProteins = exporter.getProteinEligibleForExport( proteins );

        assertNotNull( eligibleProteins );
        assertEquals( 2, eligibleProteins.size() );
        assertTrue( eligibleProteins.contains( "PROT-2" ) );
        assertTrue( eligibleProteins.contains( "PROT-3" ) );
    }


    public void testGetProteinEligibleForExport_MethodExportable() {

        DRLineExport exporter = getDrLineExporter();

        // involve interaction 1a, 2a, 3a and protein 1 2 3.
        Experiment experiment = initExperimentA();
        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation = new Annotation( institution, uniprotDrExport );
        annotation.setAnnotationText( "yes" );
        cvInteraction.addAnnotation( annotation );

        experiment.setCvInteraction( cvInteraction );

        Collection proteins = new ArrayList();
        proteins.add( protein1 );
        proteins.add( protein2 );
        proteins.add( protein3 );

        Set eligibleProteins = exporter.getProteinEligibleForExport( proteins );

        assertNotNull( eligibleProteins );
        assertEquals( 3, eligibleProteins.size() );
        assertTrue( eligibleProteins.contains( "PROT-1" ) );
        assertTrue( eligibleProteins.contains( "PROT-2" ) );
        assertTrue( eligibleProteins.contains( "PROT-3" ) );
    }

    public void testGetProteinEligibleForExport_MethodDoNotExportable() {

        DRLineExport exporter = getDrLineExporter();

        // involve interaction 1a, 2a, 3a and protein 1 2 3.
        Experiment experiment = initExperimentA();
        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation = new Annotation( institution, uniprotDrExport );
        annotation.setAnnotationText( "no" );
        cvInteraction.addAnnotation( annotation );

        experiment.setCvInteraction( cvInteraction );

        Collection proteins = new ArrayList();
        proteins.add( protein1 );
        proteins.add( protein2 );
        proteins.add( protein3 );

        Set eligibleProteins = exporter.getProteinEligibleForExport( proteins );

        assertNotNull( eligibleProteins );
        assertEquals( 0, eligibleProteins.size() );
    }

    public void testGetProteinEligibleForExport_MethodConditional_DoNotExport() {

        DRLineExport exporter = getDrLineExporter();

        // involve interaction 1a, 2a, 3a and protein 1 2 3.
        Experiment experiment = initExperimentA();
        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation = new Annotation( institution, uniprotDrExport );
        annotation.setAnnotationText( "2" );
        cvInteraction.addAnnotation( annotation );

        experiment.setCvInteraction( cvInteraction );

        Collection proteins = new ArrayList();
        proteins.add( protein1 );
        proteins.add( protein2 );
        proteins.add( protein3 );

        Set eligibleProteins = exporter.getProteinEligibleForExport( proteins );

        assertNotNull( eligibleProteins );
        assertEquals( 0, eligibleProteins.size() );
    }

    public void testGetProteinEligibleForExport_MethodConditional_PartialExport() {

        DRLineExport exporter = getDrLineExporter();

        // involve interaction 1a, 2a, 3a and protein 1 2 3.
        Experiment experimentA = initExperimentA();
        Experiment experimentB = initExperimentB();

        CvInteraction cvInteraction = initCvInteraction();
        Annotation annotation = new Annotation( institution, uniprotDrExport );
        annotation.setAnnotationText( "2" );
        cvInteraction.addAnnotation( annotation );

        experimentA.setCvInteraction( cvInteraction );
        experimentB.setCvInteraction( cvInteraction );

        Collection proteins = new ArrayList();
        proteins.add( protein1 );
        proteins.add( protein2 );
        proteins.add( protein3 );
        proteins.add( protein4 );

        Set eligibleProteins = exporter.getProteinEligibleForExport( proteins );

        assertNotNull( eligibleProteins );
        assertEquals( 2, eligibleProteins.size() );
        assertTrue( eligibleProteins.contains( "PROT-1" ) );
        assertTrue( eligibleProteins.contains( "PROT-2" ) );
//        assertTrue( eligibleProteins.contains( "PROT-3" ) );
//        assertTrue( eligibleProteins.contains( "PROT-4" ) );
    }
}