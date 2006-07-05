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

import java.util.*;

import org.apache.commons.collections.CollectionUtils;

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

    public void testInsertCvIdentificationDef() {
        try {
            doTestCvIdentificationDef();
        }
        catch (IntactException e) {
            fail(e.getMessage());
        }
    }

    public void testInsertCvIdentificationDag() {
        try {
            doTestCvIdentificationDag();
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

        topic = (CvTopic) myHelper.getObjectByLabel(CvTopic.class, "submitted");
        assertNotNull(topic);
        assertEquals(topic.getFullName(), "Data submitted by author/s directly to IntAct.");
        // One annotation.
        assertEquals(topic.getAnnotations().size(), 1);
        assertTrue(containsTopic(topic, definition));

        topic = (CvTopic) myHelper.getObjectByLabel(CvTopic.class, "url");
        assertNotNull(topic);
        assertEquals(topic.getFullName(), "URL/Web address");
        // Three annotations.
        assertEquals(topic.getAnnotations().size(), 3);
        assertTrue(containsTopic(topic, definition));

        topic = (CvTopic) myHelper.getObjectByLabel(CvTopic.class, "uniprot-dr-export");
        assertNotNull(topic);
        assertEquals(topic.getFullName(),
                "Determines if the experiment is to be exported to UniProt DR lines.");
        // Two annotations.
        assertEquals(topic.getAnnotations().size(), 2);
        // One is an definition.
        assertTrue(containsTopic(topic, definition));
        // Other is a comment.
        CvTopic comment = (CvTopic) myHelper.getObjectByLabel(CvTopic.class, "comment");
        assertTrue(containsTopic(topic, comment));
    }

    private void doTestCvDatabaseDef() throws IntactException {
        CvDatabase database = (CvDatabase) myHelper.getObjectByLabel(
                CvDatabase.class, CvDatabase.UNIPROT);
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
        // Cache cvobjs
        CvTopic definition = (CvTopic) myHelper.getObjectByLabel(CvTopic.class,
                "definition");
        CvDatabase pubmed = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class,
                "pubmed");
        CvDatabase goid = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class,
                "psi-mi");
        CvXrefQualifier identity = (CvXrefQualifier) myHelper.getObjectByLabel(
                CvXrefQualifier.class, "identity");
        CvXrefQualifier godef = (CvXrefQualifier) myHelper.getObjectByLabel(
                CvXrefQualifier.class, "go-definition-ref");

        // Temp objects.
        String shortlabel;
        AnnotatedObject annobj;

        shortlabel = "affinity techniques";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteraction.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "affinity technologies");
        // There are two annotations.
        assertEquals(annobj.getAnnotations().size(), 1);
        // Must contain topics definition.
        assertTrue(containsTopic(annobj, definition));
        // Compare the primary id for the object
        assertTrue(checkDBXref(annobj, pubmed, "14755292", godef));
        assertTrue(checkDBXref(annobj, goid, "MI:0400", identity));

        shortlabel = "anti tag coip";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteraction.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "anti tag coimmunoprecipitation");
        // There are two annotations.
        assertEquals(annobj.getAnnotations().size(), 1);
        // Must contain topics definition.
        assertTrue(containsTopic(annobj, definition));
        // Compare the primary id for the object
        assertTrue(checkDBXref(annobj, pubmed, "7708014", godef));
        assertTrue(checkDBXref(annobj, goid, "MI:0007", identity));

        shortlabel = "beta galactosidase";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteraction.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "beta galactosidase complementation");
        // There are two annotations.
        assertEquals(annobj.getAnnotations().size(), 1);
        // Must contain topics definition.
        assertTrue(containsTopic(annobj, definition));
        // Compare the primary id for the object
        assertTrue(checkDBXref(annobj, pubmed, "9237989", godef));
        assertTrue(checkDBXref(annobj, pubmed, "12042868", godef));
        assertTrue(checkDBXref(annobj, goid, "MI:0010", identity));

        shortlabel = "x-ray";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteraction.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "x-ray crystallography");
        // There are two annotations.
        assertEquals(annobj.getAnnotations().size(), 1);
        // Must contain topics definition.
        assertTrue(containsTopic(annobj, definition));
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0114", identity));
        assertTrue(checkDBXref(annobj, pubmed, "14755292", godef));

        shortlabel = "phage display";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteraction.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "phage display");
        // There are two annotations.
        assertEquals(annobj.getAnnotations().size(), 1);
        // Must contain topics definition and uniprot.
        assertTrue(containsTopic(annobj, definition));
        // Compare the primary id for the object
        assertTrue(checkDBXref(annobj, pubmed, "7708014", godef));
        assertTrue(checkDBXref(annobj, pubmed, "10975452", godef));
        assertTrue(checkDBXref(annobj, goid, "MI:0084", identity));
    }

    private void doTestCvInteractionDag() throws IntactException {
        // Check the database contents
        CvInteraction cvinter = (CvInteraction) myHelper.getObjectByLabel(CvInteraction.class,
                "interaction detectio");
        // Has one child
        assertEquals(cvinter.getChildren().size(), 3);
        assertTrue(hasChild(cvinter, "experimental"));
        assertTrue(hasChild(cvinter, "inference"));
        assertTrue(hasChild(cvinter, "prediction"));
        // No parents.
        assertTrue(cvinter.getParents().isEmpty());

        cvinter = (CvInteraction) myHelper.getObjectByLabel(CvInteraction.class,
                "biophysical");
        // Has 11 children
        assertEquals(cvinter.getChildren().size(), 11);
        assertTrue(hasChild(cvinter, "nmr"));
        assertTrue(hasChild(cvinter, "cd"));
        assertTrue(hasChild(cvinter, "x-ray"));
        // Has one parent.
        assertEquals(cvinter.getParents().size(), 1);
        assertTrue(hasParent(cvinter, "experimental"));

        cvinter = (CvInteraction) myHelper.getObjectByLabel(CvInteraction.class,
                "bret");
        // Has no children
        assertTrue(cvinter.getChildren().isEmpty());
        // Has one parent.
        assertEquals(cvinter.getParents().size(), 1);
        assertTrue(hasParent(cvinter, "fluorescence"));

        // Nodes with more than one parent.
        cvinter = (CvInteraction) myHelper.getObjectByLabel(CvInteraction.class,
                "bacterial display");
        // Has two parents.
        assertEquals(cvinter.getParents().size(), 2);
        assertTrue(hasParent(cvinter, "facs"));
        assertTrue(hasParent(cvinter, "display technologies"));

        cvinter = (CvInteraction) myHelper.getObjectByLabel(CvInteraction.class,
                "phosphatase htrf");
        // Has two parents.
        assertEquals(cvinter.getParents().size(), 2);
        assertTrue(hasParent(cvinter, "htrf"));
        assertTrue(hasParent(cvinter, "phosphatase assay"));
        // Two aliases.
        assertEquals(cvinter.getAliases().size(), 2);
        assertTrue(checkAlias(cvinter, "homogeneous time-resolved fluo"));
        assertTrue(checkAlias(cvinter, "phosphatase HTRF"));

        cvinter = (CvInteraction) myHelper.getObjectByLabel(CvInteraction.class,
                "kinase htrf");
        // Has two parents.
        assertEquals(cvinter.getParents().size(), 2);
        assertTrue(hasParent(cvinter, "protein kinase assay"));
        assertTrue(hasParent(cvinter, "htrf"));
        // Two aliases.
        assertEquals(cvinter.getAliases().size(), 2);
        assertTrue(checkAlias(cvinter, "homogeneous time-resolved fluo"));
        assertTrue(checkAlias(cvinter, "kinase HTRF"));
    }

    private void doTestCvIdentificationDef() throws IntactException {
        // Cache cvobjs
        CvDatabase goid = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class,
                "psi-mi");
        CvDatabase pubmed = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class,
                "pubmed");
        CvXrefQualifier identity = (CvXrefQualifier) myHelper.getObjectByLabel(
                CvXrefQualifier.class, "identity");
        CvXrefQualifier godef = (CvXrefQualifier) myHelper.getObjectByLabel(
                CvXrefQualifier.class, "go-definition-ref");

        // Temp objects.
        String shortlabel;
        AnnotatedObject annobj;

        shortlabel = "antibody detection";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvIdentification.class,
                shortlabel);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "identification by antibody");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0421", identity));

        shortlabel = "fingerprinting";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvIdentification.class,
                shortlabel);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "peptide massfingerprinting");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // Compare the primary id for the object
        assertTrue(checkDBXref(annobj, pubmed, "10967324", godef));
        assertTrue(checkDBXref(annobj, pubmed, "11752590", godef));
        assertTrue(checkDBXref(annobj, goid, "MI:0082", identity));

        shortlabel = "sequence tag";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvIdentification.class,
                shortlabel);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "sequence tag identification");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // Compare the primary id for the object
        assertTrue(checkDBXref(annobj, pubmed, "10967324", godef));
        assertTrue(checkDBXref(annobj, pubmed, "11752590", godef));
        assertTrue(checkDBXref(annobj, goid, "MI:0102", identity));

        shortlabel = "southern blot";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvIdentification.class,
                shortlabel);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "southern blot");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // Compare the primary id for the object
        assertTrue(checkDBXref(annobj, goid, "MI:0103", identity));
    }

    private void doTestCvIdentificationDag() throws IntactException {
        // Check the database contents
        CvIdentification cvident = (CvIdentification) myHelper.getObjectByXref(
                CvIdentification.class, "MI:0002");
        // Has 5 children
        assertEquals(cvident.getChildren().size(), 5);
        assertTrue(hasChild(cvident, "antibody detection"));
        assertTrue(hasChild(cvident, "mass spectrometry"));
        assertTrue(hasChild(cvident, "nucleotide sequence"));
        assertTrue(hasChild(cvident, "predetermined"));
        assertTrue(hasChild(cvident, "protein sequence"));
        // No parents.
        assertTrue(cvident.getParents().isEmpty());
        // One alias.
        assertEquals(cvident.getAliases().size(), 1);
        assertTrue(checkAlias(cvident, "participant detection"));

        cvident = (CvIdentification) myHelper.getObjectByLabel(CvIdentification.class,
                "mass spectrometry");
        // Has 3 children
        assertEquals(cvident.getChildren().size(), 3);
        assertTrue(hasChild(cvident, "fingerprinting"));
        assertTrue(hasChild(cvident, "ms protein sequence"));
        assertTrue(hasChild(cvident, "sequence tag"));
        // Has one parent.
        assertEquals(cvident.getParents().size(), 1);
        assertTrue(hasParent(cvident, "participant identif"));

        cvident = (CvIdentification) myHelper.getObjectByLabel(CvIdentification.class,
                "nucleotide sequence");
        // Has 2 children
        assertEquals(cvident.getChildren().size(), 2);
        assertTrue(hasChild(cvident, "full dna sequence"));
        assertTrue(hasChild(cvident, "partial dna sequence"));
        // Has one parent.
        assertEquals(cvident.getParents().size(), 1);
        assertTrue(hasParent(cvident, "participant identif"));
        // One alias.
        assertEquals(cvident.getAliases().size(), 1);
        assertTrue(checkAlias(cvident, "sequence cloning"));

        cvident = (CvIdentification) myHelper.getObjectByLabel(CvIdentification.class,
                "peptide synthesis");
        // Has no children
        assertTrue(cvident.getChildren().isEmpty());
        // Has one parent.
        assertEquals(cvident.getParents().size(), 1);
        assertTrue(hasParent(cvident, "protein sequence"));
    }

    private void doTestCvInteractionTypeDef() throws IntactException {
        // Validate types.
        verifyCvInteractionTypes(myHelper);

        // Cache cvobjs
        CvTopic definition = (CvTopic) myHelper.getObjectByLabel(CvTopic.class,
                "definition");
        CvDatabase goid = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class,
                "psi-mi");
        CvDatabase pubmed = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class,
                "pubmed");
        CvDatabase resid = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class,
                "resid");
        CvXrefQualifier identity = (CvXrefQualifier) myHelper.getObjectByLabel(
                CvXrefQualifier.class, "identity");
        CvXrefQualifier godef = (CvXrefQualifier) myHelper.getObjectByLabel(
                CvXrefQualifier.class, "go-definition-ref");

        // Temp objects.
        String shortlabel;
        AnnotatedObject annobj;

        shortlabel = "acetylation";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "acetylation reaction");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0192", identity));
        for (int i = 41; i < 56; i++) {
            assertTrue(checkDBXref(annobj, resid, "AA00" + i, godef));
        }

        shortlabel = "amidation";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "amidation reaction");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // There are 21 xrefs.
        assertEquals(annobj.getXrefs().size(), 21);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0193", identity));
        for (int i = 81; i < 100; i++) {
            assertTrue(checkDBXref(annobj, resid, "AA00" + i, godef));
        }
        assertTrue(checkDBXref(annobj, resid, "AA0100", godef));

        shortlabel = "cleavage";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "cleavage reaction");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0194", identity));
        // There is 1 xref.
        assertEquals(annobj.getXrefs().size(), 2);

        shortlabel = "covalent binding";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "covalent binding");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0195", identity));
        // There is 1 xref.
        assertEquals(annobj.getXrefs().size(), 2);

        shortlabel = "deacetylation";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "deacetylation reaction");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // There are 3 xrefs.
        assertEquals(annobj.getXrefs().size(), 3);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0197", identity));
        assertTrue(checkDBXref(annobj, resid, "AA0055", godef));
        assertTrue(checkDBXref(annobj, resid, "AA0056", godef));

        shortlabel = "defarnesylation";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "defarnesylation reaction");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // There are 2 xrefs.
        assertEquals(annobj.getXrefs().size(), 2);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0198", identity));
        assertTrue(checkDBXref(annobj, resid, "AA0102", godef));

        shortlabel = "deformylation";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "deformylation reaction");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // There are 2 xrefs.
        assertEquals(annobj.getXrefs().size(), 2);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0199", identity));
        assertTrue(checkDBXref(annobj, resid, "AA0211", godef));

        shortlabel = "degeranylation";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "degeranylation reaction");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // There are 2 xrefs.
        assertEquals(annobj.getXrefs().size(), 2);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0200", identity));
        assertTrue(checkDBXref(annobj, resid, "AA0104", godef));

        shortlabel = "demyristoylation";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "demyristoylation reaction");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // There are 2 xrefs.
        assertEquals(annobj.getXrefs().size(), 2);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0201", identity));
        assertTrue(checkDBXref(annobj, resid, "AA0078", godef));

        shortlabel = "depalmitoylation";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "depalmitoylation reaction");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // There are 5 xrefs.
        assertEquals(annobj.getXrefs().size(), 4);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0202", identity));
        assertTrue(checkDBXref(annobj, resid, "AA0106", godef));
        assertTrue(checkDBXref(annobj, resid, "AA0060", godef));
        assertTrue(checkDBXref(annobj, resid, "AA0077", godef));

        shortlabel = "dephosphorylation";
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
        // There are 9 xrefs.
        assertEquals(annobj.getXrefs().size(), 9);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0203", identity));
        assertTrue(checkDBXref(annobj, resid, "AA0033", godef));
        assertTrue(checkDBXref(annobj, resid, "AA0034", godef));
        assertTrue(checkDBXref(annobj, resid, "AA0035", godef));
        assertTrue(checkDBXref(annobj, resid, "AA0036", godef));
        assertTrue(checkDBXref(annobj, resid, "AA0037", godef));
        assertTrue(checkDBXref(annobj, resid, "AA0038", godef));
        assertTrue(checkDBXref(annobj, resid, "AA0039", godef));
        assertTrue(checkDBXref(annobj, resid, "AA0222", godef));

        shortlabel = "deubiquitination";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "deubiquitination reaction");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // There are 3 xrefs.
        assertEquals(annobj.getXrefs().size(), 3);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0204", identity));
        assertTrue(checkDBXref(annobj, pubmed, "11583613", godef));
        assertTrue(checkDBXref(annobj, resid, "AA0125", godef));

        shortlabel = "farnesylation";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "farnesylation reaction");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // There are 2 xrefs.
        assertEquals(annobj.getXrefs().size(), 2);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0206", identity));
        assertTrue(checkDBXref(annobj, resid, "AA0102", godef));

        shortlabel = "formylation";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "formylation reaction");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // Three xrefs
        assertEquals(annobj.getXrefs().size(), 3);
        // There are 3 xrefs.
        assertEquals(annobj.getXrefs().size(), 3);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0207", identity));
        // Check resid.
        assertTrue(checkDBXref(annobj, resid, "AA0211", godef));
        assertTrue(checkDBXref(annobj, resid, "AA0057", godef));

        shortlabel = "methylation";
        annobj = (AnnotatedObject) myHelper.getObjectByLabel(CvInteractionType.class,
                shortlabel);
        // Must have the object
        assertNotNull(annobj);
        assertNotNull(annobj.getAc());
        // Check the full name.
        assertEquals(annobj.getFullName(), "methylation reaction");
        // There is one definition
        assertEquals(annobj.getAnnotations().size(), 1);
        // 18 xrefs
        assertEquals(annobj.getXrefs().size(), 19);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0213", identity));
        for (int i = 61; i < 77; i++) {
            assertTrue(checkDBXref(annobj, resid, "AA00" + i, godef));
        }
        assertTrue(checkDBXref(annobj, resid, "AA0234", godef));
        assertTrue(checkDBXref(annobj, resid, "AA0272", godef));

        shortlabel = "ubiquitination";
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
        // There are 3 xrefs.
        assertEquals(annobj.getXrefs().size(), 3);
        // go id
        assertTrue(checkDBXref(annobj, goid, "MI:0220", identity));
        assertTrue(checkDBXref(annobj, pubmed, "11583613", godef));
        assertTrue(checkDBXref(annobj, resid, "AA0125", godef));
    }

    private void doTestCvInteractionTypeDag() throws IntactException {
        CvInteractionType cvintertype = (CvInteractionType) myHelper.getObjectByLabel(
                CvInteractionType.class, "cleavage");
        // Has no children.
        assertTrue(cvintertype.getChildren().isEmpty());
        // Has one parent
        assertEquals(cvintertype.getParents().size(), 1);
        assertTrue(hasParent(cvintertype, "enzymatic reaction"));

        cvintertype = (CvInteractionType) myHelper.getObjectByLabel(
                CvInteractionType.class, "lipid addition");
        // Has 4 children
        assertEquals(cvintertype.getChildren().size(), 4);
        assertTrue(hasChild(cvintertype, "farnesylation"));
        assertTrue(hasChild(cvintertype, "geranylation"));
        assertTrue(hasChild(cvintertype, "myristoylation"));
        assertTrue(hasChild(cvintertype, "palmitoylation"));
        // Has one parent.
        assertTrue(hasParent(cvintertype, "enzymatic reaction"));

        cvintertype = (CvInteractionType) myHelper.getObjectByLabel(
                CvInteractionType.class, "lipid cleavage");
        // Has two children.
        assertEquals(cvintertype.getChildren().size(), 4);
        assertTrue(hasChild(cvintertype, "defarnesylation"));
        assertTrue(hasChild(cvintertype, "degeranylation"));
        assertTrue(hasChild(cvintertype, "demyristoylation"));
        assertTrue(hasChild(cvintertype, "depalmitoylation"));
        // Has one parent.
        assertTrue(hasParent(cvintertype, "enzymatic reaction"));

        cvintertype = (CvInteractionType) myHelper.getObjectByLabel(
                CvInteractionType.class, "physical interaction");
        // Has two children.
        assertEquals(cvintertype.getChildren().size(), 2);
        assertTrue(hasChild(cvintertype, "direct interaction"));
        assertTrue(hasChild(cvintertype, "enzymatic reaction"));
        // Has one parent.
        assertTrue(hasParent(cvintertype, "interaction type"));

        cvintertype = (CvInteractionType) myHelper.getObjectByLabel(
                CvInteractionType.class, "direct interaction");
        // Has two children.
        assertEquals(cvintertype.getChildren().size(), 1);
        assertTrue(hasChild(cvintertype, "covalent binding"));
        // Has one parent.
        assertTrue(hasParent(cvintertype, "physical interaction"));
    }

    private void doTestCvFeatureTypeDef() throws IntactException {
        // Validate types.
//        verifyCvFeatureTypes(myHelper);

        // Cache cvobjs.
        CvTopic definition = (CvTopic) myHelper.getObjectByLabel(CvTopic.class,
                "definition");
        CvDatabase goid = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class,
                "psi-mi");
        CvDatabase pubmed = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class,
                "pubmed");
        CvDatabase resid = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class,
                "resid");
        CvXrefQualifier identity = (CvXrefQualifier) myHelper.getObjectByLabel(
                CvXrefQualifier.class, "identity");
        CvXrefQualifier godef = (CvXrefQualifier) myHelper.getObjectByLabel(
                CvXrefQualifier.class, "go-definition-ref");

        String shortlabel = "methylthioaspartate";
        CvFeatureType cvfeature = (CvFeatureType) myHelper.getObjectByLabel(
                CvFeatureType.class, shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "beta-methylthioaspartic acid");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // Three xrefs.
        assertEquals(cvfeature.getXrefs().size(), 3);
        // Compare the primary id for the object
        assertTrue(checkDBXref(cvfeature, pubmed, "11125103", godef));
        assertTrue(checkDBXref(cvfeature, goid, "MI:0161", identity));
        assertTrue(checkDBXref(cvfeature, resid, "AA0232", godef));
        // Two aliases.
        assertEquals(cvfeature.getAliases().size(), 6);
        assertTrue(checkAlias(cvfeature, "DM2"));

        shortlabel = "4hydroxyproline";
        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "4-hydroxy-proline");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // Three xrefs.
        assertEquals(cvfeature.getXrefs().size(), 3);
        // Compare the primary ids for the object
        assertTrue(checkDBXref(cvfeature, pubmed, "11125103", godef));
        assertTrue(checkDBXref(cvfeature, goid, "MI:0149", identity));
        assertTrue(checkDBXref(cvfeature, resid, "AA0030", godef));
        // Two aliases.
        assertEquals(cvfeature.getAliases().size(), 2);
        assertTrue(checkAlias(cvfeature, "HYP"));
        assertTrue(checkAlias(cvfeature, "4-hydroxy-L-proline"));

        shortlabel = "1-phosphohistidine";
        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "1'-phospho-histidine");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // Three xrefs.
        assertEquals(cvfeature.getXrefs().size(), 3);
        // Compare the primary id for the object
        assertTrue(checkDBXref(cvfeature, pubmed, "11125103", godef));
        assertTrue(checkDBXref(cvfeature, goid, "MI:0174", identity));
        assertTrue(checkDBXref(cvfeature, resid, "AA0035", godef));
        // 8 aliases.
        assertEquals(cvfeature.getAliases().size(), 8);
        assertTrue(checkAlias(cvfeature, "HPE"));
        assertTrue(checkAlias(cvfeature, "tau-phosphohistidine"));
        assertTrue(checkAlias(cvfeature, "tele-phosphohistidine"));

        shortlabel = "acetylarginine";
        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "n2-acetyl-arginine");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // Three xrefs.
        assertEquals(cvfeature.getXrefs().size(), 3);
        // Compare the primary id for the object
        assertTrue(checkDBXref(cvfeature, pubmed, "11125103", godef));
        assertTrue(checkDBXref(cvfeature, goid, "MI:0123", identity));
        assertTrue(checkDBXref(cvfeature, resid, "AA0354", godef));
        // Two aliases.
        assertEquals(cvfeature.getAliases().size(), 4);
        assertTrue(checkAlias(cvfeature, "RAC"));
        assertTrue(checkAlias(cvfeature, "acetylarginine"));

        shortlabel = "hypusine";
        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "n6-(4-amino-2-hydroxybutyl)-lysine");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // Three xrefs.
        assertEquals(cvfeature.getXrefs().size(), 3);
        // Compare the primary id for the object
        assertTrue(checkDBXref(cvfeature, pubmed, "11125103", godef));
        assertTrue(checkDBXref(cvfeature, goid, "MI:0187", identity));
        assertTrue(checkDBXref(cvfeature, resid, "AA0116", godef));
        // Two aliases.
        assertEquals(cvfeature.getAliases().size(), 4);
        assertTrue(checkAlias(cvfeature, "KHY"));
        assertTrue(checkAlias(cvfeature, "hypusine"));

        shortlabel = "his tagged";
        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "his tagged");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // Two xrefs.
        assertEquals(cvfeature.getXrefs().size(), 2);
        // Compare the primary id for the object
        assertTrue(checkDBXref(cvfeature, pubmed, "14755292", godef));
        assertTrue(checkDBXref(cvfeature, goid, "MI:0521", identity));
        // Three aliases.
        assertEquals(cvfeature.getAliases().size(), 3);
        assertTrue(checkAlias(cvfeature, "6-His-tagged"));
        assertTrue(checkAlias(cvfeature, "Hexa-His-tagged"));
        assertTrue(checkAlias(cvfeature, "Histidine-tagged"));

        // No Aliases for this object.
        shortlabel = "tagged molecule";
        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                shortlabel);
        // Must have the object
        assertNotNull(cvfeature);
        assertNotNull(cvfeature.getAc());
        // Check the full name.
        assertEquals(cvfeature.getFullName(), "tagged molecule");
        // There is one annotation.
        assertEquals(cvfeature.getAnnotations().size(), 1);
        // Must contain the topic definition.
        assertTrue(containsTopic(cvfeature, definition));
        // Two xrefs.
        assertEquals(cvfeature.getXrefs().size(), 2);
        // Compare the primary id for the object
        assertTrue(checkDBXref(cvfeature, pubmed, "14755292", godef));
        assertTrue(checkDBXref(cvfeature, goid, "MI:0507", identity));
        // No aliases.
        assertTrue(cvfeature.getAliases().isEmpty());
    }

    private void doTestCvFeatureTypeDag() throws IntactException {
        // Check the database contents
        CvFeatureType cvfeature = (CvFeatureType) myHelper.getObjectByLabel(
                CvFeatureType.class, "binding site");
        // Has two children
        assertEquals(cvfeature.getChildren().size(), 2);
        assertTrue(hasChild(cvfeature, "required to bind"));
        assertTrue(hasChild(cvfeature, "sufficient to bind"));
        // Has one parent.
        assertEquals(cvfeature.getParents().size(), 1);
        assertTrue(hasParent(cvfeature, "feature type"));

        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                "acetylated residue");
        // Has 20 children
        assertEquals(cvfeature.getChildren().size(), 20);
        assertTrue(hasChild(cvfeature, "acetylalanine"));
        assertTrue(hasChild(cvfeature, "acetylglycine"));
        assertTrue(hasChild(cvfeature, "acetyllysine"));
        // Has one parent.
        assertEquals(cvfeature.getParents().size(), 1);
        assertTrue(hasParent(cvfeature, "ptm"));

        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                "amidated residue");
        // Has 2 children
        assertEquals(cvfeature.getChildren().size(), 2);
        assertTrue(hasChild(cvfeature, "alanineamide"));
        assertTrue(hasChild(cvfeature, "arginineamide"));
        // Has one parent.
        assertEquals(cvfeature.getParents().size(), 1);
        assertTrue(hasParent(cvfeature, "ptm"));

        cvfeature = (CvFeatureType) myHelper.getObjectByLabel(CvFeatureType.class,
                "v5 tagged");
        // Has no children
        assertTrue(cvfeature.getChildren().isEmpty());
        // Has one parent.
        assertEquals(cvfeature.getParents().size(), 1);
        assertTrue(hasParent(cvfeature, "tagged molecule"));
    }

    private void doTestCvFeatureIdentificationDef() throws IntactException {
        // Validate types.
        verifyCvFeatureIdentifications(myHelper);

        // Cache cvobjs.
        CvTopic definition = (CvTopic) myHelper.getObjectByLabel(CvTopic.class,
                "definition");
        CvDatabase goid = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class,
                "psi-mi");
        CvDatabase pubmed = (CvDatabase) myHelper.getObjectByLabel(CvDatabase.class,
                "pubmed");
        CvXrefQualifier identity = (CvXrefQualifier) myHelper.getObjectByLabel(
                CvXrefQualifier.class, "identity");
        CvXrefQualifier godef = (CvXrefQualifier) myHelper.getObjectByLabel(
                CvXrefQualifier.class, "go-definition-ref");

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
        // Two xrefs.
        assertEquals(cvfeature.getXrefs().size(), 2);
        // Compare the primary id for the object
        assertTrue(checkDBXref(cvfeature, goid, "MI:0005", identity));
        assertTrue(checkDBXref(cvfeature, pubmed, "14755292", godef));
        // No aliases.
        assertTrue(cvfeature.getAliases().isEmpty());

        shortlabel = "full dna sequence";
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
        // Two xrefs.
        assertEquals(cvfeature.getXrefs().size(), 2);
        // Compare the primary id for the object
        assertTrue(checkDBXref(cvfeature, goid, "MI:0056", identity));
        assertTrue(checkDBXref(cvfeature, pubmed, "14755292", godef));
        // No aliases.
        assertTrue(cvfeature.getAliases().isEmpty());

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
        assertTrue(checkDBXref(cvfeature, pubmed, "11817959", godef));
        assertTrue(checkDBXref(cvfeature, goid, "MI:0042", identity));
        // Two aliases.
        assertEquals(cvfeature.getAliases().size(), 2);
        assertTrue(checkAlias(cvfeature, "EPR"));
        assertTrue(checkAlias(cvfeature, "ESR"));

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
        assertTrue(checkDBXref(cvfeature, pubmed, "12015990", godef));
        assertTrue(checkDBXref(cvfeature, goid, "MI:0094", identity));
        // No aliases.
        assertTrue(cvfeature.getAliases().isEmpty());

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
        // Two xrefs.
        assertEquals(cvfeature.getXrefs().size(), 2);
        // Compare the primary id for the object
        assertTrue(checkDBXref(cvfeature, goid, "MI:0113", identity));
        assertTrue(checkDBXref(cvfeature, pubmed, "14755292", godef));
        // One alias.
        assertEquals(cvfeature.getAliases().size(), 1);
        assertTrue(checkAlias(cvfeature, "Immuno blot"));

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
        // Two xrefs.
        assertEquals(cvfeature.getXrefs().size(), 2);
        // Compare the primary id for the object
        assertTrue(checkDBXref(cvfeature, goid, "MI:0114", identity));
        assertTrue(checkDBXref(cvfeature, pubmed, "14755292", godef));
        // One alias.
        assertEquals(cvfeature.getAliases().size(), 1);
        assertTrue(checkAlias(cvfeature, "X-ray"));
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
        // Has no children.
        assertTrue(cvfeature.getChildren().isEmpty());
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

    private boolean checkDBXref(AnnotatedObject annobj, CvDatabase db,
                                String primaryId, CvXrefQualifier xrefq) {
        for (Iterator iter = annobj.getXrefs().iterator(); iter.hasNext();) {
            Xref xref = (Xref) iter.next();
            if (xref.getCvDatabase().equals(db) && xref.getPrimaryId().equals(
                    primaryId) && xref.getCvXrefQualifier().equals(xrefq)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkAlias(AnnotatedObject annobj, String name) {
        List aliasNames = extractAliasNames((List) annobj.getAliases());
        return aliasNames.contains(name);
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

    private void verifyCvInteractionTypes(IntactHelper helper) throws IntactException {
        String[] items = new String[] {
            "acetylation", "adp ribosylation", "amidation", "cleavage", "colocalization",
            "covalent binding", "deacetylation", "defarnesylation", "deformylation",
            "degeranylation", "deglycosylation", "demyristoylation", "depalmitoylation",
            "dephosphorylation", "deubiquitination", "direct interaction", "disulfide bond",
            "enzymatic reaction", "farnesylation", "formylation", "genetic interaction",
            "geranylation", "glycosylation", "hydroxylation", "interaction type",
            "lipid addition", "lipid cleavage", "methylation", "myristoylation",
            "palmitoylation", "phosphorylation", "physical interaction",
            "synthetic lethal", "transglutamination", "ubiquitination"
        };
        List results = extractShortLabels((List) helper.search(
                CvInteractionType.class, "ac", "*"));
        assertTrue(CollectionUtils.isEqualCollection(results, Arrays.asList(items)));
    }

    private void verifyCvFeatureTypes(IntactHelper helper) throws IntactException {
        String[] items = new String[] {
            "1-phosphohistidine", "3-phosphohistidine", "4hydroxyproline",
            "acetylalanine", "acetylarginine", "acetylasparagine", "acetylaspartate",
            "acetylated residue", "acetylcysteine", "acetylglutamate", "acetylglutamine",
            "acetylglycine", "acetylhistidine", "acetylisoleucine", "acetylleucine",
            "acetyllysine", "acetylmethionine", "acetylphenylalanine", "acetylproline",
            "acetylserine", "acetylthreonine", "acetyltryptophan", "acetyltyrosine",
            "acetylvaline", "adp-ribosylarginine", "adpribosylasparagine", "adp-ribosylated",
            "adp-ribosylcysteine", "adp-ribosylglutamate", "adp-ribosylserine", "alanineamide",
            "alkylated cysteine", "amidated residue", "arginineamide", "binding site",
            "biotinyllysine", "carboxyglutamic acid", "dimethylarginine", "dimethyllysine",
            "diphthamide", "farnesylcysteine", "feature type", "flag-tagged",
            "formylated residue", "formylmethionine", "geranylgeranylcys", "glutamatemethylester",
            "glycerylpo4etohamine", "glycosylarginine", "glycosylasparagine", "glycosylated residue",
            "glycosyl-cysteine", "glycosyl-serine", "glycosyl-threonine", "gpi-alanine",
            "gpi anchor residue", "gpi-asparagine", "gpi-aspartate", "gpi-cysteine",
            "gpi-glycine", "gpi-serine", "gpi-threonine", "gst-tagged", "ha-tagged",
            "his-tagged", "hotspot", "hydroxylated residue", "hypusine", "lipid modification",
            "methylalanine", "methylarginine", "methylated alanine", "methylated arginine",
            "methylatedlysine", "methylated residue", "methylglutamine", "methylhistidine",
            "methyllysine", "methylmethionine", "methylphenylalanine", "methylthioaspartate",
            "mutation", "myc-tagged", "myristoylglycine", "myristoyllysine", "mytistoylated aa",
            "n2-acetyllysine", "n6-acetyllysine", "nitrosylcysteine", "nitro-tyrosine",
            "n-palmitoylcysteine", "other modification", "oxoalanine", "palmitoylated aa",
            "phosphoarginine", "phosphoaspartic acid", "phosphocysteine", "phosphorylated",
            "phosphoserine", "phosphoshistidine", "phosphothreonine", "phosphotyrosine",
            "prenylcysteine", "ptm", "pyroglutamic acid", "required to bind", "retinallysine",
            "selenocysteine", "selenomethionine", "s-palmitoylcysteine", "sufficient to bind",
            "sulfotyrosine", "sumoylated lysine", "t7-tagged", "tagged-protein", 
            "tap-tagged", "trimethylalanine", "trimethyllysine", "ubiquitinated lysine",
            "v5-tagged"
        };
        List results = extractShortLabels((List) helper.search(
                CvFeatureType.class, "ac", "*"));
        assertTrue(CollectionUtils.isEqualCollection(results, Arrays.asList(items)));
    }

    private void verifyCvFeatureIdentifications(IntactHelper helper) throws IntactException {
        String[] items = new String[] {
            "alanine scanning", "correlated mutations", "deletion analysis",
            "docking", "domain profile pairs", "electron resonance", "endor",
            "epr", "feature detection", "full dna sequence", "interface predictor",
            "mobility shift", "modified residue ms", "monoclonal antibody",
            "ms protein sequence", "mutation analysis", "nmr", "polyclonal antibody",
            "protein footprinting", "protein staining", "surface patches", "western blot",
            "x-ray"
        };
        List results = extractShortLabels((List) helper.search(
                CvFeatureIdentification.class, "ac", "*"));
        assertTrue(CollectionUtils.isEqualCollection(results, Arrays.asList(items)));
    }

    private List extractShortLabels(List annobjs) {
        List labels = new ArrayList();
        for (Iterator iter = annobjs.iterator(); iter.hasNext();) {
            labels.add(((AnnotatedObject) iter.next()).getShortLabel());
        }
        return labels;
    }

    private List extractAliasNames(List aliases) {
        List names = new ArrayList();
        for (Iterator iter = aliases.iterator(); iter.hasNext();) {
            names.add(((Alias) iter.next()).getName());
        }
        return names;
    }
}
