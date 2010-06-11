/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.wswublast.model;

import java.util.List;


/**
 * TODO comment this ... someday
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since <pre>12 Sep 2007</pre>
 */
public class BlastResult {

	private String uniprotAc;
	private List<Hit> hits;
	
	public BlastResult(String uniprotAc, List<Hit> hits){
		this.uniprotAc = uniprotAc;
		this.hits = hits;
	}

	public String getUniprotAc() {
		return uniprotAc;
	}

	public List<Hit> getHits() {
		return hits;
	}
}
