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
 * 14 Sep 2007
 * </pre>
 */
public class BlastServiceException extends Exception {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public BlastServiceException() {
	}

	public BlastServiceException(Throwable cause) {
		super(cause);
	}

	public BlastServiceException(String message) {
		super(message);
	}

	public BlastServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
