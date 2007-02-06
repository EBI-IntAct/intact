/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.bridge;

/**
 * Exception thrown during the processing of retreiving a UniProt entry.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Sep-2006</pre>
 */
public class UniprotBridgeException extends Exception {

    public UniprotBridgeException( String message, Throwable cause ) {
        super( message, cause );
    }

    public UniprotBridgeException( String message ) {
        super( message );
    }

    public UniprotBridgeException( Throwable cause ) {
        super( cause );
    }
}