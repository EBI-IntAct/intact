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
 * Persistence Tests for Protein.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinTest extends TestCase {

    /**
     * Handler to the IntactHelper
     */
    private IntactHelper myHelper;

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
        myHelper = new IntactHelper();
        Object obj = myHelper.getObjectByLabel(Protein.class, "prot1");
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

    /**
     * Test: (1). Persist with a sequence (2) Replace the existing sequence with
     * a shorter sequence (resuse existing chunks and delete old ones)
     * @throws IntactException
     */
    public void testPersistence1() throws IntactException {
        // The owner for objects.
        Institution owner = myHelper.getInstitution();

        // The source
        BioSource src = (BioSource) myHelper.getObjectByLabel(BioSource.class, "yeast");

        // The interactor type.
        CvInteractorType interactorType = (CvInteractorType) myHelper.getObjectByPrimaryId(
                CvInteractorType.class, CvInteractorType.getProteinMI());

        // Set the maximum sequence length for testing.
        PolymerImpl.setMaxSequenceLength(10);

        Protein prot = new ProteinImpl(owner, src, "prot1", interactorType);
        // Set the sequence.
        prot.setSequence("012345678901234");

        // Start the transaction
        try {
            myHelper.startTransaction(BusinessConstants.JDBC_TX);

            // Create a protein first
            myHelper.create(prot);

            myHelper.finishTransaction();
        }
        catch (IntactException ie) {
            myHelper.undoTransaction();
            throw ie;
        }
        Protein protRetr = (Protein) myHelper.getObjectByLabel(Protein.class, "prot1");
        assertNotNull(protRetr);
        assertEquals(protRetr.getSequence(), prot.getSequence());
        // Get chunks as a collection to compare.
        List chunks = protRetr.getSequenceChunks();
        // Two chunks
        assertEquals(chunks.size(), 2);
        assertEquals(((SequenceChunk) chunks.get(0)).getSequenceChunk(), "0123456789");
        assertEquals(((SequenceChunk) chunks.get(1)).getSequenceChunk(), "01234");

        // Save the chunk AC for later comparision.
        String chunkAc = ((SequenceChunk) chunks.get(0)).getAc();

        // Test for reusing the existing chunks (shorter chunk).
        List emptyChunks = prot.setSequence("ABC");
        // Should have one redundant chunk
        assertEquals(emptyChunks.size(), 1);
        // Persist the new sequence.
        myHelper.deleteAllElements(emptyChunks);
        myHelper.update(prot);

        // Force it to retrieve from DB
        myHelper.removeFromCache(prot);

        // Retrieve it again.
        protRetr = (Protein) myHelper.getObjectByLabel(Protein.class, "prot1");
        assertNotNull(protRetr);
        assertEquals(protRetr.getSequence(), prot.getSequence());
        // Get chunks as a collection to compare.
        chunks = protRetr.getSequenceChunks();
        // Only one chunk
        assertEquals(chunks.size(), 1);
        assertEquals(((SequenceChunk) chunks.get(0)).getSequenceChunk(), "ABC");
        // The AC must match the 'saved' AC (to prove that the new chunk simply resused
        // a previous chunk.
        assertEquals(((SequenceChunk) chunks.get(0)).getAc(), chunkAc);
    }

    /**
     * Test: (1). Persist with a sequence (2) Replace the existing sequence with
     * a similar size sequence
     * @throws IntactException
     */
    public void testPersistence2() throws IntactException {
        // The owner for objects.
        Institution owner = myHelper.getInstitution();

        // The source
        BioSource src = (BioSource) myHelper.getObjectByLabel(BioSource.class, "yeast");

        // The interactor type.
        CvInteractorType interactorType = (CvInteractorType) myHelper.getObjectByPrimaryId(
                CvInteractorType.class, CvInteractorType.getProteinMI());

        // Set the maximum sequence length for testing.
        PolymerImpl.setMaxSequenceLength(10);

        // The protein to persist
        Protein prot = new ProteinImpl(owner, src, "prot1", interactorType);

        // Set the sequence.
        prot.setSequence("0123456789");

        // Create a protein first
        myHelper.create(prot);

        // Force it to retrieve from DB
        myHelper.removeFromCache(prot);

        // Retrieve it from the persistent system
        Protein protRetr = (Protein) myHelper.getObjectByLabel(Protein.class, "prot1");
        assertNotNull(protRetr);
        assertEquals(protRetr.getSequence(), prot.getSequence());
        // Get chunks as a collection to compare.
        List chunks = protRetr.getSequenceChunks();
        // Two chunks
        assertEquals(chunks.size(), 1);
        assertEquals(((SequenceChunk) chunks.get(0)).getSequenceChunk(), "0123456789");

        // Save the chunk AC for later comparision.
        String chunkAc = ((SequenceChunk) chunks.get(0)).getAc();

        // Replace with a similar size chunk
        List emptyChunks = prot.setSequence("9876543210");
        // Shouldn't have redundant chunks
        assertTrue(emptyChunks.isEmpty());
        // Persist the new sequence.
        myHelper.update(prot);

        // Force it to retrieve from DB
        myHelper.removeFromCache(prot);

        // Retrieve it again.
        protRetr = (Protein) myHelper.getObjectByLabel(Protein.class, "prot1");
        assertNotNull(protRetr);
        assertEquals(protRetr.getSequence(), prot.getSequence());
        // Get chunks as a collection to compare.
        chunks = protRetr.getSequenceChunks();
        // Only one chunk
        assertEquals(chunks.size(), 1);
        assertEquals(((SequenceChunk) chunks.get(0)).getSequenceChunk(), "9876543210");
        // The AC must match the 'saved' AC (to prove that the new chunk simply resused
        // a previous chunk.
        assertEquals(((SequenceChunk) chunks.get(0)).getAc(), chunkAc);
    }

    /**
     * Test: (1). Persist with a sequence (2) Replace the existing sequence with
     * a shorter sequence using the setSequence methods that accepts the Intact helper.
     * @throws IntactException
     */
    public void testPersistence3() throws IntactException {
        // The owner for objects.
        Institution owner = myHelper.getInstitution();

        // The source
        BioSource src = (BioSource) myHelper.getObjectByLabel(BioSource.class, "yeast");

        // The interactor type.
        CvInteractorType interactorType = (CvInteractorType) myHelper.getObjectByPrimaryId(
                CvInteractorType.class, CvInteractorType.getProteinMI());

        // Set the maximum sequence length for testing.
        PolymerImpl.setMaxSequenceLength(10);

        Protein prot = new ProteinImpl(owner, src, "prot1", interactorType);
        // Set the sequence.
        prot.setSequence("012345678901234");

        // Start the transaction
        try {
            myHelper.startTransaction(BusinessConstants.JDBC_TX);

            // Create a protein first
            myHelper.create(prot);

            myHelper.finishTransaction();
        }
        catch (IntactException ie) {
            myHelper.undoTransaction();
            throw ie;
        }
        Protein protRetr = (Protein) myHelper.getObjectByLabel(Protein.class, "prot1");
        assertNotNull(protRetr);
        assertEquals(protRetr.getSequence(), prot.getSequence());
        // Get chunks as a collection to compare.
        List chunks = protRetr.getSequenceChunks();
        // Two chunks
        assertEquals(chunks.size(), 2);
        assertEquals(((SequenceChunk) chunks.get(0)).getSequenceChunk(), "0123456789");
        assertEquals(((SequenceChunk) chunks.get(1)).getSequenceChunk(), "01234");

        // Save the chunk AC for later comparision.
        String chunkAc = ((SequenceChunk) chunks.get(0)).getAc();

        // Test for reusing the existing chunks (shorter chunk).
        prot.setSequence(myHelper, "ABC");
        // Persist the new sequence.
        myHelper.update(prot);

        // Force it to retrieve from DB
        myHelper.removeFromCache(prot);

        // Retrieve it again.
        protRetr = (Protein) myHelper.getObjectByLabel(Protein.class, "prot1");
        assertNotNull(protRetr);
        assertEquals(protRetr.getSequence(), prot.getSequence());
        // Get chunks as a collection to compare.
        chunks = protRetr.getSequenceChunks();
        // Only one chunk
        assertEquals(chunks.size(), 1);
        assertEquals(((SequenceChunk) chunks.get(0)).getSequenceChunk(), "ABC");
        // The AC must match the 'saved' AC (to prove that the new chunk simply resused
        // a previous chunk.
        assertEquals(((SequenceChunk) chunks.get(0)).getAc(), chunkAc);
    }

    public void testLoading() throws IntactException {
        Protein prot = (Protein) myHelper.getObjectByLabel(Protein.class, "abd1_yeast");

        assertEquals(prot.getCvInteractorType().getShortLabel(), "protein");
        assertEquals(prot.getBioSource().getShortLabel(), "yeast");
    }
}
