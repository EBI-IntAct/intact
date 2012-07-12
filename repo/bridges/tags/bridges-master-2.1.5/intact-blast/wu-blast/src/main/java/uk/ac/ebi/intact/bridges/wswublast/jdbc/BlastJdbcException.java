/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridges.wswublast.jdbc;

/**
 * TODO comment this ... someday
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @version
 * @since <pre>13 Sep 2007</pre>
 */
public class BlastJdbcException extends Exception{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public BlastJdbcException(){
	}
	
	public BlastJdbcException(Throwable cause){
        super(cause);
    }

    public BlastJdbcException(String message) {
        super(message);
    }

    public BlastJdbcException(String message, Throwable cause) {
        super(message, cause);
    }
}
