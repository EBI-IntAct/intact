/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.wswublast.model;

import java.util.regex.Pattern;

/**
 * UniprotAc identifier.
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version  1.0
 * @since
 * 
 * <pre>
 * 19 Sep 2007
 * </pre>
 */
public class UniprotAc {
	private String	acNr;
    private static String uniprotTermExpr ="[A-Z][0-9][A-Z0-9]{3}[0-9]|[A-Z][0-9][A-Z0-9]{3}[0-9]-[0-9]+|[A-Z][0-9][A-Z0-9]{3}[0-9]-PRO_[0-9]{10}";

	public UniprotAc(String accessionNr) {
		if (accessionNr == null) {
			throw new IllegalArgumentException("Ac must not be null! " + accessionNr);
		}
        accessionNr = accessionNr.trim();
        if (Pattern.matches(uniprotTermExpr, accessionNr)) {
			this.acNr = accessionNr;
		}
		else{
			throw new IllegalArgumentException("Ac must be a valid uniprotAc! " + accessionNr);
			
		}
	}

	/**
	 * @return the ac
	 */
	public String getAcNr() {
		return acNr;
	}

     public static String getRegex() {
        return uniprotTermExpr;
    }

    @Override
	public String toString() {
		return acNr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UniprotAc) {
			UniprotAc ac = (UniprotAc) obj;
			return this.acNr.equals(ac.acNr);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.acNr.hashCode();
	}
}
