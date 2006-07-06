/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;

import java.util.*;

/**
 * This is the super class for Protein and Nucleic Acid types.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public abstract class PolymerImpl extends InteractorImpl implements Polymer {

    //Constants

    /**
     * As the maximum size of database objects is limited, the sequence is represented as
     * an array of strings of maximum length.
     */
    protected static int ourMaxSeqLength = 1000;

    //attributes

    /**
     * Represents the CRC64 checksum. This checksum is used to
     * detect potential inconsistencies between the sequence the object
     * refers to and the external sequence object, for example when the external
     * object has been updated.
     */
    private String crc64;

    /**
     * The protein sequence. If the protein is present in a public database,
     * the sequence should not be repeated.
     */
    private List sequenceChunks = new ArrayList();

    // Static methods

    /**
     * @return returns the maximum sequence size.
     */
    public static int getMaxSequenceLength() {
        return ourMaxSeqLength;
    }

    /**
     * Sets the maximum sequence size. <b>This method is only used by the unit
     * tester for this class</b>.
     *
     * @param max the new sequence value.
     */
    public static void setMaxSequenceLength(int max) {
        ourMaxSeqLength = max;
    }

    // Constructors

    /**
     * no-arg constructor provided for compatibility with subclasses
     * that have no-arg constructors.
     */
    protected PolymerImpl() {
        //super call sets creation time data
        super();
    }

    /**
     * Constructor for subclass use only. Ensures that Polymer cannot be
     * created without at least a shortLabel and an owner specified.
     *
     * @param owner The Institution which owns this instance
     * @param source     The biological source of the Protein observation
     * @param shortLabel The memorable label to identify this instance
     * @param type     The interactor type
     */
    protected PolymerImpl(Institution owner, BioSource source, String shortLabel,
                          CvInteractorType type) {
        super(shortLabel, owner, type);
        setBioSource(source);
    }

    // access methods for attributes

    public String getSequence() {
        if ((null == sequenceChunks) || 0 == (sequenceChunks.size())) {
            return null;
        }
        // Re-join the sequence chunks.
        // The correct ordering is done during retrieval from the database.
        // It relies on the OJB setting (mapping)
        StringBuffer sequence = new StringBuffer();
        for (Iterator iterator = sequenceChunks.iterator(); iterator.hasNext();) {
            SequenceChunk sequenceChunk = (SequenceChunk) iterator.next();
            sequence.append(sequenceChunk.getSequenceChunk());
        }
        return sequence.toString();
    }

    public List setSequence(String aSequence) {
        // Save work if the new sequence is identical to the old one.
        if (aSequence.equals(getSequence())) {
            return Collections.EMPTY_LIST;
        }
        // The container to hold redundant chunks.
        ArrayList chunkPool = null;

        // All old data are kept, we try to recycle as much chunk as possible
        if (sequenceChunks == null) {
            sequenceChunks = new ArrayList();
        }
        else if (!sequenceChunks.isEmpty()) {
            // There is existing chunk ... prepare them for recycling.
            chunkPool = new ArrayList(sequenceChunks.size());
            chunkPool.addAll(sequenceChunks);
            int count = chunkPool.size();

            // clean chunk to recycle
            for (int i = 0; i < count; i++) {
                SequenceChunk sc = (SequenceChunk) chunkPool.get(i);
                removeSequenceChunk(sc);
            }
        }

        // Note the use of integer operations
        int chunkCount = aSequence.length() / ourMaxSeqLength;
        if (aSequence.length() % ourMaxSeqLength > 0) {
            chunkCount++;
        }

        for (int i = 0; i < chunkCount; i++) {
            String chunk = aSequence.substring(i * ourMaxSeqLength,
                    Math.min((i + 1) * ourMaxSeqLength, aSequence.length()));

            if (chunkPool != null && chunkPool.size() > 0) {
                // recycle chunk
                SequenceChunk sc = (SequenceChunk) chunkPool.remove(0);
                sc.setSequenceChunk(chunk);
                sc.setSequenceIndex(i);
                addSequenceChunk(sc);
            }
            else {
                // create new chunk
                addSequenceChunk(new SequenceChunk(i, chunk));
            }
        }
        // Check for null chunkPool
        return chunkPool == null ? Collections.EMPTY_LIST : chunkPool;
    }

    /**
     * If there is existing sequence (and chunks), reuse existing chunk
     * in order to save AC.<br>
     * The update is canceled if the sequence is null or the same.
     *
     * @param helper
     * @param aSequence the sequence to update in the protein
     * @throws uk.ac.ebi.intact.business.IntactException
     */
    public void setSequence(IntactHelper helper, String aSequence) throws IntactException {
        if (null == aSequence) {
            return;
        }

        if (null == getAc()) {
            throw new IntactException("The object AC must be set before setting the sequence.");
        }

        // Save work if the new sequence is identical to the old one.
        if (aSequence.equals(getSequence())) {
            return;
        }

        ArrayList chunkPool = null;
        SequenceChunk s = null;
        String chunk = null;

        // All old data are kept, we try to recycle as much chunk as possible
        if (sequenceChunks == null) {
            sequenceChunks = new ArrayList();
        }
        else if (false == sequenceChunks.isEmpty()) {
            // There is existing chunk ... prepare them for recycling.
            chunkPool = new ArrayList(sequenceChunks.size());
            chunkPool.addAll(sequenceChunks);
            int count = chunkPool.size();

            // clean chunk to recycle
            for (int i = 0; i < count; i++) {
                s = (SequenceChunk) chunkPool.get(i);
                removeSequenceChunk(s);
            }
        }

        // Note the use of integer operations
        int chunkCount = aSequence.length() / ourMaxSeqLength;
        if (aSequence.length() % ourMaxSeqLength > 0) {
            chunkCount++;
        }

        for (int i = 0; i < chunkCount; i++) {
            chunk = aSequence.substring(i * ourMaxSeqLength,
                    Math.min((i + 1) * ourMaxSeqLength, aSequence.length()));

            if (chunkPool != null && chunkPool.size() > 0) {
                // recycle chunk
                s = (SequenceChunk) chunkPool.remove(0);
                s.setSequenceChunk(chunk);
                s.setSequenceIndex(i);
                addSequenceChunk(s);

                helper.update(s);
            }
            else {
                // create new chunk
                s = new SequenceChunk(i, chunk);
                addSequenceChunk(s);

                helper.create(s);
            }
        }

        // Delete non recyclable chunk
        while (chunkPool != null && chunkPool.size() > 0) {
            s = (SequenceChunk) chunkPool.remove(0);
            helper.delete(s);
        }
    }


    public String getCrc64() {
        return crc64;
    }

    public void setCrc64(String crc64) {
        this.crc64 = crc64;
    }

    public List getSequenceChunks() {
        return Collections.unmodifiableList(sequenceChunks);
    }

    protected void addSequenceChunk(SequenceChunk sequenceChunk) {
        if (!this.sequenceChunks.contains(sequenceChunk)) {
            this.sequenceChunks.add(sequenceChunk);
            sequenceChunk.setParentAc(this.getAc());
        }
    }

    protected void removeSequenceChunk(SequenceChunk sequenceChunk) {
        boolean removed = this.sequenceChunks.remove(sequenceChunk);
        if (removed) sequenceChunk.setParentAc(null);
    }

    /**
     * Equality for Proteins is currently based on equality for
     * <code>Interactors</code>, the sequence and the crc64 checksum.
     *
     * @param obj The object to check
     * @return true if the parameter equals this object, false otherwise
     * @see InteractorImpl
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        final Polymer other = (Polymer) obj;

        // TODO do we need to check sequence and CRC64
        if (getSequence() != null) {
            if (!getSequence().equals(other.getSequence())) {
                return false;
            }
        }
        else {
            if (other.getSequence() != null) {
                return false;
            }
        }

        if (crc64 != null) {
            return crc64.equals(other.getCrc64());
        }
        else {
            return other.getCrc64() == null;
        }
    }

    /**
     * Remember that hashCode and equals methods has to be develop in parallel
     * since : if a.equals(b) then a.hoshCode() == b.hashCode()
     * The other way round is NOT true.
     * Unless it could break consistancy when storing object in a hash-based
     * collection such as HashMap...
     */
    public int hashCode() {
        int code = super.hashCode();
        if (getSequence() != null) code = code * 29 + getSequence().hashCode();
        if (crc64 != null) code = code * 29 + crc64.hashCode();
        return code;
    }

    public String toString() {
        return super.toString() + " [ CRC64: " + getCrc64() + " Sequence: " + getSequence() + "]";
    }
}




