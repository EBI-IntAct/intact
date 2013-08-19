/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.wswublast.model;

import java.util.regex.Pattern;

 /**
 * TODO comment this ... someday
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since <pre>4 Oct 2007</pre>
 */
public class Sequence {
	private String seq;
	//TODO: get a regex for sequence
	private String sequneceTermExpr ="\\w+"; // it maches also 0-9 and _ but for the first shot it's ok
	
	public Sequence(String sequence) {
		if (sequence == null) {
			throw new IllegalArgumentException("Sequence must not be null!");
		}
        sequence = sequence.replace( " ", "" );
        sequence = sequence.replace( "\n","");
        if (Pattern.matches(sequneceTermExpr, sequence)) {
			this.seq = sequence.toLowerCase();
		}
		else{
            throw new IllegalArgumentException("Sequence must not contain enything else than letters! >" + sequence +"<");
			
		}
	}

	/**
	 * @return the seq
	 */
	public String getSeq() {
		return seq;
	}
	
	public String getFasta(){
		return ">Sequence\n" + seq + "\n";
	}
	
	@Override
	public String toString() {
		return seq;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Sequence) {
			Sequence seq = (Sequence) obj;
			return this.seq.equals(seq.seq);
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
		return this.seq.hashCode();
	}

	
}
