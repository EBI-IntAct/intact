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

	public BlastInput(UniprotAc uniprotAc) {
		if (uniprotAc == null) {
			throw new IllegalArgumentException("UniprotAc must not be null!");
		}
		this.uniprotAc = uniprotAc;
	}

	/**
	 * @return the uniprotAc
	 */
	public UniprotAc getUniprotAc() {
		return uniprotAc;
	}

	@Override
	public String toString() {
		return uniprotAc.toString();
	}

}
