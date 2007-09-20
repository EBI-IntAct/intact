/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast.model;

import java.util.regex.Pattern;

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
public class UniprotAc {
	private String	acNr;
	// TODO: ask sam for a proper regex
	private String	uniprotTermExpr	= "\\w{6,6}";

	public UniprotAc(String accessionNr) {
		if (accessionNr == null) {
			throw new IllegalArgumentException("Ac must not be null!");
		}
		if (Pattern.matches(uniprotTermExpr, accessionNr)) {
			this.acNr = accessionNr;
		}else {
			throw new IllegalArgumentException("Ac must be be built out of 6 characters!");
		}
	}

	/**
	 * @return the ac
	 */
	public String getAcNr() {
		return acNr;
	}

	@Override
	public String toString() {
		return acNr;
	}

}
