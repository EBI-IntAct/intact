/*
    Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
    All rights reserved. Please see the file LICENSE
    in the root directory of this distribution.
*/

package uk.ac.ebi.intact.model.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.CollectionUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.util.TestCaseHelper;

/**
 * The test clas for InteractionImpl.
 * 
 * @author smudali
 * @version $Id$
 */
public class InteractionImplTest extends TestCase {
    
    /**
     * The test helper.
     */
    TestCaseHelper myTestHelper;

    public InteractionImplTest(String name) throws Exception {
        super(name);
        myTestHelper = new TestCaseHelper();
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(InteractionImplTest.class);
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
    
    public void testCopy() {
        Interaction orig = 
            (Interaction) myTestHelper.getInteractions().iterator().next();
         // Makea copy.
         Interaction copy = orig.copy();
         
         // Same Owner.
         assertEquals(orig.getOwner(), copy.getOwner());
         
         // Short label must have "-x".
         assertTrue(copy.getShortLabel().endsWith("-x"));
         assertEquals(orig.getShortLabel() + "-x", copy.getShortLabel());
         
         // No experiments copied.
         assertTrue(copy.getExperiments().isEmpty());
         
         // Same biosource and cv interaction type.
         assertEquals(orig.getBioSource(), copy.getBioSource());
         assertEquals(orig.getCvInteractionType(), copy.getCvInteractionType());
         
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
         
         // New xrefs but with the same contents.
         assertNotSame(orig.getXrefs(), copy.getXrefs());
         assertEquals(orig.getXrefs(), copy.getXrefs());    
    }
}
