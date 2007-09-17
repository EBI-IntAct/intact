/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.blast;

/**
 * TODO comment this ... someday
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 7 Sep 2007
 * </pre>
 */
public class Job {

	private String	id;
	private String	uniprotAc;

	public Job(String id, String uniprotAC) {
		this.id = id;
		this.uniprotAc = uniprotAC;
	}

	/**
	 * @return the value
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the uniprotAc
	 */
	public String getUniprotAc() {
		return uniprotAc;
	}
	
	public String toString(){
		return id + ": " + uniprotAc;
	}
}
