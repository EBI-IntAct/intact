/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast.model;

/**
 * Class representing the input information needed for a blast job.
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version 1.0
 * @since
 * 
 * <pre>
 * 19 Sep 2007
 * </pre>
 */
public class BlastInput {

	private UniprotAc	uniprotAc;
	private Sequence	sequence;

    private IntactId    intactId;

    public BlastInput(UniprotAc uniprotAc) {
		if (uniprotAc == null) {
			throw new IllegalArgumentException("UniprotAc must not be null!");
		}
		this.uniprotAc = uniprotAc;
	}

    public BlastInput(String seq) {
		if (seq == null) {
			throw new IllegalArgumentException("The sequence must not be null!");
		}
		this.sequence = new Sequence(seq);
	}

	public BlastInput(UniprotAc uniprotAc, Sequence seq) {
		if (uniprotAc == null) {
			throw new IllegalArgumentException("UniprotAc must not be null!");
		}
		if(seq == null){
			throw new IllegalArgumentException("Sequence must not be null!");
		}
		this.uniprotAc = uniprotAc;
		this.sequence = seq;
	}
    
    public BlastInput(IntactId intactId, Sequence seq){
          if (intactId == null) {
			throw new IllegalArgumentException("IntactId must not be null!");
		}
		if(seq == null){
			throw new IllegalArgumentException("Sequence must not be null!");
		}
		this.intactId = intactId;
		this.sequence = seq;
    }

    /**
	 * @return the uniprotAc
	 */
	public UniprotAc getUniprotAc() {
		return uniprotAc;
	}

	/**
	 * @return the sequence
	 */
	public Sequence getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
    //TODO: is it wise to have a seq setter?
    public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}

    public IntactId getIntactId() {
        return intactId;
    }

    @Override
	public String toString() {
		return uniprotAc != null ? uniprotAc.toString() : "";
	}

    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof BlastInput ) ) return false;

        BlastInput that = ( BlastInput ) o;

        if ( sequence != null ? !sequence.equals( that.sequence ) : that.sequence != null ) return false;
        if (uniprotAc != null && that.uniprotAc != null){
            if ( !uniprotAc.equals( that.uniprotAc ) ) return false;
        }
        else if ((uniprotAc == null && that.uniprotAc != null) || (uniprotAc != null && that.uniprotAc == null)){
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result=1;
        result = 31 * result + ( uniprotAc != null ? uniprotAc.hashCode() : 0 );
        result = 31 * result + ( sequence != null ? sequence.hashCode() : 0 );
        return result;
    }
}
