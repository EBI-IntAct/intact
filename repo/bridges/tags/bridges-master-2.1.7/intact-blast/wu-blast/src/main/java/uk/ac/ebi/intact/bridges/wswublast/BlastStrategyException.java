/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.wswublast;

/**
 * TODO comment this ... someday
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since <pre>18 Sep 2007</pre>
 */
public class BlastStrategyException extends RuntimeException {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public BlastStrategyException(){
	}
	
	public BlastStrategyException(Throwable cause){
        super(cause);
    }

    public BlastStrategyException(String message) {
        super(message);
    }

    public BlastStrategyException(String message, Throwable cause) {
        super(message, cause);
    }
}
