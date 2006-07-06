/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import java.io.Serializable;

/**
 * Represents a crossreference to another database.
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@Table(name = "ia_sequence_chunk")
public class SequenceChunk implements Serializable {

    ///////////////////////////////////////
    //attributes

    /**
     * Sequence chunk accession number
     */
    private String ac;

    /**
     * To who belongs that chunk.
     */
    private String parentAc;

    /**
     * The content of the sequence chunk.
     */
    private String sequenceChunk;

    /**
     * Chunk order.
     */
    private int sequenceIndex;

    ///////////////////////////////////////
    // constructors
    public SequenceChunk() {
    }

    public SequenceChunk( int aSequenceIndex, String aSequenceChunk ) {
        this.sequenceIndex = aSequenceIndex;
        this.sequenceChunk = aSequenceChunk;
    }

    ///////////////////////////////////////
    // associations
    @Id
    public String getAc() {
        return ac;
    }

    public void setAc( String ac ) {
        this.ac = ac;
    }

    ///////////////////////////////////////
    //access methods for attributes
    @Column(name = "parent_ac")
    public String getParentAc() {
        return parentAc;
    }

    public void setParentAc( String parentAc ) {
        this.parentAc = parentAc;
    }

    @Column(name = "sequence_chunk")
    public String getSequenceChunk() {
        return sequenceChunk;
    }

    public void setSequenceChunk( String sequenceChunk ) {
        this.sequenceChunk = sequenceChunk;
    }

    @Column(name = "sequence_index")
    public int getSequenceIndex() {
        return sequenceIndex;
    }

    public void setSequenceIndex( int sequenceIndex ) {
        this.sequenceIndex = sequenceIndex;
    }

} // end Xref




