/*
    Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
    All rights reserved. Please see the file LICENSE
    in the root directory of this distribution.
*/

package uk.ac.ebi.intact.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.InteractionImpl;
import uk.ac.ebi.intact.util.TestCaseHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Test class for Interactions.
 * 
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionTest extends TestCase {
    
    /**
     * The test helper.
     */
    private TestCaseHelper myTestHelper;

    public InteractionTest(String name) throws Exception {
        super(name);
        myTestHelper = new TestCaseHelper();
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(InteractionTest.class);
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
            doCloneTest1();
        }
        catch (CloneNotSupportedException cnse) {
            fail(cnse.getMessage());
        }
        catch (IntactException ie) {
            fail(ie.getMessage());
        }
    }

    private void doCloneTest() throws CloneNotSupportedException {
        InteractionImpl orig =
            (InteractionImpl) myTestHelper.getInteractions().iterator().next();
        // Make a copy.
        Interaction copy = (Interaction) orig.clone();

        // No AC.
        assertNull(copy.getAc());

        // Time stamps are different.
        assertFalse(orig.getCreated().equals(copy.getCreated()));
        assertFalse(orig.getUpdated().equals(copy.getUpdated()));

        // Short label must have "-x".
        assertTrue(copy.getShortLabel().endsWith("-x"));
        assertEquals(orig.getShortLabel() + "-x", copy.getShortLabel());

        // Test for shared objects.
        assertSame(orig.getOwner(), copy.getOwner());
        assertSame(orig.getBioSource(), copy.getBioSource());
        assertSame(orig.getCvInteractionType(), copy.getCvInteractionType());

        // Fullname must match.
        assertEquals(orig.getFullName(), copy.getFullName());

        // Different copies of Annotations.
        assertNotSame(orig.getAnnotations(), copy.getAnnotations());
        assertEquals(orig.getAnnotations(), copy.getAnnotations());

        // Different copies of Xrefs.
        assertNotSame(orig.getXrefs(), copy.getXrefs());
        assertEquals(orig.getXrefs(), copy.getXrefs());

        // Same KD.
        assertEquals(orig.getKD(), copy.getKD());

        // Original has some experiments.
        assertFalse(orig.getExperiments().isEmpty());

        // The copy shouldn't have any experiments.
        assertTrue(copy.getExperiments().isEmpty());

        // New components.
        assertNotSame(orig.getComponents(), copy.getComponents());
        // Must be of same size.
        assertEquals(orig.getComponents().size(), copy.getComponents().size());

        // Extract the proteins and roles to match; can't compare components
        // because equals method is based on reference equality.
        Collection origprots = new ArrayList();
        Collection origroles = new ArrayList();
        for (Iterator iter = orig.getComponents().iterator(); iter.hasNext();) {
            Component comp = (Component) iter.next();
            origprots.add(comp.getInteractor());
            origroles.add(comp.getCvComponentRole());
        }
        Collection copyprots = new ArrayList();
        Collection copyroles = new ArrayList();
        for (Iterator iter = copy.getComponents().iterator(); iter.hasNext();) {
            Component comp = (Component) iter.next();
            copyprots.add(comp.getInteractor());
            copyroles.add(comp.getCvComponentRole());
        }
        // Must match the proteins and their roles.
        assertEquals(origprots, copyprots);
        assertEquals(origroles, copyroles);
    }

    // This test uses an Interaction already stored on the database.
    public void doCloneTest1() throws IntactException, CloneNotSupportedException {
        IntactHelper helper = myTestHelper.getHelper();
        InteractionImpl orig = (InteractionImpl) helper.getObjectByLabel(
                Interaction.class, "ga-3");
        // Make a copy.
        Interaction copy = (Interaction) orig.clone();

        // No AC.
        assertNull(copy.getAc());

        // Time stamps are different.
        assertFalse(orig.getCreated().equals(copy.getCreated()));
        assertFalse(orig.getUpdated().equals(copy.getUpdated()));

        // Short label must have "-x".
        assertTrue(copy.getShortLabel().endsWith("-x"));
        assertEquals(orig.getShortLabel() + "-x", copy.getShortLabel());

        // Test for shared objects.
        assertSame(orig.getOwner(), copy.getOwner());
        assertSame(orig.getBioSource(), copy.getBioSource());
        assertSame(orig.getCvInteractionType(), copy.getCvInteractionType());

        // Fullname must match.
        assertEquals(orig.getFullName(), copy.getFullName());

        // Different copies of Annotations.
        assertNotSame(orig.getAnnotations(), copy.getAnnotations());
        assertEquals(transform(orig.getAnnotations()), copy.getAnnotations());

        // Different copies of Xrefs.
        assertNotSame(orig.getXrefs(), copy.getXrefs());
        assertEquals(transform(orig.getXrefs()), copy.getXrefs());

        // Same KD.
        assertEquals(orig.getKD(), copy.getKD());

        // Original has some experiments.
        assertFalse(orig.getExperiments().isEmpty());

        // The copy shouldn't have any experiments.
        assertTrue(copy.getExperiments().isEmpty());

        // New components.
        assertNotSame(orig.getComponents(), copy.getComponents());
        // Must be of same size.
        assertEquals(orig.getComponents().size(), copy.getComponents().size());

        // Extract the proteins and roles to match; can't compare components
        // because equals method is based on reference equality.
        Collection origprots = new ArrayList();
        Collection origroles = new ArrayList();
        for (Iterator iter = orig.getComponents().iterator(); iter.hasNext();) {
            Component comp = (Component) iter.next();
            origprots.add(comp.getInteractor());
            origroles.add(comp.getCvComponentRole());
        }
        Collection copyprots = new ArrayList();
        Collection copyroles = new ArrayList();
        for (Iterator iter = copy.getComponents().iterator(); iter.hasNext();) {
            Component comp = (Component) iter.next();
            copyprots.add(comp.getInteractor());
            copyroles.add(comp.getCvComponentRole());
        }
        // Must match the proteins and their roles.
        assertEquals(origprots, copyprots);
        assertEquals(origroles, copyroles);
    }

    // Converts ListProxy to proper object for to compare.
    private List transform(Collection items) {
        List list = new ArrayList(items.size());
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            list.add(iter.next());
        }
        return list;
    }
}
