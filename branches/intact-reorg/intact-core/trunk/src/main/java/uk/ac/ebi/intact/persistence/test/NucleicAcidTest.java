/*
    Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
    All rights reserved. Please see the file LICENSE
    in the root directory of this distribution.
*/

package uk.ac.ebi.intact.persistence.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.business.BusinessConstants;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.*;

import java.util.List;

/**
 * Persistence Tests for NucleicAcid.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class NucleicAcidTest extends TestCase {

    /**
     * Handler to the IntactHelper
     */
    private IntactHelper myHelper;

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
        myHelper = new IntactHelper();
        Object obj = myHelper.getObjectByLabel(NucleicAcid.class, "na");
        if (obj != null) {
            myHelper.delete(obj);
        }
    }

    /**
     * Tears down the test fixture. Called after every test case method.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        myHelper.closeStore();
    }

    public void testPersistence1() throws IntactException {
        // The owner for objects.
        Institution owner = myHelper.getInstitution();

        // The source
        BioSource src = (BioSource) myHelper.getObjectByLabel(BioSource.class, "yeast");

        // The interactor type.
        CvInteractorType interactorType = (CvInteractorType) myHelper.getObjectByLabel(
                CvInteractorType.class, "nucleic acid");

        NucleicAcid na = new NucleicAcidImpl(owner, src, "na", interactorType);

        // Start the transaction
        try {
            myHelper.startTransaction(BusinessConstants.OBJECT_TX);

            // Need to create the nucleic acid object first.
            myHelper.create(na);

            myHelper.finishTransaction();
        }
        catch (IntactException ie) {
            //ie.printStackTrace();
            myHelper.undoTransaction();
            throw ie;
        }
        // Remove it from the cache, so it will load from the DB
        myHelper.removeFromCache(na);

        // Should have the nucleic acid object.
        NucleicAcid naRetr = (NucleicAcid) myHelper.getObjectByLabel(NucleicAcid.class, "na");

        // Both na and naRetr are different objects.
        assertFalse(naRetr == na);

        // AC, short labels should be same
        assertEquals(na.getAc(), naRetr.getAc());
        assertEquals(na.getShortLabel(), naRetr.getShortLabel());

        // No sequece.
        assertNull(na.getSequence());
        assertNull(naRetr.getSequence());
    }

    /**
     * Test strategy:
     * 1. Persist an object without a sequence
     * 2. Sets a new sequence and persist it again.
     * 3. Check that a sequence is persited
     * @throws IntactException
     */
    public void testPersistence2() throws IntactException {
        // The owner for objects.
        Institution owner = myHelper.getInstitution();

        // The source
        BioSource src = (BioSource) myHelper.getObjectByLabel(BioSource.class, "yeast");

        // The interactor type.
        CvInteractorType interactorType = (CvInteractorType) myHelper.getObjectByLabel(
                CvInteractorType.class, "nucleic acid");

        NucleicAcid na = new NucleicAcidImpl(owner, src, "na", interactorType);

        // Need to create the nucleic acid object first.
        myHelper.create(na);

        // Remove it from the cache, so it will load from the DB
        myHelper.removeFromCache(na);

        // Should have the nucleic acid object.
        NucleicAcid naRetr = (NucleicAcid) myHelper.getObjectByLabel(NucleicAcid.class, "na");

        // Both na and naRetr are different objects.
        assertFalse(naRetr == na);

        // AC, short labels, src and type should be same
        assertEquals(na.getAc(), naRetr.getAc());
        assertEquals(na.getShortLabel(), naRetr.getShortLabel());
        assertEquals(na.getBioSource(), naRetr.getBioSource());
        assertEquals(na.getCvInteractorType(), naRetr.getCvInteractorType());

        // No sequece.
        assertNull(na.getSequence());
        assertNull(naRetr.getSequence());

        // Set the sequence.
        List emptyChunks = na.setSequence("abcdef");
        // No redundant chunks.
        assertTrue(emptyChunks.isEmpty());

        // Update the na
        myHelper.update(na);

        // Remove it from the cache, so it will load from the DB
        myHelper.removeFromCache(na);

        // Should have the nucleic acid object.
        naRetr = (NucleicAcid) myHelper.getObjectByLabel(NucleicAcid.class, "na");

        // Both na and naRetr are different objects.
        assertFalse(naRetr == na);

        // AC, short labels, sequence should be same
        assertEquals(na.getAc(), naRetr.getAc());
        assertEquals(na.getShortLabel(), naRetr.getShortLabel());
        assertEquals(na.getSequence(), naRetr.getSequence());
        assertEquals(na.getBioSource(), naRetr.getBioSource());
        assertEquals(na.getCvInteractorType(), naRetr.getCvInteractorType());
    }

    /**
     * Test strategy:
     * 1. Persist an object with a sequence
     * 2. Sets a new sequence (larget than the previous seq) and persist it again.
     * 3. Check that a sequence is persited
     * @throws IntactException
     */
    public void testPersistence3() throws IntactException {
        // The owner for objects.
        Institution owner = myHelper.getInstitution();

        // The source
        BioSource src = (BioSource) myHelper.getObjectByLabel(BioSource.class, "yeast");

        // The interactor type.
        CvInteractorType interactorType = (CvInteractorType) myHelper.getObjectByLabel(
                CvInteractorType.class, "nucleic acid");

        NucleicAcid na = new NucleicAcidImpl(owner, src, "na", interactorType);
        na.setSequence("abcdef");

        // Need to create the nucleic acid object first.
        myHelper.create(na);

        // Save the chunk AC for later comparision.
        String chunkAc = ((SequenceChunk) na.getSequenceChunks().get(0)).getAc();

        // Remove it from the cache, so it will load from the DB
        myHelper.removeFromCache(na);

        // Should have the nucleic acid object.
        NucleicAcid naRetr = (NucleicAcid) myHelper.getObjectByLabel(NucleicAcid.class, "na");

        // Both na and naRetr are different objects.
        assertFalse(naRetr == na);

        // AC, short label, sequence should be same
        assertEquals(na.getAc(), naRetr.getAc());
        assertEquals(na.getShortLabel(), naRetr.getShortLabel());
        assertEquals(na.getSequence(), naRetr.getSequence());
        assertEquals(na.getBioSource(), naRetr.getBioSource());
        assertEquals(na.getCvInteractorType(), naRetr.getCvInteractorType());

        // Set a larger sequence.
        List emptyChunks = na.setSequence("0123456789");
        // No redundant chunks.
        assertTrue(emptyChunks.isEmpty());

        // Update the na
        myHelper.update(na);

        // Remove it from the cache, so it will load from the DB
        myHelper.removeFromCache(na);

        // Should have the nucleic acid object.
        naRetr = (NucleicAcid) myHelper.getObjectByLabel(NucleicAcid.class, "na");

        // Both na and naRetr are different objects.
        assertFalse(naRetr == na);

        // AC, short labels, sequence should be same
        assertEquals(na.getAc(), naRetr.getAc());
        assertEquals(na.getShortLabel(), naRetr.getShortLabel());
        assertEquals(na.getSequence(), naRetr.getSequence());
        assertEquals(na.getBioSource(), naRetr.getBioSource());
        assertEquals(na.getCvInteractorType(), naRetr.getCvInteractorType());

        // Get chunks as a collection to compare.
        List chunks = naRetr.getSequenceChunks();
        // Only one chunk
        assertEquals(chunks.size(), 1);
        assertEquals(((SequenceChunk) chunks.get(0)).getSequenceChunk(), "0123456789");
        // The AC must match the 'saved' AC (to prove that the new chunk simply resused
        // a previous chunk.
        assertEquals(((SequenceChunk) chunks.get(0)).getAc(), chunkAc);
    }

    /**
     * Test strategy:
     * 1. Persist an object with a sequence
     * 2. Sets a new sequence which expands to more than one chunk and persist it again.
     * 3. Check that a sequence is persited
     * @throws IntactException
     */
    public void testPersistence4() throws IntactException {
        // The owner for objects.
        Institution owner = myHelper.getInstitution();

        // The source
        BioSource src = (BioSource) myHelper.getObjectByLabel(BioSource.class, "yeast");

        // The interactor type.
        CvInteractorType interactorType = (CvInteractorType) myHelper.getObjectByLabel(
                CvInteractorType.class, "nucleic acid");

        // Set the maximum sequence length for testing.
        PolymerImpl.setMaxSequenceLength(10);

        NucleicAcid na = new NucleicAcidImpl(owner, src, "na", interactorType);
        na.setSequence("abcdef");

        // Need to create the nucleic acid object first.
        myHelper.create(na);

        // Save the chunk AC for later comparision.
        String chunkAc = ((SequenceChunk) na.getSequenceChunks().get(0)).getAc();

        // Remove it from the cache, so it will load from the DB
        myHelper.removeFromCache(na);

        // Should have the nucleic acid object.
        NucleicAcid naRetr = (NucleicAcid) myHelper.getObjectByLabel(NucleicAcid.class, "na");

        // Both na and naRetr are different objects.
        assertFalse(naRetr == na);

        // AC, short label, sequence should be same
        assertEquals(na.getAc(), naRetr.getAc());
        assertEquals(na.getShortLabel(), naRetr.getShortLabel());
        assertEquals(na.getSequence(), naRetr.getSequence());

        // Set a larger sequence.
        List emptyChunks = na.setSequence("0123456789pqr");
        // No redundant chunks.
        assertTrue(emptyChunks.isEmpty());

        // Update the na
        myHelper.update(na);

        // Remove it from the cache, so it will load from the DB
        myHelper.removeFromCache(na);

        // Should have the nucleic acid object.
        naRetr = (NucleicAcid) myHelper.getObjectByLabel(NucleicAcid.class, "na");

        // Both na and naRetr are different objects.
        assertFalse(naRetr == na);

        // AC, short labels, sequence should be same
        assertEquals(na.getAc(), naRetr.getAc());
        assertEquals(na.getShortLabel(), naRetr.getShortLabel());
        assertEquals(na.getSequence(), naRetr.getSequence());
        // Get chunks as a collection to compare.
        List chunks = naRetr.getSequenceChunks();
        // Two chunks
        assertEquals(chunks.size(), 2);
        assertEquals(((SequenceChunk) chunks.get(0)).getSequenceChunk(), "0123456789");
        assertEquals(((SequenceChunk) chunks.get(1)).getSequenceChunk(), "pqr");
        // The AC must match the 'saved' AC (to prove that the new chunk simply resused
        // a previous chunk.
        assertEquals(((SequenceChunk) chunks.get(0)).getAc(), chunkAc);
    }

    /**
     * Test strategy: testing for redundancy chunks.
     * @throws IntactException
     */
    public void testPersistence5() throws IntactException {
        // The owner for objects.
        Institution owner = myHelper.getInstitution();

        // The source
        BioSource src = (BioSource) myHelper.getObjectByLabel(BioSource.class, "yeast");

        // The interactor type.
        CvInteractorType interactorType = (CvInteractorType) myHelper.getObjectByLabel(
                CvInteractorType.class, "nucleic acid");

        // Set the maximum sequence length for testing.
        PolymerImpl.setMaxSequenceLength(10);

        NucleicAcid na = new NucleicAcidImpl(owner, src, "na", interactorType);
        na.setSequence("0123456789abcd");

        // Need to create the nucleic acid object first.
        myHelper.create(na);

        // Save the chunk AC for later comparision.
        String chunkAc = ((SequenceChunk) na.getSequenceChunks().get(0)).getAc();

        // Remove it from the cache, so it will load from the DB
        myHelper.removeFromCache(na);

        // Should have the nucleic acid object.
        NucleicAcid naRetr = (NucleicAcid) myHelper.getObjectByLabel(NucleicAcid.class, "na");

        // Both na and naRetr are different objects.
        assertFalse(naRetr == na);

        // AC, short label, sequence should be same
        assertEquals(na.getAc(), naRetr.getAc());
        assertEquals(na.getShortLabel(), naRetr.getShortLabel());
        assertEquals(na.getSequence(), naRetr.getSequence());

        // Set a smaller sequence.
        List emptyChunks = na.setSequence("0123");
        // One redundant chunks.
        assertEquals(emptyChunks.size(), 1);

        // Delete redunannt chunks and update the na
        myHelper.deleteAllElements(emptyChunks);
        myHelper.update(na);

        // Remove it from the cache, so it will load from the DB
        myHelper.removeFromCache(na);

        // Should have the nucleic acid object.
        naRetr = (NucleicAcid) myHelper.getObjectByLabel(NucleicAcid.class, "na");

        // Both na and naRetr are different objects.
        assertFalse(naRetr == na);

        // AC, short labels, sequence should be same
        assertEquals(na.getAc(), naRetr.getAc());
        assertEquals(na.getShortLabel(), naRetr.getShortLabel());
        assertEquals(na.getSequence(), naRetr.getSequence());
        // Get chunks as a collection to compare.
        List chunks = naRetr.getSequenceChunks();
        // A single chunk
        assertEquals(chunks.size(), 1);
        assertEquals(((SequenceChunk) chunks.get(0)).getSequenceChunk(), "0123");
        // The AC must match the 'saved' AC (to prove that the new chunk simply resused
        // a previous chunk.
        assertEquals(((SequenceChunk) chunks.get(0)).getAc(), chunkAc);
    }

    /**
     * Test strategy: testing for changing interactor type
     * @throws IntactException
     */
    public void testPersistence6() throws IntactException {
        // The owner for objects.
        Institution owner = myHelper.getInstitution();

        // The source
        BioSource src = (BioSource) myHelper.getObjectByLabel(BioSource.class, "yeast");

        // The interactor type.
        CvInteractorType interactorType = (CvInteractorType) myHelper.getObjectByLabel(
                CvInteractorType.class, "nucleic acid");

        NucleicAcid na = new NucleicAcidImpl(owner, src, "na", interactorType);

        // Need to create the nucleic acid object first.
        myHelper.create(na);

        // Remove it from the cache, so it will load from the DB
        myHelper.removeFromCache(na);

        // Should have the nucleic acid object.
        NucleicAcid naRetr = (NucleicAcid) myHelper.getObjectByLabel(NucleicAcid.class, "na");

        // Both na and naRetr are different objects.
        assertFalse(naRetr == na);

        // AC, short label, interactor type, biosource should be same
        assertEquals(na.getAc(), naRetr.getAc());
        assertEquals(na.getShortLabel(), naRetr.getShortLabel());
        assertEquals(interactorType, naRetr.getCvInteractorType());
        assertEquals(src, naRetr.getBioSource());

        // Change the interactor type
        CvInteractorType newIntType = (CvInteractorType) myHelper.getObjectByLabel(
                CvInteractorType.class, "dna");
        naRetr.setCvInteractorType(newIntType);
        myHelper.update(naRetr);

        // Remove it from the cache, so it will load from the DB
        myHelper.removeFromCache(na);

        // Should have the nucleic acid object.
        naRetr = (NucleicAcid) myHelper.getObjectByLabel(NucleicAcid.class, "na");

        // Both na and naRetr are different objects.
        assertFalse(naRetr == na);

        // AC, short label, interactor type, biosource should be same
        assertEquals(na.getAc(), naRetr.getAc());
        assertEquals(na.getShortLabel(), naRetr.getShortLabel());
        assertEquals(newIntType, naRetr.getCvInteractorType());
        assertEquals(src, naRetr.getBioSource());
    }

    /**
     * Test strategy: testing for accessing by primary key (AC)
     * @throws IntactException
     */
    public void testPersistence7() throws IntactException {
        // The owner for objects.
        Institution owner = myHelper.getInstitution();

        // The source
        BioSource src = (BioSource) myHelper.getObjectByLabel(BioSource.class, "yeast");

        // The interactor type.
        CvInteractorType interactorType = (CvInteractorType) myHelper.getObjectByLabel(
                CvInteractorType.class, "nucleic acid");

        NucleicAcid na = new NucleicAcidImpl(owner, src, "na", interactorType);

        // Need to create the nucleic acid object first.
        myHelper.create(na);

        String ac = na.getAc();

        // Remove it from the cache, so it will load from the DB
        myHelper.removeFromCache(na);

        // Should have the nucleic acid object.
        NucleicAcid naRetr = (NucleicAcid) myHelper.getObjectByAc(NucleicAcid.class, ac);

        // Change the interactor type
        CvInteractorType newIntType = (CvInteractorType) myHelper.getObjectByLabel(
                CvInteractorType.class, "dna");
        naRetr.setCvInteractorType(newIntType);
        myHelper.update(naRetr);

        // Remove it from the cache, so it will load from the DB
        myHelper.removeFromCache(na);

        // Should have the nucleic acid object.
        naRetr = (NucleicAcid) myHelper.getObjectByAc(NucleicAcid.class, ac);

        // Both na and naRetr are different objects.
        assertFalse(naRetr == na);

        // AC, short label, interactor type, biosource should be same
        assertEquals(na.getAc(), naRetr.getAc());
        assertEquals(na.getShortLabel(), naRetr.getShortLabel());
        assertEquals(newIntType, naRetr.getCvInteractorType());
        assertEquals(src, naRetr.getBioSource());
    }
}
