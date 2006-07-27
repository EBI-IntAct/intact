/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.sanityChecker.model;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class SequenceChunkBean extends IntactBean {

    private String sequence_chunk;

    private int sequence_index;

    public SequenceChunkBean() {
    }

    public String getSequence_chunk() {
        return sequence_chunk;
    }

    public void setSequence_chunk(String sequence_chunk) {
        this.sequence_chunk = sequence_chunk;
    }

    public int getSequence_index() {
        return sequence_index;
    }

    public void setSequence_index(int sequence_index) {
        this.sequence_index = sequence_index;
    }
}
