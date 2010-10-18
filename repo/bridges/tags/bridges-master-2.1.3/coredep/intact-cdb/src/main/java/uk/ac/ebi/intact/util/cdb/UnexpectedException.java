/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.cdb;

/**
 * When an unexpected error occurs.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Aug-2005</pre>
 */
public class UnexpectedException extends Exception {
    public UnexpectedException( String message, Throwable cause ) {
        super( message, cause );
    }
}
