/*
    Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
    All rights reserved. Please see the file LICENSE
    in the root directory of this distribution.
*/

package uk.ac.ebi.intact.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.util.TestCaseHelper;

/**
 * Tests for Experiments.
 * 
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentTest extends TestCase {
    
    /**
     * The test helper.
     */
    private TestCaseHelper myTestHelper;

    public ExperimentTest(String name) throws Exception {
        super(name);
        myTestHelper = new TestCaseHelper();
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(ExperimentTest.class);
    }
    
    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws Exception {
        super.setUp();
        myTestHelper.setUp();

    }

    /**
     * Tears down the test fixture. Called after every test case method.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        myTestHelper.tearDown();
    }

    public void testClone() {
        try {
            doCloneTest();
        }
        catch (CloneNotSupportedException cnse) {
            fail(cnse.getMessage());
        }
    }

    private void doCloneTest() throws CloneNotSupportedException {
        Experiment orig =
            (Experiment) myTestHelper.getExperiments().iterator().next();

        // Add an interaction to the original exp.
        if (orig.getInteractions().isEmpty()) {
            orig.addInteraction((Interaction) myTestHelper.getInteractions().get(0));
        }

        // Make a copy.
        Experiment copy = (Experiment) orig.clone();

        // Short label must have "-x".
        assertTrue(copy.getShortLabel().endsWith("-x"));
        assertEquals(orig.getShortLabel() + "-x", copy.getShortLabel());

        // Test for shared objects.
        assertSame(orig.getOwner(), copy.getOwner());
        assertSame(orig.getBioSource(), copy.getBioSource());
        assertSame(orig.getCvIdentification(), copy.getCvIdentification());
        assertSame(orig.getCvInteraction(), copy.getCvInteraction());

        // Fullname must match.
        assertEquals(orig.getFullName(), copy.getFullName());

        // Different copies of Annotations.
        assertNotSame(orig.getAnnotations(), copy.getAnnotations());
        assertEquals(orig.getAnnotations(), copy.getAnnotations());

        // Different copies of Xrefs.
        assertNotSame(orig.getXrefs(), copy.getXrefs());
        assertEquals(orig.getXrefs(), copy.getXrefs());

        // We should have at least one interaction.
        assertFalse(orig.getInteractions().isEmpty());

        // The copy shouldn't have any interactions.
        assertTrue(copy.getInteractions().isEmpty());

        // No related experiments.
        assertNull(orig.getRelatedExperiment());
        assertNull(copy.getRelatedExperiment());
    }
}
