/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.util.Utilities;

import java.util.*;

/**
 * Represents a crossreference to another database.
 *
 * @author hhe
 */
public class SequenceChunk {

    ///////////////////////////////////////
    //attributes
    private String ac;

    //private String qualifierAc;
    //protected String databaseAc;
    protected String parentAc;


    /**
     * Primary identifier of the database referred to.
     */
    protected String sequenceChunk;

    /**
     * Secondary identifier of the database. This will usually be
     * a meaningful name, for example a domain name.
     */
    protected int sequenceIndex;

    ///////////////////////////////////////
    // constructors
    public SequenceChunk() {
        super();
    }

    public SequenceChunk(int aSequenceIndex, String aSequenceChunk) {
        this.sequenceIndex = aSequenceIndex;
        this.sequenceChunk = aSequenceChunk;
    }

    ///////////////////////////////////////
    // associations

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    ///////////////////////////////////////
    //access methods for attributes
    public String getParentAc() {
        return parentAc;
    }

    public void setParentAc(String parentAc) {
        this.parentAc = parentAc;
    }

    public String getSequenceChunk() {
        return sequenceChunk;
    }

    public void setSequenceChunk(String sequenceChunk) {
        this.sequenceChunk = sequenceChunk;
    }

    public int getSequenceIndex() {
        return sequenceIndex;
    }

    public void setSequenceIndex(int sequenceIndex) {
        this.sequenceIndex = sequenceIndex;
    }

 } // end Xref




