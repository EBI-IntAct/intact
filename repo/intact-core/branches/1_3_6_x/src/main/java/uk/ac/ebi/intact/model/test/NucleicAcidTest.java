/*
    Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
    All rights reserved. Please see the file LICENSE
    in the root directory of this distribution.
*/

package uk.ac.ebi.intact.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.model.*;

/**
 * Persistence Tests for NucleicAcid.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class NucleicAcidTest extends TestCase {

    public NucleicAcidTest(String name) throws Exception {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(NucleicAcidTest.class);
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

        // The interactor type.
        CvInteractorType interactorType = new CvInteractorType(owner, "nucleic acid");

        NucleicAcid na = new NucleicAcidImpl(owner, src, "na", interactorType);

        // Shouldn't equal to null or another type
        assertFalse(na.equals(null));
        assertFalse(na.equals("abc"));

        // Should equal to itself
        assertEquals(na, na);

        // Identical object (reflexive)
        NucleicAcid other = new NucleicAcidImpl(owner, src, "na", interactorType);

        // Both should be same.
        assertEquals(na, other);
        assertEquals(na.hashCode(), other.hashCode());

        // Set the same sequence to both.
        na.setSequence("0123");
        other.setSequence("0123");

        // Both should be same.
        assertEquals(na, other);
        assertEquals(na.hashCode(), other.hashCode());

        // Change the sequence of 'na'
        na.setSequence("abcd");

        // Shouldn't be equal
        assertFalse(na.equals(other));
        assertFalse(na.hashCode() == other.hashCode());

        // Identical object (transitive)
        NucleicAcid na1 = new NucleicAcidImpl(owner, src, "na", interactorType);
        NucleicAcid na2 = new NucleicAcidImpl(owner, src, "na", interactorType);
        NucleicAcid na3 = new NucleicAcidImpl(owner, src, "na", interactorType);

        // Both should be same.
        assertEquals(na1, na2);
        assertEquals(na1.hashCode(), na2.hashCode());
        assertEquals(na2, na3);
        assertEquals(na2.hashCode(), na3.hashCode());
        // This is transitive
        assertEquals(na1, na3);
        assertEquals(na1.hashCode(), na3.hashCode());
    }
}
