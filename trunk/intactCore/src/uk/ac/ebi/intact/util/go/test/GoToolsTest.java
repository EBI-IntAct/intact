/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.go.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.util.Collection;
import java.util.Iterator;

/**
 * Test class for GoTools
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class GoToolsTest extends TestCase {

    private IntactHelper myHelper;

    /**
     * Constructs an instance with the specified name.
     *
     * @param name the name of the test.
     */
    public GoToolsTest(String name) {
        super(name);
    }

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws Exception {
        // Write setting up code for each test.
        super.setUp();
        myHelper = new IntactHelper();
    }

    /**
     * Tears down the test fixture. Called after every test case method.
     */
    protected void tearDown() throws Exception {
        // Release resources for after running a test.
        super.tearDown();
        myHelper.closeStore();
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(GoToolsTest.class);
    }

    public void testInsertCvTopicDef() {
        try {
            doTestCvTopicDef();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testInsertCvDatabaseDef() {
        try {
            doTestCvDatabaseDef();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testInsertCvInteractionDef() {
        try {
            doTestCvInteractionDef();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testInsertCvInteractionDag() {
        try {
            doTestCvInteractionDag();
        }
        catch (IntactException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testInsertCvInteractionTypeDef() {
        try {
            doTestCvInteractionTypeDef();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testInsertCvInteractionTypeDag() {
        try {
            doTestCvInteractionTypeDag();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testInsertCvFeatureTypeDef() {
        try {
            doTestCvFeatureTypeDef();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testInsertCvFeatureTypeDag() {
        try {
            doTestCvFeatureTypeDag();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testInsertCvFeatureIdentificationDef() {
        try {
            doTestCvFeatureIdentificationDef();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testInsertCvFeatureIdentificationDag() {
        try {
            doTestCvFeatureIdentificationDag();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    private void doTestCvTopicDef() throws IntactException {
        CvTopic topic = (CvTopic) myHelper.getObjectByLabel(CvTopic.class,
                "definition");
        assertNotNull(topic);
        assertEquals(topic.getFullName(), "Definition of the controlled vocabulary term");
        // No annotations or xrefs.
        assertTrue(topic.getAnnotations().isEmpty());
        assertTrue(topic.getXrefs().isEmpty());

        // Cache the definition.
        CvTopic definition = topic;

        topic = (CvTopic) myHelper.getObjectByLabel(CvTopic.class, "url");
        assertNotNull(topic);
        assertEquals(topic.getFullName(), "URL/Web address");
        // One annotation.
        assertEquals(topic.getAnnotations().size(), 1);
        assertTrue(containsTopic(topic, definition));

        topic = (CvTopic) myHelper.getObjectByLabel(CvTopic.class, "uniprot-dr-export");
        assertNotNull(topic);
        assertEquals(topic.getFullName(),
                "Determines if the experiment is to be exported to UniProt DR lines.");
        // Three annotations.
        assertEquals(topic.getAnnotations().size(), 3);
        // One is an definition.
        assertTrue(containsTopic(topic, definition));

        // There are two comments.
        assertEquals(countsTopic(topic, "comment"), 2);
    }

    private void doTestCvDatabaseDef() throws IntactException {
        CvDatabase database = (CvDatabase) myHelper.getObjectByLabel(
                CvDatabase.class, "uniprot");
        assertNotNull(database);
        assertEquals(database.getFullName(), "UniProt protein sequence database");
        // No xrefs.
        assertTrue(database.getXrefs().isEmpty());
        // Four annotations.
        assertEquals(database.getAnnotations().size(), 4);
        assertTrue(containsTopic(database, "search-url"));
        assertTrue(containsTopic(database, "search-url-ascii"));
        assertTrue(containsTopic(database, "url"));
        assertTrue(containsTopic(database, "definition"));

        database = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class, "go");
        assertNotNull(database);
        assertEquals(database.getFullName(), "Gene Ontology");
        // No xrefs.
        assertTrue(database.getXrefs().isEmpty());
        // Three annotations.
        assertEquals(database.getAnnotations().size(), 3);
        assertTrue(containsTopic(database, "search-url"));
        assertTrue(containsTopic(database, "url"));
        assertTrue(containsTopic(database, "definition"));

        database = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class, "psi-mi");
        assertNotNull(database);
        assertEquals(database.getFullName(),
                "Proteomics Standards Initiative - Molecular Interaction XML format CVs");
        // No xrefs.
        assertTrue(database.getXrefs().isEmpty());
        // Three annotations.
        assertEquals(database.getAnnotations().size(), 3);
        // Two url topics.
        assertEquals(countsTopic(database, "url"), 2);
        assertTrue(containsTopic(database, "definition"));
    }

    private void doTestCvInteractionDef() throws IntactException {
        // Cache topics
        CvTopic definition = (CvTopic) myHelper.getObjectByLabel(CvTopic.class,
                "definition");
        CvTopic uniprot = (CvTopic) myHelper.getObjectByLabel(CvTopic.class,
                "uniprot-dr-export");

        // Temp objects.
        String shortlabel;
        AnnotatedObject annobj;

        shortlabel = "affinity chromatogra";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteraction.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "affinity chromatography technologies");
        // There are two annotations.
        assertEquals(annobj.getAnnotations().size(), 2);
        // Must contain topics definition and uniprot.
        assertTrue(containsTopic(annobj, uniprot));
        assertTrue(containsTopic(annobj, definition));
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(annobj, "7708014"));
        assertTrue(containsPrimaryId(annobj, "MI:0004"));

        shortlabel = "anti tag coimmunopre";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteraction.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "anti tag coimmunoprecipitation");
        // There are two annotations.
        assertEquals(annobj.getAnnotations().size(), 2);
        // Must contain topics definition and uniprot.
        assertTrue(containsTopic(annobj, uniprot));
        assertTrue(containsTopic(annobj, definition));
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(annobj, "7708014"));
        assertTrue(containsPrimaryId(annobj, "MI:0007"));

        shortlabel = "beta galactosidase c";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteraction.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "beta galactosidase complementation");
        // There are two annotations.
        assertEquals(annobj.getAnnotations().size(), 2);
        // Must contain topics definition and uniprot.
        assertTrue(containsTopic(annobj, uniprot));
        assertTrue(containsTopic(annobj, definition));
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(annobj, "9237989"));
        assertTrue(containsPrimaryId(annobj, "12042868"));
        assertTrue(containsPrimaryId(annobj, "MI:0010"));

        shortlabel = "colocalization/visua";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteraction.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "colocalization/visualisation technologies");
        // There are two annotations.
        assertEquals(annobj.getAnnotations().size(), 2);
        // Must contain topics definition and uniprot.
        assertTrue(containsTopic(annobj, uniprot));
        assertTrue(containsTopic(annobj, definition));
        // go id
        assertTrue(containsPrimaryId(annobj, "MI:0023"));

        shortlabel = "phage display";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteraction.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "phage display");
        // There are two annotations.
        assertEquals(annobj.getAnnotations().size(), 2);
        // Must contain topics definition and uniprot.
        assertTrue(containsTopic(annobj, uniprot));
        assertTrue(containsTopic(annobj, definition));
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(annobj, "7708014"));
        assertTrue(containsPrimaryId(annobj, "10975452"));
        assertTrue(containsPrimaryId(annobj, "MI:0084"));
    }

    private void doTestCvInteractionDag() throws IntactException {
        // Check the database contents
        CvInteraction cvinter = (CvInteraction) myHelper.getObjectByLabel(CvInteraction.class,
                "interaction detectio");
        // Has two children
        assertEquals(cvinter.getChildren().size(), 2);
        assertTrue(hasChild(cvinter, "experimental"));
        assertTrue(hasChild(cvinter, "in silico"));
        // No parents.
        assertTrue(cvinter.getParents().isEmpty());

        cvinter = (CvInteraction) myHelper.getObjectByLabel(CvInteraction.class,
                "biophysical");
        // Has 11 children
        assertEquals(cvinter.getChildren().size(), 11);
        assertTrue(hasChild(cvinter, "circular dichroism"));
        assertTrue(hasChild(cvinter, "electron microscopy"));
        assertTrue(hasChild(cvinter, "electron resonance"));
        // Has one parent.
        assertEquals(cvinter.getParents().size(), 1);
        assertTrue(hasParent(cvinter, "experimental"));

        cvinter = (CvInteraction) myHelper.getObjectByLabel(CvInteraction.class,
                "bioluminescence reso");
        // Has no children
        assertTrue(cvinter.getChildren().isEmpty());
        // Has two parents.
        assertEquals(cvinter.getParents().size(), 2);
        assertTrue(hasParent(cvinter, "fluorescence technol"));
        assertTrue(hasParent(cvinter, "colocalization/visua"));
    }

    private void doTestCvInteractionTypeDef() throws IntactException {
        // Cache topics
        CvTopic definition = (CvTopic) myHelper.getObjectByLabel(CvTopic.class,
                "definition");

        // Temp objects.
        String shortlabel;
        AnnotatedObject annobj;

        shortlabel = "aggregation";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "aggregation");
        // There are no annotations.
        assertTrue(annobj.getAnnotations().isEmpty());
        // no xrefs.
        assertTrue(annobj.getXrefs().isEmpty());

        shortlabel = "dephosphorylation re";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "dephosphorylation reaction");
        // There is one annotation.
        assertEquals(annobj.getAnnotations().size(), 1);
        // Contains topics definition.
        assertTrue(containsTopic(annobj, definition));
        // no xrefs.
        assertTrue(annobj.getXrefs().isEmpty());

        shortlabel = "ubiquitination react";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "ubiquitination reaction");
        // There is one annotation.
        assertEquals(annobj.getAnnotations().size(), 1);
        // Contains topics definition.
        assertTrue(containsTopic(annobj, definition));
        // no xrefs.
        assertTrue(annobj.getXrefs().isEmpty());
    }

    private void doTestCvInteractionTypeDag() throws IntactException {
        // Check the database contents
        CvInteractionType cvintertype = (CvInteractionType) myHelper.getObjectByLabel(
                CvInteractionType.class, "aggregation");
        // Has no children.
        assertTrue(cvintertype.getChildren().isEmpty());
        // Has one parent
        assertEquals(cvintertype.getParents().size(), 1);
        assertTrue(hasParent(cvintertype, "non covalent interac"));

        cvintertype = (CvInteractionType) myHelper.getObjectByLabel(
                CvInteractionType.class, "cleavage");
        // Has 5 children
        assertEquals(cvintertype.getChildren().size(), 5);
        assertTrue(hasChild(cvintertype, "deacetylation reacti"));
        assertTrue(hasChild(cvintertype, "deformylation reacti"));
        assertTrue(hasChild(cvintertype, "dephosphorylation re"));
        // Has one parent.
        assertTrue(hasParent(cvintertype, "covalent interaction"));

        cvintertype = (CvInteractionType) myHelper.getObjectByLabel(
                CvInteractionType.class, "physical interaction");
        // Has two children.
        assertEquals(cvintertype.getChildren().size(), 2);
        assertTrue(hasChild(cvintertype, "covalent interaction"));
        assertTrue(hasChild(cvintertype, "non covalent interac"));
        // Has one parent.
        assertTrue(hasParent(cvintertype, "interaction type"));
    }

    private void doTestCvFeatureTypeDef() throws IntactException {
        // Cache cvtopics.
        CvTopic definition = (CvTopic) myHelper.getObjectByLabel(CvTopic.class,
                "definition");

        String shortlabel = "3-methylthio-asparti";
        CvFeatureType cvfeature = (CvFeatureType) myHelper.getObjectByLabel(
                CvFeatureType.class, shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "L-beta-methylthioaspartic acid");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // Two xrefs.
        assertEquals(cvfeature.getXrefs().size(), 2);
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(cvfeature, "11125103"));
        assertTrue(containsPrimaryId(cvfeature, "MI:0161"));

        shortlabel = "4-hydroxy-l-proline";
        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "4-hydroxy-L-proline");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // Two xrefs.
        assertEquals(cvfeature.getXrefs().size(), 2);
        // Compare the primary ids for the object
        assertTrue(containsPrimaryId(cvfeature, "11125103"));
        assertTrue(containsPrimaryId(cvfeature, "MI:0149"));

        shortlabel = "tau-phosphohistidine";
        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "1'-phospho-L-histidine");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // Two xrefs.
        assertEquals(cvfeature.getXrefs().size(), 2);
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(cvfeature, "11125103"));
        assertTrue(containsPrimaryId(cvfeature, "MI:0174"));

        shortlabel = "n-acetyl-l-arginine";
        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "N-acetyl-L-arginine");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // Two xrefs.
        assertEquals(cvfeature.getXrefs().size(), 2);
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(cvfeature, "11125103"));
        assertTrue(containsPrimaryId(cvfeature, "MI:0123"));

        shortlabel = "hypusine";
        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "N6-(4-amino-2-hydroxybutyl)-L-lysine (hypusine)");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // Two xrefs.
        assertEquals(cvfeature.getXrefs().size(), 2);
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(cvfeature, "11125103"));
        assertTrue(containsPrimaryId(cvfeature, "MI:0187"));

        shortlabel = "his-tagged";
        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "His-Tagged");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // One xref.
        assertEquals(cvfeature.getXrefs().size(), 1);
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(cvfeature, "MI:0521"));
    }

    private void doTestCvFeatureTypeDag() throws IntactException {
        // Check the database contents
        CvFeatureType cvfeature = (CvFeatureType) myHelper.getObjectByLabel(
                CvFeatureType.class, "binding site");
        // Has no children
        assertTrue(cvfeature.getChildren().isEmpty());
        // Has one parent.
        assertEquals(cvfeature.getParents().size(), 1);
        assertTrue(hasParent(cvfeature, "feature type"));

        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                "acetylation");
        // Has 21 children
        assertEquals(cvfeature.getChildren().size(), 21);
        assertTrue(hasChild(cvfeature, "acetylalanine"));
        assertTrue(hasChild(cvfeature, "n-acetylglycine"));
        assertTrue(hasChild(cvfeature, "n6-acetyl-l-lysine"));
        // Has one parent.
        assertEquals(cvfeature.getParents().size(), 1);
        assertTrue(hasParent(cvfeature, "ptm"));

        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                "amidation");
        // Has 2 children
        assertEquals(cvfeature.getChildren().size(), 2);
        assertTrue(hasChild(cvfeature, "alaninamide"));
        assertTrue(hasChild(cvfeature, "argininamide"));
        // Has one parent.
        assertEquals(cvfeature.getParents().size(), 1);
        assertTrue(hasParent(cvfeature, "ptm"));

        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                "v5-tagged");
        // Has no children
        assertTrue(cvfeature.getChildren().isEmpty());
        // Has one parent.
        assertEquals(cvfeature.getParents().size(), 1);
        assertTrue(hasParent(cvfeature, "tagged-protein"));
    }

    private void doTestCvFeatureIdentificationDef() throws IntactException {
        // Cache cvtopics.
        CvTopic definition = (CvTopic) myHelper.getObjectByLabel(CvTopic.class,
                "definition");

        String shortlabel = "alanine scanning";
        CvFeatureIdentification cvfeature = (CvFeatureIdentification) myHelper.getObjectByLabel(
                CvFeatureIdentification.class, shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "alanine scanning");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // One xref.
        assertEquals(cvfeature.getXrefs().size(), 1);
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(cvfeature, "MI:0005"));

        shortlabel = "complete sequence";
        cvfeature = (CvFeatureIdentification) myHelper.getObjectByLabel(CvFeatureIdentification.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "full identification by sequencing");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // One xref.
        assertEquals(cvfeature.getXrefs().size(), 1);
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(cvfeature, "MI:0056"));

        shortlabel = "epr";
        cvfeature = (CvFeatureIdentification) myHelper.getObjectByLabel(CvFeatureIdentification.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "electron paramagnetic resonance");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // Two xrefs.
        assertEquals(cvfeature.getXrefs().size(), 2);
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(cvfeature, "11817959"));
        assertTrue(containsPrimaryId(cvfeature, "MI:0042"));

        shortlabel = "protein staining";
        cvfeature = (CvFeatureIdentification) myHelper.getObjectByLabel(CvFeatureIdentification.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "protein staining");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // Two xrefs.
        assertEquals(cvfeature.getXrefs().size(), 2);
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(cvfeature, "12015990"));
        assertTrue(containsPrimaryId(cvfeature, "MI:0094"));

        shortlabel = "western blot";
        cvfeature = (CvFeatureIdentification) myHelper.getObjectByLabel(CvFeatureIdentification.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "western blot");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // One xref.
        assertEquals(cvfeature.getXrefs().size(), 1);
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(cvfeature, "MI:0113"));

        shortlabel = "x-ray";
        cvfeature = (CvFeatureIdentification) myHelper.getObjectByLabel(CvFeatureIdentification.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "x-ray crystallography");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // One xref.
        assertEquals(cvfeature.getXrefs().size(), 1);
        // Compare the primary id for the object
        assertTrue(containsPrimaryId(cvfeature, "MI:0114"));
    }

    private void doTestCvFeatureIdentificationDag() throws IntactException {
        // Check the database contents
        CvFeatureIdentification cvfeature = (CvFeatureIdentification) myHelper.getObjectByLabel(
                CvFeatureIdentification.class, "alanine scanning");
        // Has no children
        assertTrue(cvfeature.getChildren().isEmpty());
        // Has one parent.
        assertEquals(cvfeature.getParents().size(), 1);
        assertTrue(hasParent(cvfeature, "feature detection"));

        cvfeature = (CvFeatureIdentification) myHelper.getObjectByLabel(
                CvFeatureIdentification.class, "electron resonance");
        // Has two children
        assertEquals(cvfeature.getChildren().size(), 2);
        assertTrue(hasChild(cvfeature, "endor"));
        assertTrue(hasChild(cvfeature, "epr"));
        // Has one parent.
        assertEquals(cvfeature.getParents().size(), 1);
        assertTrue(hasParent(cvfeature, "feature detection"));

        cvfeature = (CvFeatureIdentification) myHelper.getObjectByLabel(
                CvFeatureIdentification.class, "endor");
        // Has one child.
        assertEquals(cvfeature.getChildren().size(), 1);
        assertTrue(hasChild(cvfeature, "docking"));
        // Has one parent.
        assertEquals(cvfeature.getParents().size(), 1);
        assertTrue(hasParent(cvfeature, "electron resonance"));

        cvfeature = (CvFeatureIdentification) myHelper.getObjectByLabel(
                CvFeatureIdentification.class, "western blot");
        // Has two children
        assertEquals(cvfeature.getChildren().size(), 2);
        assertTrue(hasChild(cvfeature, "monoclonal antibody"));
        assertTrue(hasChild(cvfeature, "polyclonal antibody"));
        // Has one parent.
        assertEquals(cvfeature.getParents().size(), 1);
        assertTrue(hasParent(cvfeature, "feature detection"));
    }

    private boolean containsTopic(AnnotatedObject annobj, String topic)
            throws IntactException {
        CvTopic cvtopic = (CvTopic) myHelper.getObjectByLabel(CvTopic.class, topic);
        return containsTopic(annobj, cvtopic);
    }

    private boolean containsTopic(AnnotatedObject annobj, CvTopic topic) {
        for (Iterator iter = annobj.getAnnotations().iterator(); iter.hasNext();) {
            Annotation annotation = (Annotation) iter.next();
            if (annotation.getCvTopic().equals(topic)) {
                return true;
            }
        }
        return false;
    }

    private int countsTopic(AnnotatedObject annobj, String topic) throws IntactException {
        CvTopic cvtopic = (CvTopic) myHelper.getObjectByLabel(CvTopic.class, topic);
        return countTopics(annobj, cvtopic);
    }

    private int countTopics(AnnotatedObject annobj, CvTopic topic) {
        int count = 0;
        for (Iterator iter = annobj.getAnnotations().iterator(); iter.hasNext();) {
            Annotation annotation = (Annotation) iter.next();
            if (annotation.getCvTopic().equals(topic)) {
                ++count;
            }
        }
        return count;
    }

    private boolean containsPrimaryId(AnnotatedObject annobj, String primaryId) {
        for (Iterator iter = annobj.getXrefs().iterator(); iter.hasNext();) {
            Xref xref = (Xref) iter.next();
            if (xref.getPrimaryId().equals(primaryId)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasChild(CvDagObject cvdag, String label) {
        return hasLabel(cvdag.getChildren(), label);
    }

    private boolean hasParent(CvDagObject cvdag, String label) {
        return hasLabel(cvdag.getParents(), label);
    }

    private boolean hasLabel(Collection collection, String label) {
        for (Iterator iter = collection.iterator(); iter.hasNext();) {
            if (((AnnotatedObject) iter.next()).getShortLabel().equals(label)) {
                return true;
            }
        }
        return false;
    }
}
