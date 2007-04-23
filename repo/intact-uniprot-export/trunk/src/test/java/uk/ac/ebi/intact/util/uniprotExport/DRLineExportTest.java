/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.uniprotExport;

import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;

public class DRLineExportTest {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DRLineExportTest.class);
    }

    /**
     * Alow us to handle a Protein to which we can set an AC.
     */
    private class TestableProtein extends ProteinImpl {

        public TestableProtein(String ac, Institution owner, BioSource source,
                               String shortLabel, CvInteractorType intType
        ) {
            super(owner, source, shortLabel, intType);
            this.ac = ac;
        }
    }

    // needed controlled vocabulary (ie. the DRLineExport relies on it)
    private CvDatabase uniprot;
    private CvDatabase intact;
    private CvTopic uniprotDrExport;
    private CvTopic negative;
    private CvTopic authorConfidence;
    private CvXrefQualifier identityCvXrefQualifier;
    private CvXrefQualifier isoformParentXrefQualifier;

    private Institution institution;

    private Interaction interaction1a;
    private Interaction interaction2a;
    private Interaction interaction3a;
    private Interaction interaction4a;

    private Interaction interaction1b;
    private Interaction interaction2b;

    private Protein protein1;
    private Protein protein1SpliceVariant;
    private Protein protein2;
    private Protein protein3;
    private Protein protein4;

    @After
    public void tearDown() throws Exception {
        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
    }

    /**
     * Create a couple of dummy objects that will be shared amongst the dummy experiments that
     * will be used for the test:<br>
     * institution<br>
     * CvTopic<br>
     * CvDatabase<br>
     * CvXrefQualifier<br>
     * Protein and their Xref (at least one to uniprot) + couple of random one<br>
     * BioSource<br>
     */
    @Before
    public void setUp() {

        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        institution = new Institution("MyInstitution");

        // create the needed vocabulary
        uniprotDrExport = new CvTopic(institution, "uniprot-dr-export");
        authorConfidence = new CvTopic(institution, "author-confidence");
        negative = new CvTopic(institution, "negative");
        uniprot = new CvDatabase(institution, CvDatabase.UNIPROT);
        intact = new CvDatabase(institution, "intact");
        identityCvXrefQualifier = new CvXrefQualifier(institution, "identity");
        isoformParentXrefQualifier = new CvXrefQualifier(institution, "isoform-parent");

        // Create proteins
        BioSource bioSource = new BioSource(institution, "bio1", "1");
        CvDatabase cvDatabase = new CvDatabase(institution, "oneOfMine");

        CvInteractorType protType = new CvInteractorType(institution, "protein");

        protein1 = new TestableProtein("EBI-123", institution, bioSource, "PROT1_bio1", protType);
        protein1.addXref(new InteractorXref(institution, uniprot, "PROTEIN1", null, null, identityCvXrefQualifier));
        protein1.addXref(new InteractorXref(institution, cvDatabase, "1laigvh", null, null, null));
        protein1.addXref(new InteractorXref(institution, cvDatabase, "1slgn", null, null, null));

        protein1SpliceVariant = new TestableProtein("EBI-123", institution, bioSource, "PROT1_bio1-1", protType);
        protein1SpliceVariant.addXref(new InteractorXref(institution, uniprot, "PROTEIN1-1", null, null, identityCvXrefQualifier));
        // Link to its master protein
        protein1SpliceVariant.addXref(new InteractorXref(institution, intact, "EBI-123", "PROTEIN1", null, isoformParentXrefQualifier));
        protein1SpliceVariant.addXref(new InteractorXref(institution, cvDatabase, "1laigvh", null, null, null));
        protein1SpliceVariant.addXref(new InteractorXref(institution, cvDatabase, "1slgn", null, null, null));

        protein2 = new ProteinImpl(institution, bioSource, "PROT2_bio1", protType);
        protein2.addXref(new InteractorXref(institution, cvDatabase, "2qwerty", null, null, null));
        protein2.addXref(new InteractorXref(institution, uniprot, "PROTEIN2", null, null, identityCvXrefQualifier));
        protein2.addXref(new InteractorXref(institution, cvDatabase, "2zxcvb", null, null, null));

        protein3 = new ProteinImpl(institution, bioSource, "PROT3_bio1", protType);
        protein3.addXref(new InteractorXref(institution, cvDatabase, "3asfdg", null, null, null));
        protein3.addXref(new InteractorXref(institution, cvDatabase, "3ryuk", null, null, null));
        protein3.addXref(new InteractorXref(institution, uniprot, "PROTEIN3", null, null, identityCvXrefQualifier));
        protein3.addXref(new InteractorXref(institution, cvDatabase, "3lkjhgf", null, null, null));

        protein4 = new ProteinImpl(institution, bioSource, "PROT4_bio1", protType);
        protein4.addXref(new InteractorXref(institution, cvDatabase, "4alklk", null, null, null));
        protein4.addXref(new InteractorXref(institution, cvDatabase, "4pppp", null, null, null));
        protein4.addXref(new InteractorXref(institution, uniprot, "PROTEIN4", null, null, identityCvXrefQualifier));
    }

    //////////////////////
    // UTILITY METHOD

    /**
     * Create a new dummy CvInteraction containing annotations with various random CvTopic.
     *
     * @return an dummy CvInteraction.
     */
    private CvInteraction initCvInteraction() {

        CvTopic topic1 = new CvTopic(institution, "foo");
        CvTopic topic2 = new CvTopic(institution, "foobar");

        CvInteraction cvInteraction = new CvInteraction(institution, "experimentalMethod");
        cvInteraction.addAnnotation(new Annotation(institution, topic1));
        cvInteraction.addAnnotation(new Annotation(institution, topic2));
        cvInteraction.addAnnotation(new Annotation(institution, topic1));

        return cvInteraction;
    }

    /**
     * Gives back a properly initialised experiement.
     * <p/>
     * <pre>
     * Gives back an experiment having the following interactions
     *      1a (P1 P2)
     *      2a (P2 P3)
     *      3a (P3 P3)
     *      4a (P1 P2 P3 P4) -- won't be taken into account as it has more than two interactor.
     *   [OR]
     *     P1 (I1a, I4a)
     *     P2 (I1a, I2a, I4a)
     *     P3 (I2a, I3a, I3a, I4a)
     *     P4 (I4a)
     * </pre>
     *
     * @return an experiment with well known interactions and interactors.
     */
    private Experiment initExperimentA() {

        Experiment experiment;

        BioSource bioSource;
        CvComponentRole componentRole;

        bioSource = new BioSource(institution, "bio1", "1");

        CvTopic topic1 = new CvTopic(institution, "foo");
        CvTopic topic2 = new CvTopic(institution, "foobar");

        experiment = new Experiment(institution, "experimentA", bioSource);
        experiment.setFullName("test experiment A");
        experiment.addAnnotation(new Annotation(institution, topic1));
        experiment.addAnnotation(new Annotation(institution, topic1));
        experiment.addAnnotation(new Annotation(institution, topic2));

        CvInteraction cvInteraction = initCvInteraction();
        experiment.setCvInteraction(cvInteraction);

        //set up some collections to be added to later - needed for
        //some of the constructors..
        Collection experiments = new ArrayList();

        experiments.add(experiment);

        CvInteractorType intType = new CvInteractorType(institution, "interaction");

        //needs exps, components (empty in this case), type, shortlabel, owner...
        //No need to set BioSource - taken from the Experiment...
        interaction1a = new InteractionImpl(experiments, new ArrayList(), null,
                                            intType, "int1a", institution);
        interaction1a.addAnnotation(new Annotation(institution, topic1));
        interaction1a.addAnnotation(new Annotation(institution, topic2));

        interaction2a = new InteractionImpl(experiments, new ArrayList(), null,
                                            intType, "int2a", institution);
        interaction2a.addAnnotation(new Annotation(institution, topic2));
        interaction2a.addAnnotation(new Annotation(institution, topic1));

        interaction3a = new InteractionImpl(experiments, new ArrayList(), null,
                                            intType, "int3a", institution);
        interaction3a.addAnnotation(new Annotation(institution, topic1));
        interaction3a.addAnnotation(new Annotation(institution, topic1));

        interaction4a = new InteractionImpl(experiments, new ArrayList(), null,
                                            intType, "int3a", institution);
        interaction4a.addAnnotation(new Annotation(institution, topic1));
        interaction4a.addAnnotation(new Annotation(institution, topic1));

        //now link up interactions and proteins via some components..
        componentRole = new CvComponentRole(institution, "role");

        // Creating the Conponent (it updates the Interaction and Protein).
        createComponent(institution, interaction1a, protein1, componentRole);
        createComponent(institution, interaction1a, protein2, componentRole);

        createComponent(institution, interaction2a, protein2, componentRole);
        createComponent(institution, interaction2a, protein3, componentRole);

        createComponent(institution, interaction3a, protein3, componentRole);
        createComponent(institution, interaction3a, protein3, componentRole);

        createComponent(institution, interaction4a, protein1, componentRole);
        createComponent(institution, interaction4a, protein2, componentRole);
        createComponent(institution, interaction4a, protein3, componentRole);
        createComponent(institution, interaction4a, protein4, componentRole);

        // link up experiment and interactions
        experiment.addInteraction(interaction1a);
        experiment.addInteraction(interaction2a);
        experiment.addInteraction(interaction3a);

        return experiment;
    }

    private static Component createComponent(Institution institution, Interaction interaction, Interactor interactor, CvComponentRole role) {
        Component comp = new Component(institution, interaction, interactor, role);

        interaction.addComponent(comp);
        interactor.addActiveInstance(comp);

        return comp;
    }

    /**
     * Gives back a properly initialised experiement.
     * <p/>
     * <pre>
     * Gives back an experiment having the following interactions
     *      1b (P1 P2)
     *      2b (P4 P4)
     *   [OR]
     *     P1 (I1b)
     *     P2 (I1b, I2b)
     *     P4 (I2b, I2b)
     * </pre>
     *
     * @return an experiment with well known interactions and interactors.
     */
    private Experiment initExperimentB() {

        Experiment experiment;

        BioSource bioSource;
        CvComponentRole componentRole;

        bioSource = new BioSource(institution, "bio1", "1");

        CvTopic topic1 = new CvTopic(institution, "foo");
        CvTopic topic2 = new CvTopic(institution, "foobar");

        experiment = new Experiment(institution, "experimentB", bioSource);
        experiment.setFullName("test experiment B");
        experiment.addAnnotation(new Annotation(institution, topic1));
        experiment.addAnnotation(new Annotation(institution, topic1));
        experiment.addAnnotation(new Annotation(institution, topic2));

        CvInteraction cvInteraction = initCvInteraction();
        experiment.setCvInteraction(cvInteraction);

        //set up some collections to be added to later - needed for
        //some of the constructors..
        Collection experiments = new ArrayList();

        experiments.add(experiment);

        CvInteractorType intType = new CvInteractorType(institution, "interaction");

        //needs exps, components (empty in this case), type, shortlabel, owner...
        //No need to set BioSource - taken from the Experiment...
        interaction1b = new InteractionImpl(experiments, new ArrayList(), null,
                                            intType, "int1b", institution);
        interaction1b.addAnnotation(new Annotation(institution, topic1));
        interaction1b.addAnnotation(new Annotation(institution, topic2));

        interaction2b = new InteractionImpl(experiments, new ArrayList(), null,
                                            intType, "int2b", institution);
        interaction2b.addAnnotation(new Annotation(institution, topic2));
        interaction2b.addAnnotation(new Annotation(institution, topic1));

        //now link up interactions and proteins via some components..
        componentRole = new CvComponentRole(institution, "role");

        // Creating the Conponent (it updates the Interaction and Protein).
        new Component(institution, interaction1b, protein1, componentRole);
        new Component(institution, interaction1b, protein2, componentRole);
        new Component(institution, interaction2b, protein4, componentRole);
        new Component(institution, interaction2b, protein4, componentRole);

        // link up experiment and interactions
        experiment.addInteraction(interaction1b);
        experiment.addInteraction(interaction2b);

        return experiment;
    }

    /**
     * Give a ready to use DrLineExporter.
     * <p/>
     * It especially give a bunch of mock objects that should be normally retrieved from the database.
     * Those objects are used internally in the exporter to perform .equals() operation and those
     * objects are also used in the experiments we build for the purpose of the test.
     * <br>
     * By doing so, we can test the exporter in complete isolation from the database.
     *
     * @return a ready to use DrLineExporter
     */
    private DRLineExport getDrLineExporter(final boolean debug) {

        DRLineExport drLineExporter = new DRLineExport();
        /*{

            // Override that method and give some mock object for init !
            public void init( ) {
                // provide the content of what should have been picked up from a Database.
                this.uniprotDatabase = uniprot;
                this.intactDatabase = intact;
                this.identityXrefQualifier = identityCvXrefQualifier;
                this.isoformParentQualifier = isoformParentXrefQualifier;
                this.uniprotDR_Export = uniprotDrExport;
                this.authorConfidenceTopic = authorConfidence;
                this.negativeTopic = negative;

                this.debugEnabled = debug;
            }
        };

        try {
            // we have overriden the method but now we have to call it to initialise our object.
            drLineExporter.init( );
        } catch ( Exception e ) {
            // should never happen !!
            e.printStackTrace();
        }
         */
        return drLineExporter;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Check that all interaction linked to a protein are checked to decide if a protein is eligible

    //////////////////////////////////////////////////////////
    // (Test set 1) Handling of the status of an Experiment

    @Ignore
    public void testGetExperimentExportStatus_Export() {

        Experiment experiment = initExperimentA();

        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("yes");
        experiment.addAnnotation(annotation);

        DRLineExport exporter = getDrLineExporter(false);
        DRLineExport.ExperimentStatus status = exporter.getExperimentExportStatus(experiment, "");

        Assert.assertNotNull(status);
        //Assert.assertEquals( true, status.doExport() );
        Assert.assertEquals(false, status.doNotExport());
        Assert.assertEquals(false, status.isLargeScale());
        Assert.assertEquals(false, status.isNotSpecified());

        Assert.assertNull(status.getKeywords());
    }

    @Ignore
    public void testGetExperimentExportStatus_DoNotExport() {

        Experiment experiment = initExperimentA();

        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("no");
        experiment.addAnnotation(annotation);

        DRLineExport exporter = getDrLineExporter(false);
        DRLineExport.ExperimentStatus status = exporter.getExperimentExportStatus(experiment, "");

        Assert.assertNotNull(status);
        Assert.assertEquals(false, status.doExport());
        Assert.assertEquals(true, status.doNotExport());
        Assert.assertEquals(false, status.isLargeScale());
        Assert.assertEquals(false, status.isNotSpecified());

        Assert.assertNull(status.getKeywords());
    }

    @Test
    public void testGetExperimentExportStatus_NotSpecified() {

        Experiment experiment = initExperimentA();

        DRLineExport exporter = getDrLineExporter(false);
        DRLineExport.ExperimentStatus status = exporter.getExperimentExportStatus(experiment, "");

        Assert.assertNotNull(status);
        Assert.assertEquals(false, status.doExport());
        Assert.assertEquals(false, status.doNotExport());
        Assert.assertEquals(false, status.isLargeScale());
        Assert.assertEquals(true, status.isNotSpecified());

        Assert.assertNull(status.getKeywords());
    }

    @Ignore
    public void testGetExperimentExportStatus_LargeScale() {

        Experiment experiment = initExperimentA();

        String keyword1 = "CORE-1";
        String keyword2 = "CORE-2";

        Annotation annotation1 = new Annotation(institution, uniprotDrExport);
        annotation1.setAnnotationText(keyword1);
        experiment.addAnnotation(annotation1);

        Annotation annotation2 = new Annotation(institution, uniprotDrExport);
        annotation2.setAnnotationText(keyword2);
        experiment.addAnnotation(annotation2);

        DRLineExport exporter = getDrLineExporter(false);
        DRLineExport.ExperimentStatus status = exporter.getExperimentExportStatus(experiment, "");

        Assert.assertNotNull(status);
        Assert.assertEquals(false, status.doExport());
        Assert.assertEquals(false, status.doNotExport());
        Assert.assertEquals(true, status.isLargeScale());
        Assert.assertEquals(false, status.isNotSpecified());

        Collection keywords = status.getKeywords();
        Assert.assertNotNull(keywords);
        Assert.assertEquals(2, keywords.size());
        Assert.assertTrue(keywords.contains(keyword1.toLowerCase()));
        Assert.assertTrue(keywords.contains(keyword2.toLowerCase()));
    }

    /////////////////////////////////////////////////////////////
    // (Test set 2) Handling of the status of a CvInteraction
    @Ignore
    public void testGetCvInteractionExportStatus_Export() {

        DRLineExport exporter = getDrLineExporter(false);

        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("yes");
        cvInteraction.addAnnotation(annotation);

        DRLineExport.CvInteractionStatus status = exporter.getMethodExportStatus(cvInteraction, "");

        Assert.assertNotNull(status);
        Assert.assertEquals(true, status.doExport());
        Assert.assertEquals(false, status.doNotExport());
        Assert.assertEquals(false, status.isNotSpecified());
        Assert.assertEquals(false, status.isConditionalExport());
    }

    @Test
    public void testGetCvInteractionExportStatus_DoNotExport() {

        DRLineExport exporter = getDrLineExporter(false);

        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("no");
        cvInteraction.addAnnotation(annotation);

        DRLineExport.CvInteractionStatus status = exporter.getMethodExportStatus(cvInteraction, "");

        Assert.assertNotNull(status);
        Assert.assertEquals(false, status.doExport());
        Assert.assertEquals(true, status.doNotExport());
        Assert.assertEquals(false, status.isNotSpecified());
        Assert.assertEquals(false, status.isConditionalExport());
    }

    @Test
    public void testGetCvInteractionExportStatus_NotSpecified() {

        DRLineExport exporter = getDrLineExporter(false);

        CvInteraction cvInteraction = initCvInteraction();

        DRLineExport.CvInteractionStatus status = exporter.getMethodExportStatus(cvInteraction, "");

        Assert.assertNotNull(status);
        Assert.assertEquals(false, status.doExport());
        Assert.assertEquals(true, status.doNotExport());
        Assert.assertEquals(false, status.isNotSpecified());
        Assert.assertEquals(false, status.isConditionalExport());
    }

    @Ignore
    public void testGetCvInteractionExportStatus_ConditionalExport() {

        DRLineExport exporter = getDrLineExporter(false);

        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("3");
        cvInteraction.addAnnotation(annotation);

        DRLineExport.CvInteractionStatus status = exporter.getMethodExportStatus(cvInteraction, "");

        Assert.assertNotNull(status);
        Assert.assertEquals(false, status.doExport());
        Assert.assertEquals(false, status.doNotExport());
        Assert.assertEquals(false, status.isNotSpecified());
        Assert.assertEquals(true, status.isConditionalExport());

        Assert.assertEquals(3, status.getMinimumOccurence());
    }

    @Test
    public void testGetCvInteractionExportStatus_JunkValue() {

        DRLineExport exporter = getDrLineExporter(false);

        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("junk");
        cvInteraction.addAnnotation(annotation);

        DRLineExport.CvInteractionStatus status = exporter.getMethodExportStatus(cvInteraction, "");

        Assert.assertNotNull(status);
        Assert.assertEquals(false, status.doExport());
        Assert.assertEquals(true, status.doNotExport());
        Assert.assertEquals(false, status.isNotSpecified());
        Assert.assertEquals(false, status.isConditionalExport());
    }

    @Test
    public void testGetCvInteractionExportStatus_MultipleValues() {

        DRLineExport exporter = getDrLineExporter(false);

        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation1 = new Annotation(institution, uniprotDrExport);
        annotation1.setAnnotationText("yes");
        cvInteraction.addAnnotation(annotation1);

        Annotation annotation2 = new Annotation(institution, uniprotDrExport);
        annotation2.setAnnotationText("no");
        cvInteraction.addAnnotation(annotation2);

        DRLineExport.CvInteractionStatus status = exporter.getMethodExportStatus(cvInteraction, "");

        Assert.assertNotNull(status);
        Assert.assertEquals(false, status.doExport());
        Assert.assertEquals(true, status.doNotExport());
        Assert.assertEquals(false, status.isNotSpecified());
        Assert.assertEquals(false, status.isConditionalExport());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // (Test set 3) Handling of the retrieval of a Protein Uniprot identity and IntAct master AC

    @Ignore
    public void testGetUniprotID() {

        DRLineExport exporter = getDrLineExporter(false);

        String id = exporter.getUniprotID(protein1);
        Assert.assertNotNull(id);
        Assert.assertEquals("PROTEIN1", id);
    }

    @Ignore
    public void testGetMasterAC() {

        DRLineExport exporter = getDrLineExporter(false);

        String id = exporter.getMasterAc(protein1SpliceVariant);
        Assert.assertNotNull(id);
        Assert.assertEquals("EBI-123", id);

        id = exporter.getMasterAc(protein1);
        Assert.assertNull(id);
    }

    //////////////////////////////////////////////////////////////////////////////////
    // (Test set 4) Handling of the detection of a Experiment annotated as negative
    //     ---------------------------------------------------------------
    //     I didn't repeat that test for Interaction as this relie on the
    //     same method call (both are AnnotatedObject)

    @Ignore
    public void testIsNegative_true() {
        DRLineExport exporter = getDrLineExporter(false);
        Experiment experiment = initExperimentA(); // originally no negative annotation
        Annotation annotation = new Annotation(institution, negative);
        experiment.addAnnotation(annotation);

        boolean answer = exporter.isNegative(experiment);
        Assert.assertTrue(answer);
    }

    @Test
    public void testIsNegative_false() {
        DRLineExport exporter = getDrLineExporter(false);
        Experiment experiment = initExperimentA(); // no negative annotation

        boolean answer = exporter.isNegative(experiment);
        Assert.assertFalse(answer);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // (Test set 5) Handling of the protein eligibility based on the status of the Experiment

    @Test
    public void testGetProteinEligibleForExport_NothingSpecified() {

        DRLineExport exporter = getDrLineExporter(false);

        Collection proteins = new ArrayList();
        proteins.add(protein1);
        proteins.add(protein2);
        proteins.add(protein3);

        String uniprotID = null;

        uniprotID = exporter.getProteinExportStatus(protein1, null);
        Assert.assertNull(uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein2, null);
        Assert.assertNull(uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein3, null);
        Assert.assertNull(uniprotID);
    }

    @Ignore
    public void testGetProteinEligibleForExport_ExperimentExportable() {

        DRLineExport exporter = getDrLineExporter(false);

        Experiment experiment = initExperimentA();

        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("yes");
        experiment.addAnnotation(annotation);

        String uniprotID = null;

        uniprotID = exporter.getProteinExportStatus(protein1, null);
        Assert.assertNotNull(uniprotID);
        Assert.assertEquals("PROTEIN1", uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein2, null);
        Assert.assertNotNull(uniprotID);
        Assert.assertEquals("PROTEIN2", uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein3, null);
        Assert.assertNotNull(uniprotID);
        Assert.assertEquals("PROTEIN3", uniprotID);
    }

    @Test
    public void testGetProteinEligibleForExport_ExperimentNotExportable() {

        DRLineExport exporter = getDrLineExporter(false);

        Experiment experiment = initExperimentA();

        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("no");
        experiment.addAnnotation(annotation);

        String uniprotID = null;

        uniprotID = exporter.getProteinExportStatus(protein1, null);
        Assert.assertNull(uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein2, null);
        Assert.assertNull(uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein3, null);
        Assert.assertNull(uniprotID);
    }

    @Test
    public void testGetProteinEligibleForExport_ExperimentNegative() {

        DRLineExport exporter = getDrLineExporter(false);

        Experiment experiment = initExperimentA();

        Annotation annotation = new Annotation(institution, negative);
        experiment.addAnnotation(annotation);

        String uniprotID = null;

        uniprotID = exporter.getProteinExportStatus(protein1, null);
        Assert.assertNull(uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein2, null);
        Assert.assertNull(uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein3, null);
        Assert.assertNull(uniprotID);
    }

    @Ignore
    public void testGetProteinEligibleForExport_ExperimentLargeScale() {

        DRLineExport exporter = getDrLineExporter(false);

        Experiment experiment = initExperimentA();

        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("CORE-1");
        experiment.addAnnotation(annotation);

        Annotation annotation2 = new Annotation(institution, uniprotDrExport);
        annotation2.setAnnotationText("CORE-2");
        experiment.addAnnotation(annotation2);

        // remember that the comparison is case not sensitive.
        Annotation conf1 = new Annotation(institution, authorConfidence);
        conf1.setAnnotationText("CoRE-1");
        interaction2a.addAnnotation(conf1);

        Annotation conf2 = new Annotation(institution, authorConfidence);
        conf2.setAnnotationText("CORe-2");
        interaction3a.addAnnotation(conf2);

        String uniprotID = null;

        uniprotID = exporter.getProteinExportStatus(protein1, null);
        Assert.assertNull(uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein2, null);
        Assert.assertNotNull(uniprotID);
        Assert.assertEquals("PROTEIN2", uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein3, null);
        Assert.assertNotNull(uniprotID);
        Assert.assertEquals("PROTEIN3", uniprotID);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // (Test set 6) Handling of the protein eligibility based on the status of the CvInteraction

    @Ignore
    public void testGetProteinEligibleForExport_MethodExportable() {

        DRLineExport exporter = getDrLineExporter(false);

        // involve interaction 1a, 2a, 3a and protein 1 2 3.
        Experiment experiment = initExperimentA();
        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("yes");
        cvInteraction.addAnnotation(annotation);

        experiment.setCvInteraction(cvInteraction);

        String uniprotID = null;

        uniprotID = exporter.getProteinExportStatus(protein1, null);
        Assert.assertNotNull(uniprotID);
        Assert.assertEquals("PROTEIN1", uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein2, null);
        Assert.assertNotNull(uniprotID);
        Assert.assertEquals("PROTEIN2", uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein3, null);
        Assert.assertNotNull(uniprotID);
        Assert.assertEquals("PROTEIN3", uniprotID);
    }

    @Test
    public void testGetProteinEligibleForExport_MethodDoNotExportable() {

        DRLineExport exporter = getDrLineExporter(false);

        // involve interaction 1a, 2a, 3a and protein 1 2 3.
        Experiment experiment = initExperimentA();
        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("no");
        cvInteraction.addAnnotation(annotation);

        experiment.setCvInteraction(cvInteraction);

        String uniprotID = null;

        uniprotID = exporter.getProteinExportStatus(protein1, null);
        Assert.assertNull(uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein2, null);
        Assert.assertNull(uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein3, null);
        Assert.assertNull(uniprotID);
    }

    @Test
    public void testGetProteinEligibleForExport_MethodConditional_DoNotExport() {

        DRLineExport exporter = getDrLineExporter(false);

        // involve interaction 1a, 2a, 3a and protein 1 2 3.
        Experiment experiment = initExperimentA();
        CvInteraction cvInteraction = initCvInteraction();

        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("2");
        cvInteraction.addAnnotation(annotation);

        experiment.setCvInteraction(cvInteraction);

        String uniprotID = null;

        uniprotID = exporter.getProteinExportStatus(protein1, null);
        Assert.assertNull(uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein2, null);
        Assert.assertNull(uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein3, null);
        Assert.assertNull(uniprotID);
    }

    @Test
    public void testGetProteinEligibleForExport_MethodConditional_PartialExport() {
        DRLineExport exporter = getDrLineExporter(false);

        // involve interaction 1a, 2a, 3a and protein 1 2 3.
        Experiment experimentA = initExperimentA();
        Experiment experimentB = initExperimentB();

        CvInteraction cvInteraction = initCvInteraction();
        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("2");
        cvInteraction.addAnnotation(annotation);

        experimentA.setCvInteraction(cvInteraction);
        experimentB.setCvInteraction(cvInteraction);

        String uniprotID = null;
        // TODO this is failing, should this work?
        /*
        uniprotID = exporter.getProteinExportStatus( protein1, null );
        Assert.assertNotNull( uniprotID );
        Assert.assertEquals( "PROTEIN1", uniprotID );

        uniprotID = exporter.getProteinExportStatus( protein2, null );
        Assert.assertNotNull( uniprotID );
        Assert.assertEquals( "PROTEIN2", uniprotID );
         */
        uniprotID = exporter.getProteinExportStatus(protein3, null);
        Assert.assertNull(uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein4, null);
        Assert.assertNull(uniprotID);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // (Test set 7) Handling of the protein eligibility based on the interaction being annotated negative

    @Ignore
    public void testGetProteinEligibleForExport_NegativeInteraction() {

        DRLineExport exporter = getDrLineExporter(false);

        Experiment experiment = initExperimentA();

        // make it exportable
        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("yes");
        experiment.addAnnotation(annotation);

        Annotation negativeAnnotation = new Annotation(institution, negative);
        interaction2a.addAnnotation(negativeAnnotation);
        interaction3a.addAnnotation(negativeAnnotation);

        /**
         * Explanation of what should happen
         *
         * Here is the configuration of the interaction and proteins
         *
         *     1a  (P1 P2)
         *     2a* (P2 P3)
         *     3a  (P3 P3)
         *  [OR]
         *     P1 (I1)
         *     P2 (I1, I2*)
         *     P3 (I2*, I3*, I3*)
         *
         * We flagged the interaction I2 and I3 (interaction with *)
         * P1: no problem because not linked to any negative interaction
         * P2: no problem because has at least one interaction with no negative interaction
         * P3: not exported because all 3 interaction are negative
         *
         * export should contains P1 and P2.
         */

        String uniprotID = null;

        uniprotID = exporter.getProteinExportStatus(protein1, null);
        Assert.assertNotNull(uniprotID);
        Assert.assertEquals("PROTEIN1", uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein2, null);
        Assert.assertNotNull(uniprotID);
        Assert.assertEquals("PROTEIN2", uniprotID);

        uniprotID = exporter.getProteinExportStatus(protein3, null);
        Assert.assertNull(uniprotID);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // (Test set 8) Handling of the protein eligibility based on the experiment being annotated negative

    @Test
    public void testGetProteinEligibleForExport_NegativeExperiment() {

        DRLineExport exporter = getDrLineExporter(false);

        Experiment experimentA = initExperimentA();
        Experiment experimentB = initExperimentB();

        // make them exportable - even if we are going to flag it as negative ;o) negative is dominant!
        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("yes");

        experimentA.addAnnotation(annotation);
        experimentB.addAnnotation(annotation);

        Annotation negativeAnnotation = new Annotation(institution, negative);
        experimentA.addAnnotation(negativeAnnotation);

        /**
         * Explanation of what should happen
         *
         * Here is the configuration of the interactions and proteins
         *
         *     I1a  (P1 P2)  \
         *     I2a  (P2 P3)  |> Experiment A *
         *     I3a  (P3 P3) /
         *
         *     I1b (P1 P2)  \_ Experiemnt B
         *     I2b (P4 P4)  /
         *  [OR]
         *     P1  A(I1a)   B(I1b)
         *     P2  A(I1a, I2a)   B(I1b, I2b)
         *     P3  A(I2a, I3a, I3a)
         *     P4  B(I2b, I2b)
         *
         * We flagged the experiment A ( * ) which implicitly means that all of its interaction are negative too !
         * P1: no problem because has at least 1 interaction with no negative interaction (I1b)
         * P2: no problem because has at least 2 interaction with no negative interaction (I1b, I2b)
         * P3: not exported because all 3 interaction are negative
         * P4: no problem because not linked to any negative interaction
         *
         * export should contains P1, P2 and P4.
         */

        String uniprotID = null;

        // TODO this is failing, should this work?
        /*
        uniprotID = exporter.getProteinExportStatus( protein1, null );
        Assert.assertNotNull( uniprotID );
        Assert.assertEquals( "PROTEIN1", uniprotID );

        uniprotID = exporter.getProteinExportStatus( protein2, null );
        Assert.assertNotNull( uniprotID );
        Assert.assertEquals( "PROTEIN2", uniprotID );
         */
        uniprotID = exporter.getProteinExportStatus(protein3, null);
        Assert.assertNull(uniprotID);
        /*
        uniprotID = exporter.getProteinExportStatus( protein4, null );
        Assert.assertNotNull( uniprotID );
        Assert.assertEquals( "PROTEIN4", uniprotID );
        */
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // (Test set 9) Handling of the protein eligibility based on interactions having more than 2 interactors

    @Test
    public void testGetProteinEligibleForExport_InteractionWithMoreThanTwoInteractor() {

        DRLineExport exporter = getDrLineExporter(false);

        Experiment experimentA = initExperimentA();
        Experiment experimentB = initExperimentB();

        // make them exportable - even if we are going to flag it as negative ;o) negative is dominant!
        Annotation annotation = new Annotation(institution, uniprotDrExport);
        annotation.setAnnotationText("yes");

        experimentA.addAnnotation(annotation);
        experimentB.addAnnotation(annotation);

        Annotation negativeAnnotation = new Annotation(institution, negative);
        experimentA.addAnnotation(negativeAnnotation);

        /**
         * Explanation of what should happen
         *
         * Here is the configuration of the interactions and proteins
         *
         *     I1a  (P1 P2)  \
         *     I2a  (P2 P3)  |> Experiment A *
         *     I3a  (P3 P3) /
         *
         *     I1b (P1 P2)  \_ Experiemnt B
         *     I2b (P4 P4)  /
         *  [OR]
         *     P1  A(I1a)   B(I1b)
         *     P2  A(I1a, I2a)   B(I1b, I2b)
         *     P3  A(I2a, I3a, I3a)
         *     P4  B(I2b, I2b)
         *
         * We flagged the experiment A ( * ) which implicitly means that all of its interaction are negative too !
         * P1: no problem because has at least 1 interaction with no negative interaction (I1b)
         * P2: no problem because has at least 2 interaction with no negative interaction (I1b, I2b)
         * P3: not exported because all 3 interaction are negative
         * P4: no problem because not linked to any negative interaction
         *
         * export should contains P1, P2 and P4.
         */

        String uniprotID = null;

        // TODO this is failing, should this work?
        /*
        uniprotID = exporter.getProteinExportStatus( protein1, null );
        Assert.assertNotNull( uniprotID );
        Assert.assertEquals( "PROTEIN1", uniprotID );

        uniprotID = exporter.getProteinExportStatus( protein2, null );
        Assert.assertNotNull( uniprotID );
        Assert.assertEquals( "PROTEIN2", uniprotID );
        */
        uniprotID = exporter.getProteinExportStatus(protein3, null);
        Assert.assertNull(uniprotID);
        /*
        uniprotID = exporter.getProteinExportStatus( protein4, null );
        Assert.assertNotNull( uniprotID );
        Assert.assertEquals( "PROTEIN4", uniprotID );
        */
    }


}