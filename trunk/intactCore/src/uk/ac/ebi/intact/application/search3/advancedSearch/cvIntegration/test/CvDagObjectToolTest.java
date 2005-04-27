/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search3.advancedSearch.cvIntegration.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.application.search3.advancedSearch.cvIntegration.CvDagObjectUtils;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;

import java.util.Collection;
import java.util.Iterator;

/**
 * TODO comment that ...
 *
 * @author Anja Friedrichsen
 * @version $id$
 */
public class CvDagObjectToolTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public CvDagObjectToolTest(final String name) {
        super(name);
    }

    Institution owner;
    CvDagObjectUtils dagUtils;
    IntactHelper helper;
    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(CvDagObjectToolTest.class);
    }

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws IntactException {
        helper = new IntactHelper();
        dagUtils = new CvDagObjectUtils(helper);
        owner = new Institution("test");
    }


     protected void tearDown() throws IntactException {
         helper.closeStore();
     }
    /**
     * This method will test the creation of a linear model (Collection)
     * from a hierarchical (tree) model. It will validate the left and
     * right bound assignments.
     */
    public void testLinearModelCreationFromTree() {
        CvDagObject first = new CvInteraction(owner, "first");
        dagUtils.buildBounds(first, 1);
        assertEquals(1, first.getLeftBound());
        assertEquals(2, first.getRightBound());

        dagUtils.buildBounds(first, 99);
        assertEquals(99, first.getLeftBound());
        assertEquals(100, first.getRightBound());

        CvDagObject second = new  CvInteraction(owner, "second");
        first.addChild(second);
        dagUtils.buildBounds(first,1);
        assertEquals(1, first.getLeftBound());
        assertEquals(4, first.getRightBound());
        assertEquals(2, second.getLeftBound());
        assertEquals(3, second.getRightBound());

        CvDagObject third = new  CvInteraction(owner, "third");
        CvDagObject fourth = new  CvInteraction(owner, "fourth");
        CvDagObject fifth = new  CvInteraction(owner, "fifth");
        first.addChild(fifth);
        second.addChild(third);
        second.addChild(fourth);
        dagUtils.buildBounds(first, 1);
        assertEquals(1, first.getLeftBound());
        assertEquals(10, first.getRightBound());
        assertEquals(2, second.getLeftBound());
        assertEquals(7, second.getRightBound());
        assertEquals(3, third.getLeftBound());
        assertEquals(4, third.getRightBound());
        assertEquals(5, fourth.getLeftBound());
        assertEquals(6, fourth.getRightBound());
        assertEquals(8, fifth.getLeftBound());
        assertEquals(9, fifth.getRightBound());

        CvDagObject sixth = new CvInteraction(owner, "sixth");
        CvDagObject seventh = new CvInteraction(owner, "seventh");
        CvDagObject eighth = new CvInteraction(owner, "eighth");
        fourth.addChild(sixth);
        fourth.addChild(seventh);
        fifth.addChild(eighth);
        dagUtils.buildBounds(first, 1);
        assertEquals(1, first.getLeftBound());
        assertEquals(16, first.getRightBound());
        assertEquals(2, second.getLeftBound());
        assertEquals(11, second.getRightBound());
        assertEquals(3, third.getLeftBound());
        assertEquals(4, third.getRightBound());
        assertEquals(5, fourth.getLeftBound());
        assertEquals(10, fourth.getRightBound());
        assertEquals(12, fifth.getLeftBound());
        assertEquals(15, fifth.getRightBound());
        assertEquals(6, sixth.getLeftBound());
        assertEquals(7, sixth.getRightBound());
        assertEquals(8, seventh.getLeftBound());
        assertEquals(9, seventh.getRightBound());
        assertEquals(13, eighth.getLeftBound());
        assertEquals(14, eighth.getRightBound());

    }

    public void testInsert() throws IntactException {
        dagUtils.insertCVs(CvInteraction.class);
        CvDagObject aDagObject = (CvDagObject) helper.getObjectByLabel(CvInteraction.class, "anti tag coimmunopre");
        CvDagObject aChild1 = (CvDagObject) helper.getObjectByLabel(CvInteraction.class, "flag tag");
        CvDagObject aChild2 = (CvDagObject) helper.getObjectByLabel(CvInteraction.class, "his tag");
        CvDagObject aChild3 = (CvDagObject) helper.getObjectByLabel(CvInteraction.class, "myc tag");
        CvDagObject aChild4 = (CvDagObject) helper.getObjectByLabel(CvInteraction.class, "ha tag");
        CvDagObject aChild5 = (CvDagObject) helper.getObjectByLabel(CvInteraction.class, "tandem affinity puri");

        Collection allChildren = dagUtils.getCvWithChildren(aDagObject);
        for (Iterator iterator = allChildren.iterator(); iterator.hasNext();) {
            String s = (String) iterator.next();
            System.out.println("Found: " + s);

        }
        assertTrue(allChildren.contains(aChild1.getAc()));
        assertTrue(allChildren.contains(aChild2.getAc()));
        assertTrue(allChildren.contains(aChild3.getAc()));
        assertTrue(allChildren.contains(aChild4.getAc()));
        assertTrue(allChildren.contains(aChild5.getAc()));
        assertEquals(5, allChildren.size());

        dagUtils.insertCVs(CvIdentification.class);
        aDagObject = (CvDagObject) helper.getObjectByLabel(CvIdentification.class, "nucleotide sequence");
        aChild1 = (CvDagObject) helper.getObjectByLabel(CvIdentification.class, "partial dna sequence");
        aChild2 = (CvDagObject) helper.getObjectByLabel(CvIdentification.class, "full identification");
        aChild3 = (CvDagObject) helper.getObjectByLabel(CvIdentification.class, "southern blot");
        aChild4 = (CvDagObject) helper.getObjectByLabel(CvIdentification.class, "primer specific pcr");

        allChildren = dagUtils.getCvWithChildren(aDagObject);
        assertTrue(allChildren.contains(aChild1.getAc()));
        assertTrue(allChildren.contains(aChild2.getAc()));
        assertTrue(allChildren.contains(aChild3.getAc()));
        assertTrue(allChildren.contains(aChild4.getAc()));
        assertEquals(4, allChildren.size());

        dagUtils.insertCVs(CvInteractionType.class);
        aDagObject = (CvDagObject) helper.getObjectByLabel(CvInteractionType.class, "lipid cleavage");
        aChild1 = (CvDagObject) helper.getObjectByLabel(CvInteractionType.class, "degeranylation");
        aChild2 = (CvDagObject) helper.getObjectByLabel(CvInteractionType.class, "defarnesylation reac");
        aChild3 = (CvDagObject) helper.getObjectByLabel(CvInteractionType.class, "demyristoylation");
        aChild4 = (CvDagObject) helper.getObjectByLabel(CvInteractionType.class, "depalmitoylation");

        allChildren = dagUtils.getCvWithChildren(aDagObject);
        assertTrue(allChildren.contains(aChild1.getAc()));
        assertTrue(allChildren.contains(aChild2.getAc()));
        assertTrue(allChildren.contains(aChild3.getAc()));
        assertTrue(allChildren.contains(aChild4.getAc()));
        assertEquals(4, allChildren.size());


    }
}
