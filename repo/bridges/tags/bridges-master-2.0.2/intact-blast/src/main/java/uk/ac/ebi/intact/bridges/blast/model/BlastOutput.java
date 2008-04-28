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
 * 19 Sep. 2007
 * </pre>
 */
public class BlastOutput {

	private String	result;
	private boolean	isXmlFormat;

	public BlastOutput(byte[] result, boolean isXmlFormat) {
		if (result == null) {
			throw new IllegalArgumentException("Result must not be null!");
		}

		this.result = new String(result);
		this.isXmlFormat = isXmlFormat;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @return the isXmlFormat
	 */
	public boolean isXmlFormat() {
		return isXmlFormat;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "format is : " + (isXmlFormat? "xml" : "not xml");
	}
	
	
}
