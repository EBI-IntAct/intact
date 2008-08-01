/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.blast.model;

/**
 * TODO comment this ... someday
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since <pre>12 Sep 2007</pre>
 */
public class Hit {
	private String uniprotAc;
	private Float eValue;
	
	public Hit(String uniprotAc, Float eValue){
		this.uniprotAc = uniprotAc;
		this.eValue = eValue;
	}

	public String getUniprotAc() {
		return uniprotAc;
	}

	public Float getEValue() {
		return eValue;
	}
}
