/*
    Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
    All rights reserved. Please see the file LICENSE
    in the root directory of this distribution.
*/

package uk.ac.ebi.intact.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.*;

/**
 * Tests for Protein class.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinTest extends TestCase {

    public ProteinTest(String name) throws Exception {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(ProteinTest.class);
    }

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Tears down the test fixture. Called after every test case method.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEqualsAndHashCode() {
        // The owner for objects.
        Institution owner = new Institution("xyz");

        // The source
        BioSource src = new BioSource(owner, "yeast", "1234");

        CvInteractorType type = new CvInteractorType(owner, "protein");

        Protein prot = new ProteinImpl(owner, src, "prot1", type);

        // Shouldn't equal to null or another type
        assertFalse(prot.equals(null));
        assertFalse(prot.equals("abc"));

        // Test for equaling with Nucleic Acid (shares the same super class)
        CvInteractorType interactorType = new CvInteractorType(owner, "nucleic acid");
        NucleicAcid na = new NucleicAcidImpl(owner, src, "na", interactorType);

        // Shouldn't equal
        assertFalse(prot.equals(na));

        // Should equal to itself
        assertEquals(prot, prot);

        // Identical protein
        Protein other = new ProteinImpl(owner, src, "prot1", type);

        // Set the same sequence to both.
        prot.setSequence("0123");
        other.setSequence("0123");

        // Both should be same.
        assertEquals(prot, other);
        assertEquals(prot.hashCode(), other.hashCode());

        // Change the sequence
        prot.setSequence("abcd");

        // Shouldn't be equal
        assertFalse(prot.equals(other));
        assertFalse(prot.hashCode() == other.hashCode());

        // Identical object (transitive)
        Protein p1 = new ProteinImpl(owner, src, "p1", type);
        Protein p2 = new ProteinImpl(owner, src, "p1", type);
        Protein p3 = new ProteinImpl(owner, src, "p1", type);

        // Both should be same.
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertEquals(p2, p3);
        assertEquals(p2.hashCode(), p3.hashCode());
        // This is transitive
        assertEquals(p1, p3);
        assertEquals(p1.hashCode(), p3.hashCode());
    }
}
