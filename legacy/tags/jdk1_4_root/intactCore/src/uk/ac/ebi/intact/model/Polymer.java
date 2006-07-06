/*
Copyright (c) 2002-2005 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;

import java.util.List;

/**
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public interface Polymer extends Interactor {

    /**
     * @return a sequence as a string
     */
    public String getSequence();

    /**
     * Sets the current sequence. Use this method instead of
     * {@link #setSequence(uk.ac.ebi.intact.business.IntactHelper, String)} because
     * in the near future, reference to IntactHelper will be removed from model classes.
     * @param aSequence the sequence to set
     * @return a list of SequenceChunk objects to remove. This list is non empty
     * only when the current sequence is longer than the new sequence
     * (i.e, <code>aSequence</code>).
     */
    public List setSequence(String aSequence);

    /**
     * Sets the current sequence. Use the method {@link #setSequence(String)} instead.
     * @param helper
     * @param aSequence
     * @throws IntactException
     */
    public void setSequence(IntactHelper helper, String aSequence) throws IntactException;

    /**
     * @return crc64 as a string
     */
    public String getCrc64();

    /**
     * Sets the crc64
     * @param crc64 the crc64 value
     */
    public void setCrc64(String crc64);

    /**
     * This method is mainly for testing purposes to examine the chunks array
     * @return unmodifiable list of sequence chunks.
     */
    public List getSequenceChunks();
}
