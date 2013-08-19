/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.confidence.blastmapping;

/**
 * Exception-Wrapper for BlastMappingReader.
 * 
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since
 * 
 * <pre>
 * 6 Sep 2007
 * </pre>
 */
public class BlastMappingException extends Exception {
	public BlastMappingException() {
		super();
	}

	public BlastMappingException(String message) {
		super(message);
	}

	public BlastMappingException(String message, Throwable cause) {
		super(message, cause);
	}

	public BlastMappingException(Throwable cause) {
		super(cause);
	}
}
