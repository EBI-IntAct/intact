/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast.model;

/**
 * TODO comment this ... someday
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 19 Sep 2007
 * </pre>
 */
public class BlastInput {

	private UniprotAc	uniprotAc;
	private Sequence		sequence;

	public BlastInput(UniprotAc uniprotAc) {
		if (uniprotAc == null) {
			throw new IllegalArgumentException("UniprotAc must not be null!");
		}
		this.uniprotAc = uniprotAc;
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
	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}
	@Override
	public String toString() {
		return uniprotAc.toString();
	}
}
